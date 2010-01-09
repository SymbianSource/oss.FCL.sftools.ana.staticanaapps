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

package com.nokia.s60tools.apiquery.ui.views.main.search;


import org.eclipse.jface.viewers.ITableLabelProvider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Label provider for the TableViewerExample
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
public class DataLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
	
	public String getColumnText(Object element, int columnIndex) {
		APITask task = (APITask) element;
		return  task.getColumnData(columnIndex);
	}

	 public Image getColumnImage(Object element, int columnIndex) {
	 return null;
	}
	

}
