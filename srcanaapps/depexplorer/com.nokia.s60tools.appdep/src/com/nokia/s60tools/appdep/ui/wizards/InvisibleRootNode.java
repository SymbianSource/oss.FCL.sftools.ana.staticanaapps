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

import java.util.ArrayList;
import java.util.Iterator;


/**
 * SDK Selection tree view's invisible root component used to add
 * actual user visible data.
 */
class InvisibleRootNode extends TreeViewNode{

	/**
	 * SDK node objects owned by the root node.
	 */
	private ArrayList<SdkTreeViewNode> sdkList = null;
	
	/**
	 * Adds new node.
	 * @param sdkNode Node to add.
	 */
	public void addSdkNode(SdkTreeViewNode sdkNode){
		sdkList.add(sdkNode);
	}
	
	/**
	 * Constructor.
	 */
	public InvisibleRootNode(){	
		super(null);
		sdkList = new ArrayList<SdkTreeViewNode>();
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.TreeViewNode#getChildren()
	 */
	public Object[] getChildren() {
		return sdkList.toArray();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.TreeViewNode#hasChildren()
	 */
	public boolean hasChildren() {
		return (sdkList.size() > 0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Tries to find tree viewer node that matches
	 * with the given parameters.
	 * @param sdkIdString Id string of the SDK.
	 * @return Returns the matching object if it was found, 
	 *         otherwise return <code>null</code>.
	 */
	public Object find(String sdkIdString) {
		for (Iterator<SdkTreeViewNode> iter = sdkList.iterator(); iter.hasNext();) {
			SdkTreeViewNode sdkNode = iter.next();
			Object obj = sdkNode.find(sdkIdString);
			if(obj != null){
				return obj;
			}
		}
		// No matches were found
		return null;
	}
}