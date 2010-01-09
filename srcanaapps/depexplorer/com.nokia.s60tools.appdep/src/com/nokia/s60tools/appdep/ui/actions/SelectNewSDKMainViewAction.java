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

import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.ui.wizards.WizardUtils;

/**
 * Starts SDK selection wizard.
 */
public class SelectNewSDKMainViewAction extends AbstractMainViewAction {
	
	/**
	 * Action's ID
	 */
	public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.SelectNewSDKMainViewAction"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public SelectNewSDKMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("SelectNewSDKMainViewAction.SelectNewSDK_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectNewSDKMainViewAction.SelectNewSDK_Action_Tooltip")); //$NON-NLS-1$
		
		setId(ACTION_ID);
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.SELECT_SDK));	
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		
		// Aborting possible ongoing search
		MainView.abortCurrentlyOngoingSearches();
		
		Shell sh = view.getViewSite().getShell();
		if(WizardUtils.invokeSDKAndTargetPlatformSelectionWizard(sh, true)){
			view.inputUpdated();    			
		}
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}
}
