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
 

package com.nokia.s60tools.appdep.core;

/**
 * Class representing target platform selection.
 */
public class TargetPlatform implements ITargetPlatform {

	private final String id;

	/**
	 * Constructor.
	 * @param id Unique string identfier for target platform type
	 */
	public TargetPlatform(String id){
		this.id = id;
		
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.ITargetPlatform#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.ITargetPlatform#idEquals(java.lang.String)
	 */
	public boolean idEquals(String targetPlatformId){
		return id.equals(targetPlatformId);
	}

}
