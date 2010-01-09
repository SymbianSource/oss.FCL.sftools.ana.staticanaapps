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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;

/**
 * AppDep specific extensions to IWizard interface.
 */
public interface ISelectSDKWizard extends IWizard {

	/**
	 * In here is listed all the possible exit
	 * status codes for the wizards that implement
	 * this interface. Values are starting from 100
	 * in order not to overlap with normally used
	 * UI constants.
	 */
	public static final int FINISH_CACHE_CREATION = 100;
	public static final int FINISH_COMPONENT_SELECTED = 200;
	public static final int CANCEL = 300;
	
	/**
	 * Gets exit status for the wizard.
	 * @return Exit status for the wizard.
	 */
	public int getExitStatus();

	/**
	 * Gets component iterator for the currently configured/selected.
	 * SDK/Target Platform.
	 * @param duplicateItemsList Out parameter that contains the list of duplicate
	 *                           components found from the selected targets.
	 * @return Component Iterator, or <code>null</code> if component 
	 *         iterator cannot be built.
	 */
	public Iterator<ComponentListNode> getComponentIterator(
			List<String> duplicateItemsList);

	/**
	 * Gets settings object used by this wizard instance.
	 * @return settings object used by this wizard instance.
	 */
	public AppDepSettings getSettings();

	/**
	 * Updates currently selected component name.
	 * @param componentName Name of the component to be set as selected one.
	 * @param targetPlatform Target platform of the component to be set as selected one. 
	 */
	public void updateAnalyzedComponentSelection(String componentName, ITargetPlatform targetPlatform);

	/**
	 * Sets canFinish flag to <code>true</code> and updates exit status.
	 * @param exitStatus New exist status.
	 */
	public void enableCanFinish(int exitStatus);

	/**
	 * Sets canFinish flag <code>false</code> and updates exit status
	 * by default to <code>IAppDepWizard.CANCEL</code>.
	 */
	public void disableCanFinish();

	/**
	 * Delegate method for initializes cache generation options that are available 
	 * based on the currently selected build target. 
	 * @see com.nokia.s60tools.appdep.ui.wizards.CacheGenerationOptionsWizardPage#setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget()
	 */
	public void setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget();

}
