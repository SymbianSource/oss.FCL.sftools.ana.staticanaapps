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
 

package com.nokia.s60tools.appdep.ui.views.data;

import java.text.DateFormat;
import java.util.Date;

import com.nokia.s60tools.appdep.core.ITargetPlatform;

/**
 * Represents a single component item in the
 * list showing available components for 
 * the currently selected target(s).
 */
public class ComponentListNode{
		
	//
	// Column sorting indices for table column sorter
	//
	public static final int NAME_COLUMN_INDEX = 0;
	public static final int TARGET_TYPE_COLUMN_INDEX = 1;
	public static final int DATE_CACHED_COLUMN_INDEX = 2;
	
	/**
	 * Component name
	 */
	private final String name;
	/**
	 * Target platform.
	 */
	private final ITargetPlatform buildTargetType;
	/**
	 * Last modification timestamp (unix time seconds) for component when cached.
	 */
	private final long cachedComponentModificationTimestamp;
	
	/**
	 * Using SHORT length date string representation.
	 * Using static member in order to prevent fetching of country/locale information 
	 * for each date formatting operation.
	 */
	private static final DateFormat dtFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	
	/**
	 * Using MEDIUM length time string representation.
	 * Using static member in order to prevent fetching of country/locale information 
	 * for each date formatting operation.
	 */
	private DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);;	
	
	/**
	 * @param name Component name
	 * @param buildTargetType Target platform.
	 * @param cachedComponentModificationTimestamp last modification time for component
	 */
	public ComponentListNode(String name, ITargetPlatform buildTargetType, long cachedComponentModificationTimestamp){
		this.name = name;
		this.buildTargetType = buildTargetType;
		this.cachedComponentModificationTimestamp = cachedComponentModificationTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}

	/**
	 * Gets build target type for the component as string.
	 * @return build target type for the component as string.
	 */
	public String getBuildTargetTypeAsString() {
		return buildTargetType.getId();
	}
	
	/**
	 * Gets last modification time for component when cached.
	 * @return last modification time for component when cached
	 */
	public long getCachedComponentModificationTimestamp() {
		return cachedComponentModificationTimestamp;
	}

	/**
	 * Gets last modification time for component when cached as string representation.
	 * @return last modification time for component when cached as string
	 */
	public String getCachedComponentModificationTimestampAsString() {
		Date dt = new Date(cachedComponentModificationTimestamp);
		String dateStr = dtFormat.format(dt);
		String timeStr = timeFormat.format(dt);
		return dateStr + " " + timeStr; //$NON-NLS-1$
	}

	/**
	 * Gets component name.
	 * @return component name.
	 */
	public String getComponentName() {
		return name;
	}

	/**
	 * Gets build target type for the component.
	 * @return build target type for the component.
	 */
	public ITargetPlatform getBuildTargetType() {
		return buildTargetType;
	}

}
