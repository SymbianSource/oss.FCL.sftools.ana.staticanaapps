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

public class APITask {

	private String[] Columns = null;	


	
	public APITask(String[] columnData) {

		super();
		Columns = columnData;
		
	}
	

	public String getColumnData(int index) {
		return Columns[index];
	}



	public void setColumnData(String string, int index) {
		Columns[index] = string;
	}

	

}
