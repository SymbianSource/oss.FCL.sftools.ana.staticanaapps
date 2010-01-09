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

package com.nokia.s60tools.appdep.core.model;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Stores exported functions and other library specific data.
 */
public class LibPropertiesData {

	/**
	 * Base name of the library without extension (*.dso or *.lib).
	 */
	private final String libraryBaseName;
	/**
	 * Timestamp of the library stored into symbols cache file.
	 */
	private final long libraryCacheTimestamp;
	
	/**
	 * Functions exported by library.
	 */
	private SortedMap<Integer, ExportFunctionData> exportedFunctions;

	/**
	 * Constructor.
	 * @param libraryBaseName Base name of the library without extension (*.dso or *.lib).
	 * @param libraryCacheTimestamp Timestamp of the library stored into symbols cache file.
	 */
	public LibPropertiesData(String libraryBaseName, long libraryCacheTimestamp) {
		this.libraryBaseName = libraryBaseName;
		this.libraryCacheTimestamp = libraryCacheTimestamp;	
		exportedFunctions = new TreeMap<Integer, ExportFunctionData>();
	}
	
	/**
	 * Adds a new export function to the library.
	 * @param exportFuncData Export function to be added.
	 */
	public void addExportedFunction(ExportFunctionData exportFuncData) throws IllegalArgumentException {
		Integer functionOrdinalAsInteger = exportFuncData.getFunctionOrdinalAsInteger();
		if(exportedFunctions.get(functionOrdinalAsInteger) != null){
			throw new IllegalArgumentException(Messages.getString("LibPropertiesData.FunctionOrdinalMustBeUnique_ErrMsg")); //$NON-NLS-1$
		}
		exportedFunctions.put(functionOrdinalAsInteger, exportFuncData);	
	}

	/**
	 * Gets base name of the library without extension  (*.dso or *.lib).
	 * @return Base name of the library without extension.
	 */
	public String getLibraryBaseName() {
		return libraryBaseName;
	}

	/**
	 * Gets library cache timestamp.
	 * @return the libraryCacheTimestamp
	 */
	public long getLibraryCacheTimestamp() {
		return libraryCacheTimestamp;
	}
	
	/**
	 * Gets functions exported by the library.
	 * @return Exported function collection in the ordinal order.
	 */
	public Collection<ExportFunctionData> getExportedFunctions(){
		return exportedFunctions.values();
	}
}
