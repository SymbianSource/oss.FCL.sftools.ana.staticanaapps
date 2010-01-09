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

import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Expands all subtrees in main view's component tree.
 */
public class ExpandAllMainViewAction extends AbstractMainViewAction {
		
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ExpandAllMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("ExpandAllMainViewAction.ExpandAllAction_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ExpandAllMainViewAction.ExpandAllAction_Tooltip")); //$NON-NLS-1$
		
		
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		setImageDescriptor(ImageResourceManager.
							getImageDescriptor(ImageKeys.EXPAND_ALL_ACTION));				
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		view.getComponentTreeViewer().expandAll();
		view.getComponentTreeViewer().refresh();
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
	}
}
