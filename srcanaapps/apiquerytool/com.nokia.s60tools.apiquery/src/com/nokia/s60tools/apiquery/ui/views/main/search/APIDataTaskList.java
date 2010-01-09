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

package com.nokia.s60tools.apiquery.ui.views.main.search;
import java.util.Vector;

/**
 * Class that plays the role of the domain model in the TableViewerExample
 * In real life, this class would access a persistent store of some kind.
 * 
 */

//Data  Objects

public class APIDataTaskList {

	private Vector<APITask> tasks ;
	public APIDataTaskList() {
		super();
		tasks = new Vector<APITask>();
	}
	public void addTask(APITask task)
	{
		tasks.add(task);
	}

	public Vector<APITask> getTasks() {
		return tasks;
	}

	public void removeTaksList()
	{
		tasks = new Vector<APITask>();
	}
}
