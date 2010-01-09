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

/**
 * Tree view node object.
 */
abstract class TreeViewNode{
	
	/**
	 * Parent reference.
	 */
	protected Object parent = null;
	
	/**
	 * Constructor.
	 */
	public TreeViewNode(){			
		this.parent = null;
	}

	/**
	 * Constructor.
	 * @param parent Parent node.
	 */
	public TreeViewNode(TreeViewNode parent){			
		this.parent = parent;
	}

	/**
	 * Gets parent node.
	 * @return Returns the parent.
	 */
	public Object getParent() {
		return parent;
	}

	/**
	 * Gets children nodes.
	 * @return children nodes
	 */
	public abstract Object[] getChildren();

	/**
	 * Checks if node has children.
	 * @return <code>true</code> if has children.
	 */
	public abstract boolean hasChildren();
}