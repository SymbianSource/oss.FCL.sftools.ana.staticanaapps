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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class ToolbarShortcutAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	/**
	 * The constructor.
	 */
	public ToolbarShortcutAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
                
    	try {
    		IWorkbenchPage page = window.getActivePage();
    		
    		// Checking if view is already open
    		IViewReference[] viewRefs = page.getViewReferences();
    		for (int i = 0; i < viewRefs.length; i++) {
				IViewReference reference = viewRefs[i];
				String id = reference.getId();
				if(id.equalsIgnoreCase(MainView.ID)){
					// Found, restoring the view
					IViewPart viewPart = reference.getView(true);
					page.activate(viewPart);
		    		startDefaultWizard(viewPart);
					return;
				}
			}
    		
    		// View was not found, opening it up as a new view.
    		IViewPart viewPart = page.showView(MainView.ID);
    		startDefaultWizard(viewPart);
    		
    	} catch (Exception e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(e.getMessage(), 
										 IConsolePrintUtility.MSG_ERROR);
		}
    	
	}

	/**
	 * Starts the default wizard of the tool.
	 * @param viewPart Reference to the tools main view.
	 */
	private void startDefaultWizard(IViewPart viewPart) {
		MainView view = (MainView) viewPart;		
		// Starting default wizard
		SelectNewSDKMainViewAction selectNewSDKMainViewAction = new SelectNewSDKMainViewAction(view);
		selectNewSDKMainViewAction.run();
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}