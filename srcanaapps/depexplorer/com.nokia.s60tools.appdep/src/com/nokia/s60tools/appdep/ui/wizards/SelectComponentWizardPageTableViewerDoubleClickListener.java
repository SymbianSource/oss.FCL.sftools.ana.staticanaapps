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
 
 
package com.nokia.s60tools.appdep.ui.wizards;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;

/**
 * Listens for double-click event, and finishes dialog
 * when a valid component is double-clicked from the
 * component selection wizard.
 */
class SelectComponentWizardPageTableViewerDoubleClickListener implements
		IDoubleClickListener {
	
	/**
	 * Wizard page instance to listen for.
	 */
	private final SelectComponentWizardPage page;
	
	/**
	 * Constructor.
	 * @param page Wizard page instance to listen for.
	 */
	public SelectComponentWizardPageTableViewerDoubleClickListener(SelectComponentWizardPage page){
		this.page = page;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		//Asking page to update it button states
		this.page.recalculateButtonStates();	
		// Checking if we can perform finish
		IWizard wiz = page.getWizard();
		if(wiz.canFinish()){
			IWizardContainer container = wiz.getContainer();
			if(container != null){
				if(container instanceof AppDepWizardDialog){
					AppDepWizardDialog dlg = (AppDepWizardDialog) container;
					dlg.finishPressed();
				}
			}
		}
		
	}

}
