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
 
 
package com.nokia.s60tools.apiquery.shared.searchmethodregistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;




/**
 * Singleton class that is created on plugin
 * startup, and is kept active as long as plugin is active.
 * 
 * The purpose of this class is to store search method
 * extensions that were found from the system.
 */
public class SearchMethodExtensionRegistry {

	/**
	 * Singleton instance.
	 */
	static private SearchMethodExtensionRegistry instance = null;

	/**
	 * Listeners interested in job completions operations.
	 */
	private static Map<String, ISearchMethodExtension> extensions = Collections.synchronizedMap(new HashMap<String, ISearchMethodExtension>());
	
	/**
	 * Public Singleton instance accessor.
	 * @return Returns instance of this singleton class-
	 */
	public static SearchMethodExtensionRegistry getInstance(){
		if( instance == null ){
			instance = new SearchMethodExtensionRegistry();
		}
		return instance;		
	}	
	
	/**
	 * Private default constructor.
	 */
	private SearchMethodExtensionRegistry() {
		DbgUtility.println(DbgUtility.PRIORITY_CLASS, "-- <<create>> --> " + getClass().getName()); //$NON-NLS-1$
	}	
	
	/**
	 * Add a new Search Method
	 * @param extension
	 */
	public void addExtension(ISearchMethodExtension extension){
		synchronized(extensions){
			ISearchMethodExtensionInfo info = extension.getExtensionInfo();
			String id = info.getId();
			ISearchMethodExtension possibleDuplicate = extensions.get(id);			
			if( possibleDuplicate != null ){				
				String errMsg = Messages.getString("SearchMethodExtensionRegistry.Failed_To_Add_Ext_ConsoleMsg")  //$NON-NLS-1$
						+ Messages.getString("SearchMethodExtensionRegistry.Extension_Prefix") //$NON-NLS-1$
						+ id
						+ Messages.getString("SearchMethodExtensionRegistry.Is_Registered_Already_MsgPostfix"); //$NON-NLS-1$
				APIQueryConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
				throw new RuntimeException(errMsg);
			}
			extensions.put(id, extension);
		} // synchronized
	}
	
	/**
	 * Remove a Search Method
	 * @param extension
	 */
	public void removeExtension(ISearchMethodExtension extension){
		synchronized(extensions){
			ISearchMethodExtensionInfo info = extension.getExtensionInfo();
			String id = info.getId();
			ISearchMethodExtension possibleDuplicate = extensions.get(id);			
			if( possibleDuplicate == null ){				
				String errMsg = Messages.getString("SearchMethodExtensionRegistry.Failed_To_Remove_Ext_ConsoleMsg")  //$NON-NLS-1$
					+ Messages.getString("SearchMethodExtensionRegistry.Extension_Prefix") //$NON-NLS-1$
					+ id
					+ Messages.getString("SearchMethodExtensionRegistry.Is_Registered_Already_MsgPostfix"); //$NON-NLS-1$
				APIQueryConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
				throw new RuntimeException(errMsg);
			}
			extension.notifyExtensionShutdown();
			extensions.remove(extension);
		} // synchronized
	}
	
	/**
	 * Called before host plugin shutdown.  
	 */
	public void notifyPluginShutdown(){
		for (ISearchMethodExtension ext : getExtensions()) {
			ext.notifyExtensionShutdown();						
		}
	}
	
	/**
	 * Returns the iterator for the currently registered extensions.
	 * @return Returns the currently registered extensions.
	 */
	public Collection<ISearchMethodExtension> getExtensions(){
		return extensions.values();
	}
	
	/**
	 * Gets the amount of currently registered extensions.
	 * @return Returns the amount of currently registered extensions.
	 */
	public int getExtensionCount(){
		return getExtensions().size();
	}

	/**
	 * Gets the extension that matches with the given description.
	 * @param extensionDescription Description of the search method.
	 * @return The extension matching the description or <code>null</code> if not found.
	 */
	public ISearchMethodExtension getByDescription(String extensionDescription) {
		for (ISearchMethodExtension ext : getExtensions()) {
			ISearchMethodExtensionInfo extInfo = ext.getExtensionInfo();
			if(extInfo.getDescription().equals(extensionDescription)){
				return ext;
			}
		}
		return null;
	}

	/**
	 * Gets the extension that matches with the given id.
	 * @param id Id of the search method.
	 * @return The extension matching the id or <code>null</code> if not found.
	 */
	public ISearchMethodExtension getById(String id) {
		for (ISearchMethodExtension ext : getExtensions()) {
			ISearchMethodExtensionInfo extInfo = ext.getExtensionInfo();
			if(extInfo.getId().equals(id)){
				return ext;
			}
		}
		return null;
	}
}
