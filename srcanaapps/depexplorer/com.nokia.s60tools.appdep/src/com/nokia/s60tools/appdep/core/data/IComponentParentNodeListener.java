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
 
 
package com.nokia.s60tools.appdep.core.data;

/**
 * This class declares a listener interface
 * that can be used if there is need to listen
 * for component node events.
 */
public interface IComponentParentNodeListener {
	
	/**
	 * Called when a new child node is added.
	 * @param child Added child node.
	 */
	public void childAdded(ComponentNode child);
	
	/**
	 * Called when a child node is removed.
	 * @param child Removed child node.
	 */
	public void childRemoved(ComponentNode child);
	
	/**
	 * Called when all child nodes are removed.
	 */
	public void allChildrensRemoved();
}
