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
 
package com.nokia.s60tools.appdep.ui.dialogs;

import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;

/**
 * Sorter implementation for SIS file entry data.
 */
public class SISFileEntryTableViewerSorter extends S60ToolsViewerSorter {

	//
	// Sorting criteria constants
	//
	public static final int CRITERIA_NAME = 1;
	public static final int CRITERIA_LOCATION = 2;

	public SISFileEntryTableViewerSorter() {
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
		
		SISFileEntry entry1 = (SISFileEntry) e1;
		SISFileEntry entry2 = (SISFileEntry) e2;
		
		switch (sortCriteria) {

		case CRITERIA_NAME:
			compRes = alphabeticSort(entry1.getFileName(), entry2.getFileName());
			break;
			
		case CRITERIA_LOCATION:
			compRes = alphabeticSort(entry1.getLocation(), entry2.getLocation());
			break;

		case CRITERIA_NO_SORT:
			// No sorting criteria defined.
			break;

		default:			
			AppDepConsole.getInstance()
					.println(
							Messages.getString("SISFileEntryTableViewerSorter.Unexpected_Sort_Criteria_ErrMsg") + sortCriteria, //$NON-NLS-1$
							AppDepConsole.MSG_ERROR);
			break;
		}
				
		return compRes;
	}

}
