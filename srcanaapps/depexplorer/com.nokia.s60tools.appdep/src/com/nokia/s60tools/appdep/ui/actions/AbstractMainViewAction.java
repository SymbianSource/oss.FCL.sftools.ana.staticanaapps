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

import org.eclipse.jface.action.Action;

import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Common abstract base class for all main view
 * related actions.
 */
public abstract class AbstractMainViewAction extends Action {

	/**
	 * Reference to the view.
	 */
	MainView view;

	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public AbstractMainViewAction(MainView view) {
		super();
		this.view = view;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		// Makes sure that action buttons states are updated 
		// correctly whenever some action is performed.
		view.updateViewActionEnabledStates();
	}
	
}
