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

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Sets selected component as new root component.
 * Available in component tree.
 */
public class SetNewRootMainViewAction extends AbstractMainViewAction {
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public SetNewRootMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("SetNewRootMainViewAction.SetNewRoot_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SetNewRootMainViewAction.SetNewRoot_Action_Tooltip")); //$NON-NLS-1$
		
		
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
			
			boolean isCurrentSelectionRootComponent = view.isRootNodeSelected();
			if(isCurrentSelectionRootComponent){
				// Selected node was already root component
				return;				
			}

			// Aborting possible ongoing search
			MainView.abortCurrentlyOngoingSearches();
			
			// The object is for sure an instance of ComponentNode
			ComponentNode node = (ComponentNode) view.getComponentTreeSelectedElement();
			String selectedComponentName = node.getName();
			
			AppDepSettings st = AppDepSettings.getActiveSettings();
			st.setCurrentlyAnalyzedComponentName(selectedComponentName);
			st.setCurrentlyAnalyzedComponentTargetPlatform(node.getTargetPlatform());

			view.inputUpdated();    			
						
			// Remember to always call AbstractMainViewAction
			// base class implementation
			super.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
