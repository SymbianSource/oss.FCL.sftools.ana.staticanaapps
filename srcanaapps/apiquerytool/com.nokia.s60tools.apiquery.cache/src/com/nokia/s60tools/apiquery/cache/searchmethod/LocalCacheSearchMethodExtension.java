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
 
package com.nokia.s60tools.apiquery.cache.searchmethod;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;



import com.nokia.s60tools.apiquery.cache.configuration.CacheEntry;
import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.core.job.UpdateSDKSelectionJob;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.cache.searchmethod.ui.LocalCacheUIComposite;
import com.nokia.s60tools.apiquery.cache.util.SDKFinder;
import com.nokia.s60tools.apiquery.cache.util.SDKUtil;
import com.nokia.s60tools.apiquery.cache.xml.MetadataXMLToUIMappingRules;
import com.nokia.s60tools.apiquery.popup.actions.OpenFileAction;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescriptionSearchResults;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.exceptions.XMLNotValidException;
import com.nokia.s60tools.apiquery.shared.searchmethod.AbstractSearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.SearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.services.QueryServices;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Search Method extension allowing to create local caches from the web server
 * entries, and used the cached information instead of the online information.
 */
public class LocalCacheSearchMethodExtension implements  ISearchMethodExtension, IExecutableExtension, IJobChangeListener
{
	
	/**
	 * This class is created by using <code>createExecutableExtension</code>
	 * method from <code>IConfigurationElement</code> class, and therefore the
	 * constructor cannot have any parameters.
	 * 
	 * @see org.eclipse.core.runtime.IConfigurationElement#createExecutableExtension
	 */
	public LocalCacheSearchMethodExtension(){
		extensionInfo = new SearchMethodExtensionInfo();			
	}
	
