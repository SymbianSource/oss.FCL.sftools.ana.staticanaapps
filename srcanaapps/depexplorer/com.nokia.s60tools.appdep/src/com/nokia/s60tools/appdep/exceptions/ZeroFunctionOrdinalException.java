/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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


package com.nokia.s60tools.appdep.exceptions;

/**
 * Thrown when zero ordinal is encountered for a function
 * when loading cache file.
 */
public class ZeroFunctionOrdinalException extends RuntimeException {
	
	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = -8929030211801349973L;
	/**
	 * Name of the function having zero ordinal
	 */
	private final String functionName;

	/**
	 * Constructor.
	 */
	public ZeroFunctionOrdinalException(String functionName){
		super();
		this.functionName = functionName;
	}

	/**
	 * Gets name of the function having zero ordinal.
	 * @return name of the function having zero ordinal.
	 */
	public String getFunctionName() {
		return functionName;
	}
	
}
