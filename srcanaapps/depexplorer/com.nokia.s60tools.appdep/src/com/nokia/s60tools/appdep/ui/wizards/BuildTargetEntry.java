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

import com.nokia.s60tools.appdep.core.BuildTypeRelease;
import com.nokia.s60tools.appdep.core.IBuildType;
import com.nokia.s60tools.appdep.resources.Messages;


/**
 * Stores information on a single build target entry.
 * This contains data that is shown in UI.
 * 
 * <code>BuildTargetEntryInfo</code> class is used to fetch
 * actual domain model data info for UI object.
 * @see BuildTargetEntryInfo
 */
public class BuildTargetEntry {
	
	//
	// Column sorting indices for table column sorter
	//
	public static final int TARGET_TYPE_COLUMN_INDEX = 0;
	public static final int COMPONENT_COUNT_COLUMN_INDEX = 1;
	public static final int STATUS_COLUMN_INDEX = 2;

	/**
	 * Build target status enumeration.
	 *  - ECacheReady means that cache file has been created for the target and cache file is up-to-date.
	 *  - ECacheNeedsUpdate means that cache file has been created for the target and cache file is up-to-date.
	 *  - ENoCache means that no cache files has been created for the target yet.
	 *  - EEmptyTarget means that no components are build for the target and therefore it is not possible to create cache file.
	 *  - ENotSupported  means that target exists in the selected SDK but the tool does not support the target type.
	 *  - ECachesIsBeingIndexed means that cache index generation for the target is currently ongoing.
	 *  - ECacheIsBeingGenerated means that cache generation for the target is currently ongoing.
	 */
	public enum BuildTargetStatusEnum {
		EUnresolved,					// Initial status							
		ECacheReady,                        	
		ECacheNeedsUpdate, 	          			
		ENoCache,     							 
		EEmptyTarget,  					     						 
		ENotSupported, 	  						
		ECachesIsBeingIndexed,  				
		ECacheIsBeingGenerated 					 
	};
	
	/**
	 * Target entry info object that is used to get info about real world object.
	 */
	private BuildTargetEntryInfo targetEntryInfo;

		
	/**
	 * Constructor.
	 * @param targetEntryInfo Target entry info object that is used to get info about real world object. 
	 */
	public BuildTargetEntry(BuildTargetEntryInfo targetEntryInfo){
		this.targetEntryInfo = targetEntryInfo;
	}
		
	/**
	 * Gets target's description string shown for the user.
	 * The description is combination of target and build type.
	 * @return target's description.
	 */
	public String getTargetDescription() {
		return targetEntryInfo.getTargetType();
	}

	/**
	 * Gets component count for the entry.
	 * @return component count for the entry.
	 */
	public String getComponentCount() {
		// Not counting components for non-supported targets
		if(getStatus() == BuildTargetStatusEnum.ENotSupported){
			return Messages.getString("BuildTargetEntry.NotApplicable_Abbrev_Str"); //$NON-NLS-1$
		}
		// Otherwise fetching component count from entry info object
		int componentCount = targetEntryInfo.getComponentCount();
		if(componentCount == BuildTargetEntryInfo.UNRESOLVED_COMPONENT_COUNT){
			return Messages.getString("BuildTargetEntry.Counting_InfoMsg"); //$NON-NLS-1$
		}
		return new Integer(componentCount).toString();
	}

	/**
	 * Gets status for the entry.
	 * @return status for the entry.
	 */
	public BuildTargetStatusEnum getStatus() {
		return targetEntryInfo.getStatus();
	}
	
	/**
	 * Gets status as string for the entry.
	 * @return status as string for the entry.
	 */
	public String getStatusAsString(){
		String statusStr = Messages.getString("BuildTargetEntry.UnresolvedStatus_InfoMsg"); //$NON-NLS-1$

		switch (getStatus()) {
		
		case ECacheReady:
			statusStr = Messages.getString("BuildTargetEntry.CacheReadyStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case ECacheNeedsUpdate:
			statusStr = Messages.getString("BuildTargetEntry.CacheNeedsUpdateStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case ENoCache:
			statusStr = Messages.getString("BuildTargetEntry.NoCacheStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case EEmptyTarget:
			statusStr = Messages.getString("BuildTargetEntry.EmptyTargetStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case ENotSupported:
			statusStr = Messages.getString("BuildTargetEntry.NotSupportedStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case ECachesIsBeingIndexed:
			statusStr = Messages.getString("BuildTargetEntry.CreatingCacheIndexStatus_InfoMsg"); //$NON-NLS-1$
			break;

		case ECacheIsBeingGenerated:
			statusStr = Messages.getString("BuildTargetEntry.GeneratingCacheStatus_InfoMsg"); //$NON-NLS-1$
			break;

		default:
			break;
		}
		
		return statusStr;
	}
	
	/**
	 * Checks if target entry is release target.
	 * @return <code>true</code> if target entry is release target, otherwise <code>false</code>.
	 */
	public boolean isReleaseTarget() {
		if(targetEntryInfo.getBuildType().getBuildTypeName().equals(BuildTypeRelease.NAME)){
			return true;
		}
		return false;
	}	
	
	/**
	 * Checks if target entry is empty target.
	 * @return <code>true</code> if target entry is empty, otherwise <code>false</code>.
	 */
	public boolean isEmptyTarget() {
		return (getStatus() == BuildTargetStatusEnum.EEmptyTarget);
	}
	
	/**
	 * Checks if target entry is supported target.
	 * @return <code>true</code> if target entry is empty, otherwise <code>false</code>.
	 */
	public boolean isSupportedTarget() {
		return (getStatus() != BuildTargetStatusEnum.ENotSupported);
	}		
	
	/**
	 * Gets name of the build target without build type information.
	 * @return name of the build target without build type information.
	 */
	public String getTargetName() {
		return targetEntryInfo.getTargetType();
	}

	/**
	 * Get build target's build type.
	 * @return build target's build type.
	 */
	public IBuildType getBuildType() {
		return targetEntryInfo.getBuildType();
	}
	
	/**
	 * Compares if given target platform and build type combination
	 * equals this build target entry.
	 * @param buildTargetName Target platform name.
	 * @param buildTypeString Build type string.
	 * @return <code>true</code> if combination equals this object.
	 */
	public boolean equals(String buildTargetName, String buildTypeString) {
		// Build targets are considered as equal if both
		// build target name and build type name matches.
		return (
				(getTargetName().equalsIgnoreCase(buildTargetName))
				&&
				(getBuildType().getBuildTypeName().equalsIgnoreCase(buildTypeString))
				);
	}
}
