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

/**
 * Cache load progress notification interface.
 */
public interface ICacheLoadProgressNotification {

	/**
	 * Notifies about the loaded component.
	 * @param componentName Name of the component loaded.
	 */
	public void componentLoaded(String componentName);
}
