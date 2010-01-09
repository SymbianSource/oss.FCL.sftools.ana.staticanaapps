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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;
import com.nokia.s60tools.util.sourcecode.ProjectUtils;
import com.nokia.s60tools.util.sourcecode.SourceFileLocation;

/**
 * Utility methods for handling Carbide/Eclipse projects, uses ProjectUtils class
 * and captures IStatus messages so that project creation can be used outside {@link CreateProjectJob}.
 */
public class DEProjectUtils extends ProjectUtils{
	
	/**
	 * Constructor
	 * @param printUtility console for printing warning info, and error messages
	 */
	public DEProjectUtils(IConsolePrintUtility printUtility) {
		super(printUtility);
	}
	
	/**
	 * Creates project pointed by the given bld.inf file.
	 * @param monitor Job progress monitor.
	 * @param bldInfFilePath Absolute path to bld.inf file
	 * @return Project creation status object.
	 */
	public CProjectJobStatus createProjectImpl(IProgressMonitor monitor, String bldInfFilePath) {
		return runImpl(monitor, null, bldInfFilePath);
	}
	/**
	 * Creates project pointed by the given source file location.
	 * @param monitor Job progress monitor.
	 * @param location Source file location.
	 * @return Project creation status object.
	 */
	public CProjectJobStatus createProjectImpl(IProgressMonitor monitor,  SourceFileLocation location) {
		return runImpl(monitor, location, null);
	}
	
	/**
	 * Creates project pointed by the given bld.inf file and source file location.
	 * @param monitor Job progress monitor.
	 * @param location Source file location. Used as default input info, if defined.
	 *                 If set to <code>null</code> using bld.inf file instead.
	 * @param bldInfFilePath Absolute path to bld.inf file
	 * @return Project creation status object.
	 */
	private CProjectJobStatus runImpl(IProgressMonitor monitor, SourceFileLocation location, String bldInfFilePath) {
		IStatus status = null;		
		ICProject cProject = null;
		CProjectJobStatus ret = new CProjectJobStatus();
		try {
			AppDepSettings settings = AppDepSettings.getActiveSettings();
			SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();		
			
			if(location != null){
				cProject = createProject(monitor, location, sdkInfo.getSdkId());
			}else{
				cProject = createProject(monitor, bldInfFilePath, sdkInfo.getSdkId());
			}
			// Creation went OK, if we end up here.
			status = Status.OK_STATUS;
		} catch (JobCancelledByUserException e) {
			status = Status.CANCEL_STATUS;
		} catch (Exception e) {
			e.printStackTrace();
			String errMsg = Messages.getString("CreateProjectJob.Err_Msg"); //$NON-NLS-1$
			status = new Status(
					Status.ERROR,Platform.PI_RUNTIME,
					Status.ERROR,errMsg, e);
			AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);

		}finally{
			ret.setCProject(cProject);
			ret.setStatus(status);
		}
		return ret;
	}	
}
