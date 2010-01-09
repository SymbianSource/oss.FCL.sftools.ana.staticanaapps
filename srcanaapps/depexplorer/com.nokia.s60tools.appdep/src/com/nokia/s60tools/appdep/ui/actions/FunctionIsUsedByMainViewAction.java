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
 
 
package com.nokia.s60tools.appdep.ui.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.job.UsedByOtherComponentsJob;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;

/**
 * Common base class for actions querying
 * is used -relation ships for different 
 * function types.
 */
public abstract class FunctionIsUsedByMainViewAction extends AbstractMainViewAction {

	/**
	 * Resulting components found by the query,
	 */
	ArrayList<ComponentPropertiesData> resultComponentsArrayList = null;

	/**
	 * Constructor.
	 * @param view Reference to main view.
	 */
	public FunctionIsUsedByMainViewAction(MainView view) {
		super(view);
	}

	/**
	 * Starts job that search for the components that are using
	 * the function given as parameter.
	 * @param componentName Component name scope for the functions.
	 * @param functionName  Name of the function to search dependencies for.
	 * @param ordinalAsString Ordinal of the function to search dependencies for.
	 */
	protected void startIsUsedDependencyQueryJob(String componentName, String functionName, String ordinalAsString) {
		AppDepSettings settings = AppDepSettings.getActiveSettings();
		if(settings.getCurrentlyAnalyzedComponentName() != null){
			AppDepConsole.getInstance().println(Messages.getString("FunctionIsUsedByMainViewAction.FunctionIsUsedBy_Query_Start_Console_Msg") //$NON-NLS-1$
	                + functionName + "'..."); //$NON-NLS-1$
			
			resultComponentsArrayList = new ArrayList<ComponentPropertiesData>();
			Job jb = new UsedByOtherComponentsJob(Messages.getString("FunctionIsUsedByMainViewAction.FunctionIsUsedBy_Job_Title_Text"), settings, //$NON-NLS-1$
													componentName,
													functionName,
													ordinalAsString,
													resultComponentsArrayList,
													AppDepPlugin.getCurrentlyActivePage());
			
			// We do not want cache generation to block other 
			// jobs and therefore using the lowest priority
			jb.setPriority(Job.DECORATE);
			jb.schedule();
		}
	}

}
