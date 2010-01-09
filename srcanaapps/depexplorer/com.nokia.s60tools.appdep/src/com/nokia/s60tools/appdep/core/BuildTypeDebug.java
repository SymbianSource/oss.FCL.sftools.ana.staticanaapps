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

import com.nokia.s60tools.appdep.resources.Messages;

public class BuildTypeDebug implements IBuildType {

	public static final String NAME = "udeb"; //$NON-NLS-1$
	private static final String DESCRIPTION = Messages.getString("BuildTypeDebug.BuildType_Description"); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IBuildType#getBuildTypeName()
	 */
	public String getBuildTypeName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IBuildType#getBuildTypeDescription()
	 */
	public String getBuildTypeDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IBuildType#equals(com.nokia.s60tools.appdep.core.IBuildType)
	 */
	public boolean equals(IBuildType buildType){
		return NAME.equals(buildType.getBuildTypeName());
	}
}
