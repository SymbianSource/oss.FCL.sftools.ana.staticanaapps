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


import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * The content provider class is responsible for
 * providing objects to the Component Selection Wizard. 
 */
class SelectComponentWizardPageContentProvider implements IStructuredContentProvider {
		
	/**
	 * Wizard page to provide content for.
	 */
	private SelectComponentWizardPage wizardPage = null;
	/**
	 * Filter string that can be used to filter the contents.
	 */
	private String filterString = null;
	/**
	 * Component list used currently
	 */
	private ArrayList<ComponentListNode> components;
	
	/**
	 * Content provider's constructor
	 * @param wizardPage Wizard page to provide content for.
	 */
	public SelectComponentWizardPageContentProvider(SelectComponentWizardPage wizardPage){
		this.wizardPage = wizardPage;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		// Not needed
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Not needed
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		
		components = new ArrayList<ComponentListNode>();
		
		ISelectSDKWizard wiz = (ISelectSDKWizard) wizardPage.getWizard();
		
		try {
			
			Iterator<ComponentListNode> compIter = wiz.getComponentIterator(wizardPage.getDuplicateComponentListInstance());
			if(compIter != null){
				for (; compIter.hasNext();) {
					ComponentListNode componentNode = compIter.next();
					if(filterString != null){
						String componentName = componentNode.toString();
						if(componentName.contains(filterString)){
							addComponent(components, componentNode);													
						}
					}
					else{
						addComponent(components, componentNode);						
					}
				}				
			}
			
		} catch (NullPointerException e) {
			// We can ignore this this happens because there
			// are no SDK or Target Platform selected yet.
		} catch (Exception e) {
			AppDepConsole.getInstance().println(Messages.getString("SelectComponentWizardPageContentProvider.Failed_To_Get_Component_List") //$NON-NLS-1$
												+ e.getMessage(), 
												IConsolePrintUtility.MSG_ERROR);
			e.printStackTrace();
		}
		
		return components.toArray();
	}

	/**
	 * Adds a component to UI list. Check also if we are in SIS mode and want to 
	 * show only SIS components.
	 * @param components
	 * @param componentNode
	 */
	private void addComponent(ArrayList<ComponentListNode> components,
			ComponentListNode componentNode) {
		
		//If We are in SIS Analysis mode, and user has been selecting to show only SIS components
		if(wizardPage.showOnlySISComponents()){
			//Then we show only SIS components (components that starts with "sis"
			if(componentNode.getBuildTargetType().getId().toLowerCase().startsWith(
					AppDepSettings.TARGET_TYPE_ID_SIS.toLowerCase())){
				components.add(componentNode);							
			}
		}else{
			components.add(componentNode);			
		}
	}

	/**
	 * Sets the criteria to filter out the components
	 * that does not match with it.
	 * @param searchText Search criteria to run match with.
	 */
	void setFilter(String searchText) {
		filterString = searchText;
	}

	/**
	 * Gets current element count shown by content provider.
	 * @return current element count shown by content provider.
	 */
	public int getElementCount() {
		if(components != null){
			return components.size();
		}
		// Components array not initialized
		return 0;
	}
	
}
