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
 
 
package com.nokia.s60tools.apiquery.job;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.ListenerList;

import com.nokia.s60tools.apiquery.shared.job.IJobCompletionListener;
import com.nokia.s60tools.apiquery.shared.job.IManageableJob;



/**
 * Singleton class that is created on plugin
 * startup, and is kept active as long as plugin is active.
 * 
 * The purpose of this class is to enable shutdown of all
 * ongoing jobs on forced shutdown. All the jobs implementing
 * <code>IManageableJob</code> should register itself to 
 * this class and deregister when completed.
 */
public class ActiveProjectQueryJobManager {

	/**
	 * Singleton instance.
	 */
	static private ActiveProjectQueryJobManager instance = null;

	/**
	 * List of registered jobs.
	 */
	private ArrayList<IManageableJob> registeredJobs = null;

	/**
	 * Listeners interested in job completions operations.
	 */
	private ListenerList listeners = null;
	
	/**
	 * Public Singleton instance accessor.
	 * @return Returns instance of this singleton class-
	 */
	public static ActiveProjectQueryJobManager getInstance(){
		if( instance == null ){
			instance = new ActiveProjectQueryJobManager();
		}
		return instance;		
	}	
	
	/**
	 * Private default constructor.
	 */
	private ActiveProjectQueryJobManager() {
		registeredJobs = new ArrayList<IManageableJob>();
		listeners = new ListenerList();
	}
	
	/**
	 * Register job to manager
	 * @param job
	 */
	void registerJob(IManageableJob job){
		registeredJobs.add(job);
	}

	/**
	 * Un register a job from manager
	 * @param job
	 */
	void unregisterJob(IManageableJob job){
		registeredJobs.remove(job);
		Object[] listenerArray = listeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			IJobCompletionListener listenerObj 
								= (IJobCompletionListener) listenerArray[i];
			listenerObj.backgroundJobCompleted(job);
		}
	}
	
	/**
	 * Shutdown all jobs registerd
	 */
	public void shutdown(){
		for (Iterator<IManageableJob> iter = registeredJobs.iterator(); iter.hasNext();) {
			IManageableJob job = (IManageableJob) iter.next();
			job.forcedShutdown();			
		}
		registeredJobs.clear();
		// Giving a moment for processes to really shutdwown
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a listener
	 * @param listener
	 */
	public void addListener(IJobCompletionListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * @param listener
	 */
	public void removeListener(IJobCompletionListener listener){
		listeners.remove(listener);
	}
}
