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
 
package com.nokia.s60tools.apiquery.shared.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.resource.FileFinder;
import com.nokia.s60tools.util.resource.IFileFinderObserver;

/**
 * Job for seeking header file from under SDK:s include paths given given. 
 */
public class FindFileFromFoldersJob extends AbstractJob implements IFileFinderObserver {


	
	/**
	 * Total steps as double, to be able to count percentages.
	 */
	double stepsAsDouble = 1;

	/**
	 * Name of the file to be seeked
	 */
	private final String fileName;

	/**
	 * Paths where to seek the file
	 */
	private final List<File> paths;

	/**
	 * Unique ID of SDK where file is seeked.
	 */
	private final String sdkId;

	/**
	 * Collection holds files found to be returned for client.
	 */
	private Collection<File> foundFiles = null;
	
	
	/**
	 * Create a Job to seek .metaxml files under selected SDK.
	 * @param name
	 * @param sdkInformation
	 */
	public FindFileFromFoldersJob(String name, List<File> paths, String fileName, String sdkId) {
		super(name);
		this.paths = paths;
		this.fileName = fileName;
		this.sdkId = sdkId;
		setUser(true);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		setMonitor(monitor);
		try {			
			reportStartTime();
			FileFinder finder = new FileFinder(APIQueryConsole.getInstance());

			String fileNames [] = new String[1];
			fileNames[0] = fileName;
			String [] folders = new String[paths.size()];
			int i = 0;
			for (Iterator<File> iterator = paths.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				folders[i] = file.getAbsolutePath();
				i++;
			}
			finder.seekFilesFromFolders(this, fileNames, folders);
			return Job.ASYNC_FINISH;
		} catch (Exception e) {
			IStatus status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,Messages.getString("FindFileFromFoldersJob.ErrorOnSeekin_StatusMsg_Part1") +fileName +Messages.getString("FindFileFromFoldersJob.ErrorOnSeekin_StatusMsg_Part2") +sdkId, e);  //$NON-NLS-1$ //$NON-NLS-2$
			return status;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.resource.IFileFinderObserver#completed(int, java.util.Collection)
	 */
	public void completed(int exitValue, Collection<File> files) {

		try {
			if(isCanceled() || exitValue == IStatus.CANCEL){
				interrupted(Messages.getString("FindFileFromFoldersJob.CancelledByUser_Msg"));  //$NON-NLS-1$
			}
			else if(exitValue == IStatus.OK){
				handleSeekCompleatedOK(files);
			}
			else{
				handleSeekCompleatedError(exitValue);
			}
			reportEndTime();
		} catch (Exception e) {
			//Handle file not found e.g. errors.
			e.printStackTrace();
			handleSeekCompleatedError(e);
		}
	}

	/**
	 * Handle Error situations after getting data
	 * @param e
	 */
	private void handleSeekCompleatedError(Exception e) {
		String message = Messages.getString("FindFileFromFoldersJob.ErrorSeekingFile_ErrMsg_Part1") +fileName +Messages.getString("FindFileFromFoldersJob.ErrorSeekingFile_ErrMsg_Part2") +sdkId;  //$NON-NLS-1$ //$NON-NLS-2$
		APIQueryConsole.getInstance().println(message, APIQueryConsole.MSG_ERROR);
		IStatus status = new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR,message, e);
		
		done(status);
	}
	
	/**
	 * Handle error return values
	 * @param exitValue
	 */
	private void handleSeekCompleatedError(int exitValue) {
		String message = Messages.getString("FindFileFromFoldersJob.ErrorSeekingFileWithValue_ErrMsg_Part1") +fileName +Messages.getString("FindFileFromFoldersJob.ErrorSeekingFileWithValue_ErrMsg_Part2") +sdkId + Messages.getString("FindFileFromFoldersJob.ErrorSeekingFileWithValue_ErrMsg_Part3") +exitValue;  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		APIQueryConsole.getInstance().println(message, APIQueryConsole.MSG_ERROR);		
		IStatus status = new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR,message, null);
		
		done(status);
	}

	/**
	 * If status was OK, handling completed situation
	 * @param files
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void handleSeekCompleatedOK(Collection<File> files) throws FileNotFoundException, IOException {

		//We are pretty much done, so marking that we are in 100% of progress, opening file found won't happen in this job anyway
		progress(steps, Messages.getString("FindFileFromFoldersJob.FileFound_Progress_Msg_Part1") +fileName +Messages.getString("FindFileFromFoldersJob.FileFound_Progress_Msg_Part2"));  //$NON-NLS-1$ //$NON-NLS-2$
		foundFiles = files;
		reportEndTime();
		done(Status.OK_STATUS);			
	}


	/**
	 * Get file found in job
	 * @return files found
	 */
	public Collection<File> getFoundSourceFiles(){
		return foundFiles;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.resource.IFileFinderObserver#interrupted(java.lang.String)
	 */
	public void interrupted(String reasonMsg) {
		done(Status.CANCEL_STATUS);
		super.cancel();
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.resource.IFileFinderObserver#isCancelled()
	 */
	public boolean isCanceled(){
		return getMonitor().isCanceled();
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.resource.IFileFinderObserver#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int steps) {

		//Its not allowed to dived with "0"
		if(steps < 1){
			steps = 1;
		}
		this.steps = steps;
		stepsAsDouble = new Double(steps).doubleValue();
		
		getMonitor().beginTask(name, PROGRESS_COMPLETED_PERCENTAGE);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.job.AbstractJob#progress(int, java.lang.String)
	 */
	public void progress(int stepsCompleated, String prosessing) {

        try {
        	int persentage = (new Double( stepsCompleated / stepsAsDouble * 100)).intValue();
        	
			super.progress(persentage, prosessing);
		} catch (JobCancelledByUserException e) {
			e.printStackTrace();
			done(Status.CANCEL_STATUS);
		}
	}


}
