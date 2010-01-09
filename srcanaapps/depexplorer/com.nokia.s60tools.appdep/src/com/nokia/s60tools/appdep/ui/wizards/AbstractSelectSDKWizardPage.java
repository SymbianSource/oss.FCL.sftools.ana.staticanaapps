/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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

import org.eclipse.swt.widgets.Composite;

import com.nokia.s60tools.ui.wizards.S60ToolsWizardPage;

/**
 * Abstract base class for SDK selection wizard page
 * making possible to make different concreted implementations.
 */
public abstract class AbstractSelectSDKWizardPage extends S60ToolsWizardPage{

	/**
	 * Constructor
	 * @param pageName wizard page name
	 */
	public AbstractSelectSDKWizardPage(String pageName) {
		super(pageName);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.wizards.S60ToolsWizardPage#recalculateButtonStates()
	 */
	public abstract void recalculateButtonStates();

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.wizards.S60ToolsWizardPage#setInitialFocus()
	 */
	public abstract void setInitialFocus();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public abstract void createControl(Composite parent);

}
