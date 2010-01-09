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
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.search.SearchConstants;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepSearchDialog;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Action for starting search dialog for certain search type.
 */
public class SearchMainViewAction extends AbstractMainViewAction {
	
	/**
	 * Action's ID
	 */
	public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.SearchMainViewAction"; //$NON-NLS-1$
		
	/**
	 * Search type for the action defines what kind of search scope
	 * is used by default when this action is run.
	 */
	private final SearchConstants.SearchType searchType;

	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 * @param searchType Default search type for this action instance.
	 */
	public SearchMainViewAction(MainView view, SearchConstants.SearchType searchType){
		super(view);
		this.searchType = searchType;
		setText(Messages.getString("SearchMainViewAction.SearchMainView_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SearchMainViewAction.SearchMainView_Action_Tooltip")); //$NON-NLS-1$
		
		setId(ACTION_ID);
		//Adding image descriptor if an icon is created for this action		
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.SEARCH));	
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		
		String currentRootComponent = null;
		currentRootComponent =  AppDepSettings.getActiveSettings().getCurrentlyAnalyzedComponentName();		
		
		Shell sh = AppDepPlugin.getCurrentlyActiveWbWindowShell();

		if(currentRootComponent == null){
			// User has not selected any components for analysis
			String infoMsg = Messages.getString("GeneralMessages.Select_SDK_First_ErrMsg"); //$NON-NLS-1$
			AppDepMessageBox msgBox = new AppDepMessageBox(sh, infoMsg, SWT.OK | SWT.ICON_INFORMATION);
			msgBox.open();
			return;
		}				
		
		// Opens search dialog
		AppDepSearchDialog dlg = new AppDepSearchDialog(sh, searchType, view);
		dlg.create();
		dlg.open();
				
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}
}
