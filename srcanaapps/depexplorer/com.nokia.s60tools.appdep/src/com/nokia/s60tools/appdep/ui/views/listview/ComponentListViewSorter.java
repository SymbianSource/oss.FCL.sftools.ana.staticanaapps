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
 
 
package com.nokia.s60tools.appdep.ui.views.listview;

import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Sorter for component properties in component list.
 */
public class ComponentListViewSorter extends S60ToolsViewerSorter {

	//
	// Sorting criteria constants
	//
	public static final int CRITERIA_NAME = 1;
	public static final int CRITERIA_BIN_FORMAT = 2;
	public static final int CRITERIA_UID1 = 3;
	public static final int CRITERIA_UID2 = 4;
	public static final int CRITERIA_UID3 = 5;
	public static final int CRITERIA_SECURE_ID = 6;
	public static final int CRITERIA_VENDOR_ID = 7;
	public static final int CRITERIA_MIN_HEAP = 8;
	public static final int CRITERIA_MAX_HEAP = 9;
	public static final int CRITERIA_STACK_SIZE = 10;

	/**
	 * Constructor.
	 */
	public ComponentListViewSorter() {
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
		
		ComponentPropertiesData prop1 = (ComponentPropertiesData) e1;
		ComponentPropertiesData prop2 = (ComponentPropertiesData) e2;
		
		switch (sortCriteria) {

		case CRITERIA_NAME:
			// Ignoring component name case during sorting
			compRes = alphabeticSort(prop1.getFilename().toLowerCase(), prop2.getFilename().toLowerCase());
			break;
			
		case CRITERIA_BIN_FORMAT:
			compRes = alphabeticSort(prop1.getBinaryFormat(), prop2.getBinaryFormat());
			break;

		case CRITERIA_UID1:
			compRes = numericSortFromHexString(prop1.getUid1(), prop2.getUid1());
			break;

		case CRITERIA_UID2:
			compRes = numericSortFromHexString(prop1.getUid2(), prop2.getUid2());
			break;

		case CRITERIA_UID3:
			compRes = numericSortFromHexString(prop1.getUid3(), prop2.getUid3());
			break;

		case CRITERIA_SECURE_ID:
			compRes = numericSortFromHexString(prop1.getSecureId(), prop2.getSecureId());
			break;

		case CRITERIA_VENDOR_ID:
			compRes = numericSortFromHexString(prop1.getVendorId(), prop2.getVendorId());
			break;

		case CRITERIA_MIN_HEAP:
			compRes = numericSortFromDecString(prop1.getMinHeapSize(), prop2.getMinHeapSize());
			break;

		case CRITERIA_MAX_HEAP:
			compRes = numericSortFromDecString(prop1.getMaxHeapSize(), prop2.getMaxHeapSize());
			break;

		case CRITERIA_STACK_SIZE:
			compRes = numericSortFromDecString(prop1.getStackSize(), prop2.getStackSize());
			break;

		case CRITERIA_NO_SORT:
			// No sorting criteria defined.
			break;

		default:
			AppDepConsole.getInstance()
					.println(
							Messages.getString("ComponentListViewSorter.Unexpected_Sort_Criteria_Msg"), //$NON-NLS-1$
							IConsolePrintUtility.MSG_ERROR);
			break;
		}
				
		return compRes;
	}

}
