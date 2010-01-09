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
 
package com.nokia.s60tools.apiquery.shared.exceptions;

/**
 * Thrown when invalid XML data is encountered.
 */
public class XMLNotValidException extends Exception {

	private static final long serialVersionUID = -3037053770134709611L;
	private String fileName = null;
	
	/**
	 * Private hidden constructor.
	 */
	@SuppressWarnings("unused")
	private XMLNotValidException(){
		super();
	}
	/**
	 * Public constructor coming always with descriptive message.
	 */
	public XMLNotValidException(String message){
		super(message);
	}

	/**
	 * Public constructor coming always with descriptive message.
	 */
	public XMLNotValidException(String message, String fileName){
		super(message);
		this.fileName = fileName;
	}

	/**
	 * Get file name
	 * @return file name or <code>null</code> if not set.
	 */
	public String getFileName() {
		return fileName;
	}	
	
}
