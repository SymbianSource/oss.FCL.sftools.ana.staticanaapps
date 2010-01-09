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

package com.nokia.s60tools.appdep.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.core.model.AbstractFunctionData;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.core.model.UsedComponentData;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.search.MatchType;
import com.nokia.s60tools.appdep.search.SearchConstants.SearchType;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Interface for accessing cache data manager object itself.
 */
public interface ICacheDataManager {
	
	/**
	 * Searches from cache with wanted parameters.
	 * 
	 * @param searchString String to search for.
	 * @param searchType what kind of information wanted to search.
	 * @param matchType how searchString must be occur in results.
	 * @return Map of matching results or empty list if none found. Map contains component names as keys
	 * and function data list as values. Values are <code>null</code>s when search type is {@link SearchType.SEARCH_COMPONENTS}
	 * and instances of {@link ExportFunctionData} when search type is {@link SearchType.SEARCH_EXPORTED_FUNCTION}
	 * and instances of {@link ImportFunctionData} when search type is {@link SearchType.SEARCH_IMPORTED_FUNCTIONS}
	 * 
	 */
	public Map<String, List<AbstractFunctionData>> searchCache(String searchString, SearchType searchType, MatchType matchType);
	
	/**
	 * Searches concrete component implementations for generic component name. 
	 * Results will be a list of component that has given component names
	 * as suffix.
	 * 
	 * E.g. if given generic component name is <code>hal.dll</code> returned list can be:
	 * <code>
	 *   _h2_hal.dll
	 *   _h4hrp_hal.dll
	 *   _integrator_cm1136_hal.dll
	 *   _template_hal.dll
	 * </code>
	 * 
	 * @param genericComponentName Name of the generic component
	 * @return a String array of concrete component names found in used SDK, or empty array if none found. 
	 */
	public String [] searchConcreteComponentsByGenericComponent(String genericComponentName);
	
	/**
	 * Gets the name of the concrete implementation by generic component name and prefix list.
	 * 
	 * E.g. if prefix list is: 
	 * <code>
	 *   _h2_
	 *   _h4hrp_
	 *   _integrator_cm1136_
	 *   _template_
	 * </code>
	 * And wanted component name name is <code>hal.dll</code>. When in used <code>sdk</code>
	 * there is components with prefixes given and component name as suffix:
	 * <code>
	 *   _h2_hal.dll
	 *   _h4hrp_hal.dll
	 *   _integrator_cm1136_hal.dll
	 *   _template_hal.dll
	 * </code>
	 * Result will be <code>_h2_hal.dll</code> because <code>_h2_</code> prefix was first in prefix list.  
	 * 
	 * @param prefixList Prefix list.
	 * @param componentName Component name.
	 * @return First component matching prefix list by in order they appear or <code>null</code> if can't found any.
	 */
	public String searchComponentWithPrefix(List<String> prefixList, String componentName);

	/**
	 * Gets the component properties for the given component name.
	 * Delegates call further to corresponding method in <code>ITargetCacher</code> interface.
	 * @param cmpName The name of the component.
	 * @param targetPlatformRestriction Component must belong to the given target platform (armv5, armv9e etc.).
	 *                                  If set to <code>null</code>, match is made only based on component name.
	 * @return component properties data for the component, or <code>null</code> if component was not found.
	 * @throws CacheIndexNotReadyException 
	 * @throws IOException 
	 */
	public ComponentPropertiesData getComponentPropertyArrayForComponent(String cmpName, ITargetPlatform targetPlatformRestriction) throws CacheIndexNotReadyException, IOException;
	
	/**
	 * Gets the components that the given component directly depends upon.
	 * Delegates call further to corresponding method in <code>ITargetCacher</code> interface.
	 * @param componentName The name of the component.
	 * @return The string array of component name that are directly depended upon.
	 * @throws IOException 
	 */
	public List<UsedComponentData> getDirectlyDependentComponentsFor(String compToSearchFor) throws IOException;
	
	/**
	 * Gets the functions that are exported from the given library for the use
	 * of other components. Methods can be found from symbols table cache 
	 * file in the ordinal order 1->N.
	 * Delegates call further to corresponding method in <code>ITargetCacher</code> interface.
	 * @param exportedCmpName Name of the component the functions are exported from.
	 * @return String array of exported function info lines without any parsing.
	 * @throws CacheIndexNotReadyException 
	 * @throws IOException 
	 */
	public Collection<ExportFunctionData> getExportedFunctionsForComponent(String componentNameWithExtension) throws CacheIndexNotReadyException, IOException;
	
	/**
	 * Gets the functions that are imported from imported component for the use
	 * of parent component.
	 * Delegates call further to corresponding method in <code>ITargetCacher</code> interface.
	 * @param parentCmpName Name of the parent component that imports the functions.
	 * @param importedCmpName Name of the component that the functions are imported from.
	 * @return String array of imported function info lines without any parsing.
	 * @throws CacheIndexNotReadyException 
	 * @throws IOException 
	 */
	public Collection<ImportFunctionData> getParentImportedFunctionsForComponent(String parentCmpName, String importedCmpName) throws CacheIndexNotReadyException, IOException;
	
	/**
	 * Gets the string array of component properties for the components
	 * that are using the given component. 
	 * Delegates call further to corresponding method in <code>ITargetCacher</code> interface.
	 * @param progressCallback Job progress callback interface.
	 * @param resultComponentsArrayList Array list objects to return resulting components into. 
	 * @param componentName Name of the component to search using components for. 
	 * @param functionOrdinal Ordinal of the function to search using components for. 
	 *                        This parameter can be set to <code>null</code> if we are
	 *                        only interested in components that are using the given component.
	 * @throws JobCancelledByUserException 
	 * @throws IOException 
	 * @throws CacheIndexNotReadyException 
	 */
	public void getUsingComponents(IJobProgressStatus progressCallback, ArrayList<ComponentPropertiesData> resultComponentsArrayList, String componentName, String functionOrdinal) throws JobCancelledByUserException, IOException;
	
}
