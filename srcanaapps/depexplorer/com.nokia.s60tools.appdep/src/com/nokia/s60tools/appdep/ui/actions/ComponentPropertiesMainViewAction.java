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

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.ui.dialogs.S60ToolsListBoxDialog;

/**
 * Opens up component properties dialog.
 */
public class ComponentPropertiesMainViewAction extends AbstractMainViewAction {
		
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ComponentPropertiesMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("ComponentPropertiesMainViewAction.ComponentProperties_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ComponentPropertiesMainViewAction.ComponentProperties_Action_Tooltip")); //$NON-NLS-1$
		
		setId("com.nokia.s60tools.appdep.ui.actions.ComponentPropertiesMainViewAction"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {

		ComponentPropertiesData propData = view.getSelectedComponentPropertiesData();
		// Parent shell
		Shell sh = view.getViewSite().getShell();

		if(propData != null){
			// Launching properties dialog
			String dialogTitle = Messages.getString("ComponentPropertiesMainViewAction.ComponentProperties_Dialog_Title"); //$NON-NLS-1$
			String listBoxContent = propData.toString();
			
			S60ToolsListBoxDialog dlg = new S60ToolsListBoxDialog(
											  sh, 
											  dialogTitle, 
                                              listBoxContent, 
                                              false, // not resizable
                                              true, // no vertical scroll bar
                                              true,  // has horizontal scroll bar
                                              300,    // default width
                                              250,    // default height
                                              false, // No cancel button
                                              null,  // no extra label
                                              AppDepHelpContextIDs.APPDEP_PROPERTIES // context-sensitive help ID
																	);
			dlg.create();
			dlg.open();			
		}
		else{
			String userMessage = Messages.getString("ComponentPropertiesMainViewAction.Component_Does_Not_Exist_In_Cache_Msg"); //$NON-NLS-1$
			AppDepMessageBox msgBox = new AppDepMessageBox(sh, userMessage, SWT.OK | SWT.ICON_INFORMATION);
			msgBox.open();
		}
		
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
	}
}
