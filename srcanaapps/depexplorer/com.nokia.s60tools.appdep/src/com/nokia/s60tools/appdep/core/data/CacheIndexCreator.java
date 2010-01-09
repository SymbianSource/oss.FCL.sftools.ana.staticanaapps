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
package com.nokia.s60tools.appdep.core.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.core.AppDepCacheIndexManager;
import com.nokia.s60tools.appdep.core.ICacheIndexListener;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.wizards.ICacheIndexCreatorObserver;

/**
 * Registers the valid cache index creation requests
 * and perform creation of registered cache indices 
 * when asked to do so.
 */
public class CacheIndexCreator implements Runnable, IRunnableWithProgress, ICacheIndexListener{

	/**
	 * Observer needing to get informed when cache index creation is ready.
	 */
	private final ICacheIndexCreatorObserver indexCreatorObserver;

	/**
	 * Count of cache indices to be created.
	 * Accessed only via increment and decrement methods
	 */
	private int cacheIndicesToBeCreatedCount = 0;
	
	/**
	 * Stores cache index creation requests.
	 */
	private ArrayList<CacheIndexCreationRequest> cacheIndexCreationReqArr = null;
	
	/**
	 * Reference to cache index manager.
	 */
	AppDepCacheIndexManager indexMgr = null;
	
	/**
	 * Reference to progress monitor of the job launching cache index creation.
	 */
	private IProgressMonitor monitorStored = null;
	
	/**
	 * Constructor to be launched from JUnit tests.
	 * @param indexCreatorObserver Observer needing to get informed when cache index creation is ready.
	 */
	public CacheIndexCreator(){
		this.indexCreatorObserver = null;
		this.allocateMembers();
	}
	
	/**
	 * Constructor to be launched from UI
	 * @param indexCreatorObserver Observer needing to get informed when cache index creation is ready.
	 */
	public CacheIndexCreator(ICacheIndexCreatorObserver indexCreatorObserver){
		this.indexCreatorObserver = indexCreatorObserver;
		allocateMembers();
	}
	
	/**
	 * Allocates member variables not initialized via constructor parameters.
	 */
	public void allocateMembers(){
		cacheIndexCreationReqArr = new ArrayList<CacheIndexCreationRequest>();				
	}
	
	/**
	 * Increments cache index creation count.
	 * @return incremented count.
	 */
	synchronized private int incrementIndicesToBeCreatedCount(){
		return (++cacheIndicesToBeCreatedCount);
	}
	
	/**
	 * Decrements cache index creation count.
	 * @return decremented count.
	 */
	synchronized private int decrementIndicesToBeCreatedCount(){
		return (--cacheIndicesToBeCreatedCount);			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		if(monitorStored == null){			
			monitorStored = monitor;
			int steps = cacheIndexCreationReqArr.size();
			if(monitorStored != null){ // There is no monitor initialized in JUnit tests
				monitorStored.beginTask(Messages.getString("CacheIndexCreator.Creating_Cache_Indices"), steps); //$NON-NLS-1$			
			}
			handleIndexCreationRequests();			
		}//if			
		
	} //run

	/**
	 * Handles registered cache index creation request.
	 */
	private void handleIndexCreationRequests() {
		for (Iterator<CacheIndexCreationRequest> iter = cacheIndexCreationReqArr.iterator(); iter.hasNext();) {
			CacheIndexCreationRequest req = iter.next();
			try {
				// Requesting
				File cacheFile = new File(req.getCacheFile());
				if(!cacheFile.exists()){
					throw new CacheFileDoesNotExistException(cacheFile.getAbsolutePath());
				}
				CacheIndex.getCacheIndexInstance(cacheFile, req.getBuildDir());
			} catch (CacheFileDoesNotExistException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} //for
		
		// Resetting array after all request have been handled
		cacheIndexCreationReqArr.clear();
	}

	/**
	 * Registers cache index creation request for a target identified 
	 * in parameters, if does not registered already.
	 * @param cacheFileAbsolutePathName Cache file path name for target to be registerd.
	 * @param buildDirAbsolutePathName Build directory file path name for target to be registerd.
	 */
	public void registerCacheIndexCreation(String cacheFileAbsolutePathName, 
			   String buildDirAbsolutePathName) {
								
		//Checking if we already have the cache index
		if(CacheIndex.cacheIndexExistsFor(cacheFileAbsolutePathName)){
			// Already exists, no need to re-create
			return;
		}
		
		// Checking that we do not already have the same request?...
		for (Iterator<CacheIndexCreationRequest> iter = cacheIndexCreationReqArr.iterator(); iter.hasNext();) {
			CacheIndexCreationRequest req = iter.next();
			if(req.getCacheFile().equalsIgnoreCase(cacheFileAbsolutePathName)){
				// Request already registered!
				return;
			}
		}
		
		// Not registered yet, registering...
		if(indexMgr == null){
			indexMgr = AppDepCacheIndexManager.getInstance();			
			indexMgr.addListener(this);								
		}
		
		incrementIndicesToBeCreatedCount();
		cacheIndexCreationReqArr.add(new CacheIndexCreationRequest(cacheFileAbsolutePathName, 
												   buildDirAbsolutePathName));
		
	}
	
	/**
	 * Checks if there are any cache creation requests.
	 * @return <code>true</code> if requests, otherwise <code>false</code>.
	 */
	public boolean hasIndexCreationRequests(){
		return (cacheIndexCreationReqArr.size() > 0);
	}
		
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.ICacheIndexListener#cacheIndexCreationCompleted(com.nokia.s60tools.appdep.core.data.CacheIndex)
	 */
	public void cacheIndexCreationCompleted(CacheIndex cacheIndexObj) {
					
		final ICacheIndexListener listenerObj = this;
		final CacheIndex cacheIndexObjFinal = cacheIndexObj;
		
		Runnable monitorStatusUpdateRunnable = new Runnable(){
			public void run(){
				try {
					// One cache index creation step completed
					if(monitorStored != null){ // There is no monitor initialized in JUnit tests
						monitorStored.worked(1);						
					}
					
					try {
						// Refreshing tree view
						if(indexCreatorObserver != null){
							indexCreatorObserver.cacheIndexCreated(cacheIndexObjFinal);
						}						
					} catch (Exception e) {
						// User might have been Canceled the wizard
						// and widget is disposed and this call fails
						// Just then ignoring and making sure that
						// done() is called for monitor. 
					}
					
					// Updating count of work that is still pending
					// and testing if all the work has been done
					if(decrementIndicesToBeCreatedCount() == 0){
						if(monitorStored != null){ // There is no monitor initialized in JUnit tests
							monitorStored.done();
						}
						indexMgr.removeListener(listenerObj);
						// Forcing the manager creation next time
						// if new registrations happen during 
						// the lifetime of this object
						indexMgr = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		// Update request done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(monitorStatusUpdateRunnable);
	
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		handleIndexCreationRequests();			
	}

}