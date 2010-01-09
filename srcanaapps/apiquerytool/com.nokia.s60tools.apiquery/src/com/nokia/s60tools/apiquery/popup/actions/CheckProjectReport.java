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
 
package com.nokia.s60tools.apiquery.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.nokia.s60tools.apiquery.servlets.APIQueryWebServerConfigurator;
import com.nokia.s60tools.apiquery.servlets.ReportActionConstants;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.common.ProductInfoRegistry;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.resource.FileUtils;

/**
 * Class representing Actice Project API Query report.
 * Initialize by constructor and ask for HTML or CSV -report.
 *
 */
public class CheckProjectReport {

	/**
	 * &nbsp;
	 */
	private static final String HTML_WHITE_SPACE = "&nbsp;"; //$NON-NLS-1$

	/**
	 * _
	 */
	public static final String UNKNOWN_API_NAME_SEPARATOR = "_"; //$NON-NLS-1$

	/**
	 * Unknown
	 */
	public static final String UNKNOWN_API_NAME = Messages.getString("CheckProjectReport.Unknown_MSg"); //$NON-NLS-1$
	
	/**
	 * API name, Headers which is used from that API
	 */
	private final SortedMap<String, Vector<String>> usedHeaders;
	
	/**
	 * API name, Files which is using that API
	 */
	private final Hashtable<String, Vector<IFile>> usingFiles;

	/**
	 * <API Name, API Details> APIs which project is using
	 */
	private final Hashtable<String, APIDetails> projectUsingAPIDetails;
	
	/**
	 * Fields to be added to report from APIDetails besides API Name, Used Headers and Using Files 
	 */
	String [] apiDetailsToReport  = null;

	private final IProject selectedProject;
	


	/**
	 * 
	 * @param usedHeaders <API name, Headers which is used from that API>
	 * @param usingFiles <API name, Files which is using that API>
	 * @param projectUsingAPIDetails <API Name, API Details> APIs which project is using 
	 * @throws IllegalArgumentException if usedHeaders and usingFiles is not same sized or argument is null.
	 */
	public CheckProjectReport(SortedMap<String, Vector<String>> usedHeaders, 
			Hashtable<String, Vector<IFile>> usingFiles,
			Hashtable<String, APIDetails> projectUsingAPIDetails, String [] apiDetailsToReport,
			IProject selectedProject) 
		throws IllegalArgumentException
	{
		if(usedHeaders == null || usingFiles == null || usedHeaders.size() != usingFiles.size()){
			throw new IllegalArgumentException(
					Messages.getString("CheckProjectReport.UsedHeader_ErrMsg")); //$NON-NLS-1$
		}
		this.usedHeaders = usedHeaders;
		this.usingFiles = usingFiles;
		this.projectUsingAPIDetails = projectUsingAPIDetails;
		this.apiDetailsToReport = apiDetailsToReport;
		this.selectedProject = selectedProject;
		
	}


	/**
	 * Generating report as HTML format
	 * @param title
	 * @return a report
	 */
	public String toHTML(String title) {
		StringBuffer html = new StringBuffer();		
		//Get API Names from used Headers keys
		Set <String> apis = usedHeaders.keySet();
		Vector<String> tmpVect;
		
		//Add HTML header to report
		html.append(getHTMLHeader(title));
		String note = Messages.getString("CheckProjectReport.NoteForReportLinks_Msg"); //$NON-NLS-1$
		html.append("<p>" + note //$NON-NLS-1$
				+ "</p>"); //$NON-NLS-1$
		
		html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"1\">"); //$NON-NLS-1$
		html.append("<tr><th>"); //$NON-NLS-1$
		html.append(Messages.getString("CheckProjectReport.APIName_Msg")); //$NON-NLS-1$
		html.append("</th>" ); //$NON-NLS-1$
		//Adding titles to API Details information
		for (int i = 0; i < apiDetailsToReport.length; i++) {
			html.append("<th>"); //$NON-NLS-1$
			html.append(apiDetailsToReport[i]);
			html.append("</th>"); //$NON-NLS-1$
		}
		
		html.append("<th>");//$NON-NLS-1$
		html.append(Messages.getString("CheckProjectReport.UsedHeader_Msg")); //$NON-NLS-1$
		html.append("</th><th>");//$NON-NLS-1$
		html.append(Messages.getString("CheckProjectReport.UsingFile_Msg")); //$NON-NLS-1$
		html.append("</th></tr>"); //$NON-NLS-1$
		html.append("\n"); //$NON-NLS-1$
		
