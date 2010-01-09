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
 
package com.nokia.s60tools.appdep.locatecomponent;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.IStatus;

/**
 * Class for holding {@link IStatus} and {@link ICProject} references.
 */
public class CProjectJobStatus {
	
	/**
	 * Project job success/failure status.
	 */
	private IStatus status;
	
	/**
	 * Project object.
	 */
	private ICProject cProject;

	/**
	 * Sets job success/failure status.
	 * @return the status
	 */
	public IStatus getStatus() {
		return status;
	}

	/**
	 * Gets job success/failure status.
	 * @param status the status to set
	 */
	public void setStatus(IStatus status) {
		this.status = status;
	}

	/**
	 * Gets the handled project object.
	 * @return the cProject
	 */
	public ICProject getCProject() {
		return cProject;
	}

	/**
	 * Sets the handled project object.
	 * @param project the cProject to set
	 */
	public void setCProject(ICProject project) {
		cProject = project;
	}

}
