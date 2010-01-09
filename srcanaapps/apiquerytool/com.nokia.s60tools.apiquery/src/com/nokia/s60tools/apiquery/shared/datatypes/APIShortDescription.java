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
 
package com.nokia.s60tools.apiquery.shared.datatypes;

import java.util.ArrayList;

/**
 * Stores information related to a single API Summary
 * object to be shown in UI.
 */
public class APIShortDescription {

	/**
	 * Name of the API in question.
	 */
	private final String name;
	
	/**
	 * Source this informatio summary is got from. 
	 */
	private final String source;

	/**
	 * Description for the source shown in the UI.
	 */
	private final String sourceDescription;
	
	private ArrayList<String> detailsShortData = null;


	private APIDetails apiDetails = null;

	/**
	 * Constructor. 
	 * @param name Name of the API.
	 * @param source Source this information summary is got from. Can be used
	 *               to find further details.
	 * @param sourceDescription Description for the source shown in the UI.
	 */
	public APIShortDescription(String name, String source, String sourceDescription){
		this.name = name;
		this.source = source;
		this.sourceDescription = sourceDescription;		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the sourceDescription
	 */
	public String getSourceDescription() {
		return sourceDescription;
	}

	/**
	 * Set API Details for this Description
	 * @param apiDetails
	 */
	public void setAPIDetails(APIDetails apiDetails) {
		this.apiDetails = apiDetails;
	}
	
	/**
	 * Get API Details for this Description
	 * @param apiDetails or <code>null</code> if not set.
	 */
	public APIDetails getAPIDetails() {
		return apiDetails;
	}

	/**
	 * Check if this Description has {@link APIDetails} added.
	 * @return <code>true</code> if API Details is added, <code>false</code> otherwise.
	 */
	public boolean hasAPIDetails(){
		return apiDetails != null;
	}
	
	public  void addSerachedData (ArrayList<String> serachedData) {
		
		this.detailsShortData =serachedData;
			}
	
public ArrayList<String>  	getSearchedData()
{
	return  detailsShortData;
}
	
}
