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


package com.nokia.s60tools.apiquery.shared.preferences;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;

/**
 * Class used to initialize default preference values.
 */
public class APIQueryPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		
		APIQueryPlugin.getPrefsStore().setDefault(APIQueryPreferenceConstants.SHOW_DATASOURCE_IN_RESULTS, true);
	}
}

