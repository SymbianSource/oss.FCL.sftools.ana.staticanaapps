/*
* Copyright (c) 2006 Nokia Corporation and/or its subsidiary(-ies). 
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of "Eclipse Public License v1.0"
* which accompanies this distribution, and is available
* at the URL "http://www.eclipse.org/legal/epl-v10.html".
*
* Initial Contributors:
* Nokia Corporation - initial contribution.
*
* Contributors:
*
* Description:
*
*/
 
 
package com.nokia.s60tools.appdep.core.job;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.CacheDataConstants;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.listview.ListView;
import com.nokia.s60tools.appdep.ui.views.main.MainViewDataPopulator;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Generates a background job that searches for
 * is used by dependencies.
 */
public class UsedByOtherComponentsJob extends Job 
							 implements IJobProgressStatus,
							            IManageableJob {

	//
	// Private members and constants
	// 
	
	private AppDepSettings settings = null;
	private IProgressMonitor monitor = null;
	private Process proc = null;
	private final ArrayList<ComponentPropertiesData> resultComponentsArrayList;
	private final String componentName;
	private final IWorkbenchPage activeWbPage;
	private final int steps;
	private final String functionOrdinal;
	private String functionName;
	private String listViewTitleString;
	private int previousPercentage = 0;	
	private static final int VIEW_ACTIVATION_STEPS = 20;
	
	/**
	 * Constructor.
	 * @param name Name of the job to be presented to user in Job title.
	 * @param settings Settings object used for the cache generation.
	 * @param componentName Name of the component selected by user. Either the component 
	 *                      for which we are searching dependencies, or the component that 
	 *                      implements the function we are searching dependencies for.
	 * @param functionName Can be set to <code>null</code> if we searching only dependencies
	 *                     between the component and other components. Otherwise we are searching
	 *                     components that are using this function. If this parameter is set, then
	 *                     also function ordinal has to be set.
	 * @param functionOrdinal Can be set to <code>null</code> if we searching only dependencies
	 *                     between the component and other components. Otherwise we are searching
	 *                     components that are using this function.
	 * @param resultComponentsArrayList Result components array. 
	 * @param activeWbPage	Currently active workbench page. This must be passes as parameter, because
	 *                      non-UI thread cannot otherwise launch the component list view.
	 * @param maximumComponentCount The amount of components available for the SDK/Platform currentlu
	 *                              under analysis. This information is needed in order to be able to
	 *                              show reasonable progress information bar.
	 */
	public UsedByOtherComponentsJob(String name, AppDepSettings settings,
									String componentName,
									String functionName,
									String functionOrdinal,
			                        ArrayList<ComponentPropertiesData> resultComponentsArrayList,
			                        IWorkbenchPage activeWbPage) {
		super(name);
		setUser(true);
		this.settings = settings;
		this.componentName = componentName;
		this.functionName = functionName;
		this.functionOrdinal = functionOrdinal;
		this.resultComponentsArrayList = resultComponentsArrayList;
		this.activeWbPage = activeWbPage;
        // Showing progress as percentages & reserving steps for view activation
		steps = 100 + VIEW_ACTIVATION_STEPS; 
		// Checking that all passes parameters are correct ones
		checkParameterPreconditions();
	}

	/**
	 * Checks that provided parameters for constructor are valid ones.
	 * @throws IllegalArgumentException
	 */
	private void checkParameterPreconditions(){
		// Both function related arguments has to be defined, or 
		// none of the function related arguments should be set-
		boolean bothFuncParamsNotNull = (functionName != null) && (functionOrdinal != null);
		boolean bothFuncParamsNull  = (functionName == null) && (functionOrdinal == null);
		if(! (bothFuncParamsNotNull || bothFuncParamsNull)){
			String msg = Messages.getString("UsedByOtherComponentsJob.InvalidParams_ConsoleMsg"); //$NON-NLS-1$
			AppDepConsole.getInstance().println(msg, IConsolePrintUtility.MSG_ERROR);    				
			throw new IllegalArgumentException(msg );
		}
		// If functiona name cannot be resolved...
		if(functionName != null && functionName.equals(CacheDataConstants.FUNC_NAME_NOT_RESOLVED)){
			//...using component name with ordinal instead
			functionName = componentName + "@" + functionOrdinal; //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor progressMonitor) {
		
		IStatus stat = Status.OK_STATUS;
		
		this.monitor = progressMonitor;
		
		try {
			AppDepJobManager.getInstance().registerJob(this);
			AppDepConsole.getInstance().println(Messages.getString("UsedByOtherComponentsJob.Seeking_dependencies_ConsoleMsg"));    						 //$NON-NLS-1$
			String msg = null;
				
			if(functionOrdinal != null){
				msg = Messages.getString("UsedByOtherComponentsJob.Seeking_Components_Using_ConsoleMsg") //$NON-NLS-1$
					+ "'" + functionName //$NON-NLS-1$
					+ "'";		 //$NON-NLS-1$
			}
			else{
				msg = Messages.getString("UsedByOtherComponentsJob.Seeking_Components_Using_ConsoleMsg") //$NON-NLS-1$
					+ "'" + componentName //$NON-NLS-1$
					+ "'";				 //$NON-NLS-1$
			}
			
			// Starting task
			monitor.beginTask(msg, steps);
			// Getting using components into resultComponentsArrayList
			MainViewDataPopulator.getUsingComponents(settings, this, resultComponentsArrayList, 
			                                         componentName, functionOrdinal);	
			// Handling the results
			handleResultsAndInformUser();
			
		} catch (CacheFileDoesNotExistException e1) {
			stat = handleException(Messages.getString("UsedByOtherComponentsJob.Failed_To_Seek_No_Cache_File"), e1); //$NON-NLS-1$
			this.cancel();
		} catch (IOException e2) {
			stat = handleException(Messages.getString("UsedByOtherComponentsJob.Failed_To_Seek_IO_Exception") //$NON-NLS-1$
					                         + e2.getMessage() + ").", e2); //$NON-NLS-1$
			this.cancel();
		} catch (JobCancelledByUserException e) {
			// Job was cancelled by the user
			stat = Status.CANCEL_STATUS;
		}finally{
			AppDepJobManager.getInstance().unregisterJob(this);
			monitor.done();
			
		}
		
        if ((stat == Status.OK_STATUS) && isCanceled()){        		
            return Status.CANCEL_STATUS;
        }
        
        return stat;
	  }

	/**
	 * Handles exception and wraps it into <code>IStatus</code> object.
	 * @param consoleMsg message to be shown in console.
	 * @param exception exception encountered.
	 * @return <code>IStatus</code> object.
	 */
	private IStatus handleException(String consoleMsg, Throwable exception){
		AppDepConsole.getInstance().println(consoleMsg, IConsolePrintUtility.MSG_ERROR);
		return new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR, consoleMsg, exception);
	}
	
	/**
	 * Checks job results and in case of successful ending launches
	 * component list view.
	 */
	public void handleResultsAndInformUser() {
		
        if (isCanceled()){        		
            return;
        }

        if (isCanceled()){        		
            return;
        }
        monitor.subTask(Messages.getString("UsedByOtherComponentsJob.Activatinb_View"));	 //$NON-NLS-1$
        final int stepsLeft = (steps - previousPercentage);
        monitor.worked(stepsLeft);
        
		// Launching the component list view
		Runnable launchViewRunnable = new Runnable(){
			public void run(){
				launchListView();
			}
		};
		
		// Update request done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(launchViewRunnable);        		    		
	}

	/**
	 * Launches list view and shows data for the user.
	 */
	private void launchListView() {
		
	   try {
			IWorkbenchPage page = activeWbPage;

			// Checking if view is already open
			IViewReference[] viewRefs = activeWbPage.getViewReferences();
			
			for (int i = 0; i < viewRefs.length; i++) {
				IViewReference reference = viewRefs[i];
				String id = reference.getId();
				if(id.equalsIgnoreCase(ListView.ID)){
					// Found, restoring the view
					IViewPart viewPart = reference.getView(true);
					ListView listView = (ListView) viewPart;
					updateListView(listView);
					page.activate(viewPart);
					return;
				}
			}
			IViewPart viewPart = page.showView(ListView.ID);
			ListView listView = (ListView) viewPart;
			updateListView(listView);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(Messages.getString("UsedByOtherComponentsJob.Failed_To_Invoke_ListView_ConsoleMsg")  //$NON-NLS-1$
											+ e.getMessage() 
											+ ").", //$NON-NLS-1$
											IConsolePrintUtility.MSG_ERROR);    				
		}
		
	}

	/**
	 * Updates information to the list view.
	 * @param listView List view to be updated.
	 */
	private void updateListView(ListView listView) {
		listView.setInput(resultComponentsArrayList);
		if(functionName != null){
			listViewTitleString = Messages.getString("UsedByOtherComponentsJob.Found") + resultComponentsArrayList.size()  //$NON-NLS-1$
			    +Messages.getString("UsedByOtherComponentsJob.Components_Using_Function") //$NON-NLS-1$
				+ "'" + functionName //$NON-NLS-1$
				+ "'";						 //$NON-NLS-1$
		}
		else{
			listViewTitleString = Messages.getString("UsedByOtherComponentsJob.Found") + resultComponentsArrayList.size()  //$NON-NLS-1$
			    +Messages.getString("UsedByOtherComponentsJob.Components_Using_Component") //$NON-NLS-1$
				+ "'" + componentName //$NON-NLS-1$
				+ "'";				 //$NON-NLS-1$
		}
		listView.updateDescription(listViewTitleString);
		listView.setComponentName(componentName);
		// Storing function name => set automatically to null if only queried for a component
		listView.setFunctionName(functionName);		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IManageableJob#forcedShutdown()
	 */
	public void forcedShutdown() {
		if(proc != null){
			proc.destroy();
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}					
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.IJobProgressStatus#progress(int, java.lang.String)
	 */
	public void progress(int percentage, String prosessing) throws JobCancelledByUserException {
        if (isCanceled()){
        	String msg = Messages.getString("UsedByOtherComponentsJob.Canceled_By_User_ConsoleMsg"); //$NON-NLS-1$
    		AppDepConsole.getInstance().println(msg);
    		throw new JobCancelledByUserException(msg);
        }
        monitor.subTask(percentage + Messages.getString("UsedByOtherComponentsJob.Percentage_Completed")); //$NON-NLS-1$
        monitor.worked(percentage - previousPercentage);
        previousPercentage = percentage;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.IJobProgressStatus#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}
	
}
