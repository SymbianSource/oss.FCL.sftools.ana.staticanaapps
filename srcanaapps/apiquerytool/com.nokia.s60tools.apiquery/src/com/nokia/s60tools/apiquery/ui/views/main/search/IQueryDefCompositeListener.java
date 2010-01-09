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
 
package com.nokia.s60tools.apiquery.ui.views.main.search;

/**
 * Defines interface for listening query action events
 * from query search string definition UI composite.
 */
interface IQueryDefCompositeListener {
	
	/**
	 * Notifies listener that the query string has been modified.
	 * @param searchString Search string written by the user.
	 */
	public void queryModified(String searchString);
	
	/**
	 * Notifies listener that the query is started by the user.
	 * @param searchString Search string given by the user.
 	 * @param useExactMatch <code>true</code> if search string will be searched with exact match 
	 * instead of contains.
	 */
	public void queryStarted(String searchString, boolean useExactMatch);
}
