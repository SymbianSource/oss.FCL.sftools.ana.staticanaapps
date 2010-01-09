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


package com.nokia.s60tools.appdep.core.job;

import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * Extends manageable job for providing possibility to 
 * query for target SDK for the cache generation job. 
 */
public interface ICacheGenerationJob extends IManageableJob{
	
	/**
	 * Gets SDK for the generation job.
	 * @return SDK for the generation job.
	 */
	public SdkInformation getTargetSdkForJob();
	
	/**
	 * Gets target platform for the generation job.
	 * @return Target platform for the generation job.
	 */
	public ITargetPlatform[] getTargetPlatformForJob();
	
	/**
	 * Gets build type for the generation job.
	 * @return Build type for the generation job.
	 */
	public IBuildType getBuildTypeForJob();
}
