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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.nokia.carbide.cdt.builder.CarbideBuilderPlugin;
import com.nokia.carbide.cdt.builder.EpocEngineHelper;
import com.nokia.carbide.cdt.builder.ICarbideBuildManager;
import com.nokia.carbide.cdt.builder.project.ICarbideBuildConfiguration;
import com.nokia.carbide.cdt.builder.project.ICarbideProjectInfo;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.job.FindFileFromFoldersJob;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;

/**
 * Action class for find and then open wanted file to Editor area. 
 * File can be seeked under SDK include paths or
 * under given project in Carbides Workspace.
 */
public class OpenFileAction implements IJobChangeListener{
	
	/**
	 * Current default build configuration SDK ID. 
	 */
	private String sdkUniqueId = null;
	private String fileName = null;
	private String projectName;

	/**
	 * Opens file from given project in Carbide workspace.
	 * @param fileName
	 * @param projectName
	 */
	public void openFileFromProject(String fileName, String projectName) {

		this.fileName = fileName;
		this.projectName = projectName;
		try {
			
			APIQueryConsole.getInstance().println(Messages.getString("OpenFileAction.StartingSeek_Msg_Part1") +fileName +Messages.getString("OpenFileAction.StartingSeek_Msg_Part2") +projectName +"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			IProject project = getProject(projectName);
			
			IResource[] members = project.members();
		
			Vector<IResource> files = getMatchingResources(fileName, members);
			
			if(!files.isEmpty()){
				
				//It should not be possible that there is multiple foundings for resource, because path is added when file was seeked
				IResource res = files.get(0);									
				IPath path = res.getFullPath();
				URI uri = res.getLocationURI();
				openFile(uri, path.lastSegment());
			}else{
				String message = Messages.getString("OpenFileAction.CannotFoundFile_ErrMsg_Part1") +fileName +Messages.getString("OpenFileAction.CannotFoundFile_ErrMsg_Part2") +projectName +"'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				showErrorMessage(message);		

			}
		
		} catch (Exception e) {
			showUnexpectedErrorMsg(e);
		}

	}


	/**
	 * Logs and shows error message for unexpected error situations.
	 * @param fileName
	 * @param projectName
	 * @param e {@link Exception}
	 */
 private void showUnexpectedErrorMsg(Exception e) {
		e.printStackTrace();
		String msg = Messages.getString("OpenFileAction.Unexpected_ErrMsg_Part1")+fileName +Messages.getString("OpenFileAction.Unexpected_ErrMsg_Part2") +projectName +"'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String consoleMsg = msg +Messages.getString("OpenFileAction.Unexpected_ErrMsg_Part3") +e; //$NON-NLS-1$
		APIQueryConsole.getInstance().println(consoleMsg, APIQueryConsole.MSG_ERROR);
		showErrorMessage(msg);
	}



	/**
	 * Show an error message to user and logs same message to console.
	 * @param message
	 */
	private void showErrorMessage(final String message) {
		APIQueryConsole.getInstance().println(message, APIQueryConsole.MSG_ERROR);		
		Runnable run = new Runnable(){
			public void run(){
				new APIQueryMessageBox(message,
						SWT.ICON_ERROR | SWT.OK).open();
				
			}
		};
		Display.getDefault().asyncExec(run);
	}
	


	/** 
	 * Check recursively all folders inside given resources and seeks give file
	 * @param fileName
	 * @param members
	 * @return resources found
	 * @throws CoreException
	 */
	private Vector<IResource> getMatchingResources(String fileName,
			IResource[] members) throws CoreException {
		Vector<IResource> files = new Vector<IResource>();

		for (int i = 0; i < members.length; i++) {
			IResource res = members[i];
			//If resource is folder, seeking all folders and files under it
			if (res instanceof IFolder) {
				IFolder fold = (IFolder) res;
				IResource[] folderMembers = fold.members();
				files.addAll(getMatchingResources(fileName, folderMembers));
			}
			//If resource is file, checking if thats file we are interest of
			else if (res instanceof IFile) {

				String name = res.getName();
				//If file name match to resource name adding to
				if (fileName.equalsIgnoreCase(name)) {
					files.add(res);
				}else if(fileName.equalsIgnoreCase(res.getProjectRelativePath().toOSString())){
					files.add(res);
				}
			}
			//Else we have no interest of resource
		}
		return files;
	}
	
	/**
	 * Open file given to Editor area. 
	 * @param uri
	 * @param fileName
	 */
	public void openFile(final URI uri, final String fileName) {		
		
		//Runnable to open new file
		final Runnable runOpen = new Runnable() {
			public void run() {
				// do the actual work in here
								
				try {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchPage page = APIQueryPlugin.getCurrentlyActivePage();
					//Find default editor for that file
					IEditorRegistry reg = workbench.getEditorRegistry();					
					IEditorDescriptor editor = reg.getDefaultEditor(fileName);
					//We open editor by it's ID
					final String editorId = editor.getId();
					//Opening file in editor
					IDE.openEditor(page, uri, editorId, true);
					
				} catch (Exception e) {
					showUnexpectedErrorMsg(e);
				} 

			}
		};		
		Display.getDefault().asyncExec(runOpen);
	}	
	

	/**
	 * Open file to Editor area
	 * @param file
	 * @throws URISyntaxException 
	 */
	private void openFile(File file) throws URISyntaxException {
		String path = file.getAbsolutePath();
		String uriStr = path.replace("\\", "/");; //$NON-NLS-1$ //$NON-NLS-2$
		uriStr = "file://" + uriStr; //$NON-NLS-1$			
		URI uri = new URI(uriStr);
		openFile(uri, file.getName());
	}	
	
	/**
	 * Opens file from SDK. Projects default build configuration is used as SDK,
	 * MMP files from that project is seeked, and file is seeked under inc paths found in 
	 * MMP files.
	 * 
	 * File is opened or error message is shown when done.
	 * 
	 * @param fileName
	 * @param projectName
	 */
	public void openFileFromSDK(String fileName, String projectName) {

		this.fileName = fileName;
		this.projectName = projectName;
		try {
			//Get project by name
			IProject project = getProject(projectName);
			//Gets Include paths for project. Header files must be found from includepaths.
			List<File> paths = getIncludePaths(project);
			APIQueryConsole.getInstance().println(
					Messages.getString("OpenFileAction.TargetFound_Msg_Part1") +sdkUniqueId  //$NON-NLS-1$
					+Messages.getString("OpenFileAction.TargetFound_Msg_Part2") +projectName  //$NON-NLS-1$
					+Messages.getString("OpenFileAction.TargetFound_Msg_Part3") +fileName +"'."); //$NON-NLS-1$ //$NON-NLS-2$
			
			//Seeking paths where file(s) are found
			//it will happened asynchronously, and when done, file will be opened.
			findFiles(fileName, paths);
			
		} catch (QueryOperationFailedException e) {
			showErrorMessage(e.getMessage());
		}catch (Exception e) {
			showUnexpectedErrorMsg(e);
		}
		

	}


	/**
	 * Get include paths for project
	 * @param project
	 * @return absolutely include paths for project, found in MMP files.
	 * @throws QueryOperationFailedException 
	 */
	private List<File> getIncludePaths(IProject project) throws QueryOperationFailedException {
		List<File> paths = new ArrayList<File>();
		
		ICarbideBuildManager buildMgr = CarbideBuilderPlugin.getBuildManager();
		ICarbideProjectInfo cpi = null;
		if (buildMgr.isCarbideProject(project)){
		  // check to make sure this is a Carbide project
		  cpi = buildMgr.getProjectInfo(project);
			// Get the default build configuration
			ICarbideBuildConfiguration defaultConfig = cpi.getDefaultConfiguration();
			sdkUniqueId = defaultConfig.getSDK().getUniqueId();
			
			List<File> _userPaths = new ArrayList<File>();
			List<File> _systemPaths = new ArrayList<File>();
			EpocEngineHelper.getProjectIncludePaths(cpi, defaultConfig, _userPaths, _systemPaths) ;
			paths.addAll(_userPaths);
			paths.addAll(_systemPaths);
		}		
		else{
			throw new QueryOperationFailedException(Messages.getString("OpenFileAction.NotCarbideProject_ErrMsg_Part1") +projectName +Messages.getString("OpenFileAction.NotCarbideProject_ErrMsg_Part2")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		
		return paths;
	}

	/**
	 * Get project by name.
	 * @param projectName
	 * @return project
	 * @throws CoreException 
	 * @throws QueryOperationFailedException 
	 */
	private IProject getProject(String projectName) throws CoreException, QueryOperationFailedException {
		IProject prj = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if(!prj.exists()){
			 throw new QueryOperationFailedException (Messages.getString("OpenFileAction.ProjectNotExist_ErrMsg_Part1") +projectName +Messages.getString("OpenFileAction.ProjectNotExist_ErrMsg_Part2")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(!prj.isOpen()){
			//If project is closed, just opening it before returning
			prj.open(new NullProgressMonitor());
		}
		prj.getWorkspace();
		return prj;
	}


	
	/**
	 * Find file under folders given
	 * @param fileName
	 * @param includePaths
	 */
	private void findFiles(String fileName, List<File> includePaths) {
			//	System.out.println(Messages.getString("OpenFileAction.18")); //$NON-NLS-1$
		FindFileFromFoldersJob job = new FindFileFromFoldersJob(Messages.getString("OpenFileAction.JobName_Msg") +fileName, includePaths, fileName, sdkUniqueId); //$NON-NLS-1$
		job.setPriority(Job.DECORATE);
		//Listener will be called when seeking is done
		job.addJobChangeListener(this);
		job.schedule();	
		
	}




	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {

		//
		// When job is done, this will be called, and found file will be opened

		try {
			FindFileFromFoldersJob job = (FindFileFromFoldersJob) event
					.getJob();
			Collection<File> files = job.getFoundSourceFiles();

			if (files.isEmpty()) {
				String message = Messages.getString("OpenFileAction.CannotFoundFile_Msg_Part1") + fileName //$NON-NLS-1$
						+ Messages.getString("OpenFileAction.CannotFoundFile_Msg_Part2") + sdkUniqueId //$NON-NLS-1$
						+ "'."; //$NON-NLS-1$
				showErrorMessage(message);
			}
			//Else we just open first file that we found
			else{
				File file = ((File[]) files.toArray(new File[0]))[0];
				openFile(file);
			}
		} catch (Exception e) {
			showUnexpectedErrorMsg(e);
		}

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
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		//Not needed		
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
		//Not needed		
	}



}
