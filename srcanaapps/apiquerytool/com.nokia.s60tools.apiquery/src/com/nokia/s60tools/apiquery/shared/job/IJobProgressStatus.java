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


public interface IJobProgressStatus {
	/**
	 * Reports progress of a batch process like
	 * execution. It depends on the invoked command
	 * if any progress information can be gained. 
	 * @param percentage Progress percentage.
	 * @param processedItem Item that is currently under processing.
	 * @throws JobCancelledByUserException 
	 */
	public void progress(int percentage, String processedItem) throws JobCancelledByUserException;

	/**
	 * Checks if job has been cancelld by the user.
	 * @return Returns <code>true</code> if jobs has been cancelled by user, 
	 *         otherwise <code>false</code>.
	 */
	public boolean isCanceled();
}
