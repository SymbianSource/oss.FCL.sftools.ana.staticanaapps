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
 
 
package com.nokia.s60tools.appdep.util;

import org.eclipse.jface.resource.ImageDescriptor;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.util.console.AbstractProductSpecificConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Singleton class that offers console printing
 * services for the extension.
 */
public class AppDepConsole extends AbstractProductSpecificConsole {
	
	/**
	 * Singleton instance of the class.
	 */
	static private AppDepConsole instance = null;
	
	/**
	 * Public accessor method.
	 * @return Singleton instance of the class.
	 */
	static public AppDepConsole getInstance(){
		if(instance == null ){
			instance = new AppDepConsole();
		}
		return instance;
	}
	
	/**
	 * Private constructor forcing Singleton usage of the class.
	 */
	private AppDepConsole(){		
	}
			
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.console.AbstractProductSpecificConsole#getProductConsoleName()
	 */
	protected String getProductConsoleName() {
		return ProductInfoRegistry.getConsoleWindowName();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.util.console.AbstractProductSpecificConsole#getProductConsoleImageDescriptor()
	 */
	protected ImageDescriptor getProductConsoleImageDescriptor() {
		return ImageResourceManager.getImageDescriptor(ImageKeys.IMG_APP_ICON);
	}

	/**
	 * Prints given stack trace element array contents to console as error message.
	 * @param stackTrace stack trace to be printed into the console
	 */
	public void printStackTrace(StackTraceElement[] stackTrace) {
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement stackTraceElement = stackTrace[i];
			String traceString = stackTraceElement.toString();
			println(traceString, IConsolePrintUtility.MSG_ERROR);
		}
	}
}
