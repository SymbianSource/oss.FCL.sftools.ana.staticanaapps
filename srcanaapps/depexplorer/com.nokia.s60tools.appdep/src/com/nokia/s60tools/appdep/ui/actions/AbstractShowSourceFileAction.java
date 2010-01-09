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
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.IDE;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.ui.views.main.MainViewDataPopulator;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.sourcecode.ISourceFinder;
import com.nokia.s60tools.util.sourcecode.SourceFileLocation;
import com.nokia.s60tools.util.sourcecode.SourceFinderFactory;

/**
 * Real implementation for show source action.
 */
public abstract class AbstractShowSourceFileAction extends AbstractMainViewAction{
	
	/**
	 * Build variant used for source file search (=target Platform used).
	 */
	protected String variant;
	/**
	 * SDK used for source file search.
	 */
	protected SdkInformation sdkInfo;
	/**
	 * Set to <code>true</code> if current target selection contains SIS file targets.
	 */
	protected boolean showSISfileWarningDialog = false;
	/**
	 * EPOCROOT used for source file search.
	 */
	protected String epocRootPath;
	/**
	 * Build type used for source file search.
	 */
	protected String build;

	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public AbstractShowSourceFileAction(MainView view){
		super(view);
		
		setText(Messages.getString("ShowSourceFileMainViewAction.ShowSource_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ShowSourceFileMainViewAction.ShowSource_Action_Tooltip")); //$NON-NLS-1$
	
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

				//Search source file location by collected information
				SourceFileLocation location = finder.findSourceFile(ordinal,
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
					//Open found file, and set focus to method line
					openFileAndSetFocus(location);
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
			+ e;
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
	 * Resolves source search parameters based on the settings and component name
	 * @param componentName Component name.
	 * @param settings Used settings.
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 * @throws CacheFileDoesNotExistException
	 */
	protected void resolveSearchParameters(String componentName,
			AppDepSettings settings) throws IOException,
			CacheIndexNotReadyException, CacheFileDoesNotExistException {
		sdkInfo = settings.getCurrentlyUsedSdk();
		epocRootPath = sdkInfo.getEpocRootDir();
		build = settings.getBuildType().getBuildTypeName();
		
		variant = MainViewDataPopulator.getTargetPlatformIdStringForComponent(settings, componentName);		
		
		showSISfileWarningDialog = includesSISFileTarget(settings);
	}

	/**
	 * Checks if target platform array includes a SIS file target.
	 * @param settings settings object to be used for getting target platform data
	 * @return <code>true</code> if includes a SIS file target, otherwise <code>false</code>.
	 */
	private boolean includesSISFileTarget(AppDepSettings settings) {
		ITargetPlatform[] usedPlatforms = settings.getCurrentlyUsedTargetPlatforms();
		for (int i = 0; i < usedPlatforms.length; i++) {
			ITargetPlatform platform = usedPlatforms[i];
			if(platform.getId().equals(AppDepSettings.TARGET_TYPE_ID_SIS)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Show on error message
	 * @param msg Error message.
	 */
	protected void showErrorMsgDialog(String msg){
		AppDepMessageBox msgBox = new AppDepMessageBox(msg, SWT.ICON_ERROR | SWT.OK);
		msgBox.open();			
	}

	/**
	 * OpensFile in editor
	 * @param location Source file location
	 * @throws URISyntaxException
	 * @throws CoreException
	 */
	private void openFileAndSetFocus(final SourceFileLocation location) throws URISyntaxException, CoreException {

		AppDepConsole.getInstance().println(
				Messages.getString("ShowSourceFileMainViewAction.OpeningFile_Msg") //$NON-NLS-1$
					+ location.getSourceFileLocation() //$NON-NLS-1$)					
					+ "'"); //$NON-NLS-1$)
		
		File file = new File(location.getSourceFileLocation());
		if(file == null || !file.exists()){
			AppDepConsole.getInstance().println(
					Messages.getString("ShowSourceFileMainViewAction.SourceFileDoesNotExist_ErrMsg_Part1") //$NON-NLS-1$
						+ location.getSourceFileLocation() 
						+Messages.getString("ShowSourceFileMainViewAction.SourceFileDoesNotExist_ErrMsg_Part2"), AppDepConsole.MSG_ERROR); //$NON-NLS-1$)
			return;			
		}

		//Create URI to open file
		String uriStr = location.getSourceFileLocation().replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		uriStr = "file://" + uriStr; //$NON-NLS-1$
		final URI srcURI = new URI(uriStr);
		
		//Find default editor for that file
		IEditorRegistry reg = PlatformUI.getWorkbench().getEditorRegistry();
		
		
		IEditorDescriptor editor = reg.getDefaultEditor(file.getName());
		//We open editor by it's ID
		final String editorId = editor.getId();
					
		
		//Runnable to open new file
		final IWorkspaceRunnable runOpen = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// do the actual work in here

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {

					IEditorPart part = IDE.openEditor(page, srcURI, editorId, true);
					
					if(part != null){
						//Set focus to correct line
						 setFocusToLineWhereMethodIs(location);
					}

				} catch (PartInitException e) {
					e.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							"com.nokia.s60tools.metadataeditor", 0, e //$NON-NLS-1$
									.getMessage(), e);

					throw new CoreException(status);
				} 

			}
		};		
		ResourcesPlugin.getWorkspace().run(runOpen, null, IWorkspace.AVOID_UPDATE, null);
		
	}
	
	/**
	 * Setting the focus in opened file there where the method name occurs,
	 * must call after file is opened and only if opening was successful
	 * @param location Source file location
	 * @throws CoreException
	 */
	protected void setFocusToLineWhereMethodIs(
			final SourceFileLocation location) throws CoreException {
		//Runnable to open new file
		final IWorkspaceRunnable runSetFocus = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// do the actual work in here

				try {
					//Setting focus to correct line
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart activeEditor = activePage.getActiveEditor();

					if(activeEditor != null && activeEditor instanceof TextEditor){
						
						// This is actually an instance of 'org.eclipse.cdt.internal.ui.editor.CEditor' 
						// that extends org.eclipse.ui.editors.text.TextEditor
						TextEditor editor = (TextEditor) activeEditor;
						IDocument doc =  getDocument(editor);
						if(doc != null){

							String text = doc.get();
							int methodOffset = location.getMethodOffset();
							if(methodOffset == SourceFileLocation.OFFSET_NOT_FOUND){
								// Removing parameters and getting new offset.
								String methodNameWithOutParams = location.getMethodName();
								methodNameWithOutParams = methodNameWithOutParams.substring(0, methodNameWithOutParams.indexOf("(")); //$NON-NLS-1$
								methodOffset = text.indexOf(methodNameWithOutParams);

								if(methodOffset == SourceFileLocation.OFFSET_NOT_FOUND){
									// Removing possible namespace and getting new offset.
									String separator = "::"; //$NON-NLS-1$
									int separatorLocation = methodNameWithOutParams.lastIndexOf(separator);
									if(separatorLocation > 0){
										methodNameWithOutParams = methodNameWithOutParams.substring(separatorLocation + separator.length());
										methodOffset = text.indexOf(methodNameWithOutParams);
									}
								}
							}
							
							editor.setHighlightRange(methodOffset, 0, true);
						}							
					}					

				} catch (Exception e) {
					e.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							Messages.getString("ShowSourceFileActionGeneralMessage.OpenFileAndSetLineFocus_ErrMsg"), 0, e //$NON-NLS-1$
									.getMessage(), e);
					throw new CoreException(status);
				} 

			}
		};
		
		
		ResourcesPlugin.getWorkspace().run(runSetFocus, null, IWorkspace.AVOID_UPDATE, null);
	}
	
	/**
	 * Returns the document interface for the currently active document 
	 * in the given editor.
	 * @param editor Editor to ask currently active document from. 
	 * @return Document interface if found, otherwise <code>null</code>.
	 */
	private IDocument getDocument(TextEditor editor) {
		
		TextFileDocumentProvider  documentProvider = (TextFileDocumentProvider) editor.getDocumentProvider();
		if(documentProvider != null){
			return  documentProvider.getDocument(editor.getEditorInput());
			}								
		return null;
	}	
}
