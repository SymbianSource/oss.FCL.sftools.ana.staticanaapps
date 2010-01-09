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

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.job.UsedByOtherComponentsJob;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Starts is used by query from main view's component tree.
 */
public class ComponentIsUsedByMainViewAction extends AbstractMainViewAction{
	
	/**
	 * Result array from is used by query.
	 */
	ArrayList<ComponentPropertiesData> resultComponentsArrayList = null;

	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ComponentIsUsedByMainViewAction(MainView view){
		super(view);
		
		setText(Messages.getString("ComponentIsUsedByMainViewAction.IsUsedBy_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ComponentIsUsedByMainViewAction.IsUsedBy_Action_Tooltip")); //$NON-NLS-1$
	
		
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
		Object obj = view.getComponentTreeSelectedElement();
					
		if(obj == null){
			// We might get null-selections when
			// tree is expanded/collapsed.
			return;
		}

		// Component is for sure a component node
		ComponentNode node = (ComponentNode) obj;
		String componentName;
		//If component was a generic component and is replaced with concrete component, must do search with original name
		if(node instanceof ComponentParentNode){
			ComponentParentNode parentNode = (ComponentParentNode)node;
			if(parentNode.wasGenericComponent()){
				componentName = parentNode.getOriginalName();
			}
			else{
				componentName = node.getName();							
			}
		}else{
			componentName = node.getName();			
		}
		

		AppDepSettings settings = AppDepSettings.getActiveSettings();
		if(settings.getCurrentlyAnalyzedComponentName() != null){
			AppDepConsole.getInstance().println(Messages.getString("ComponentIsUsedByMainViewAction.IsUsedBy_Query_Start_Console_Msg") //$NON-NLS-1$
                    + componentName + "'..."); //$NON-NLS-1$
			
			resultComponentsArrayList = new ArrayList<ComponentPropertiesData>();
			Job jb = new UsedByOtherComponentsJob(Messages.getString("ComponentIsUsedByMainViewAction.IsUsedBy_Job_Title_Text"), settings, //$NON-NLS-1$
													componentName,
													null,
													null,
													resultComponentsArrayList,
													AppDepPlugin.getCurrentlyActivePage());
			
			// We do not want cache generation to block other 
			// jobs and therefore using the lowest priority
			jb.setPriority(Job.DECORATE);
			jb.schedule();
		}
				
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();		
	}
}
