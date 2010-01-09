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


package com.nokia.s60tools.appdep.export;

import java.io.FileNotFoundException;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Job class managing exporting UI view contents
 * into an external report.
 */
public class ExportJob extends Job implements IJobProgressStatus{

	//
	// Private members
	//
	
	//Progress indication for components, @see ExportVisitor.COMPONENTS_EXPORT_PERCENTAGE	
	private static final int PROGRESS_AFTER_PROPERTIES_PERCENTAGE = 94;
	private static final int PROGRESS_AFTER_EXPORTEDFUNCTIONS_PERCENTAGE = 96;
	private static final int PROGRESS_AFTER_XML_FILE_SAVE_PERCENTAGE = 97;
	private static final int PROGRESS_COMPLETE_PERCENTAGE = 100;
	int previousPercentage = 0;
	private IProgressMonitor monitor = null;
	private ComponentNode node;
	private String exportHTMLFileName;
	private String exportXMLFileName;	
	final int steps = 100; // percentage figure
	
	/**
	 * Constructor
	 * @param name Job name (root component name)
	 * @param node root node
	 * @param exportHTMLFileName file name for exported html report
	 * @param exportXMLFileName  file name for exported xml report
	 */
	public ExportJob(String name, 
			ComponentNode node, 
			String exportHTMLFileName, 
			String exportXMLFileName) {
		super(name);
		setUser(true);
		this.node = node;
		this.exportHTMLFileName = exportHTMLFileName;
		this.exportXMLFileName = exportXMLFileName;		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		this.monitor = monitor;
		
		IStatus status;
		
		String msg = Messages.getString("ExportJob.Exporting_Msg") +node.getName(); //$NON-NLS-1$
		monitor.beginTask(msg , steps);
		
		try {
			long runMethodStartTime = reportStartTime();
			export();
			reportEndTime(runMethodStartTime);
			status = Status.OK_STATUS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
			status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,Messages.getString("ExportJob.Export_Failed_Msg") + "! " + Messages.getString("ExportJob.File_Not_Found_Msg") + ": " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			AppDepConsole.getInstance().println(Messages.getString("ExportJob.Export_Failed_Msg") + "! " +  Messages.getString("ExportJob.File_Not_Found_Msg") + ": " + e.getMessage(),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					                            IConsolePrintUtility.MSG_ERROR);
			this.cancel();	
		} catch (JobCancelledByUserException e) {
			this.cancel();
			status = Status.CANCEL_STATUS;
		} catch (TransformerException e) {
			e.printStackTrace();
			this.cancel();	
			status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,Messages.getString("ExportJob.Export_Failed_Msg") + "! " +  Messages.getString("ExportJob.Transform_Not_Complete_Msg") + ": " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			AppDepConsole.getInstance().println(Messages.getString("ExportJob.Export_Failed_Msg") + "! " +  Messages.getString("ExportJob.Transform_Not_Complete_Msg") + ": "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
												 + e.getMessage(),
					                             IConsolePrintUtility.MSG_ERROR);
		}
		
		return status;

	}
	
	/**
	 * Reports start time of the export.
	 * @return start time of the export.
	 */
	private long reportStartTime(){
		long runMethodStartTime = System.currentTimeMillis();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
							"Export started: "  //$NON-NLS-1$
							+ new Date(runMethodStartTime).toString());	
		return runMethodStartTime;	
	}
	
	/**
	 * Reports end time and time elapsed for the export.
	 * @param runMethodStartTime start time of the export
	 */
	private void reportEndTime(long runMethodStartTime){
		long endTime = System.currentTimeMillis();		
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
				"Export ended: " + new Date(endTime).toString()); //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
						  "TOTAL: "  //$NON-NLS-1$
				           + (endTime-runMethodStartTime)/1000.0 + " seconds!");		 //$NON-NLS-1$
	}
	
	/**
	 * Exports XML data.
	 * @throws JobCancelledByUserException
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	private void export() 
		throws JobCancelledByUserException, 
				FileNotFoundException, 
				TransformerException
	{
		AppDepSettings st = AppDepSettings.getActiveSettings();
		SdkInformation sdkInfo = st.getCurrentlyUsedSdk();

		progress(0, Messages.getString("ExportJob.Components_Str")); //$NON-NLS-1$
		
		ExportVisitor visitor = new ExportVisitor(sdkInfo.getSdkId(), 
				st.getCurrentlyUsedTargetPlatformsAsString(),
				st.getBuildType().getBuildTypeDescription(), 
				node.getName(), node.getFullName(),
				this);

		//Progress will go up to ExportVisitor.COMPONENTS_EXPORT_PERCENTAGE = 85		
		((ComponentParentNode)node).accept(visitor);
		
		visitor.createProperties();

		progress(PROGRESS_AFTER_PROPERTIES_PERCENTAGE, Messages.getString("ExportJob.Exported_Functions_Str")); //$NON-NLS-1$
				
		visitor.createExportedFunctions();

		progress(PROGRESS_AFTER_EXPORTEDFUNCTIONS_PERCENTAGE, Messages.getString("ExportJob.XML_Str")); //$NON-NLS-1$
		
		XMLUtils.parseXML(visitor.toString(), exportHTMLFileName);
	
		//Currently no one will
		if(AppDepSettings.getActiveSettings().isExportXMLreport()){
			progress(PROGRESS_AFTER_XML_FILE_SAVE_PERCENTAGE, Messages.getString("ExportJob.File_Writing_Str"));		 //$NON-NLS-1$
			visitor.toFile(exportXMLFileName);
		}

		progress(PROGRESS_COMPLETE_PERCENTAGE, Messages.getString("ExportJob.Complete_Str")); //$NON-NLS-1$				
	
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.IJobProgressStatus#progress(int, java.lang.String)
	 */
	public void progress(int percentage, String prosessing) 
		throws JobCancelledByUserException {
				
        if (isCanceled()){
        	String msg = Messages.getString("ExportJob.Export_Canceled_By_User_Msg")  //$NON-NLS-1$
        		         + "."; //$NON-NLS-1$
        	AppDepConsole.getInstance().println(msg);    				
		    throw new JobCancelledByUserException(msg);
        }

        monitor.subTask(percentage + Messages.getString("ExportJob.Percent_Complete_Msg")  //$NON-NLS-1$
        		        + "."  //$NON-NLS-1$
        		        + Messages.getString("ExportJob.Processing_Msg")  //$NON-NLS-1$
        		        + ": "  //$NON-NLS-1$
        		        + prosessing);			
        monitor.worked(percentage - previousPercentage);
        
        previousPercentage = percentage;

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.IJobProgressStatus#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}	
	
}
