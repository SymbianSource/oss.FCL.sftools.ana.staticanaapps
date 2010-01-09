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
 
 

package com.nokia.s60tools.appdep.core;

/**
 * Common interface for all the supported 
 * toolchain types.
 */
public interface IToolchain {
	
	/**
	 * Returns the build type name that matches with
	 * the toolchain name used when invoking command
	 * line tool.
	 * @return Toolchain type name.
	 */
	public String getToolchainName();
	
	/**
	 * Returns a short description of the toolchain.
	 * @return Toolchain description.
	 */
	public String getToolchainDescription();
	
	/**
	 * Returns the installation status.
	 * @return Returns <code>true</code> if toolchain is
	 * installed, otherwise <code>false</code>.
	 */
	public boolean isInstalled();
	
	/**
	 * Updates the installation status for the toolchain.
	 */
	public void setIsInstalled(boolean isInstalled);
	
	/**
	 * Check it this toolchain is a default one.
	 * @return Returns <code>true</code> if toolchain is
	 * default one, otherwise <code>false</code>.
	 */
	public boolean isDefault();
	
	/**
	 * Gets toolchain version if available.
	 * @return toolchain version, or <code>null</code> if not available.
	 */
	public String getVersion();
	
}
