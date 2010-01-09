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

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.sourcecode.SourceFileLocation;

/**
 * Job for seeking possible concrete implementation of generic component name.
 */
public class CreateProjectJob extends Job{

	/**
	 * Source file location to be used.
	 */
	private SourceFileLocation location = null;

	/**
	 * Project instance after creation. 
	 */
	private ICProject cProject;
	
	/**
	 * set to <code>true</code> if should be treated as user job, otherwise
	 * treated as workspace level system job.
	 */
	private boolean isUserJob = false;
	
	/**
	 * Creates a new project based on given source file location.
	 * @param name Job name.
	 * @param location Source location.
	 * @param isUserJob set to <code>true</code> if should be treated as user job, otherwise
	 *                  treated as workspace level system job.
	 */
	public CreateProjectJob(String name, SourceFileLocation location, boolean isUserJob) {
		super(name);
		setUser(isUserJob);
		this.isUserJob = isUserJob;
		this.location = location;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		// Informing user about progress if this is user visible foreground job
		if(isUserJob == true){
			monitor.beginTask(Messages.getString("CreateProjectJob.ShowSourceInProject_Task_Msg"), //$NON-NLS-1$
					IProgressMonitor.UNKNOWN);
		}
		// Delegating actual project creation to project utilities which 
		// takes care of error management and error reporting in case of an error.
		CProjectJobStatus stat;
		DEProjectUtils utils = new DEProjectUtils(AppDepConsole.getInstance());
		stat = utils.createProjectImpl(monitor, location);
		this.cProject = stat.getCProject();		
		IStatus status = stat.getStatus();
		if(status.getSeverity() == IStatus.ERROR){
			this.cancel();
		}
		return status;
	}
	
	/**
	 * Gets the created C++ project.
	 * @return the cProject where given source belongs to.
	 */
	public ICProject getCProject() {
		return cProject;
	}
	
}
