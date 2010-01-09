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
 * Stores a single field of information
 * for API details.
 */
public class APIDetailField {

	public static final String VALUE_FIELD_SEPARATOR = ", "; //$NON-NLS-1$

	/**
	 * Description for the detail field.
	 */
	private final String description;
	/**
	 * Value for the detail field.
	 */
	private String value;

	/**
	 * Constructor.
	 * @param description Description for the detail field.
	 * @param value Value for the detail field.
	 */
	public APIDetailField(String description, String value){
		this.description = description;
		this.value = value;
		
	}

	/**
	 * Gets the description of the field.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the value of the field.
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Appends new data to existing value string.
	 * @param strToAppend String to be appended to existing value string.
	 */
	public void appendToExistingValue(String strToAppend) {
		value = value + VALUE_FIELD_SEPARATOR + strToAppend;
	}

}
