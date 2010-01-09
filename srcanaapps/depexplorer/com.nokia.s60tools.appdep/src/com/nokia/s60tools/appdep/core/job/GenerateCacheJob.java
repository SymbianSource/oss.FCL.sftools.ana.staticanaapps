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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.core.AppDepCacheIndexManager;
import com.nokia.s60tools.appdep.core.AppDepCoreFacade;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.CacheDataLoadProcessManager;
import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.core.ICacheIndexListener;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.ui.wizards.WizardUtils;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver;
import com.nokia.s60tools.util.cmdline.UnsupportedOSException;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Generates cache as a background job.
 */
public class GenerateCacheJob extends Job 
							 implements ICmdLineCommandExecutorObserver,
							            ICacheGenerationJob,
							            ICacheIndexListener{

	/**
	 * Core facade to delegate execution of cache generation.
	 */
	private AppDepCoreFacade invoker = null;
	
	/**
	 * Current tool settings used for generation.
	 */
	private AppDepSettings settings = null;
	
	/**
	 * Job progress monitor.
	 */
	private IProgressMonitor monitor = null;
	
	/**
	 * External appdep core tool process handle.
	 */
	private Process proc = null;
	
	/**
	 * Maximum number of steps for job (100%) 
	 */
	private final int steps = 100;
	
	/**
	 * Storing previous percentage amount got from job progress notifications. 
	 */
	private int previousPercentage = 0;
	
	/**
	 * Job's cancel status.
	 */
	private boolean isCanceled = false;
	
	/**
	 * Target platforms the cache is generated for.
	 */
	private ITargetPlatform[] targets = null;
	
	/**
	 * This list is used to find out when all cache indices are updated
	 * after the cache generation job is finished.
	 */
	private List<String> dirtyCachesBuilDirArrList = null;
	
	/**
	 * Exit value from cache generation command done by appdep core.
	 */
	private int exitValue;
	 
	
	/**
	 * Constructor.
	 * @param name Name of the job to be presented to user in Job title.
	 * @param settings Settings object used for the cache generation.
	 */
	public GenerateCacheJob(String name, AppDepSettings settings) {
		super(name);
		setUser(true);
		this.settings = settings;
		targets = settings.getCurrentlyUsedTargetPlatforms();
		dirtyCachesBuilDirArrList = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor progressMonitor) {
		
		this.monitor = progressMonitor;
		try {
			AppDepJobManager.getInstance().registerJob(this);
			AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.StartingCacheGener_Msg"));    			 //$NON-NLS-1$
			invoker = new AppDepCoreFacade(settings, this);
			// Storing caches that really need update, must be called before calling generateCache
			storeDirtyCachesBuildDirList();
			invoker.generateCache(this);
		} catch (InvalidCmdLineToolSettingException e1) {
			e1.printStackTrace();
			return completedWithStartupError(Messages.getString("GenerateCacheJob.CacheGenerFailed_Msg_InvalidSettings") //$NON-NLS-1$
											+ e1.getMessage());    				
		} catch (UnsupportedOSException e2) {
			e2.printStackTrace();
			String msg = Messages.getString("GenerateCacheJob.CacheGenerFailed_Msg_OS_NotSupported"); //$NON-NLS-1$
			return completedWithStartupError(msg + ": " //$NON-NLS-1$
											+ e2.getMessage());
		}
		
       return Job.ASYNC_FINISH;
       
	  }

	/**
	 * Stores the list of caches that needs update.
	 */
	private void storeDirtyCachesBuildDirList() {
		dirtyCachesBuilDirArrList.clear();
		SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();
		IBuildType buildType = settings.getBuildType();
		for (int i = 0; i < targets.length; i++) {
			ITargetPlatform targetPlatform = targets[i];
			String targetPlatformId = targetPlatform.getId();
			String buildDir = settings.getBuildDir(targetPlatformId);
			if(settings.cacheNeedsUpdate(sdkInfo, new ITargetPlatform[]{targetPlatform}, buildType)){
				DbgUtility.println(DbgUtility.PRIORITY_LOOP, "DIRTY CACHE build dir: " + buildDir); //$NON-NLS-1$
				// Storing build directory in lower case letters for later getting correct match
				dirtyCachesBuilDirArrList.add(buildDir.toLowerCase());					
			}
		}
		// Finally checking if we are in SIS mode we need to also include it into the list
		if(settings.isInSISFileAnalysisMode()){
			String buildDir = settings.getBuildDir(AppDepSettings.TARGET_TYPE_ID_SIS);
			dirtyCachesBuilDirArrList.add(buildDir.toLowerCase());
		}
	}

	/**
	 * Shows error message to user when job could not be started due to fault
	 * in input parameters.
	 * @param consoleMsg Message printed to console.
	 * @return <code>IStatus</code> object.
	 */
	private IStatus completedWithStartupError(String consoleMsg){
		AppDepConsole.getInstance().println(consoleMsg, IConsolePrintUtility.MSG_ERROR);
		AppDepJobManager.getInstance().unregisterJob(this);
		monitor.done();
		return Status.OK_STATUS;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#progress(int)
	 */
	public void progress(int percentage) {
		
		isCanceled = monitor.isCanceled();
        if (isCanceled){
    		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Canceled_By_User_Msg"));    				 //$NON-NLS-1$
        	proc.destroy();
			monitor.done();
		    done(Status.CANCEL_STATUS);
		    return;
        }
        monitor.subTask(percentage + Messages.getString("GenerateCacheJob.Percent_Complete"));			 //$NON-NLS-1$
        monitor.worked(percentage - previousPercentage);
        previousPercentage = percentage;

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#processCreated(java.lang.Process)
	 */
	public void processCreated(Process proc) {
		
		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Started_Msg"));    				 //$NON-NLS-1$
		
		// Storing reference to the process
		this.proc = proc;
		
		String sdkStr = settings.getCurrentlyUsedSdk().getSdkId();
	
		String buildTypeStr = settings.getBuildType().getBuildTypeName();
		String cacheGenerateMsg = Messages.getString("GenerateCacheJob.CacheGener_For_Msg") //$NON-NLS-1$
									+ sdkStr + "/" + settings.getCurrentlyUsedTargetPlatformsAsString() //$NON-NLS-1$
									+ " (" + buildTypeStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		monitor.beginTask(cacheGenerateMsg, steps);
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#interrupted(java.lang.String)
	 */
	public void interrupted(String reasonMsg) {
		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Interrupted_Msg"));   //$NON-NLS-1$
		AppDepJobManager.getInstance().unregisterJob(this);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#completed(int)
	 */
	public void completed(int exitValue) {

		// Storing exitValue for future reference
        this.exitValue = exitValue;
        
		if (!isCanceled){
        	if(exitValue == 0){
        		//
        		// Cache generation was completed successfully
        		//
        		
        		// Doing cache index update it it is needed
        		if(dirtyCachesBuilDirArrList.size() > 0){
            		// Starting cache index update
            		AppDepCacheIndexManager.getInstance().addListener(this);
                    
            		monitor.subTask(Messages.getString("GenerateCacheJob.CacheIndex_Updating_Msg")); //$NON-NLS-1$
            		for (int i = 0; i < targets.length; i++) {
    					String targetPlatformId = targets[i].getId();
    	                try {
    	    				AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheIndex_Update_Starting_Msg"));    				 //$NON-NLS-1$
    	    				String buildDir = settings.getBuildDir(targetPlatformId);
    	    				// Converting build directory to lower case for correct match
    	    				if(dirtyCachesBuilDirArrList.contains(buildDir.toLowerCase())){
    							String cacheFileAbsolutePathName = settings.getCacheFileAbsolutePathName(targetPlatformId);
    	    					DbgUtility.println(DbgUtility.PRIORITY_LOOP, "Updating cache index for: " + cacheFileAbsolutePathName); //$NON-NLS-1$
								CacheIndex.updateCacheIndexFor(cacheFileAbsolutePathName,
    																buildDir);	    					
    	    				}
    	    				AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheIndex_Update_Started_Msg"));    				 //$NON-NLS-1$
    	    			} catch (IOException e1) {
    	    				e1.printStackTrace();
    	    				AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheIndex_Update_Failed_IOExecption_Msg") //$NON-NLS-1$
    	    												+ e1.getMessage(), IConsolePrintUtility.MSG_ERROR);    				
    	    			} catch (CacheFileDoesNotExistException e2) {
    	    				e2.printStackTrace();
    	    				AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheIndex_Update_Failed_FileNotExist_Msg") //$NON-NLS-1$
    	    												+ e2.getMessage(), IConsolePrintUtility.MSG_ERROR);    				
    	    			}					
    				}
            		
            		// Job is still active until cache indices has been updated...            		        			
        		}
        		else{
        			// No need to update any cache indices...we are done
    	        	monitor.done();
    			    done(Status.OK_STATUS);
    			    // Showing completion status message to user now.
    	        	runCacheCompletionMsgBoxInUIThread();        				    
        		}
        		
        	}
        	else{
        		//
        		// Something failed in cache generation
        		//        		
        		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Failed_WithCode_Msg") + exitValue);    				 //$NON-NLS-1$
        		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Removing_Corrupted_Files_Msg"));    				 //$NON-NLS-1$
	            // If was not canceled by user
	        	// => Job itself was terminated normally even though there was some error in cache generation.
	        	monitor.done();
			    done(Status.OK_STATUS);
			    // Showing completion status message to user now.
	        	runCacheCompletionMsgBoxInUIThread();        				    
        	}		    
		    
        }
        else{
        	// User canceled the operation.
    		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Removing_Partially_Generated_Files_Msg"));    				 //$NON-NLS-1$
        }
        
		AppDepConsole.getInstance().println(Messages.getString("GenerateCacheJob.CacheGener_Ended_WithExitVal_Msg") + exitValue);    				 //$NON-NLS-1$
		AppDepJobManager.getInstance().unregisterJob(this);
	}

	/**
	 * Informs user about cache generation results. Gives an error message in case
	 * of an error has happened or in case of success queries if user wants to 
	 * select a component from just generated cache.
	 */
	private void informUserAboutCacheCreationCompletion() {
		
		try {
			
			String targetPlatformStr = settings.getCurrentlyUsedTargetsAsString();
			String msg = null;
			AppDepMessageBox msgBox = null;

			if(exitValue != 0){				
				// Something failed				
				msg = Messages.getString("GenerateCacheJob.CacheGener_Failed_For_Msg") + targetPlatformStr  //$NON-NLS-1$
					  + " (" + Messages.getString("GenerateCacheJob.Exit_Code") + "=" + exitValue + "). " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					  + Messages.getString("GenerateCacheJob.See_Console_Log_Msg"); //$NON-NLS-1$
				
				msgBox = new AppDepMessageBox(msg, SWT.ICON_ERROR | SWT.OK);
				msgBox.open();			
			}
			else{				
				// Generation completed successfully
				
				// Setting cache update flag 
				settings.cacheWasUpdated();
				
				msg = Messages.getString("GenerateCacheJob.CacheGener_Completed_For_Msg") + targetPlatformStr + ". "  //$NON-NLS-1$ //$NON-NLS-2$
				     +  Messages.getString("GenerateCacheJob.CacheGener_Do_You_Want_To_Select_Component_Question"); //$NON-NLS-1$
				
				msgBox = new AppDepMessageBox(msg, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				int userResponse = msgBox.open();			
				if(userResponse == SWT.YES){
					// Invoking component selection dialog
					if(WizardUtils.invokeSDKAndTargetPlatformSelectionWizard(msgBox.getParent(), 
																			 false,
																			 settings,
																			 true)){
						MainView.update();
					}				
				}
				else{
				     // Otherwise just reloading cache and refreshing existing view with new information
					try {
						CacheDataLoadProcessManager.runCacheLoadProcess(settings, true);
					} catch (Exception e) {
						e.printStackTrace();
						// Cache reload failed				
						msg = Messages.getString("GeneralMessages.CacheDataReload_Failed_For_Msg") //$NON-NLS-1$
						        + " '" + targetPlatformStr  + "'. " //$NON-NLS-1$ //$NON-NLS-2$
							  + Messages.getString("GenerateCacheJob.See_Console_Log_Msg"); //$NON-NLS-1$
						
						msgBox = new AppDepMessageBox(msg, SWT.ICON_ERROR | SWT.OK);
						msgBox.open();			
					}
					MainView.update();					
				}
			}			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IManageableJob#forcedShutdown()
	 */
	public void forcedShutdown() {
		if(proc != null){
			proc.destroy();
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}					
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.ICacheGenerationJob#getTargetSdkForJob()
	 */
	public SdkInformation getTargetSdkForJob() {
		return settings.getCurrentlyUsedSdk();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.ICacheGenerationJob#getBuildTypeForJob()
	 */
	public IBuildType getBuildTypeForJob() {
		return settings.getBuildType();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.ICacheGenerationJob#getTargetPlatformForJob()
	 */
	public ITargetPlatform[] getTargetPlatformForJob() {
		return settings.getCurrentlyUsedTargetPlatforms();
	}
	
	/**
	 * Convenience method for creating cache generation job
	 * for the currently active settings.
	 */
	public static void triggerCacheGenerationForCurrentlyActiveSettings(){
		// Cache generation uses the settings set by the user.			
		// Getting a local copy of currently active settings...
		AppDepSettings localSettings 
				= (AppDepSettings) AppDepSettings.getActiveSettings().clone();
		//... and passing it to cache generate job object
		Job jb = new GenerateCacheJob(Messages.getString("GeneralMessages.CacheGeneration_Job_Title_Text"), localSettings); //$NON-NLS-1$
		
		// We do not want cache generation to block other 
		// jobs and therefore using the lowest priority
		jb.setPriority(Job.DECORATE);
		jb.schedule();		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.ICacheIndexListener#cacheIndexCreationCompleted(com.nokia.s60tools.appdep.core.data.CacheIndex)
	 */
	public void cacheIndexCreationCompleted(CacheIndex cacheIndexObj) {
 
		String buildDirectory = cacheIndexObj.getBuildDirectory();
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "DIRTY CACHE UPDATED for build dir: " + buildDirectory); //$NON-NLS-1$

		// Converting build directory in lower case letters for getting correct match
		dirtyCachesBuilDirArrList.remove(buildDirectory.toLowerCase());
		// Have all the scheduled cache indices updated
		if(dirtyCachesBuilDirArrList.size() == 0){
			// No more need for listening completions
			AppDepCacheIndexManager.getInstance().removeListener(this);
			
            // Job has completed successfully
        	monitor.done();
		    done(Status.OK_STATUS);
		    // Showing completion status message to user
        	runCacheCompletionMsgBoxInUIThread();        				    
		}
	}

	/**
	 * Schedules completion message box showing to the UI Thread.
	 * Showing dialog in UI thread causes <code>IllegalThreadAccess</code> exception.
	 */
	private void runCacheCompletionMsgBoxInUIThread() {
		// Informing used about the result of cache generation
		Runnable cacheCreationCompletedRunnable = new Runnable(){
			public void run(){
				informUserAboutCacheCreationCompletion();
			}
		};
		
		// Showing a visible message in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(cacheCreationCompletedRunnable);
	}
	
}
