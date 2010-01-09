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
 
package com.nokia.s60tools.apiquery.cache.util;

import org.osgi.framework.Version;

import com.nokia.carbide.cpp.sdk.core.ISDKManager;
import com.nokia.carbide.cpp.sdk.core.SDKCorePlugin;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.sdk.SdkEnvInfomationResolveFailureException;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.sdk.SdkManager;
import com.nokia.s60tools.util.console.IConsolePrintUtility;


/**
 * Helper Class for finding SDK information.
 */
public class SDKFinder {

	/**
	 * Get SDK information by SDK ID
	 * @param sdkID
	 * @return information about SDK
	 */
	public static SdkInformation getSDKInformation(String sdkID){
		try{
			
		SdkInformation[] sdkInfoColl = SdkManager.getSdkInformation();
			
			for (int i = 0; i < sdkInfoColl.length; i++) {
				SdkInformation info = sdkInfoColl[i];
			
	
	
				if(info.getSdkId().equalsIgnoreCase(sdkID)){
					return info;
				}
			}
			return null;//Should not be able to occur
		} catch (SdkEnvInfomationResolveFailureException e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().println(e.getMessage(), 
										IConsolePrintUtility.MSG_ERROR);
			return null;
		}		
	}	
	
}
