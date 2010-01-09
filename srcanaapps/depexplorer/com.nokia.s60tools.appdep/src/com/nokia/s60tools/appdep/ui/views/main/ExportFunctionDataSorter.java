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
 
 
package com.nokia.s60tools.appdep.ui.views.main;

import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Sorter for export functions tab.
 */
public class ExportFunctionDataSorter extends S60ToolsViewerSorter {

	/**
	 * Import function data is sorted by function ordinal.
	 */
	public static final int CRITERIA_ORDINAL = 1;
	/**
	 * Import function data is sorted by function name.
	 */
	public static final int CRITERIA_NAME = 2;
	
	/**
	 * Constructor.
	 */
	public ExportFunctionDataSorter() {
		super();		
		// By default we are not sorting the information
		setSortCriteria(CRITERIA_NO_SORT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		// By default comparison does not do any ordering
		int comparisonResult = 0;
		
		ExportFunctionData f1 = (ExportFunctionData) e1;
		ExportFunctionData f2 = (ExportFunctionData) e2;
		
		switch (sortCriteria) {
		
		case CRITERIA_ORDINAL:			
			comparisonResult 			
				= numericSortFromDecString(f1.getFunctionOrdinal(), 
													   f2.getFunctionOrdinal());
			break;

		case CRITERIA_NAME:
			comparisonResult 
			   = alphabeticSort(
			        		f1.getFunctionName(), 
			        		f2.getFunctionName());
			break;
			
		case CRITERIA_NO_SORT:
			// No sorting criteria defined.
			break;

		default:
			AppDepConsole.getInstance().println(Messages.getString("ExportFunctionDataSorter.Unexpected_Sort_Criteria_Msg"),  //$NON-NLS-1$
					                     IConsolePrintUtility.MSG_ERROR);
			break;
		}
		
		return comparisonResult;
	}

}
