/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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
package com.nokia.s60tools.appdep.core.data;

/**
 * Storage class used to store cache index creation
 * request related data.
 */
public class CacheIndexCreationRequest{

	/**
	 * Absolute path name to cache file.
	 */
	private final String cacheFile;
	/**
	 * Absolute path name to build directory..
	 */
	private final String buildDir;

	/**
	 * Constructor.
	 * @param cacheFile Absolute path name to cache file.
	 * @param buildDir Absolute path name to build directory..
	 */
	public CacheIndexCreationRequest(String cacheFile, String buildDir){
		this.cacheFile = cacheFile;
		this.buildDir = buildDir;			
	}

	/**
	 * Gets build directory name.
	 * @return build directory name.
	 */
	public String getBuildDir() {
		return buildDir;
	}

	/**
	 * Gets cache file name.
	 * @return cache file name.
	 */
	public String getCacheFile() {
		return cacheFile;
	}
}