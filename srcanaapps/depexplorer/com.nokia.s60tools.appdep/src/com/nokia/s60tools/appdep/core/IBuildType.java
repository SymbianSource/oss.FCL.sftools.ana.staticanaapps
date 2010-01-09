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
 
 

package com.nokia.s60tools.appdep.core;

/**
 * Common interface for all the supported 
 * build types.
 */
public interface IBuildType {
	
	/**
	 * Returns the build type name that matches with
	 * the build directory name.
	 * @return Build type name.
	 */
	public String getBuildTypeName();
	
	/**
	 * Returns a short description of the build type.
	 * @return Build type description.
	 */
	public String getBuildTypeDescription();
	
	/**
	 * @return <code>true</code> if build types equals, otherwise <code>false</code>.
	 */
	public boolean equals(IBuildType buildType);
}
