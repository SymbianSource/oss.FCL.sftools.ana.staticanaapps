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
package com.nokia.s60tools.apiquery.cache.util;

import java.net.URI;
import java.util.ArrayList;

import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;

import com.nokia.s60tools.apiquery.cache.searchmethod.ui.LocalCacheUIComposite;
import com.nokia.s60tools.apiquery.popup.actions.OpenFileAction;

import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.sdk.SdkManager;

public class SDKUtil {

	public static void headerOpen(String sdkName, String apiName,
			String headerName) {
		try {

			CacheEntryStorage cacheEntryStorage = CacheEntryStorage
					.getInstance();
			String source = cacheEntryStorage.getID(sdkName, apiName,
					headerName);
			System.out.println("source " + source);
			String temp = source.replace("\\", "/");
			System.out.println(temp);

			temp = temp.substring(0, temp.lastIndexOf("/")) + "/inc/"
					+ headerName;
			temp = "file://" + temp;
			System.out.print("temp" + temp);

			OpenFileAction action = new OpenFileAction();
			action.openFile(new URI(temp), headerName);

		} catch (Exception e) {

		}

	}

	/*
	 * return cachedarray
	 */
	public static String[] getCachedSDKs() {
		String[] cachedsdks = new String[0];

		try {
			ArrayList<String> sdks = new ArrayList<String>();
			SdkInformation[] sdkInfoColl = SdkManager.getSdkInformation();
			//String[] sdks = new String[sdkInfoColl.length];

			for (int i = 0; i < sdkInfoColl.length; i++) {
               String sdkid = sdkInfoColl[i].getSdkId();
				if (LocalCacheUIComposite.isSDKAllreadySeeked(SDKFinder
						.getSDKInformation(sdkid))) {
					sdks.add(sdkid);
					System.out.println("cached" + sdkid);

				}
			}

			cachedsdks = new String[sdks.size()];
			sdks.toArray(cachedsdks);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cachedsdks;

	}
	
	

}
