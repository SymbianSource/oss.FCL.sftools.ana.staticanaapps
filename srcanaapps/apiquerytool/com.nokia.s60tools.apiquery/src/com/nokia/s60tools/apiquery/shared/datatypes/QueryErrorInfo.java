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

/**
 * Simple class for storing error information returned query operations.
 * This is needed because queries are runned inside a runnable and
 * error info is got via try/catch, and the error information usage
 * related needs to be done outside runnable where catch is made. 
 * This class should stay with package private visibility.
 */
public class QueryErrorInfo{		
	/**
	 * Error description is set on failure.
	 */
	private String errorDescription = new String();
	
	/**
	 * Gets error description.
	 * @return the errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * Sets error description.
	 * @param errorDescription the errorDescription to set
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
