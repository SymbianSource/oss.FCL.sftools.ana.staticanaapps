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
 
 
/**
 * Listens to selection changes and updated buttons
 * states after the selection has been changed.
 */
package com.nokia.s60tools.appdep.ui.wizards;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Select component wizard page tree viewer listener.
 */
class SelectComponentWizardPageTableViewerSelectionChangedListener implements ISelectionChangedListener{
	
	/**
	 * Wizard page instance to listen for.
	 */
	private final SelectComponentWizardPage page;
	
	/**
	 * Constructor
	 * @param page Wizard page instance to listen for.
	 */
	public SelectComponentWizardPageTableViewerSelectionChangedListener(SelectComponentWizardPage page){
		this.page = page;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {		
		try {
			//Asking page to update it button states
			this.page.recalculateButtonStates(); 
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}