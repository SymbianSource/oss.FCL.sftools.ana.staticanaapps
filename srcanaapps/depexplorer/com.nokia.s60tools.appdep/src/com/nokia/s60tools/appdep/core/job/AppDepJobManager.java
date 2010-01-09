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
 
 
package com.nokia.s60tools.appdep.core.job;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.ListenerList;

import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.sdk.SdkInformation;


/**
 * Singleton class that is created on plugin
 * startup, and is kept active as long as plugin is active.
 * 
 * The purpose of this class is to enable shutdown of all
 * ongoing jobs on forced shutdown. All the jobs implementing
 * <code>IManageableJob</code> should register itself to 
 * this class and deregister when completed.
 */
public class AppDepJobManager {

	/**
	 * Singleton instance.
	 */
	static private AppDepJobManager instance = null;

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
	public static AppDepJobManager getInstance(){
		if( instance == null ){
			instance = new AppDepJobManager();
		}
		return instance;		
	}	
	
	/**
	 * Private default constructor.
	 */
	private AppDepJobManager() {
		registeredJobs = new ArrayList<IManageableJob>();
		listeners = new ListenerList();
	}
	
	void registerJob(IManageableJob job){
		registeredJobs.add(job);
	}

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
	 * Performs forced shutdown for all the registered jobs.
	 */
	public void shutdown(){
		for (Iterator<IManageableJob> iter = registeredJobs.iterator(); iter.hasNext();) {
			IManageableJob job = iter.next();
			job.forcedShutdown();			
		}
		registeredJobs.clear();
		// Giving a moment for processes to really shutdown
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds job completion listener.
	 * @param obj job completion listener.
	 */
	public void addListener(IJobCompletionListener obj){
		listeners.add(obj);
	}
	
	/**
	 * Removes job completion listener.
	 * @param obj  job completion listener.
	 */
	public void removeListener(IJobCompletionListener obj){
		listeners.remove(obj);
	}
	
	/**
	 * Checks if there are any cache generation processed ongoing
	 * for the given target.
	 * @param sdkId SDK ID for the SDK to check against. 
	 * @param targetPlatformName Target Platform Name string
	 * @param buildType Build type.
	 * @return <code>true</code> if there is cache generation job ongoing for 
	 *         the given SDK, otherwise <code>false</code>.
	 */
	public boolean hasCacheGenerationJobForTarget(String sdkId, 
												 String targetPlatformName,
												 IBuildType buildType) {	
		for (Iterator<IManageableJob> iter = registeredJobs.iterator(); iter.hasNext();) {
			IManageableJob job = iter.next();
			if(job instanceof ICacheGenerationJob){
				SdkInformation targetSdkForJob = ((ICacheGenerationJob)job).getTargetSdkForJob();
				ITargetPlatform[] targetsForJob = ((ICacheGenerationJob)job).getTargetPlatformForJob();
				boolean targetPlatformMatchFound = false;
				for (int i = 0; i < targetsForJob.length; i++) {
					ITargetPlatform targetPlatformForJob = targetsForJob[i];
					if(targetPlatformForJob.idEquals(targetPlatformName)){
						targetPlatformMatchFound = true;
					}
				}
				IBuildType buildTypeForJob = ((ICacheGenerationJob)job).getBuildTypeForJob();				
				if(targetSdkForJob.getSdkId().equals(sdkId)
					&&
					targetPlatformMatchFound
					&&
					buildTypeForJob.equals(buildType)
					){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if there are any cache generation processed ongoing
	 * for the given SDK.
	 * @param sdkId SDK ID for the SDK to check against. 
	 * @return <code>true</code> if there is cache generation job ongoing for 
	 *         the given SDK, otherwise <code>false</code>.
	 */
	public boolean hasCacheGenerationJobForSdk(String sdkId) {	
		for (Iterator<IManageableJob> iter = registeredJobs.iterator(); iter.hasNext();) {
			IManageableJob job = iter.next();
			if(job instanceof ICacheGenerationJob){
				SdkInformation targetSdkForJob = ((ICacheGenerationJob)job).getTargetSdkForJob();
				if(targetSdkForJob.getSdkId().equals(sdkId)){
					return true;
				}
			}
		}
		return false;
	}
}
