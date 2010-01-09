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
 
 
package com.nokia.s60tools.appdep.ui.views.main;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.data.IComponentParentNodeListener;
import com.nokia.s60tools.appdep.resources.Messages;

/**
 * The content provider class is responsible for
 * providing objects to the Main View. 
 */
class MainViewComponentTreeContentProvider implements IStructuredContentProvider, 
									   ITreeContentProvider,
									   IComponentParentNodeListener{
	/**
	 * Component tree's invisible root node.
	 */
	private ComponentParentNode invisibleRoot;
	/**
	 * Main view reference.
	 */
	private MainView view;
	
	private String sdkId = null;

	/**
	 * Constructor
	 * @param view Main view reference.
	 */
	public MainViewComponentTreeContentProvider(MainView view){
		invisibleRoot = new ComponentParentNode(""); //$NON-NLS-1$
		invisibleRoot.setNodeListener(this);
		this.view = view;
	}
	
	/**
	 * @return
	 */
	public Object getInput(){
		return invisibleRoot;
	}

	/**
	 * Gets root component node.
	 * @return Returns root component node.
	 */
	public Object getRootComponentNode(){
		// There is for sure only one child for invisible root
		return invisibleRoot.getChildren()[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		boolean sdkChanged = false;
		
		try {
			if (parent.equals(getInput())) {
				String currentlyAnalyzedComponentName = AppDepSettings.getActiveSettings().getCurrentlyAnalyzedComponentName();
				ITargetPlatform currentlyAnalyzedComponentTargetPlatform = AppDepSettings.getActiveSettings().getCurrentlyAnalyzedComponentTargetPlatform();
				
				// Adding default start-up message as invisible root's new child if component has not selected, and 
				// if invisible root does not have any children (either components, or the start-up message already added). 
			   boolean isStartup = (currentlyAnalyzedComponentName == null) && (!invisibleRoot.hasChildren());
			   // Component has been selected but tree needs to be populated (e.g. in view close/re-open situation)
			   boolean noNodesButComponentIsSelected = (!invisibleRoot.hasChildren()) && (currentlyAnalyzedComponentName != null);
			   //In case component tree is already populated it makes sense to make further checking
			   boolean areNodesButComponentHasChanged = false; // default value in case not going to next if
			   if(!isStartup && !noNodesButComponentIsSelected){
				   if(sdkId == null && AppDepSettings.getActiveSettings().getCurrentlyUsedSdk() != null){
					   //we have no previous SDK ID to compare with .
					   // Storing the ID for future comparisons.
					   sdkId = AppDepSettings.getActiveSettings().getCurrentlyUsedSdk().getSdkId();
				   }
				   //We have a previous SDK id . Let's check if it has been changed
				   else if(sdkId != null && !sdkId.equalsIgnoreCase(AppDepSettings.getActiveSettings().getCurrentlyUsedSdk().getSdkId()))
				   {
					   sdkChanged = true;
					   sdkId = AppDepSettings.getActiveSettings().getCurrentlyUsedSdk().getSdkId();
				   }
				   
				   // Tree is already populated => safe to refer to root node.
				   ComponentNode rootNode = invisibleRoot.getChildren()[0];
				   ITargetPlatform rootNodeTargetPlatform = rootNode.getTargetPlatform();
				   boolean targetPlatformHasChanged = (rootNodeTargetPlatform != null && currentlyAnalyzedComponentTargetPlatform != null) 
				                                      && 
				                                      !rootNodeTargetPlatform.idEquals(currentlyAnalyzedComponentTargetPlatform.getId()); 				   
				   areNodesButComponentHasChanged = (currentlyAnalyzedComponentName != null)
				   											&&
					   										(invisibleRoot.hasChildren())
				   											&& 
				   											(!rootNode.getName().equals(currentlyAnalyzedComponentName)
				   											|| sdkChanged
															 || 
															 targetPlatformHasChanged);					   
			   }
			   
			   // Doing actions according analyzed situation
				if(isStartup){
					// No component to be analyzed has been selected by a user so far...giving initial prompt for the user
					String userMsg = Messages.getString("MainViewComponentTreeContentProvider.ComponentTreeView_Msg_After_Extension_Startup"); //$NON-NLS-1$
					ComponentParentNode root = new ComponentParentNode(userMsg);
					root.setRootComponent(true);
					invisibleRoot.addChild(root);				
				}
				else if(noNodesButComponentIsSelected || areNodesButComponentHasChanged){
					MainViewDataPopulator.populateView(
						                    invisibleRoot,
											new MainViewPopulateProgressListener(view), 
											currentlyAnalyzedComponentName,
											currentlyAnalyzedComponentTargetPlatform
													);
				}
				return getChildren(invisibleRoot);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e; // There is no way to recover...needed catch for debugging
		}
		return getChildren(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object child) {
		if (child instanceof ComponentNode) {
			return ((ComponentNode)child).getParent();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object [] getChildren(Object parent) {
		if (parent instanceof ComponentParentNode) {
			return ((ComponentParentNode)parent).getChildren();
		}
		return new Object[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof ComponentParentNode)
			return ((ComponentParentNode)parent).hasChildren();
		return false;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentParentNodeListener#childAdded(com.nokia.s60tools.appdep.core.data.ComponentNode)
	 */
	public void childAdded(ComponentNode child) {
		// not needed		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentParentNodeListener#childRemoved(com.nokia.s60tools.appdep.core.data.ComponentNode)
	 */
	public void childRemoved(ComponentNode child) {
		// not needed		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.IComponentParentNodeListener#allChildrensRemoved()
	 */
	public void allChildrensRemoved() {
		// not needed		
	}
	
	/**
	 * Sets given component node as new root component.
	 * @param node Component node to be set as new root component.
	 */
	public void setExistingNodeAsNewRoot(ComponentNode node){
		ComponentParentNode pNode = null;
		if(node instanceof ComponentParentNode){
			pNode = (ComponentParentNode) node;
		}
		else if (node instanceof ComponentLinkLeafNode){
			ComponentLinkLeafNode linkNode = (ComponentLinkLeafNode) node;
			pNode = linkNode.getReferredComponent();			
		}
		
		// Just sanity check in case earlier checks has been failed
		if(pNode.isRootComponent()){
			return;
		}		
		
		invisibleRoot.removeAllChildren();
		pNode.setRootComponent(true);
		invisibleRoot.addChild(pNode);
	}
	
}
