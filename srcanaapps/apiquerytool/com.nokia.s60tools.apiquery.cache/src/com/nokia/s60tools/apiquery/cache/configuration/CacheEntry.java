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
 
package com.nokia.s60tools.apiquery.cache.configuration;


import java.io.FileNotFoundException;
import java.io.IOException;

import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.cache.xml.MetadataXMLToUIMappingRules;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.exceptions.XMLNotValidException;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.resource.FileUtils;

/**
 * Stores information for a single API Metadata information from the SDK entry.
 */
public class CacheEntry extends AbstractEntry {

	//
	// Column sorting indices for table column sorter
	//
	public static final int NAME_COLUMN_INDEX = 0;
	public static final int FOLDER_COLUMN_INDEX = 1;
	
	/**
	 * file size 
	 */
	private long size;
	/**
	 * File name
	 */
	private String name;

	/**
	 * Date of file creation
	 */
	private long date = 0;
	
	/**
	 * SDK Id as given by {@link SdkInformation#getSdkId()}
	 */
	private String sdkid;
	
	private String apiName;
	
	
	/**
	 *  API Details (XML data file contents readed to memory)
	 */
	private APIDetails APIDetails = null;


	/**
	 * Constructor.
	 * @param id Entry id that is unique per search method. 
	 * @param name If Sheet entry name.
	 * @param size Folder path name.
	 * @param isSelected Is this server entry used for queries.
	 */
	public CacheEntry(String id, String name, String SDKID, boolean isSelected, long size, long date,String apiName){
		super(id, isSelected);//There are no not selected entries in Metadata data source
		sdkid = SDKID;
		this.date  = date;
		validateArguments(id, name);
		this.name = name;
		this.size = size;
		this.apiName =apiName;
	}
	

	/**
	 * Validates that all the datafiles are valid
	 * i.e. currently checking that they all contain values.
	 * @param id file name with absolutely path
	 * @param name entry (file) name.
	 * @param size File size on disk.
	 * @throws IllegalArgumentException
	 */
	private void validateArguments(String id, String name) throws IllegalArgumentException{
		if( ( id == null || name == null )
			||
			( id.length() == 0 || name.length() == 0 )){
			throw new IllegalArgumentException(new String(Messages.getString("CacheEntry.Cannot_Contain_Empty_Field_ErrMsg"))); //$NON-NLS-1$
			}
	}



	/**
	 * @return the folder in file system
	 */
	public long getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.web.configuration.AbstractEntry#updateEntryTypeSpecificDataFields(com.nokia.s60tools.apiquery.web.configuration.AbstractEntry)
	 */
	public void updateEntryTypeSpecificDataFields(AbstractEntry entryWithUpdatedData) {
		CacheEntry entry = (CacheEntry) entryWithUpdatedData;
		this.size = entry.getSize();	
		this.name = entry.getName();
		this.date = entry.getDate();
		this.sdkid = entry.getSDKID();
		this.isSelected = entry.isSelected;		
	}

	
	/**
	 * Get ID for SDK where entry belongs to
	 * @return SDK Id as given by {@link SdkInformation#getSdkId()}
	 */
	public String getSDKID() {
		return sdkid;
	}

	/**
	 * return the file name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * return the file creation date
	 * @return date
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Set API details
	 * @param APIDetails
	 */
	public void setAPIDetails(APIDetails APIDetails) {
		this.APIDetails = APIDetails;
	}

	/**
	 * get API details
	 * @return API Details
	 */
	public APIDetails getAPIDetails() {
		//If details is null, it must not be loaded now, because of lot of time taken
		//and UpdateSDKSelectionJob gets API Details when saving current situation to ram 
		//for user cancel situation data restore.
		return APIDetails;
	}	

	/**
	 * Load file contents for this entry.
	 * 
	 * if load() fails for some reason, this entry will be set as not selected.
	 * @see CacheEntry#setSelected(false)
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void load() throws XMLNotValidException{

		try {
			StringBuffer buf = FileUtils.loadDataFromFile(getId());	
		
			APIDetails details = XMLUtils.extractAPIDetailsData(buf.toString(), new MetadataXMLToUIMappingRules());
			  
			MetadataHeadersFinder headerFinder = new MetadataHeadersFinder();
			details.addOrUpdateField(MetadataXMLToUIMappingRules.HEADERS, headerFinder.getHeadersForEntry(this));
			setAPIDetails(details);
		} catch (FileNotFoundException e) {
			setSelected(false, true);//Because load was failed, this cannot be selected as data source, if is, search will fail
			throw new XMLNotValidException(e.getMessage(), getId());
		} catch (IOException e) {
			setSelected(false, true);//Because load was failed, this cannot be selected as data source, if is, search will fail
			throw new XMLNotValidException(e.getMessage(), getId());			
		}catch (Exception e) {
			setSelected(false, true);//Because load was failed, this cannot be selected as data source, if is, search will fail
			e.printStackTrace();
			throw new XMLNotValidException(e.getMessage(), getId());			
		}		
		
	}

	/**
	 * Release API details from memory by setting API Details to <code>null</code>.
	 */
	public void unload() {
		setAPIDetails(null);
	}
	
  public String getAPIName()
  {
	  return apiName;
  }

}
