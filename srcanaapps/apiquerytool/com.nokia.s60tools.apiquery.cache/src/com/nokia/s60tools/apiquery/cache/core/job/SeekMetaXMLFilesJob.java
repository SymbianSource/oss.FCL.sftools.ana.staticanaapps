/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.apiquery.cache.configuration.CacheEntry;
import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.datatypes.config.DuplicateEntryException;
import com.nokia.s60tools.apiquery.shared.datatypes.config.EntryNotFoundException;
import com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener;
import com.nokia.s60tools.apiquery.shared.job.AbstractJob;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.resource.FileFinder;
import com.nokia.s60tools.util.resource.FileUtils;
import com.nokia.s60tools.util.resource.IFileFinderObserver;

/**
 * Job for seeking .metaxml files from under SDK ({@link SdkInformation}) given. 
 */
public class SeekMetaXMLFilesJob extends AbstractJob implements IFileFinderObserver {

	/**
	 * Metaxml file type
	 */
	private static final String METAXML_FILE_SUFFIX = ".metaxml"; //$NON-NLS-1$

	/**
	 * s60 folder name in R&D SDK
	 */
	private static final String S60 = "s60"; //$NON-NLS-1$
	
	/***
	 * SF folder 
	 * 
	 */
	private static final String SF = "sf"; //$NON-NLS-1$

	/**
	 * Information about selected SDK
	 */
	private final SdkInformation sdkInformation;
	
	/**
	 * Total steps as double, to be able to count percentages.
	 */
	double stepsAsDouble = 1;

