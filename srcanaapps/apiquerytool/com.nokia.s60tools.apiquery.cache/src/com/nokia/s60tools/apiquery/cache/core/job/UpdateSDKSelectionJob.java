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
 
package com.nokia.s60tools.apiquery.cache.core.job;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.cache.searchmethod.ui.LocalCacheUIComposite;
import com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener;
import com.nokia.s60tools.apiquery.shared.job.AbstractJob;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * Job for loading already seeked SDK data to RAM
 */
public class UpdateSDKSelectionJob extends AbstractJob {

	private final SdkInformation info;
	private boolean showErrorDialog = true;


	/**
	 * Constructor
	 * @param name
	 * @param info
	 */
	public UpdateSDKSelectionJob(String name, SdkInformation info) {
		super(name);
		this.info = info;
		setUser(true);
	}
	/**
	 * Constructor
	 * @param name
	 * @param info
	 * @param showErrorDialog set <code>false</code> if dont want to show error dialog when there was errors 
	 * when updating cache. 
	 */
	public UpdateSDKSelectionJob(String name, SdkInformation info, boolean showErrorDialog) {
		super(name);
		this.info = info;
		setUser(true);
		this.showErrorDialog = showErrorDialog;
	}	
	
	/**
	 * Check if job is allready running
	 * @return true if job with same name is allready running
	 */
	public boolean isAllreadyRunning(){

		//Get and check all jobs
		Job[] jobs = getJobManager().find(null);
		for (int i = 0; i < jobs.length; i++) {
			if(jobs[i] instanceof UpdateSDKSelectionJob){
				UpdateSDKSelectionJob job = (UpdateSDKSelectionJob)jobs[i];
				//If we found job what is not this job, and it runs for same SDK (same name)
				//then there is job allready runnig to update this SDK
				if(!job.equals(this) && job.getName().equals(this.getName())){
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
	
		
		MainView.enablePropTabcontents(false);
		//Check if Update is already ongoing, returning warning if is.
		if(isAllreadyRunning()){
			IStatus status = new Status(
					Status.WARNING,Platform.PI_RUNTIME,
					Status.WARNING, "Job: '" +getName() +"' already runnig.", null);			
			return status;
		}		
		
		setMonitor(monitor);
		try{
			reportStartTime();
			updateDataStore();
			reportEndTime();
			return Status.OK_STATUS;
			
		}catch (JobCancelledByUserException e) {			
			return Status.CANCEL_STATUS;
		}catch (Exception e) {
			e.printStackTrace();
			return returnErrorStatus(e);
		}		
		finally{
			
			MainView.enablePropTabcontents(true);
			
			
		}

	}
	
	/**
	 * Return error status
	 * @param e
	 * @return
	 */
	private IStatus returnErrorStatus(Exception e){
		String msg = Messages.getString("UpdateSDKSelectionJob.SDKReadError_Msg") +info.getSdkId() +"'";	 //$NON-NLS-1$ //$NON-NLS-2$
		IStatus status = new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR,msg, e);
		return status;			
	}

	/**
	 * When known that data source allready found (files seeked under SDK) just
	 * updating datastore selections so that selected SDK is selected.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws JobCancelledByUserException 
	 */
	private void updateDataStore() throws FileNotFoundException, IOException, JobCancelledByUserException {

		getMonitor().beginTask(
				Messages.getString("UpdateSDKSelectionJob.ReadingSDK_ProgressMsg") + info.getSdkId() + "'", //$NON-NLS-1$ //$NON-NLS-2$
				IProgressMonitor.UNKNOWN); 

		CacheEntryStorage storage = CacheEntryStorage.getInstance();

		getMonitor().subTask(Messages.getString("UpdateSDKSelectionJob.ParsingFiles_ProgressMsg")); //$NON-NLS-1$
		// Cancel situations will throw exception and restore old data
		storage.selectSDKAndLoadAllSelectedDatasToMemory(getMonitor(), info);

		// Notifying listeners, only one notifying should be enough.
		storage.notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRYS_UPDATED);

		// Show error message or write to log if there is some errors on load.
		if (storage.isLoadErros()) {
			if (showErrorDialog) {
				JobMessageUtils.showLoadErrorDialog();
			} else {
				JobMessageUtils.printLoadErrorMessage();
			}
		}
		
	}



}
