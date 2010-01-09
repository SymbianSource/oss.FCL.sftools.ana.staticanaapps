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
 
 
package com.nokia.s60tools.apiquery.ui.views.main;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.nokia.s60tools.apiquery.servlets.APIQueryWebServerConfigurator;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.ui.views.main.properties.PropertiesTabComposite;
import com.nokia.s60tools.apiquery.ui.views.main.search.SearchTabComposite;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This class comprises the Main View of the API Query
 * application.
 * 
 * This example main view demonstrated the creation of a 
 * view with two tabbed view. The second tab also demonstrated
 * how context menus can be attached to some control
 */
public class MainView extends ViewPart {
	 
	/**
	 * We can get view ID at runtime once the view is instantiated, but we
	 * also need static access to ID in order to be able to invoke the view.
	 */
	public static final String ID = "com.nokia.s60tools.apiquery.ui.views.main.MainView"; //$NON-NLS-1$
		
	
	//
	// Controls and related classes (providers etc.)
	// 
						
	private CTabItem searchTab;
	private CTabItem propertiesTab;

static	private PropertiesTabComposite propertiesTabContents;
	private SearchTabComposite searchTabContents;

	/**
	 * The constructor.
	 */
	public MainView() {
		//Starting APIQuery WebServer
		APIQueryWebServerConfigurator.startServer(APIQueryWebServerConfigurator.Carbide_Instance_start);		
	}

	
	public  static  void enablePropTabcontents(boolean enable) 
	  { 
		  if(propertiesTabContents!=null)
		  {
		  if(enable)  propertiesTabContents.enablePropTabComponents();
		  else propertiesTabContents.disablePropTabComponents();
		  }
			  
			  
	  }
	
	
	
	
	
