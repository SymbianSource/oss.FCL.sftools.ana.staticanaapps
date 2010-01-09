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
 

package com.nokia.s60tools.apiquery.shared.datatypes.config;

/**
 * Interface for the services that needs to know if 
 * configuration has been changed. 
 */
public interface IConfigurationChangedListener {
	
	/**
	 * Entry  added event type. 
	 */
	public static final int ENTRY_ADDED = 0;
	
	/**
	 * Entry  modified event type. 
	 */
	public static final int ENTRY_MODIFIED = 1;
	
	/**
	 * Entry removed event type. 
	 */
	public static final int ENTRY_REMOVED = 2;
	
	/**
	 * Entry selected event type. 
	 */
	public static final int ENTRY_CHECKED = 3;
	
	/**
	 * Entry deselected event type. 
	 */
	public static final int ENTRY_UNCHECKED = 4;
	
	/**
	 * All entries selected event type. 
	 */
	public static final int ALL_ENTRIES_CHECKED = 5;

	/**
	 * All entries deselected event type. 
	 */
	public static final int ALL_ENTRIES_UNCHECKED = 6;
	
	/**
	 * All Entrys removed event type. 
	 */
	public static final int ALL_ENTRYS_REMOVED = 7;

	/**
	 * All Entrys updated event type. 
	 */
	public static final int ALL_ENTRYS_UPDATED = 8;	
	
	/**
	 * All selected Entrys removed event type. 
	 */
	public static final int ALL_SELECTED_ENTRYS_REMOVED = 9;	
	
	/**
	 * Configuration change notification method.
	 * @param eventType One of the event types define in this interface.
	 */
	public void configurationChanged(int eventType);
}
