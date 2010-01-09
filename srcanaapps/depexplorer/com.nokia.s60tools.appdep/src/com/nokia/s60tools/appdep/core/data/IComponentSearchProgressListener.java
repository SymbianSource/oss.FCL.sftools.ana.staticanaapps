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
 * Notification interface that enables to follow-up component search progress.
 */
public interface IComponentSearchProgressListener {
	
	/**
	 * Search start notification callback.
	 */
	public void searchStarted();
	
	/**
	 * Notifies about addition of single component.
	 * @param node Component node that has been added.
	 * @param componentTotalCount Total count of components found so far.
	 */
	public void componentAdded(ComponentNode node, int componentTotalCount);
	
	/**
	 * Search aborted notification callback.
	 * @param componentTotalCount Total count of components found so far.
	 */
	public void searchAborted(int componentTotalCount);

	/**
	 * Search end notification callback.
	 * @param componentTotalCount Total count of components found.
	 */
	public void searchFinished(int componentTotalCount);
}
