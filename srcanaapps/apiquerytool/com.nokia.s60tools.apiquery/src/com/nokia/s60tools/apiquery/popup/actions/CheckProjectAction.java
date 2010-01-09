/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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
 
package com.nokia.s60tools.apiquery.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;

import com.nokia.s60tools.apiquery.job.ActiveProjectQueryJob;
import com.nokia.s60tools.apiquery.servlets.APIQueryWebServerConfigurator;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.dialogs.OpenReportStatusDialog;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Check project action is invoked from <b>API Query > for Active Project</b> popup menu.
 * The action finds all the identifier candidates from the project and used them 
 * to form a query used to figure out which APIs the project is using. This
 * action supports only projects with Carbide.c++ project nature.
 */
public class CheckProjectAction implements IObjectActionDelegate, IJobChangeListener {
	

	private static final String DEFAULT_EXPORT_FILENAME_SUFFIX = "-project_is_using_APIs.html"; //$NON-NLS-1$

	/**
	 * The project that user has selected from a project.
	 */
	IProject selectedProject;
		
	
	/**
	 * Constructor.
	 */
	public CheckProjectAction() {
		super();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {

			final IPath path = openSaveAsDialog();
			if(path == null){
				return ;
			}
			
			//When API Query for project is started, making sure that web server is running
			APIQueryWebServerConfigurator.startServer(APIQueryWebServerConfigurator.Active_Project_Start);
			
			//Creating job, must be running inside of Runnable, because of UI Thread connections
			final ActiveProjectQueryJob job = new ActiveProjectQueryJob(
					Messages.getString("CheckProjectAction.APIQuery_JobName_Msg")+action.getText() +" (" + getProjectName()  //$NON-NLS-1$ //$NON-NLS-2$
					+").", selectedProject, path); //$NON-NLS-1$
		
			job.setPriority(Job.DECORATE);
			//Cant .join() to job because of deathlock @see .join() documentation.
			job.addJobChangeListener(this);			
			job.reportStartTime();
			//Start to run
			job.schedule();
			
		} catch (Exception e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().println(e.getMessage(), 
					 IConsolePrintUtility.MSG_ERROR);
			showErrorDialog(Messages.getString("CheckProjectAction.CannotGenerateReport_ErrMsg")  //$NON-NLS-1$
					+getProjectName() +". " +e.getMessage());			 //$NON-NLS-1$
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// Storing the selected file instance used for resolving the active project
		StructuredSelection structSel = (StructuredSelection) selection;
		Object elem = structSel.getFirstElement();
		if(elem instanceof IProject){
			 this.selectedProject = (IProject) elem;			
		}
		else if(elem instanceof IFile){
			 this.selectedProject = ((IFile) elem).getProject();						
		}
		else if(elem instanceof IFolder){
			 this.selectedProject = ((IFolder) elem).getProject();						
		}
		else{
			action.setEnabled(false);
			return;
		}
		action.setEnabled(true);
	}
	
	
	/**
	 * Shows save as dialog and set this.exportFileName
	 * @return IPath path if fileName was set, null if dialog was not epened or cancel was pushed
	 *
	 */
	private IPath openSaveAsDialog() {
		Shell shell = APIQueryPlugin.getCurrentlyActiveWbWindowShell();
		SaveAsDialog saveAs = new SaveAsDialog(shell);
		IFile file = selectedProject.getFile(getDefaultFileName());
		saveAs.setOriginalFile(file);
		
		int status = saveAs.open();
		if(status == SaveAsDialog.OK ){
			IPath path = saveAs.getResult();
			if(saveAs.getReturnCode() == SaveAsDialog.OK ){
				return path;
			}else{
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * Asking user if he/she want's to open created file or not
	 * by opening a Dialog
	 * @param newFile
	 * @return
	 */
	private boolean openRequired(IFile newFile){
		Shell sh = APIQueryPlugin.getCurrentlyActiveWbWindowShell();
		String fileName = newFile.getName();
		OpenReportStatusDialog in = new OpenReportStatusDialog(sh, fileName);
		
		in.open();
		if( in.getReturnCode() == OpenReportStatusDialog.OK){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Opens generated report to workspace
	 * @param newFile
	 * @throws CoreException
	 */
	private void openReport(final IFile newFile) throws CoreException{
		
		if(!openRequired(newFile)){
			return;
		}
		
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		//Runnable to open new file
		final IWorkspaceRunnable runOpen = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// do the actual work in here
	
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, newFile, true);
	
				} catch (Exception e) {
					//PartInitException may occur
					e.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							"com.nokia.s60tools.apiquery", 0, e.getMessage(), e); //$NON-NLS-1$
	
					throw new CoreException(status);
				} 
	
			}
		};	
		workspace.run(runOpen, null, IWorkspace.AVOID_UPDATE, null);
	}


	/**
	 * get default name export file to
	 * @return filename
	 */
	private String getDefaultFileName() {
		String name = getProjectName() +DEFAULT_EXPORT_FILENAME_SUFFIX;
		return name;
	}


	/**
	 * get project name
	 * @return project name
	 */
	private String getProjectName() {
		return selectedProject.getName();
	}
	
	/**
	 * Show an error Dialog
	 * @param errMsg
	 */
	
	private void showErrorDialog(String errMsg) {
		APIQueryMessageBox mbox = new APIQueryMessageBox(errMsg, SWT.ICON_ERROR | SWT.OK);
		mbox.open();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 * 
	 * When done, asking if user wants to open created file. If Cancelled, doing nothing,
	 * if an error occurs, system will show an error dialog.
	 * 
	 */
	public void done(IJobChangeEvent event) {

		ActiveProjectQueryJob job = (ActiveProjectQueryJob) event.getJob();
		
		job.reportEndTime();
		
		IStatus status = job.getResult();
		
		if(status.getSeverity() == IStatus.OK){
			final IFile file = job.getGeneratedReportFile();

				
			// Must do query in Runnable, because of UI actions from job
			Runnable activeProjectQueryJobRunnable = new Runnable() {
				public void run() {
					try {
						openReport(file);
					} catch (CoreException e) {
						APIQueryConsole.getInstance().println(Messages.getString("CheckProjectAction.UnableToOpenReport_ErrMsg") + e.getMessage(),  //$NON-NLS-1$
								 IConsolePrintUtility.MSG_ERROR);
						e.printStackTrace();
					}
				}
			};

			// Showing a visible message has to be done in its own thread
			// in order not to cause invalid thread access
			Display.getDefault().asyncExec(activeProjectQueryJobRunnable);				
				
		
		}

	}

}