	/**
	 * This is called by framework when the controls for
	 * the view should be created.
	 */
	public void createPartControl(Composite parent) {
		
		try {

			//
			// Creating controls
			//

			// The left side contains component hierarchy tree view
			SashForm mainViewSashForm = new SashForm(parent,
					SWT.HORIZONTAL);

			// The right side contains tabbed panes for showing...
			CTabFolder mainViewTabFolder = new CTabFolder(
					mainViewSashForm, SWT.BOTTOM);
			//... search tab
			createSearchTabControl(mainViewTabFolder);
			//... properties tab
			createPropertiesTabControl(mainViewTabFolder);

			// Default selection for tab folder
			mainViewTabFolder.setSelection(searchTab);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Creates search tab.
	 * @param parentTabFolder Parent tab folder.
	 */
	private void createSearchTabControl(CTabFolder parentTabFolder) {
		SashForm searchTabSashForm = new SashForm(parentTabFolder, SWT.VERTICAL);		
		searchTab = new CTabItem(parentTabFolder, SWT.NONE);
		searchTab.setControl(searchTabSashForm);
		searchTab.setText(Messages.getString("MainView.Search_Tab_Title"));		 //$NON-NLS-1$
		createSearchTabContents(searchTabSashForm);
	}

	/**
	 * Creates search tab contents. This can be also delegated to a class of its own.
	 * @param parentComposite	Parent composite.
	 */
	private void createSearchTabContents(Composite parentComposite) {
		// Contents creation is delegated to an external class
		searchTabContents = new SearchTabComposite(parentComposite);	
	}

	/**
	 * Creates properties tab.
	 * @param parentTabFolder Parent tab folder.
	 */
	private void createPropertiesTabControl(CTabFolder parentTabFolder) {		
		SashForm propertiesTabSashForm = new SashForm(parentTabFolder, SWT.VERTICAL);		
		propertiesTab = new CTabItem(parentTabFolder, SWT.NONE);
		propertiesTab.setControl(propertiesTabSashForm);
		propertiesTab.setText(Messages.getString("MainView.Properties_Tab_Title")); //$NON-NLS-1$
		createPropertiesTabContents(propertiesTabSashForm);
	}
	
	/**
	 * Creates properties tab contents.
	 * @param parentComposite	Parent composite.
	 */
	private void createPropertiesTabContents(Composite parentComposite) {
		// Contents creation is delegated to an external class
		propertiesTabContents = new PropertiesTabComposite(parentComposite);	
	}
	
	
	/**
	 * Focus request should be passed to the view's primary control.
	 */
	public void setFocus() {
	}
	
	/**
	 * Allows other classes to update content description.
	 * @param newContentDescription New description.
	 */
	public void updateDescription(String newContentDescription){
		setContentDescription(newContentDescription);
		IToolBarManager tbManager = getViewSite().getActionBars().getToolBarManager();
		tbManager.update(true);
	}

	/**
	 * The view should refresh all its UI components in this method.
	 */
	public void refresh(){
	}

	/**
	 * Sets enabled/disabled states for actions commands
	 * on this view, based on the current application state.
	 * This method should be called whenever an operation is
	 * started or stopped that migh have effect on action 
	 * button states.
	 */
	public void updateActionButtonStates(){
	 // Currently there is no states that should be set here
	}

	/**
	 * Enables to get reference of the main view
	 * from the classes that do not actually
	 * have reference to the main view instance.
	 * This method opens activates/opens up the 
	 * view if it was not visible.
	 * @throws PartInitException 
	 */
	public static MainView getViewInstance() throws PartInitException{
		
		IWorkbenchPage page = APIQueryPlugin.getCurrentlyActivePage();
		return getViewInstance(page);
		
	}
	/**
	 * Enables to get reference of the main view
	 * from the classes that do not actually
	 * have reference to the main view instance.
	 * This method opens activates/opens up the 
	 * view if it was not visible.
	 * @throws PartInitException 
	 */
	public static MainView getViewInstance(IWorkbenchPage page) throws PartInitException{
				
		boolean viewAlreadyVisible = false;
		IViewPart viewPart = null;
		
		// Checking if view is already open
		IViewReference[] viewRefs = page.getViewReferences();
		for (int i = 0; i < viewRefs.length; i++) {
			IViewReference reference = viewRefs[i];
			String id = reference.getId();
			if(id.equalsIgnoreCase(MainView.ID)){
				viewAlreadyVisible = true;
				// Found, restoring the view
				viewPart = reference.getView(true);
				page.activate(viewPart);
			}
		}
		// View was not opened
		if(! viewAlreadyVisible){
			viewPart = page.showView(MainView.ID);							
		}	
		return ((MainView) viewPart);
	}	

	/**
	 * Enables update request for the main view
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 */
	public static void update(){
		try {
			
			getViewInstance().inputUpdated();
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Enables the starting of API queries
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @param useExactMatch <code>true</code> if search string will be searched with exact match 
	 * instead of contains.
	 * @throws QueryOperationFailedException 
	 */
	public static void runAPIQueryFromExternalClass(int queryType, String queryString, boolean useExactMatch ) throws QueryOperationFailedException{
		try {
			
			MainView view = getViewInstance();
			view.runAPIQuery(queryType, queryString, useExactMatch);
			
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new QueryOperationFailedException(e.getMessage());
		}
	}
	
	/**
	 * Enables the starting of API queries
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @return collection of APIShortDescription objects or empty list if not found any
	 * 	or null if MainView cannot found
	 */
	public static Collection<APIShortDescription> runActiveProjectQueryFromExternalClass(int queryType, String queryString){
		try {
			
			MainView view = getViewInstance();
			return view.runActiveProjectQuery(queryType, queryString);
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}	
		
	
	/**
	 * Starts API query operation programmatically.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 */
	private Collection<APIShortDescription> runActiveProjectQuery(int queryType, String queryString){
		return searchTabContents.runActiveProjectQuery(queryType, queryString);
	}	
	
	
	/**
	 * Starts API query operation programmatically.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @param useExactMatch <code>true</code> if search string will be searched with exact match 
	 * instead of contains.
	 */
	public void runAPIQuery(int queryType, String queryString, boolean useExactMatch){
		searchTabContents.runAPIQuery(queryType, queryString, useExactMatch);
	}
	
	/**
	 * The view should refresh all its UI components in this method.
	 */
	private void inputUpdated() {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- Dispose() --> " + getClass().getName());		 //$NON-NLS-1$
		searchTabContents.dispose();
		propertiesTabContents.dispose();
		//Stopping APIQuery WebServer
		APIQueryWebServerConfigurator.stopServer();		
		
	}

}
