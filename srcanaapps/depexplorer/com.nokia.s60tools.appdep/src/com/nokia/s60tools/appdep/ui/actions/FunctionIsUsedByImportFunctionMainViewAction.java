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



import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * Starts query for components that use certain import function.
 */
public class FunctionIsUsedByImportFunctionMainViewAction extends FunctionIsUsedByMainViewAction{
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public FunctionIsUsedByImportFunctionMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("FunctionIsUsedByImportFunctionMainViewAction.IsUsedByImportFunction_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("FunctionIsUsedByImportFunctionMainViewAction.IsUsedByImportFunction_Action_Tooltip")); //$NON-NLS-1$
		
		
        /*******************************************************************************
         * This piece of the graphic is taken/modified from a graphic that is made 
         * available under the terms of the Eclipse Public License v1.0.
         *
         * See 'com.nokia.s60tools.appdep.resources.ImageResourceManager' 
         * for detailed information about the original graphic.
         *  
         *******************************************************************************/        
		setImageDescriptor(ImageResourceManager.
							getImageDescriptor(ImageKeys.IS_USED_BY_ACTION));				
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		Object importFunctionObj = view.getSelectedImportFunction();
		Object compNodeObj = view.getComponentTreeSelectedElement();
					
		if(importFunctionObj == null || compNodeObj == null){			
			// We might get null-selections when
			// tree is expanded/collapsed.
			// or when Go Into -action is run.
			
			// Trying to check if we have valid selection cached
			compNodeObj = view.getMostRecentlySelectedComponentNode();
			if(importFunctionObj == null || compNodeObj == null){
				return;				
			}
		}

		// Casting and getting the needed information
		ComponentNode node = (ComponentNode) compNodeObj;
		String componentName = node.getName();			
		
		ImportFunctionData ifData = (ImportFunctionData) importFunctionObj;
		String functionName = ifData.getFunctionName();
		String ordinalAsString = ifData.getFunctionOrdinal();

		super.startIsUsedDependencyQueryJob(componentName, functionName, ordinalAsString);
				
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();		
	}
}
