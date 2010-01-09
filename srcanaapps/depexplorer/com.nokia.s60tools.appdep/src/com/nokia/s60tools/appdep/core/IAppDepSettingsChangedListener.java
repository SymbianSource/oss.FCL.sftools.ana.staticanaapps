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
 * Listener interface for listening the change of currently
 * active settings.
 */
public interface IAppDepSettingsChangedListener {

	/**
	 * Informs listeners that active settings has been changed.
	 * @param isTargetBuildChanged Should set to <code>true</code> by the caller if, the currently
	 * 							   used target build settings has been changed, otherwise set to 
	 * 							   <code>false</code> by the caller (for example, if only currently
	 *                             analyzed component has been changed). 
	 */
	public void settingsChanged(boolean isTargetBuildChanged);
}
