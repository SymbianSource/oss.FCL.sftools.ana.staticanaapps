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

package com.nokia.s60tools.appdep.core.model;

import com.nokia.s60tools.appdep.exceptions.ZeroFunctionOrdinalException;

/**
 * Stores the data for a single exported
 * function.
 */
public class ExportFunctionData extends AbstractFunctionData{
	
	//
	// NOTE: Column indeces must start from zero (0) and
	// the columns must be added in ascending numeric
	// order.
	//
	public static final int ORDINAL_COLUMN_INDEX = 0; // This is used in UI
	public static final int NAME_COLUMN_INDEX = 1; // This is used in UI

	/**
	 * Constructor.
	 * @param functionOrdinal Function ordinal (1..n when converted into integer).
	 * @param functionName Function name.
	 * @throws IllegalArgumentException 
	 * @throws ZeroFunctionOrdinalException 
	 */
	public ExportFunctionData(String functionOrdinal, String functionName) throws IllegalArgumentException, ZeroFunctionOrdinalException{
		super(functionOrdinal, functionName);
	}
	
}
