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

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.job.GenerateCacheJob;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Start cache update if needed from main view's toolbar.
 */
public class CacheUpdateMainViewAction extends AbstractMainViewAction {
	
	public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.CacheUpdateMainViewAction"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public CacheUpdateMainViewAction(MainView view){
		super(view);
		setText(Messages.getString("CacheUpdateMainViewAction.CacheUpdateMainView_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("CacheUpdateMainViewAction.CacheUpdateMainView_Action_Tooltip")); //$NON-NLS-1$
		
		setId(ACTION_ID);
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.CACHE_UPDATE_ACTION));	
		
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
		
		if(activeSettings.currentlySelectedCachesNeedsUpdate()){
			// Aborting possible ongoing search
			MainView.abortCurrentlyOngoingSearches();
			
			// Triggering cache generation job
			GenerateCacheJob.triggerCacheGenerationForCurrentlyActiveSettings();			
		}
		else{
			// Cache is up-to-date
			String infoMsg = null;
			if(activeSettings.getCurrentlyUsedTargetPlatforms().length > 1){
				infoMsg = Messages.getString("CacheUpdateMainViewAction.Caches_Are_Up_To_Date_InfoMsg"); //$NON-NLS-1$				
			}
			else{
				infoMsg = Messages.getString("CacheUpdateMainViewAction.Cache_Is_Up_To_Date_InfoMsg"); //$NON-NLS-1$								
			}
			new AppDepMessageBox(infoMsg, SWT.OK | SWT.ICON_INFORMATION).open();
			return;			
		}
		
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}
		
}
