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
 
 
package com.nokia.s60tools.appdep.ui.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchWindow;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.core.TargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheIndexCreator;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * The content provider class is responsible for providing objects to 
 * the Build Target Selection Wizard page. The content provider preserves
 * existing domain objects as long same SDK is selected and build target
 * list is only refreshed in case SDK selection changes.
 * 
 * This is done because if domain objects change in every query, the checkbox 
 * table viewer component loses check statuses done by user.
 */
class SelectBuildTargetWizardPageContentProvider implements IStructuredContentProvider{
	
	/**
	 * Empty object array returned whenever there is no content.
	 */
	static Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Wizard page for which offering content. 
	 */
	private final SelectBuildTargetWizardPage page;
	
	/**
	 * Used to trigger cache index creation
	 * whenever needed.  
	 */
	CacheIndexCreator indexCreator;

	/**
	 * Currently used targets. If SDK does not change the contents are only updated.
	 *  
	 */
	private List<BuildTargetEntry> targetList;

	/**
	 * Currently used SDK to fetch targets.
	 */
	private SdkInformation currentlyUsedSdk;
	
	/**
	 * Content provider's constructor
	 */
	public SelectBuildTargetWizardPageContentProvider(SelectBuildTargetWizardPage page){
		this.page = page;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		
		// Getting SDK from settings and initializing element array
		AppDepSettings settings = page.getSettings();
		SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();		
		
		// If no SDK selection is made yet...
		if(sdkInfo == null){
			return  EMPTY_ARRAY; // ...returning an empty array
		}
		
		// If target list is not yet created or SDK selection has changed	
		if(targetList == null || currentlyUsedSdk == null ){
			// Storing the used SDK and creating new build target list
			currentlyUsedSdk = sdkInfo;
			createBuildTargetList(settings, currentlyUsedSdk);
		}
		
		return targetList.toArray();
	}

	/**
	 * Creates build target list based on given settings and used SDK.
	 * @param settings used settings
	 * @param sdkInfo used SDK.
	 */
	private void createBuildTargetList(AppDepSettings settings, SdkInformation sdkInfo) {

		targetList = new ArrayList<BuildTargetEntry>();
		
		// Making sure that index creator is uninitialized at this point
		indexCreator = null; // thus preventing messing up with already running instance
		
		// Going through all the build targets platforms and corresponding build types available for SDK 
		String[] platforms = sdkInfo.getPlatforms();
		
		for (int i = 0; i < platforms.length; i++) {
			String targetTypeName = platforms[i];
			
			String buildTypes[] = sdkInfo.getBuildTypesForPlatform(targetTypeName);
			for (int j = 0; j < buildTypes.length; j++) {
				String buildTypeString = buildTypes[j];
				try {
					addBuilTargetEntry(targetTypeName, buildTypeString, targetList, settings);
				} catch (InvalidCmdLineToolSettingException e1) {
					// We will get this exception if the given build type string
					// is not supported by the tool. We can ignore the exception
					// and continue the for loop normally.
				}
								
			} // for
			
		} // for		
		
		// Finally launching cache index creation if needed i.e. whenever index creator 
		// object has been created on need basis only.
		if(indexCreator != null){
			launchCacheIndexCreation();
		}
	}

	/**
	 * Creates build target entry info and build target object and adds it to target list.
	 * @param targetTypeName name of the target to add.
	 * @param buildTypeString build type name string.
	 * @param targetList target list to add new target into.
	 * @param settings settings for the target to be created.
	 * @throws InvalidCmdLineToolSettingException 
	 */
	private void addBuilTargetEntry(String targetTypeName, String buildTypeString, List<BuildTargetEntry> targetList,
			AppDepSettings settings) throws InvalidCmdLineToolSettingException {
		// Creating build target entry and adding it into element array
		SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();	
		IBuildType buildType = settings.getBuildTypeFromString(buildTypeString);
		boolean isSupportedTargetPlatform = settings.isSupportedTargetPlatform(targetTypeName); 
		BuildTargetEntryInfo buildTargetEntryInfo = new BuildTargetEntryInfo(page, settings, sdkInfo, new TargetPlatform(targetTypeName), buildType, isSupportedTargetPlatform);
		BuildTargetEntry buildTargetEntry = new BuildTargetEntry(buildTargetEntryInfo);
		// Creating cache index in case cache exist for the target
		if(buildTargetEntryInfo.isTargetCached()){
			String cacheFileAbsolutePathName = settings.getCacheFileAbsolutePathNameForSdkAndPlatform(
																										sdkInfo,
																										targetTypeName,
																										buildType
																									);
			String buildDirAbsolutePathName = settings.getBuildDirectoryForSdkAndPlatform(sdkInfo, targetTypeName, buildType);
			registerCacheIndexCreation(buildTargetEntryInfo, cacheFileAbsolutePathName, buildDirAbsolutePathName);
		}
		targetList.add(buildTargetEntry);
	}
	
	/**
	 * Launches cache index creation runnable.
	 */
	public void launchCacheIndexCreation() {
		if(indexCreator != null && indexCreator.hasIndexCreationRequests()){
			try {
				IWorkbenchWindow wbw = AppDepPlugin.getCurrentlyActivePage().getWorkbenchWindow();
				wbw.run(true, false, indexCreator);
			   } catch (InvocationTargetException e) {
				   e.printStackTrace();
			   } catch (InterruptedException e) {
				   e.printStackTrace();
			   } catch (Exception e) {
				      e.printStackTrace();
			   }
		}		
	}
	
	/**
	 * Registering a new instance of cache index creation.
	 * @param entryInfo target entry info object listening or cache creation completion.
	 * @param cacheFileAbsolutePathName Absolute path name to cache file.
	 * @param buildDirAbsolutePathName Absolute path name to build directory..
	 */
	public void registerCacheIndexCreation(BuildTargetEntryInfo entryInfo, String cacheFileAbsolutePathName, 
			String buildDirAbsolutePathName) {
		if(indexCreator == null){
			indexCreator = new CacheIndexCreator(entryInfo);
		}		
		indexCreator.registerCacheIndexCreation(cacheFileAbsolutePathName, 
				buildDirAbsolutePathName);
	}

	/**
	 * Tries to find build target node that matches
	 * with the given parameters.
	 * @param sdkIdString Id string of the SDK.
	 * @param buildTargetName Name of the platform.
	 * @param buildTypeString Build type of the node to search for.
	 * @return Returns the matching object if it was found, 
	 *         otherwise return <code>null</code>.
	 */
	public Object find(String sdkId, String buildTargetName,
			String buildTypeString) {
		if(currentlyUsedSdk.getSdkId().equals(sdkId)){
			for (Iterator<BuildTargetEntry> iterator = targetList.iterator(); iterator.hasNext();) {
				BuildTargetEntry entry = (BuildTargetEntry) iterator.next();
				if(entry.equals(buildTargetName, buildTypeString)){
					return entry;
				}
			}			
		}
		// Match not found
		return null;
	}

	/**
	 * This method clears the previously fetched target list.
	 *
	 */
	public void clearBuildTargetsList()
	{
		this.targetList = null;
	}
}
