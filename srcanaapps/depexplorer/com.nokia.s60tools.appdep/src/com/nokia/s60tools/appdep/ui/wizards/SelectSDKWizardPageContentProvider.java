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
 
 
package com.nokia.s60tools.appdep.ui.wizards;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkEnvInfomationResolveFailureException;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.sdk.SdkManager;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * The content provider class is responsible for
 * providing objects to the SDK Selection Wizard. 
 */
class SelectSDKWizardPageContentProvider implements IStructuredContentProvider{
	/**
	 * Invisible root object.
	 */
	private InvisibleRootNode invisibleRoot;
	
	/**
	 * Empty object array returned whenever there is no content.
	 */
	static Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Content provider's constructor
	 */
	public SelectSDKWizardPageContentProvider(){
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
		if (parent.equals(this)) {
			if (invisibleRoot==null){
				invisibleRoot = new InvisibleRootNode();
	    		SdkInformation[] sdkInfoColl = null;
				try {
					sdkInfoColl = SdkManager.getSdkInformation();
		    		for (int i = 0; i < sdkInfoColl.length; i++) {
						SdkInformation info = sdkInfoColl[i];
						invisibleRoot.addSdkNode(new SdkTreeViewNode(info, invisibleRoot));
					}
				} catch (SdkEnvInfomationResolveFailureException e) {
					e.printStackTrace();
					AppDepConsole.getInstance().println(e.getMessage(), 
												IConsolePrintUtility.MSG_ERROR);
				}	    		
			}
		}
		return invisibleRoot.getChildren();
	}
	
	/**
	 * Tries to find tree viewer node that matches
	 * with the given parameters.
	 * @param sdkIdString Id string of the SDK.
	 * @return Returns the matching object if it was found, 
	 *         otherwise return <code>null</code>.
	 */
	public Object find(String sdkIdString){
		return invisibleRoot.find(sdkIdString);
	}
		
}
