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

 
package com.nokia.s60tools.appdep.ui.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.locatecomponent.ShowMethodCallLocationsJob;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Common service for implementation for show method call location actions.
 */
public abstract class AbstractShowMethodCallLocationsAction extends AbstractMainViewAction {
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public AbstractShowMethodCallLocationsAction(MainView view){
		super(view);		
		setText(Messages.getString("AbstractShowMethodCallLocationsAction.MainView_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("AbstractShowMethodCallLocationsAction.MainView_Action_Tooltip")); //$NON-NLS-1$			
	}
	
	/**
	 * Common run method implementation to method available for sub classes 
	 * and to be called from run() method of the sub class. 
	 * Method implementation that takes only method name as parameter.
	 * @param methodName Method name to search method call locations for.
	 * @param methodName method name from user selection
	 */
	protected void runImpl(String methodName) {
			
			String currentRootComponent = null;
			AppDepSettings settings = AppDepSettings.getActiveSettings();
			currentRootComponent =  settings.getCurrentlyAnalyzedComponentName();		
			
			Shell sh = AppDepPlugin.getCurrentlyActiveWbWindowShell();

			if(currentRootComponent == null){
				// User has not selected any components for analysis
				String infoMsg = Messages.getString("GeneralMessages.Select_SDK_First_ErrMsg"); //$NON-NLS-1$
				AppDepMessageBox msgBox = new AppDepMessageBox(sh, infoMsg, SWT.OK | SWT.ICON_INFORMATION);
				msgBox.open();
				return;
			}				
			
			Object obj = view.getComponentTreeSelectedElement();

			if (obj == null) {
				// We might get null-selections when
				// tree is expanded/collapsed.
				// Getting component node that is cached.
				obj = view.getMostRecentlySelectedComponentNode();
				if(obj == null){
					return;
				}
			}			
			
			try {
				// Component is for sure a component node
				ComponentNode node = (ComponentNode) obj;
				ComponentNode parent = node.getParent();
				String componentName = parent.getName();
				// Check whether can we proceed to start the Job or not .
				if(canProceedToShowMethod(componentName)){
					// Triggering method call location job
					runImpl(methodName, componentName);	
				}
				else{
					showErrorMsgDialog(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg_ToUser"));
					AppDepConsole.getInstance().println(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg")
							+Messages.getString("SourceFileFeatureCommonMessages.TheComponet_ErrMsg_Part3")+componentName+ " "
							+Messages.getString("SourceFileFeatureCommonMessages.Reason_ErrMsg_Part4"),IConsolePrintUtility.MSG_ERROR);
				}			
			
			} 		
			 catch (Exception e) {
				e.printStackTrace();
				showErrorMsgDialog(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg_ToUser")); //$NON-NLS-1$
				AppDepConsole.getInstance().println(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg") +e, AppDepConsole.MSG_ERROR); //$NON-NLS-1$
			}		
			 finally{
				// Remember to always call AbstractMainViewAction
				// base class implementation
				super.run();
			 }
					
		}

	/**
	 * Common run method implementation to method available for sub classes 
	 * and to be called from run() method of the sub class. 
	 * Method implementation that takes both method and component name as parameter.
	 * @param methodName Method name to search method call locations for.
	 * @param componentName Component name to search method call locations from.
	 */
	protected void runImpl(String methodName, String componentName) {
		String jobName = ProductInfoRegistry.getProductName() 
						+ " - " //$NON-NLS-1$ 
						+ Messages.getString("AbstractShowMethodCallLocationsAction.MethodCallLocSearch_Job_Name_Text"); //$NON-NLS-1$
		String mainTaskMessage = Messages.getString("AbstractShowMethodCallLocationsAction.SearchingMethodCallLocationsForMethod_JobInfoMsg")  + " " + methodName;  //$NON-NLS-1$ //$NON-NLS-2$

		// Creating a job for executing the search
		ShowMethodCallLocationsJob job = new ShowMethodCallLocationsJob(jobName, mainTaskMessage, componentName, methodName, true );
		
		job.setPriority(Job.DECORATE);
		job.schedule();
	}
	
	/**
	 * Shows on error message
	 * @param msg Error message.
	 */
	protected void showErrorMsgDialog(String msg){
		AppDepMessageBox msgBox = new AppDepMessageBox(msg, SWT.ICON_ERROR | SWT.OK);
		msgBox.open();			
	}
	
	/**
	 * This function will validate whether the componentName is from SIS target.
	 * if it is from Sis Target & not present in another targets then return <code>false<code>
	 * else if it is present in other targets then return <code>true<code> 
	 * @param componentName in which searching the method locations.
	 */
	protected boolean canProceedToShowMethod(String componentName){
		AppDepSettings settings = AppDepSettings.getActiveSettings();
		if(settings.getCurrentlyAnalyzedComponentTargetPlatform().getId().equalsIgnoreCase("sis")){
			// Storing component list nodes into here
			List<String> allComponentNames = new ArrayList<String>();
			List<String> duplicateItemsList = new ArrayList<String>();
			String targetPlatformId = settings.getCurrentlyAnalyzedComponentTargetPlatform().getId();
			String cacheFileAbsolutePathName = settings.getCacheFileAbsolutePathName(targetPlatformId);
			File cacheFile = new File(cacheFileAbsolutePathName);
			if(!cacheFile.exists()){
				try {
					throw new CacheFileDoesNotExistException(cacheFileAbsolutePathName);
				} catch (CacheFileDoesNotExistException e) {
					e.printStackTrace();
				}
			}
			String buildDirectory = settings.getBuildDir(targetPlatformId);
			// Getting existing or creating new instance of cache index 
			CacheIndex cacheIndx = null;
			Set<String> tmpSet = null;
			Iterator<String> tmpIter = null;
			try {
				cacheIndx = CacheIndex.getCacheIndexInstance(cacheFile, buildDirectory);
				tmpSet= cacheIndx.getComponentNameSet();
				tmpIter = tmpSet.iterator();
				CacheIndex.getComponentIteratorForGivenSettings(settings, duplicateItemsList);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CacheFileDoesNotExistException e) {
				e.printStackTrace();
			}
			// Iterating through the whole set
			if(tmpIter != null){
				while (tmpIter.hasNext()) {
					String cmpName = tmpIter.next();
					if(!allComponentNames.contains(cmpName))
						allComponentNames.add(cmpName);
				}
			}
			if(allComponentNames.contains(componentName)&& !duplicateItemsList.contains(componentName))
				return false;
		}
		
		return true;
	}
	
}
