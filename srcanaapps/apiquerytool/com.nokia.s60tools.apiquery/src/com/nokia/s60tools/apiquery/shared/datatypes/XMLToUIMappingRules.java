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

import java.util.HashMap;
import java.util.Map;

/**
 * Extends field mapping rules class by 
 * including definitions for attributes.
 */
public class XMLToUIMappingRules extends FieldMappingRules {
	
	/**
	 * Storage for attributes that are examined for a certain
	 * element.
	 */
	private Map <String, Map<String, String>> attributeMap;
	
	/**
	 * Storing rules that enforces that rule does not match 
	 * unless the parent element also matches.
	 * It is optional to set this restriction for a rule.
	 */
	private Map <String, String> parentElementRestrictionMap = null;	
	

	/**
	 * Constructor.
	 */
	public XMLToUIMappingRules(){
		super();
		attributeMap = new HashMap<String, Map<String, String>>();
		parentElementRestrictionMap = new HashMap<String, String>();
	}

	/**
	 * Adds a new rule to the mapping rules including
	 * a set of attribute names to be stored.
	 * @param elementNameStr Element name string to map from.
	 * @param mapToStr String to map into.
	 * @param attributes Set of attribute names to be converted.
	 */
	public void addRule(String elementNameStr, String mapToStr, Map<String, String> attributes){
		super.addRule(elementNameStr, mapToStr);
		attributeMap.put(elementNameStr, attributes);
	}

	
	/**
	 * @return the attributeMap
	 */
	public Map<String, Map<String, String>> getAttributeMap() {
		return attributeMap;
	}

	/**
	 * Adds a new rule to the mapping rules.
	 * @param mapFromStr String to map from.
	 * @param mapToStr String to map into.
	 * @param parentElementRestriction Rule matches only if the parent name matches. 
	 */
	public void addRule(String mapFromStr, String mapToStr, String parentElementRestriction){
		this.parentElementRestrictionMap.put(mapFromStr, parentElementRestriction);
		ruleMap.put(mapFromStr, mapToStr);
	}

	/**
	 * Adds a new rule to the mapping rules including
	 * a set of attribute names to be stored.
	 * @param elementNameStr Element name string to map from.
	 * @param mapToStr String to map into.
	 * @param parentElementRestriction Rule matches only if the parent name matches. 
	 * @param attributes Set of attribute names to be converted.
	 */
	public void addRule(String elementNameStr, String mapToStr, 
			            String parentElementRestriction, Map<String, String> attributes){
		super.addRule(elementNameStr, mapToStr);
		this.parentElementRestrictionMap.put(elementNameStr, parentElementRestriction);
		attributeMap.put(elementNameStr, attributes);
	}
	

	/**
	 * Gets parent element restriction for an element.
	 * @param elementNameStr Element name.
	 * @return parent element restriction or <code>null</code> if does not exist.
	 */
	public String getParentElementRestriction(String elementNameStr) {
		return parentElementRestrictionMap.get(elementNameStr);
	}
	
	/**
	 * Check existence of parent element restriction for an element.
	 * @param elementNameStr Element name.
	 * @return <code>true</code> if has restriction, otherwise <code>false</code>.
	 */
	public boolean hasParentElementRestriction(String elementNameStr) {
		return parentElementRestrictionMap.containsKey(elementNameStr);
	}

	/**
	 * Return parent restrictions for conversion rules.
	 * @return Parent restrictions for conversion rules.
	 */
	public Map<String, String> getParentElementRestrictionMap() {
		return parentElementRestrictionMap;
	}

	/**
	 * Get map for attribute header names
	 * @return attribute headers or <code>null</code> if not set
	 */
	public Map <String,String> getAttributeNamesMap(String elementNameStr) {
		return getAttributeMap().get(elementNameStr);
	}
}
