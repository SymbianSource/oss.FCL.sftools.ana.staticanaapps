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
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;


/**
 * Represents leaf node that cannot have any children 
 * in component tree hierarchy. This leaf node is a link
 * to actual component node that already exists in the 
 * component tree. Link leaf node is used to break cyclic
 * dependency hierarchies thus making possible to represent
 * data in tree view. 
 */
public class ComponentLinkLeafNode extends ComponentNode {
	
	/**
	 * Component node the link node refers to.
	 */
	private ComponentParentNode referredComponent;
	
	/**
	 * Constructor.
	 * @param referredComponent Component node the link node refers to.
	 */
	public ComponentLinkLeafNode(ComponentParentNode referredComponent) {
		super();
		this.referredComponent = referredComponent;
	}

	/**
	 * Get referred component node.
	 * @return Returns the referredComponent.
	 */
	public ComponentParentNode getReferredComponent() {
		return referredComponent;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#toString()
	 */
	public String toString() {
		return referredComponent.getName();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#getName()
	 */
	public String getName() {
		return referredComponent.getName();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitable#accept(com.nokia.s60tools.appdep.ui.views.data.IVisitor)
	 */
	public void accept(IVisitor visitor) {
		visitor.visit(this);
		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#getTargetPlatform()
	 */
	@Override
	public ITargetPlatform getTargetPlatform() {
		return getReferredComponent().getTargetPlatform();
	}
}
