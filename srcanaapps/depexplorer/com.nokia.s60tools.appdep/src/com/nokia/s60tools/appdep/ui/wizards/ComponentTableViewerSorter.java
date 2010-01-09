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

import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;

/**
 * Sorter implementation for build target entry data.
 */
public class ComponentTableViewerSorter extends S60ToolsViewerSorter {

	//
	// Sorting criteria constants
	//
	public static final int CRITERIA_NAME = 1;
	public static final int CRITERIA_TARGET_TYPE = 2;
	public static final int CRITERIA_DATE_CACHED = 3;

	public ComponentTableViewerSorter() {
		super();		
		// By default we are not sorting the information
		setSortCriteria(CRITERIA_NAME);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		// By default comparison does not do any ordering
		int compRes = 0;
		
		ComponentListNode entry1 = (ComponentListNode) e1;
		ComponentListNode entry2 = (ComponentListNode) e2;
		
		switch (sortCriteria) {

		case CRITERIA_NAME:
			compRes = alphabeticSort(entry1.getComponentName(), entry2.getComponentName());
			break;

		case CRITERIA_TARGET_TYPE:
			compRes = alphabeticSort(entry1.getBuildTargetTypeAsString(), entry2.getBuildTargetTypeAsString());
			break;
			
		case CRITERIA_DATE_CACHED:
			compRes = numericSort(entry1.getCachedComponentModificationTimestamp(), entry2.getCachedComponentModificationTimestamp());
			break;
			
		case CRITERIA_NO_SORT:
			// No sorting criteria defined.
			break;

		default:			
			AppDepConsole.getInstance()
					.println(
							Messages.getString("ComponentTableViewerSorter.Unexpected_Sort_Criteria_ErrMsg") + sortCriteria, //$NON-NLS-1$
							AppDepConsole.MSG_ERROR);
			break;
		}
				
		return compRes;
	}

}
