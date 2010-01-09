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

import org.eclipse.jface.viewers.TableViewer;

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Selects all items from a table viewer component.
 */
public class SelectAllFromTableViewerMainViewAction extends AbstractMainViewAction{
	
	/**
	 * Reference to table viewer component.
	 */
	private final TableViewer viewer;

	/**
	 * Constructor.
	 * @param viewer Reference to table viewer component.
	 */
	public SelectAllFromTableViewerMainViewAction(MainView view, TableViewer viewer){
		super(view);
		this.viewer = viewer;		
		setText(Messages.getString("SelectAllFromTableViewerMainViewAction.SelectAll_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectAllFromTableViewerMainViewAction.SelectAll_Action_Tooltip"));		 //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		viewer.getTable().selectAll();	
		// We must update context menu accordingly
		view.updateImportFunctionsContextMenuStates(true);
		super.run();
	}
}
