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
 * Thrown whenever trying to set invalid tool related settings into tool settings.
 */
public class InvalidCmdLineToolSettingException extends Exception {
		
	/**
	 * Serial version UID.
	 */
	static final long serialVersionUID = -2523621839223100246L;

	/**
	 * Default constructor. 
	 */
	public InvalidCmdLineToolSettingException(){
		super();
	}

	/**
	 * Constructor with attached message.
	 * @param message
	 */
	public InvalidCmdLineToolSettingException( String message ){
		super(message);
	}

}
