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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.nokia.s60tools.appdep.plugin.AppDepPlugin;

/**
 * Helper class to use Dependency Explorer preferences. Use this class for accessing DE preferences 
 * instead of accessing directly through  {@link org.eclipse.jface.util.IPropertyChangeListener.IPreferenceStore}.
 */
public class DEPreferences {
	
	/**
	 * Get search order prefixes
	 * @return prefixes in order they appear in preference page, or <code>null</code> if none found. 
	 */
	public static String[] getSearchOrderPrefixs(){
		String values = AppDepPlugin.getPrefsStore().getString(DEPreferenceConstants.DE_PREFIX_SEARCH_ORDER_VALUES);
		String [] prefixs = null;
		if(values != null){
			prefixs = values.split(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR);
		}
		return prefixs;
	}
	
	/**
	 * Gets search order prefixes
	 * @return prefixes in order they appear in preference page, or empty list if none found. 
	 */
	public static List<String> getSearchOrderPrefixsList(){
		ArrayList<String> prefixList = new ArrayList<String>();
		String values = AppDepPlugin.getPrefsStore().getString(DEPreferenceConstants.DE_PREFIX_SEARCH_ORDER_VALUES);
		//Adding values only if there is something to add.
		if(values != null && values.trim().length() > 0){
			String [] prefixs = null;
			prefixs = values.split(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR);
			for (int i = 0; i < prefixs.length; i++) {
				prefixList.add(prefixs[i]);
			}
			
		}
				
		return prefixList;
	}	

	/**
	 * Gets "Don't ask again" value for Search set as new root confirmation.
	 * @return <code>true</code> if preference "don't ask again" is cheked, <code>false</code> otherwise.
	 */
	public static boolean getDontAskSetAsNewRootFromSearch(){
		
		String value = AppDepPlugin.getPrefsStore().getString(DEPreferenceConstants.DE_DONT_ASK_SET_AS_NEW_ROOT_FROM_SEARCH);
		if(value != null && value.equalsIgnoreCase(DEPreferenceConstants.TRUE)){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * Sets "Don't ask again" value for Search set as new root confirmation.
	 * @param isDontAskAgainChecked <code>true</code> if don't ask again is selected, <code>false</code> otherwise.
	 */
	public static void setDontAskAtainAsNewRootFromSearch(boolean isDontAskAgainChecked){
		
		IPreferenceStore store = AppDepPlugin.getPrefsStore();

		if(isDontAskAgainChecked){
			store.setValue(DEPreferenceConstants.DE_DONT_ASK_SET_AS_NEW_ROOT_FROM_SEARCH, DEPreferenceConstants.TRUE);
		}else{
			store.setValue(DEPreferenceConstants.DE_DONT_ASK_SET_AS_NEW_ROOT_FROM_SEARCH, DEPreferenceConstants.FALSE);
		}
	}
	
}
