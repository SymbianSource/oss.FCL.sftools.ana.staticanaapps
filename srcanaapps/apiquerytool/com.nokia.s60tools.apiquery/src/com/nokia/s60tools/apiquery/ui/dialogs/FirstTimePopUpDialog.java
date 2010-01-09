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
 
package com.nokia.s60tools.apiquery.ui.dialogs;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;

/**
 * Class for showing First time pop up dialog (Quick help).
 * 
 * This dialog is ment to open when API Query application 
 * is started for the wery first time.
 *
 */
public class FirstTimePopUpDialog {
	
	private static final String NEW_LINE = "\n\n"; //$NON-NLS-1$

	/**
	 * For checking if Web data source is available to build message, we need to check
	 * id Web data source was found and we cannot have dependency to data source
	 * plug-ins in main plug-in. Thats why we keep this duplicate ID here.
	 */
	private static final String WEB_DATASOURCE_ID = "web";
	
	/**
	 * No construction, only static method to use.
	 *
	 */
	private FirstTimePopUpDialog(){
	}
	
	/**
	 * Open the first time pop up dialog (quick help).
	 *
	 */
	public static void open(Shell parent) {

		StringBuffer msg = new StringBuffer();		

		msg.append(Messages.getString("FirstTimePopUpDialog.Topic_Msg")); //$NON-NLS-1$
		msg.append(NEW_LINE);
		msg.append(Messages.getString("FirstTimePopUpDialog.Tool_Is_For_Msg")); //$NON-NLS-1$
		
		String dataSourcesMsg = getDataSourcesMsg();
		
		msg.append(NEW_LINE);
		msg.append(dataSourcesMsg); 
		msg.append(NEW_LINE);
		msg.append(Messages.getString("FirstTimePopUpDialog.Seach_Msg")); //$NON-NLS-1$
		msg.append(NEW_LINE);
		msg.append(Messages.getString("FirstTimePopUpDialog.Results_Msg")); //$NON-NLS-1$
		msg.append(NEW_LINE);
		msg.append(Messages.getString("FirstTimePopUpDialog.Project_Query_Msg")); //$NON-NLS-1$
		msg.append(NEW_LINE);
		msg.append(Messages.getString("FirstTimePopUpDialog.More_Information_Msg")); //$NON-NLS-1$
		
		MessageDialog.openInformation(parent,
				Messages.getString("FirstTimePopUpDialog.Title_Msg"),msg.toString());//$NON-NLS-1$
	}

	/**
	 * Get part of the dialog messages where data sources are described. 
	 * Message content depends on current data source configuration
	 * @return msg to show in UI
	 */
	private static String getDataSourcesMsg() {
				
		//Message with 3 data sources will be:
		//To configure Data Sources, open the Properties tab at the bottom of the API Query view. 
		//Three types of Data Sources are currently available: 
		//Web Server, API Metadata files from the SDK and Excel Interface Sheets. 
		//There are three Web servers configured by default, but only two of them are enabled.		

		SearchMethodExtensionRegistry smeRegistry = SearchMethodExtensionRegistry.getInstance();
		int extensionCount = smeRegistry.getExtensionCount();
		//If we have web data source or not
		boolean isWeb = false;
		
		Collection<ISearchMethodExtension> extensions = smeRegistry.getExtensions();
		String dataSourcesMsg = Messages.getString("FirstTimePopUpDialog.Properties_Msg_Configure"); //$NON-NLS-1$
		dataSourcesMsg += " ";//$NON-NLS-1$
		//If we have only one data source
		if(extensionCount == 1){
			dataSourcesMsg =Messages.getString("FirstTimePopUpDialog.Properties_Msg_Option_One_DataSources"); //$NON-NLS-1$
			String ds1 = null;
			for (Iterator<ISearchMethodExtension> iterator = extensions.iterator(); iterator.hasNext();) {
				ISearchMethodExtension extension = (ISearchMethodExtension) iterator.next();
				isWeb = isExtensionWeb(isWeb, extension);
				ds1 = extension.getExtensionInfo().getDescription();
			}		
			dataSourcesMsg = String.format(dataSourcesMsg, ds1);
			
		}
		//If we have 2 data sources available
		else if(extensionCount == 2){
			dataSourcesMsg = Messages.getString("FirstTimePopUpDialog.Properties_Msg_Option_Two_DataSources"); //$NON-NLS-1$
			String ds1 = null;
			String ds2 = null;
			for (Iterator<ISearchMethodExtension> iterator = extensions.iterator(); iterator.hasNext();) {
				ISearchMethodExtension extension = (ISearchMethodExtension) iterator.next();
				isWeb = isExtensionWeb(isWeb, extension);
				if(ds1 == null){
					ds1 = extension.getExtensionInfo().getDescription();
				}else{
					ds2 = extension.getExtensionInfo().getDescription();
				}
			}		
			dataSourcesMsg = String.format(dataSourcesMsg, ds1, ds2);
		}
		//if we have 3 data sources available
		else{//else it will be 3, but just in case having just else case
			dataSourcesMsg = Messages.getString("FirstTimePopUpDialog.Properties_Msg_Option_Three_DataSources"); //$NON-NLS-1$
			String ds1 = null;
			String ds2 = null;
			String ds3 = null;
			for (Iterator<ISearchMethodExtension> iterator = extensions.iterator(); iterator.hasNext();) {
				ISearchMethodExtension extension = (ISearchMethodExtension) iterator.next();
				isWeb = isExtensionWeb(isWeb, extension);
				if(ds1 == null && ds2 == null){
					ds1 = extension.getExtensionInfo().getDescription();
				}else if(ds2 == null){
					ds2 = extension.getExtensionInfo().getDescription();
				}else {
					ds3 = extension.getExtensionInfo().getDescription();
				}
			}		
			dataSourcesMsg = String.format(dataSourcesMsg, ds1, ds2, ds3);
		}
		//If we have web data source, adding web data source information to the end
		if(isWeb){
			dataSourcesMsg += " ";//$NON-NLS-1$
			dataSourcesMsg += Messages.getString("FirstTimePopUpDialog.Properties_Msg_WebDescription"); //$NON-NLS-1$
		}
		return dataSourcesMsg;
	}	

	/**
	 * Checking if this is Web extension
	 * @param isWebAllreadyFound boolean that indicates that web is already found
	 * @param extension
	 * @return <code>true</code> if is <code>false</code> otherwise
	 */
	private static boolean isExtensionWeb(boolean isWebAllreadyFound , ISearchMethodExtension extension) {
		return isWebAllreadyFound || extension.getExtensionInfo().getId().equalsIgnoreCase(WEB_DATASOURCE_ID);
	}
}
