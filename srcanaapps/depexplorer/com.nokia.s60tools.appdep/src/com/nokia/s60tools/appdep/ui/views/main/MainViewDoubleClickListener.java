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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.DrillDownAdapter;

import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.ui.wizards.WizardUtils;
import com.nokia.s60tools.ui.ProgrammaticSelection;

/**
 * Main view's double-click listener.
 */
public class MainViewDoubleClickListener implements IDoubleClickListener {
	
	/**
	 * Main view reference.
	 */
	private MainView view;
	/**
	 * Adapter used to implement double-click action.
	 */
	private DrillDownAdapter drillDownAdapter;
	
	/**
	 * Constructor.
	 * @param view Main view reference.
	 * @param drillDownAdapter Adapter used to implement double-click action.
	 */
	public MainViewDoubleClickListener(MainView view, DrillDownAdapter drillDownAdapter){
		this.view = view;
		this.drillDownAdapter = drillDownAdapter;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		try {			
			ISelection selection = view.getComponentTreeViewer().getSelection();
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			
			if(obj instanceof ComponentLinkLeafNode){
				
				// Making sure that referred component can be found
				if(drillDownAdapter.canGoHome()){
					drillDownAdapter.goHome();
				}
				
				ComponentLinkLeafNode link = (ComponentLinkLeafNode) obj;
				ProgrammaticSelection newSelection = null;
				newSelection = new ProgrammaticSelection(
													new ComponentNode[]{
															link.getReferredComponent()
																		}
														);
				view.getComponentTreeViewer().setSelection(newSelection, true);
			}
			else if(obj instanceof ComponentParentNode){
				ComponentParentNode pnode = (ComponentParentNode) obj;
				if(pnode.isRootComponent()){

					// Aborting possible ongoing search
					MainView.abortCurrentlyOngoingSearches();
					
					// Opening wizard dialog
					Shell sh = view.getViewSite().getShell();
					if(WizardUtils.invokeSDKAndTargetPlatformSelectionWizard(sh)){
						view.inputUpdated();    			
					}											
				}
				else{
					drillDownAdapter.goInto();								
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
