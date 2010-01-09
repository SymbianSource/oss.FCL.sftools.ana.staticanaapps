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
 
package com.nokia.s60tools.apiquery.shared.services;

import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;

public class QueryServices {
	
	/**
	 * Enables the starting of API queries
	 * also from the other plug-ins that should
	 * not know anything about internal UI components.
	 * However, this method must be called from an UI thread, or
	 * use <code>Display.getDefault().asyncExec(...)</code> method
	 * and pass runnable as parameter in order to schedule
	 * query request to UI thread.
	 * @param queryType Query type.
	 * @param queryString Query string.
	 * @throws QueryOperationFailedException 
	 */
	public static void runAPIQuery(int queryType, String queryString) throws QueryOperationFailedException{
			MainView.runAPIQueryFromExternalClass(queryType,
					queryString, false);			
	}

}
