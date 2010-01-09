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


package com.nokia.s60tools.appdep.locatecomponent;

import java.util.Map;

import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;

/**
 * Concrete visitor class for seeking parent nodes from nodes.
 */
public class SeekParentNodesVisitor implements IVisitor{

	
	/**
	 * Search results to be returned to the client class 
	 */
	private final Map<String, ComponentParentNode> searchResultList;
	
	/**
	 * Constructor.
	 * @param searchString String to search for
	 * @param searchResultList Search results to be returned in the list object. Component names will be there in lower case.
	 */
	public SeekParentNodesVisitor( Map<String, ComponentParentNode> searchResultList){
		this.searchResultList = searchResultList;
		
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode)
	 */
	public void visit(ComponentLinkLeafNode node) {
		//No action with leaf nodes
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentParentNode)
	 */
	public void visit(ComponentParentNode node) {
	    // Adds node to search result list
		searchResultList.put(node.getName().toLowerCase(), node);

		// Going through also all the childrens
		ComponentNode[] childArray = node.getChildren();
		for (int i = 0; i < childArray.length; i++) {
			ComponentNode childNode = childArray[i];
			childNode.accept(this);
		}	
	}

}
