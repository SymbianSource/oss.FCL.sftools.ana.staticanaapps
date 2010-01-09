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
 
package com.nokia.s60tools.appdep.search;

/**
 * Class for search match type
 */
public class MatchType {
	
	/**
	 * <code>true</code> in case of case sensitive search.
	 */
	private boolean isCaseSensitiveSearch = false;
	
	/**
	 * Match type used for the search.
	 */
	private MatchTypes matchType;
	
	/**
	 * Constructor.
	 * @param matchType Match type used for the search.
	 */
	public MatchType(MatchTypes matchType){
		this.matchType = matchType;		
	}
	
	/**
	 * Match types selectable in search Options
	 */
	public enum MatchTypes{
		CONTAINS, // Matches when the search string is contained in object's name
		STARTS_WITH, // Matches when object's name starts with the search string
		ENDS_WITH, // Matches when object's name ends with the search string
		EXACT_MATCH, // Matches when object's name and the search string equals
		REGULAR_EXPRESSION, // Matches when the search string as regular expression matches with object's name
	}

	/**
	 * Gets case sensitive check status.
	 * @return <code>true</code> in case of case sensitive search, otherwise <code>false</code>.
	 */
	public boolean isCaseSensitiveSearch() {
		return isCaseSensitiveSearch;
	}

	/**
	 * Sets case sensitiveness for the search.
	 * @param isCaseSensitiveSearch the isCaseSensitiveSearch to set
	 */
	public void setCaseSensitiveSearch(boolean isCaseSensitiveSearch) {
		this.isCaseSensitiveSearch = isCaseSensitiveSearch;
	}

	/**
	 * Gets match type.
	 * @return the matchType
	 */
	public MatchTypes getMatchType() {
		return matchType;
	}

	/**
	 * Sets match type.
	 * @param matchType the matchType to set
	 */
	public void setMatchType(MatchTypes matchType) {
		this.matchType = matchType;
	}	

}
