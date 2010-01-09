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


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;

/**
 * Content provider for Component Properties tab item.
 */
class MainViewComponentPropertiesTabContentProvider implements IStructuredContentProvider {
		
	/**
	 * Reference to parent view.
	 */
	private final MainView view;
	
	/**
	 * Content provider's constructor.
	 * @param view Reference to parent view.
	 */
	public MainViewComponentPropertiesTabContentProvider(MainView view){
		this.view = view;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		ComponentPropertiesData componentProperties;
		componentProperties = view.getSelectedComponentPropertiesData();
		if(componentProperties != null){
			return componentProperties.toPropertyDataArray();			
		}
		return new Object[0];
	}
	
}
