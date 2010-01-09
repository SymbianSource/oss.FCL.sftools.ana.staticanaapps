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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Dialog to add component in prefix search order preferences page
 */
public class AddComponentPrefixSearchOrderDialog extends Dialog {
		
	/**
	 * List box dialog text control for showing the content.
	 */
	private Text listBoxContentTextControl;
	/**
	 * Data for text control (available also after text control is disposed)
	 */
	private String text;	

	/**
	 * Constructor.
	 * @param parentShell Parent shell.
	 */
	public AddComponentPrefixSearchOrderDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Constructor.
	 * @param parentShell Parent shell provider.
	 */
	public AddComponentPrefixSearchOrderDialog(IShellProvider parentShell) {
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
		Button ok = getButton(IDialogConstants.OK_ID);
		ok.setEnabled(false);
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.getString("AddComponentPrefixSearchOrderDialog.AddComponent_Txt")); //$NON-NLS-1$
    }    
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);		
	
		//
		//Adding custom controls
		//
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_BOTH);

		dialogAreaComposite.setLayout(gdl);
		dialogAreaComposite.setLayoutData(gd);
	
		GridLayout gdl2 = new GridLayout(cols, false);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
	
		dialogAreaComposite.setLayoutData(gd2);
		listBoxContentTextControl = new Text(dialogAreaComposite, SWT.LEFT | SWT.BORDER | SWT.BORDER);	
		dialogAreaComposite.setLayout(gdl2);
		listBoxContentTextControl.setLayoutData(gd2);
		listBoxContentTextControl.setEnabled(true);
		
		Label label = new Label(dialogAreaComposite,SWT.HORIZONTAL);
		label.setText(Messages.getString("AddComponentPrefixSearchOrderDialog.Help_Txt")); //$NON-NLS-1$
				
		listBoxContentTextControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				text = listBoxContentTextControl.getText();
				if(text != null  && text.trim().length() > 1){
					Button ok = getButton(IDialogConstants.OK_ID);
					ok.setEnabled(true);	
				}else{
					Button ok = getButton(IDialogConstants.OK_ID);
					ok.setEnabled(false);
					
				}
			}
		});	 				
		return dialogAreaComposite;
	}    
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open(){
		text = ""; //$NON-NLS-1$
		return super.open();
	}
	
	/**
	 * Gets prefix text entered by user.
	 * @return prefix text entered by user.
	 */
	public String getText(){
		return text;
	}
	
}
