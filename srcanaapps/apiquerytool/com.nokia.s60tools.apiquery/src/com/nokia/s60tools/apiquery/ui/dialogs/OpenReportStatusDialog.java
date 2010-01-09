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
 
package com.nokia.s60tools.apiquery.ui.dialogs;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.apiquery.shared.resources.Messages;

/**
 * Simple dialog asking if user want to open created file or not.
 *
 */
public class OpenReportStatusDialog extends StatusDialog {

	private final String fileName;

	/**
	 * Open a Dialog
	 * @param parent
	 * @param fileName
	 */
	public OpenReportStatusDialog(Shell parent, String fileName) {
		super(parent);
		this.fileName = fileName;
		init();

	}

	/**
	 * Set title, default orientation and help not available
	 */
	private void init() {
		setTitle(Messages.getString("OpenReportStatusDialog.OpenReport_Msg"));		 //$NON-NLS-1$
		setHelpAvailable(false);
		setDefaultOrientation(SWT.CENTER);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite composite = (Composite) super.createDialogArea(parent);
	
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_BOTH);

		composite.setLayout(gdl);
		composite.setLayoutData(gd);
		
		Label label = new Label(composite,SWT.HORIZONTAL);
		label.setText(Messages.getString("OpenReportStatusDialog.DoYouWantOpenReport_Msg") +fileName +"?");				 //$NON-NLS-1$ //$NON-NLS-2$
			
		return composite;
	} 	
	
	

}
