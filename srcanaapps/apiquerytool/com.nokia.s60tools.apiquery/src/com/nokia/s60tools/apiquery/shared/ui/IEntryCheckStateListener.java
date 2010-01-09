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
 
package com.nokia.s60tools.apiquery.shared.ui;

/**
 * Notifies listeners when ever either all
 * or none of the items are checke in the UI.
 */
public interface IEntryCheckStateListener {
	
	 /**
	 * All entries were checked. 
	 */
	public void allEntriesChecked();
	
	 /**
	 * All entries were unchecked.
	 */
	public void allEntriesUnchecked();

	/**
	 * Some entries has been checked.
	 */
	public void someEntriesChecked();
}
