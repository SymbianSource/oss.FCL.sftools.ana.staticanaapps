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
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.find.IFindStartNodeProvider;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepFindDialog;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Action for starting find dialog for component tree.
 */
public class FindMainViewAction extends AbstractMainViewAction implements IFindStartNodeProvider {
	
	/**
	 * Action's ID.
	 */
	public static final String ACTION_ID = "com.nokia.s60tools.appdep.ui.actions.FindMainViewAction"; //$NON-NLS-1$
			
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public FindMainViewAction(MainView view){
		super(view);
		setText(Messages.getString("FindMainViewAction.FindMainView_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("FindMainViewAction.FindMainView_Action_Tooltip")); //$NON-NLS-1$
		
		setId(ACTION_ID);
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.FIND_ACTION));	
		
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
		
		// Starting dialog find dialog for the component node
		AppDepFindDialog dlg = new AppDepFindDialog(sh, this, view);
		dlg.create();
		dlg.open();					
				
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
				
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.search.ISearchStartNodeProvider#getSearchStartNode()
	 */
	public ComponentNode getSearchStartNode() {
		return view.getRootComponentNode();
	}
}
