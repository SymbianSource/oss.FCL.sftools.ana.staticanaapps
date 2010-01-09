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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;

import com.nokia.s60tools.appdep.core.model.ImportFunctionData;

/**
 * This class listens to the selection events happening in
 * imported functions pane.
 * @see org.eclipse.jface.viewers.ISelectionChangedListener
 */
public class ImportedFunctionsTabSelectionChangedListener implements ISelectionChangedListener {

	/**
	 * Reference to main view.
	 */
	private final MainView view;
	
	/**
	 * Default constructor.
	 * @param view Reference to main view
	 */
	public ImportedFunctionsTabSelectionChangedListener(MainView view){
		this.view = view;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {
			TableViewer viewer = view.getImportFunctionsViewer();
			IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
			if(sel.size() > 1){
				view.updateImportFunctionsContextMenuStates(true);
			}
			else{
				view.updateImportFunctionsContextMenuStates(false);				
			}
			
			Object functionObj = view.getSelectedImportFunction();
			if(functionObj == null){
				return;
			}
			ImportFunctionData efData = (ImportFunctionData) functionObj;
			String methodName = efData.getFunctionName();
			enableOrDisableShowSourceMenuAction(methodName);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Enable or disable show source menu action, depending on method name
	 * @param methodName Method name
	 * @param mainView Reference to main view
	 */
	private void enableOrDisableShowSourceMenuAction(String methodName) {
		
		boolean disableShowSource = false;
		if (methodName==null){
			disableShowSource = true;
		}
		String [] disabledMethodNames = view.getDisableShowSourcePrefixes();
		for (int i = 0; !disableShowSource && i < disabledMethodNames.length; i++) {
			if(methodName.startsWith(disabledMethodNames[i])){
				disableShowSource = true;
			}
		}
		
		// Getting if multiple lines is selected.
		TableViewer viewer = view.getImportFunctionsViewer();
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		boolean isMultipleLinesSelected = sel.size() > 1;
		
		if(disableShowSource || isMultipleLinesSelected){
			view.disableImportShowSourceAction();
		}
		else{
			view.enableImportShowSourceAction();
		}
	}

}
