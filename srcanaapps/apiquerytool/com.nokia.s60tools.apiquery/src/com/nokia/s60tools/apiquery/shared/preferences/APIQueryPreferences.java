/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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

package com.nokia.s60tools.apiquery.shared.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;

/**
 * Helper class to use Dependency Explorer preferences. Use this class for accessing DE preferences 
 * instead of accessing directly through  {@link org.eclipse.jface.util.IPropertyChangeListener.IPreferenceStore}.
 */
public class APIQueryPreferences {
	


	/**
	 * Get "Show only API names" value
	 * @return <code>true</code> if preference "Show only API names" is cheked, <code>false</code> otherwise.
	 */
	public static boolean getShowOnlyAPINames(){
		
		String value = APIQueryPlugin.getPrefsStore().getString(APIQueryPreferenceConstants.SHOW_ONLY_APINAMES);
		if(value != null && value.equalsIgnoreCase(APIQueryPreferenceConstants.TRUE)){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * Set "Show only API names" value 
	 * @param isDontAskAgainChecked <code>true</code> if "Show only API names" is selected, <code>false</code> otherwise.
	 */
	public static void setShowOnlyAPINames(boolean isShowOnlyAPINamesChecked){
		
		IPreferenceStore store = APIQueryPlugin.getPrefsStore();

		if(isShowOnlyAPINamesChecked){
			store.setValue(APIQueryPreferenceConstants.SHOW_ONLY_APINAMES, APIQueryPreferenceConstants.TRUE);
		}else{
			store.setValue(APIQueryPreferenceConstants.SHOW_ONLY_APINAMES, APIQueryPreferenceConstants.FALSE);
		}
	}
	
	/**
	 * Get "Show Data Source in results" value
	 * @return <code>true</code> if preference "Show Data Source in results" is cheked, <code>false</code> otherwise.
	 */
	public static boolean getShowDataSourceInResults(){
		
		boolean value = APIQueryPlugin.getPrefsStore().getBoolean(APIQueryPreferenceConstants.SHOW_DATASOURCE_IN_RESULTS);
		return value;
	
	}
	
	/**
	 * Set "Show Data Source in results" value 
	 * @param isDontAskAgainChecked <code>true</code> if "Show Data Source in results" is selected, <code>false</code> otherwise.
	 */
	public static void setShowDataSourceInResults(boolean isShowDataSourceInResultsChecked){
		
		IPreferenceStore store = APIQueryPlugin.getPrefsStore();
		store.setValue(APIQueryPreferenceConstants.SHOW_DATASOURCE_IN_RESULTS, isShowDataSourceInResultsChecked);
	}	
	
	
}