	/**
	 * Folders where to seek .metaxml files under selected SDK
	 */
	private static final String [] FOLDERS_TO_SEEK_META_FILES = new String [] {
		"osext",  //$NON-NLS-1$
		"mw",  //$NON-NLS-1$
		"app",
		"os" }; //$NON-NLS-1$
	
	
	/**
	 * Create a Job to seek .metaxml files under selected SDK.
	 * @param name
	 * @param sdkInformation
	 */
	public SeekMetaXMLFilesJob(String name, SdkInformation sdkInformation) {
		super(name);
		this.sdkInformation = sdkInformation;
		setUser(true);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		
			
		
		setMonitor(monitor);
	
		reportStartTime();
		try {	
			MainView.enablePropTabcontents(false);
			FileFinder finder = new FileFinder(APIQueryConsole.getInstance());

			String epocRoot = sdkInformation.getEpocRootDir();
			
			//System.out.println("epoc 32 " + epocRoot);
			
			String message = Messages.getString("SeekMetaXMLFilesJob.StartJobMsg_Part1")  //$NON-NLS-1$
				+METAXML_FILE_SUFFIX +Messages.getString("SeekMetaXMLFilesJob.StartJobMsg_Part2") +sdkInformation.getSdkId() //$NON-NLS-1$
				+Messages.getString("SeekMetaXMLFilesJob.StartJobMsg_Part3") +epocRoot +"'."; //$NON-NLS-1$ //$NON-NLS-2$
			APIQueryConsole.getInstance().println(message);

			String [] folders = new String[FOLDERS_TO_SEEK_META_FILES.length];
			  
			boolean isS60 = true;
			//try to check if s60 is present
			File directoryOfPdfs = new File(epocRoot );

			if(directoryOfPdfs.isDirectory()) { // check to make sure it is a directory
			String filenames[] = directoryOfPdfs.list(); //make array of filenames.
			for(int i =0 ;i <filenames.length;i++)
			{
			if (	filenames[i].equalsIgnoreCase(SF) )
			{ isS60 = false;				
				break;
			}
			}
			}
			
					
			
			for (int i = 0; i < FOLDERS_TO_SEEK_META_FILES.length; i++) {
				folders[i] = epocRoot + ((isS60)?S60 : SF)+File.separatorChar + FOLDERS_TO_SEEK_META_FILES[i];
			}		
			
		
			finder.seekFilesFromFolders(this, METAXML_FILE_SUFFIX, folders);
			return Job.ASYNC_FINISH;
		}		
		catch (Exception e) {
			IStatus status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,Messages.getString("SeekMetaXMLFilesJob.SeekErrorMsg") +sdkInformation.getSdkId(), e); //$NON-NLS-1$
			return status;
		}
		
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.resource.IFileFinderObserver#completed(int, java.util.Collection)
	 */
	public void completed(int exitValue, Collection<File> files) {

		try {
	
			if(isCanceled() || exitValue == IStatus.CANCEL){
				interrupted(Messages.getString("SeekMetaXMLFilesJob.CancelledByUser")); //$NON-NLS-1$
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
		finally{
			MainView.enablePropTabcontents(true);
			
			
		}
	}

	/**
	 * Handle Error situations after getting data
	 * @param e
	 */
	private void handleSeekCompleatedError(Exception e) {
		String message = Messages.getString("SeekMetaXMLFilesJob.SeekCompleatedErrorMsg") +sdkInformation.getSdkId(); //$NON-NLS-1$
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
		String message = Messages.getString("SeekMetaXMLFilesJob.SeekCompleatedErrorMsg") +sdkInformation.getSdkId() +Messages.getString("SeekMetaXMLFilesJob.SeekCompleatedExitValueErrorMsg") +exitValue; //$NON-NLS-1$ //$NON-NLS-2$
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

		//We are pretty much done, so marking that we are in 100% of progress, updating storage does not really take any time
		progress(steps, Messages.getString("SeekMetaXMLFilesJob.UpdatingDB_ProgressMsg")); //$NON-NLS-1$

		CacheEntryStorage storage = CacheEntryStorage.getInstance();

		try {
		
		
		
			//set to data store that all are deselected
			storage.deselectAll(true);//When SDK is changed, all entries is deselected
				
			for (File file : files) {
				//Read the File , get the API Name
				//System.out.println("file path" + file.getAbsolutePath());
				StringBuffer buf = FileUtils.loadDataFromFile(file.getAbsolutePath());
				int startIndex = buf.indexOf("<name>");
					String apiName =   buf.substring(startIndex+6,  buf.indexOf("</name>",startIndex ));	
					System.out.println("api name while seeking" + apiName);
				   
				CacheEntry entry = new CacheEntry(
						file.getAbsolutePath(), file.getName(), sdkInformation.getSdkId(), 
						true, file.length(),  file.lastModified(),apiName.trim());
				try {
					
					if(isCanceled()){
						interrupted(Messages.getString("SeekMetaXMLFilesJob.CancelledByUser")); //$NON-NLS-1$
						return;
					}		
					if(storage.contains(entry)){
						storage.updateEntry(entry, true);					
					}else{
						storage.addEntry(entry, true);					
					}
				} catch (DuplicateEntryException e) {
					// Should not be able to occur, because if(storage.contains(entry)) check before adding
					e.printStackTrace();
				}
				catch (EntryNotFoundException e) {
					// Should not be able to occur, because if(storage.contains(entry)) check before update
					e.printStackTrace();
				}
				
	 		}	
	
			reportStartTime();
			storage.loadAllSelectedDatasToMemory(getMonitor());//There can be parsing errors, so listeners must be notified afterwards 
	
			//Notifying listeners, only one notifying should be enaugh.
			storage.notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRYS_UPDATED);
			
			//If there was some load errors.
			if(storage.isLoadErros()){
				JobMessageUtils.showLoadErrorDialog();
			}		
			reportEndTime();
			
			
			if(files == null || files.size()==0){
				showNoMetadatasWarning();			
			}		
			
			done(Status.OK_STATUS);
			
		} catch (JobCancelledByUserException e) {
			e.printStackTrace();
			//return original map 
			done(Status.CANCEL_STATUS);
		}		
	}

	/**
	 * Show warning dialog when no metadata found from selected sdk 
	 */
	private void showNoMetadatasWarning() {
		Runnable showWarning = new Runnable(){			

			public void run() {
				APIQueryMessageBox msg = new APIQueryMessageBox(
						Messages.getString("SeekMetaXMLFilesJob.MetadataNotFoundWarningMsg_Part1")  //$NON-NLS-1$
						+sdkInformation.getSdkId() 
						+Messages.getString("SeekMetaXMLFilesJob.MetadataNotFoundWarningMsg_Part2") ,  //$NON-NLS-1$
						SWT.OK| SWT.ICON_WARNING );
				msg.open();				
			}
		};
		Display.getDefault().asyncExec(showWarning);
	}

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
