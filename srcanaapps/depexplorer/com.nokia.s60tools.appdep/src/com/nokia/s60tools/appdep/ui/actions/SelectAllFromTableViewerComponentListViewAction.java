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
import org.eclipse.jface.viewers.TableViewer;

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.listview.ListView;

/**
 * Selects all items from component list view.
 */
public class SelectAllFromTableViewerComponentListViewAction extends Action{
	
	/**
	 * Reference to component list view.
	 */
	private final ListView view;

	/**
	 * Constructor.
	 * @param viewer Reference to component list view.
	 */
	public SelectAllFromTableViewerComponentListViewAction(ListView view){		
		this.view = view;
		setText(Messages.getString("SelectAllFromTableViewerComponentListViewAction.SelectAll_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectAllFromTableViewerComponentListViewAction.SelectAll_Action_Tooltip"));		 //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		TableViewer viewer = view.getComponentListViewer();
		viewer.getTable().selectAll();	
		// We must update context menu accordingly
		view.updateViewActionEnabledStates();
	}
}
