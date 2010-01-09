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


package com.nokia.s60tools.appdep.find;

import java.util.List;

import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;

/**
 * Concrete visitor class for enabling find from component tree.
 */
public class FindVisitor implements IVisitor{

	/**
	 * String to look for.
	 */
	private final String matchString;
	
	/**
	 * Find results to be returned to the client class 
	 */
	private final List<ComponentNode> findResultList;
	
	/**
	 * Constructor.
	 * @param findString String to find for
	 * @param findResultList Find results to be returned in the list object.
	 */
	public FindVisitor(String findString, List<ComponentNode> findResultList){
		this.matchString = findString;
		this.findResultList = findResultList;
		
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode)
	 */
	public void visit(ComponentLinkLeafNode node) {
		if(isMatch(node.toString())){
			findResultList.add(node);
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentParentNode)
	 */
	public void visit(ComponentParentNode node) {
		
		if(isMatch(node.toString())){
			findResultList.add(node);
		}

		// Going through also all the childrens
		ComponentNode[] childArray = node.getChildren();
		for (int i = 0; i < childArray.length; i++) {
			ComponentNode childNode = childArray[i];
			childNode.accept(this);
		}
		
	}

	/**
	 * Checks if the given component name matches the find.
	 * @param cmpName component name
	 * @return <code>true</code> if matches, otherwise <code>false</code>.
	 */
	private boolean isMatch(String cmpName) {		
		return cmpName.toLowerCase().contains(matchString.toLowerCase());
	}

}
