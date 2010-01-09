/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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

import org.eclipse.swt.SWTException;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.wizards.BuildTargetEntry.BuildTargetStatusEnum;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * This build target info object can be bound with UI object
 * presenting build target related info, but this class binds
 * <code>BuildTargetEntry</code> class into concrete world
 * and provides information for it about the real build target.
 * @see BuildTargetEntry
 */
public class BuildTargetEntryInfo implements ICacheIndexCreatorObserver {
	
	//
	// Public constants
	//
	/**
	 * Constant for unresolved component count.
	 */
	public static final int UNRESOLVED_COMPONENT_COUNT = -1;
		
	//
	// Constants and members
	//
		
	/**
	 * Target type of the available build target e.g. armv5 Release.
	 */
	private ITargetPlatform targetPlatform;
	
	/**
	 * Target's build type (urel/udeb).
	 */
	private IBuildType buildType;
	
	/**
	 * Build target status. Initially unresolved.
	 */
	private BuildTargetStatusEnum status = BuildTargetStatusEnum.EUnresolved;

	/**
	 * Amount of components found from the target. 
	 */
	private int componentCount = UNRESOLVED_COMPONENT_COUNT;

	/**
	 * SDK information object the target belongs to.
	 */
	private final SdkInformation sdkInfo;
	
	/**
	 * Currently used settings.
	 */
	private final AppDepSettings settings;

	/**
	 * Interface to notify about need to refresh UI.
	 * Used to notify component count finishing for the target.
	 */
	private final IRefreshable notifyUIRefreshIf;

	/**
	 * Constructor.
	 * @param settings Currently active settings. 
	 * @param notifyUIRefreshIf Interface to notify about need to refresh UI. Used to notify component count finishing for the target.
	 * @param sdkInfo SDK information object the target belongs to.
	 * @param targetType target type of the available build target e.g. armv5 Release 
	 * @param buildType target's build type (urel/udeb).
	 * @param isSuppported set to <code>true</code> if target type is supported, otherwise <code>false</code>.
	 */
	public BuildTargetEntryInfo(IRefreshable notifyUIRefreshIf, AppDepSettings settings, SdkInformation sdkInfo, ITargetPlatform targetType, IBuildType buildType, boolean isSuppported){
		validateArguments(notifyUIRefreshIf, settings, sdkInfo, targetType, buildType);
		this.notifyUIRefreshIf = notifyUIRefreshIf;
		this.settings = settings;
		this.sdkInfo = sdkInfo;
		this.targetPlatform = targetType;
		this.buildType = buildType;
		if(!isSuppported){
			// In case target is not supported => no need to resolve any other information further
			this.status = BuildTargetStatusEnum.ENotSupported;
		}
	}

	/**
	 * Validates that entry fields passed have some values.
	 * @param notifyUIRefreshIf Interface to notify about need to refresh UI. Used to notify component count finishing for the target.
	 * @param settings Currently active settings. 
	 * @param sdkInfo SDK information object the target belongs to.
	 * @param targetType target type of the available build target e.g. armv5 Release 
	 * @param buildType target's build type (urel/udeb).
	 * @throws IllegalArgumentException
	 */
	private void validateArguments(IRefreshable notifyUIRefreshIf, AppDepSettings settings, SdkInformation sdkInfo, ITargetPlatform targetType, IBuildType buildType) throws IllegalArgumentException{
		if( (notifyUIRefreshIf == null || settings == null || sdkInfo == null || targetType == null || buildType == null)){
			throw new IllegalArgumentException(new String(Messages.getString("BuildTargetEntryInfo.BuildTargetInfoValidateFailed_ErrMsg"))); //$NON-NLS-1$
			}
	}
	
	/**
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetPlatform.getId();
	}

	/**
	 * Get build target's build type.
	 * @return build target's build type.
	 */
	public IBuildType getBuildType() {
		return buildType;
	}
	
	/**
	 * Gets status for the entry.
	 * @return status for the entry.
	 */
	public BuildTargetStatusEnum getStatus() {
		if(status != BuildTargetStatusEnum.ENotSupported){
			checkAndUpdateTargetStatus();
		}
		return status;
	}
	
