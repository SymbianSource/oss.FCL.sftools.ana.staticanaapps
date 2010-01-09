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


import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.model.ExportFunctionData;

/**
 * Content provider for Import Functions tab item. 
 */
class MainViewExportFunctionsTabContentProvider implements IStructuredContentProvider {
		
	/**
	 * Reference to <code>ArrayList</code> object that always 
	 * contains the current list of imported functions.
	 * This list is populated by <code>MainViewSelectionChangedListener</code> 
	 * class.
	 * @see com.nokia.s60tools.appdep.ui.views.main.MainViewSelectionChangedListener
	 */
	private ArrayList<ExportFunctionData> exportFunctionsArrayList = null;
	
	/**
	 * Content provider's constructor.
	 * @param exportFunctionsArrayList <code>ArrayList</code> object that always 
	 * contains the current list of exported functions.
	 */
	public MainViewExportFunctionsTabContentProvider(ArrayList<ExportFunctionData> exportFunctionsArrayList){
		this.exportFunctionsArrayList = exportFunctionsArrayList;
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
		return exportFunctionsArrayList.toArray();
	}
	
}
