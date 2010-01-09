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

/**
 * General interface for objects that can be visited
 * by <code>IVisitor</code> instances.
 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor
 */
public interface IVisitable {
	
	/**
	 * Calls concrete method performing visiting 
	 * from the <code>IVisitor</code> instance passes
	 * as parameter. 
	 * @param visitor Visitor to be used to perform 
	 *                some concrete visit operation.
	 */
	public void accept(IVisitor visitor);

}
