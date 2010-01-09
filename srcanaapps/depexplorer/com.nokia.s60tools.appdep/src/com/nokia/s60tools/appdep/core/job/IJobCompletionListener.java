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
 
 
package com.nokia.s60tools.appdep.core.job;



/**
 * Listener interface for object requiring information
 * about successful cache creation.
 */
public interface IJobCompletionListener {
	
	/**
	 * Informs the listener about successful cache index creation.
	 * @param cacheIndexObj Cache index that was just created.
	 */
	public void backgroundJobCompleted(IManageableJob jobObject);

}
