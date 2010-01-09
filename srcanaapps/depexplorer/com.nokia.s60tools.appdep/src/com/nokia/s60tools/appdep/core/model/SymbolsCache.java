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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.nokia.s60tools.appdep.exceptions.InvalidModelDataException;
import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Model class instance representing data that is available via symbols cache data file.
 */
public class SymbolsCache {

	/**
	 * Map that can be used to get cached library by using
	 * library base name as key.
	 */
	private Map<String, LibPropertiesData> libraryPropertiesMap;

	/**
	 * Constructor.
	 */
	public SymbolsCache(){
		libraryPropertiesMap = new HashMap<String, LibPropertiesData>();
	}
	
	/**
	 * Adds given library properties data into model.
	 * @param libPropData Library properties data to be added.
	 * @throws InvalidModelDataException 
	 */
	public void addLibPropertiesData(LibPropertiesData libPropData) throws InvalidModelDataException{
		String libBaseName = libPropData.getLibraryBaseName().toLowerCase();
		if(libraryPropertiesMap.get(libBaseName) != null){
			throw new InvalidModelDataException(Messages.getString("SymbolsCache.SymbolHasDublicateDefinition_Err_Msg")); //$NON-NLS-1$
		}
		libraryPropertiesMap.put(libBaseName, libPropData);
	}
	
	/**
	 * Returns all library properties of the cache file.
	 * The collection is not ordered.
	 * @return All library properties of the cache file.
	 */
	public Collection<LibPropertiesData> getAllLibraryProperties() {
		return libraryPropertiesMap.values();
	}

	/**
	 * Gets component properties data object for given component.
	 * @param libBaseName Base name of the library to get properties data object for.
	 * @return Component properties data object for given component or <code>null</code> if component is not in cache. 
	 */
	public LibPropertiesData getLibPropertiesData(String libBaseName){
		return libraryPropertiesMap.get(libBaseName);
	}

	/**
	 * Returns file name without file extension.
	 * @param fileNameStr File name to be remove extension from.
	 * @return Return file name without an extension, or file
	 *         name itself, if file did not have any extension.
	 */
	public static String removeFileExtension(String fileNameStr){
		int extIndex = fileNameStr.lastIndexOf("."); //$NON-NLS-1$
		if(extIndex != -1){
			return fileNameStr.substring(0, extIndex);
		}
		else{
			return fileNameStr;
		}		
	}
	
	/**
	 * Gets exported functions for the given component name.
	 * @param componentNameWithExtension Component name with an extension.
	 * @return Exported functions for the given component name.
	 */
	public Collection<ExportFunctionData> getExportedFunctionsForComponent(String componentNameWithExtension) {
		String libraryBaseName = removeFileExtension(componentNameWithExtension);
		LibPropertiesData libPropData = libraryPropertiesMap.get(libraryBaseName);
		if(libPropData == null){
			throw new NoSuchElementException(Messages.getString("SymbolsCache.ComponentNotFoundFromCache_ErrMsg") + ": '" + libraryBaseName + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return libPropData.getExportedFunctions();
	}
	
}
