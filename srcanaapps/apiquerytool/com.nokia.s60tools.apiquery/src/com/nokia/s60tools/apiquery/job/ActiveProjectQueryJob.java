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
 
package com.nokia.s60tools.apiquery.job;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.nokia.s60tools.apiquery.popup.actions.CheckProjectReport;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescriptionSearchResults;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.job.AbstractJob;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.util.SourceCodeParsingUtilities;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This class implements the Job for Active project query. 
 *
 */
public class ActiveProjectQueryJob extends AbstractJob {

	private static final String HEADERS_SEPARATOR = ","; //$NON-NLS-1$
	
	//
	//Researched values for indicating progress persentages in different steps
	//
	private static final int PROGRESS_STEP_1_PERCENTAGE = 5;
	private static final int PROGRESS_STEP_2_PERCENTAGE = 10;
	private static final int PROGRESS_STEP_3_PERCENTAGE = 70;	
	private static final int PROGRESS_STEP_4_PERCENTAGE = 95;
	
	/**
	 * File types count in for project search
	 */
	public static final String [] FILE_TYPES={"c", "cpp", "h", "hpp", "inl"};//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	/**
	 * File types count in for project search
	 */	
	private Vector<String> fileTypes;
	/**
	 * File types count in for #include statements
	 */
	public static final String [] INCLUDE_TYPES={"h", "hpp"};	//$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * File types count in for #include statements
	 */
	private Vector<String> includeTypes;
	/**
	 * Ingnoring folders named for project search
	 */
	public static final String [] IGNORE_FOLDERS={"tsrc", "internal"};	//$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * Ingnoring folders named for project search
	 */
	private Vector<String> ignoreFolders;
	
	/**
	 * 
	 * HashMap containing information of used headers in this project
	 * as <code>String (header name)</code> and <code>Vector (files including that header)</code> 
	 * containig information of what files is using that header ()
	 */	
	private HashMap<String, Vector<IFile>>headersUsedInFiles;
	
	/**
	 * filenames in project in lover case 
	 */
	private Vector<String> projectFileNames;
	

	/**
	 * Collection to store returned API names and headers in those APIs
	 * <code>header name, API name</code>
	 */
	Hashtable <String, String> headerBelongsToAPI ;	
	
	/**
	 * Path where exported file will be located
	 */
	private IPath exportFilePath;	
	
	/**
	 * Exported file
	 */
	private IFile generatedReportFile; 

	
	/**
	 * Project for making the query for. 
	 */
	private final IProject selectedProject;
	
	/**
	 * Semi-colon separated headers found in project files, search string
	 */
	private String searchString;

	/**
	 * 
	 * @param name Jobs name
	 * @param selectedProject Project for making the query for. 
	 * @param exportFilePath Path where exported file will be located
	 */
	public ActiveProjectQueryJob(String name, IProject selectedProject, IPath exportFilePath) {
		super(name);
		this.selectedProject = selectedProject;
		this.exportFilePath = exportFilePath;
		
		//Init lists  
		init();		
	}

