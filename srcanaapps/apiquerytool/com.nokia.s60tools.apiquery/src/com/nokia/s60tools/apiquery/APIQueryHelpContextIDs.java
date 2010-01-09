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
 
package com.nokia.s60tools.apiquery;


/**
 * IDs for context sensitive help.
 * @see contexts.xml -file IDs links to <code> <context id="<ID>"> </code>
 */
public class APIQueryHelpContextIDs {

	/**
	 * The plug-in ID. Copy from APIQueryHelpActivator.PLUGIN_ID
	 * to here to avoid runtime dependency to help project 
	 */	 
	private static final String API_QUERY_HELP_PROJECT_PLUGIN_ID = "com.nokia.s60tools.apiquery.help";//$NON-NLS-1$
	
  
	/**
	 * Context sensitive help id to API Query search tab
	 */
    public static final String API_QUERY_HELP_SEARCH_TAB = 
  		  API_QUERY_HELP_PROJECT_PLUGIN_ID +".API_QUERY_HELP_SEARCH_TAB";//$NON-NLS-1$
    
    
	/**
	 * Context sensitive help id to API Query properties tab
	 */
    public static final String API_QUERY_HELP_PROPERTIES_TAB = 
  		  API_QUERY_HELP_PROJECT_PLUGIN_ID +".API_QUERY_HELP_PROPERTIES_TAB";//$NON-NLS-1$
    


        

}
