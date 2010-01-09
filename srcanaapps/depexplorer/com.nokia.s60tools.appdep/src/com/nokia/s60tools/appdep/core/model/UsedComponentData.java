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

/**
 * Stores import functions and other component specific data.
 */
public class UsedComponentData {

	/**
	 * Name of the component.
	 */
	private final String componentName;
	
	/**
	 * Functions imported from the component by the using parent component.
	 */
	private SortedMap<Integer, ImportFunctionData> importedFunctions;

	/**
	 * Constructor.
	 * @param componentName Name of the component.
	 */
	public UsedComponentData(String componentName) {
		this.componentName = componentName;
		importedFunctions = new TreeMap<Integer, ImportFunctionData>();
	}
	
	/**
	 * Adds a new import function to the library.
	 * @param importFuncData Import function to be added.
	 */
	public void addImportedFunction(ImportFunctionData importFuncData) throws IllegalArgumentException {
		Integer functionOrdinalAsInteger = importFuncData.getFunctionOrdinalAsInteger();
		importedFunctions.put(functionOrdinalAsInteger, importFuncData);	
	}

	/**
	 * Gets functions imported from the component by the using parent component.
	 * @return Imported function collection in the ordinal order.
	 */
	public Collection<ImportFunctionData> getParentImportedFunctions(){
		return importedFunctions.values();
	}

	/**
	 * Gets component name.
	 * @return the componentName
	 */
	public String getComponentName() {
		return componentName;
	}
	
}
