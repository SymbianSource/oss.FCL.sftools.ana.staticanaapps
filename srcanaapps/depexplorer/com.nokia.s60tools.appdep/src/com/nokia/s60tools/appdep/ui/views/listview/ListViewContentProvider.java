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
 
 
package com.nokia.s60tools.appdep.ui.views.listview;


import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;

/**
 * Content provider for Import Functions tab item. 
 */
class ListViewContentProvider implements IStructuredContentProvider {
		
	/**
	 * Array containing component properties data
	 */
	private ArrayList<ComponentPropertiesData> listViewItemsArrayList = null;

	/**
	 * Content provider's constructor.
	 */
	public ListViewContentProvider(){
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		if(newInput instanceof ArrayList){
			// Input is what we have expected
			listViewItemsArrayList = (ArrayList<ComponentPropertiesData>)newInput;
		}
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
		if(listViewItemsArrayList != null){
			return listViewItemsArrayList.toArray();			
		}
		return new Object[0];
	}
	
}
