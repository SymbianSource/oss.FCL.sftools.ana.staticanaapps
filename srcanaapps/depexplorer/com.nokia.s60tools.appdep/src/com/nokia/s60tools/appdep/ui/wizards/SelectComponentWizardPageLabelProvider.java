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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Select component wizard page label provider.
 */
class SelectComponentWizardPageLabelProvider extends LabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		
		// Only using image for name columns
		if(columnIndex == ComponentListNode.NAME_COLUMN_INDEX){
			return getNameColumnImage();
		}
		
		// Otherwise no images are used
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String label = element.toString();
		
		ComponentListNode entryData = (ComponentListNode) element;

		switch (columnIndex) {
	
			case ComponentListNode.NAME_COLUMN_INDEX:
				label = entryData.getComponentName();
				break;
	
			case ComponentListNode.TARGET_TYPE_COLUMN_INDEX:
				label = entryData.getBuildTargetTypeAsString();
				break;
	
			case ComponentListNode.DATE_CACHED_COLUMN_INDEX:
				label = entryData.getCachedComponentModificationTimestampAsString();
				break;
	
			default:
				AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg") + ": " + columnIndex, AppDepConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
				break;
		}
		
		return label;
	}
	
	/**
	 * Gets image for name columns.
	 * @return image for name columns.
	 */
	public Image getNameColumnImage() {
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		String imageKey = ImageKeys.BIN_OBJ;								
		return ImageResourceManager.getImage(imageKey);
	}
}
