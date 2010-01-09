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

import java.util.ArrayList;

import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;

/**
 * Class representing component parent node in component tree.
 */
public class ComponentParentNode extends ComponentNode {
	
	/**
	 * Binding type enumerator.
	 */
	public static enum CompBindType{NONE, AUTO_BIND, USER_BIND};
	
	/**
	 * Node name.
	 */
	private String name = null;
	
	/**
	 * Node's children 
	 */
	private ArrayList<ComponentNode> children = null;
	
	/**
	 * Node listener. 
	 */
	private IComponentParentNodeListener listener = null;
	
	/**
	 * This information is used to select the correct icon
	 * by the label provider for the root component node.
	 */
	private boolean isRootComponent = false;

	/**
	 * This flag is set to true if information for
	 * this component cannot be found from cache.
	 */
	private boolean isMissing = false;	
	
	/**
	 * This boolean flag is set to <code>true</code> after
	 * all direct children of this component are resolved.
	 */
	private boolean isDirectChildrensResolved = false;
	
	/**
	 * Original name of node, if concrete name was set
	 */
	private String originalName = null;
	
	/**
	 * Bind type of node, used when concrete name was set 
	 */
	private CompBindType compBindType = CompBindType.NONE;
	
	/**
	 * Target platform for the component node, or <code>null</code> if not set.
	 */
	private ITargetPlatform targetPlatform = null;	

	/**
	 * Constructor.
	 * @param name Node name.
	 */
	public ComponentParentNode(String name) {
		super();
		this.name = name;
		children = new ArrayList<ComponentNode>();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#toString()
	 */
	public String toString() {
		return getName();
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds new child node.
	 * @param child child node to add
	 */
	public void addChild(ComponentNode child) {
		addChild(child, children.size());
	}
	
	/**
	 * Removes given child node.
	 * @param child child node to remove 
	 */
	public void removeChild(ComponentNode child) {
		children.remove(child);
		child.setParent(null);
		if(listener != null){
			listener.childRemoved(child);
		}
	}
	
	/**
	 * Replaces a child node.
	 * @param childToBeRemoved child node to replace to
	 * @param childToBeAdded child node to replace with
	 */
	public void replaceChild(ComponentNode childToBeRemoved, ComponentNode childToBeAdded){
		int index = children.indexOf(childToBeRemoved);
		removeChild(childToBeRemoved);
		addChild(childToBeAdded, index);
	}

	/**
	 * Adds child to the specified index.
	 * @param childToBeAdded child node to be added.
	 * @param index index for addition.
	 */
	private void addChild(ComponentNode childToBeAdded, int index) {
		children.add(index, childToBeAdded);
		childToBeAdded.setParent(this);
		if(listener != null){
			listener.childAdded(childToBeAdded);
		}
	}

	/**
	 * Gets node's children.
	 * @return node's children.
	 */
	public ComponentNode [] getChildren() {
		return (ComponentNode [])children.toArray(new ComponentNode[children.size()]);
	}
	
	/**
	 * Checks if node has any children.
	 * @return <code>true</code> in case has children, otherwise <code>false</code>.
	 */
	public boolean hasChildren() {
		return children.size()>0;
	}

	/**
	 * Checks if node is root component.
	 * @return <code>true</code> if node is root component, otherwise <code>false</code>.
	 */
	public boolean isRootComponent() {
		return isRootComponent;
	}

	/**
	 * Sets root component property.
	 * @param isRootComponent <code>true</code> if node is set to root component, otherwise <code>false</code>.
	 */
	public void setRootComponent(boolean isRootComponent) {
		this.isRootComponent = isRootComponent;
	}

	/**
	 * Checks missing status.
	 * @return Returns the isMissing.
	 */
	public boolean isMissing() {
		return isMissing;
	}

	/**
	 * Sets missing status.
	 * @param isMissing The isMissing to set.
	 */
	public void setMissing(boolean isMissing) {
		this.isMissing = isMissing;
	}

	/**
	 * Checks if direct dependencies for the node has been resolved.
	 * @return Returns the isDirectChildrensResolved.
	 */
	public boolean isDirectChildrensResolved() {
		return isDirectChildrensResolved;
	}

	/**
	 * Sets status of direct dependencies resolvance.
	 * @param isDirectChildrensResolved The isDirectChildrensResolved to set.
	 */
	public void setDirectChildrensResolved(boolean isDirectChildrensResolved) {
		this.isDirectChildrensResolved = isDirectChildrensResolved;
	}
	
	/**
	 * Removes all children nodes.
	 */
	public void removeAllChildren(){
		children.clear();
		if(listener != null){
			listener.allChildrensRemoved();
		}
	}
	
	/**
	 * Sets node listener.
	 * @param listener Listener to set.
	 */
	public void setNodeListener(IComponentParentNodeListener listener){
		this.listener = listener;
	}
	
	/**
	 * Gets root node.
	 * @return root node.
	 */
	public ComponentParentNode getRootNode(){
		if(this.isRootComponent){
			return this;
		}
		else{
			return getParent().getRootNode();
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitable#accept(com.nokia.s60tools.appdep.ui.views.data.IVisitor)
	 */
	public void accept(IVisitor visitor) {
		visitor.visit(this);
		
	}

	/**
	 * Sets generic component name to concrete component name. 
	 * Use only when found component that is generic a component, e.g. <code>hal.dll</code>
	 * and there is user selected or automatically selected concrete name for component.
	 * When component is missing {@link ComponentParentNode.isMissing()} 
	 * component can be generic component.
	 * @param concreteComponentName Concrete component name.
	 * @param bindType of component, if it was user selected, then set {@link CompBindType.USER_BIND}
	 * if it was set automatically by preferences, then set {@link CompBindType.AUTO_BIND}
	 */
	public void setConcreteName(String concreteComponentName, CompBindType bindType) {
		this.originalName  = this.name;
		this.name = concreteComponentName;
		this.compBindType = bindType;
	}

	/**
	 * Return the original name of the component, if concrete name was set.
	 * @return the original name of component or <code>null</code> if component does not have a concrete name.
	 */
	public String getOriginalName() {
		return originalName;
	}
	
	/**
	 * Check if component was a generic component originally and a concrete component
	 * was found to replacing it. To get original component name use @see ComponentParentNode.getOriginalName().
	 * @return <code>true</code> if component was a generic component and 
	 *         a concrete component was set to replace it, <code>false</code> otherwise.
	 */
	public boolean wasGenericComponent(){
		return  getOriginalName() == null ? false : true;
	}

	/**
	 * Get bind type of component. 
	 * @return <code>CompBindType.NONE</code> if this component is a concrete component by its own 
	 *         or it has not been set to concrete component, 
	 * <code>CompBindType.AUTO_BIND</code> if component was set to concrete automatically by preference list,
	 * <code>CompBindType.USER_BIND</code> if component was set to concrete by user action with dialog.
	 */
	public CompBindType getGenericComponentBindType() {
		return compBindType ;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.ComponentNode#getTargetPlatform()
	 */
	@Override
	public ITargetPlatform getTargetPlatform() {
		return targetPlatform;
	}
	
	/**
	 * Sets target platform for the component node, or <code>null</code> if not set.
	 * @param targetPlatform target platform for the component node
	 */
	public void setTargetPlatform(ITargetPlatform targetPlatform) {
		this.targetPlatform = targetPlatform;
	}

}