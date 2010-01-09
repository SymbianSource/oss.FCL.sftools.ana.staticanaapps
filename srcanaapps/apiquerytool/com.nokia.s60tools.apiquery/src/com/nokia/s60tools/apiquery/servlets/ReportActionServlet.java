/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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
package com.nokia.s60tools.apiquery.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.apiquery.popup.actions.CheckIdentifierAction;
import com.nokia.s60tools.apiquery.popup.actions.OpenFileAction;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;

/**
 * Servlet that takes request from API Query report and forwards it to action.
 * Web Server inside Carbide must be up and running when Servlet is executed.
 * 
 * The Web server is running when API Query {@link MainView} is alive. 
 *  
 * @see APIQueryWebServerConfigurator#startServer()
 */
public class ReportActionServlet extends HttpServlet implements Servlet {


	/**
	 * Default ID.
	 */
	private static final long serialVersionUID = 1L;



	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
 
		String queryType = req.getParameter(ReportActionConstants.ACTION_QUERY_TYPE);
		try {
			String projectName;
			if(req.getParameter(ReportActionConstants.ACTION_QUERY_TYPE) != null){
				int type = 1;
				if(queryType != null){
					type = Integer.parseInt(queryType);
				}
				String searchString = (String) req.getParameter(ReportActionConstants.PARAM_QUERY_FOR);			
				runAPIQueryFor(searchString, type);			
			}
			else if(req.getParameter(ReportActionConstants.ACTION_OPEN_USED_HEADER) != null){
				String headerName = (String) req.getParameter(ReportActionConstants.ACTION_OPEN_USED_HEADER);
				projectName = (String) req.getParameter(ReportActionConstants.PARAM_PROJECT_NAME);
				openFileFromSDK(headerName, projectName);		
			}
			else if(req.getParameter(ReportActionConstants.ACTION_OPEN_USING_FILE) != null){
				String fileName = (String) req.getParameter(ReportActionConstants.ACTION_OPEN_USING_FILE);
				projectName = (String) req.getParameter(ReportActionConstants.PARAM_PROJECT_NAME);
				openFileFromProject(fileName, projectName);
			}
			else{
				String msg = Messages.getString("ReportActionServlet.NotSupportedAction_Msg"); //$NON-NLS-1$
				APIQueryConsole.getInstance().println(msg);
			}

			
			PrintWriter out = resp.getWriter();
			//When actual servlet page is loaded (Action is launched) directing user back where come from
			//by loading java script on body load.
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " + //$NON-NLS-1$
			                                    "Transitional//EN\">\n" + //$NON-NLS-1$
			            "<HTML>\n" + //$NON-NLS-1$
			            "<HEAD><TITLE></TITLE></HEAD>\n" + //$NON-NLS-1$
			            "<BODY onload=\"javascript:history.back()\">\n" + //$NON-NLS-1$
			            "</BODY></HTML>"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			String msg = Messages.getString("ReportActionServlet.NotSupportedQueryType_ErrMsg_Part1") +queryType +Messages.getString("ReportActionServlet.NotSupportedQueryType_ErrMsg_Part2"); //$NON-NLS-1$ //$NON-NLS-2$
			APIQueryConsole.getInstance().println(msg, APIQueryConsole.MSG_ERROR);
			e.printStackTrace();
		} catch (QueryOperationFailedException e) {
			showErrorMessage(e.getMessage());
		}

	}
	
	/**
	 * Shows on error message to user.
	 * @param message
	 */
	private void showErrorMessage(final String message) {
		Runnable run = new Runnable(){
			public void run(){
				new APIQueryMessageBox(message,
						SWT.ICON_ERROR | SWT.OK).open();
				
			}
		};
		Display.getDefault().asyncExec(run);
	}	


	/**
	 * Open File from project
	 * @param fileName
	 * @param projectName
	 */
	private void openFileFromProject(String fileName, String projectName) {
		OpenFileAction action = new OpenFileAction();
		action.openFileFromProject(fileName, projectName);
	}
	
	/**
	 * Open file from SDK
	 * @param fileName
	 * @param projectName
	 */
	private void openFileFromSDK(String fileName, String projectName) {
		OpenFileAction action = new OpenFileAction();
		action.openFileFromSDK(fileName, projectName);
	}



	/**
	 * Runs API Query for selected String with selected type
	 * @param searchString String to search for
	 * @param type API Query type
	 * @throws QueryOperationFailedException
	 */
	private void runAPIQueryFor(final String searchString, final int type) throws QueryOperationFailedException{
		
		ISearchMethodExtension currentlySelectedSearchMethod = UserSettings.getInstance().getCurrentlySelectedSearchMethod();
		
		//Check if search method is supported for selected type. Cannot really occur until
		//some data source is giving API Query report details for API that some other Data source is not supporting
		//and report is generated with different Data source that is currently in use.
		boolean isSupported = currentlySelectedSearchMethod.isSupportedQueryType(type);		
		if(!isSupported){
			String msg = Messages.getString("ReportActionServlet.NotSupportedSearchMethod_ErrMsg_Part1") //$NON-NLS-1$
			+ currentlySelectedSearchMethod.getAPIDetailNameInDetailsByQueryType(type)
			+Messages.getString("ReportActionServlet.NotSupportedSearchMethod_ErrMsg_Part2") //$NON-NLS-1$
			+ currentlySelectedSearchMethod.getExtensionInfo().getDescription()
			+"'."; //$NON-NLS-1$
			APIQueryConsole.getInstance().println(msg, APIQueryConsole.MSG_ERROR);
			throw new QueryOperationFailedException(
					msg);
		}
		
				
		//Runnable to run API Query action
		final Runnable run = new Runnable() {
			public void run() {
				// do the actual work in here
				try {
					CheckIdentifierAction act = new CheckIdentifierAction();
					act.runAPIQuery(searchString, type);
				} catch (Exception e) {
					e.printStackTrace();
					String msg = Messages.getString("ReportActionServlet.Unexpected_ErrMsg_Part1")  //$NON-NLS-1$
						+ searchString +Messages.getString("ReportActionServlet.Unexpected_ErrMsg_Part2") +type +Messages.getString("ReportActionServlet.Unexpected_ErrMsg_Part3") +e; //$NON-NLS-1$ //$NON-NLS-2$
					APIQueryConsole.getInstance().println(msg, APIQueryConsole.MSG_ERROR);
				}

			}
		};		
		Display.getDefault().asyncExec(run);
	}	

}
