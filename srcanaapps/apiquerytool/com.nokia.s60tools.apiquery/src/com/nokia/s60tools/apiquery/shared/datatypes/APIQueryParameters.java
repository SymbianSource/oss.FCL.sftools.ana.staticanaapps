/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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
 
package com.nokia.s60tools.apiquery.shared.datatypes;

import java.util.ArrayList;

import com.nokia.s60tools.apiquery.shared.resources.Messages;

/**
 * Used to store parameters for an API query.
 */
public class APIQueryParameters {


	//
	// Query types
	//
	public static final int QUERY_BY_API_NAME = 1;
	public static final int QUERY_BY_SUBSYSTEM_NAME = 2;
	public static final int QUERY_BY_DLL_NAME = 3;
	public static final int QUERY_BY_LIB_NAME = 4;
	public static final int QUERY_BY_HEADER_NAME = 5;
	public static final int QUERY_BY_CRPS_KEY_NAME = 6;
	
	/**
	 * Search can contain multiple search items that can be
	 * separated with this separator char.
	 */
	public static final String SEARCH_ITEM_SEPARATOR_CHAR = ";"; //$NON-NLS-1$
	
	/**
	 * Query type.
	 */
	private final int queryType;
	
	/**
	 * Search string used for the query.
	 */
	private final String searchString;
	
	/**
	 * Is query from UI thread or not
	 */
	private boolean isQueryFromUI = true;
	
	/**
	 * is API Details ment to add to Descriptions when search is done.
	 */
	private boolean isDetailsMentToAddToDescriptions = false;	
	
	/**
	 * If Exact match shoud be used instead of contains when search results is gathered.
	 */
	boolean isExactMatchInUse = false;
	
	/**
	 * Constructor.
	 * @param queryType Query type.
	 * @param searchString Search string used for the query.
	 * @throws java.lang.IllegalArgumentException
	 * @see java.lang.IllegalArgumentException
	 */
	public APIQueryParameters(int queryType, String searchString){
		this.queryType = queryType;
		this.searchString = searchString.trim();
		validateArguments();
	}


	/**
	 * @return the isQueryFromUI
	 */
	public boolean isQueryFromUI() {
		return isQueryFromUI;
	}


	/**
	 * @param isQueryFromUI the isQueryFromUI to set
	 */
	public void setQueryFromUI(boolean isQueryFromUI) {
		this.isQueryFromUI = isQueryFromUI;
	}


	/**
	 * Validating the parameter data.
	 * @throws java.lang.IllegalArgumentException
	 * @see java.lang.IllegalArgumentException
	 */
	private void validateArguments() {
		if(queryType < 1 || queryType >6){
			throw new IllegalArgumentException(Messages.getString("APIQueryParameters.UnsupportedQueryType_Msg") + queryType); //$NON-NLS-1$
		}
	}


	/**
	 * @return the queryType
	 */
	public int getQueryType() {
		return queryType;
	}


	/**
	 * @return the searchString
	 */
	public String getSearchString() {
		return searchString;
	}
	
	/**
	 * Returns currently supported query types.
	 * @return Array of integer constants for the currently
	 *         supported query types.
	 */
	public static Integer[] getQueryTypes(){
		ArrayList<Integer> queryTypesArr = new ArrayList<Integer>();
		for (int i = QUERY_BY_API_NAME; i <= QUERY_BY_CRPS_KEY_NAME; i++) {
			queryTypesArr.add(new Integer(i));
		}
		return queryTypesArr.toArray(new Integer[0]);
	}
	
	/**
	 * Maps query type to description.
	 * @param queryType Query type constant.
	 * @return Description for the given query type.
	 */
	public static String getDescriptionForQueryType(int queryType){
		
		String desc = null;
		
		switch (queryType) {
		
		case QUERY_BY_API_NAME:
			desc = Messages.getString("APIQueryParameters.APIName_Msg"); //$NON-NLS-1$
			break;

		case QUERY_BY_SUBSYSTEM_NAME:
			desc = Messages.getString("APIQueryParameters.SubsystemName_Msg"); //$NON-NLS-1$
			break;

		case QUERY_BY_DLL_NAME:
			desc = Messages.getString("APIQueryParameters.DLLName_Msg"); //$NON-NLS-1$
			break;

		case QUERY_BY_LIB_NAME:
			desc = Messages.getString("APIQueryParameters.LIBName_Msg"); //$NON-NLS-1$
			break;

		case QUERY_BY_HEADER_NAME:
			desc = Messages.getString("APIQueryParameters.HeaderName_Msg"); //$NON-NLS-1$
			break;

		case QUERY_BY_CRPS_KEY_NAME:
			desc = Messages.getString("APIQueryParameters.CR_PS_Name_Msg"); //$NON-NLS-1$
			break;

		default:
			throw new IllegalArgumentException(Messages.getString("APIQueryParameters.QueryTypePart1_ErrMsg")  //$NON-NLS-1$
					                           + queryType
					                           + Messages.getString("APIQueryParameters.QueryTypePart2_ErrMsg") //$NON-NLS-1$
					                           );
		}
		
		return desc;
	}
		
	
	

	/**
	 * Maps query type to description.
	 * @param queryType Query type constant as Integer object..
	 * @return Description for the given query type.
	 */
	public static String getDescriptionForQueryType(Integer queryType){
		return getDescriptionForQueryType(queryType.intValue());
	}	
	
	/**
	 * Gets default query type. 
	 * @return Returns default query type.
	 */
	public static int getDefaultQueryType(){
		// NOTE: This query type SHOULD be supported by ALL search methods.
		return QUERY_BY_API_NAME;
	}


	/**
	 * Check if {@link APIDetails} should be added to {@link APIShortDescription}:s when 
	 * query is executed.
	 * @return <code>true</code> if API Details should be added to Descriptions, <code>false</code> otherwise.
	 */
	public boolean isDetailsMentToAddToDescriptions() {
		return isDetailsMentToAddToDescriptions ;
	}
	/**
	 * Set if {@link APIDetails} should be added to {@link APIShortDescription}:s when 
	 * query is executed.
	 * @param <code>true</code> if {@link APIDetails} should be added to {@link APIShortDescription}:s when 
	 * query is executed.
	 */
	public void setDetailsMentToAddToDescriptions(
			boolean isDetailsMentToAddToDescriptions) {
		this.isDetailsMentToAddToDescriptions = isDetailsMentToAddToDescriptions;
	}
	
	/**
	 * Check if exact match should be used instead of contain when search results are compared
	 * @return the isExactMatchInUse <code>true</code> if exact match should be used, <code>false</code> otherwise.
	 */
	public boolean isExactMatchInUse() {
		return isExactMatchInUse;
	}


	/**
	 * Set if exact match should be used instead of contain when search results are compared.
	 * @param isExactMatchInUse <code>true</code> if exact match should be used, <code>false</code> otherwise.
	 */
	public void setExactMatchInUse(boolean isExactMatchInUse) {
		this.isExactMatchInUse = isExactMatchInUse;
	}	
}
