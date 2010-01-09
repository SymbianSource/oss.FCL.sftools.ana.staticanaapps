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
 
 
package com.nokia.s60tools.appdep.ui.views.listview;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * This class listens to the selection events happening in
 * list view.
 * @see org.eclipse.jface.viewers.ISelectionChangedListener
 */
public class ListViewSelectionChangedListener implements ISelectionChangedListener {

	/**
	 * Reference to list view.
	 */
	private final ListView view;
	
	/**
	 * Default constructor.
	 * @param view Reference to list view
	 */
	public ListViewSelectionChangedListener(ListView view){
		this.view = view;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			view.updateViewActionEnabledStates();
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
