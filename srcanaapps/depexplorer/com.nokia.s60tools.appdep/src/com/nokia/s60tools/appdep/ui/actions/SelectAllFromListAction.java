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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.List;

import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Selects all items from a list.
 */
public class SelectAllFromListAction extends Action{
	
	/**
	 * Reference to list.
	 */
	private final List list;

	/**
	 * Constructor.
	 * @param viewer Reference to a list.
	 */
	public SelectAllFromListAction(List list){
		this.list = list;		
		setText(Messages.getString("SelectAllFromListAction.SelectAll_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectAllFromListAction.SelectAll_Action_Tooltip")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		list.selectAll();	
		super.run();
	}
}
