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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;

import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescriptionSearchResults;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.exceptions.XMLNotValidException;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Abstract class that provides common services for all search methods.
 */
public abstract class AbstractSearchMethodExtension implements ISearchMethodExtension, IExecutableExtension {

	public static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
	public static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$
	public static final String IS_DEFAULT_ATTRIBUTE = "isDefault"; //$NON-NLS-1$
		
	private SearchMethodExtensionInfo extensionInfo;
	
	/**
	 * The subclasses of this abstract class is created by using <code>createExecutableExtension</code>
	 * method from <code>IConfigurationElement</code> class, and therefore
	 * the constructor cannot have any parameters.
	 * @see org.eclipse.core.runtime.IConfigurationElement#createExecutableExtension
	 */
	public AbstractSearchMethodExtension(){
		extensionInfo = new SearchMethodExtensionInfo();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement configElem, String classPropertyName, Object data) throws CoreException {
		extensionInfo.setId(configElem.getAttribute(ID_ATTRIBUTE));
		extensionInfo.setDescription(configElem.getAttribute(DESCRIPTION_ATTRIBUTE));
		extensionInfo.setDefault(Boolean.parseBoolean(configElem.getAttribute(IS_DEFAULT_ATTRIBUTE)));
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getExtensionInfo()
	 */
	public SearchMethodExtensionInfo getExtensionInfo() {
		return extensionInfo;
	}

	/**
	 * Throws an exception for user with the given message.
	 * @param errorMsg Main error message for the user.
	 * @param detailedMsg More detailed message shown in parenthesis.
	 * @throws QueryOperationFailedException 
	 */
	protected void queryFailed(String errorMsg, String detailedMsg) throws QueryOperationFailedException {
		APIQueryConsole.getInstance().println(errorMsg, APIQueryConsole.MSG_ERROR);
		String combinedMsg = getCombinedMessage(errorMsg, detailedMsg);
				throw new QueryOperationFailedException(combinedMsg);
	}


	private String getCombinedMessage(String errorMsg, String detailedMsg) {
		String combinedMsg = errorMsg + " (" + detailedMsg + ")."; //$NON-NLS-1$ //$NON-NLS-2$
		return combinedMsg;
	}
	
	/**
	 * Throws an exception for user with the given message.
	 * @param errorMsg Main error message for the user.
	 * @param detailedMsg More detailed message shown in parenthesis.
	 * @param QueryOperationFailedException
	 * @throws QueryOperationFailedException 
	 */
	private void queryFailed(String errorMsg, String detailedMsg, QueryOperationFailedException e) throws QueryOperationFailedException {
		APIQueryConsole.getInstance().println(errorMsg, APIQueryConsole.MSG_ERROR);
		String combinedMsg = getCombinedMessage(errorMsg, detailedMsg);
		QueryOperationFailedException ex = new QueryOperationFailedException(combinedMsg);
		ex.setSummarys(e.getSummarys());
		throw ex;
	}	
	
	/**
	 * Examines the type of throwable parameter, and adds appropriate
	 * error message or default message, and forwards the information
	 * for further processing.
	 * @param throwable Throwable to be examined.
	 * @throws QueryOperationFailedException 
	 */
	protected void examineAndHandleQueryFailureException(Throwable throwable) throws QueryOperationFailedException{
		
		String errMsg = Messages.getString("AbstractSearchMethodExtension.UnexpectedException_ErrMsg"); //$NON-NLS-1$
		
		if(throwable instanceof QueryOperationFailedException) {
			errMsg = "Query error";
			queryFailed(errMsg, throwable.getMessage(), (QueryOperationFailedException)throwable);
		} else{
			if (throwable instanceof UnknownHostException) {
				errMsg = Messages.getString("AbstractSearchMethodExtension.UnknownHost_ErrMsg"); //$NON-NLS-1$
			} 
			else if(throwable instanceof MalformedURLException) {
				errMsg = Messages.getString("AbstractSearchMethodExtension.MalformedURL_ErrMsg"); //$NON-NLS-1$
			} 
			else if(throwable instanceof IOException) {
				errMsg = Messages.getString("AbstractSearchMethodExtension.IOException_ErrMsg"); //$NON-NLS-1$
			}
			else if(throwable instanceof XMLNotValidException) {
				errMsg = Messages.getString("AbstractSearchMethodExtension.InvalidXML_ErrMsg"); //$NON-NLS-1$
			} 
			
			queryFailed(errMsg, throwable.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getAPIDetails(com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription)
	 */
	public APIDetails getAPIDetails(APIShortDescription summary) throws QueryOperationFailedException {
		String detailsXMLData = null;
		APIDetails details = null;
		
		try {		
			detailsXMLData = getAPIDetailsAsXML(summary);			
			details = XMLUtils.xmlToAPIDetails(detailsXMLData.toString());
		} catch (Exception e) {
			DbgUtility.println(DbgUtility.PRIORITY_CLASS, "###Errors on parsing API Details. API Name was: "  //$NON-NLS-1$
					+summary.getName() +", API source was: " +summary.getSource());			 //$NON-NLS-1$
			e.printStackTrace();
			examineAndHandleQueryFailureException(e);
		}		
		
		return details;				
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#runAPIQuery(com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters)
	 */
	public APIShortDescriptionSearchResults runAPIQuery(APIQueryParameters parameters) {
		
		APIShortDescriptionSearchResults results = new APIShortDescriptionSearchResults();

		// Making query for each configured and selected server entry.
		AbstractEntry[] selEntryArr = getEntryStorageInstance().getSelectedEntries();			
		
		// Were any entries configured or selected?
		if(selEntryArr.length == 0){
			results.addSearchError(new QueryOperationFailedException(Messages.getString("AbstractSearchMethodExtension.NoServerEntries_ErrMsg"))); //$NON-NLS-1$
			return results;
		}
		for (int i = 0; i < selEntryArr.length; i++) {
			AbstractEntry entry = selEntryArr[i];
			if(parameters.getSearchString().contains(APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR)){
				//Adding results for results, if there was errors, adding error to results
				try {
					results.addSearchResults(handleMultiEntryQuery(parameters, entry));
				} catch (QueryOperationFailedException e) {
					results.addSearchError(e);
					if(e.getSummarys() != null){
						results.addSearchResults(e.getSummarys());
					}
				}
			}else{
				//Adding results for results, if there was errors, adding error to results				
				try {
					results.addSearchResults(handleSingleEntryQuery(parameters, entry));
				} catch (QueryOperationFailedException e) {
					results.addSearchError(e);
					if(e.getSummarys() != null){
						results.addSearchResults(e.getSummarys());
					}
				}
			}
		}
		
		return results;
	}

	/**
	 * Handles API summary query for a given server entry.
	 * @param parameters Query parameters.
	 * @param srvEntry Entry to be query data from.
	 * @return Collection of API summaries for the given server entry.
	 * @throws QueryOperationFailedException
	 */
	protected abstract Collection<APIShortDescription> handleSingleEntryQuery(APIQueryParameters parameters, AbstractEntry entry) throws QueryOperationFailedException;

	/**
	 * Handles API summary query for a given server entry.
	 * 
	 * Used when search string contains multiple search strings semi-colon (;) separated. 
	 * Eg. multiple headers as semi-colon separated.
	 * 
	 * Should call when <code>APIQueryParameters.getSearchString().contains(APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR)</code>
	 * occurs.
	 * 
	 * @param parameters Query parameters. Where getSearchString() contains semi-colons.
	 * @param srvEntry Entry to be query data from.
	 * @return Collection of API summaries for the given server entry.
	 * @throws QueryOperationFailedException
	 */
	protected abstract Collection<APIShortDescription> handleMultiEntryQuery(APIQueryParameters parameters, AbstractEntry entry) throws QueryOperationFailedException;
		
	
	//
	// Abstract methods to be defined by subclasses.
	//
	
	/**
	 * Gets API Details as XML formatted string for given summary.
	 * Abstract method that must be implemented by all sub classes.
	 * @param summary API summary data to get details for.
	 * @return API Detail XML data as string. 
	 */
	protected abstract String getAPIDetailsAsXML(APIShortDescription summary) throws Exception; 
	
	/**
	 * Handles API summary query for a given server entry.
	 * @param parameters Query parameters.
	 * @param srvEntry Entry to be query data from.
	 * @return API summaries for the given server entry as XML formatted string.
	 * @throws QueryOperationFailedException
	 */
	protected abstract String runAPIQueryForSingleEntryAndReturnXMLString(APIQueryParameters parameters, AbstractEntry entry) throws Exception;
	
	
	/**
	 * Web query job cancellat
	 * @param parameters
	 * @param progressMonitor
	 * @return
	 * @throws JobCancelledByUserException
	 */
		public APIShortDescriptionSearchResults runAPIQuery(APIQueryParameters parameters,IProgressMonitor progressMonitor)throws JobCancelledByUserException {
		

		
		
			APIShortDescriptionSearchResults results = new APIShortDescriptionSearchResults();
		
			// Making query for each configured and selected server entry.
			AbstractEntry[] selEntryArr = getEntryStorageInstance().getSelectedEntries();
			
		
			
			
			// Were any entries configured or selected?
			if(selEntryArr.length == 0){
				results.addSearchError(new QueryOperationFailedException(Messages.getString("AbstractSearchMethodExtension.NoServerEntries_ErrMsg"))); //$NON-NLS-1$
				return results;
			}
			for (int i = 0; i < selEntryArr.length; i++) {
				AbstractEntry entry = selEntryArr[i];
				if(parameters.getSearchString().contains(APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR)){
					//Adding results for results, if there was errors, adding error to results
					try {
						results.addSearchResults(handleMultiEntryQuery(parameters, entry,progressMonitor));
					} catch (QueryOperationFailedException e) {
						results.addSearchError(e);
					}
				}else{
					//Adding results for results, if there was errors, adding error to results				
					try {
						results.addSearchResults(handleSingleEntryQuery(parameters, entry,progressMonitor));
					} catch (QueryOperationFailedException e) {
						results.addSearchError(e);
					}
				}
			}
		
				
			return results;
		}
	
	

		protected abstract Collection<APIShortDescription> handleSingleEntryQuery(APIQueryParameters parameters, AbstractEntry entry,IProgressMonitor monitor) throws JobCancelledByUserException,QueryOperationFailedException;
		protected abstract Collection<APIShortDescription> handleMultiEntryQuery(APIQueryParameters parameters, AbstractEntry entry,IProgressMonitor progressMonitor) throws QueryOperationFailedException,JobCancelledByUserException;
	
}
