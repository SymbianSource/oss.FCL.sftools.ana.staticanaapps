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
 
 
package com.nokia.s60tools.apiquery.shared.searchmethod;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import org.eclipse.swt.widgets.Composite;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescriptionSearchResults;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;

/**
 * Interface that has to be implemnted by all 
 * search method instances. 
 */
public interface ISearchMethodExtension {
	
	/**
	 * Gets the extension info for the search method.
	 * @return Returns extension info for the search method.
	 */
	ISearchMethodExtensionInfo getExtensionInfo();
		
	/**
	 * This method is called when we do not need
	 * the services of this extension. This gives
	 * possibilities for the extension to make
	 * any shutdown related operations it sees
	 * as necessary.
	 */
	public void notifyExtensionShutdown();

	/**
	 * Returns the search method specific configuration UI as 
	 * UI Composite that can be disposed and re-created whenever needed.
	 * @param parent Parent composite for the created configuration UI.
	 * @return Returns the search method specific configuration UI.
	 */
	public AbstractUiFractionComposite createExtensionConfigurationUi(Composite parent);

	/**
	 * Runs API Query with the given parameters. 
	 * Throws <code>QueryOperationFailedException</code> if something failed
	 * during query operation.
	 * @param parameters Parameters guiding the query.
	 * @return APIShortDescriptionSearchResults containing Collection of API summary objects 
	 * 	for the APIs found based on the query and possible QueryOperationFailedException search errors Collection.
	 */
	public APIShortDescriptionSearchResults runAPIQuery(APIQueryParameters parameters);

	/**
	 * Gets API details for the given API.
	 * Throws <code>QueryOperationFailedException</code> if something failed
	 * during query operation.
	 * @param summary API summary object to get details for. 
	 * @return API details for the given summary object.
	 * @throws QueryOperationFailedException
	 */
	public APIDetails getAPIDetails(APIShortDescription summary) throws QueryOperationFailedException;

	/**
	 * Gets API details for the given APIs.
	 * Throws <code>QueryOperationFailedException</code> if something failed
	 * during query operation.
	 * @param list of API summary objects to get details for. 
	 * @return API details for the given summary objects. Given API name as key and APIDetails as value.
	 * If cant found or parse a Details, an empty Details is added to collection.
	 * @throws QueryOperationFailedException
	 */	
	Hashtable<String, APIDetails> getAPIDetails(Collection<APIShortDescription> apis)  throws QueryOperationFailedException;

	
	/**
	 * Can be used to check if a certain query type 
	 * is supported by the search method.
	 * @param queryType Query type defined in APIQueryParameters to be checked.
	 * @return	<code>true</code> if the query type is supported, otherwise <code>false</code>.
	 * @see	APIQueryParameters#QUERY_BY_API_NAME
	 * @see APIQueryParameters#QUERY_BY_SUBSYSTEM_NAME
	 * @see APIQueryParameters#QUERY_BY_DLL_NAME
	 * @see APIQueryParameters#QUERY_BY_LIB_NAME
	 * @see APIQueryParameters#QUERY_BY_HEADER_NAME
	 * @see APIQueryParameters#QUERY_BY_CRPS_KEY_NAME	
 	 */
	public boolean isSupportedQueryType(int queryType);
	
	/**
	 * Returns search method specific configuration storage instance.
	 * @return Data Source specific configuration storage instance.
	 */
	public AbstractEntryStorage getEntryStorageInstance();

	/**
	 * Get API details to show in report.
	 * @return API Detail topics to be shown in report
	 */
	public String[] getAPIDetailsToReport(Set <String> usedAPIs, Hashtable<String, APIDetails> projectUsingAPIDetails);
	
	/**
	 * Maps query type to API Detail topic in API Details.
	 * @param queryType Query type constant.
	 * @return Name of the API Detail.
	 */	
	public String getAPIDetailNameInDetailsByQueryType(int queryType);

	/**
	 * Maps API Detail topic in API Details to query type.
	 * @param queryType Query type constant.
	 * @return Query type int described in {@link APIQueryParameters} or -1 if not supported.
	 */	
	public int getQueryTypeByAPIDetailNameInDetails(String queryType);
	
	/**
	 * 
	 * @return true for web query and false for other data sources 
	 */
	public boolean isAsyncQueryPreferred();
	
	/**
	 * if it's for a header serach,then should the headerlink be enabled so that user can click on it and open it.
	 */
	public boolean serachHeaderLinkEnable();
	
/**
 * opens the supplied header file.Provide an implementation if headerlink is enabled.
 * @param header : header name
 * @param source : APIShortDescription.getSource()
 * @return 0-successfully opened , 1-error
 */
	public int openHeaderFile(String headerName,String source);
	
	/*
	 * 
	 */
	public String[] getHeaderSourceList();
		
	

	

}
