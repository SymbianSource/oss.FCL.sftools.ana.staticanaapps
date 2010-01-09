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


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.export.ExportJob;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;


/**
 * Triggers export report functionality.
 */
public class ExportReportMainViewAction extends AbstractMainViewAction 
	implements IJobChangeListener  {

	//
	// Members
	//
	private String exportHTMLFileName;
	private String exportXMLFileName;
	private String exportPath = ""; //$NON-NLS-1$
	private String exportFilePrefix = Messages.getString("ExportReportMainViewAction.ExportFilePrefix_String"); //$NON-NLS-1$
	private String completedMessage;	
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ExportReportMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("ExportReportMainViewAction.ExportReport_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ExportReportMainViewAction.ExportReport_Action_Tooltip")); //$NON-NLS-1$
		
		setId("com.nokia.s60tools.appdep.ui.actions.ExportReportMainViewAction"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {

		// Parent shell
		Shell sh = view.getViewSite().getShell();

		Object obj = view.getComponentTreeSelectedElement();		
		if(obj == null){
			// We might get null-selections when
			// tree is expanded/collapsed.
			return;
		}
					
		// Component is for sure a component node
		ComponentNode node = (ComponentNode) obj;

		setExportHTMLFileName( exportFilePrefix + node.getName() + ".html"); //$NON-NLS-1$
		setExportXMLFileName( exportFilePrefix + node.getName() + ".xml"); //$NON-NLS-1$
		
		String compToSearchFor = null;
		compToSearchFor =  AppDepSettings.getActiveSettings().getCurrentlyAnalyzedComponentName();		
		
		if(compToSearchFor == null){
			// No component to be analyzed
			completedMessage = Messages.getString("GeneralMessages.Select_SDK_First_ErrMsg"); //$NON-NLS-1$
			showCompletedMessage();
			return;
		}		

		FileDialog fdia = new FileDialog(sh, SWT.SAVE);

		String path = AppDepSettings.getActiveSettings().getExportPrintReportPath();
		if(path == null){
			path =""; //$NON-NLS-1$
		}
		
		setExportPath(path);
		fdia.setFileName(getExportHTMLFileNameAndPath());

		String msg = Messages.getString("ExportReportMainViewAction.ExportReport_Msg_Start"); //$NON-NLS-1$
		//If XML file is generated aswell, adding info for that to note
		if(AppDepSettings.getActiveSettings().isExportXMLreport()){
			msg 
				+= ". " + Messages.getString("ExportReportMainViewAction.ExportReport_Msg_End"); //$NON-NLS-1$ //$NON-NLS-2$
		}		
		fdia.setText(msg);

		String fullPath = fdia.open();		
		
		//User select "Cancel"
		if(fullPath == null){
			return;
		}
		setExportPath(fdia.getFilterPath());
		setExportHTMLFileName(fdia.getFileName());
		//Setting default path to most recently used export path
		AppDepSettings.getActiveSettings().setExportPrintReportPath(fdia.getFilterPath());
			
		String jobName = ProductInfoRegistry.getProductName() 
		                 + " " + Messages.getString("ExportReportMainViewAction.Export") + " "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		                 + node.getName();
		
		ExportJob job = new ExportJob(
				jobName, 
				node, 
				getExportHTMLFileNameAndPath(), 
				getExportXMLFileNameAndPath() );
		
		job.addJobChangeListener(this);		
		job.setPriority(Job.DECORATE);
		job.schedule();
	
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
	}

	/**
	 * Gets file path name for exported HTML file.
	 * @return file path name for exported HTML file.
	 */
	public String getExportHTMLFileNameAndPath() {
		
		String prefix = ("".equals(exportPath)) ? "" : exportPath  //$NON-NLS-1$ //$NON-NLS-2$
				+ System.getProperty("file.separator");  //$NON-NLS-1$
		
		return prefix +exportHTMLFileName;
	}

	/**
	 * Sets file path name for exported HTML file.
	 * @param name file path name for exported HTML file
	 */
	private void setExportHTMLFileName(String name) {
		this.exportHTMLFileName = name;
	}

	/**
	 * Sets destination directory for export.
	 * @param exportPath destination directory for export.
	 */
	private void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	/**
	 * Gets file path name for exported XML file.
	 * @param name file path name for exported HTML file
	 */
	public String getExportXMLFileNameAndPath() {
		return exportPath 
			+ System.getProperty("file.separator")  //$NON-NLS-1$
			+ exportXMLFileName;
	}

	/**
	 * Sets file path name for exported XML file.
	 * @param name file path name for exported HTML file
	 */
	private void setExportXMLFileName(String name) {
		this.exportXMLFileName = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		// We do not need to do anything because of this event		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		// We do not need to do anything because of this event		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {

		IStatus status = event.getResult();
		
		//Showing note only if not cancelled
		if(status.getSeverity() == IStatus.OK){

			// Runnable implementing the actual printing to console
			Runnable showCompletedMessagRunnable = new Runnable(){
				public void run(){				
						showCompletedMessage();										
				}
			};
			
			completedMessage = 
				Messages.getString("ExportReportMainViewAction.HTMLExported_Msg") + ": " + getExportHTMLFileNameAndPath(); //$NON-NLS-1$ //$NON-NLS-2$
			//If XML file is generated as well, adding info for that to note
			if(AppDepSettings.getActiveSettings().isExportXMLreport()){
				completedMessage 
					+= "\n" + Messages.getString("ExportReportMainViewAction.XMLExported_Msg") + ": "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+getExportXMLFileNameAndPath();
			}
						
			Display.getDefault().asyncExec(showCompletedMessagRunnable);   

		}	

		
	}

	/**
	 * Shows export completed message.
	 */
	private void showCompletedMessage() {
		Shell sh = view.getViewSite().getShell();
		AppDepMessageBox msgBox = new AppDepMessageBox(sh, completedMessage, SWT.OK | SWT.ICON_INFORMATION);
		msgBox.open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// We do not need to do anything on this event		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		// We do not need to do anything on this event		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// We do not need to do anything on this event		
	}
	
}
