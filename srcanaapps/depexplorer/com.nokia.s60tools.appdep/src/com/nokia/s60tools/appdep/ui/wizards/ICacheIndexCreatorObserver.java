/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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


package com.nokia.s60tools.appdep.ui.wizards;

import com.nokia.s60tools.appdep.core.data.CacheIndex;

/**
 * Notifies cache index creator observers about cache creation.
 */
public interface ICacheIndexCreatorObserver {
	/**
	 * Cache index was created for the passed object.
	 * @param cacheIndexObj Cache index object just created.
	 */
	public void cacheIndexCreated(CacheIndex cacheIndexObj);
}
