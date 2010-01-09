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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Label provider for Import Functions tab item.
 */
class ListViewLabelProvider extends LabelProvider 
												implements ITableLabelProvider  {
	
	/**
	 * Constructor
	 */
	public ListViewLabelProvider() {
		super();
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		
		// Currently no images is used
		Image img = null;

		switch (columnIndex) {

		case ComponentPropertiesData.NAME_COLUMN_INDEX:
		case ComponentPropertiesData.BIN_FORMAT_COLUMN_INDEX:
		case ComponentPropertiesData.UID1_COLUMN_INDEX:
		case ComponentPropertiesData.UID2_COLUMN_INDEX:
		case ComponentPropertiesData.UID3_COLUMN_INDEX:
		case ComponentPropertiesData.SECURE_ID_COLUMN_INDEX:
		case ComponentPropertiesData.VENDOR_ID_COLUMN_INDEX:
		case ComponentPropertiesData.MIN_HEAP_COLUMN_INDEX:
		case ComponentPropertiesData.MAX_HEAP_COLUMN_INDEX:
		case ComponentPropertiesData.STACK_SIZE_COLUMN_INDEX:
			break;
			
		default:
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
			break;
		}
		
		return img;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		
		String label = element.toString();
		
		ComponentPropertiesData propData = (ComponentPropertiesData) element;

		switch (columnIndex) {
	
			case ComponentPropertiesData.NAME_COLUMN_INDEX:
				label = propData.getFilename();
				break;
	
			case ComponentPropertiesData.BIN_FORMAT_COLUMN_INDEX:
				label = propData.getBinaryFormat();
				break;
	
			case ComponentPropertiesData.UID1_COLUMN_INDEX:
				label = propData.getUid1();
				break;
	
			case ComponentPropertiesData.UID2_COLUMN_INDEX:
				label = propData.getUid2();
				break;
	
			case ComponentPropertiesData.UID3_COLUMN_INDEX:
				label = propData.getUid3();
				break;
	
			case ComponentPropertiesData.SECURE_ID_COLUMN_INDEX:
				label = propData.getSecureId();
				break;
	
			case ComponentPropertiesData.VENDOR_ID_COLUMN_INDEX:
				label = propData.getVendorId();
				break;
	
			case ComponentPropertiesData.MIN_HEAP_COLUMN_INDEX:
				label = propData.getMinHeapSize();
				break;
	
			case ComponentPropertiesData.MAX_HEAP_COLUMN_INDEX:
				label = propData.getMaxHeapSize();
				break;
	
			case ComponentPropertiesData.STACK_SIZE_COLUMN_INDEX:
				label = propData.getStackSize();
				break;
			
			default:
				AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
				break;
		}
		
		return label;
	}
	
}
