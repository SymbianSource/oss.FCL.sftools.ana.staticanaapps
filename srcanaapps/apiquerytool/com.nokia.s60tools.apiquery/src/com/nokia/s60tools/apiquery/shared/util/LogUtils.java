/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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


package com.nokia.s60tools.apiquery.shared.util;

import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Miscellaneous logging utilities to be used as tracing aid in case of error 
 * for which is otherwise hard to find information about the error in end-user environment
 * unless properly logged also in production environment with exact error data.
 */
public class LogUtils {

	/**
	 * Logs error message and expection's stack trace to product console if we are running code 
	 * in Eclipse workbench environment. If not (i.e running JUnit tests) 
	 * catching the exceptions and ignoring them.
	 * 
	 * Logging done here because we loose important information because
	 * cache data load is done inside job that looses stack trace. 
	 * 
	 * @param errorMsg Error message to log.
	 * @param e Exception to log.
	 */
	public static void logStackTrace(final String errorMsg, final Exception e) {
		try {
			if(e != null) e.printStackTrace();
			// Scheduling log task to UI thread.
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					APIQueryConsole.getInstance().println(errorMsg, IConsolePrintUtility.MSG_ERROR);
					if(e != null){
						StackTraceElement[] stackTrace = e.getStackTrace();
						APIQueryConsole.getInstance().printStackTrace(stackTrace);															
					}
				}				
			});
		} catch (Exception e2) {
			// Ignoring possible errors due to not having workbench environment running.
		}
	}

	/**
	 * Logs internal error message to tool's console and throws run-time exception.
	 * @param errMsg Error message to log and pass as message to runtime exception. 
	 * @throws RuntimeException
	 */
	static public void logInternalErrorAndThrowException(String errMsg) throws RuntimeException {
		APIQueryConsole.getInstance().println("INTERNAL ERROR: " + errMsg ); //$NON-NLS-1$
		throw new RuntimeException(errMsg);
	}

}
