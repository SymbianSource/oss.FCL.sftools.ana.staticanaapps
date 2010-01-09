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
 
 
package com.nokia.s60tools.appdep.ui.wizards;

import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.CacheDataLoadProcessManager;
import com.nokia.s60tools.appdep.core.job.GenerateCacheJob;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsUIConstants;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This class contains wizard related static
 * utility methods.
 */
public class WizardUtils {
	
	/**
	 * Only one instance of the wizard dialog can be active.
	 * Using this static member to make it sure.
	 */
	static WizardDialog wizDialog = null;
	
	/**
	 * Invokes wizard for selection of currently used 
	 * SDK and target platform. Selection of the the wizard page to be
	 * opened is made based on the settings that are currently in effect.
	 * By default shows all the duplicate components are reported if encountered.
	 * @param sh Parent shell for the wizard.
	 * @return Returns <code>true</code> if current settings have changed
	 *         and <code>false</code> if the settings are not changed.
	 *         If settings are changed, it requires UI component refresh. 
	 */
	public static boolean invokeSDKAndTargetPlatformSelectionWizard(Shell sh) {
		return invokeSDKAndTargetPlatformSelectionWizard(sh, false, null, true);
	}

	/**
	 * Invokes wizard for selection of currently used 
	 * SDK and target platform. 
	 * By default shows all the duplicate components are reported if encountered.
	 * @param sh Parent shell for the wizard.
	 * @param openSDKSelectionPage Set to <code>true</code> if the one wants forcibly
	 *                             open the SDK selection page instead of starting the default page.
	 * @return Returns <code>true</code> if current settings have changed
	 *         and <code>false</code> if the settings are not changed.
	 *         If settings are changed, it requires UI component refresh. 
	 */
	public static boolean invokeSDKAndTargetPlatformSelectionWizard(Shell sh,  
			                                                        boolean openSDKSelectionPage) {
		return invokeSDKAndTargetPlatformSelectionWizard(sh, openSDKSelectionPage, null, true);
	}

	/**
	 * Invokes wizard for selection of currently used 
	 * SDK and target platform. 
	 * @param sh Parent shell for the wizard.
	 * @param selectNewSDK Set to <code>true</code> if the one wants forcibly
	 *                             open the SDK selection page for setting new SDK selection.
	 * @param newSettings If this value is set to <code>null</code> the current settings are cloned as
	 *                    base for the new settings, the value is not <code>null</code> the given settings
	 *                    are added into active settings and used when wizard is opened.
	 * @param showDuplicateComponentInfo if set <code>true</code> all duplicate components are reported.
	 * @return Returns <code>true</code> if current settings have changed
	 *         and <code>false</code> if the settings are not changed.
	 *         If settings are changed, it requires UI component refresh. 
	 */
	public static boolean invokeSDKAndTargetPlatformSelectionWizard(Shell sh,  
			                                                        boolean selectNewSDK, 
			                                                        AppDepSettings newSettings,
			                                                        boolean showDuplicateComponentInfo) {

		// Closing possibly existing wizard dialog
		// forcibly before invoking another one.
		if(wizDialog != null){
			wizDialog.close();
		}
		
		Set<String> previousTargetPlatformsSet = AppDepSettings.getActiveSettings().getBuildDirsAsSet();
		AppDepSettings currentSettings = null;
		
		if(newSettings != null){
			// Given settings act as a base for new user selections.
			AppDepSettings.setAsNewActiveInstance(newSettings);
			currentSettings = newSettings;		
		}
		else{
			// The clone of current settings acts as a base for new user selections.
			currentSettings = AppDepSettings.cloneAndAddAsNewActiveInstance();
		}
		if(selectNewSDK){
			//Set explicitly mode to not be SIS mode because user wants to select new SDK.
			AppDepSettings.getActiveSettings().setIsInSISFileAnalysisMode(false);
		}

		// Creating wizard
		SelectSDKWizard wiz = new SelectSDKWizard(currentSettings,  
                selectNewSDK, 
                showDuplicateComponentInfo);		
		wizDialog = new AppDepWizardDialog(sh, wiz);
		
		wizDialog.create();		
		wizDialog.getShell().setSize(S60ToolsUIConstants.WIZARD_DEFAULT_WIDTH,
				              S60ToolsUIConstants.WIZARD_DEFAULT_HEIGHT);		
		wizDialog.addPageChangedListener(wiz);
		
		// Showing wizard to user
		int userResponse = wizDialog.open();
		
		// Making sure that static instance is null for next users
		wizDialog = null;

		return handleUserResponse(previousTargetPlatformsSet, userResponse);
	}