	/**
	 * Checks current build target status and updates status field accordingly.
	 */
	private void checkAndUpdateTargetStatus() {
		
		// By default cache is not yet created for target or not under creation.
		BuildTargetStatusEnum resolveStatus = BuildTargetStatusEnum.ENoCache;
		
		// Resolving current target status
		if(getComponentCount() == 0){
			resolveStatus = BuildTargetStatusEnum.EEmptyTarget;
		}		
		else if (settings.isCacheGenerationOngoingForTarget(sdkInfo, targetPlatform.getId(), buildType)){
			resolveStatus = BuildTargetStatusEnum.ECacheIsBeingGenerated;				
		}			
		else if (isTargetCached()){						
			String cacheFileAbsolutePathName = settings.getCacheFileAbsolutePathNameForSdkAndPlatform(
																									sdkInfo,
																									targetPlatform.getId(),
																									buildType
																								);
			if (! CacheIndex.cacheIndexCreatedFor(cacheFileAbsolutePathName)){
				resolveStatus = BuildTargetStatusEnum.ECachesIsBeingIndexed;									
			}		
			else if(settings.cacheNeedsUpdate(sdkInfo, new ITargetPlatform[]{targetPlatform}, buildType)){
				resolveStatus = BuildTargetStatusEnum.ECacheNeedsUpdate;									
			}
			else{
				resolveStatus = BuildTargetStatusEnum.ECacheReady;									
			}
		}			
		// Updating status field with latest status info
		status = resolveStatus;								
	}

	/**
	 * Gets component count for the entry.
	 * @return component count for the entry.
	 */
	public int getComponentCount() {
		// Getting component count only once
		if(componentCount == UNRESOLVED_COMPONENT_COUNT){
			getComponentCountForSdkAndPlatform(settings, sdkInfo, targetPlatform.getId(), buildType);
		}
		return componentCount;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.ICacheIndexCreatorObserver#cacheIndexCreated(com.nokia.s60tools.appdep.core.data.CacheIndex)
	 */
	public void cacheIndexCreated(CacheIndex cacheIndexObj) {
		// Not needing this information now but may be useful later on to get handle to created index object.
	}

	/**
	 * Checks targets caching status. 
	 * @return Returns <code>true</code> if target is cached, otherwise <code>false</code>.
	 */
	public boolean isTargetCached() {
		return settings.isTargetPlatformCached(sdkInfo.getSdkId(), targetPlatform.getId(), buildType);
	}
	
	/**
	 * Gets component count for the target build pointed by given 
	 * parameters. This is wrapper method for the corresponding method
	 * in AppDepSettings class.
	 * 
	 * NOTE: The query may take some time! Therefore the actual query
	 * is run in background thread withouth blockin UI.
	 * 
	 * @param settings Used AppDep settings. 
	 * @param sdkInfo	SDK information for the queried SDK/Platform
	 * @param targetName Target from the SDK/Platform.
	 * @param buildType Build type for the target.
	 * @return Component count for the selected target and build type.
	 * @see com.nokia.s60tools.appdep.core.AppDepSettings#getComponentCountForSdkAndPlatform
	 */
	void getComponentCountForSdkAndPlatform(AppDepSettings settings, SdkInformation sdkInfo, 
			                               String targetName, IBuildType buildType){
		
		final AppDepSettings settingsFinal = settings;
		final SdkInformation sdkInfoFinal = sdkInfo; 
        final String targetNameFinal = targetName;
        final IBuildType buildTypeFinal = buildType;
		
		Thread queryComponentCountRunnable = new Thread(){
			public void run(){
				// Triggering component count calculation
				int count = settingsFinal.getComponentCountForSdkAndPlatform(sdkInfoFinal, 
						                                    targetNameFinal,
						                                    buildTypeFinal);
				// Updating component count member variable
				componentCount = count;
				// Requesting UI refresh
				notifyUiRefresh();
			}
		};
		// Run component
		queryComponentCountRunnable.start();		
	}

	/**
	 * Notifies UI to refresh itself.
	 */
	private void notifyUiRefresh() {
		Runnable queryComponentCountRunnable = new Runnable(){
			public void run(){
				try {
					notifyUIRefreshIf.refresh();					
				} catch (SWTException e) {
					// We'll get 'SWTException: Widget is disposed' exceptions whenever
					// wizard dialog's page has been closed and try to do refresh.
					// This exception can be therefore ignored safely.
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		// Refresh request is scheduled to UI thread because there is need to modify 
		// UI components which cannot be done from background thread.
		PlatformUI.getWorkbench().getDisplay().asyncExec(queryComponentCountRunnable);		
	}	
	
}
