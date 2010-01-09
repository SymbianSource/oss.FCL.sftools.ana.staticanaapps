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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard dialog for extension specific wizards.
 */
public class AppDepWizardDialog extends WizardDialog {
	
	/**
	 * Used AppDep Wizard.
	 */
	private ISelectSDKWizard wizard = null;
	
    /**
     * Creates a new wizard dialog for the given wizard.
     * Just calls the super constructor. 
     * @param parentShell the parent shell
     * @param newWizard The AppDep wizard this dialog is working on.
     */
	public AppDepWizardDialog(Shell parentShell, ISelectSDKWizard newWizard){
		super(parentShell, newWizard);
		this.wizard = newWizard;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardDialog#finishPressed()
     */
    protected void finishPressed() {
    	int exitStatus = wizard.getExitStatus();
    	super.finishPressed();
    	setReturnCode(exitStatus);
    }
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed() {
    	super.cancelPressed();
    	setReturnCode(ISelectSDKWizard.CANCEL);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardDialog#backPressed()
     */
    @Override
    protected void backPressed() {
    	// Getting current wizard page
    	IWizardPage currentPage = getCurrentPage();
		if(currentPage instanceof SelectComponentWizardPage){
			SelectComponentWizardPage scwp = (SelectComponentWizardPage)currentPage;
			scwp.setShowDuplicateComponentInfo(true);
    	}
    	super.backPressed();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardDialog#nextPressed()
     */
    @Override
    protected void nextPressed() {
    	// Getting current wizard page
    	IWizardPage currentPage = getCurrentPage();
    	
		// Refreshing data on next page
		IRefreshable nextPage = (IRefreshable) wizard.getNextPage(currentPage);
		nextPage.refresh();
		
    	super.nextPressed();    	
    }
	
}
