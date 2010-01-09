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
 
 
package com.nokia.s60tools.appdep.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver;
import com.nokia.s60tools.util.cmdline.ICustomLineReader;

class LinesToStringArrayListCustomLineReader implements ICustomLineReader {

	private final ArrayList<String> resultLinesArrayList;

	public LinesToStringArrayListCustomLineReader(ArrayList<String> resultLinesArrayList) {
		this.resultLinesArrayList = resultLinesArrayList;
	}

	public String readLine(BufferedReader br, 
			   ICmdLineCommandExecutorObserver observer) throws IOException {		
		String line = br.readLine();
		if(line != null){
			resultLinesArrayList.add(line);			
		}
		return line;
	}

}
