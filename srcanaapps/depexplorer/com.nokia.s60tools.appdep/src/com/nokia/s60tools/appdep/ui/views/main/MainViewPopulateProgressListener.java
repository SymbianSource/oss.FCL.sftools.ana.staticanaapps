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

import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.IComponentSearchProgressListener;
import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Main view population progress listener
 */
public class MainViewPopulateProgressListener implements IComponentSearchProgressListener {

	/**
	 * Determines the UI refresh cycle. The refresh is done after
	 * UI_REFRESH_CYCLE amount of components has been added.
	 */
	private static final int UI_REFRESH_CYCLE = 20;
	
	/**
	 * Determines how often component count information
	 * on toolbar is updated.
	 */
	private static final int COMPONENT_COUNT_REFRESH_CYCLE = 5;
	
	/**
	 * View into which report progress into.
	 */
	MainView view = null;
	
	/**
	 * Default constructor
	 * @param view View into which report progress into.
	 */
	public MainViewPopulateProgressListener(MainView view){
		this.view = view;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentSearchProgressListener#searchStarted()
	 */
	public void searchStarted() {
		view.searchStarted();
		String descriptionText = Messages.getString("MainViewPopulateProgressListener.Searching_Msg"); //$NON-NLS-1$
		view.updateDescription(descriptionText);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentSearchProgressListener#componentAdded(com.nokia.s60tools.appdep.core.data.ComponentNode, int)
	 */
	public void componentAdded(ComponentNode node, int componentTotalCount) {
		final String descriptionText = Messages.getString("MainViewPopulateProgressListener.Searching_Msg_Start")  //$NON-NLS-1$
			                     + componentTotalCount + Messages.getString("MainViewPopulateProgressListener.Searching_Msg_End"); //$NON-NLS-1$
		
		final int total = componentTotalCount;
		
		Runnable updateRunnable = new Runnable(){
			public void run(){
				if((total % COMPONENT_COUNT_REFRESH_CYCLE) == 0){
					view.updateDescription(descriptionText);	
				}
				if((total % UI_REFRESH_CYCLE) == 0){
					view.refresh();
				}
			}
		};
		
		// Update request done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(updateRunnable);		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentSearchProgressListener#searchFinished(int)
	 */
	public void searchFinished(int componentTotalCount) {
		final String descriptionText = Messages.getString("MainViewPopulateProgressListener.Search_Complete_Msg_Start")  //$NON-NLS-1$
			                     + componentTotalCount + Messages.getString("MainViewPopulateProgressListener.Search_Complete_Msg_End"); //$NON-NLS-1$
				
		Runnable updateRunnable = new Runnable(){
			public void run(){
				view.updateDescription(descriptionText);	
				view.searchCompleted();
				view.refresh();
			}
		};
		
		// Update request done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(updateRunnable);		

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentSearchProgressListener#searchAborted(int)
	 */
	public void searchAborted(int componentTotalCount) {
		final String descriptionText = Messages.getString("MainViewPopulateProgressListener.Search_Aborted_Msg_Start")  //$NON-NLS-1$
			                     + componentTotalCount + Messages.getString("MainViewPopulateProgressListener.Search_Aborted_Msg_End"); //$NON-NLS-1$
				
		Runnable updateRunnable = new Runnable(){
			public void run(){
				view.updateDescription(descriptionText);	
				view.searchCompleted();
				view.refresh();
			}
		};
		
		// Update request done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(updateRunnable);		
		
	}

}
