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

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.locatecomponent.LocateComponentJob;
import com.nokia.s60tools.appdep.locatecomponent.SeekParentNodesService;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.dialogs.LocateComponentDialog;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.ui.views.main.MainViewDataPopulator;
import com.nokia.s60tools.appdep.ui.views.main.MainViewPopulateProgressListener;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Locates concrete component for the selected generic component from component tree.
 */
public class LocateComponentMainViewAction extends AbstractMainViewAction implements IJobChangeListener{
	
	/**
	 * Name of the generic component.
	 */
	private String componentName;
	/**
	 * Selected node (=generic component) from component tree.
	 */
	private ComponentNode startNode;

	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public LocateComponentMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("LocateComponentMainViewAction.Locate_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("LocateSourceMainViewAction.Locate_Action_Tooltip")); //$NON-NLS-1$
	
		
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/
		//Adding image descriptor if an icon is created for this action		
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.BIN_OBJ_BIND));	
				
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		Object obj = view.getComponentTreeSelectedElement();
					
		if(obj == null){
			// We might get null-selections when
			// tree is expanded/collapsed.
			return;
		}

		startNode = (ComponentNode) obj;
		componentName = startNode.getName();
		AppDepSettings settings = AppDepSettings.getActiveSettings();
		if(settings.getCurrentlyAnalyzedComponentName() != null){
			AppDepConsole.getInstance().println(Messages.getString("LocateComponentMainViewAction.Query_Start_Console_Msg") //$NON-NLS-1$
                    + componentName + "'..."); //$NON-NLS-1$
			
			String jobName = ProductInfoRegistry.getProductName() 
				+ " " + Messages.getString("LocateComponentMainViewAction.LocateJobName_Str") + " "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            	+ componentName;

			//searching components in job.
			LocateComponentJob job = new LocateComponentJob(
				jobName, 
				componentName );		
			job.addJobChangeListener(this);		
			job.setPriority(Job.DECORATE);
			job.schedule();

		}
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		//Not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		//Not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		
		IStatus status = event.getResult();
		LocateComponentJob job = (LocateComponentJob) event.getJob();
		final String [] components = job.getConcreteComponent();
		
		//Showing dialog only if not cancelled
		if(status.getSeverity() == IStatus.OK){

			// Runnable implementing the actual printing to console
			Runnable completedRunnable = new Runnable(){
				public void run(){				
					openDialog(components);										
				}
			};
									
			Display.getDefault().asyncExec(completedRunnable);   
		}					
	}
	
	/**
	 * Opens the dialog with search results
	 * @param components Component alternatives to be shown in dialog.
	 */
	private void openDialog(String [] components){
		Shell sh = view.getViewSite().getShell();
		
		// Did we found any concrete component candidates for generic component name? 
		if(components.length == 0){
			String infoMsg = Messages.getString("LocateComponentMainViewAction.NoConcreteComponentsFound_InfoMsg_Part1") + componentName + Messages.getString("LocateComponentMainViewAction.NoConcreteComponentsFound_InfoMsg_Part2"); //$NON-NLS-1$ //$NON-NLS-2$
			new AppDepMessageBox(sh, infoMsg, SWT.ICON_INFORMATION).open();
			return;
		}
		
		LocateComponentDialog dialog = new LocateComponentDialog(sh, components, componentName );
		int ok = dialog.open();
		
		if(ok == LocateComponentDialog.OK){
			int selection = dialog.getSelectionIndex();
			if(selection != -1){
				String newComponentName = components[selection];
				
				//Get all already found Parent nodes to prevent them to occur more than once.
				ComponentParentNode parent = startNode.getParent();
				ComponentParentNode root = parent.getRootNode();
				//Get components all ready added, to prevent duplicates
				Map<String, ComponentParentNode> parentNodes = SeekParentNodesService.findParentNodes(root);				
				MainViewPopulateProgressListener listener = new MainViewPopulateProgressListener(view);
				//populate tree from parent node of selected component
				MainViewDataPopulator.populatePartOfView(parent, newComponentName, listener, parentNodes, startNode);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// Not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		//Not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// Not needed		
	}
	
}
