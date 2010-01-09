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
 

package com.nokia.s60tools.appdep.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.core.model.AbstractFunctionData;
import com.nokia.s60tools.appdep.core.model.CacheFactory;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ICacheLoadProgressNotification;
import com.nokia.s60tools.appdep.core.model.ITargetCache;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.core.model.UsedComponentData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.search.MatchType;
import com.nokia.s60tools.appdep.search.MatchType.MatchTypes;
import com.nokia.s60tools.appdep.search.SearchConstants.SearchType;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Singleton facade class for accessing services of <code>ITargetCache</code> interface 
 * that can handle only single cache file related searches. This manager class
 * makes possible to handle searches from settings containing multiple targets.
 */
public class CacheDataManager implements ICacheDataManager {

	//
	// Class variables
	//
	
	/**
	 * Settings object this manager instance gets its settings data from.
	 */
	private static AppDepSettings settings = null;

	/**
	 * Storing each target cache interface reference for getting component data.
	 */
	private static List<ITargetCache> targetCacheList = null;

	/**
	 * Access to Singleton instance.
	 */
	private static CacheDataManager instance = null;

	/**
	 * Constructor (private).
	 * Method <code>loadCache</code> is used to initialize the Singleton instance 
	 * with proper data and <code>getInstance</code> later to access the initialized instance. 
	 */
	private CacheDataManager(){
	}
	
	/**
	 * Creating cache data manager that uses given settings.
	 * @param settings Settings object to get user's selections from.
	 * @throws CacheFileDoesNotExistException
	 * @throws IOException
	 */
	public static ICacheDataManager getInstance() throws CacheFileDoesNotExistException, IOException{
		
		checkInstanceAccessPreconditions();
		
		if(instance == null){
			instance = new CacheDataManager();
		}
		return instance;
	}

	/**
	 * Checks that Singleton instance has been properly configured
	 */
	private static void checkInstanceAccessPreconditions() {
		if(
			settings == null
			||
			targetCacheList.size() < 1
			){
			// This failure happens in case cache data loading has failed due to some error logged elsewhere in code.
			String errMsg = Messages.getString("CacheDataManager.TriedToAccessUninitializedSingletonInstance_ErrMsg") //$NON-NLS-1$
										+ "'"  //$NON-NLS-1$
										+ CacheDataManager.class.getSimpleName()
										+ "' " //$NON-NLS-1$
										+ Messages.getString("CacheDataManager.TargetCacheListSizeNegative_ErrMsg") //$NON-NLS-1$
										+ "(" //$NON-NLS-1$
										+ Messages.getString("CacheDataManager.Size_Str")   //$NON-NLS-1$
										+ "=" //$NON-NLS-1$
										+ targetCacheList.size() 
										+ ")."; //$NON-NLS-1$
			AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
			throw new RuntimeException(errMsg);			
		}		
	}

	/**
	 * Creates cache data manager that uses given settings.
	 * @param settings Settings object to get user's selections from.
	 * @param notifierRequestor If not <code>null</code> the client is requesting notifications about component loading process.
	 * @throws CacheFileDoesNotExistException
	 * @throws IOException
	 */
	public static void loadCache(AppDepSettings loadSettings, ICacheLoadProgressNotification notifierRequestor) throws CacheFileDoesNotExistException, IOException{
		settings = loadSettings; // New settings override the old settings
		targetCacheList = new ArrayList<ITargetCache>();
		ITargetPlatform[] targets = settings.getCurrentlyUsedTargetPlatforms();
		for (int i = 0; i < targets.length; i++) {
			ITargetPlatform targetPlatform = targets[i];
			String targetPlatformId = targetPlatform.getId();
			String cacheDirForTarget = settings.getCacheDirForTarget(targetPlatformId);			
			ITargetCache loadedTargetCache = CacheFactory.getInstance().loadCache(cacheDirForTarget, targetPlatformId, notifierRequestor);			
			targetCacheList.add(loadedTargetCache);
			}		
	}

