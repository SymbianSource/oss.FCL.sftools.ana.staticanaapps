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

package com.nokia.s60tools.appdep.core.model;

import java.util.regex.Pattern;

import com.nokia.s60tools.appdep.exceptions.ZeroFunctionOrdinalException;
import com.nokia.s60tools.appdep.resources.Messages;


/**
 * Abstract base class for import and export function data.
 */
public abstract class AbstractFunctionData {
	
	/**
	 * The following character is used to separate class/namespace name from function name in raw cache data.
	 */
	private final static String FUNC_NAME_SEPARATOR = "::"; //$NON-NLS-1$
	
	/**
	 * The following character is used to start parameter list in function name.
	 */
	private final static String FUNC_PARAM_LIST_START_CHAR = "("; //$NON-NLS-1$
	
	/**
	 * Function ordinal (1..n when converted into integer).
	 */
	private final String functionOrdinal;
	/**
	 * Function name.
	 */
	protected String functionName;

	/**
	 * Constructor.
	 * @param functionOrdinal Function ordinal (1..n when converted into integer).
	 * @param functionName Function name.
	 * @throws IllegalArgumentException 
	 * @throws ZeroFunctionOrdinalException 
	 */
	public AbstractFunctionData(String functionOrdinal, String functionName) throws IllegalArgumentException, ZeroFunctionOrdinalException{
		this.functionOrdinal = functionOrdinal;
		this.functionName = functionName;
		validateOrdinal();		
	}

	/**
	 * Checks that ordinal can be converted into integer.
	 * @throws ZeroFunctionOrdinalException 
	 */
	private void validateOrdinal() throws IllegalArgumentException, ZeroFunctionOrdinalException{
		String validateErrMsg = Messages.getString("AbstractFunctionData.NotValidOrdinal_ErrMsg") + functionOrdinal; //$NON-NLS-1$
		try {
			int ordinal = getFunctionOrdinalAsInt();
			// Ordinal should be positive value from 1..N
			if(!(ordinal > 0)){
				if(ordinal == 0){
					// Zero ordinal functions are special cases handled by caller
					throw new ZeroFunctionOrdinalException(functionName);
				}
				throw new IllegalArgumentException(validateErrMsg);				
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(validateErrMsg);
		}		
	}

	/**
	 * Returns function ordinal as int.
	 * @return function ordinal as int.
	 */
	public int getFunctionOrdinalAsInt(){
		return Integer.parseInt(functionOrdinal);
	}

	/**
	 * Returns function ordinal as Integer.
	 * @return function ordinal as Integer.
	 */
	public Integer getFunctionOrdinalAsInteger(){
		return new Integer(getFunctionOrdinalAsInt());
	}
	
	/**
	 * Gets function ordinal. 
	 * @return the functionOrdinal
	 */
	public String getFunctionOrdinal() {
		return functionOrdinal;
	}

	/**
	 * Gets function name.
	 * @return the functionName
	 */
	public String getFunctionName() {
		return functionName;
	}
	
	/**
	 * Extracts function's base name from the full name.
	 * @return Function's base name without class or namespace name prefix and parameter list.
	 */
	public String getFunctionBaseName() {
		// Separating component name prefix from function name and parameters
		String[] splittedFuncName = functionName.split(Pattern.quote(FUNC_NAME_SEPARATOR));
		// If there was component name prefix...
		if(splittedFuncName.length == 2){
			//  returning function base name without class/namespace name and without parameter list
			// e.g RFs::PrivatePath(TDes16&) => PrivatePath			[efsrv.dll:27]
			// q.g. std::terminate() => terminate					[drtrvct2_2.dll:1]
			return splittedFuncName[1].split(Pattern.quote(FUNC_PARAM_LIST_START_CHAR))[0];
		}
		else{
			// Otherwise returning just function name without parameter list
			// e.g FileNamesIdentical(TDesC16 const&, TDesC16 const&) => FileNamesIdentical		[efsrv.dll:1]
			// e.g operator delete(void*) => operator delete									[scppnwdl.dll:3]
			// e.g __btod_div_common => __btod_div_common										[drtrvct2_2.dll:11]
			return functionName.split(Pattern.quote(FUNC_PARAM_LIST_START_CHAR))[0];
		}
	}
}
