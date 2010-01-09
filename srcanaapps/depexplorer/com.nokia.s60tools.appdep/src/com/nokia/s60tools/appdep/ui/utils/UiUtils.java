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


package com.nokia.s60tools.appdep.ui.utils;

import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.ui.dialogs.SearchConfirmNewRootSelectionDialog;
import com.nokia.s60tools.appdep.ui.preferences.DEPreferences;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Contains static service methods that are needed by multiple
 * UI related classes in order to prevent duplication of similar 
 * functionality in multiple places.
 */
public class UiUtils {

	/**
	 * Opens ask dialog if needed and asks confirmation about if new root is to be set or not.
	 * If in preferences there is selection, "dont ask again" dialog want be opened but <code>true</code> is returned.
	 * @param newRootComponentName
	 * @return <code>true</code> if new root is to be set, <code>false</code> otherwise.
	 */
	private static boolean handleSetAsNewRootComponentConfirmation(Shell sh, String newRootComponentName) {
		
		//checking if preferences has already value dontAskAgain
		if(DEPreferences.getDontAskSetAsNewRootFromSearch()){
			return true;
		}
		else {							
			SearchConfirmNewRootSelectionDialog dialog = new SearchConfirmNewRootSelectionDialog(sh, newRootComponentName);
			int doOpen = dialog.open();
			boolean setAsNewRoot = (doOpen == SearchConfirmNewRootSelectionDialog.OK) ? true : false;
			
			//if canceled, preferences wont be changed.
			if(setAsNewRoot){
				boolean dontAskAgain = dialog.isDontAskAgainChecked();
				//setting to preferences
				DEPreferences.setDontAskAtainAsNewRootFromSearch(dontAskAgain);
			}
	
			return setAsNewRoot;
		}
	}

	/**
	 * Sets given component as new root component (and optionally triggers
	 * confirmation query). 
	 * @param view Reference to main view.
	 * @param selectedComponentName Component to be set as new root component.
	 * @param targetPlatform Target platform for the component, or <code>null</code> if not known.
	 */
	public static void setComponentAsNewRootInMainView(MainView view, String selectedComponentName, ITargetPlatform targetPlatform) {
		Shell sh = view.getViewSite().getShell();
		if( handleSetAsNewRootComponentConfirmation(sh, selectedComponentName)){		
			AppDepSettings st = AppDepSettings.getActiveSettings();
			st.setCurrentlyAnalyzedComponentName(selectedComponentName);
			st.setCurrentlyAnalyzedComponentTargetPlatform(targetPlatform);
			view.inputUpdated();   		
		}
	}

	
	
}
