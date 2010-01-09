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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.job.GenerateCacheJob;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.ui.wizards.WizardUtils;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * Opens component selection wizard (and SDK selection 1st if no SDK selected previously).
 */
public class SelectNewRootComponentMainViewAction extends AbstractMainViewAction {
	
	/**
	 * Action's ID
	 */
	public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.SelectNewRootComponentMainViewAction"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public SelectNewRootComponentMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("SelectNewRootComponentMainViewAction.SelectNewRootComponen_Action_T")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectNewRootComponentMainViewAction.SelectNewRootComponen_Action_Tooltip")); //$NON-NLS-1$
		
		setId(ACTION_ID);
		
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.ROOT_OBJ));	
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		
		// Aborting possible ongoing search
		MainView.abortCurrentlyOngoingSearches();
		
		Shell sh = view.getViewSite().getShell();

		AppDepSettings st = AppDepSettings.getActiveSettings();
		SdkInformation sdkInfo = st.getCurrentlyUsedSdk();
		
		
		if(sdkInfo != null){
			
			boolean cacheNeedsUpdate = st.cacheNeedsUpdate(sdkInfo,
															st.getCurrentlyUsedTargetPlatforms(),
															st.getBuildType()
															);
					
			if(cacheNeedsUpdate){
				String targetPlatformStr = "'"  //$NON-NLS-1$
											+ st.getCurrentlyUsedSdk().getSdkId()
											+ " - " //$NON-NLS-1$
											+ st.getCurrentlyUsedTargetPlatformsAsString()
											+ " " //$NON-NLS-1$
											+ st.getBuildType().getBuildTypeDescription()
											+ "'"; //$NON-NLS-1$
				String msg = Messages.getString("SelectNewRootComponentMainViewAction.Cache_For") + targetPlatformStr + Messages.getString("SelectNewRootComponentMainViewAction.Needs_Update")  //$NON-NLS-1$ //$NON-NLS-2$
				             +  Messages.getString("SelectNewRootComponentMainViewAction.Perform_Cache_Update_Now_Question"); //$NON-NLS-1$
				
				AppDepMessageBox msgBox = new AppDepMessageBox(sh, msg, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				int userResponse = msgBox.open();						
				if(userResponse == SWT.YES){
					// Triggering cache generation job
					GenerateCacheJob.triggerCacheGenerationForCurrentlyActiveSettings();
				}
				else{
					openComponentSelectionWizardPage(sh);					
				}
			}
			else{
				openComponentSelectionWizardPage(sh);					
			}
		}
		//If there is no SDK selection, opening SDK selection page
		else{
			openSDKSelectionWizardPage(sh);
		}
		
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}

	/**
	 * Opens component selection wizard page.
	 * @param sh Parent shell to the opened wizard.
	 */
	private void openComponentSelectionWizardPage(Shell sh) {
		// Otherwise opening component selection page
		if(WizardUtils.invokeSDKAndTargetPlatformSelectionWizard(sh, 
																 false,
																 //Using current settings
																 null,
																 // Duplicate components have been already
																 // reported earlier, no need to repeat.
																 false)){
			// If commenting this out, there is no refresh at normal component selection.
			view.inputUpdated();    			
		}
	}
	
	/**
	 * Opens component selection wizard page.
	 * @param sh Parent shell to the opened wizard.
	 */
	private void openSDKSelectionWizardPage(Shell sh) {
		// Otherwise opening component selection page
		if(WizardUtils.invokeSDKAndTargetPlatformSelectionWizard(sh, 
																 true)){
			// NOTE: If uncommenting this out => there is no refresh at normal component selection
			view.inputUpdated();    			
		}
	}	
}
