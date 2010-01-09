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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Main view's component tree's label provider.
 */
class MainViewComponentTreeLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		
		String label = obj.toString();
		
		if (obj instanceof ComponentParentNode){
			ComponentParentNode pn = (ComponentParentNode) obj;
			if(! pn.isRootComponent()){
				if (pn.isMissing()){
					label = label
						+ " (" + Messages.getString("MainViewComponentTreeLabelProvider.ComponentDoesNotExist_StatusMsg") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				else if(! pn.isDirectChildrensResolved()){
					if(MainView.isDependencySearchOngoing()){
						label = label
							+ " (" + Messages.getString("MainViewComponentTreeLabelProvider.Searching_StatusMsg") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					else{
						label = label
							+ " (" + Messages.getString("MainViewComponentTreeLabelProvider.Search_Aborted_StatusMsg") + ")";						 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}					
				}
				else if( pn.wasGenericComponent()){
					if(pn.getGenericComponentBindType() == ComponentParentNode.CompBindType.AUTO_BIND){
						label = label
							+ " (" + Messages.getString("MainViewComponentTreeLabelProvider.ComponentBindType_Auto") +" " +pn.getOriginalName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
					else if(pn.getGenericComponentBindType() == ComponentParentNode.CompBindType.USER_BIND){
						label = label
							+ " (" + Messages.getString("MainViewComponentTreeLabelProvider.ComponentBindType_User") +" " +pn.getOriginalName() + ")";	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}					
				}				
			}
		}
		return label;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object obj) {
		
		// Default image		
        /*******************************************************************************
         * This method uses graphics that are taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		String imageKey = ImageKeys.BIN_OBJ_LINK;
		
		if (obj instanceof ComponentParentNode){
			ComponentParentNode pn = (ComponentParentNode) obj;
			if(pn.isRootComponent()){
				if (pn.isMissing()){					
					imageKey = ImageKeys.ROOT_OBJ_ERROR;
				}
				else{
					imageKey = ImageKeys.ROOT_OBJ;					
				}
			}
			else if (pn.isMissing()){
				imageKey = ImageKeys.BIN_OBJ_ERROR;				
			}
			else if (pn.wasGenericComponent()){
				imageKey = ImageKeys.BIN_OBJ_BIND;				
			}			
			else if(! pn.isDirectChildrensResolved()){
				if(MainView.isDependencySearchOngoing()){
					imageKey = ImageKeys.BIN_OBJ_WARNING;									
				}
				else{
					imageKey = ImageKeys.BIN_OBJ_ERROR;				
				}
			}
			else{
				imageKey = ImageKeys.BIN_OBJ;				
			}
		}
		
		return ImageResourceManager.getImage(imageKey);
	}
}
