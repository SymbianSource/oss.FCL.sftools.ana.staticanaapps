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


package com.nokia.s60tools.appdep.locatecomponent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndexChangeEvent;
import org.eclipse.cdt.core.index.IIndexChangeListener;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IIndexerStateEvent;
import org.eclipse.cdt.core.index.IIndexerStateListener;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.search.CSearchMessages;
import org.eclipse.cdt.internal.ui.search.PDOMSearchPatternQuery;
import org.eclipse.cdt.internal.ui.search.PDOMSearchQuery;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.main.MainViewDataPopulator;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;
import com.nokia.s60tools.util.sourcecode.CannotFoundFileException;
import com.nokia.s60tools.util.sourcecode.IProjectFinder;
import com.nokia.s60tools.util.sourcecode.ISourcesFinder;
import com.nokia.s60tools.util.sourcecode.ProjectFinderFactory;
import com.nokia.s60tools.util.sourcecode.SourceFinderFactory;

/**
 * Job for seeking possible concrete implementation of generic component name.
 */
// If public access to {@link PDOMSearchPatternQuery} is provided later on, the suppressing of warnings in here is no more needed.
@SuppressWarnings("restriction")
public class ShowMethodCallLocationsJob extends Job implements IQueryListener {

	/**
	 * Amount of steps used for the create project job progress follow-up.
	 */
	private static final int CREATING_PROJECT_STEPS = 5;
	/**
	 * Step amounts reserved for searching and indexing phase.
	 */
	private static final int SEARCH_AND_INDEX_STEPS = 85;

	/**
	 * Search string formed for the used search service-
	 */
	private String searchString = null;

	/**
	 * Project to be created for which contents the search is done.
	 */
	private ICProject cProject;

	/**
	 * File list defining valid search scope.
	 */
	private String[] filesBelongsToComponent;

	/**
	 * Job's progress monitor.
	 */
	private IProgressMonitor progressMonitor;
	/**
	 * Name of the component the method call locations are searched from.
	 */
	private String componentName = null;
	
	/**
	 * Method name to be searched from the component.
	 */
	private String methodName = null;
	/**
	 * Name of main task that is show to the user in job progress dialog.
	 */
	private final String mainTaskMessage;

	/**
	 * Constructor.
	 * @param jobName Job name 
	 * @param mainTaskMessage Task name used for main task message
	 * @param componentName Component name to search method calls from.
	 * @param methodName Method name which occurrences to search from the component..
	 * @param isUserJob if <code>true</code> job is set as user level job that prompts dialog.
	 */
	public ShowMethodCallLocationsJob(String jobName, String mainTaskMessage, String componentName, String methodName, boolean isUserJob) {
		super(jobName);
		this.mainTaskMessage = mainTaskMessage;
		this.componentName = componentName;
		this.methodName = methodName;
		setUser(isUserJob);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		
		progressMonitor = monitor;
		IStatus status;		

		try {
		
			progressMonitor.beginTask(mainTaskMessage,  100);

			AppDepSettings settings = AppDepSettings.getActiveSettings();

			SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();
			String epocRootPath = sdkInfo.getEpocRootDir(); 			

			progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.SearchingFiles_SubTask_Msg") +"\n" +componentName +"'..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			filesBelongsToComponent = getUsedComponentSourceFiles(settings, componentName, epocRootPath);			
			//Limit number of files simple founding part of the method name, to decrease project files
			setSearchString(methodName);
									
			checkIfCancelled();
			progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.SearchingBldInf_SubTask_Msg")); //$NON-NLS-1$
			progressMonitor.worked(CREATING_PROJECT_STEPS);
			
			//Finds and sets source files as IFile to variables
			String bldFile = getBldInfFileName(filesBelongsToComponent);
			
			//If the project already exist, just executing the search
			if(isProjectAllreadyExistingAndOpenIfClosed(bldFile, progressMonitor)){

				progressMonitor.worked(SEARCH_AND_INDEX_STEPS);
				checkIfCancelled();
				executeSearch(searchString);
				status = Status.OK_STATUS;

			}
			//otherwise first create a project, wait until indexed, and then execute the search
			else{			
				//Create a job to create project by bld.inf file if needed, and then execute search
				progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.CreatingProjectFromBldInf_SubTask_Msg") +"\n" +bldFile +"'..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				checkIfCancelled();
				status = createAndRunProjectCreatingJobAndExecuteSearch(bldFile, progressMonitor);				
				progressMonitor.worked(CREATING_PROJECT_STEPS);

				if(status.getSeverity() == IStatus.ERROR){
					this.cancel();
				}
				else{
					startIndexingAndSearch();					
					status = Job.ASYNC_FINISH;
				}
			}
			
		} catch (JobCancelledByUserException e) {
			// Job cancel because of user request 
			this.cancel();
			status = Status.CANCEL_STATUS;
		}catch (Exception e) {
			// Job cancel because of an exception 
			e.printStackTrace();
			String errMsg = Messages.getString("CreateProjectJob.Err_Msg") + ": " + e.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$ 
			status = reportError(e, errMsg);
			this.cancel();	
		}
		return status;
	}

