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

/**
 * Stores the informative metadata for 
 * a search method extension.
 */
public interface ISearchMethodExtensionInfo {
	/**
	 * Gets the id of the search method.
	 * @return Id of the search method.
	 */
	public String getId();
	
	/**
	 * Gets the description for the search method.
	 * @return Description for the search method.
	 */
	public String getDescription();
	
	/**
	 * Checks if this search method is the default one, unless
	 * otherwise decided according user settings.
	 * @return Returns <code>true</code> if this search method is the default one,
	 *                 otherwise returns <code>false</code>.
	 */
	public boolean isDefault();
	
	/** 
	 * Compares the Id's of the two search methods
	 * @return <code>true</code> if the two search Ids match, otherwise <code>false</code>.
	 */
	public boolean hasEqualId(ISearchMethodExtensionInfo extInfo);
}
