/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.job.GenerateCacheJob;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AddSISFilesDialog;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

public class AddSisAndUpdateCacheMainViewAction extends AbstractMainViewAction {

    public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.AddSisAndUpdateCacheMainViewAction";  //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public AddSisAndUpdateCacheMainViewAction(MainView view){
		super(view);
		setText("Add SIS files...");  //$NON-NLS-1$
		setToolTipText("Add SIS files");  //$NON-NLS-1$
		
		setId(ACTION_ID);
		
		ImageDescriptor imageDesc = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD);
		setImageDescriptor(imageDesc);
			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		
		String currentRootComponent = null;
		AppDepSettings activeSettings = AppDepSettings.getActiveSettings();
		currentRootComponent =  activeSettings.getCurrentlyAnalyzedComponentName();	
		
		if(currentRootComponent == null){
			// User has not selected any components for analysis
			String infoMsg = Messages.getString("GeneralMessages.Select_SDK_First_ErrMsg"); //$NON-NLS-1$
			new AppDepMessageBox(infoMsg, SWT.OK | SWT.ICON_INFORMATION).open();
			return;
		}
		
		String[] previouslyAnalysedSISFiles = null;
		Shell sh = AppDepPlugin.getCurrentlyActiveWbWindowShell();
		AddSISFilesDialog entryDialog = new AddSISFilesDialog(sh);
		entryDialog.create();
		
		// If already selected some set of SIS files getting the list...
		if(activeSettings.isInSISFileAnalysisMode()){
			//...and setting the initial list
			previouslyAnalysedSISFiles = activeSettings.getSISFilesForAnalysis();
			entryDialog.setInitialSISFileSet(previouslyAnalysedSISFiles);
		}
		int userSelection = entryDialog.open();
		if(userSelection == Window.OK){
			// Getting selected SIS files
			String[] selectedSISFiles = entryDialog.getSelectedSISFiles();
			if(selectedSISFiles.length > 0){
				activeSettings.setSISFilesForAnalysis(selectedSISFiles );
				
				if( areNewSISFilesProvided(selectedSISFiles, previouslyAnalysedSISFiles)){
					// Entering to SIS file mode...
					activeSettings.setIsInSISFileAnalysisMode(true);
					// ...and setting files to current settings
					addSISTarget(activeSettings);
					//Generate Cache with the modified active settings.
					GenerateCacheJob.triggerCacheGenerationForCurrentlyActiveSettings();
				}
				else{
					
					String infoMsg = Messages.getString("AddSisAndUpdateCacheMainViewAction.Cache_is_up_to_date_msg");  //$NON-NLS-1$
					new AppDepMessageBox(infoMsg, SWT.OK | SWT.ICON_INFORMATION).open();
				}
			}
			else{
				// Disabling SIS file mode
				activeSettings.setIsInSISFileAnalysisMode(false);
				activeSettings.setSISFilesForAnalysis(null);
			}	
						
		}
		
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}
	

	/** 
	 * The method adds SIS target to the given settings.
	 * @param settings
	 */
	private void addSISTarget(AppDepSettings settings) {
		
		try{
		// In SIS mode we need to add an extra target
		if(settings.isInSISFileAnalysisMode()){
			
			ITargetPlatform[] targetPlatforms = settings.getCurrentlyUsedTargetPlatforms();
			
			ArrayList<String> targetPlatformNames = new ArrayList<String>();
			
			for(ITargetPlatform platform: targetPlatforms)
				targetPlatformNames.add(platform.getId());
			
			if(! targetPlatformNames.contains(AppDepSettings.TARGET_TYPE_ID_SIS)){
				settings.addTargetPlatform(AppDepSettings.TARGET_TYPE_ID_SIS);				
			}
		}
		}catch (InvalidCmdLineToolSettingException e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Failed_To_Update_Current_Settings"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
		}
	}
	
	/**
	 * The method compares list of newly provided sis files with the previous list of analysed files.
	 * The method returns true if some files have been added or removed.
	 */
	private boolean areNewSISFilesProvided(String [] newFiles, String [] previouslyAnalysedFiles)
	{
		if(previouslyAnalysedFiles == null)
			return true;		
		else if (newFiles.length != previouslyAnalysedFiles.length)
			return true;
		else
		{
			List<String> analysedFilesList = Arrays.asList(previouslyAnalysedFiles);
			
			for(String file:newFiles)
			{
				if(!analysedFilesList.contains(file))
					return true;
			}
			
			return false;
		}
	}
}
