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

import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Label provider for Import Functions tab item.
 */
class MainViewImportFunctionsTabLabelProvider extends LabelProvider 
												implements ITableLabelProvider  {
	
	public MainViewImportFunctionsTabLabelProvider() {
		super();
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		
		// Default image
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		String imageKey = ImageKeys.FUNCTION_OBJ;	
		Image img = ImageResourceManager.getImage(imageKey);
		
		ImportFunctionData ifData = (ImportFunctionData) element;

		switch (columnIndex) {
		
		case ImportFunctionData.ORDINAL_COLUMN_INDEX:
			img = null; // No image is used for the ordinals
			break;

		case ImportFunctionData.NAME_COLUMN_INDEX:			
			// Using different image for virtual methods
			if(ifData.isVirtual()){
		        /*******************************************************************************
		         * This piece of the graphic is taken/modified from a graphic that is made 
		         * available under the terms of the Eclipse Public License v1.0.
		         *
		         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
		         * for detailed information about the original graphic.
		         *  
		         *******************************************************************************/        
				img = ImageResourceManager.getImage(ImageKeys.VIRTUAL_FUNCTION_OBJ);
			}
			break;

		case ImportFunctionData.OFFSET_COLUMN_INDEX:
			img = null; // No image is used for the offset
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
		
		ImportFunctionData ifData = (ImportFunctionData) element;
		
		switch (columnIndex) {
		
		case ImportFunctionData.ORDINAL_COLUMN_INDEX:
			label = ifData.getFunctionOrdinal();
			break;

		case ImportFunctionData.NAME_COLUMN_INDEX:
			label = ifData.getFunctionName();
			break;

		case ImportFunctionData.OFFSET_COLUMN_INDEX:
			label = ifData.getFunctionOffsetAsString();
			break;

		default:
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
			break;
		}
		
		return label;
	}
	
}
