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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Interface for accessing information of a single cache object.
 */
public interface ITargetCache {
	
	/**
	 * Gets cache version string.
	 * @return cache version string.
	 */
	public String getVersion();

	/**
	 * Returns components for the target.
	 * @return Component properties data collection having all components of the cache.
	 */
	public Collection<ComponentPropertiesData> getComponents();

	/**
	 * Gets the component property data for given component.
	 * @param cmpName The name of the component.
	 * @return component properties data for the component.
	 */
	public ComponentPropertiesData getComponentPropertiesForComponent(String cmpName);
	
	/**
	 * Gets the components that the given component directly depends upon.
	 * @param componentName The name of the component.
	 * @return Component properties data list.
	 */
	public List<UsedComponentData> getDirectlyDependentComponentsFor(String compToSearchFor);
	
	/**
	 * Gets the functions that are exported from the given library for the use
	 * of other components. Methods can be found from symbols table cache 
	 * file in the ordinal order 1->N.
	 * @param exportedCmpName Name of the component the functions are exported from.
	 * @return Export function data list.
	 */
	public Collection<ExportFunctionData> getExportedFunctionsForComponent(String componentNameWithExtension);
	
	/**
	 * Gets the functions that are imported from imported component for the use
	 * of parent component.
	 * @param parentCmpName Name of the parent component that imports the functions.
	 * @param importedCmpName Name of the component that the functions are imported from.
	 * @return Imported function data list.
	 */
	public Collection<ImportFunctionData> getParentImportedFunctionsForComponent(String parentCmpName, String importedCmpName);
	
	/**
	 * Gets the string array of component properties for the components
	 * that are using the given component. 
	 * @param progressCallback Job progress callback object that can be used to report progress. 
	 *                         Can be set to <code>null</code> when no progress information is needed.
	 * @param resultComponentsArrayList Array list object to return resulting components into. 
	 * @param componentName Name of the component to search using components for. 
	 * @param functionOrdinal Ordinal of the function to search using components for. 
	 *                        This parameter can be set to <code>null</code> if we are
	 *                        only interested in components that are using the given component.
	 * @param totalComponentCount Total component count forming possibly from several caches.
	 * 							  This parameter can be set to any value if <code>progressCallback</code> is set to <code>null</code>.
	 * @throws JobCancelledByUserException
	 */
	public void getUsingComponents(IJobProgressStatus progressCallback, ArrayList<ComponentPropertiesData> resultComponentsArrayList, 
			                       String componentName, String functionOrdinal, int totalComponentCount) throws JobCancelledByUserException;

	/**
	 * Returns collection of libraries stored into the cache.
	 * @return collection of libraries stored into the cache.
	 */
	public Collection<LibPropertiesData> getLibraries();

	/**
	 * Gets target platform id this target cache represents.
	 * @return target platform id this target cache represents.
	 */
	public String getId();

}
