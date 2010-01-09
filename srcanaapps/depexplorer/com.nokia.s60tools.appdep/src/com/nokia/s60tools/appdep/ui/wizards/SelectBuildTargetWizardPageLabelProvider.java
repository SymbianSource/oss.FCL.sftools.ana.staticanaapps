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
import com.nokia.s60tools.appdep.ui.wizards.BuildTargetEntry.BuildTargetStatusEnum;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Select Build Target wizard page label provider.
 */
class SelectBuildTargetWizardPageLabelProvider extends LabelProvider 
															implements ITableLabelProvider  {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		
		// By default no images are used
		Image img = null;
		BuildTargetEntry entryData = (BuildTargetEntry) element;

		switch (columnIndex) {

		case BuildTargetEntry.TARGET_TYPE_COLUMN_INDEX:
		case BuildTargetEntry.COMPONENT_COUNT_COLUMN_INDEX:
			// fall-through, no image used for these in the moment
			break;

		case BuildTargetEntry.STATUS_COLUMN_INDEX:
			img = getStatusColumneImage(entryData);
			break;
			
		default:
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg") + ": " +columnIndex, AppDepConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
			break;
		}
		
		return img;
	}
	
	/**
	 * Gets status column image for an entry data object.
	 * @param entryData entry data.
	 * @return status column image for an entry data object.
	 */
	private Image getStatusColumneImage(BuildTargetEntry entryData) {
		
		BuildTargetStatusEnum status = entryData.getStatus();
		String imageKey = ImageKeys.TRAFFIC_LIGHT_RED_OBJ;
		
		switch (status) {
		
		case ECacheReady:
			imageKey = ImageKeys.TRAFFIC_LIGHT_GREEN_OBJ;
			break;

		case ECacheNeedsUpdate:
		case ENoCache:
			// fall-through
			imageKey = ImageKeys.TRAFFIC_LIGHT_YELLOW_OBJ;
			break;

		case EEmptyTarget:
		case ENotSupported:
		case ECachesIsBeingIndexed:
		case ECacheIsBeingGenerated:
			// fall-through
			imageKey = ImageKeys.TRAFFIC_LIGHT_RED_OBJ;
			break;

		default:
			break;
		}
		
		return ImageResourceManager.getImage(imageKey);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		
		String label = element.toString();
		
		BuildTargetEntry entryData = (BuildTargetEntry) element;

		switch (columnIndex) {
	
			case BuildTargetEntry.TARGET_TYPE_COLUMN_INDEX:
				label = entryData.getTargetDescription();
				break;
	
			case BuildTargetEntry.COMPONENT_COUNT_COLUMN_INDEX:
				label = entryData.getComponentCount();
				break;
	
			case BuildTargetEntry.STATUS_COLUMN_INDEX:
				label = entryData.getStatusAsString();
				break;
				
			default:
				AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg") + ": " +columnIndex, AppDepConsole.MSG_ERROR);  //$NON-NLS-1$ //$NON-NLS-2$
				break;
		}
		
		return label;
	}	
	
}
