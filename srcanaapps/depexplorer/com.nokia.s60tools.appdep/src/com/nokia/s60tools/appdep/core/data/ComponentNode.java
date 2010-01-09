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

import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.ui.views.data.IVisitable;


/**
 * This is an abstract class that defines the parent relationship
 * and defines an interface i.e. abstract methods that force that
 * the derived node classes must be able to tell their name.
 */
public abstract class ComponentNode implements IVisitable {
	
	/**
	 * Separator character used in fully qualified component node name.
	 */
	protected static final String SEPARATOR = "/"; //$NON-NLS-1$
	
	/**
	 * Parent node reference.
	 */
	private ComponentParentNode parent;

	/**
	 * Constructor.
	 */
	public ComponentNode() {
	}
	
	/**
	 * Sets parent for the node.
	 * @param parent parent node
	 */
	public void setParent(ComponentParentNode parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets parent for the node.
	 * @return parent for the node.
	 */
	public ComponentParentNode getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	
	/**
	 * Gets node name.
	 * @return node name.
	 */
	public abstract String getName();

	/**
	 * Gets node's fully qualified name.
	 * @return fully qualified name.
	 */
	public String getFullName(){

		// Checking root component case
		if(this instanceof ComponentParentNode){			
			if(((ComponentParentNode)this).isRootComponent()){
				return SEPARATOR + getName(); 
			}
		}
		
		// Otherwise proceeding normally towards root node
		StringBuffer fullName = new StringBuffer(getName());
		ComponentParentNode parent = getParent();
		while(!parent.isRootComponent()){
			fullName.insert(0, parent.getName() + SEPARATOR);
			parent = parent.getParent();
		}
		// Adding finally the root component
		fullName.insert(0, SEPARATOR + parent.getName() + SEPARATOR);
				
		return fullName.toString();
	}

	/**
	 * Gets target platform for the component node, or <code>null</code> if not set.
	 * Abstract method to be implemented by subclasses.
	 * @return target platform for the component node, or <code>null</code> if not set.
	 */
	public abstract ITargetPlatform getTargetPlatform();
	
}