	private SearchMethodExtensionInfo extensionInfo;
	private APIQueryParameters apiQueryParameters = null;
		


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement configElem, String classPropertyName, Object data) throws CoreException {
		extensionInfo.setId(configElem.getAttribute(AbstractSearchMethodExtension.ID_ATTRIBUTE));
		extensionInfo.setDescription(configElem.getAttribute(AbstractSearchMethodExtension.DESCRIPTION_ATTRIBUTE));
		extensionInfo.setDefault(Boolean.parseBoolean(configElem.getAttribute(AbstractSearchMethodExtension.IS_DEFAULT_ATTRIBUTE)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getExtensionInfo()
	 */
	public SearchMethodExtensionInfo getExtensionInfo() {
		return extensionInfo;
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtension#notifyExtensionShutdown()
	 */
	public void notifyExtensionShutdown() {
		// Currently nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.searchmethodregistry.ISearchMethodExtension#createExtensionConfigurationUi(org.eclipse.swt.widgets.Composite)
	 */
	public AbstractUiFractionComposite createExtensionConfigurationUi(Composite parent) {
		return new LocalCacheUIComposite(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#isSupportedQueryType(int)
	 */
	public boolean isSupportedQueryType(int queryType) {
		// By default the query type is not supported.
		boolean isSupported = false;
		
		switch (queryType) {
		
	     	// Flow through (supported types)
			case APIQueryParameters.QUERY_BY_API_NAME:
			case APIQueryParameters.QUERY_BY_SUBSYSTEM_NAME:
			case APIQueryParameters.QUERY_BY_LIB_NAME:
			case APIQueryParameters.QUERY_BY_HEADER_NAME:
				 isSupported = true;
			     break;
			     
			default:
				break;
		}

		return isSupported;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getAPIDetails(com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription)
	 */
	public APIDetails getAPIDetails(APIShortDescription summary) throws QueryOperationFailedException {
		
		CacheEntryStorage storage = CacheEntryStorage.getInstance();
		if(!storage.isLoaded()){
			startUpdateJobAndWaitUntilFinished(storage);		
		}
		
		APIDetails details = null;
	
		CacheEntry entry = (CacheEntry) storage.getByEntryId(summary.getSource());		
		details = entry.getAPIDetails();		
		return details;				
	}	
	


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getEntryStorageInstance()
	 */
	public AbstractEntryStorage getEntryStorageInstance() {
		return CacheEntryStorage.getInstance();
	}


	
	/**
	 * Handles (run) query for one entry
	 * 
	 * @param parameters
	 * @param runSynchronous
	 * @return APIs matching search criteria
	 * @throws QueryOperationFailedException
	 */
	private Collection<APIShortDescription> handleSingleEntryQuery(APIQueryParameters parameters, boolean runSynchronous) throws QueryOperationFailedException {
		
		ArrayList<APIShortDescription> summary = null;
		// Create a job to convert needed Excel If sheets to XML format
		try {
				summary = getSummarys(parameters, runSynchronous);
		} catch (Exception e1) {
			examineAndHandleQueryFailureException(e1);
		}
		
		return summary;
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#runAPIQuery(com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters)
	 */
	public APIShortDescriptionSearchResults runAPIQuery(APIQueryParameters parameters) {
		
		this.apiQueryParameters  = parameters;
		// Owerwriting query for if sheets, because of usually there is multiple
		// (>100) data sources
		APIShortDescriptionSearchResults results = new APIShortDescriptionSearchResults();

		try {
			// Making query for each configured and selected server entry.
			AbstractEntry[] selEntryArr = getEntryStorageInstance().getSelectedEntries();
			
			// Were any entries configured or selected?
			if(selEntryArr.length == 0){
				throw new QueryOperationFailedException(Messages.getString("LocalCacheSearchMethodExtension.No_Server_Entries_ErrMsg")); //$NON-NLS-1$
			}
			
			boolean runSynchronous = !parameters.isQueryFromUI();// If query
																	// comes
																	// from UI,
																	// running
																	// async, if
																	// not,
																	// running
																	// sync
			
			// If there was semi-comma separated search items or not
			if (parameters.getSearchString().contains(
					APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR)) {
				results.addSearchResults(handleMultiEntryQuery(parameters, runSynchronous));
			} else {
				results.addSearchResults(handleSingleEntryQuery(parameters, runSynchronous));
			}
		}catch(QueryOperationFailedException e){
			results.addSearchError(e);
		}
		catch (Exception e) {
			e.printStackTrace();
			results.addSearchError( new QueryOperationFailedException(e.getMessage()));
		}
		
		return results;
	}		
	
	/**
	 * Get APIShortDescriptions from XML Excel If Sheet XML files.
	 * 
	 * @return
	 * @throws QueryOperationFailedException
	 */
	private ArrayList<APIShortDescription> getSummarys( APIQueryParameters parameters, boolean runSynchronous) throws QueryOperationFailedException {
		ArrayList<APIShortDescription> summary = new ArrayList<APIShortDescription>();
		String curId = "";//$NON-NLS-1$
		try {		
			CacheEntryStorage storage = CacheEntryStorage.getInstance();
			Collection<AbstractEntry> selectedAPIs = storage.getSelectedEntriesCollection();
			
			// if storage is not loaded, e.g. this is first time of searching
			// after Carbide is started up, must load data first
			if(!storage.isLoaded() && !runSynchronous){
				startUpdateJobAndRunQueryWhenFinished(storage);
			}
			else{
				if(!storage.isLoaded()){
					startUpdateJobAndWaitUntilFinished(storage);
				}				
			
				String [] searchStrings = new String []{parameters.getSearchString()};			
	
				
				// Try to found search string from selected search method
				// (selected field from XML data)
				for (AbstractEntry abstractEntry : selectedAPIs) {
					
					CacheEntry entry = (CacheEntry)abstractEntry;
					
					APIDetails det = entry.getAPIDetails();
					if(det == null){
						APIQueryConsole.getInstance().println(Messages.getString("LocalCacheSearchMethodExtension.UnexpectedEntryErrMsg") +entry.getId()); //$NON-NLS-1$
						continue;// Should not be able to occur, because load
									// deselects source if load fails
					}
					curId =  entry.getId(); 
					String curSourceDesc = entry.getName() ;
					// Select Field from APIDetail, by selected search type, to
					// found if that field contais the search string
					APIDetailField fieldToSearchFor = getSelectedSearchField(det, parameters.getQueryType());
					String fieldValueToSearch = fieldToSearchFor.getValue();
					String apiName = det.getDetail(MetadataXMLToUIMappingRules.NAME).getValue();
	
					DbgUtility.println(DbgUtility.PRIORITY_LOOP, "From API: " +//$NON-NLS-1$
							apiName
							+", file: " + curId //$NON-NLS-1$
							+", querytype: " +parameters.getQueryType()//$NON-NLS-1$
							+ ", foundig values to search for: " +fieldValueToSearch);//$NON-NLS-1$
					
					// Looking for all search strings if there was semi-comma
					// separated strings to search for
					for (int i = 0; i < searchStrings.length; i++) {
						boolean matchs;
						if(apiQueryParameters.isExactMatchInUse()){
							// If exact match is in use, using equals as match
							matchs = fieldValueToSearch.equalsIgnoreCase(searchStrings[i]);
						}
						else{
							// If exact match is not in use, and if result
							// contains search string (case insensitive)
							matchs = fieldValueToSearch.toLowerCase().contains(searchStrings[i].toLowerCase());
						}
						// If result matches to search string, creating new API
						// Summary object and add it to result

						if(matchs){
							APIShortDescription sum = new APIShortDescription(
									apiName
									,curId,
									curSourceDesc);
							// If API Details should be added to description
							// then adding it
							if(parameters.isDetailsMentToAddToDescriptions()){
								sum.setAPIDetails(det);
							}
							summary.add(sum);
						}					
					}
				}
			}
		
		} catch (Exception e) {
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Error when handling file: " + curId);//$NON-NLS-1$
			e.printStackTrace();
			examineAndHandleQueryFailureException(e);
		}
		return summary;
	}

	/**
	 * Update (load) data sources if not loaded yet, after done, launch real
	 * query
	 * 
	 * @param storage
	 * @throws QueryOperationFailedException
	 */
	private void startUpdateJobAndRunQueryWhenFinished(CacheEntryStorage storage)
			throws QueryOperationFailedException {

		try {		
			
			SdkInformation info = SDKFinder.getSDKInformation(storage.getCurrentlySelectedSDKID());
			UpdateSDKSelectionJob job = new UpdateSDKSelectionJob(Messages.getString("LocalCacheSearchMethodExtension.UpdatingDataSourceMsg_Part1") +info.getSdkId() +Messages.getString("LocalCacheSearchMethodExtension.UpdatingDataSourceMsg_Part2"), info, false); //$NON-NLS-1$ //$NON-NLS-2$
			if(job.isAllreadyRunning()){
				return;
			}
			job.setPriority(Job.DECORATE);			
			job.addJobChangeListener(this);
			job.schedule();

		} catch (Exception e) {
			e.printStackTrace();
			examineAndHandleQueryFailureException(e);
		}
	}	
	
	/**
	 * Update (load) data sources if not loaded yet, after done, launch real
	 * query
	 * 
	 * @param storage
	 * @throws QueryOperationFailedException
	 */
	private void startUpdateJobAndWaitUntilFinished(CacheEntryStorage storage)
			throws QueryOperationFailedException {

		try {		
			
			SdkInformation info = SDKFinder.getSDKInformation(storage.getCurrentlySelectedSDKID());
			UpdateSDKSelectionJob job = new UpdateSDKSelectionJob(Messages.getString("LocalCacheSearchMethodExtension.UpdatingDataSourceMsg_Part1") +info.getSdkId() +Messages.getString("LocalCacheSearchMethodExtension.UpdatingDataSourceMsg_Part2"), info, false); //$NON-NLS-1$ //$NON-NLS-2$
			if(job.isAllreadyRunning()){
				return;
			}
			job.setPriority(Job.DECORATE);
			job.schedule();
			job.join();

		} catch (Exception e) {
			e.printStackTrace();
			examineAndHandleQueryFailureException(e);
		}
	}	
	
	
	/**
	 * get search field by query type
	 * 
	 * @param det
	 * @param queryType
	 * @return
	 */
	private APIDetailField getSelectedSearchField(APIDetails det, int queryType) {
				
		APIDetailField field;
		switch (queryType) {
		case APIQueryParameters.QUERY_BY_API_NAME:
			field = det.getDetail(MetadataXMLToUIMappingRules.NAME);
			break;
		case APIQueryParameters.QUERY_BY_SUBSYSTEM_NAME:
			field = det.getDetail(MetadataXMLToUIMappingRules.SUBSYSTEM);
			break;
		case APIQueryParameters.QUERY_BY_LIB_NAME:
			field = det.getDetail(MetadataXMLToUIMappingRules.LIBS);
			break;
		case APIQueryParameters.QUERY_BY_HEADER_NAME:
			field = det.getDetail(MetadataXMLToUIMappingRules.HEADERS);
			break;
		default:
			field = det.getDetail(MetadataXMLToUIMappingRules.NAME);
			break;
		}		
		return field;
	}	


	/**
	 * Handles (run) query for several search strings
	 * 
	 * @param parameters
	 * @param runSynchronous
	 * @return APIs matching search criteria
	 * @throws QueryOperationFailedException
	 */
	private Collection<APIShortDescription> handleMultiEntryQuery(APIQueryParameters parameters, boolean runSynchronous) throws QueryOperationFailedException {
		
		// Using table to make sure that same API:s does not exist more than
		// once
		Hashtable<String, APIShortDescription> summary  = new Hashtable<String, APIShortDescription>();
		String [] searchStrings = parameters.getSearchString().split(APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR);
		
		APIQueryParameters param ;
		Collection<APIShortDescription>  temp;
		// Adding all APIDetails to collection one by one
		for (int i = 0; i < searchStrings.length; i++) {
			param = new APIQueryParameters(parameters.getQueryType(), searchStrings[i]);

			temp = handleSingleEntryQuery(param, runSynchronous);
			for (APIShortDescription api : temp) {
				summary.put(api.getName(), api);// One API can contain in
												// results only once. HashTable
												// does not allow multiple
												// occurances as keys.
			}			

		}		
		return summary.values();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getAPIDetails(java.util.Collection)
	 */
	public Hashtable<String, APIDetails> getAPIDetails(Collection<APIShortDescription> apis) throws QueryOperationFailedException {

		CacheEntryStorage storage = CacheEntryStorage.getInstance();
		if(!storage.isLoaded()){
			startUpdateJobAndWaitUntilFinished(storage);		
		}
		
		Hashtable<String, APIDetails> apiDetails = new Hashtable<String, APIDetails>(apis.size());
		for (APIShortDescription summary : apis) {
			apiDetails.put(summary.getName(), getAPIDetails(summary));
		}
		return apiDetails;
	}	
	
	/**
	 * Examines the type of throwable parameter, and adds appropriate error
	 * message or default message, and forwards the information for further
	 * processing.
	 * 
	 * @param throwable
	 *            Throwable to be examined.
	 * @throws QueryOperationFailedException
	 */
	private void examineAndHandleQueryFailureException(Throwable throwable) throws QueryOperationFailedException{
		
		String errMsg = Messages.getString("LocalCacheSearchMethodExtension.UnexpectedException_ErrMsg"); //$NON-NLS-1$
		
		if(throwable instanceof IOException) {
			errMsg = Messages.getString("LocalCacheSearchMethodExtension.IOException_ErrMsg"); //$NON-NLS-1$
		}
		else if(throwable instanceof XMLNotValidException) {
			errMsg = Messages.getString("LocalCacheSearchMethodExtension.InvalidXML_ErrMsg"); //$NON-NLS-1$
		} 
		
		queryFailed(errMsg, throwable.getMessage());
	}
	/**
	 * Throws an exception for user with the given message.
	 * 
	 * @param errorMsg
	 *            Main error message for the user.
	 * @param detailedMsg
	 *            More detailed message shown in parenthesis.
	 * @throws QueryOperationFailedException
	 */
	private void queryFailed(String errorMsg, String detailedMsg) throws QueryOperationFailedException {
		APIQueryConsole.getInstance().println(errorMsg, APIQueryConsole.MSG_ERROR);
		String combinedMsg = errorMsg + " (" + detailedMsg + ")."; //$NON-NLS-1$ //$NON-NLS-2$
		throw new QueryOperationFailedException(combinedMsg);
	}	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 *      When data sources (metadata) is loaded, running real query
	 * @see com.nokia.s60tools.apiquery.ui.views.main.MainView#runAPIQueryFromExternalClass(int,
	 *      java.lang.String)
	 */
	public void done(IJobChangeEvent event) {
		
		Job job = event.getJob();
		UpdateSDKSelectionJob ifsJob = (UpdateSDKSelectionJob)job;
		ifsJob.reportEndTime();
		IStatus status = ifsJob.getResult();

		// Chekc that job status was ok before launching query
		if( status != null && status.getSeverity() == IStatus.OK) { 
		
			// Launching the the real query when XML:s was generated
			Runnable runQueryRunnable = new Runnable(){
				public void run(){
					try {
						QueryServices.runAPIQuery(apiQueryParameters.getQueryType(),
								apiQueryParameters.getSearchString());
					} catch (QueryOperationFailedException e) {
						APIQueryConsole.getInstance().println(Messages.getString("LocalCacheSearchMethodExtension.Failed_To_Start_Query_ErrMsg")  //$NON-NLS-1$
								+e.getMessage(), APIQueryConsole.MSG_ERROR);
						e.printStackTrace();
					}
				}
			};
						
			// Showing a visible message has to be done in its own thread
			// in order not to cause invalid thread access
			Display.getDefault().asyncExec(runQueryRunnable);        	
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		// Not needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		// Not needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// Not needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		// Not needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// Not needed
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getAPIDetailsToReport()
	 */
	public String[] getAPIDetailsToReport(Set <String> usedAPIs, Hashtable<String, APIDetails> projectUsingAPIDetails) {
		
		boolean containsCollection = false;
		boolean containsSubsystem = false;
		for (String api : usedAPIs) {
			APIDetails det = projectUsingAPIDetails.get(api);
			if(det == null || det.getKeys() == null){
				continue;
			}
			if(det.getKeys().contains(MetadataXMLToUIMappingRules.COLLECTION)){
				containsCollection = true;
			}
			if(det.getKeys().contains(MetadataXMLToUIMappingRules.SUBSYSTEM)){
				containsSubsystem = true;
			}	
			// If we found occurrence of both subsystems version (sybsystem in
			// data version 1.0 collection in data version 2.0)
			// we can continue because then both headers will occur in report
			// anyway
			if(containsCollection && containsSubsystem){
				break;
			}
		}
		
		String [] apiDetailsToReport;
		
		// If both subsystem versions is found
		if(containsCollection && containsSubsystem){
			apiDetailsToReport= new String []{
				MetadataXMLToUIMappingRules.COLLECTION,
				MetadataXMLToUIMappingRules.SUBSYSTEM,
				MetadataXMLToUIMappingRules.RELEASE_CATEGORY,
				MetadataXMLToUIMappingRules.RELEASE_SINCE_VERSION,
				MetadataXMLToUIMappingRules.RELEASE_DEPRECATED_SINCE_VERSION,
				MetadataXMLToUIMappingRules.LIBS		
			};
		}
		// if we found only collections, not subsystems
		else if(containsCollection && !containsSubsystem){
			apiDetailsToReport= new String []{
				MetadataXMLToUIMappingRules.COLLECTION,
				MetadataXMLToUIMappingRules.RELEASE_CATEGORY,
				MetadataXMLToUIMappingRules.RELEASE_SINCE_VERSION,
				MetadataXMLToUIMappingRules.RELEASE_DEPRECATED_SINCE_VERSION,
				MetadataXMLToUIMappingRules.LIBS		
			};
		}
		// else if we dont found collections or we did not found anything, using
		// subsystem as default value
		else {// if(!containsCollection){
			apiDetailsToReport= new String []{
				MetadataXMLToUIMappingRules.SUBSYSTEM,
				MetadataXMLToUIMappingRules.RELEASE_CATEGORY,
				MetadataXMLToUIMappingRules.RELEASE_SINCE_VERSION,
				MetadataXMLToUIMappingRules.RELEASE_DEPRECATED_SINCE_VERSION,
				MetadataXMLToUIMappingRules.LIBS		
			};
		}				
		return apiDetailsToReport;
	}	
	
	/**
	 * Maps query type to API Detail topic in API Details.
	 * 
	 * @param queryType
	 *            Query type constant.
	 * @return Name of the API Detail.
	 */
	public String getAPIDetailNameInDetailsByQueryType(int queryType){
		
		String apiDetail = null;
		
		switch (queryType) {

		case APIQueryParameters.QUERY_BY_API_NAME:
			apiDetail = MetadataXMLToUIMappingRules.NAME;
			break;

		case APIQueryParameters.QUERY_BY_SUBSYSTEM_NAME:
			apiDetail = MetadataXMLToUIMappingRules.SUBSYSTEM;
			break;

		case APIQueryParameters.QUERY_BY_DLL_NAME:
			apiDetail = XMLUtils.DESCRIPTION_DLLS;
			break;

		case APIQueryParameters.QUERY_BY_LIB_NAME:
			apiDetail = MetadataXMLToUIMappingRules.LIBS;
			break;

		case APIQueryParameters.QUERY_BY_HEADER_NAME:
			apiDetail = MetadataXMLToUIMappingRules.HEADERS;
			break;

		case APIQueryParameters.QUERY_BY_CRPS_KEY_NAME:
			apiDetail = XMLUtils.DESCRIPTION_KEY_NAME;
			break;

		default:
			throw new IllegalArgumentException(Messages.getString("APIQueryParameters.QueryTypePart1_ErrMsg")  //$NON-NLS-1$
					                           + queryType
					                           + Messages.getString("APIQueryParameters.QueryTypePart2_ErrMsg") //$NON-NLS-1$
					                           );
		}
		
		return apiDetail;
	}		
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension#getQueryTypeByAPIDetailNameInDetails(java.lang.String)
	 */
	public int getQueryTypeByAPIDetailNameInDetails(String queryType) {
		int queryInt = -1;
		
		if(queryType == null){
			return queryInt;
		}		

		if(queryType.equals(MetadataXMLToUIMappingRules.NAME)){
			queryInt = APIQueryParameters.QUERY_BY_API_NAME;
		}
		else if(queryType.equals( MetadataXMLToUIMappingRules.SUBSYSTEM)){
			queryInt = APIQueryParameters.QUERY_BY_SUBSYSTEM_NAME;
		}
		else if(queryType.equals(XMLUtils.DESCRIPTION_DLLS)){
			queryInt = APIQueryParameters.QUERY_BY_DLL_NAME;
		}
		else if(queryType.equals(MetadataXMLToUIMappingRules.LIBS)){
			queryInt = APIQueryParameters.QUERY_BY_LIB_NAME;
		}
		else if(queryType.equals(MetadataXMLToUIMappingRules.HEADERS)){
			queryInt = APIQueryParameters.QUERY_BY_HEADER_NAME;
		}
		else if(queryType.equals(XMLUtils.DESCRIPTION_KEY_NAME)){
			queryInt = APIQueryParameters.QUERY_BY_CRPS_KEY_NAME;
		}		
		
		return queryInt;
	}

	public boolean isAsyncQueryPreferred() {
		return false;
	}

	
/**
 * opens the header file in the workbench
 */
	/*
	public int openHeaderFile(String headerName,String source) {
		try{
		
			String temp =source.replace(
					"\\", "/");
			System.out.println(temp);

			temp = temp.substring(0, temp.lastIndexOf("/"))
					+ "/inc/" + headerName;
			temp = "file://" + temp;
			System.out.print("temp" + temp);
		
		
			
			OpenFileAction action = new OpenFileAction();
			action.openFile(new URI(temp), headerName);
		}catch (Exception e) {
			return 1;
		}
		
		return 0;
	}
	*/
	
	

	public boolean serachHeaderLinkEnable() {
	
		return true;
	}

	public String[] getHeaderSourceList() {
		return	new String[] 
				         {"http://s60lxr", "http://developer.symbian.org/xref/oss", "3.2RnDSDK", "pf_5250robot"};			    
		
		}	
	
	

		public int  openHeaderFile(String headerName, String APIName) {
			try{
			System.out.println("header" + headerName + "APINAME " +APIName);
				
		// get the source
		String	source =	LocalCacheUIComposite.headerSource;
		String url = " ";
		boolean isweb = false;
		if(source.equalsIgnoreCase("http://s60lxr")){
			 url  = "http://s60lxr/search?filestring=%2F"+headerName+"%24&advanced=1&string=";
			 isweb = true;			
		}
		else if(source.equalsIgnoreCase("http://developer.symbian.org/xref/oss"))
		{url="http://developer.symbian.org/xref/epl/search?q=&defs=&refs=&path="+headerName+"&hist=&project=%2FMCL";
		isweb = true;
		}

	
			
			if(isweb)	
			{	//open from the browser	
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWebBrowser browser;

				browser = workbench.getBrowserSupport()
						.createBrowser(null);
							
				browser
						.openURL(new java.net.URL(url));
			}
			else
			{ //open from the sdk
				SDKUtil.headerOpen(source, APIName, headerName);
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return 0 ;
				
			}
				
	 
			
			
		
	
	
	
		
}
