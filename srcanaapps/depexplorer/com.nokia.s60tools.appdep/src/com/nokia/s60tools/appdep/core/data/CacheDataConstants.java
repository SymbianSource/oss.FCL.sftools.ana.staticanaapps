/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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
 * Declares and defines all cache data format related constants
 * that are needed by multiple classes. 
 */
public class CacheDataConstants {

	/**
	 * Separator which is used to divide different data fields
	 * in a single line of cache data.
	 */
	public static final String CACHE_FIELD_SEPARATOR = "|"; //$NON-NLS-1$
	/**
	 * Each non-corrupted cache file should end with the following string.
	 */
	public static final String CACHE_FILE_END_MARK = "#end"; //$NON-NLS-1$
	/**
	 * Header containing version info is separated with this character.
	 * The header line is of format:
	 * 
	 *  appdep dependencies cache version: 101
	 */
	public static final String CACHE_VERSION_INFO_SEPARATOR = ":"; //$NON-NLS-1$
	/**
	 * Virtual flag for imported function is set to 'true' when the values is the following. 
	 */
	public static final String VIRTUAL_INDEX_FIELD_IS_TRUE_VALUE = "1"; //$NON-NLS-1$
	/**
	 * Indices to import function properties.
	 */
	public static final int FUNC_ORDINAL_INDEX = 0;
	public static final int FUNC_NAME_INDEX = 1;
	public static final int FUNC_IS_VIRTUAL_INDEX = 2;
	public static final int FUNC_OFFSET_INDEX = 3;
	/**
	 * If function name has not been resolved, it is shown with the following string instance.
	 */
	public static final String FUNC_NAME_NOT_RESOLVED = "Import library not found!"; //$NON-NLS-1$

}
