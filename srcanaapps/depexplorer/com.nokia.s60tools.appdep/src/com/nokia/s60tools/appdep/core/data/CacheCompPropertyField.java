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
 
 
package com.nokia.s60tools.appdep.core.data;

/**
 * Constants for indexing component property line
 * in cache file.
 */
public class CacheCompPropertyField {	
	public static final int DIRECTORY_ARR_INDX = 0; 
	public static final int FILENAME_ARR_INDX = 1;
	public static final int BINARY_FORMAT_ARR_INDX = 2;
	public static final int UID1_ARR_INDX = 3;
	public static final int UID2_ARR_INDX = 4;
	public static final int UID3_ARR_INDX = 5;
	public static final int SECURE_ID_ARR_INDX = 6;
	public static final int VENDOR_ID_ARR_INDX = 7;
	public static final int CAPABILITIES_ARR_INDX = 8;
	public static final int MIN_HEAP_SIZE_ARR_INDX = 9;
	public static final int MAX_HEAP_SIZE_ARR_INDX = 10;
	public static final int STACK_SIZE_ARR_INDX = 11;
	public static final int CACHE_TIMESTAMP_ARR_INDX = 12;	
	public static final int DLL_REF_TABLE_COUNT_ARR_INDX = 13;
	
	/**
	 * Gets the component property line field count.
	 * 
	 * This method should be up-to-date with corresponding field
	 * indices defined above of this method.
	 * @return The amount of fields in component property line in cache file.
	 */
	public static int getCompPropertyFieldCount(){
		return 14;
	}
	
}
