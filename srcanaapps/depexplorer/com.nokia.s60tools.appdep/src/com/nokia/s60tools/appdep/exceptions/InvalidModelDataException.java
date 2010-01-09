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

package com.nokia.s60tools.appdep.exceptions;

/**
 * Thrown when invalid data is tried to store into a model.
 */
public class InvalidModelDataException extends Exception {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5051997802316898070L;

	/**
	 * Constructor.
	 * @param message Informative message about the error causing the exception.
	 */
	public InvalidModelDataException(String message){
		super(message);
	}
	
}
