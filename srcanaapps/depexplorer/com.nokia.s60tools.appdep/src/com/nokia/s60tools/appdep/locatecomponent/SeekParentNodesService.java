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

import java.util.HashMap;
import java.util.Map;

import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;

/**
 * Search service facade class offering utilities for seeking {@link ComponentParentNode}s under given node.
 */
public class SeekParentNodesService {

	/**
	 * Finds the child nodes for given node.
	 * @param startNode Start node for the search. If root node is given, all {@link ComponentParentNode}s is returned.
	 * @return Map of {@link ComponentParentNode}s under given node where map keys will be on lower case.
	 */
	public static Map<String, ComponentParentNode> findParentNodes(ComponentNode startNode){
		Map<String, ComponentParentNode> searchResultList = new HashMap<String, ComponentParentNode>();
		IVisitor v = new SeekParentNodesVisitor(searchResultList);
		startNode.accept(v);
		return searchResultList;
	}
	
}
