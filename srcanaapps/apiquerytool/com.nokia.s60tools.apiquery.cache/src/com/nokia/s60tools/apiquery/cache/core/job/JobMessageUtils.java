/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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
 
package com.nokia.s60tools.apiquery.cache.core.job;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.exceptions.XMLNotValidException;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;

/**
 * Class for show and print Messages
 */
public class JobMessageUtils {
	
	/**
	 * Show error dialog for load errors. Also prints message to console.
	 */
	public static void showLoadErrorDialog() {
		
		Runnable showMsg = new Runnable(){

			public void run() {
				String msg = getLoadErrorMessage();
				printErrorMessage(msg);
				new APIQueryMessageBox(msg, SWT.OK| SWT.ICON_ERROR  ).open();
				
			}

		};
		Display.getDefault().asyncExec(showMsg);

	}	
	/**
	 * Get load errors as message
	 * @return error message
	 */
	private static String getLoadErrorMessage() {
		CacheEntryStorage storage = CacheEntryStorage.getInstance();
		Vector<XMLNotValidException> errors = storage.getLoadErrors();
		StringBuffer msg = new StringBuffer();
		msg.append(Messages.getString("JobMessageUtils.DataSourceLoadErrorMsg_Part1")); //$NON-NLS-1$
		for (XMLNotValidException e : errors) {
			msg.append(e.getFileName());
			msg.append(": "); //$NON-NLS-1$
			msg.append(e.getMessage());
			msg.append("\n"); //$NON-NLS-1$
		}
		msg.append(Messages.getString("JobMessageUtils.DataSourceLoadErrorMsg_Part2")); //$NON-NLS-1$
		return msg.toString();
	}
	
	/**
	 * Print error message to console
	 */
	public static void printLoadErrorMessage(){
		printErrorMessage(getLoadErrorMessage());
	}
	/**
	 * Print error message to console
	 */
	private static void printErrorMessage(String msg){
		APIQueryConsole.getInstance().println(msg,APIQueryConsole.MSG_ERROR);
	}	

}
