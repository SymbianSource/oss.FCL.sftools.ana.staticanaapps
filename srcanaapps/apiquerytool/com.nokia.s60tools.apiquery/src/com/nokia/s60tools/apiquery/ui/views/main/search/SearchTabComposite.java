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
 
 
package com.nokia.s60tools.apiquery.ui.views.main.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * UI composite that collecting all search tab composites to Search tab.
 *
 */
public class SearchTabComposite extends AbstractUiFractionComposite {

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 1;
	
	/**
	 * Reference to the ui fraction showing query types.
	 */
	private QueryTypeSelectionComposite queryTypeUiFraction;
	
	/**
	 * Reference to the ui fraction showing search string related fields.
	 */
	private QueryDefComposite searchStringUiFraction;
	
	/**
	 * Reference to the ui fraction showing query results.
	 */
	private QueryResultsComposite queryResultsUiFraction;
	
	/**
	 * Mediator controlling logic between UI components.
	 */
	private SearchTabUIFieldsMediator mediator;

	/**
	 * Reference to the UI fraction showing result type selection
	 */
//	private ResultViewTypeSelectionComposite resultViewTypeSelectionUiFraction;
	
	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 */
	public SearchTabComposite(Composite parentComposite) {
		super(parentComposite);		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayout()
	 */
	protected Layout createLayout() {
		return new GridLayout(COLUMN_COUNT, false);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayoutData()
	 */
	protected Object createLayoutData(){
		return 	new GridData(GridData.FILL_HORIZONTAL);
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		queryTypeUiFraction = new QueryTypeSelectionComposite(this);
		searchStringUiFraction = new QueryDefComposite(this);
		queryResultsUiFraction = new QueryResultsComposite(this);
//		resultViewTypeSelectionUiFraction =  new ResultViewTypeSelectionComposite(this);
		
		// Creating mediator class that handles co-operation between
		// different search related UI parts
		mediator = new SearchTabUIFieldsMediator(queryTypeUiFraction, searchStringUiFraction, 
				                      queryResultsUiFraction);
				
	}

	/**
	 * Enables update starting of API queries
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @param useExactMatch <code>true</code> if search string will be searched with exact match 
	 * instead of contains.
	 */
	public void runAPIQuery(int queryType, String queryString, boolean useExactMatch){
		try {
			queryTypeUiFraction.setQueryType(queryType);
			searchStringUiFraction.setQueryString(queryString);
			mediator.queryStarted(queryString, useExactMatch);			
		} catch (Exception e) {
			APIQueryMessageBox msgBox = new APIQueryMessageBox(
					Messages.getString("SearchTabComposite.API_Query_ErrMsg") + e.getMessage() //$NON-NLS-1$
					, SWT.ICON_ERROR | SWT.OK);
			msgBox.open();		
		}
	}
	
	/**
	 * Enables update starting of API queries
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @return collection of APIShortDescription objects or empty list if not found
	 */
	public Collection<APIShortDescription> runActiveProjectQuery(int queryType, String queryString){
		try {
			queryTypeUiFraction.setQueryType(queryType);
			searchStringUiFraction.setQueryString(queryString);
			return mediator.activeProjectQueryStarted(queryString);			
		} catch (Exception e) {
			APIQueryMessageBox msgBox = new APIQueryMessageBox(
					Messages.getString("SearchTabComposite.API_Query_ErrMsg") + e.getMessage() //$NON-NLS-1$
					, SWT.ICON_ERROR | SWT.OK);
			msgBox.open();				
		}
		return new ArrayList<APIShortDescription>();
	}	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- Dispose() --> " + getClass().getName());		 //$NON-NLS-1$

		queryTypeUiFraction.dispose();
		searchStringUiFraction.dispose();
		queryResultsUiFraction.dispose();
		
		// Releasing listeners used by mediator class
		mediator.releaseListeners();
		
	}	
	
}
