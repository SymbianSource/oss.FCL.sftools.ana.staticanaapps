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
 
package com.nokia.s60tools.apiquery.shared.job;

/**
 * {@link Exception} class for Job cancelled by User.
 */
public class JobCancelledByUserException extends Exception {

	public JobCancelledByUserException(String msg) {
		super(msg);
	}


	private static final long serialVersionUID = -235428303120175424L;

}
