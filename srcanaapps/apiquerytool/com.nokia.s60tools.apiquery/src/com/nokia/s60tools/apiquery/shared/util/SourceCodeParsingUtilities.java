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
 
package com.nokia.s60tools.apiquery.shared.util;

/**
 * Miscellaneous utilities for parsing source code.
 */
public class SourceCodeParsingUtilities {

	/**
	 * Parses header name from include directive line.
	 * @param line Line to check for.
	 * @return Name of the header file if found, otherwise <code>null</code>.
	 */
	public static String parseIncludeFromLine(String line) {
		String include = null;
		//If include is system inculde (#include <someapi.h>)
		//This can be really a user include, if user has been using
		//SYSTEMINCLUDE and USERINCLUDE in "wrong" ways
		if(hasSystemInclude(line)){
			include = line.substring(line.indexOf("<") +1, line.indexOf(">"));			 //$NON-NLS-1$ //$NON-NLS-2$
			include = removeIncludePath(include);
							
		}
		//If include is user inculde (#include "myapi.h")
		//this can be really a system include, if user has been using 
		//SYSTEMINCLUDE and USERINCLUDE in "wrong" ways
		else if(hasUserInclude(line)){
			include=line.substring(line.indexOf("\"") +1, line.lastIndexOf("\"")); //$NON-NLS-1$ //$NON-NLS-2$
			include = removeIncludePath(include);
		}
		return include;
	}

	/**
	 * Removes path from the include, if exists, and returns only header's basename.
	 * @param include Absolute header path name.
	 * @return Header's basename.
	 */
	private static String removeIncludePath(String include) {
		if(include.contains("\\")){ //$NON-NLS-1$
			include = include.substring(include.lastIndexOf("\\") + 1);  //$NON-NLS-1$
		}
		if(include.contains("/")){ //$NON-NLS-1$
			include = include.substring(include.lastIndexOf("/") + 1);  //$NON-NLS-1$
		}
		return include;
	}

	/**
	 * Checks if line contains include directive.
	 * @param line Line to check for.
	 * @return <code>true</code> if include was found.
	 */
	private static boolean hasInclude(String line) {
		return line.contains("#include"); //$NON-NLS-1$
	}

	/**
	 * Checks if line contains user include directive.
	 * @param line Line to check for.
	 * @return <code>true</code> if include was found.
	 */
	public static boolean hasUserInclude(String line) {
		return hasInclude(line) && line.contains("\""); //$NON-NLS-1$
	}

	/**
	 * Checks if line contains system include directive.
	 * @param line Line to check for.
	 * @return <code>true</code> if include was found.
	 */
	public static boolean hasSystemInclude(String line) {
		return hasInclude(line) && line.contains("<") && line.contains(">"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Checks if line contains system or user include directives.
	 * @param line Line to check for.
	 * @return <code>true</code> if an include was found.
	 */
	public static boolean hasIncludes(String line) {
		return hasUserInclude(line) || hasSystemInclude(line);
	}

}
