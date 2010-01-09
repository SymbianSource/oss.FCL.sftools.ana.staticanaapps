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


package com.nokia.s60tools.appdep.export;


/**
 * String utilities used for character replacements 
 * e.g. when creating XML reports.  
 */
public class StringUtils {

	/**
	 * Replaces those characters that are no allowed inside XML elements
	 * with corresponding XML entities.
	 * @param in string to replace forbidden characters from
	 * @return string with forbidden characters replaced with corresponding entities.
	 */
	public static String replaceForbiddenCharacters(String in){
		String out = in.replace("&","&#38;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("<","&#60;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace(">","&#62;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("\"","&#34;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("'","&#39;"); //$NON-NLS-1$ //$NON-NLS-2$
			
		return out;
		
	}

	/**
	 * Replaces given sub string with given replacement if found from the input string.
	 * @param in input string used for replacement
	 * @param replaceMe sub string to be replaced
	 * @param replaceWith sub string used as replacement
	 * @return string with all occurrences replace if found.
	 */
	public static String replace(String in, String replaceMe, String replaceWith){
		String out = in.replace(replaceMe,replaceWith);			
		return out;		
	}	
	
}
