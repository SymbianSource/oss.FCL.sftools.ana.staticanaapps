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

 
package com.nokia.s60tools.appdep.ui.actions;

import org.eclipse.jface.action.Action;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.listview.ListView;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Sets selected component as new root component.
 * Available in component list view.
 */
public class SetNewRootComponentListViewAction extends Action {
	
	/**
	 * Component list view reference.
	 */
	private final ListView listView;
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public SetNewRootComponentListViewAction(ListView view){
		this.listView = view;
		
		setText(Messages.getString("SetNewRootComponentListViewAction.SetNewRoot_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SetNewRootComponentListViewAction.SetNewRoot_Action_Tooltip")); //$NON-NLS-1$
		
		
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		setImageDescriptor(ImageResourceManager.
								getImageDescriptor(ImageKeys.NEW_ROOT_ACTION));				
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {

		try {
			Object obj = listView.getComponentListSelectedElement();

			MainView.abortCurrentlyOngoingSearches();
			
			// The object is for sure an instance of ComponentPropertiesData
			ComponentPropertiesData propData = (ComponentPropertiesData) obj;
			String selectedComponentName = propData.getFilename();
			ITargetPlatform targetPlatform = propData.getTargetPlatform();
			
			AppDepSettings st = AppDepSettings.getActiveSettings();
			st.setCurrentlyAnalyzedComponentName(selectedComponentName);
			st.setCurrentlyAnalyzedComponentTargetPlatform(targetPlatform);
			MainView.update();
						
			// Remember to always call AbstractMainViewAction
			// base class implementation
			super.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
}
