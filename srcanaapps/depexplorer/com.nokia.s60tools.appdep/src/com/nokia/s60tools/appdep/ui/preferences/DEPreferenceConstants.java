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


package com.nokia.s60tools.appdep.ui.preferences;

/**
 * Class for storing keys to preferences.
 */
public class DEPreferenceConstants {
	
	/**
	 * <code>true</code> value
	 */
	public static final String TRUE = "true"; //$NON-NLS-1$
	
	/**
	 * <code>false</code> value
	 */
	public static final String FALSE = "false"; //$NON-NLS-1$
	
	/**
	 * Separator for prefix search order components.
	 */
	public static final String PREFIX_SEARCH_ORDER_SEPARATOR = ";"; //$NON-NLS-1$

	/**
	 * preference DB key for prefix search order.
	 */
	public final static String DE_PREFIX_SEARCH_ORDER_VALUES = "dePrefixSeachOrderValues"; //$NON-NLS-1$
	
	/**
	 * preference DB key for "don't ask again" in Search dialog for confirmation about set as new root component. 
	 */
	public final static String DE_DONT_ASK_SET_AS_NEW_ROOT_FROM_SEARCH = "deDontAskSetAsNewRootFromSearch";//$NON-NLS-1$

}
