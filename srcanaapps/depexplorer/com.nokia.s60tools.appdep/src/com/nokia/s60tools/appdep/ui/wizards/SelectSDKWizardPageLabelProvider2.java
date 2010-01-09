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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;

/**
 * Select SDK wizard page label provider.
 */
class SelectSDKWizardPageLabelProvider2 extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		String label = obj.toString();
		return label;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object obj) {

		// Default image
		String imageKey = ImageKeys.FOLDER_OBJ;	
		
		if(obj instanceof SdkTreeViewNode){
			SdkTreeViewNode sdkNode = (SdkTreeViewNode) obj;
			if(! sdkNode.getSdkInfo().epocRootExists()){
		        /*******************************************************************************
		         * This piece of the graphic is taken/modified from a graphic that is made 
		         * available under the terms of the Eclipse Public License v1.0.
		         *
		         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
		         * for detailed information about the original graphic.
		         *  
		         *******************************************************************************/        
				imageKey = ImageKeys.FOLDER_OBJ_ERR;
			}
		}
		
		return ImageResourceManager.getImage(imageKey);
	}
}
