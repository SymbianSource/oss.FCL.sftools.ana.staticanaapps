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
 * Thrown when trying to access cache index before it is created 
 * and updated with up-to-date data.
 */
public class CacheIndexNotReadyException extends Exception {
		
	/**
	 * Serial version UID.
	 */
	static final long serialVersionUID = -779456382616245857L;

	/**
	 * Default constructor. 
	 */
	public CacheIndexNotReadyException(){
		super();
	}

	/**
	 * Constructor with attached message.
	 * @param message Detailed message for the user.
	 */
	public CacheIndexNotReadyException( String message ){
		super(message);
	}

}
