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


package com.nokia.s60tools.appdep.ui.views.data;

import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;

/**
 * General visitor interface for visiting component tree
 * hierarchy. To be implemented by concrete visitors.
 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitable
 */
public interface IVisitor {
	
	/**
	 * Visit method for component link leaf nodes.
	 * @param node Component link leaf node to be visited.
	 */
	public void visit(ComponentLinkLeafNode node);
	/**
	 * Visit method for component parent nodes.
	 * @param node Component parent node to be visited.
	 */
	public void visit(ComponentParentNode node);
}
