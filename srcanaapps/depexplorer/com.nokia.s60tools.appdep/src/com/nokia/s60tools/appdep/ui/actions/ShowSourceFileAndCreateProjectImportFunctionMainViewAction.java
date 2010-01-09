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

import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * public implementation for Imported functions view show source action. 
 * Real implementation is in abstract class {@link AbstractShowSourceFileAction}
 */
public class ShowSourceFileAndCreateProjectImportFunctionMainViewAction extends
	ShowSourceFileAndCreateProjectMainViewAction {

	/**
	 * Constructor.
	 * @param view Reference to main view.
	 */
	public ShowSourceFileAndCreateProjectImportFunctionMainViewAction(MainView view) {
		super(view);
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.actions.AbstractMainViewAction#run()
	 */
	public void run() {

		//All we do here is found out method name and ordinal that user is selected
		String methodName;
		String ordinal;

		Object functionObj = view.getSelectedImportFunction();
		if (functionObj == null) {
			// We might get null-selections when
			// tree is expanded/collapsed.
			AppDepConsole
					.getInstance()
					.println(Messages.getString("ShowSourceFileAndCreateProjectMainViewAction.EmptySelection_ErrMsg"), //$NON-NLS-1$
							AppDepConsole.MSG_WARNING);

			return;
		}
		ImportFunctionData efData = (ImportFunctionData) functionObj;
		methodName = efData.getFunctionName();
		ordinal = efData.getFunctionOrdinal();
		
		super.runImpl(methodName, ordinal);

	}

}