		String [] detailsToReport ;//For API Details
		//Add all APIs used in project to report
		for (String api : apis) {
			html.append("<tr>"); //$NON-NLS-1$
			
			tmpVect = usedHeaders.get(api);
			html.append("<td>"); //$NON-NLS-1$
			//Adding API Name to html. If details for API could not be found, adding name this.UNKNOWN_API_NAME
			if(!api.startsWith(UNKNOWN_API_NAME + UNKNOWN_API_NAME_SEPARATOR)){
				
				//Creating link for launching action to run API Query by API Name
				html.append("<a href=\"" +getServletURL() +"?" +ReportActionConstants.PARAM_QUERY_FOR   //$NON-NLS-1$  //$NON-NLS-2$
							+"=" +api  //$NON-NLS-1$
							+"&" +ReportActionConstants.ACTION_QUERY_TYPE  //$NON-NLS-1$
							+"=" +APIQueryParameters.QUERY_BY_API_NAME +"\">" +api +"</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			//API Is unknown 
			else{
				html.append("<i>"); //$NON-NLS-1$
				html.append(UNKNOWN_API_NAME);
				html.append("</i>"); //$NON-NLS-1$
			}
			//Adding API Details to html
			detailsToReport = getApiDetails(api);
			for (int i = 0; i < detailsToReport.length; i++) {
				html.append("<td>"); //$NON-NLS-1$
				int searchMethodId = UserSettings.getInstance().getCurrentlySelectedSearchMethod().
					getQueryTypeByAPIDetailNameInDetails(apiDetailsToReport[i]);
				//If search method is supported
				if(searchMethodId != -1 
						&& UserSettings.getInstance().getCurrentlySelectedSearchMethod().isSupportedQueryType(searchMethodId)
						&& !detailsToReport[i].equals(HTML_WHITE_SPACE)){
					
					String comma = ","; //$NON-NLS-1$
					String [] items = detailsToReport[i].split(comma);
					for (int j = 0; j < items.length; j++) {
						
						//Creating link for launching action to run API Query by a type
						html.append("<a href=\"" +getServletURL() //$NON-NLS-1$
								+"?" +ReportActionConstants.PARAM_QUERY_FOR  //$NON-NLS-1$
								+"=" +items[j]  //$NON-NLS-1$
								+"&" +ReportActionConstants.ACTION_QUERY_TYPE  //$NON-NLS-1$
								+"=" +searchMethodId +"\">" +items[j] +"</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						if(j < (items.length-1)){
							html.append(comma);
						}
					}
					
				}
				//If current topic is not supported for API Query
				else{
					html.append(detailsToReport[i]);
				}
				html.append("</td>\n"); //$NON-NLS-1$
			}
			
			html.append("</td>\n<td>"); //$NON-NLS-1$
			//Adding "used headers" to html
			for (String header : tmpVect) {
				//Creating link for launching action to open file
				html.append("<a href=\"" +getServletURL() //$NON-NLS-1$
							+"?" +ReportActionConstants.ACTION_OPEN_USED_HEADER //$NON-NLS-1$
							+"=" +header +getProjectNameParam() +"\">"); //$NON-NLS-1$ //$NON-NLS-2$
				html.append(header);	
				html.append("</a>"); //$NON-NLS-1$
				html.append(" "); //$NON-NLS-1$
			}
			html.append("</td>\n<td>"); //$NON-NLS-1$
			Vector<IFile> usingFileTmpVect = usingFiles.get(api);
			
			Vector<IFile> allreadyAdded = new Vector<IFile>();
			//Adding "using files" to html
			for (IFile file : usingFileTmpVect) {
				//To prevent adding duplicates of same file
				if(!allreadyAdded.contains(file)){
					//Creating link for launching action to open file
					html.append("<a href=\"" +getServletURL()  //$NON-NLS-1$
							+"?" +ReportActionConstants.ACTION_OPEN_USING_FILE //$NON-NLS-1$
							+"=" +file.getProjectRelativePath().toOSString() +getProjectNameParam() +"\">"); //$NON-NLS-1$ //$NON-NLS-2$
					html.append(file.getName());	
					html.append("</a>"); //$NON-NLS-1$
					html.append(" "); //$NON-NLS-1$
					allreadyAdded.add(file);
				}
				
			}
			html.append("</td>\n</tr>\n"); //$NON-NLS-1$
		}
		
		html.append("</table>"); //$NON-NLS-1$
		html.append("\n"); //$NON-NLS-1$

		
		//Add HTML end tags to report
		html.append(getHTMLEndTags());
		return html.toString();		
	}
	
	private String getProjectNameParam(){
		return "&" +ReportActionConstants.PARAM_PROJECT_NAME +"=" +selectedProject.getName(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Get URL for actio Servlet
	 * @return http://[server]:[port]/[servlet]
	 */
	private String getServletURL(){
		return "http://" +APIQueryWebServerConfigurator.WEB_SERVER_HOST  //$NON-NLS-1$
			+":" +APIQueryWebServerConfigurator.WEB_SERVER_HTTP_PORT  //$NON-NLS-1$
			+"/" +ReportActionConstants.SERVLET_NAME; //$NON-NLS-1$
	}
	
	/**
	 * get API Details to report. Founding API_DETAILS_TO_REPORT keys from
	 * projectUsingAPIDetails by api.
	 * @param api
	 * @return
	 */
	private String[] getApiDetails(String api){
		String[] detailsToReport = new String [apiDetailsToReport.length];
		APIDetails details = projectUsingAPIDetails.get(api);
		if(details != null){
			APIDetailField field;
			for (int i = 0; i < apiDetailsToReport.length; i++) {
				field = details.getDetail(apiDetailsToReport[i]);
				//To fill up empty HTML table columns, adding an white space (&nbsp;) 
				//to detail where is no real data. This looks bettre in UI (HTML report)
				if(field.getValue() != null && !field.getValue().trim().equals("")){ //$NON-NLS-1$
					detailsToReport[i] = field.getValue();
				}
				//To avoid HTML table different behavior with empty slots, putting a white space to emtpy slots
				else{
					detailsToReport[i] = HTML_WHITE_SPACE;
				}
			}
		}
		else {
			//To avoid HTML table different behavior with empty slots, putting a white space to emtpy slots
			for (int i = 0; i < apiDetailsToReport.length; i++) {
				detailsToReport[i] = HTML_WHITE_SPACE;
			}
		}		
		return detailsToReport;
	}
	
	/**
	 * 
	 * Not implemented.
	 * 
	 * Generate report in CSV format.
	 * @return <code>null</code>
	 */
	public String toCSV() {
		//Not implemented
		return null;
	}	
	
	/**
	 * Writes this.toHTML() to file
	 * @param title
	 * @param fileName
	 */
	public void toHTMLFile(String title, String fileName ) {		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					fileName));
			out.write(toHTML(title));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * Not implemented until {@link #toCSV()} is implemented.
	 * 
	 * Writes this.toCSV() to file
	 * @param title
	 * @param fileName
	 */
	public void toCSVFile(String title, String fileName ) {		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					fileName));
			out.write(toCSV());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * generating html header and opening body and adding title to it
	 * @param title
	 * @return
	 */
	private StringBuffer getHTMLHeader(String title){
		StringBuffer b = new StringBuffer();
		b.append("<html><head><title>"); //$NON-NLS-1$
		b.append(title);
		b.append("</title>\n"); //$NON-NLS-1$
		b.append(getHTMLStyles());

		b.append("\n</head><body>\n"); //$NON-NLS-1$
		b.append("<h1>"); //$NON-NLS-1$
		b.append(title);
		b.append("</h1>\n"); //$NON-NLS-1$
		return b;
	}
	/**
	 * get body and html end tags
	 * @return
	 */
	private StringBuffer getHTMLEndTags(){
		StringBuffer b = new StringBuffer();
		b.append("</body></html>"); //$NON-NLS-1$
		return b;
	}	
	/**
	 * get HTML style sheet 
	 * @return styles
	 */
	private StringBuffer getHTMLStyles(){
		
		String cssPath = null;
		
		try {
			//Get location for stylesheet file
			cssPath = APIQueryPlugin.getDefault().getPluginInstallPath() +
				File.separatorChar + ProductInfoRegistry.getResourcesRelativePath() 
				+File.separatorChar + ProductInfoRegistry.getReportCSSFileName();
			//return contents of the stylesheet file
			return FileUtils.loadDataFromFile(cssPath);
		} catch (Exception e) {
			//FileNotFoundException or IOException may occur
			e.printStackTrace();
			APIQueryConsole.getInstance().println(
					Messages.getString("CheckProjectReport.UnableToLoadCSS_Part1_ErrMsg") +cssPath +Messages.getString("CheckProjectReport.UnableToLoadCSS_Part2_ErrMsg") +e, //$NON-NLS-1$ //$NON-NLS-2$
					APIQueryConsole.MSG_ERROR);
			//If there was an error, returning dummy (hard coded) styles
			return getDummyCSS();
		} 
		
	}

	/**
	 * If there is something wrong, hard coded styles can be used
	 * @return dummy styles
	 */
	private StringBuffer getDummyCSS() {
		StringBuffer b = new StringBuffer();
		b.append("<style type=\"text/css\">"); //$NON-NLS-1$
		b.append("body {background-color: white; font-family: Verdana; font-size: 10px; }"); //$NON-NLS-1$
		b.append("p { font-family=\"Verdana\"; font-size: 10px; }"); //$NON-NLS-1$
		b.append("td { font-family=\"Verdana\"; font-size: 10px; text-align: left; }");	 //$NON-NLS-1$
		b.append("th { font-family=\"Verdana\"; font-size: 10px; font-weight: bold; text-align: left; }"); //$NON-NLS-1$
		b.append("dt { font-family=\"Verdana\"; font-size: 10px; font-weight: normal text-align: left; }");			 //$NON-NLS-1$
		b.append("h1 { font-family=\"Verdana\"; font-size: 18px; }");  	 //$NON-NLS-1$
		b.append("</style>"); //$NON-NLS-1$
		return b;
	}	

}