	/**
	 * Reports error to user.
	 * @param e Encountered exception.
	 * @param errMsg Error message.
	 * @return status object.
	 */
	private IStatus reportError(Exception e, String errMsg) {
		IStatus status;
		status = new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR,errMsg, e);
		AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
		return status;
	}
	
	/**
	 * Gets all source files belonging to the given component.
	 * @param settings Tool settings.
	 * @param componentName component name to search method call locations from.
	 * @param epocRootPath EPOCROOT path
	 * @return Source file name array
	 * @throws CannotFoundFileException
	 * @throws CacheFileDoesNotExistException 
	 * @throws CacheIndexNotReadyException 
	 * @throws IOException 
	 */
	private String[] getUsedComponentSourceFiles(AppDepSettings settings,
			String componentName, String epocRootPath)
			throws CannotFoundFileException, IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException {

		ISourcesFinder finder = SourceFinderFactory.createSourcesFinder(AppDepConsole.getInstance());
		String  variant = MainViewDataPopulator.getTargetPlatformIdStringForComponent(settings, componentName);
		String build = settings.getBuildType().getBuildTypeName();		
		String [] files = finder.findSourceFiles(componentName, variant, build, epocRootPath);
		return files;
	}

	/**
	 * Sets search string based on the method name.
	 * @param methodName Method name.
	 */
	private void setSearchString(String methodName) {
		String shortMethodName = methodName;
		int start = methodName.indexOf("::"); //$NON-NLS-1$
		if(start != -1){
			shortMethodName = methodName.substring(start + 2);
			
		}
		int end = shortMethodName.indexOf("("); //$NON-NLS-1$
		if(end != -1){
			shortMethodName = shortMethodName.substring(0, end);
		}
		
		String longMethodNameWithOutParams;
		if(methodName.indexOf("(") != -1){ //$NON-NLS-1$
			longMethodNameWithOutParams = methodName.substring(0, methodName.indexOf("(")); //$NON-NLS-1$
		}else{
			longMethodNameWithOutParams = methodName;
		}
		
		searchString  =  longMethodNameWithOutParams;// methodName;// shortMethodName;				
	}


	/**
	 * Seeks all files by bld.inf file and set them to filesInBldInf 
	 * and seeks all bld.inf to add them to bldInfFiles
	 * @param files File name array.
	 * @return bld.inf file path and an empty string if not found.
	 * @throws URISyntaxException
	 * @throws JobCancelledByUserException 
	 */
	private String getBldInfFileName(String[] files) throws URISyntaxException, JobCancelledByUserException {
		//Opening project(s) where source files belongs to
		IProjectFinder prjFinder = ProjectFinderFactory.createProjectFinder(AppDepConsole.getInstance(), progressMonitor);
		Vector<String> bldInfFiles = new Vector<String>();
		int errorsCount = 0;
		for (int i = 0; i < files.length; i++) {
			String bldInfFile;
			try {
				String mmpFile = prjFinder.findMMPFile(files[i]);
				//If search is canceled, null was returned from the previously called method				
				if(mmpFile == null){
					checkIfCancelled();
				}else{
					bldInfFile = prjFinder.findBLDINFFile(mmpFile);
					//If search is canceled, null is returned, cancellation check will be check if null is returned
					if(bldInfFile != null){
						bldInfFiles.add(bldInfFile);
					}else {
						checkIfCancelled();
					}
				}
			} catch (CannotFoundFileException e) {
				errorsCount++;//Just counting errors, really doing nothing with those but skipping if some file does not exist
			}
		}

		// Throwing an error in case bld.inf file was not found
		if(bldInfFiles.size() == 0){
			String errMsg = Messages.getString("ShowMethodCallLocationsJob.CouldNotFindBldInfFile_ErrMsg") //$NON-NLS-1$
							+ componentName
							+ "'"; //$NON-NLS-1$
			throw new RuntimeException(errMsg);
		}
		// Returning found bld.inf file path name on success
		return bldInfFiles.elementAt(0);
	}

	/**
	 * Checks if job was cancelled by user.
	 * @throws JobCancelledByUserException
	 */
	private void checkIfCancelled() throws JobCancelledByUserException {
		if(progressMonitor.isCanceled()){
			throw new JobCancelledByUserException(Messages.getString("ShowMethodCallLocationsJob.JobCanceledByUser_ErrMsg")); //$NON-NLS-1$
		}
	}	
	
	/**
	 * Creates a project.
	 * @param bldFile bld.inf file to create project based on.
	 * @param monitor Progress monitor object
	 * @throws IOException 
	 * @throws CoreException 
	 */
	private IStatus createAndRunProjectCreatingJobAndExecuteSearch(String bldFile, IProgressMonitor monitor) throws IOException, CoreException {		
			
			DEProjectUtils utils = new DEProjectUtils(AppDepConsole.getInstance());
			CProjectJobStatus stat = utils.createProjectImpl(progressMonitor, bldFile);

			this.cProject = stat.getCProject();		
			IStatus status = stat.getStatus();
		
			return status;
	}	
	
	/**
	 * Checks if project already exist in workspace.
	 * @param bldFile bld.inf file to create project based on.
	 * @param monitor Progress monitor object
	 * @return <code>true</code> if project is found from workspace, otherwise <code>false</code>.
	 * @throws IOException
	 * @throws CoreException 
	 */
	private boolean isProjectAllreadyExistingAndOpenIfClosed(String bldFile, IProgressMonitor monitor) throws IOException, CoreException {

		IPath path = new Path(bldFile);	
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
		
		if(files.length > 0){	
			
			IProject prj = files[0].getProject();
			if(prj.isOpen()){
				return true;
			}
			else{
				prj.open(monitor);				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates query and execute the search
	 * @param searchText Search text
	 * @throws JobCancelledByUserException 
	 */
	private void executeSearch(final String searchText){

		progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.ExcecutingSearch_SubTask_Msg")); //$NON-NLS-1$
		
		final IResource[] resources = createResourcesTable();
		
		
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Starting to execute search with " +resources.length +" resources..."); //$NON-NLS-1$ //$NON-NLS-2$
		
			Runnable runSeach = new Runnable(){
				public void run(){
					try {
						if(progressMonitor.isCanceled()){
							cancelProgress();
						}else{
							ISearchQuery query = null;
							//Query is created by CDT:s interal API
							query = getSearchQuery(searchText, resources);
							//Start to listen search query for noticing done() when its completed
							NewSearchUI.addQueryListener(getIQueryListener());
							NewSearchUI.activateSearchResultView();		
							NewSearchUI.runQueryInBackground(query);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						AppDepConsole.getInstance().println(Messages.getString("ShowMethodCallLocationsJob.UnableToExecuteSearch_ErrMsg_Part2") +e, AppDepConsole.MSG_ERROR); //$NON-NLS-1$
						errorProgress(Messages.getString("ShowMethodCallLocationsJob.UnableToExecuteSearch_ErrMsg_Part1"), e);  //$NON-NLS-1$
					}
				}

			};
			
			// Showing a visible message in its own thread
			// in order not to cause invalid thread access
			Display.getDefault().asyncExec(runSeach);									
	}		
	
	/**
	 * Get search query.
	 * <p>
	 * This method is using CDT:s internal API:s and if public access to {@link PDOMSearchPatternQuery}
	 * is provided, implementation should be changed to use public implementation. 
	 * Internal API is used, because of ready made implementation of find functions and methods references
	 * is found in there. 
	 * @param searchText, text to search, without parameters. Cut search String before first brace "(". 
	 * With parameters and braces, you don't get any results. 
	 * @param resources
	 * @return query to give to Search engine.
	 * @throws CoreException
	 */
	private ISearchQuery getSearchQuery(
			final String searchText, final IResource[] resources)
			throws CoreException {
		
		// get the list of elements for the scope
		List<Object> elements = new ArrayList<Object>();
		//Search from the given source files found in .map file
		String scopeDescription = CSearchMessages.SelectionScope; 

		for (int i = 0; i < resources.length; i++) {
			elements.add(CoreModel.getDefault().create(resources[i]));
		}

		boolean isCaseSensitive = true;
		//to Search query, we must give resources as ICElement
		ICElement[] scope = elements.isEmpty() ? null : elements.toArray(new ICElement[elements.size()]);
		//Same flags covers search for and limit to selections, we want to search functions/methods and only references
		int searchFlags = PDOMSearchPatternQuery.FIND_FUNCTION | PDOMSearchPatternQuery.FIND_METHOD
							| PDOMSearchQuery.FIND_REFERENCES;

		//class to execute search is interal class provided in CDT, permission to use internal classes is granted.
		//Parameters cannot be used wih Query, only class name(s) and method name
		PDOMSearchPatternQuery query = new PDOMSearchPatternQuery(scope, scopeDescription, searchText, 
				isCaseSensitive , searchFlags );		
		
		return query;
	}
	
	/**
	 * Creates a array containing IFile links to files that were found from map file.
	 * @return Resource object array.
	 */
	private IResource[] createResourcesTable() {
		HashSet<IFile> filesInBldInf = new HashSet<IFile>();
		
		for (int i = 0; i < filesBelongsToComponent.length; i++) {
			String uriStr = filesBelongsToComponent[i].replace("\\", "/");; //$NON-NLS-1$ //$NON-NLS-2$
			uriStr = "file://" + uriStr; //$NON-NLS-1$
			IFile[] wsFile;
			try {
				wsFile = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(
						new URI( uriStr));
				for (int j = 0; j < wsFile.length; j++) {
					filesInBldInf.add(wsFile[j]);							
				}			
			} catch (URISyntaxException e) {
				e.printStackTrace();
				AppDepConsole.getInstance().println(
						Messages.getString("ShowMethodCallLocationsJob.UnableToAddFile_ErrMsg_Part1") +uriStr  //$NON-NLS-1$
						+Messages.getString("ShowMethodCallLocationsJob.UnableToAddFile_ErrMsg_Part2") +e, AppDepConsole.MSG_ERROR); //$NON-NLS-1$
				
			}
		}
		
		final IResource[] resources = (IFile[])filesInBldInf.toArray(new IFile[0]);
		return resources;
	}		

	/**
	 * Triggers indexing and searching.
	 * @throws JobCancelledByUserException
	 */
	private void startIndexingAndSearch() throws JobCancelledByUserException {
		checkIfCancelled();
		IIndexManager indexManager = CCorePlugin.getIndexManager();		
		boolean projectIndexed = indexManager.isProjectIndexed(cProject);
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Project: " +cProject.getElementName() + " indexed: " +projectIndexed + " and indexer is idling: " +indexManager.isIndexerIdle()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		//If project is already indexed, search can be started right away
		if(projectIndexed && indexManager.isIndexerIdle()){
			executeSearch( searchString);								
		}
		//otherwise starting to listen indexer, and when its done, executing search
		else{						
			progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.StartingIndexing_SubTask_Msg")); //$NON-NLS-1$
			IndexerStateListener listener = new IndexerStateListener();
			indexManager.addIndexerStateListener(listener);			
			indexManager.addIndexChangeListener(listener);
		}
	}	
	
	/**
	 * Listener class implementation for listening indexer state changes and start
	 * execution of search when indexer is in idle state.
	 */
	private class IndexerStateListener implements IIndexerStateListener, IIndexChangeListener {
	
		public IndexerStateListener(){
			progressMonitor.subTask(Messages.getString("ShowMethodCallLocationsJob.IndexingProject_SubTask_Msg") +cProject.getProject().getName() +"'..."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		private Double step = new Double (new Double(SEARCH_AND_INDEX_STEPS) / new Double( filesBelongsToComponent.length));
		private double workLeftFromPreviousStep = 0;

		/* (non-Javadoc)
		 * @see org.eclipse.cdt.core.index.IIndexerStateListener#indexChanged(org.eclipse.cdt.core.index.IIndexerStateEvent)
		 */
		public void indexChanged(IIndexerStateEvent event) {
			try {
				boolean indexerIsIdle = event.indexerIsIdle();
				if(indexerIsIdle){
					execute();
				}
			} catch (Exception e) {
				errorProgress(Messages.getString("ShowMethodCallLocationsJob.UnableToExecuteSearch_ErrMsg_Part1"), e); //$NON-NLS-1$
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.cdt.core.index.IIndexChangeListener#indexChanged(org.eclipse.cdt.core.index.IIndexChangeEvent)
		 */
		public void indexChanged(IIndexChangeEvent event) {

			try{
				if(progressMonitor.isCanceled()){
					IIndexManager indexManager = CCorePlugin.getIndexManager();
					removeListeners(indexManager);
					cancelProgress();
				}
				
				//Counting what's left from previous step and number of one step
				double stepTo = workLeftFromPreviousStep + step;
				Double stepNow = new Double(stepTo);
				//Take out int value of this step (x from x.yyyyyy)
				int workNow = stepNow.intValue();
				stepNow = stepNow-workNow;
				//put on hold what was left from this step to next step (yyyy from x.yyyy)			
				workLeftFromPreviousStep = stepNow.doubleValue();
				
				progressMonitor.worked(workNow);
				
				//Continuing execution if indexer is idle.
				IIndexManager indexManager = CCorePlugin.getIndexManager();
				if(indexManager.isIndexerIdle()){
					execute();
				}
			} catch (Exception e) {
				errorProgress(Messages.getString("ShowMethodCallLocationsJob.UnableToExecuteSearch_ErrMsg_Part1"), e);//$NON-NLS-1$
			}			
		}
		
		/**
		 * Starts execution of search 
		 * @throws JobCancelledByUserException 
		 */
		private void execute() {
			IIndexManager indexManager = CCorePlugin.getIndexManager();
			boolean projectIndexed = indexManager.isProjectIndexed(cProject);
			
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Project indexed: " +projectIndexed  + " and indexer is idling: " +indexManager.isIndexerIdle());	//$NON-NLS-1$ //$NON-NLS-2$
			//Search can be started if project is indexed and indexer is ready (not doing anything anymore).
			if(projectIndexed && indexManager.isIndexerIdle()){
				
				removeListeners(indexManager);
				executeSearch( searchString);								
			}
		}

		/**
		 * Removes listeners from index manager.
		 * @param indexManager Index manager.
		 */
		private void removeListeners(IIndexManager indexManager) {
			indexManager.removeIndexChangeListener(this);
			indexManager.removeIndexerStateListener(this);
		}
		
	}

	/**
	 * An error occurred, cancels progress and sends an error.
	 */
	private void errorProgress(String errMsg, Exception e) {
		// Cancelling query
		progressMonitor.done();
		IStatus status = new Status(
				Status.ERROR,Platform.PI_RUNTIME,
				Status.ERROR,errMsg, e);		
		done(status);
	}	
	
	/**
	 * Cancels progress without sending exception. 
	 */
	private void cancelProgress() {
		// Cancelling query
		progressMonitor.done();
		done(Status.CANCEL_STATUS);
	}

	/**
	 * Gets query listener interface.
	 * @return this
	 */
	public IQueryListener getIQueryListener(){
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.IQueryListener#queryAdded(org.eclipse.search.ui.ISearchQuery)
	 */
	public void queryAdded(ISearchQuery query) {
		// not needed
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.IQueryListener#queryFinished(org.eclipse.search.ui.ISearchQuery)
	 */
	public void queryFinished(ISearchQuery query) {
		//When query is finished, returning OK status
		progressMonitor.done();
        // Job has completed successfully
	    done(Status.OK_STATUS);				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.IQueryListener#queryRemoved(org.eclipse.search.ui.ISearchQuery)
	 */
	public void queryRemoved(ISearchQuery query) {
		cancelProgress();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.IQueryListener#queryStarting(org.eclipse.search.ui.ISearchQuery)
	 */
	public void queryStarting(ISearchQuery query) {
		// not needed		
	}		
	
}
