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
 
package com.nokia.s60tools.appdep;

/**
 * IDs for context sensitive help.
 * @see contexts.xml -file IDs links to <code> <context id="<ID>"> </code>
 */
public class AppDepHelpContextIDs {
	/**
     * Help plug-in ID the context sensitive help is applied to.
     */	 
    private static final String APPDEP_HELP_PROJECT_PLUGIN_ID = 
                                        "com.nokia.s60tools.appdep.help"; //$NON-NLS-1$
	
    //
    // Context-sensitive help IDs
    //
    
    public static final String APPDEP_MAIN_VIEW = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID +".APPDEP_MAIN_VIEW"; //$NON-NLS-1$

    public static final String APPDEP_COMPONENT_LIST_VIEW = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_COMPONENT_LIST_VIEW"; //$NON-NLS-1$
  
    public static final String APPDEP_IMPORTED_FUNCTIONS = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_IMPORTED_FUNCTIONS"; //$NON-NLS-1$

    public static final String APPDEP_EXPORTED_FUNCTIONS = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_EXPORTED_FUNCTIONS"; //$NON-NLS-1$

    public static final String APPDEP_PROPERTIES = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_PROPERTIES"; //$NON-NLS-1$
  
    public static final String APPDEP_DIALOG_ADD_SIS_FILES = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_DIALOG_ADD_SIS_FILES"; //$NON-NLS-1$

    public static final String APPDEP_WIZARD_PAGE_SDK_SELECT = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_WIZARD_PAGE_SDK_SELECT"; //$NON-NLS-1$

    public static final String APPDEP_WIZARD_PAGE_BUILD_TARGET_SELECT = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_WIZARD_PAGE_BUILD_TARGET_SELECT"; //$NON-NLS-1$

    public static final String APPDEP_WIZARD_PAGE_CACHE_GEN_OPT = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_WIZARD_PAGE_CACHE_GEN_OPT"; //$NON-NLS-1$

    public static final String APPDEP_WIZARD_PAGE_COMP_SELECT = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_WIZARD_PAGE_COMP_SELECT"; //$NON-NLS-1$

    public static final String APPDEP_FIND_DIALOG = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_FIND_DIALOG"; //$NON-NLS-1$
  
    public static final String APPDEP_LOCATE_COMPONENT_DIALOG = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_LOCATE_COMPONENT_DIALOG"; //$NON-NLS-1$

    public static final String APPDEP_SEARCH_DIALOG = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_SEARCH_DIALOG"; //$NON-NLS-1$
  
    public static final String APPDEP_PREF_PAGE = 
		  APPDEP_HELP_PROJECT_PLUGIN_ID 
		  +".APPDEP_PREF_PAGE"; //$NON-NLS-1$  
    
}
