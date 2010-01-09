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
 
package com.nokia.s60tools.apiquery.shared.datatypes.config;

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
public class DuplicateEntryException extends Exception {

	/**
	 * Serial versio UID.
	 */
	private static final long serialVersionUID = -4168668065952326532L;

	/**
	 * Default constructor is hidden from the user.
	 */
	@SuppressWarnings("unused")
	private DuplicateEntryException(){
		super();
	}

	/**
	 * Only publicly available constructor is one with detailed error information.
	 * @param errorMessage Detailed information about the error that caused the exception.
	 */
	public DuplicateEntryException(String errorMessage){
		super(errorMessage);
	}
}
