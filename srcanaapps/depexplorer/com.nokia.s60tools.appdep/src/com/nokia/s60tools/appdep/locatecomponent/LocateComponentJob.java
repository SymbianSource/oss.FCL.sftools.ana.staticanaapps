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


package com.nokia.s60tools.appdep.locatecomponent;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.core.data.CacheDataManager;
import com.nokia.s60tools.appdep.core.data.ICacheDataManager;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Job for seeking possible concrete implementation of generic component name.
 */
public class LocateComponentJob extends Job{

	/**
	 * Name of the component to locate. 
	 */
	private final String componentName;
	/**
	 * Concrete component list.
	 */
	private String concreteComponents[] = null;
	
	/**
	 * Constructor.
	 * @param name Job name (root component name)
	 * @param node root node
	 * @param component name
	 */
	public LocateComponentJob(String name, String componentName) {
		super(name);
		this.componentName = componentName;
		setUser(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		
		IStatus status; // Return status for the job.		
		
		try {
			locateConcreteComponent();
			status = Status.OK_STATUS;
		} catch (JobCancelledByUserException e) {
			this.cancel();
			status = Status.CANCEL_STATUS;
		} catch (Exception e) {
			e.printStackTrace();			
			String errMsg = Messages.getString("LocateComponentJob.Err_Msg") + ": " + e.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$ 
			status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,errMsg, e);
			AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
			this.cancel();	
		}
		return status;

	}	

	/**
	 * Starts process for locating concrete components.
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws CacheFileDoesNotExistException 
	 */
	private void locateConcreteComponent() 
		throws JobCancelledByUserException, 
				IOException, CacheFileDoesNotExistException
	{
		ICacheDataManager manager = CacheDataManager.getInstance();
		concreteComponents = manager
				.searchConcreteComponentsByGenericComponent(componentName);
	}

	/**
	 * Gets array located components
	 * @return array located components or <code>null</code> if not found any.
	 */
	public String[] getConcreteComponent() {
		return concreteComponents;
	}		

	
}
