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

package com.nokia.s60tools.apiquery.ui.views.main.search;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

/**
 * Sorter for the results table viewer
 */
public class DataSorter extends ViewerSorter {

	private int columnNum;
	private int sortDirection;

	public DataSorter(int dir, int criteria) {
		this.sortDirection = dir;
		this.columnNum = criteria;
	}

	public int compare(Viewer viewer, Object o1, Object o2) {
		
		int returnValue = 0;
		
		String s1 = ((APITask)o1).getColumnData(columnNum);
		String s2 = ((APITask)o2).getColumnData(columnNum);
		
		returnValue = s1.compareTo(s2);
		
		if(sortDirection == SWT.UP)
			return returnValue;
		else
			return returnValue * -1;
	}
}
