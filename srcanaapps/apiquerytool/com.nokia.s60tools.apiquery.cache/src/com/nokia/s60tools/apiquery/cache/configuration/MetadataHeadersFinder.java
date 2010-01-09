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
 
package com.nokia.s60tools.apiquery.cache.configuration;

import java.io.File;
import java.io.FilenameFilter;

import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * Helper class for seeking header files from where .metaxml file was found.
 */
public class MetadataHeadersFinder {
	
	private IConsolePrintUtility printUtility;

	/**
	 * Constructor with IConsolePrintUtility
	 * @param printUtility
	 */
	public MetadataHeadersFinder(IConsolePrintUtility printUtility){
		this.printUtility = printUtility;
		
	}
	public MetadataHeadersFinder(){
		this.printUtility = APIQueryConsole.getInstance();
	}
	
	public static final String INC_FOLDER = "inc"; //$NON-NLS-1$
	public static final String HEADER_FILE_H_SUFFIX = ".h"; //$NON-NLS-1$
	public static final String HEADER_FILE_HPP_SUFFIX = ".hpp";	 //$NON-NLS-1$
	public static final String HEADER_FILE_HRH_SUFFIX = ".hrh"; //$NON-NLS-1$
	public static final String HEADER_FILE_DISTRIBUTION_POLICY_PREFIX = "distribution.policy"; //$NON-NLS-1$
	
	/**
	 * Get headers as comma separated String
	 * @param entry
	 * @return headers belongs to API
	 */
	public String getHeadersForEntry(CacheEntry entry){
		
		try {
			String metaXMLSourcePath = entry.getId();
			String metaPath = metaXMLSourcePath.substring(0, metaXMLSourcePath.lastIndexOf(File.separator));
			String incPath = metaPath + File.separatorChar + INC_FOLDER;
			File incPathFile = new File(incPath);
			//We did not found inc folder, just returning with no results
			if(!incPathFile.exists() || !incPathFile.isDirectory()){
				//It's probably error if there is no ../inc folder at all, when metadata file is existing, but its no major error, just printing console message for that
				printUtility.println(Messages.getString("MetadataHeadersFinder.IncFolderNotFound_WarnMsg_Part1") +incPath +Messages.getString("MetadataHeadersFinder.IncFolderNotFound_WarnMsg_Part2") +entry.getId() +Messages.getString("MetadataHeadersFinder.IncFolderNotFound_WarnMsg_Part3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return ""; //$NON-NLS-1$
			}
			File [] headers = incPathFile.listFiles(new HeaderFilter());
			if(headers.length > 0){
				StringBuffer b = new StringBuffer();
				for (int i = 0; i < headers.length; i++) {
					File header = headers[i];
					//Skipping folders, only files is added
					if(header.isDirectory()){
						continue;
					}
					String headerName = header.getName();
					b.append(headerName);
					b.append(APIDetailField.VALUE_FIELD_SEPARATOR);
				}
				//If there was no files, only folders, there is no ", ":s eather, then just returning empty string
				int commaIndex = b.lastIndexOf(APIDetailField.VALUE_FIELD_SEPARATOR);
				if(commaIndex == -1){
					return "";
				}
				//Cut last ", " of, and then return header names.				
				return b.substring(0, commaIndex);
			}
			//Else it's not an error if there are none header files in folder,
			//so no operations when there are no headers found.
		} catch (Exception e) {			
			e.printStackTrace();
			printUtility.println(Messages.getString("MetadataHeadersFinder.HeadersSeekError_Msg") +e, IConsolePrintUtility.MSG_ERROR);			 //$NON-NLS-1$
		}
		
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Checks that file is header file. Every other files will be accepted, 
	 * but {@link MetadataHeadersFinder#HEADER_FILE_DISTRIBUTION_POLICY_PREFIX}
	 * will be excluded.
	 */
	private class HeaderFilter implements FilenameFilter{

		
		public boolean accept(File f, String name) {
			
			String name_ = name.toLowerCase();			
			if( name_.startsWith(HEADER_FILE_DISTRIBUTION_POLICY_PREFIX) ){
				return false;
			}else{
				return true;
			}
		}
		
	}

}
