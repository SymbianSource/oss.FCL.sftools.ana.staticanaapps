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

import java.net.URISyntaxException;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.locatecomponent.CreateProjectJob;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.sourcecode.ISourceFinder;
import com.nokia.s60tools.util.sourcecode.SourceFileLocation;
import com.nokia.s60tools.util.sourcecode.SourceFinderFactory;

/**
 * Real implementation for show source file in project action.
 */
public abstract class ShowSourceFileAndCreateProjectMainViewAction extends AbstractShowSourceFileAction implements IJobChangeListener{
	
	/**
	 * Source file location.
	 */
	private SourceFileLocation location = null;

	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ShowSourceFileAndCreateProjectMainViewAction(MainView view){
		super(view);

		setText(Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.ShowSource_Action_Text"));  //$NON-NLS-1$
		setToolTipText(Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.ShowSource_Action_Tooltip"));  //$NON-NLS-1$
	}
	
	/**
	 * Real run method implementation for run() method.
	 * @see com.nokia.s60tools.appdep.ui.actions.AbstractMainViewAction#run()
	 * @param methodName method name from user selection
	 * @param ordinal from user selection
	 */
	protected void runImpl(String methodName,
	String ordinal) {
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
			boolean showSISfileWarningDialog = false;

			// Component is for sure a component node
			ComponentNode node = (ComponentNode) obj;
			String componentName = node.getName();

			AppDepSettings settings = AppDepSettings.getActiveSettings();
			if (settings.getCurrentlyAnalyzedComponentName() != null) {

				AppDepConsole.getInstance().println(
						Messages.getString("ShowSourceFileMainViewAction.ShowSource_Query_Start_Console_Msg_Part1") //$NON-NLS-1$
										+ componentName//$NON-NLS-1$ 
										+Messages.getString("ShowSourceFileMainViewAction.ShowSource_Query_Start_Console_Msg_Part2") //$NON-NLS-1$
										+ ordinal//$NON-NLS-1$										
										+ "'..."); //$NON-NLS-1$
				
				// Collection needed information to get source file path
				ISourceFinder finder = SourceFinderFactory
						.createSourceFinder(AppDepConsole.getInstance());
				
				// Resolving source search parameters based on the settings and component name
				resolveSearchParameters(componentName, settings);

				location = finder.findSourceFile(ordinal,
						methodName, componentName, variant, build, epocRootPath);
				if(location.getSourceFileLocation() == null){
					
					String msg = Messages.getString("ShowSourceFileMainViewAction.SourceFileCannotBeFound_Msg_Part1") //$NON-NLS-1$
						+ componentName//$NON-NLS-1$
						+Messages.getString("ShowSourceFileMainViewAction.SourceFileCannotBeFound_Msg_Part2") //$NON-NLS-1$
						+ ordinal//$NON-NLS-1$
						+Messages.getString("ShowSourceFileMainViewAction.SourceFileCannotBeFound_Msg_Part3") //$NON-NLS-1$
						+ methodName//$NON-NLS-1$
						+"'";//$NON-NLS-1$
					
					AppDepConsole.getInstance().println(msg, AppDepConsole.MSG_WARNING); 
					
					showErrorMsgDialog(msg);
					
				}else{
					
					// Getting files in same location from workspace.
					IPath path = new Path(location.getSourceFileLocation());	
					IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
					
					if(files.length > 0){
						// Same file was found from projects.
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();	
						IWorkbenchPage page = window.getActivePage();
						
						IFile fileToOpen = files[0];
						
						if (page != null){
							// Opening project if it isn't already open.
							fileToOpen.getProject().open(null);
							
							// Opens first found file.
							IEditorPart part = IDE.openEditor(page, fileToOpen);

							if(part != null){
								//Set focus to correct line
								setFocusToLineWhereMethodIs(location);
							}					   
						}		
					}
					else {
						// Creating new project from file.
						String jobName = ProductInfoRegistry.getProductName() 
			            	+ Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.CreateProjectJob_Title_Text")  //$NON-NLS-1$
			            	+ location.getSourceFileLocation();					
						//searching components in job.
						CreateProjectJob job = new CreateProjectJob(
							jobName, 
							location, true );		
						job.addJobChangeListener(this);		
						job.setPriority(Job.DECORATE);
						job.schedule();
					}
				}
				//If SIS file is in analysis, showing on information dialog, that opened suorce file is not neccessarily same than used when SIS was build.
				if(showSISfileWarningDialog ){
					AppDepMessageBox msgBox = new AppDepMessageBox(
							Messages.getString("ShowSourceFileMainViewAction.SourceFileOpenedFromSISFile_Info_Msg"),  //$NON-NLS-1$
							SWT.ICON_INFORMATION | SWT.OK);
					msgBox.open();						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = Messages.getString("ShowSourceFileMainViewAction.SourceFileCannotBeFound_ErrMsg") //$NON-NLS-1$
			+" "//$NON-NLS-1$
			+ e.getMessage();
			AppDepConsole.getInstance().println(
					msg,  //$NON-NLS-1$
					AppDepConsole.MSG_ERROR);
			showErrorMsgDialog(Messages.getString("ShowSourceFileMainViewAction.SourceFileCannotBeFound_ErrMsg_ToUser"));//$NON-NLS-1$
			
		}		
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
	}
	
	/**
	 * Opening File in editor
	 * @param location
	 * @throws URISyntaxException
	 * @throws CoreException
	 */
	private void openFileAndSetFocus(final SourceFileLocation location, final ICProject cProject) throws CoreException {

		AppDepConsole.getInstance().println(
				Messages.getString("ShowSourceFileMainViewAction.OpeningFile_Msg") //$NON-NLS-1$
					+ location.getSourceFileLocation() //$NON-NLS-1$)					
					+ "'"); //$NON-NLS-1$)
		
		//Runnable to open new file
		final IWorkspaceRunnable runOpen = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// do the actual work in here
				try {

					IWorkbench workbench = PlatformUI.getWorkbench();
					IPath path = new Path(location.getSourceFileLocation());		

					// FindElement doesn't work with every with every file. Using it as default
					// and opening file with link otherwise.
					ICElement element = null;
					IFile file = null;

					// First try to get file with findElement.
					try {
						element = cProject.findElement(path);
						// Removing first segment from path, because project folder is not needed.
						file = cProject.getProject().getFile(element.getPath().removeFirstSegments(1));
					} catch (CModelException e) {
						// Finding element failed. Creating link instead.
					}

					// Getting file by creating a link only if file can't be opened.
					if(element == null){
						file = cProject.getProject().getFile(path.lastSegment());
						file.createLink(path, IResource.REPLACE, null);
					}

					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();	
					IWorkbenchPage page = window.getActivePage();

					if (page != null){
						IEditorPart part = IDE.openEditor(page, file);

						if(part != null){
							//Set focus to correct line
							setFocusToLineWhereMethodIs(location);
						}					   
					}					
				} catch (PartInitException e) {
					e.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							Messages.getString("ShowSourceFileActionGeneralMessage.OpenFileAndSetLineFocus_ErrMsg"), 0, e //$NON-NLS-1$
							.getMessage(), e);

					throw new CoreException(status);
				} 
			}
		};		
		ResourcesPlugin.getWorkspace().run(runOpen, null, IWorkspace.AVOID_UPDATE, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		
		IStatus status = event.getResult();
		CreateProjectJob job = (CreateProjectJob) event.getJob();

		final ICProject cProject = job.getCProject();
		
		//Showing dialog only if not canceled
		if(status.getSeverity() == IStatus.OK){

			// Runnable implementing the actual printing to console
			Runnable completedRunnable = new Runnable(){
				public void run(){				
					//Open found file, and set focus to method line
					try {
						openFileAndSetFocus(location, cProject);
					} catch (CoreException e) {
						String msg = Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.SourceFileOpenFailed_ErrMsg") //$NON-NLS-1$
						+ e.getMessage();
						AppDepConsole.getInstance().println(
								msg,
								AppDepConsole.MSG_ERROR);
						showErrorMsgDialog(Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.SourceFileOpenFailed_ErrMsg_ToUser")); //$NON-NLS-1$
					}								
				}
			};

			Display.getDefault().asyncExec(completedRunnable);   
		}			
	}
	
	public void aboutToRun(IJobChangeEvent event) {
		// Not needed
	}

	public void awake(IJobChangeEvent event) {
		// Not needed
	}

	public void running(IJobChangeEvent event) {
		// Not needed
	}

	public void scheduled(IJobChangeEvent event) {
		// Not needed
	}

	public void sleeping(IJobChangeEvent event) {
		// Not needed
	}	
}