	/**
	 * This can be used to reset current settings.
	 * This is mainly needed by JUnit tests for testing condition 
	 * of accessing uninitialized manager instance.
	 */
	public static void reset(){
		settings = null;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getComponentPropertyArrayForComponent(java.lang.String)
	 */
	public ComponentPropertiesData getComponentPropertyArrayForComponent(String cmpName, ITargetPlatform targetPlatformRestriction) throws CacheIndexNotReadyException, IOException {
		
		ComponentPropertiesData results = null;
		// Combining information from caches of all selected targets 
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			try {
				// Checking if target platform must match
				if(targetPlatformRestriction != null && !targetPlatformRestriction.idEquals(targetCache.getId())){
					continue; // target platform match is required, and not matching => skipping this target cache
				}				
				// Search keys for accessing model data should be lower-case to overcome possible name mismatches
				results = targetCache.getComponentPropertiesForComponent(cmpName.toLowerCase());
				return results;
			} catch (NoSuchElementException e) {
				// If this was the last cache reader on the list informing the client via exception 
				// that the component was not found, otherwise continuing for the next reader instance.
				if(i == (targetCacheList.size()-1)){
					throw e;					
				}
			}			
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getDirectlyDependentComponentsFor(java.lang.String)
	 */
	public List<UsedComponentData> getDirectlyDependentComponentsFor(String compToSearchFor) throws IOException{

		List<UsedComponentData> results = null;		
		
		// Combining information from caches of all selected targets 
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			try {
				// Search keys for accessing model data should be lower-case to overcome possible name mismatches
				results = targetCache.getDirectlyDependentComponentsFor(compToSearchFor.toLowerCase());
				return results;
			} catch (NoSuchElementException e) {
				// If this was the last cache reader on the list informing the client via exception 
				// that the component was not found, otherwise continuing for the next reader instance.
				if(i == (targetCacheList.size()-1)){
					throw e;					
				}
			}			
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getExportedFunctionsForComponent(java.lang.String)
	 */
	public Collection<ExportFunctionData> getExportedFunctionsForComponent(String componentNameWithExtension) throws CacheIndexNotReadyException, IOException {
		Collection<ExportFunctionData> results = null;		
		
		// Combining information from caches of all selected targets 
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			try {
				// Search keys for accessing model data should be lower-case to overcome possible name mismatches
				results = targetCache.getExportedFunctionsForComponent(componentNameWithExtension.toLowerCase());;
				return results;
			} catch (NoSuchElementException e) {
				// If this was the last cache reader on the list informing the client via exception 
				// that the component was not found, otherwise continuing for the next reader instance.
				if(i == (targetCacheList.size()-1)){
					throw e;					
				}
			}			
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getParentImportedFunctionsForComponent(java.lang.String, java.lang.String)
	 */
	public Collection<ImportFunctionData> getParentImportedFunctionsForComponent(String parentCmpName, String importedCmpName) throws CacheIndexNotReadyException, IOException {
		Collection<ImportFunctionData> results = null;		
		
		// Combining information from caches of all selected targets 
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			try {
				// Search keys for accessing model data should be lower-case to overcome possible name mismatches.
				results = targetCache.getParentImportedFunctionsForComponent(parentCmpName.toLowerCase(), importedCmpName.toLowerCase());
				return results;
			} catch (NoSuchElementException e) {
				// If this was the last cache reader on the list informing the client via exception 
				// that the component was not found, otherwise continuing for the next reader instance.
				if(i == (targetCacheList.size()-1)){
					throw e;					
				}
			}			
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getUsingComponents(com.nokia.s60tools.appdep.core.job.IJobProgressStatus, java.util.ArrayList, java.lang.String, java.lang.String)
	 */
	public void getUsingComponents(IJobProgressStatus progressCallback, ArrayList<ComponentPropertiesData> resultComponentsArrayList, String componentName, String functionOrdinal) throws JobCancelledByUserException, IOException {
		
		int totalComponentCount = 0;
		// Calculating the total component count for all caches
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			totalComponentCount += targetCache.getComponents().size();
		}
		// Getting using components for each cache and adding them to results array list
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);
			targetCache.getUsingComponents(progressCallback, resultComponentsArrayList, componentName, functionOrdinal, totalComponentCount);
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#searchComponentWithPrefix(java.util.List, java.lang.String)
	 */
	public String searchComponentWithPrefix(List<String> prefixList,
			String componentName) {
				
		if( prefixList == null || prefixList.isEmpty() ){
			return null;
		}
		
		String[] prefixes = new String[prefixList.size()];
		int j = 0;
		
		//Make map where is prefixes as lower case and in order they appear in users defined list
		for (String pref : prefixList) {
			prefixes[j] = pref;
			j++;
		}
		
		
		// Combining information from caches of all selected targets 
		Map<String, ComponentPropertiesData> compsWithWantedName = new HashMap<String, ComponentPropertiesData>();
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);

			Collection<ComponentPropertiesData> components = targetCache
					.getComponents();
			
			for (ComponentPropertiesData data : components) {
				
				// if this component name ends with component name wanted:
				String compName = data.getFilename().toLowerCase();
				if (compName.endsWith(componentName.toLowerCase())) {
					// If first component in prefix list is found, we can just
					// return it, otherwise keep on seeking
					// because if we found 3rd or 4th on prefix list, we cannot
					// know it there is a 1st or 2nd on the list.
					if (compName.startsWith(prefixes[0])) {
						return data.getFilename();
					} else {
						// If found component is one of the wanted components
						// storing it to matching components
						for (int k = 0; k < prefixes.length; k++) {
							if (compName.startsWith(prefixes[k])) {
								compsWithWantedName.put(prefixes[k], data);
								break;
							}
						}
					}
				}
				
			}
			
		}
		
		//If we did not found first in the prefix list, just returning first on the list
		for(int k = 0; k<prefixes.length; k++){
			if(compsWithWantedName.containsKey(prefixes[k])){
				return compsWithWantedName.get(prefixes[k]).getFilename();
			}
			
		}
		return null;
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#searchConcreteComponentsByGenericComponent(java.lang.String)
	 */
	public String[] searchConcreteComponentsByGenericComponent(
			String genericComponentName) {
		
		// Combining information from caches of all selected targets 
		ArrayList<String> concreteComponents = new ArrayList<String>();
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);

			Collection<ComponentPropertiesData> components = targetCache
					.getComponents();
			
			for (ComponentPropertiesData data : components) {
				
				// if this component name ends with component name wanted:
				String compName = data.getFilename();
				if (compName.toLowerCase().endsWith(genericComponentName.toLowerCase())) {

					//Adding name of the component, which ends with generic component name to list
					concreteComponents.add(compName);
				}			
			}		
		}		
		
		return (String[]) concreteComponents.toArray(new String[0]);
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#searchCache(java.lang.String, com.nokia.s60tools.appdep.search.SearchService.SearchType, com.nokia.s60tools.appdep.search.SearchService.MatchType)
	 */
	public Map<String, List<AbstractFunctionData>> searchCache(String searchString, SearchType searchType,
			MatchType matchType) {
		

		Map<String, List<AbstractFunctionData>> matchingComponents = new HashMap<String, List<AbstractFunctionData>>();
		
		// Combining information from caches of all selected targets 
		for (int i = 0; i < targetCacheList.size(); i++) {
			ITargetCache targetCache = targetCacheList.get(i);

			Collection<ComponentPropertiesData> components = targetCache
					.getComponents();

			int errors = 0;
			for (ComponentPropertiesData data : components) {
				
				try {
					handleSearchByType(searchString, searchType, matchType,
							matchingComponents, data, targetCache);
				} catch (NoSuchElementException e) {
					//No action for NoSuchElementException
					errors++;//Counting errors
				} 
			}
			if(errors > 0){
				String dbgMessage = "There were totally: '" + errors //$NON-NLS-1$
									+ "' error(s) when searchtype was '" //$NON-NLS-1$ 
									+ searchType  + "'."; //$NON-NLS-1$
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, dbgMessage);				
			}
		}		
		
		return matchingComponents;
	}

	/**
	 * Makes search to cache based based on given search type. Delegates search query further with correct parameters.
	 * @param searchString String to search for.
	 * @param searchType what kind of information wanted to search.
	 * @param matchType how searchString must be occur in results.
	 * @param matchingComponents Output parameter. Map of matching results or empty list if none found. Map contains component names as keys
	 *                           and function data list as values. Values are <code>null</code>s when search type is {@link SearchType.SEARCH_COMPONENTS}
	 *                           and instances of {@link ExportFunctionData} when search type is {@link SearchType.SEARCH_EXPORTED_FUNCTION}
	 *                           and instances of {@link ImportFunctionData} when search type is {@link SearchType.SEARCH_IMPORTED_FUNCTIONS}
	 * @param data  Component properties data to be examined for possible match.
	 * @param targetCache Target cache containing component properties currently under search.
	 */
	private void handleSearchByType(String searchString, SearchType searchType, MatchType matchType,
			Map<String, List<AbstractFunctionData>> matchingComponents, ComponentPropertiesData data, ITargetCache targetCache) {

		switch (searchType) {
		case SEARCH_COMPONENTS:
			searchForComponents(searchString, data, matchType, matchingComponents);		
			break;
		case SEARCH_EXPORTED_FUNCTION:
			searchForExportedFunctions(searchString, data, matchType, matchingComponents, targetCache);
			break;
		case SEARCH_IMPORTED_FUNCTIONS:
			searchForImportedFunctions(searchString, data, matchType, matchingComponents, targetCache);
			break;

		}		

	}

	/**
	 * Makes component search based on the given search string and match type.
	 * @param searchString String to search for.
	 * @param data  Component properties data to be examined for possible match.
	 * @param matchType how searchString must be occur in results.
	 * @param matchingComponents Output parameter. Map of matching results or empty list if none found. Map contains component names as keys
	 *                           and function data list as values. Values are <code>null</code>s when search type is {@link SearchType.SEARCH_COMPONENTS}
	 *                           and instances of {@link ExportFunctionData} when search type is {@link SearchType.SEARCH_EXPORTED_FUNCTION}
	 *                           and instances of {@link ImportFunctionData} when search type is {@link SearchType.SEARCH_IMPORTED_FUNCTIONS}
	 */
	private void searchForComponents(String searchString, ComponentPropertiesData data, 
			MatchType matchType, Map<String, List<AbstractFunctionData>> matchingComponents) {
		
		boolean isCaseSensitive = matchType.isCaseSensitiveSearch();
				
		String compName = data.getFilename();
		boolean matches = checkMatchByMatchType(searchString, compName, matchType,
				isCaseSensitive);
				
		//Adding component to results if it marches to search string
		if (matches) {

			//Adding name of the component, which ends with generic component name to list
			matchingComponents.put(compName, null);
		}			
	}
	
	/**
	 * Makes exported function search based on the given search string and match type.
	 * @param searchString String to search for.
	 * @param data  Component properties data to be examined for possible match.
	 * @param matchType how searchString must be occur in results.
	 * @param matchingComponents Output parameter. Map of matching results or empty list if none found. Map contains component names as keys
	 *                           and function data list as values. Values are <code>null</code>s when search type is {@link SearchType.SEARCH_COMPONENTS}
	 *                           and instances of {@link ExportFunctionData} when search type is {@link SearchType.SEARCH_EXPORTED_FUNCTION}
	 *                           and instances of {@link ImportFunctionData} when search type is {@link SearchType.SEARCH_IMPORTED_FUNCTIONS}
	 * @param targetCache Target cache containing component properties currently under search.
	 */
	private void searchForExportedFunctions(
			String searchString, ComponentPropertiesData data, MatchType matchType, 
			Map<String, List<AbstractFunctionData>> matchingComponents, ITargetCache targetCache) {
		
		boolean isCaseSensitive = matchType.isCaseSensitiveSearch();
				
		String compName = data.getFilename();
		Collection<ExportFunctionData> exportedFunctions = targetCache.getExportedFunctionsForComponent(compName);

		//Seeking through all exported functions, and if found one function matching criteria,
		//adding that function and component name to list. There can be more than one
		boolean matches = false; 		
		for (ExportFunctionData exportFunctionData : exportedFunctions) {
			String exportFunctionName = exportFunctionData.getFunctionName(); // By default using long name
			// For StartsWith, EndsWith, and ExactMatch searches we are using function's base name instead
			if(matchType.getMatchType() == MatchTypes.STARTS_WITH 
					|| matchType.getMatchType() == MatchTypes.ENDS_WITH
					|| matchType.getMatchType() == MatchTypes.EXACT_MATCH){
				exportFunctionName = exportFunctionData.getFunctionBaseName();
			}
			matches = checkMatchByMatchType(searchString, exportFunctionName, matchType,
					isCaseSensitive);
					
			//Adding component to results if it marches to search string
			if (matches) {

				if(matchingComponents.containsKey(compName)){
					List<AbstractFunctionData> functions = matchingComponents.get(compName);
					functions.add(exportFunctionData);
				}else{
					List<AbstractFunctionData> functions = new ArrayList<AbstractFunctionData>();
					functions.add(exportFunctionData);										
					//Adding name of the component, which ends with generic component name to list
					matchingComponents.put(compName, functions);					
				}				
			}			
		}
	}	
	
	/**
	 * Makes imported function search based on the given search string and match type.
	 * @param searchString String to search for.
	 * @param data  Component properties data to be examined for possible match.
	 * @param matchType how searchString must be occur in results.
	 * @param matchingComponents Output parameter. Map of matching results or empty list if none found. Map contains component names as keys
	 *                           and function data list as values. Values are <code>null</code>s when search type is {@link SearchType.SEARCH_COMPONENTS}
	 *                           and instances of {@link ExportFunctionData} when search type is {@link SearchType.SEARCH_EXPORTED_FUNCTION}
	 *                           and instances of {@link ImportFunctionData} when search type is {@link SearchType.SEARCH_IMPORTED_FUNCTIONS}
	 * @param targetCache Target cache containing component properties currently under search.
	 */
	private void searchForImportedFunctions(
			String searchString, ComponentPropertiesData data, MatchType matchType, 
			Map<String, List<AbstractFunctionData>> matchingComponents, ITargetCache targetCache) {
		
		boolean isCaseSensitive = matchType.isCaseSensitiveSearch();
				
		
		String compName = data.getFilename();
		//We already have this component in list
		if(matchingComponents.containsKey(compName)){
			return;
		}
		//Gets the list of components that are used directly by this component.
		Collection<UsedComponentData> usedComponentList = data.getUsedComponentList();
		//Looping through all components that are using selected component
		boolean matches = false; 
		for (UsedComponentData usedComponentData : usedComponentList) {
			//Gets functions imported from the component by the using parent component.
			Collection<ImportFunctionData> imp = usedComponentData.getParentImportedFunctions();
			//Looping through all imported function data 
			for (ImportFunctionData importFunctionData : imp) {
				//Check if import function name matches with search criteria
				String importFunctionName = importFunctionData.getFunctionName(); // By default using long name
				// For StartsWith, EndsWith, and ExactMatch searches we are using function's base name instead
				if(matchType.getMatchType() == MatchTypes.STARTS_WITH 
						|| matchType.getMatchType() == MatchTypes.ENDS_WITH
						|| matchType.getMatchType() == MatchTypes.EXACT_MATCH){
					importFunctionName = importFunctionData.getFunctionBaseName();
				}
				matches = checkMatchByMatchType(searchString, importFunctionName, matchType,
						isCaseSensitive);
				
				//Adding component to results if it marches to search string
				if (matches) {

					//If there already is that function found
					if(matchingComponents.containsKey(compName)){ 
						List<AbstractFunctionData> functions = matchingComponents.get(compName);
						functions.add(importFunctionData);
					}
					//else that function occurs very first time, and will be added to matchingComponents
					else{
						List<AbstractFunctionData> functions = new ArrayList<AbstractFunctionData>();
						functions.add(importFunctionData);										
						//Adding name of the component, which ends with generic component name to list
						matchingComponents.put(compName, functions);					
					}
					
				}									
			}
		}
	}		

	/**
	 * Checks if search string match to another string by the given match type.
	 * @param searchString search string.
	 * @param stringToCompareWith string to compare search string against.
	 * @param matchType match type
	 * @param isCaseSensitive set to <code>true</code> if check should be case sensitive, otherwise <code>false</code>.
	 * @return <code>true</code> if matches, <code>false</code> otherwise.
	 */
	private boolean checkMatchByMatchType(String searchString,
			String stringToCompareWith, MatchType matchType,
			boolean isCaseSensitive) {
		
		stringToCompareWith = stringToCompareWith.trim();
		boolean matches;
		switch (matchType.getMatchType()) {
		case STARTS_WITH:
			if(isCaseSensitive){
				matches = stringToCompareWith.startsWith(searchString);				
			}else{
				matches = stringToCompareWith.toLowerCase().startsWith(searchString.toLowerCase());				
			}
			break;

		case ENDS_WITH:
			if(isCaseSensitive){
				matches = stringToCompareWith.endsWith(searchString);				
			}else{
				matches = stringToCompareWith.toLowerCase().endsWith(searchString.toLowerCase());				
			}			
			break;
		case CONTAINS:
			if(isCaseSensitive){
				matches = stringToCompareWith.contains(searchString);				
			}else{
				matches = stringToCompareWith.toLowerCase().contains(searchString.toLowerCase());				
			}			
			break;
		case EXACT_MATCH:
			if(isCaseSensitive){
				matches = stringToCompareWith.equals(searchString);				
			}else{
				matches = stringToCompareWith.equalsIgnoreCase(searchString);				
			}			
			break;
		case REGULAR_EXPRESSION:
			if(isCaseSensitive){
				 Pattern p = Pattern.compile(searchString);
				 Matcher m = p.matcher(stringToCompareWith);
				 matches = m.matches();		
			}else{
				 Pattern p = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
				 Matcher m = p.matcher(stringToCompareWith);
				 matches = m.matches();
				 }				
			
			break;			
		default:
			matches = false;
			break;
		}
		return matches;
	}
	
	
}
