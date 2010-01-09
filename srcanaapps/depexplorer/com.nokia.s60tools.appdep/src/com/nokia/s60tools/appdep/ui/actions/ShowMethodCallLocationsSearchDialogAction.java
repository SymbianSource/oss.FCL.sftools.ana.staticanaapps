/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepSearchDialog;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Public implementation for Imported functions tab's for show method call locations action.
 */
public class ShowMethodCallLocationsSearchDialogAction extends AbstractShowMethodCallLocationsAction {

	/**
	 * Class that provides selected function.
	 */
	private AppDepSearchDialog searchDialog;

	/**
	 * Constructor.
	 * @param searchDialog Reference to search dialog.
	 * @param view Reference to main view.
	 */
	public ShowMethodCallLocationsSearchDialogAction(AppDepSearchDialog searchDialog, MainView view) {
		super(view);
		this.searchDialog = searchDialog;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.actions.AbstractMainViewAction#run()
	 */
	public void run() {
		
		String functionName = searchDialog.getSelectedFunction();
		if (functionName == null) {
			AppDepConsole.getInstance().println(
					Messages.getString("SourceFileFeatureCommonMessages.EmptySelection_ErrMsg"), AppDepConsole.MSG_WARNING); //$NON-NLS-1$
			return;
		}
		
		String componentName = searchDialog.getComponentForSelectedFunction();
		if (componentName == null) {
			AppDepConsole.getInstance().println(
					Messages.getString("SourceFileFeatureCommonMessages.EmptySelection_ErrMsg"), AppDepConsole.MSG_WARNING); //$NON-NLS-1$
			return;
		}

		// Search dialog can be closed, so that progress dialog can come in front.
		searchDialog.close();
		
		try {
			//Searching components in job triggered by base class implementation
			runImpl(functionName, componentName);
		} 		
		catch (Exception e) {
			e.printStackTrace();
			showErrorMsgDialog(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg_ToUser")); //$NON-NLS-1$
			AppDepConsole.getInstance().println(Messages.getString("SourceFileFeatureCommonMessages.UnableToShowLocations_ErrMsg") +e, AppDepConsole.MSG_ERROR); //$NON-NLS-1$
		}
	}
	
}
