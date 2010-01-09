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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters Build Target Selection -wizard page content based on the build type.
 */
public class BuildTargetSelectionBuildTypeFilter extends ViewerFilter {

	/**
	 * Mode for the filtering.
	 */
	public enum BuildTargetFilterModeEnum{
		EShowReleaseTargets,
		EShowDebugTargets
	}

	/**
	 * Current filter mode.
	 */
	private BuildTargetFilterModeEnum filterMode;

	/**
	 * Constructor.
	 * @param filterMode Initial filter mode used.
	 */
	public BuildTargetSelectionBuildTypeFilter(BuildTargetFilterModeEnum filterMode) {
		super();
		this.filterMode = filterMode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		BuildTargetEntry targetEntry = (BuildTargetEntry) element;
		if(filterMode == BuildTargetFilterModeEnum.EShowReleaseTargets){
			return targetEntry.isReleaseTarget();
		}
		// Otherwise filter is in debug filter mode
		return !targetEntry.isReleaseTarget();
	}

	/**
	 * Sets filter mode to be used.
	 * @param filterMode filter mode
	 */
	public void setFilterMode(BuildTargetFilterModeEnum filterMode) {
		this.filterMode = filterMode;
	}

}
