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
 
 
package com.nokia.s60tools.appdep.exceptions;

/**
 * Thrown whenever cache files is not found.
 */
public class CacheFileDoesNotExistException extends Exception {
		
	/**
	 * Serial version UID.
	 */
	static final long serialVersionUID = -9121983872766182745L;

	/**
	 * Default constructor usage if forbidden.
	 */
	@SuppressWarnings("unused")
	private CacheFileDoesNotExistException(){
		super();
	}

	/**
	 * Constructor with attached message that should
	 * inform catcher about what cache file was missing.
	 * @param message Message informing catcher what cache file was missing. 
	 */
	public CacheFileDoesNotExistException( String message ){
		super(message);
	}

}
