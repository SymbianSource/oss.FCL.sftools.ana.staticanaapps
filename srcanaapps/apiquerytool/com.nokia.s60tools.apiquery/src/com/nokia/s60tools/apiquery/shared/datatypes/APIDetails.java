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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores API details information in the format
 * that is directly used by UI components.
 */
public class APIDetails  implements Iterable<APIDetailField>{
	
	/**
	 * Stores detail fields.
	 */
	private Map<String, APIDetailField> details;
		
	/**
	 * Constructor.
	 */
	public APIDetails(){
		//Linked hash map preserves the order of the fields 
		//which is needed in this case. 
		details = new LinkedHashMap<String, APIDetailField> ();
	}
	
	/**
	 * Adds a new field into API details.
	 * @param description Description for the detail. 
	 * @param value	Value for the detail.
	 */
	public void addOrUpdateField(String description, String value){
		APIDetailField oldApiDetail = details.get(description);
		if(oldApiDetail != null){
			oldApiDetail.appendToExistingValue(value);
		}
		else{
			details.put(description, new APIDetailField(description, value));			
		}
	}
	
	/**
	 * Adds a new field into API details.
	 * @param field to be added.
	 */
	public void addOrUpdateField(APIDetailField field){
		APIDetailField oldApiDetail = details.get(field.getDescription());
		if(oldApiDetail != null){
			oldApiDetail.appendToExistingValue(field.getValue());
		}
		else{
			details.put(field.getDescription(), field);			
		}
	}	
	
	/**
	 * Gets the details as an iterator.
	 * @return Iterator containing detail fields.
	 */
	public Iterator<APIDetailField> iterator(){
		return details.values().iterator();		
	}
	
	/**
	 * Gets the keys. Can be used for getting fields by description this.getDetail(key)
	 * @return Iterator containing detail fields.
	 */
	public Set<String> getKeys(){
		return details.keySet();		
	}	
	
	/**
	 * Get value for for the description
	 * @param description 
	 * @return API Detail Field or new APIDetailField with given description and empty ("") value if not exist.
	 * @see com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils 
	 * 		public static variables for description keys.
	 */
	public APIDetailField getDetail(String description){
		APIDetailField detail = details.get(description);
		if(detail != null){
			return detail;
		}else{
			return new APIDetailField(description,""); //$NON-NLS-1$
		}
	}
	
}
