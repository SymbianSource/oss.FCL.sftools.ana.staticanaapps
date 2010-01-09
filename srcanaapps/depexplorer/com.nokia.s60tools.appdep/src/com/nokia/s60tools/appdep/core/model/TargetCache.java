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
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Object used for storing accessing information of a single cache 
 * file via an object model.
 */
public class TargetCache implements ITargetCache {
	
	/**
	 * Version of the cache.
	 */
	private String version;
	
	/**
	 * Dependencies cache data storage
	 */
	DependenciesCache dependenciesCache = null;

	/**
	 * Library cache data storage
	 */
	SymbolsCache symbolsCache = null;

	/**
	 * Id of the target platform this target cache represents 
	 * The id is got based on the cache directory structure
	 * and set after construction of cache object.
	 */
	private String id;

	/**
	 * Constructor (created only by CacheFactory).
	 * @param id target platform id this target cache represents.
	 */
	TargetCache(String id){
		this.id = id;
	}	
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getVersion()
	 */
	public String getVersion() {
		return version;		
	}

	/**
	 * Checks that user has set up necessary data members for enabling cache data access.
	 */
	private void checkPreconditions() {
		if(dependenciesCache == null || symbolsCache == null){
			throw new RuntimeException(Messages.getString("TargetCache.CacheNotSetUpProperly_ErrMsg"));			 //$NON-NLS-1$
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getComponents()
	 */
	public Collection<ComponentPropertiesData> getComponents() {
		checkPreconditions();
		return dependenciesCache.getAllComponentProperties();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getComponentPropertiesForComponent(java.lang.String)
	 */
	public ComponentPropertiesData getComponentPropertiesForComponent(
			String cmpName) {
		checkPreconditions();
		return dependenciesCache.getComponentPropertiesData(cmpName);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getDirectlyDependentComponentsFor(java.lang.String)
	 */
	public List<UsedComponentData> getDirectlyDependentComponentsFor(
			String compToSearchFor) {
		checkPreconditions();
		return dependenciesCache.getDirectlyDependentComponentsFor(compToSearchFor);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getExportedFunctionsForComponent(java.lang.String)
	 */
	public Collection<ExportFunctionData> getExportedFunctionsForComponent(
			String componentNameWithExtension) {
		return symbolsCache.getExportedFunctionsForComponent(componentNameWithExtension);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getParentImportedFunctionsForComponent(java.lang.String, java.lang.String)
	 */
	public Collection<ImportFunctionData> getParentImportedFunctionsForComponent(
			String parentCmpName, String importedCmpName) {
		checkPreconditions();
		return dependenciesCache.getParentImportedFunctionsForComponent(parentCmpName, importedCmpName);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getUsingComponents(java.util.ArrayList, java.lang.String, java.lang.String)
	 */
	public void getUsingComponents( IJobProgressStatus progressCallback, 
									ArrayList<ComponentPropertiesData> resultComponentsArrayList,
									String componentName, String functionOrdinal,
									int totalComponentCount) throws JobCancelledByUserException{
		checkPreconditions();
		dependenciesCache.getUsingComponents(progressCallback, resultComponentsArrayList, componentName, functionOrdinal, totalComponentCount);
	}

	/**
	 * Sets reference to dependencies cache object (done only by CacheFactory).
	 * @param dependenciesCache the dependenciesCache to set
	 */
	void setDependenciesCache(DependenciesCache dependenciesCache) {
		this.dependenciesCache = dependenciesCache;
	}

	/**
	 * Sets reference to dependencies cache object (done only by CacheFactory).
	 * @param symbolsCache the symbolsCache to set
	 */
	void setSymbolsCache(SymbolsCache symbolsCache) {
		this.symbolsCache = symbolsCache;
	}

	/**
	 * Sets target cache version once it is sure that dependencies and symbols file versions match
	 * @param Version number string to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getLibraries()
	 */
	public Collection<LibPropertiesData> getLibraries() {
		return symbolsCache.getAllLibraryProperties();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.model.ITargetCache#getId()
	 */
	public String getId() {
		return id;
	}

}
