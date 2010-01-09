/*
* Copyright (c) 2006 Nokia Corporation and/or its subsidiary(-ies). 
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
 
 
package com.nokia.s60tools.apiquery.shared.searchmethod;

public class SearchMethodExtensionInfo  implements ISearchMethodExtensionInfo{

	private String id;
	private String description;
	private boolean isDefault;

	public SearchMethodExtensionInfo(){
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtensionInfo#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtensionInfo#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtensionInfo#isDefault()
	 */
	 public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Sets the description.
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the id.
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the isDefault status.
	 * @param isDefault The isDefault to set.
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtensionInfo#hasEqualId(com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtensionInfo)
	 */
	public boolean hasEqualId(ISearchMethodExtensionInfo extInfo) {
		return this.getId().equalsIgnoreCase(extInfo.getId());
	}
}
