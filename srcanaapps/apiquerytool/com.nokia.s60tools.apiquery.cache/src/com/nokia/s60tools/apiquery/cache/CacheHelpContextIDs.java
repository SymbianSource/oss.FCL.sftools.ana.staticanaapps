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
 
package com.nokia.s60tools.apiquery.cache;


/**
 * IDs for context sensitive help.
 * @see contexts.xml -file IDs links to <code> <context id="<ID>"> </code>
 */
public class CacheHelpContextIDs {
	/**
	 * The plug-in ID. Copy from APIQueryHelpActivator.PLUGIN_ID
	 * to here to avoid runtime dependency to help project 
	 */	 
	private static final String CACHE_HELP_PROJECT_PLUGIN_ID = "com.nokia.s60tools.apiquery.cache.help"; //$NON-NLS-1$
	
	
	/**
	 * ID for Excel Interface Sheets properties -tab.
	 */
    public static final String CACHE_HELP_PROPERTIES_TAB = 
  		  CACHE_HELP_PROJECT_PLUGIN_ID +".CACHE_HELP_PROPERTIES_TAB"; //$NON-NLS-1$
}
