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

package com.nokia.s60tools.appdep.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.core.data.CacheDataManager;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.core.model.ICacheLoadProgressNotification;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.util.LogUtils;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This Singleton class takes care that cache data loading happens
 * in synchronized manner that end-used experiences fluent
 * notification about cache load process and population 
 * of component tree.
 */
public class CacheDataLoadProcessManager {

	/**
	 * Private Singleton instance of this class.
	 */
	private static CacheDataLoadProcessManager instance;
	
	/**
	 * IRunnableWithProgress object that performs cache load process.
	 */
	private class CacheLoadJobRunnable implements IRunnableWithProgress, ICacheLoadProgressNotification {

		/**
		 * Expected component count to be loaded (=amount of steps).
		 */
		private final int expectedComponentCount;

		/**
		 * Component count of currently loaded components.
		 */
		private int loadedComponentCount;

		/**
		 * Progress monitor used to report job progress.
		 */
		private IProgressMonitor progressMonitor;

		/**
		 * Settings to be used for cache loading.
		 */
		private final AppDepSettings activeSettings;

		/**
		 * Title message of progress dialog to show user.
		 */
		private final String title;

		/**
		 * Constructor.
		 * @param activeSettings Active settings used for cache load.
		 * @param expectedComponentCount Expected component count to be loaded (=amount of steps).
		 */
		public CacheLoadJobRunnable(String title, AppDepSettings activeSettings, int expectedComponentCount) {
			this.title = title;
			this.activeSettings = activeSettings;
			this.expectedComponentCount = expectedComponentCount;
		}
		
		public void run(IProgressMonitor monitor) {
			progressMonitor = monitor;
						
			try {
				progressMonitor.beginTask(title, expectedComponentCount);
				// Loading cache data
				CacheDataManager.loadCache(activeSettings, this);
			} catch (CacheFileDoesNotExistException e) {
				throw new RuntimeException(e.getMessage() + " (" + e.getClass().getSimpleName()  +")."); //$NON-NLS-1$ //$NON-NLS-2$	
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage() + " (" + e.getClass().getSimpleName()  +")."); //$NON-NLS-1$ //$NON-NLS-2$	
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage() + " (" + e.getClass().getSimpleName()  +")."); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		/* (non-Javadoc)
		 * @see com.nokia.s60tools.appdep.core.model.ICacheLoadProgressNotification#componentLoaded(java.lang.String)
		 */
		public void componentLoaded(String componentName) {
			loadedComponentCount++;
			if(loadedComponentCount < expectedComponentCount){
				progressMonitor.worked(1);						
			}
			else{
				// Cache load about to finish => can inform user already
				progressMonitor.setTaskName(Messages.getString("CacheDataLoadProcessManager.CacheDataLoaded_InfoMsg")); //$NON-NLS-1$
			}
		}
		
	};

	/**
	 * Private constructor. Instance of this class is generated via <code>runCacheLoadProcess</code> method.
	 */
	private CacheDataLoadProcessManager(){
	}
	
	/**
	 * Cache load process triggering publicly available access method.
	 * @param activeSettings Settings used for cache data load process.
	 * @param isCacheUpdated Set to <code>true</code> when we are actually re-loading currently analyzed cache.
	 */
	public static void runCacheLoadProcess(AppDepSettings activeSettings, boolean isCacheUpdated) {
		if(instance == null){
			instance = new CacheDataLoadProcessManager();			
		}
		instance.runCacheLoadProcessImpl(activeSettings, isCacheUpdated);
	}

	/**
	 * Cache load process triggering private implementation.
	 * @param activeSettings Settings used for cache data load process.
	 * @param isCacheUpdated Set to <code>true</code> when we are actually re-loading currently analyzed cache.
	 */
	public void runCacheLoadProcessImpl(AppDepSettings activeSettings, boolean isCacheUpdated){ 		
		try {
			// Resettings possibly set cache update flag
			activeSettings.resetCacheUpdateFlag();
			// Starting load process
			loadCacheDataShowProgressAndWaitForCompletion(activeSettings, isCacheUpdated);
		} catch (Exception e) {			
			String errMessageShort = Messages.getString("CacheDataLoadProcessManager.CacheDataLoadFailed_ErrMsg"); //$NON-NLS-1$
			String errMessageExtended = errMessageShort + ": (" + e.getClass().getSimpleName() //$NON-NLS-1$ 
										+ "): " + e.getMessage(); //$NON-NLS-1$
			LogUtils.logStackTrace(errMessageExtended, e);
			throw new RuntimeException(errMessageShort + ".");//$NON-NLS-1$
		}
	}
	
	/**
	 * Loads cache data and synchronously waits for load completion while notifies user
	 * @param activeSettings Settings used for cache data load process.
	 * @param isCacheUpdated Set to <code>true</code> when we are actually re-loading currently analyzed cache.
	 * @throws IOException 
	 * @throws CacheFileDoesNotExistException 
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	private void loadCacheDataShowProgressAndWaitForCompletion(AppDepSettings activeSettings, boolean isCacheUpdated) throws CacheFileDoesNotExistException, IOException, InvocationTargetException, InterruptedException {
		List<String> duplicateItemsList = new ArrayList<String>();
		List<ComponentListNode> componentIteratorForGivenSettings = CacheIndex.getComponentIteratorForGivenSettings(activeSettings, duplicateItemsList);
		int componentCountToBeLoaded = componentIteratorForGivenSettings.size();
		String title = null;
		title = Messages.getString("CacheDataLoadProcessManager.LoadingCache_ProgressMsg");			 //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, title); //$NON-NLS-1$
		CacheLoadJobRunnable waitForCacheLoadJob = new CacheLoadJobRunnable(title, activeSettings, componentCountToBeLoaded);
		ProgressMonitorDialog loadJobDld = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		loadJobDld.run(true, false, waitForCacheLoadJob);
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "CACHE LOAD DONE!"); //$NON-NLS-1$
	}
	
}
