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

import com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver;
import com.nokia.s60tools.util.cmdline.ICustomLineReader;

public class CacheCreationProgressLineReader implements ICustomLineReader {

	public CacheCreationProgressLineReader() {
	}

    private void filterProgressStatus(String line, 
			   						  ICmdLineCommandExecutorObserver observer){
    	String[] splitArr = line.split("%"); //$NON-NLS-1$
    	if(splitArr.length == 2){
	    	String token1 = splitArr[0];
	    	String progressPercentageStr = token1.substring(token1.length()-3);
	    	int progressPercentageInt = Integer.parseInt(progressPercentageStr.trim());
	    	observer.progress(progressPercentageInt);
    	}
    }

	public String readLine(BufferedReader br, 
						   ICmdLineCommandExecutorObserver observer) throws IOException {
		
    	StringBuffer line = new StringBuffer(""); //$NON-NLS-1$
    	// Marking is used to go back one character in the stresm after all the
    	// backspaces are skipped. So we allow reading of one character and
    	// the mark is still preserved.
    	final int readAheadLimit = 1;
    	int charAsInteger;
    	char ch;
    	while((charAsInteger = br.read()) != -1){
    		ch = (char) charAsInteger;
    		if(ch == '\n' || ch == '\r'){
    			 filterProgressStatus(line.toString(), observer);
	    		 return line.toString();
    		}
    		else if(ch == '\b'){
    			// Consuming all the backspaces
    			while(ch == '\b'){
    				// Marking the place before read
    				br.mark(readAheadLimit);
    				charAsInteger = br.read();
    				if(charAsInteger == -1){
    					break;
    				}
    	    		ch = (char) charAsInteger;
    			}
    			// Going back into marked place (one character back)
    			br.reset();
    			 filterProgressStatus(line.toString(), observer);
	    		return line.toString();
    		}
    		else{
    			line.append(ch);	    			
    		}
    	}
    	return null;
	}

}
