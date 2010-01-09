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
import java.util.Set;

import com.nokia.s60tools.apiquery.shared.resources.Messages;

/**
 * Stores field mapping rules that can be used to
 * map API detail field descriptions to XML 
 * and vice versa.
 */
public class FieldMappingRules {

	/**
	 * Storage for the mapping rules.
	 */
	Map <String, String> ruleMap;
	
	/**
	 * Constructor
	 */
	public FieldMappingRules(){
		ruleMap = new HashMap<String, String>();
	}
	
	/**
	 * Adds a new rule to the mapping rules.
	 * @param mapFromStr String to map from.
	 * @param mapToStr String to map into.
	 */
	public void addRule(String mapFromStr, String mapToStr){
		ruleMap.put(mapFromStr, mapToStr);
	}
	
	/**
	 * Gets the string that was mapped into the given parameter string.
	 * @param keyStr Key string to ask mapped result for.
	 * @return Returns result string or throws <code>IllegalArgumentException</code>
	 *         if no .
	 * @throws java.lang.IllegalArgumentException
	 * @see java.lang.IllegalArgumentException
	 */
	public String mapFrom(String keyStr){
		String resultStr = ruleMap.get(keyStr);
		if(resultStr == null){
			throw new IllegalArgumentException(Messages.getString("FieldMappingRules.NoMappingRule_ErrMsg") + keyStr); //$NON-NLS-1$
		}
		return resultStr;
	}
	
	/**
	 * Returns key set that this field mapping rule instance 
	 * has mapping rules for.
	 * @return Set of String key values.
	 */
	public Set<String> getMapFromKeySet(){
		return ruleMap.keySet();
	}
}