	/**
	 * Initing headersUsedInFiles, projectFileNames, headerBelongsToAPI,
	 * fileTypes, includeTypes and ignoreFolders
	 *
	 */
	private void init() {
		
		headersUsedInFiles = new HashMap<String, Vector<IFile>>();
		projectFileNames = new Vector<String>();				
		headerBelongsToAPI = new Hashtable<String, String>();
		
		fileTypes = new Vector<String>(FILE_TYPES.length);		
		for (int i = 0; i < FILE_TYPES.length; i++) {
			fileTypes.add(FILE_TYPES[i]);
		}
		
		includeTypes = new Vector<String>(INCLUDE_TYPES.length);		
		for (int i = 0; i < INCLUDE_TYPES.length; i++) {
			includeTypes.add(INCLUDE_TYPES[i]);
		}
		
		ignoreFolders = new Vector<String>(IGNORE_FOLDERS.length);		
		for (int i = 0; i < IGNORE_FOLDERS.length; i++) {
			ignoreFolders.add(IGNORE_FOLDERS[i]);
		}		
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {

		IStatus status;

		setMonitor(monitor);
		ActiveProjectQueryJobManager.getInstance().registerJob(this);
		APIQueryConsole.getInstance().println(Messages.getString("ActiveProjectQueryJob.StartingToRun_Msg") + super.getName(),  //$NON-NLS-1$
				IConsolePrintUtility.MSG_NORMAL);		

		try {
			
			//Start to do actual job
			status = doQuery();		

		} catch (Exception e) {
			e.printStackTrace();
			status = new Status(IStatus.ERROR, this.getName(), IStatus.ERROR,
					Messages.getString("ActiveProjectQueryJob.ErrorsOnJob_ErrMsg") + this.getName(), e); //$NON-NLS-1$
		}

		return status;

	}
	
	
	/**
	 * Doing actual API Query
	 * @return status how we succeeded
	 */
	private IStatus doQuery() {
		
//		 disable datasouce selection
		MainView.enablePropTabcontents(false);
		
		getMonitor().beginTask(Messages.getString("ActiveProjectQueryJob.TaskStarted_Msg") , steps);		 //$NON-NLS-1$
		APIQueryConsole.getInstance().println(Messages.getString("ActiveProjectQueryJob.APIQueryForProject_Msg") +getProjectName() +Messages.getString("ActiveProjectQueryJob.APIQueryForProject_OnProgress_Part2_Msg"), IConsolePrintUtility.MSG_NORMAL);		 //$NON-NLS-1$ //$NON-NLS-2$
		
		IStatus status = null;

		try {
			// Get files from p
			IFile [] files = getProjectFiles();	
			
			progress(PROGRESS_STEP_1_PERCENTAGE, Messages.getString("ActiveProjectQueryJob.Progresbar_Step1_Msg")); //$NON-NLS-1$
			foundHeadersFromFiles(files);
			
			//Generate search string (header1.h;header2.h;...)
			searchString = generateSearchString();


			progress(PROGRESS_STEP_2_PERCENTAGE, Messages.getString("ActiveProjectQueryJob.Progresbar_Step2_Msg")); //$NON-NLS-1$

			
			APIShortDescriptionSearchResults projectUsingAPIs = getAPIShortDescriptions(APIQueryParameters.QUERY_BY_HEADER_NAME);			
			
			// Did we succeed?
			if(projectUsingAPIs.getSearchResults() != null){

				// Something failed, e.g. no servers configured.				
				if(projectUsingAPIs.hasErrors()){
					String errMsg = Messages.getString("ActiveProjectQueryJob.APIQueryFailed_ErrMsg") +"\n" + projectUsingAPIs.getErrorMessages(); //$NON-NLS-1$ //$NON-NLS-2$
					throw new QueryOperationFailedException(errMsg);
				}								
				
				progress(PROGRESS_STEP_3_PERCENTAGE, Messages.getString("ActiveProjectQueryJob.Progresbar_Step3_Msg"));					 //$NON-NLS-1$
				
				//Getting details for all used APIs
				/**
				 * Collection for APIDetails used in selected project. API Name as key.
				 */
				Hashtable<String, APIDetails> projectUsingAPIDetails = getAPIDetails(projectUsingAPIs.getSearchResults());						

				progress(PROGRESS_STEP_4_PERCENTAGE, Messages.getString("ActiveProjectQueryJob.Progresbar_Step4_Msg")); //$NON-NLS-1$
				
				//Get detailed information of apis
				findHeadersFromAPIs(projectUsingAPIDetails);			
				String report = generateReport(projectUsingAPIDetails);
				generatedReportFile = doSave(report);
					
				status = Status.OK_STATUS;
			}		
			else{
				status = new Status(IStatus.ERROR, this.getName(), IStatus.ERROR,
						Messages.getString("ActiveProjectQueryJob.ErrorsOnJob_ErrMsg") + Messages.getString("ActiveProjectQueryJob.CouldNotGetAPISUmmaries_ErrMsg"), null); //$NON-NLS-1$ //$NON-NLS-2$
			}				
			
			progress(PROGRESS_COMPLETED_PERCENTAGE,Messages.getString("ActiveProjectQueryJob.Progresbar_Done_Msg")); //$NON-NLS-1$
			APIQueryConsole.getInstance().println(Messages.getString("ActiveProjectQueryJob.APIQueryForProject_Msg") +getProjectName() +Messages.getString("ActiveProjectQueryJob.APIQueryForProject_Compeleated_Msg"), IConsolePrintUtility.MSG_NORMAL);        	 //$NON-NLS-1$ //$NON-NLS-2$
        	
			//If we get some results, but there was also some errors
			if(projectUsingAPIs.hasErrors()){
				status = new Status(IStatus.WARNING, this.getName(), IStatus.ERROR,
						projectUsingAPIs.getErrorMessages(), null);				
			}
        	
		} catch (JobCancelledByUserException e) {
			status = new Status(IStatus.CANCEL, this.getName(), IStatus.CANCEL,
					e.getMessage(), e);

		} catch (CoreException e) {
			status = new Status(IStatus.ERROR, this.getName(), IStatus.ERROR,
					Messages.getString("ActiveProjectQueryJob.ErrorsOnJob_ErrMsg") + e.getMessage(), e); //$NON-NLS-1$
			e.printStackTrace();
		} catch (IOException e) {
			status = new Status(IStatus.ERROR, this.getName(), IStatus.ERROR,
					Messages.getString("ActiveProjectQueryJob.ErrorsOnJob_ErrMsg") + e.getMessage(), e); //$NON-NLS-1$
			e.printStackTrace();
		}
		 catch (Exception e) {
			status = new Status(IStatus.ERROR, this.getName(), IStatus.ERROR,
					Messages.getString("ActiveProjectQueryJob.ErrorsOnJob_ErrMsg") + e.getMessage(), e); //$NON-NLS-1$
			e.printStackTrace();
		}
		 finally{
			 getMonitor().done();
				// enable data source selection
				MainView.enablePropTabcontents(true);
		 }
		 
		 return status;
		 
	}

	/**
	 * Searching headers from project files, using this.handleOneFile for each file
	 * @param files
	 * @throws JobCancelledByUserException
	 * @throws CoreException
	 * @throws IOException
	 */
	private void foundHeadersFromFiles(IFile[] files) throws JobCancelledByUserException, CoreException, IOException {

		for (int i = 0; i < files.length; i++) {
			IFile file = files[i];			
			InputStream in = file.getContents();
			String [] fileIncludeLines = getFileIncludeLines(in);
			in.close();
			
			handleOneFile(fileIncludeLines, file);
			
		}

	}

	/**
	 * Gets search String, semi colon (;) separated list of
	 * includes found in project 
	 * @return
	 */
	private String generateSearchString() {
		StringBuffer search = new StringBuffer();
		String incl;
		for (Iterator<String> it = headersUsedInFiles.keySet().iterator(); it.hasNext();) {
			incl = it.next().trim();
			search.append(incl);
			search.append(APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR);
		}
		return search.toString();
	}
	
	/**
	 * Found all includes from file. Using this.addOneHeaderToIncludes to set includes.
	 * @param fileIncludeLines
	 */
	private void handleOneFile(String [] fileIncludeLines, IFile file) {		
		String line;		
		for (int i = 0; i < fileIncludeLines.length; i++) {
			line = fileIncludeLines[i];
			//If it's a system include <>
			if(line != null && line.trim().length() > 0){				
				String include = SourceCodeParsingUtilities.parseIncludeFromLine(line);
				if(include != null){
					addOneHeaderToIncludes(include, file);
				}

			}
		}	
	}

	/**
	 * Adds one header to this.includes 
	 * if include filetype matches INCLUDE_TYPES (this.includeTypes)
	 * And header is not one of project files (this.projectFiles) 
	 * @param header
	 * @param fileName
	 */
	private void addOneHeaderToIncludes(String header, IFile file) {
		//if user using wrong way SYSTEMINCLUDE and USERINCLUDE 
		//a system header can be really a user include and vice versa
		if(includeTypes.contains(
				header.substring(header.indexOf(".") +1))//$NON-NLS-1$
				&& !projectFileNames.contains(header.toLowerCase()))
		{	
			//If a header is already found and added to includes
			//get Vector where is files using this header and add
			//this file to that file list
			if(headersUsedInFiles.containsKey(header)){
				Vector<IFile> usedInFiles = headersUsedInFiles.get(header);
				usedInFiles.add(file);
				headersUsedInFiles.put(header, usedInFiles);
			}
			//if this was first time founding this header, creating new
			//Vector and add current file to it and add this set to includes
			else{
				Vector<IFile> usedInFiles = new Vector<IFile>();
				usedInFiles.add(file);
				headersUsedInFiles.put(header, usedInFiles);
			}			
		}
	}
	

	

	/**
	 * Found all files (file type is one of this.FILE_TYPES)
	 * from project where this.selectedFile belongs
	 * @return
	 * @throws CoreException
	 */
	private IFile[] getProjectFiles() throws CoreException{
		
		Vector<IResource> projectFiles = new Vector<IResource>();	
		IResource [] resources = selectedProject.members();
		//Seek the project and found all files
		searchFiles(projectFiles, resources);
		IFile [] files = projectFiles.toArray(new IFile[0]);			
		return files;
		
	}

	/**
	 * Search recursively all files from resource. If a resource is
	 * a IFolder calls recursively it self to found all files under that 
	 * folder. Ignoring this.IGNORE_FOLDERS (tsrc and internal) folders.
	 * 
	 * Adds all files to Vector this.projectFiles if file type found from 
	 * this.FILE_TYPES
	 * 
	 * Saves fileNames to this.projectFileNames
	 * 
	 * @param resources
	 * @throws CoreException
	 */
	private void searchFiles(Vector<IResource> v, IResource[] resources) throws CoreException {

		for (int i = 0; i < resources.length; i++) {
			if(resources[i] instanceof IFile){
				IFile file = (IFile)resources[i];
				if( fileTypes.contains( file.getFileExtension() ) ){
					v.add(file);
					projectFileNames.add(file.getName().toLowerCase());
				}
			}
			else if(resources[i] instanceof IFolder){
				IFolder folder = (IFolder)resources[i];
				//Ignorin tsrc and internal folders, so headers from those
				//folders is not included to search
				if(!ignoreFolders.contains(folder.getName().toLowerCase())){
					searchFiles(v, folder.members());
				}
			}						
		}
	}
	
	/**
	 * Get lines from stream containing #include in the line,
	 * ingnoring all other lines.
	 * @param is
	 * @return String[] of lines, each of them contains #include 
	 * @throws CoreException
	 * @throws IOException
	 */
	private String [] getFileIncludeLines(InputStream is) throws CoreException,
			IOException {

		Vector<String> lines = new Vector<String>();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		String include = "#include";//$NON-NLS-1$
		while ((line = br.readLine()) != null) {
			if(line.contains(include)){
				lines.add(line);
			}
		}
		// Closing buffers
		br.close();
		isr.close();
		return lines.toArray(new String[0]);
	}

	public String getSearchString() {
		return this.searchString;
	}


	/**
	 * 
	 * HashMap containing information of used headers in this project
	 * as <code>String</code> and <code>Vector</code> containig information
	 * of what files is using that header ()
	 * 
	 * @return HashMap<String, Vector<String>> where <code>String</code> is header name 
	 * and <code>Vector</code> contains file names what includes that header
	 */

	public HashMap<String, Vector<IFile>> getHeadersUsedInFiles() {
		return headersUsedInFiles;
	}	
	
	/**
	 * Starts query for the current identifier selection with 
	 * the given query type.
	 * @param queryType
	 * @throws JobCancelledByUserException 
	 * @throws CoreException 
	 * @throws IOException 
	 */
	private APIShortDescriptionSearchResults getAPIShortDescriptions(final int queryType) throws JobCancelledByUserException, CoreException, IOException{
	
		String msg = Messages.getString("ActiveProjectQueryJob.Starting_Part1_Msg") //$NON-NLS-1$
			+ APIQueryParameters.getDescriptionForQueryType(queryType)
			+ Messages.getString("ActiveProjectQueryJob.Starting_Part2_Msg")  //$NON-NLS-1$
			+ selectedProject.getName()+ Messages.getString("ActiveProjectQueryJob.Starting_Part3_Msg") //$NON-NLS-1$
			+this.searchString + "'.";//$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_CLASS, msg);
		APIQueryConsole.getInstance().println(msg, 
				IConsolePrintUtility.MSG_NORMAL);	
		
		final APIQueryParameters params = new APIQueryParameters(queryType, searchString);
		params.setQueryFromUI(false);

		ISearchMethodExtension currSelExt = UserSettings.getInstance().getCurrentlySelectedSearchMethod();

		APIShortDescriptionSearchResults projectUsingAPIs = currSelExt.runAPIQuery(params);
		return projectUsingAPIs;	
		
	}


	/**
	 * put headername - API name pairs to this.headerBelongsToAPI
	 * @param projectUsingAPIDetails Find headers from all APIs from table given
	 * @throws JobCancelledByUserException
	 */
	private void findHeadersFromAPIs(Hashtable<String, APIDetails> projectUsingAPIDetails) throws JobCancelledByUserException {
		String headersStr;
		String [] headersTbl;
		String headerName;

		Vector<String> headers;	
		
		Set<String> keys = projectUsingAPIDetails.keySet();
		//Looping all received API Summarys				

		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String api = (String) iter.next();
			if(api == null){
				continue;
			}
			APIDetails details = projectUsingAPIDetails.get(api);
				

			if(details != null){				
				
				//Splitting headers string eg. "header1.h, header2.h" to table {"header1.h", header2.h}
				//And putting those values to Vector
				headersStr = details.getDetail(XMLUtils.DESCRIPTION_HEADERS).getValue();
				headersTbl = headersStr.split(HEADERS_SEPARATOR);
				headers = new Vector<String>(headersTbl.length);
				for (int i = 0; i < headersTbl.length; i++) {
					headerName = headersTbl[i].trim();
					headers.add(headerName);
					//Putting "header1.h", API Name" to used header and APIs, same API Name will be several time in map						
					headerBelongsToAPI.put(headerName.toLowerCase(), api);
				}
				//Putting API used with all headers API contais to table

			}
			//When error occurs (there is no Details for API) just printing to console that there was no details
			//This should not be happend so often (if never), but if occurs from time to time, checkin implementation
			//and/or adding more error situation handling might needed.
			else{
				DbgUtility.println(DbgUtility.PRIORITY_CLASS, "Can't found or parse details for API: " +api );//$NON-NLS-1$
				APIQueryConsole.getInstance().println(Messages.getString("ActiveProjectQueryJob.NoDetailsForAPI_Msg") +api,  //$NON-NLS-1$
						IConsolePrintUtility.MSG_NORMAL);		
				
			}			
		}
	}
	
