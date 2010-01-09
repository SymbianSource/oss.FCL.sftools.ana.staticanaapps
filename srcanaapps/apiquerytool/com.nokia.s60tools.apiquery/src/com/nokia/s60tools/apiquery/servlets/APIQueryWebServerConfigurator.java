/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Class for handling API Query Web server functionalities. E.g. start and stop Web Server.
 */
public class APIQueryWebServerConfigurator {

	/**
	 * Host where report server is running (localhost)
	 */
	public static final String WEB_SERVER_HOST = "localhost"; //$NON-NLS-1$
	
	/**
	 * HTTP port to be used with running HTTP server
	 */
	public static final int WEB_SERVER_HTTP_PORT = 4191;
			
	/**
	 * Name of the API Query Web Server
	 */
	private static final String API_QUERY_WEB_SERVER_NAME = "API Query Server"; //$NON-NLS-1$
	
	/**
	 * 
	 */
public static final int Active_Project_Start  = 0;

public static final int Carbide_Instance_start  =1;

static boolean activeProjectStartedServer = false;


	/**
	 * Starts API Query Web Server. Web Server is used for launch actions in HTML report. 
	 * Reports are created with Export report functionality (API Query for Active project).
	 * @see JettyConfigurator#startServer(String, java.util.Dictionary)
	 */
public static void startServer(int msgdialogueCode) {
		try {
           
		if (msgdialogueCode == Active_Project_Start) activeProjectStartedServer = true;
			// Check if server is already running
			if (allreadyRunning()) {
				//Show the dialogue box
				
				if (msgdialogueCode==Carbide_Instance_start && APIQueryPlugin.isFirstLaunch&&!activeProjectStartedServer)
				{
					APIQueryPlugin.isFirstLaunch = false;					
				MessageDialog.openInformation(APIQueryPlugin.getCurrentlyActiveWbWindowShell(), "APIQuery Plugin: Active Project Option Information", "Active project report links may not work. Close other Carbide.C++ instances and restart API Query plug-in.");
				}
				return;
			}

			Hashtable<String, Object> set = new Hashtable<String, Object>();
			set.put(JettyConstants.HTTP_PORT, WEB_SERVER_HTTP_PORT);
			JettyConfigurator.startServer(API_QUERY_WEB_SERVER_NAME, set);

			Bundle bundle = Platform
					.getBundle("org.eclipse.equinox.http.registry"); //$NON-NLS-1$

			if (bundle.getState() == Bundle.RESOLVED) {
				bundle.start(Bundle.START_TRANSIENT);
			}
			APIQueryConsole
					.getInstance()
					.println(
							Messages
									.getString("APIQueryWebServerConfigurator.StartServer_Msg_Part1")//$NON-NLS-1$ 
									+ API_QUERY_WEB_SERVER_NAME
									+ Messages
											.getString("APIQueryWebServerConfigurator.StartServer_Msg_Part2")//$NON-NLS-1$ 
									+ "http://" + WEB_SERVER_HOST + ":" + WEB_SERVER_HTTP_PORT //$NON-NLS-1$ //$NON-NLS-2$ 
									+ Messages
											.getString("APIQueryWebServerConfigurator.StartServer_Msg_Part3")); //$NON-NLS-1$

		} catch (Exception e) {
			e.printStackTrace();
			APIQueryConsole
					.getInstance()
					.println(
							Messages
									.getString("APIQueryWebServerConfigurator.StartServer_ErrMsg_Part1") + API_QUERY_WEB_SERVER_NAME + Messages.getString("APIQueryWebServerConfigurator.StartServer_ErrMsg_Part2") + e, APIQueryConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$

		}
	}	
	
	
	
	/**
	 * Check if HTTP Server is already running
	 * @return <code>true</code> if server is alreaydy running and <code>false</code> if not.
	 */
	private static boolean allreadyRunning() {
		
		
		try {
			//Servlet URL 
			String servletURL = "http://" +WEB_SERVER_HOST  +":" +WEB_SERVER_HTTP_PORT + "/" + ReportActionConstants.SERVLET_NAME;
			URL url = new URL(servletURL);
			//Try to keep http connection alive
			System.setProperty("http.keepAlive", "true");
		
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-----> Reading URL: " + url.toString());
			//Getting connection to check if server is running
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			} 
		
		} catch (IOException e) {
			// IOExeption occurs if HttpURLConnection is not alive 
			//and when HttpURLConnection.getResponseCode() is called
			//then we know that HTTP server is not up and running			
		}
		catch (Exception e) {
			//No operation if something else happens, just returning false
		}		
		
		return false;
	}

	/**
	 * Stops API Query Web Server.
	 * @see JettyConfigurator#stopServer(String)
	 */
	public static void stopServer(){
		try {
			JettyConfigurator.stopServer(API_QUERY_WEB_SERVER_NAME);
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryWebServerConfigurator.StoptServer_Msg_Part1") +API_QUERY_WEB_SERVER_NAME +Messages.getString("APIQueryWebServerConfigurator.StoptServer_Msg_Part2")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryWebServerConfigurator.StoptServer_ErrMsg_Part1") +API_QUERY_WEB_SERVER_NAME +Messages.getString("APIQueryWebServerConfigurator.StoptServer_ErrMsg_Part2") +e, APIQueryConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
}
