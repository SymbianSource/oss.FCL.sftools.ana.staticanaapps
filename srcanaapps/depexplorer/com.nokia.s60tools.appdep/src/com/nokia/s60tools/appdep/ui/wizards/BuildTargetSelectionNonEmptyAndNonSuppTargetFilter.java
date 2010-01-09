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
 * Accepts in Build Target Selection -wizard page only the targets
 * that are non-empty or supported.
 * 
 * The enabling and disabling of filter is managed using <code>setFilterEnabled</code>
 * method because using <code>addFilter</code> and <code>removeFilter</code> methods
 * did not produce wanted UI behavior (transient state while adding/removing could be
 * detected by user by flicker and showing temporarily unwanted data set before filter
 * was fully applied).
 */
public class BuildTargetSelectionNonEmptyAndNonSuppTargetFilter extends ViewerFilter {

	
	/**
	 * By default the filter is enabled.
	 */
	boolean isFilterEnabled = true;
	
	/**
	 * Constructor.
	 * @param isFilterEnabled set to <code>true</code> to enable filter
	 *                        and to <code>false</code> to disable filter.
	 */
	public BuildTargetSelectionNonEmptyAndNonSuppTargetFilter(boolean isFilterEnabled){
		this.isFilterEnabled = isFilterEnabled;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		BuildTargetEntry targetEntry = (BuildTargetEntry) element;
		// If filter is enabled...
		if(isFilterEnabled){
			// The target entry must be both
			// - non-empty target, and
			// - supported one.
			return (!targetEntry.isEmptyTarget()) && (targetEntry.isSupportedTarget());	
		}
		// Otherwise accepting all
		return true;
	}

	/**
	 * Sets filter into enabled or disabled state.
	 * @param isFilterEnabled set to <code>true</code> to enable filter
	 *                        and to <code>false</code> to disable filter.
	 */
	public void setFilterEnabled(boolean isFilterEnabled) {
		this.isFilterEnabled = isFilterEnabled;
	}

	
}