	/**
	 * Getting API Details from selected search method
	 * @param apis
	 * @return
	 * @throws QueryOperationFailedException 
	 */
	private Hashtable<String, APIDetails> getAPIDetails(Collection<APIShortDescription> apis) throws QueryOperationFailedException {
		ISearchMethodExtension currSelExt = UserSettings.getInstance().getCurrentlySelectedSearchMethod();
		Hashtable<String, APIDetails> details = currSelExt.getAPIDetails(apis);
		return details;
	}


	/**
	 * Putting pieces together. Collecting needed data from headersUsedInFiles and 
	 * headerBelongsToAPI to send for CheckProjectReport
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	private String generateReport(Hashtable<String, APIDetails> projectUsingAPIDetails) throws CoreException, IOException{

		
		//API Name and headers what is used for that API in searched project
		SortedMap<String, Vector<String>>usedHeaders = new TreeMap<String, Vector<String>>();
		//API Name and files in searched project that is using that API		
		Hashtable<String, Vector<IFile>>usingFiles = new Hashtable<String, Vector<IFile>>();		
		
		
		Set<String> headersUsedInProject = headersUsedInFiles.keySet();
		Vector<String> tmpUsedHeaders;
		Vector<IFile> tmpUsingFiles;
		String tmpAPIName;
		int unknownAPIIndex = 0;
		
		
		for (String header : headersUsedInProject) {
			//API where this header belongs, was found with selected search method
			if(headerBelongsToAPI.containsKey(header.toLowerCase())){
				tmpAPIName = headerBelongsToAPI.get(header.toLowerCase());
				//If current API is allready added to usedHeaders, just adding one 
				//header that is using that API to collection
				if(usedHeaders.containsKey(tmpAPIName)){
					
					tmpUsedHeaders = usedHeaders.get(tmpAPIName);
					if(!tmpUsedHeaders.contains(header.toLowerCase())){
						tmpUsedHeaders.add(header.toLowerCase());
					}
					usedHeaders.put(tmpAPIName, tmpUsedHeaders);

					tmpUsingFiles = usingFiles.get(tmpAPIName);
					tmpUsingFiles.addAll(headersUsedInFiles.get(header));
					usingFiles.put(tmpAPIName, tmpUsingFiles);					
				}
				//Else this is first time for this API, so creating new Vectors to put in maps
				else{					
					tmpUsedHeaders = new Vector<String>();
					if(!tmpUsedHeaders.contains(header.toLowerCase())){
						tmpUsedHeaders.add(header.toLowerCase());
					}
					usedHeaders.put(tmpAPIName, tmpUsedHeaders);

					tmpUsingFiles = new Vector<IFile>();
					tmpUsingFiles.addAll(headersUsedInFiles.get(header));
					usingFiles.put(tmpAPIName, tmpUsingFiles);					
				}				
			}
			//Else API for this header cannot be found, setting it as "Unknown" API
			else{
				tmpAPIName = CheckProjectReport.UNKNOWN_API_NAME 
					+CheckProjectReport.UNKNOWN_API_NAME_SEPARATOR 
					+unknownAPIIndex;
				unknownAPIIndex ++;
				tmpUsedHeaders = new Vector<String>();
				tmpUsedHeaders.add(header);
				usedHeaders.put(tmpAPIName, tmpUsedHeaders);

				tmpUsingFiles = new Vector<IFile>();
				tmpUsingFiles.addAll(headersUsedInFiles.get(header));
				usingFiles.put(tmpAPIName, tmpUsingFiles);					
			}
		}

		
		ISearchMethodExtension searchMethod = UserSettings.getInstance().getCurrentlySelectedSearchMethod();
		//API Details to be added for report, parameters are given so it can be checked if Collection/Subsystem header topic are found to be added to report 
		String [] apiDetailsToReport = searchMethod.getAPIDetailsToReport(usedHeaders.keySet(), projectUsingAPIDetails);	
		CheckProjectReport report = new CheckProjectReport(usedHeaders, usingFiles, projectUsingAPIDetails, apiDetailsToReport, selectedProject);

		return report.toHTML(getTitle());

	}
	
	/**
	 * Save the report
	 * @param html
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	private IFile doSave(String html) throws CoreException, IOException{
		String containerName = exportFilePath.removeLastSegments(1).toOSString();
		String fileName = exportFilePath.lastSegment();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		//Creating temp file because given API name must be found
		InputStream stream = new ByteArrayInputStream(html.getBytes());		
		if (file.exists()) {
			file.setContents(stream, true, true, null);
		} else {
			file.create(stream, true, null);
		}			
		stream.close();
		return file;
		
	}	

	/**
	 * get title to exported report header
	 * @return title
	 */
	private String getTitle(){		
		String title = Messages.getString("ActiveProjectQueryJob.Title_Project_Msg") +getProjectName() +Messages.getString("ActiveProjectQueryJob.Title_IsUsingFollowingAPIs_Msg"); //$NON-NLS-1$ //$NON-NLS-2$
		return title;
	}	
	
	/**
	 * Get project name where report was saved
	 * @return project name
	 */
	private String getProjectName() {
		return selectedProject.getName();
	}

	/**
	 * Get report file
	 * @return file generated
	 */
	public IFile getGeneratedReportFile() {
		return generatedReportFile;
	}	

}
