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


package com.nokia.s60tools.apiquery.shared.job;

import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Abstract implementation of {@link Job}. Implements few functionalities so sub classes
 * can be written with less code.
 */
public abstract class AbstractJob extends Job implements IJobProgressStatus, IManageableJob{

	protected static final int PROGRESS_COMPLETED_PERCENTAGE = 100;
	protected int previousPercentage = 0;
	private IProgressMonitor monitor = null;
	private Process process = null;
	private long runMethodStartTime; 
	
	protected int steps = 100; // percentage figure
	
	/**
	 * Use only this constructor
	 * 
	 * @param name Job name (root component name)
	 */
	public AbstractJob(String name) {
		super(name);		
	}

	/**
	 * Get start time and print it
	 * @return start time in ms (System.currentTimeMillis())
	 */
	public long reportStartTime(){
		this.runMethodStartTime = System.currentTimeMillis();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
							"Job: '" +super.getName() + "' start time: "   //$NON-NLS-1$ //$NON-NLS-2$
							+ new Date(runMethodStartTime).toString());	
		return runMethodStartTime;	
	}
	
	/**
	 * count time taken since startTime, printing it
	 * @param runMethodStartTime
	 */
	public void reportEndTime(){
		long endTime = System.currentTimeMillis();		
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
				"Job " +super.getName() + " ended: " + new Date(endTime).toString());  //$NON-NLS-1$ //$NON-NLS-2$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
						  "TOTAL: "  //$NON-NLS-1$
				           + (endTime-runMethodStartTime)/1000.0 + " seconds!"); //$NON-NLS-1$
	}
	


	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.job.IJobProgressStatus#progress(int, java.lang.String)
	 */
	public void progress(int percentage, String prosessing) 
		throws JobCancelledByUserException {
				
        if (isCanceled()){
        	String msg = Messages.getString("AbstractJob.JobCancelled_Part1_ErrMsg") +super.getName() + Messages.getString("AbstractJob.JobCancelled_Part2_ErrMsg"); //$NON-NLS-1$ //$NON-NLS-2$
		    throw new JobCancelledByUserException(msg);
        }
        //Making sure that there is no percentages higher than 100
        if(percentage > PROGRESS_COMPLETED_PERCENTAGE){
        	percentage = PROGRESS_COMPLETED_PERCENTAGE;
        }
        
        monitor.subTask(percentage + Messages.getString("AbstractJob.Percentage_Complete_Msg")        		         //$NON-NLS-1$
        		        + Messages.getString("AbstractJob.Prosessing_Msg") //$NON-NLS-1$
        		        + prosessing);			
        monitor.worked(percentage - previousPercentage);
        previousPercentage = percentage;

	}


	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.job.IJobProgressStatus#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}	
	

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.job.IManageableJob#forcedShutdown()
	 */
	public void forcedShutdown() {
		if(process != null){
			process.destroy();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}					
		}
	}


	/**
	 * Get Process
	 * @return Process
	 */
	protected Process getProcess() {
		return process;
	}


	/**
	 * Set process
	 * @param process
	 */
	protected void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * Get progress monitor
	 * @return
	 */
	protected IProgressMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Set progress monitor to this Job
	 * @param monitor
	 */
	protected void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
}
