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
 

package com.nokia.s60tools.apiquery.settings;

/**
 * Interface for the listeners that want to get user setting modifications events.
 */
public interface IUserSettingsListener {

	/**
	 * Notifies the listener that user settings have changed.
	 */
	public void userSettingsChanged();
	
}
