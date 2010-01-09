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
 
 
package com.nokia.s60tools.appdep.ui.views.main;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.PropertyData;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Label provider class for Component Properties
 * tab item.
 */
class MainViewComponentPropertiesTabLabelProvider extends LabelProvider 
												implements ITableLabelProvider  {
	
	/**
	 * Constructor.
	 */
	public MainViewComponentPropertiesTabLabelProvider() {
		super();
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		// Property view does not use images
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		
		String label = element.toString();
		
		PropertyData propData = (PropertyData) element;
		
		switch (columnIndex) {
		
		case ComponentPropertiesData.PROPERTY_COLUMN_INDEX:
			label = propData.getPropertyDescription();
			break;

		case ComponentPropertiesData.VALUE_COLUMN_INDEX:
			label = propData.getPropertyValue();
			break;

		default:
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
			break;
		}
		
		return label;
	}
	
}
