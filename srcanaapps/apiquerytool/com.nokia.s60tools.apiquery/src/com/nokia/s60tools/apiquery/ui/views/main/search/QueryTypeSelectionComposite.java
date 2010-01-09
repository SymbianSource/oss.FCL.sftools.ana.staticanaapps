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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.apiquery.APIQueryHelpContextIDs;
import com.nokia.s60tools.apiquery.settings.IUserSettingsListener;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * UI composite that shows the search type selections in Search tab.
 *
 */
class QueryTypeSelectionComposite extends AbstractUiFractionComposite implements IUserSettingsListener{

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 3;
	private Group queryTypeSelectionGroup;
	private Map<Integer, Button> queryTypeBtnMap;
	
	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 */
	public QueryTypeSelectionComposite(Composite parentComposite) {
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
	
	/**
	 * Set context sensitive help ids to components that can have focus
	 *
	 */
	private void setContextSensitiveHelpIds(Button btn){
	    PlatformUI.getWorkbench().getHelpSystem().setHelp(btn, 
	    		APIQueryHelpContextIDs.API_QUERY_HELP_SEARCH_TAB);
	}	
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
				
		queryTypeSelectionGroup = new Group(this, SWT.SHADOW_NONE);
		queryTypeSelectionGroup.setText(Messages.getString("QueryTypeSelectionComposite.Query_By_Msg"));		 //$NON-NLS-1$
		
		GridLayout gdl2 = new GridLayout(COLUMN_COUNT, false);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
				
		queryTypeSelectionGroup.setLayout(gdl2);
		queryTypeSelectionGroup.setLayoutData(gd2);

		// Adding different query types
		Integer[] queryTypesArr = APIQueryParameters.getQueryTypes();
		int queryTypeCnt = queryTypesArr.length;

		// Query buttons are store into an internal array for later reference
		queryTypeBtnMap = new HashMap<Integer, Button>(queryTypeCnt);
		
		for (Integer queryType : queryTypesArr) {
			String desc = APIQueryParameters.getDescriptionForQueryType(queryType);
			Button queryBtn = new Button(queryTypeSelectionGroup, SWT.RADIO);			
			queryBtn.setText(desc);
			queryBtn.setSelection(false);			
			// Storing to Button array for later reference
			queryTypeBtnMap.put(queryType, queryBtn);
			setContextSensitiveHelpIds(queryBtn);
		}
		
		// Checking the statuses supported query types for currently
		// selected search method.
		checkSupportedQueryTypesAndUpdateUi();
		
		// Starting to listen user settings change events
		UserSettings.getInstance().addUserSettingListener(this);
	}

	/**
	 * Checks the statuses of currently supported query types and updates UI.
	 */
	private void checkSupportedQueryTypesAndUpdateUi() {
		try {
			// Setting enable and disable statuses for query types
			ISearchMethodExtensionInfo currSelExtInfo =  UserSettings.getInstance().getCurrentlySelectedSearchMethodInfo();
			String id = currSelExtInfo.getId();
			ISearchMethodExtension currSelExt = SearchMethodExtensionRegistry.getInstance().getById(id);
					
			Set<Integer> queryTypesSet = queryTypeBtnMap.keySet();
			for (Integer qType : queryTypesSet) {
				Button btn = queryTypeBtnMap.get(qType);
				boolean isSupported = currSelExt.isSupportedQueryType(qType);
				btn.setSelection(false);
				btn.setEnabled(isSupported);
			}
			
			// Setting default query type that should be supported by all extensions.
			// and therefore is always enabled and set as default selection.
			// It is on the responsibility of extension developer to make
			// sure that the default query type is supported.
			int defaultQueryType = APIQueryParameters.getDefaultQueryType();
			Button defQueryTypeBtn = queryTypeBtnMap.get(defaultQueryType);
			defQueryTypeBtn.setEnabled(true);
			defQueryTypeBtn.setSelection(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets query type for the currently selected query type.
	 * @return Returns query type for the currently selected query type.
	 */
	public int getSelectedQueryType(){
		Set<Integer> queryTypeSet = queryTypeBtnMap.keySet();
		for (Integer queryType : queryTypeSet) {
			Button btn = queryTypeBtnMap.get(queryType);
			if(btn.getSelection()){
				// This query type was selected
				return queryType;
			}
		}
		// We should never get into here
		throw new RuntimeException(Messages.getString("QueryTypeSelectionComposite.Query_Type_Was_Not_Selected_Msg")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.settings.IUserSettingsListener#userSettingsChanged()
	 */
	public void userSettingsChanged() {
		// Settings have changed which might mean that 
		// the user has changed the currently used query type.
		checkSupportedQueryTypesAndUpdateUi();		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose(){
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- Dispose() --> " + getClass().getName());		 //$NON-NLS-1$
		queryTypeSelectionGroup.dispose();
		Collection<Button> buttonsColl = queryTypeBtnMap.values();
		for (Button btn : buttonsColl) {
			btn.dispose();
		}
		queryTypeBtnMap.clear();
		// Stopping the event listening
		UserSettings.getInstance().removeUserSettingListener(this);
	}

	/**
	 * Sets the query type if it is allowed for 
	 * the currently selected search method.
	 * @param queryType Query type to be set.
	 */
	public void setQueryType(int queryType) {
		String errMsg = null;
		Button btn = queryTypeBtnMap.get(queryType);
		if(btn != null){
			if(btn.isEnabled()){
				deselectAllTypes();
				btn.setSelection(true);
				return;
			}
			else{
				errMsg = Messages.getString("QueryTypeSelectionComposite.Not_Supported_ErrMsg");			 //$NON-NLS-1$
			}
		}
		else{
			errMsg = Messages.getString("QueryTypeSelectionComposite.Query_Type_Not_Available_ErrMsg");			 //$NON-NLS-1$
		}
		// We get here only if something fails
		throw new IllegalArgumentException(errMsg);
	}

	/**
	 * Clears all the query type selections.
	 */
	private void deselectAllTypes() {
		for (Button btn : queryTypeBtnMap.values()) {
			btn.setSelection(false);			
		}
	}
}