	/**
	 * Checks the user response and triggers actions based on the response.
	 * @param previousTargetPlatformsSet Previously selected target platform set.
	 * @param userResponse User response to check.
	 * @return Returns <code>true</code> if current settings have changed
	 *         and <code>false</code> if the settings are not changed.
	 *         If settings are changed, it requires UI component refresh. 
	 */
	private static boolean handleUserResponse(
			Set<String> previousTargetPlatformsSet, int userResponse) {
		// Handling user response
		if(userResponse == ISelectSDKWizard.FINISH_COMPONENT_SELECTED){
			// Wizard was finished normally and user accepted the new settings.
			// There are no more reasons to preserve old settings.
			AppDepSettings.removePreviousInstances();
			// Comparing previous settings against latest settings
			AppDepSettings latestSettings = AppDepSettings.getActiveSettings();
			Set<String> currentTargetPlatformsSet = latestSettings.getBuildDirsAsSet();
			
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "User selection after wizard closes:"); //$NON-NLS-1$
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "\t currentTargetPlatformsSet: " + currentTargetPlatformsSet.toString()); //$NON-NLS-1$
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "\t currentlyAnalyzedComponentName: " + latestSettings.getCurrentlyAnalyzedComponentName()); //$NON-NLS-1$
			
			boolean isCacheUpdated = latestSettings.isCacheUpdated();
			if(
				!currentTargetPlatformsSet.equals(previousTargetPlatformsSet)
				||
				isCacheUpdated
					){
			     // Reloading cache and refreshing existing view with new information
				try {
					CacheDataLoadProcessManager.runCacheLoadProcess(latestSettings, isCacheUpdated);
				} catch (Exception e) {
					e.printStackTrace();
					// Cache reload failed
					String targetPlatformStr = latestSettings.getCurrentlyUsedTargetsAsString();
					String msg = Messages.getString("GeneralMessages.CacheDataReload_Failed_For_Msg") //$NON-NLS-1$
					      + " '" + targetPlatformStr  + "'. " //$NON-NLS-1$ //$NON-NLS-2$
						  + Messages.getString("GenerateCacheJob.See_Console_Log_Msg"); //$NON-NLS-1$
					
					AppDepMessageBox msgBox = new AppDepMessageBox(msg, SWT.ICON_ERROR | SWT.OK);
					msgBox.open();			
				}
			}
			return true;
		}
		else if(userResponse == ISelectSDKWizard.FINISH_CACHE_CREATION){			
			triggerCacheGenerationJobAndRestoreSettings();
			return false;
		}
		else if(userResponse == ISelectSDKWizard.CANCEL){
			restorePreviousActiveSettings();
			return false;
		}
		else{
			AppDepConsole.getInstance().println(Messages.getString("WizardUtils.Unexpected_Wizard_Exit_Status"),  //$NON-NLS-1$
											IConsolePrintUtility.MSG_ERROR);
			restorePreviousActiveSettings();
			return false;
		}
	}	
	
	/**
	 * Triggers cache generation job and restores previous settings.
	 */
	private static void triggerCacheGenerationJobAndRestoreSettings() {
		// Cache generation uses the settings set by the user.			
		// Getting a local copy of currently active settings...
		AppDepSettings localSettings 
				= (AppDepSettings) AppDepSettings.getActiveSettings().clone();
		//... and passing it to cache generate job object
		Job jb = new GenerateCacheJob(Messages.getString("GeneralMessages.CacheGeneration_Job_Title_Text"), localSettings); //$NON-NLS-1$
		
		// We do not want cache generation to block other 
		// jobs and therefore using the lowest priority
		jb.setPriority(Job.DECORATE);
		jb.schedule();
		
		// It is now safe to restore original settings, because cache generation uses
		// its own copy of settings.
		restorePreviousActiveSettings();
	}

	/**
	 * Restores the previously active global application settings. 
	 */
	private static void restorePreviousActiveSettings() {
		if(AppDepSettings.hasPreviousActiveInstance()){
			AppDepSettings.restorePreviousActiveInstance();
		}
	}

}
