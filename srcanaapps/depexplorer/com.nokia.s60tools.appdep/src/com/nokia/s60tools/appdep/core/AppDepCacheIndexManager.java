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
 
 
package com.nokia.s60tools.appdep.core;

import java.util.ArrayList;

import org.eclipse.core.runtime.ListenerList;

import com.nokia.s60tools.appdep.core.data.CacheIndex;

/**
 * Singleton class that is created on plugin
 * startup, and is kept active as long as plugin is active.
 * 
 * The purpose of this class is to management of cache index
 * creation process that happens at background. All cache index
 * background builds register themselves to to this class.
 * The other users can register to listen to the cache index
 * creation completion events if they wish to do so.
 */
public class AppDepCacheIndexManager {

	/**
	 * Singleton instance.
	 */
	static private AppDepCacheIndexManager instance = null;

	/**
	 * List of ongoing cache index operations.
	 */
	private ArrayList<CacheIndex> ongoingCacheIndexBuildOperations = null;
	
	/**
	 * Listeners interested in cache creation operations.
	 */
	private ListenerList listeners = null;
	
	/**
	 * Public Singleton instance accessor.
	 * @return Returns instance of this singleton class-
	 */
	public static AppDepCacheIndexManager getInstance(){
		if( instance == null ){
			instance = new AppDepCacheIndexManager();
		}
		return instance;		
	}	
	
	/**
	 * Private default constructor.
	 */
	private AppDepCacheIndexManager() {
		ongoingCacheIndexBuildOperations = new ArrayList<CacheIndex>();
		listeners = new ListenerList();
	}
	
	public void addListener(ICacheIndexListener obj){
		listeners.add(obj);
	}
	
	public void removeListener(ICacheIndexListener obj){
		listeners.remove(obj);
	}

	public void registerCacheIndexCreationProcess(CacheIndex indx){
		ongoingCacheIndexBuildOperations.add(indx);
	}

	public void unregisterCacheIndexCreationProcess(CacheIndex indx){
		ongoingCacheIndexBuildOperations.remove(indx);
		Object[] listenerArray = listeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			ICacheIndexListener listenerObj 
								= (ICacheIndexListener) listenerArray[i];
			listenerObj.cacheIndexCreationCompleted(indx);
		}
	}
	
	public void shutdown(){
		// It is possible also here to prepare for plugin shutdown
	}
	
}
