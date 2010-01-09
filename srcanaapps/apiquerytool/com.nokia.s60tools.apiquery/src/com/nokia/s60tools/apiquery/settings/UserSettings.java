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
 
 
package com.nokia.s60tools.apiquery.settings;

import java.util.ArrayList;

import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;
import com.nokia.s60tools.util.debug.DbgUtility;




/**
 * Singleton class that is created on plugin
 * startup, and is kept active as long as plugin is active.
 * 
 * The purpose of this class is store run-time instance
 * of active user settings.
 */
public class UserSettings {

	/**
	 * Singleton instance.
	 */
	static private UserSettings instance = null;

	/**
	 * Currently selected search method.
	 */
	private ISearchMethodExtensionInfo currentlySelectedSearchMethodInfo = null;
	
	/**
	 * Listeners that can be attached for listening the changes in the user settings.
	 */
	private ArrayList<IUserSettingsListener> settingsListeners = new ArrayList<IUserSettingsListener>();
	
	/**
	 * Public Singleton instance accessor.
	 * @return Returns instance of this singleton class-
	 */
	public static UserSettings getInstance(){
		if( instance == null ){
			instance = new UserSettings();
		}
		return instance;		
	}	
	
	/**
	 * Private default constructor.
	 */
	private UserSettings() {
		DbgUtility.println(DbgUtility.PRIORITY_CLASS, "-- <<create>> --> " + getClass().getName()); //$NON-NLS-1$
	}

	/**
	 * Gets the search method that is currently selected by user.
	 * @return Returns the currentlySelectedSearchMethodInfo, or
	 *         <code>null</code> if not set.
	 */
	public ISearchMethodExtensionInfo getCurrentlySelectedSearchMethodInfo() {
		return currentlySelectedSearchMethodInfo;
	}

	/**
	 * Gets the currently selected search method.
	 * @return The currently selected search method.
	 */
	public ISearchMethodExtension getCurrentlySelectedSearchMethod() {
		ISearchMethodExtensionInfo currSelExtInfo =  getInstance().getCurrentlySelectedSearchMethodInfo();
		String id = currSelExtInfo.getId();
		ISearchMethodExtension currSelExt = SearchMethodExtensionRegistry.getInstance().getById(id);
		return currSelExt;
	}	
	
	/**
	 * Sets the search method that is currently selected by user.
	 * @param currentlySelectedSearchMethodInfo The currentlySelectedSearchMethodInfo to set.
	 */
	public void setCurrentlySelectedSearchMethodInfo(
			ISearchMethodExtensionInfo currentlySelectedSearchMethodInfo) {
		this.currentlySelectedSearchMethodInfo = currentlySelectedSearchMethodInfo;
		settingsChanged();
	}
	
	/**
	 * Adds a user settings listener.
	 * @param listener Setting listener to add.
	 */
	public void addUserSettingListener(IUserSettingsListener listener){
		settingsListeners.add(listener);
	}
	
	/**
	 * Removes a user settings listener.
	 * @param listener Setting listener to remove.
	 */
	public void removeUserSettingListener(IUserSettingsListener listener){
		settingsListeners.remove(listener);
	}
	
	/**
	 * Notifies listeners that the settings has been changed.
	 */
	public void settingsChanged(){
		for (IUserSettingsListener listener : settingsListeners) {
			listener.userSettingsChanged();
		}
	}
}
