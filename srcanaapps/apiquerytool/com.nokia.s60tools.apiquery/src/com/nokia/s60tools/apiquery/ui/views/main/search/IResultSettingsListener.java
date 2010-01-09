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
package com.nokia.s60tools.apiquery.ui.views.main.search;

/**
 * Interface to pass Result view settings between mediator and composite classes
 */
public interface IResultSettingsListener {

	/**
	 * Call when result view setting is changed in UI from 
	 * "Show only API names" to show results in query type centric way 
	 * and vice versa.
	 */
	public void resultSettingsChanged();
	
}
