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
 * Data placeholder for import function data.
 */
public class ImportFunctionData extends AbstractFunctionData{

	//
	// These indexes are used to access cache data
	//
	public static final int ORDINAL_FIELD_INDEX = 0; // This field count is to access cache data file
	public static final int NAME_FIELD_INDEX = 1; // This field count is to access cache data file
	public static final int ISVIRTUAL_FIELD_INDEX = 2; // This field count is to access cache data file
	public static final int OFFSET_FIELD_INDEX = 3; // This field count is to access cache data file

	//
	// NOTE: Column indeces must start from zero (0) and
	// the columns must be added in ascending numeric
	// order.
	//
	public static final int ORDINAL_COLUMN_INDEX = 0; // This field count is for UI
	public static final int NAME_COLUMN_INDEX = 1; // This field count is for UI
	public static final int OFFSET_COLUMN_INDEX = 2; // This field count is for UI

	/**
	 * <code>true</code> if function is virtual, otherwise <code>false</code>.
	 */
	private final boolean isVirtual;
	/**
	 * Function offset.
	 */
	private final String functionOffset;

	/**
	 * Constructor.
	 * @param functionOrdinal Function ordinal (1..n when converted into integer).
	 * @param functionName Function name.
	 * @param isVirtual <code>true</code> if function is virtual, otherwise <code>false</code>.
	 * @param functionOffset Function offset.
	 * @throws IllegalArgumentException
	 * @throws ZeroFunctionOrdinalException 
	 */
	public ImportFunctionData(String functionOrdinal, String functionName, boolean isVirtual, String functionOffset) throws IllegalArgumentException, ZeroFunctionOrdinalException{
		super(functionOrdinal, functionName);
		this.isVirtual = isVirtual;
		this.functionOffset = functionOffset;
	}
	
	/**
	 * Checks if function is virtual of not.
	 * @return <code>true</code> in case is virtual, otherwise <code>false</code>.
	 */
	public boolean isVirtual() {
		return isVirtual;
	}

	/**
	 * Gets function offset 
	 * @return the functionOffset
	 */
	public int getFunctionOffset() {
		return Integer.parseInt(functionOffset);
	}

	/**
	 * Getting string representation for function offset. Only valid for virtual functions.
	 * @return the functionOffset
	 */
	public String getFunctionOffsetAsString() {
		// Returning empty string for non-virtual function that do not really have offset.
		if(!isVirtual()){
			return ""; //$NON-NLS-1$
		}
		return functionOffset;
	}

	/**
	 * Sets function name for the given imported function data.
	 * This is needed in order to set function name in case it is resolved
	 * later on by help of the user interaction that provides additional
	 * information.
	 * @param functionName Function name to be set.
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

}
