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
 
 
package com.nokia.s60tools.appdep.ui.views.data;

/**
 * Stores simple property description/propertyValue pairs.
 */
public class PropertyData {
	
	/**
	 * Property description.
	 */
	private final String propertyDescription;
	/**
	 * Property value.
	 */
	private final String propertyValue;

	/**
	 * Constructor.
	 * @param propertyDescription Property description.
	 * @param propertyValue Property value.
	 */
	public PropertyData(String propertyDescription, String propertyValue){
		this.propertyDescription = propertyDescription;
		this.propertyValue = propertyValue;
		
	}

	/**
	 * Gets property description.
	 * @return Returns the propertyDescription.
	 */
	public String getPropertyDescription() {
		return propertyDescription;
	}

	/**
	 * Gets property value.
	 * @return Returns the propertyValue.
	 */
	public String getPropertyValue() {
		return propertyValue;
	}
}
