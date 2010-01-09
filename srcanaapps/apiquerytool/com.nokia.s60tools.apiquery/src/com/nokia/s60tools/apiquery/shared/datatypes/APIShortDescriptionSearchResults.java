/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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
import java.util.Collection;
import java.util.Iterator;

import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;

/**
 * Class for holding search results and search errors. 
 */
public class APIShortDescriptionSearchResults {
	
	public APIShortDescriptionSearchResults(){
		searchResults = new ArrayList<APIShortDescription>();
		searchErrors = new ArrayList<QueryOperationFailedException>();
	}
	
	/**
	 * Search results
	 */
	private Collection<APIShortDescription> searchResults = null;
	
	/**
	 * Search errors
	 */
	private Collection<QueryOperationFailedException> searchErrors = null;

	/**
	 * @return the searchResults
	 */
	public Collection<APIShortDescription> getSearchResults() {
		return searchResults;
	}

	/**
	 * @param searchResults the searchResults to set
	 */
	public void addSearchResults(Collection<APIShortDescription> searchResults) {
		//Can't add null to results
		if(searchResults != null){
			this.searchResults.addAll(searchResults);
		}
	}

	/**
	 * Get errors occurred when queries was executed 
	 * @return the searchErrors
	 */
	public Collection<QueryOperationFailedException> getSearchErrors() {
		return searchErrors;
	}

	/**
	 * Add one search error.
	 * @param searchError
	 */
	public void addSearchError(QueryOperationFailedException searchError) {

		if(searchError != null){
			this.searchErrors.add(searchError);
		}
	}
	
	/**
	 * Check if there was some errors whit queries.
	 * @return <code>true</code> if there was some errors <code>false</code> otherwise. 
	 */
	public boolean hasErrors(){
		return !this.searchErrors.isEmpty();
	}
	
	/**
	 * Get all error messages. One message takes one line, so if there is many errors there is as many lines of text also.
	 * @return Errors as list or empty string if there was not any.
	 */
	public String getErrorMessages(){
		if(!hasErrors()){
			return new String();
		}
		else{
			StringBuffer errors = new StringBuffer();
			int count = searchErrors.size();
			int i = 0;
			for (Iterator<QueryOperationFailedException> iterator = searchErrors.iterator(); iterator.hasNext();) {
				QueryOperationFailedException err = (QueryOperationFailedException) iterator.next();
				errors.append(err.getMessage());
				if(count > 1 && i < (count - 1)){
					errors.append("\n");//$NON-NLS-1$
				}
				i++;
			}
			return errors.toString();
		}
	}

}
