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
 
package com.nokia.s60tools.apiquery.shared.util.xml;

import java.util.Map;

/**
 * Stores data for a single XML element that
 * has simple string content and no sub elements.
 * Only attribute data is stored.
 */
public class XMLElementData {

	/**
	 * Content for the element.
	 */
	private final String elementContent;
	
	/**
	 * Name of the element.
	 */
	private final String elementName;

	/**
	 * Attribute data for the element.
	 */
	private final Map<String, String> attrData;

	public XMLElementData(String elementName, String elementContent, Map<String, String> attrData){
		this.elementName = elementName;
		this.elementContent = elementContent;
		this.attrData = attrData;		
	}

	/**
	 * @return the elementContent
	 */
	public String getElementContent() {
		return elementContent;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}
	
	/**
	 * @return the attrData
	 */
	public Map<String, String> getAttributes() {
		return attrData;
	}	
}
