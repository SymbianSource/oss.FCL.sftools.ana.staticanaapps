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
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.export.IsUsedByXMLReport;
import com.nokia.s60tools.appdep.export.XMLUtils;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.listview.ListView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.ui.S60ToolsViewerSorter;
import com.nokia.s60tools.util.console.IConsolePrintUtility;


/**
 * Starts export report from list view. 
 */
public class ExportReportListViewAction extends Action {

	//
	// Members
	//
	private final ListView view;	
	private List<ComponentPropertiesData> isUsedByData;	
	private String exportHTMLFileName;
	private String exportXMLFileName;
	private String exportPath = ""; //$NON-NLS-1$
	private String exportFileSuffix = Messages.getString("ExportReportListViewAction.IsUsedByReportPrefix"); //$NON-NLS-1$
	private String completedMessage;	
	
	/**
	 * Constructor.
	 * @param view Reference to the view.
	 */
	public ExportReportListViewAction(ListView view){
		//super(view);
		this.view = view;
		
		setText(Messages.getString("ExportReportListViewAction.ExportReport_Action_Text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ExportReportListViewAction.ExportReport_Action_Tooltip")); //$NON-NLS-1$
		
		setId("com.nokia.s60tools.appdep.ui.actions.ExportReportMainViewAction"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {

		// Parent shell
		Shell sh = view.getViewSite().getShell();
		
		try {
			if(isUsedByData == null || isUsedByData.isEmpty()){
				completedMessage = Messages.getString("ExportReportListViewAction.Nothing_To_Export_Msg"); //$NON-NLS-1$
				showCompletedMessage();
				return;
			}
			
			String componentName = view.getComponentName();
			String functionName = view.getFunctionName();		
			String componentOrFunctionNameString = null;
			String functionNameForFile = null;
			
			if(functionName != null){
				// Making sure that function name does not contain characters illegal for creating new files
				functionNameForFile = removeIllegalFileCharactersFromFunctionName(functionName);
				
				if(functionNameForFile.contains("@")){  //$NON-NLS-1$
					// If using format component@ordinal, then there is no need to replicate component name
					setExportHTMLFileName( functionNameForFile + exportFileSuffix +  ".html"); //$NON-NLS-1$
					setExportXMLFileName( functionNameForFile + exportFileSuffix + ".xml");			 //$NON-NLS-1$				
				}
				else{
					int paramListStart = functionNameForFile.indexOf('(');
					if(paramListStart != -1){
						functionNameForFile = functionNameForFile.substring(0, paramListStart);
					}
					// Otherwise using format component_function
					setExportHTMLFileName( componentName + "_" + functionNameForFile + exportFileSuffix +  ".html"); //$NON-NLS-1$ //$NON-NLS-2$
					setExportXMLFileName( componentName + "_" + functionNameForFile + exportFileSuffix + ".xml");			 //$NON-NLS-1$ //$NON-NLS-2$				
				}
				componentOrFunctionNameString = componentName + " [" + Messages.getString("ExportReportListViewAction.Str_Function") + "=" + functionName + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else{
				setExportHTMLFileName( componentName + exportFileSuffix +  ".html"); //$NON-NLS-1$
				setExportXMLFileName( componentName + exportFileSuffix + ".xml");			 //$NON-NLS-1$			
				componentOrFunctionNameString = componentName;
			}

			FileDialog fdia = new FileDialog(sh, SWT.SAVE);
			String path = AppDepSettings.getActiveSettings().getExportPrintReportPath();
			if(path == null){
				path ="";// AppDepSettings.getActiveSettings().getCacheBaseDir(); //$NON-NLS-1$
			}
			
			setExportPath(path);
			fdia.setFileName(getExportHTMLFileNameAndPath());

			String msg = Messages.getString("ExportReportListViewAction.ExportReport_Msg_Start"); //$NON-NLS-1$
			//If XML file is generated aswell, adding info for that to note
			if(AppDepSettings.getActiveSettings().isExportXMLreport()){
				msg 
					+= ". " + Messages.getString("ExportReportListViewAction.ExportReport_Msg_End"); //$NON-NLS-1$ //$NON-NLS-2$
			}		
			fdia.setText(msg);

			String fullPath = fdia.open();		
			
			//User select "Cancel"
			if(fullPath == null){
				return;
			}
			setExportPath(fdia.getFilterPath());
			setExportHTMLFileName(fdia.getFileName());
			//Setting default paht to most recently used export path
			AppDepSettings.getActiveSettings().setExportPrintReportPath(fdia.getFilterPath());
				
			exportReport(removeIllegalXMLCharactersFromStr(componentOrFunctionNameString));
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
	
		// Remember to always call AbstractMainViewAction
		// base class implementation
		super.run();
	}

	/**
	 * Replacing all possible characters that cannot be used in file names
	 * @param functionName Function name string to be replaced for illegal characters.
	 * @return Modified function name
	 */
	private String removeIllegalFileCharactersFromFunctionName(String functionName) {
		String localFunctionNameVar = new String(functionName);
		if(localFunctionNameVar != null){
			// Replacing all possible characters that cannot be used in file names
			localFunctionNameVar = localFunctionNameVar.replace("::", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("\\", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("/", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("*", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("?", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace(">", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localFunctionNameVar = localFunctionNameVar.replace("|", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return localFunctionNameVar;
	}

	/**
	 * Replacing all possible characters that cannot be used in XML.
	 * @param str Function name string to be replaced for illegal characters.
	 * @return Modified function name
	 */
	private String removeIllegalXMLCharactersFromStr(String str) {
		String localStr = new String(str);
		if(localStr != null){
			// Replacing all possible characters that cannot be used in file names
			localStr = localStr.replace("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localStr = localStr.replace(">", ""); //$NON-NLS-1$ //$NON-NLS-2$
			localStr = localStr.replace("&", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return localStr;
	}
	
	/**
	 * Exports the report.
	 * @param componentOrFunctionNameString Name of the component or component and function name combination
	 *                                      to be shown in the report.
	 */
	private void exportReport(String componentOrFunctionNameString) {
		
		try {
			AppDepSettings st = AppDepSettings.getActiveSettings();
			SdkInformation sdkInfo = st.getCurrentlyUsedSdk();
	
			TableViewer viewer = view.getComponentListViewer();
			
			//For exporting report sorted as it is in GUI, getting data
			//for new ArrayList one by one from TableViewer, witch
			ArrayList<ComponentPropertiesData> sortedData = new ArrayList<ComponentPropertiesData>(isUsedByData.size());

			for(int i =0; ; i++){
				ComponentPropertiesData data = (ComponentPropertiesData)viewer.getElementAt(i);
				if(data != null){
					sortedData.add(data);
				}
				else{
					break;					
				}
			}
			
			S60ToolsViewerSorter sorter = (S60ToolsViewerSorter) viewer.getSorter();
			int sort = sorter.getSortCriteria(); 									
			
			IsUsedByXMLReport report = new IsUsedByXMLReport(sortedData, 
					sdkInfo.getSdkId(), 
					st.getCurrentlyUsedTargetPlatformsAsString(),
					st.getBuildType().getBuildTypeDescription(), 
					componentOrFunctionNameString,
					sort);
			
			if(AppDepSettings.getActiveSettings().isExportXMLreport()){
				report.toFile(getExportXMLFileNameAndPath());
			}
	
			String xslPath = AppDepSettings.getActiveSettings().getResourcesPath()
				+ System.getProperty("file.separator") //$NON-NLS-1$
				+ AppDepSettings.getActiveSettings().getIsUsedByXSLFileName();			
		
			XMLUtils.parseXML(report.toString(), xslPath, getExportHTMLFileNameAndPath());
			
			completedMessage = 
				Messages.getString("ExportReportListViewAction.HTMLReport_Exported_Msg") + ": " +getExportHTMLFileNameAndPath(); //$NON-NLS-1$ //$NON-NLS-2$
			//If XML file is generated aswell, adding info for that to note
			if(AppDepSettings.getActiveSettings().isExportXMLreport()){
				completedMessage 
					+= "\n" + Messages.getString("ExportReportListViewAction.XMLReport_Exported_Msg") + ": "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+getExportXMLFileNameAndPath();
			}
			showCompletedMessage();					
			
		} catch (Exception e) {			
			e.printStackTrace();
			completedMessage = Messages.getString("ExportReportListViewAction.Export_Not_Complete_Msg")  //$NON-NLS-1$
				                + " "  //$NON-NLS-1$
				                + Messages.getString("ExportReportListViewAction.Error_Was_Msg")  //$NON-NLS-1$
				                + ": " + e.getMessage(); //$NON-NLS-1$
			showCompletedMessage();
			AppDepConsole.getInstance().println(Messages.getString("ExportReportListViewAction.Export_Failed_Msg")  //$NON-NLS-1$
					                            + " "  //$NON-NLS-1$
					                            + Messages.getString("ExportReportListViewAction.Error_Was_Msg")  //$NON-NLS-1$
					                            + ": " + e.getMessage(),  //$NON-NLS-1$
                    IConsolePrintUtility.MSG_ERROR);			
		}
	}

	
	/**
	 * Gets file path name for exported HTML file.
	 * @return file path name for exported HTML file.
	 */
	public String getExportHTMLFileNameAndPath() {
		
		String prefix = ("".equals(exportPath)) ? "" : exportPath  //$NON-NLS-1$ //$NON-NLS-2$
				+ System.getProperty("file.separator");  //$NON-NLS-1$
		
		return prefix +exportHTMLFileName;
	}

	/**
	 * Sets file path name for exported HTML file.
	 * @param name file path name for exported HTML file
	 */
	private void setExportHTMLFileName(String name) {
		this.exportHTMLFileName = name;
	}

	/**
	 * Sets destination directory for export.
	 * @param exportPath destination directory for export.
	 */
	private void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	/**
	 * Gets file path name for exported XML file.
	 * @param name file path name for exported HTML file
	 */
	public String getExportXMLFileNameAndPath() {
		return exportPath 
			+ System.getProperty("file.separator")  //$NON-NLS-1$
			+ exportXMLFileName;
	}

	/**
	 * Sets file path name for exported XML file.
	 * @param name file path name for exported HTML file
	 */
	private void setExportXMLFileName(String name) {
		this.exportXMLFileName = name;
	}

	/**
	 * Shows export completed message.
	 */
	private void showCompletedMessage() {
		Shell sh = view.getViewSite().getShell();
		AppDepMessageBox msgBox = new AppDepMessageBox(sh, completedMessage, SWT.OK | SWT.ICON_INFORMATION);
		msgBox.open();
	}

	/**
	 * @return Returns the isUsedByData.
	 */
	public List<ComponentPropertiesData> getIsUsedByData() {
		return isUsedByData;
	}

	/**
	 * @param isUsedByData The isUsedByData to set.
	 */
	public void setIsUsedByData(List<ComponentPropertiesData> isUsedByData) {
		this.isUsedByData = isUsedByData;
	}

}
