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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.exceptions.InvalidModelDataException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Model class instance representing data that is available via dependencies cache data file.
 */
public class DependenciesCache {

	/**
	 * Map that can be used to get cached component by using
	 * component name (=Filename) as key.
	 * All map keys should be stored in lower case.
	 */
	private Map<String, ComponentPropertiesData> componentPropertiesMap;

	/**
	 * Constructor.
	 */
	public DependenciesCache(){
		componentPropertiesMap = new HashMap<String, ComponentPropertiesData>();
	}
	
	/**
	 * Adds given component properties data into model.
	 * @param cPropData Component properties data to be added.
	 */
	public void addComponentPropertiesData(ComponentPropertiesData cPropData,String targetPlatformId) {
		String compName = cPropData.getFilename().toLowerCase(); // Map keys are always stored in lower case
		if(componentPropertiesMap.get(compName) == null){
			// Only accepting the 1st instance of a component with the same name
			componentPropertiesMap.put(compName, cPropData);
		}
		else{
			// Debug reporting duplicate
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Duplicate component detected: "+ cPropData.getFilename()//$NON-NLS-1$
																							+" ["+targetPlatformId+"]");//$NON-NLS-1$ //$NON-NLS-2$
		}
		
	}
	
	/**
	 * Returns all component properties of the cache file.
	 * The collection is not ordered.
	 * @return All component properties of the cache file.
	 */
	public Collection<ComponentPropertiesData> getAllComponentProperties() {
		return componentPropertiesMap.values();
	}

	/**
	 * Gets component properties data object for given component.
	 * @param compName Name of the component to get properties data object for.
	 * @return Component properties data object for given component, or throws <code>NoSuchElementException</code> 
	 *                   if component is not in cache. 
	 * @throws NoSuchElementException
	 */
	public ComponentPropertiesData getComponentPropertiesData(String compName){
		// Map keys are always stored in lower case
		ComponentPropertiesData cmpPropData = componentPropertiesMap.get(compName.toLowerCase());
		if(cmpPropData == null){
			throw new NoSuchElementException(Messages.getString("DependenciesCache.ComponentNotFoundFromCache_ErrMsg") + ": '" + compName + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return cmpPropData;
	}

	/**
	 * Gets the components that the given component directly depends upon.
	 * @param componentName The name of the component.
	 * @return Component properties data list.
	 */
	public List<UsedComponentData> getDirectlyDependentComponentsFor(String compToSearchFor) {
		ComponentPropertiesData cmpPropData = getComponentPropertiesData(compToSearchFor);
		return cmpPropData.getUsedComponentList();
	}

	/**
	 * Gets the functions that are imported from imported component for the use
	 * of parent component.
	 * @param parentCmpName Name of the parent component that imports the functions.
	 * @param importedCmpName Name of the component that the functions are imported from.
	 * @return Imported function data list.
	 */
	public Collection<ImportFunctionData> getParentImportedFunctionsForComponent(String parentCmpName, String importedCmpName) {
		ComponentPropertiesData cmpPropData = getComponentPropertiesData(parentCmpName);
		Collection<UsedComponentData> usedComponentList = cmpPropData.getUsedComponentList();
		for (UsedComponentData usedComponentData : usedComponentList) {
			String componentName = usedComponentData.getComponentName();
			if(componentName.equalsIgnoreCase(importedCmpName)){
				return usedComponentData.getParentImportedFunctions();
			}
		}
		throw new NoSuchElementException(Messages.getString("DependenciesCache.ImportedComponentNotFoundFromCache_ErrMsgStart") + importedCmpName  //$NON-NLS-1$
											+ Messages.getString("DependenciesCache.ImportedComponentNotFoundFromCache_ErrMsgEnd")  //$NON-NLS-1$
											+ parentCmpName + "'."); //$NON-NLS-1$
	}

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
	 * @throws JobCancelledByUserException 
	 */
	public void getUsingComponents(IJobProgressStatus progressCallback,
			ArrayList<ComponentPropertiesData> resultComponentsArrayList,
			String componentName, String functionOrdinal,
			int totalComponentCount) throws JobCancelledByUserException {
		
		boolean reportProgress = (progressCallback != null) && (totalComponentCount > 0);
		
		// Variables used to inform about the progress
		final int hundred = 100;
		// Calculating 10 percentage of components from all the components in cache
		int hundredComponentsAsPercentage = reportProgress ? (hundred * hundred) / totalComponentCount: 0;
		int totalPercentage = 0;
		int componentCount = 0;
		
		boolean isFunctionUsageCheck = (functionOrdinal != null);
		Collection<ComponentPropertiesData> componentsColl = componentPropertiesMap.values();
		for (ComponentPropertiesData cmpPropData : componentsColl) {
			// Reporting progress if queried about the seeking of using components
			if(reportProgress){
				// Updating component count
				componentCount++;
				// Informing about the progress
				if((componentCount % hundred) == 0){
					totalPercentage = totalPercentage + hundredComponentsAsPercentage;
					progressCallback.progress(totalPercentage, null);				
				}
			}			
			boolean isMatch = cmpPropData.usesComponent(componentName); 			
			if(isMatch){
				// This component is using the queried component
				if(isFunctionUsageCheck){
					UsedComponentData usedCmpData = cmpPropData.getUsedComponent(componentName);
					Collection<ImportFunctionData> parentImportedFunctions = usedCmpData.getParentImportedFunctions();
					for (ImportFunctionData importFunctionData : parentImportedFunctions) {
						String functionOrdinalToCompare = importFunctionData.getFunctionOrdinal();
						if(functionOrdinalToCompare.equals(functionOrdinal)){
							// This component is using the queried function
							resultComponentsArrayList.add(cmpPropData);
							break; // We can break this inner for loop and continue outermost one
						}
					}
				}
				else{
					// This component is using the queried component
					resultComponentsArrayList.add(cmpPropData);
				}				
			}
		}
	}
	
}

