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
 * Represents toolchain abstraction in 
 * the level needed for the application. 
 */
public class Toolchain implements IToolchain {

	private final String name;
	private final String description;
	private boolean isInstalled;
	private final boolean isDefault;
	private String toolchainVersion;

	/**
	 * Constructor.
	 * @param name Toolchain name
	 * @param description Toolchain description
	 * @param isDefault set to <code>true</code> if this is default toolchain to be used, otherwise set to <code>false</code>
	 * @param isInstalled set to <code>true</code> if this toolchain is installed on user's workstation, otherwise set to <code>false</code>
	 */
	public Toolchain(String name, String description, boolean isDefault, boolean isInstalled){
		this.name = name;
		this.description = description;
		this.isDefault = isDefault;
		this.isInstalled = isInstalled;		
	}
	
	/**
	 * Constructor for setting toolchain initially as non-installed.
	 * @param name Toolchain name
	 * @param description Toolchain description
	 * @param isDefault set to <code>true</code> if this is default toolchain to be used, otherwise set to <code>false</code>
	 */
	public Toolchain(String name, String description, boolean isDefault){
		this.name = name;
		this.description = description;
		this.isDefault = isDefault;
		this.isInstalled = false;		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IToolchain#getToolchainName()
	 */
	public String getToolchainName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IToolchain#getToolchainDescription()
	 */
	public String getToolchainDescription() {
		return description;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public boolean isDefault() {
		return isDefault;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IToolchain#getVersion()
	 */
	public String getVersion() {
		return toolchainVersion;
	}

	/**
	 * Sets toolchain version.
	 * @param version toolchain version
	 */
	public void setVersion(String version) {
		toolchainVersion = version;
	}	
	
}
