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
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;

/**
 * Sorter implementation for build target entry data.
 */
public class BuildTargetTableViewerSorter extends S60ToolsViewerSorter {

	//
	// Sorting criteria constants
	//
	public static final int CRITERIA_TARGET_TYPE = 1;
	public static final int CRITERIA_COMPONENT_COUNT = 2;
	public static final int CRITERIA_STATUS = 3;

	public BuildTargetTableViewerSorter() {
		super();		
		// By default we are not sorting the information
		setSortCriteria(CRITERIA_NO_SORT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		// By default comparison does not do any ordering
		int compRes = 0;
		
		BuildTargetEntry entry1 = (BuildTargetEntry) e1;
		BuildTargetEntry entry2 = (BuildTargetEntry) e2;
		
		switch (sortCriteria) {

		case CRITERIA_TARGET_TYPE:
			compRes = alphabeticSort(entry1.getTargetDescription(), entry2.getTargetDescription());
			break;
			
		case CRITERIA_COMPONENT_COUNT:
			compRes = numericSortFromDecString(entry1.getComponentCount(), entry2.getComponentCount());
			break;

		case CRITERIA_STATUS:
			compRes = alphabeticSort(entry1.getStatusAsString(), entry2.getStatusAsString());
			break;
			
		case CRITERIA_NO_SORT:
			// No sorting criteria defined.
			break;

		default:			
			AppDepConsole.getInstance()
					.println(
							Messages.getString("BuildTargetTableViewerSorter.Unexpected_Sort_Criteria_ErrMsg") + sortCriteria, //$NON-NLS-1$
							AppDepConsole.MSG_ERROR);
			break;
		}
				
		return compRes;
	}

}
