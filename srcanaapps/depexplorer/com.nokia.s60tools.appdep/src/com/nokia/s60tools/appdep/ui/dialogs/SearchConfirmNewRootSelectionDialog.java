/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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
 
 
package com.nokia.s60tools.appdep.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Dialog for asking if user wants or not to select selected component as new root component.
 * Also possible to select that this dialog is not shown again.
 * @see org.eclipse.jface.dialogs.Dialog
 */
public class SearchConfirmNewRootSelectionDialog extends Dialog {
	
	private String newRootComponentName = null;
	private boolean dontAskAgain = false;

	/**
	 * Constructor
	 * @param parentShell Parent shell.
	 * @param newRootComponentName Name of the new root component.
	 */
	public SearchConfirmNewRootSelectionDialog(Shell parentShell, String newRootComponentName) {
		super(parentShell);
		this.newRootComponentName = newRootComponentName;
	}
	
	/**
	 * Constructor
	 * @param parentShell Parent shell.
	 */
	@SuppressWarnings("unused")
	private SearchConfirmNewRootSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Constructor
	 * @param parentShell Parent shell provider.
	 */
	@SuppressWarnings("unused")
	private SearchConfirmNewRootSelectionDialog(IShellProvider parentShell) {
		super(parentShell);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Creating just OK button
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL,
                true);     
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText( Messages.getString("SearchConfirmNewRootSelectionDialog.Shell_Txt") );//$NON-NLS-1$ 
    }    
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);		
		
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_BOTH);

		dialogAreaComposite.setLayout(gdl);
		dialogAreaComposite.setLayoutData(gd);


		//Label
		Label label = new Label(dialogAreaComposite,SWT.HORIZONTAL);
		label.setText(
				Messages.getString("SearchConfirmNewRootSelectionDialog.Question_Txt_Part1")//$NON-NLS-1$  
				+newRootComponentName //$NON-NLS-1$ 
				+Messages.getString("SearchConfirmNewRootSelectionDialog.Question_Txt_Part2"));//$NON-NLS-1$  
				
		//empty label just for empty row between items 
		Label nullLabel = new Label(dialogAreaComposite,SWT.HORIZONTAL);
		nullLabel.setText("");//$NON-NLS-1$

		//Button for opening preferences page
		final Button dontAskAgainBtn = new Button(dialogAreaComposite, SWT.CHECK);
		dontAskAgainBtn.setText(Messages.getString("SearchConfirmNewRootSelectionDialog.DontAskAgain_Txt"));//$NON-NLS-1$ 
		dontAskAgainBtn.setToolTipText(Messages.getString("SearchConfirmNewRootSelectionDialog.DontAskAgain_ToolTip_Txt"));//$NON-NLS-1$
		GridData btnData = new GridData();
		dontAskAgainBtn.setLayoutData(btnData);
		
		//listener for add button
		dontAskAgainBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setDontAskAgain(dontAskAgainBtn.getSelection());
			}
		});				
 				
		return dialogAreaComposite;
	}    
		

	/**
	 * Gets don't ask again setting status. 
	 * @return the dontAskAgain
	 */
	public boolean isDontAskAgainChecked() {
		return dontAskAgain;
	}

	/**
	 * Sets don't ask again setting status. 
	 * @param dontAskAgain
	 */
	private void setDontAskAgain(boolean dontAskAgain) {
		this.dontAskAgain = dontAskAgain;
	}
	
	
}
