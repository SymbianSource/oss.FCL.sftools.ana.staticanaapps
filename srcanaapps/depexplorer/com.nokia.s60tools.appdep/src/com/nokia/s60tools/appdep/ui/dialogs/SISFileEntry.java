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
 
package com.nokia.s60tools.appdep.ui.dialogs;

import java.io.File;


/**
 * Stores information on a single SIS file entry.
 */
public class SISFileEntry {
	
	//
	// Column sorting indices for table column sorter
	//
	public static final int NAME_COLUMN_INDEX = 0;
	public static final int LOCATION_COLUMN_INDEX = 1;
	
	/**
	 * Path name of the directory SIS file is locating.
	 */
	private final String locationPath;
	
	/**
	 * Name of the SIS file without path. 
	 */
	private final String fileName;
		
	/**
	 * Constructor.
	 * @param locationPath path name of the directory SIS file is locating.
	 * @param fileName name of the SIS file without path.
	 */
	public SISFileEntry(String locationPath, String fileName){
		this.locationPath = locationPath;
		this.fileName = fileName;		
	}

	/**
	 * Gets path name of the directory SIS file is locating.
	 * @return path name of the directory SIS file is locating.
	 */
	public String getLocation() {
		return locationPath;
	}

	/**
	 * Gets name of the SIS file without path. 
	 * @return name of the SIS file without path.
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Gets name of the SIS file with absolute path. 
	 * @return name of the SIS file with absolute path.
	 */
	public String getFullPathFileName() {
		return getLocation() + File.separator + getFileName();
	}
	
}
