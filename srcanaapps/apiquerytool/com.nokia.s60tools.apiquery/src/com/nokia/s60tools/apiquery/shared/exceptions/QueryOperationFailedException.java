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
 
package com.nokia.s60tools.apiquery.shared.exceptions;

import java.util.Collection;

import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;

/**
 * This exception might be raised by the search methods
 * when query operation fails due to some reason. 
 * 
 * There might be several reasons for the failure such as:
 * 
 * - Missing or invalid configuration information to run 
 *   the query operation.
 * - Required data is not availabe (due to network restrictions, cached data
 *   is missing from the local disk etc.) 
 */
public class QueryOperationFailedException extends Exception {

	/**
	 * Serial versio UID.
	 */
	private static final long serialVersionUID = -8124615355279044581L;
	
	/**
	 * API Query summarys for search if those was gathered before error occurred
	 */
	private Collection<APIShortDescription> summary = null;

	/**
	 * Default constructor is hidden from the user.
	 */
	@SuppressWarnings("unused")
	private QueryOperationFailedException(){
		super();
	}

	/**
	 * Only publicly available constructor is one with detailed error information.
	 * @param errorMessage Detailed information about the error that caused the exception.
	 */
	public QueryOperationFailedException(String errorMessage){
		super(errorMessage);
	}

	/**
	 * Set summarys for this exeption
	 * @param summary
	 */
	public void setSummarys(Collection<APIShortDescription> summary) {
		this.summary = summary;
	}
	/**
	 * @return the summarys or <code>null</code> if not set
	 */
	public Collection<APIShortDescription> getSummarys() {
		return summary;
	}	
}
