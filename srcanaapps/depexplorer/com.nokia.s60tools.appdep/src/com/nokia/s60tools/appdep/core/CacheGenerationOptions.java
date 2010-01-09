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

import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.resources.Messages;

/**
 * Class storing cache generation options.
 */
public class CacheGenerationOptions {

	public static final int USE_DSO_FILES = 0;
	public static final int USE_LIB_FILES = 1;
	
	private final IToolchain usedToolchain;
	private int usedLibraryType;
	
	public CacheGenerationOptions(IToolchain usedToolchain, int usedLibraryType) throws InvalidCmdLineToolSettingException{
		this.usedToolchain = usedToolchain;
		if(usedLibraryType == USE_DSO_FILES || usedLibraryType == USE_LIB_FILES){
			this.usedLibraryType = usedLibraryType;			
		}
		else{
			String msg = Messages.getString("CacheGenerationOptions.Invalid_LibraryType_Msg")  //$NON-NLS-1$
				         + ": " //$NON-NLS-1$
						 + usedLibraryType;
			throw new InvalidCmdLineToolSettingException(msg);
		}
	}

	/**
	 * @return Returns the usedLibraryType.
	 */
	public int getUsedLibraryType() {
		return usedLibraryType;
	}

	/**
	 * @return Returns the usedToolchain.
	 */
	public IToolchain getUsedToolchain() {
		return usedToolchain;
	}
		
}
