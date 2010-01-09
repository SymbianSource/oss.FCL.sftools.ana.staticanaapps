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
 
 
/**
 * 
 */
package com.nokia.s60tools.appdep.ui.wizards;

import com.nokia.s60tools.sdk.SdkInformation;

/**
 * Represents SDK node in SDK selection wizard's tree view 
 */
class SdkTreeViewNode  extends TreeViewNode{

	/**
	 * Empty object array returned whenever there is no content.
	 */
	static Object[] EMPTY_ARRAY = new Object[0];
	
	/**
	 * SDK information object storing SDK data for .
	 */
	private SdkInformation sdkInfo = null;
	
	/**
	 * Constructor.
	 * @param sdkInfo SDK info object.
	 * @param parent Parent node.
	 */
	public SdkTreeViewNode(SdkInformation sdkInfo, TreeViewNode parent){
		super(parent);
		this.sdkInfo = sdkInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.TreeViewNode#getChildren()
	 */
	public Object[] getChildren() {
		return EMPTY_ARRAY;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.TreeViewNode#hasChildren()
	 */
	public boolean hasChildren() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(sdkInfo != null){
			return sdkInfo.getSdkId();				
		}
		return null;
	}

	/**
	 * Gets SDK information objecct for the node.
	 * @return SDK information objecct for the node.
	 */
	public SdkInformation getSdkInfo() {
		return sdkInfo;
	}

	/**
	 * Tries to find tree viewer node that matches
	 * with the given parameters.
	 * @param sdkIdString Id string of the SDK.
	 * @return Returns the matching object if it was found, 
	 *         otherwise return <code>null</code>.
	 */
	public Object find(String sdkIdString) {
		if(sdkInfo.getSdkId().equalsIgnoreCase(sdkIdString)){
			// Match found
			return this;
		}
		// Not matching
		return null;
	}
}