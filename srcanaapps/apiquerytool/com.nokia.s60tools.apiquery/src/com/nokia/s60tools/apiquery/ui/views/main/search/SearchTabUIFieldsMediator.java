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

package com.nokia.s60tools.apiquery.ui.views.main.search;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.apiquery.settings.IUserSettingsListener;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescriptionSearchResults;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.AbstractSearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Mediator class between the search tab's UI fields.
 * Listens the actions and creates the query object, 
 * performs the query, and controls the population
 * of the fields used to query results. 
 */
class SearchTabUIFieldsMediator implements IQueryDefCompositeListener,
		IUserSettingsListener, IResultSettingsListener {

	/**
	 * UI control used to set the query type.
	 */
	private final QueryTypeSelectionComposite queryTypeControl;

	private final QueryResultsComposite queryResultsControl;

	private final QueryDefComposite queryDefControl;

	/**
	 * Collection to store returned API summary data and errors.
	 */
	APIShortDescriptionSearchResults resultColl = null;

	private APIQueryParameters params;

	static Job prevWebSerachJob = null;

	static Object prevQueryJobObjLock = new Object();

	static String prevQueryString = null;

	static public IProgressMonitor progressMonitor;

	//	Web query job
	class WebQueryJob extends Job {
		private String searchString = null;

		public WebQueryJob(String jobDescription, String searchString) {
			super(jobDescription);
			this.searchString = searchString;
		}

		protected IStatus run(IProgressMonitor monitor) {
			try {
				progressMonitor = monitor;
				prevQueryString = searchString;
				//start the progress operation
				progressMonitor.beginTask("Web Query in progress... ",
						IProgressMonitor.UNKNOWN);

				performQuery(params,monitor);

				if (monitor.isCanceled()) {

					return Status.CANCEL_STATUS;
				}

				return Status.OK_STATUS;

			}
			catch (JobCancelledByUserException e)
			{	
				//System.out.println("Job cancelled");
				return Status.CANCEL_STATUS;
				
			}

			catch (Exception e) {

				APIQueryMessageBox msgBox = new APIQueryMessageBox(
						Messages
								.getString("SearchTabComposite.API_Query_ErrMsg") + e.getMessage() //$NON-NLS-1$
						, SWT.ICON_ERROR | SWT.OK);
				msgBox.open();
			} finally {
				monitor.done();
			}
			return Status.CANCEL_STATUS;
		}

	}

	class WebQueryJobListener extends JobChangeAdapter {

		Display d = null;

		String searchString = null;

		WebQueryJobListener(Display d, String searchString) {
			this.d = d;
			this.searchString = searchString;
		}

		public void done(IJobChangeEvent event) {

			synchronized (prevQueryJobObjLock) {

				try {
					//	if job done ,update the display with results
					if (event.getResult().isOK()) {
						updateResults(true, d, searchString);

					}

				} catch (Exception e) {

					e.printStackTrace();

				} finally {
					// finally release
					if (event.getJob() == prevWebSerachJob) {
						prevWebSerachJob = null;

					}
				}

			}

		}
	}

	public SearchTabUIFieldsMediator(
			QueryTypeSelectionComposite queryTypeControl,
			QueryDefComposite queryDefControl,
			QueryResultsComposite queryResultsControl) {
		this.queryTypeControl = queryTypeControl;
		this.queryDefControl = queryDefControl;
		this.queryResultsControl = queryResultsControl;
		// Registering to listen for query start actions
		this.queryDefControl.setCompositeListener(this);
		// Registering to listen for result view setting changes 
		this.queryResultsControl.setResultSettingsListener(this);
		// Starting to listen user settings change events
		UserSettings.getInstance().addUserSettingListener(this);
	}

	/**
	 * Performs the according the given parameters and
	 * updates the UI components after the query.
	 * @param params Parameters for the query.
	 * @param errInfo Error info class for returning 
	 *                error description in case if an error.
	 */
	private void performQuery(APIQueryParameters params) {
		try { // disable datasource selection option from properites page
			MainView.enablePropTabcontents(false);
			resultColl = null; // Resetting previous searches.		
			ISearchMethodExtension currSelExt = UserSettings.getInstance()
					.getCurrentlySelectedSearchMethod();
			resultColl = currSelExt.runAPIQuery(params);
		} finally {

			MainView.enablePropTabcontents(true);
		}
	}

	/**
	 * Runs <code>this.queryStarted(searchString)</code> and returns APIShortDescriptions
	 * @param searchString semi-colon separated search criterias
	 * @return all API Summarys matching semi-colon separated search criterias
	 * @see com.nokia.s60tools.apiquery.ui.views.main.search.IQueryDefCompositeListener#queryStarted(java.lang.String)
	 */
	public Collection<APIShortDescription> activeProjectQueryStarted(
			String searchString) {
		queryStarted(searchString, false);
		return resultColl.getSearchResults();
	}

	/**
	 * update the results page
	 * 
	 * @param syncDisp :
	 *            for webquery jobs
	 * @param d
	 * @param queryString
	 *            :Search string to be shown in the query serach string text box
	 */
	private void updateResults(boolean syncDisp, final Display d,
			final String queryString) {

	
		// Did we succeed?
		if (resultColl != null) {

			if (syncDisp) {// for non UI thread
				Runnable updateCompositeThread = new Runnable() {
					public void run() {
						// Setting queried data into UI control.
						updateResultComposite();
						// update the search string text box for this search
						//queryDefControl.setQueryString(queryString);

					}
				};

				// update the result composite page
				d.syncExec(updateCompositeThread);

			} else {
				// Setting queried data into UI control.
				updateResultComposite();
			}
		}

		if (resultColl.hasErrors()) {
		
			// Something failed
			// If there was some errors, but after all, there was also some
			// results, giving warning dialog
			// but if everything failed, then gbvgiving error dialog. So
			// then is easier to make different if e.g. only one server was
			// down but others works OK.
			int messageType = resultColl.getSearchResults().isEmpty() ? APIQueryConsole.MSG_ERROR
					: APIQueryConsole.MSG_WARNING;
			int messageTypeIcon = resultColl.getSearchResults().isEmpty() ? SWT.ICON_ERROR
					: SWT.ICON_WARNING;

			final String errMsg = Messages
					.getString("SearchTabUIFieldsMediator.APIListQueryFailed_ErrMsg") + "\n" + resultColl.getErrorMessages(); //$NON-NLS-1$ //$NON-NLS-2$
			APIQueryConsole.getInstance().println(errMsg, messageType);
			if (d == null) {
				APIQueryMessageBox mbox = new APIQueryMessageBox(errMsg,
						messageTypeIcon | SWT.OK);
				mbox.open();
			} else {
				Runnable errormsg = new Runnable() {
					public void run() {
						MessageDialog.openError(d.getActiveShell(),
								"API query", errMsg);
					}
				};

				// update the result composite page
				d.syncExec(errormsg);

			}

		}

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.search.IQueryDefCompositeListener#queryStarted(java.lang.String)
	 */
	public void queryStarted(String searchString, boolean useExactMatch) {

		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"* Search started: " + searchString); //$NON-NLS-1$
		int queryType = queryTypeControl.getSelectedQueryType();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"** Query type as int: " + queryType); //$NON-NLS-1$
		DbgUtility
				.println(
						DbgUtility.PRIORITY_OPERATION,
						"** Query type descr: " + APIQueryParameters.getDescriptionForQueryType(queryType)); //$NON-NLS-1$	

		params = new APIQueryParameters(queryType, searchString);
		params.setDetailsMentToAddToDescriptions(!queryResultsControl
				.isShowOnlyAPINamesSelected());
		params.setExactMatchInUse(useExactMatch);

		ISearchMethodExtension currSelExt = UserSettings.getInstance()
				.getCurrentlySelectedSearchMethod();

		// Showing busy cursor during operation
		Display d = APIQueryPlugin.getCurrentlyActiveWbWindowShell()
				.getDisplay();
		if (currSelExt.isAsyncQueryPreferred()) {
			// run as the back ground job only if it's a webquery

			synchronized (prevQueryJobObjLock) {
				//any query running
				if (prevWebSerachJob != null) {
					boolean cancel = true;
					if (!progressMonitor.isCanceled()) {
						//if job not cancelled yet,check if user wants to continue with new query
						cancel = MessageDialog.openQuestion(APIQueryPlugin
								.getCurrentlyActiveWbWindowShell(),
								"Query Cancel",
								"Do you want to cancel pervious apiquery for :"
										+ prevQueryString
										+ " and continue with new search?");
					}

					if (cancel) {

						//already cancelled?
						if (!progressMonitor.isCanceled()) {
							if (progressMonitor != null)
								progressMonitor
										.setTaskName("In cancellation...........");
							if (prevWebSerachJob != null)
								prevWebSerachJob.cancel();
						}
						//set the job to null
						prevWebSerachJob = null;

						MainView.enablePropTabcontents(true);
					}

					else {
						// end the new search
						return;
					}

				}
			}

			prevWebSerachJob = new WebQueryJob("Web Query for search string: "
					+ searchString, searchString);

			prevWebSerachJob.addJobChangeListener(new WebQueryJobListener(d,
					searchString));

			prevWebSerachJob.setPriority(Job.SHORT);
			prevWebSerachJob.setUser(true);
			prevWebSerachJob.schedule(); // start as soon as possible

		}

		else {
			Runnable performAPIDetailsQueryRunnable = new Runnable() {
				public void run() {
					performQuery(params);
				}
			};
			// run with a busy indicator
			BusyIndicator.showWhile(d, performAPIDetailsQueryRunnable);
			updateResults(false, d, null);
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.search.IQueryActionListener#queryModified(java.lang.String)
	 */
	public void queryModified(String searchString) {
		// Currently we do not do anything when query string is modified.
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.settings.IUserSettingsListener#userSettingsChanged()
	 */
	public void userSettingsChanged() {
		// Settings have changed which might mean that 
		// the user has changed the currently used query type.
		// => Therefore resetting previous query results
		queryResultsControl.clear();
	}

	/**
	 * Releases all the listeners. Called when parent UI composite
	 * is disposed. If we have listeners in action after UI components
	 * are disposed we will get "Widget is disposed" error.
	 */
	public void releaseListeners() {
		UserSettings.getInstance().removeUserSettingListener(this);
		queryDefControl.setCompositeListener(null);
		queryResultsControl.setResultSettingsListener(null);
	}

	/**
	 * Updates results composite 
	 */
	private void updateResultComposite() {
		// Clearing old results
		queryResultsControl.clear();

		//Updating results composite
		queryResultsControl.updateAPIShortDescriptionData(resultColl
				.getSearchResults(), params);

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.search.IResultSettingsListener#resultSettingsChanged()
	 */
	public void resultSettingsChanged() {
		//When request comes outside of this class, show only API names selection must be updated
		params.setDetailsMentToAddToDescriptions(!queryResultsControl
				.isShowOnlyAPINamesSelected());
		updateResultComposite();
	}
	
	/**
	 * Performs the according the given parameters and updates the UI components
	 * after the query.
	 * 
	 * @param params
	 *            Parameters for the query.
	 * @param errInfo
	 *            Error info class for returning error description in case if an
	 *            error.
	 */
	private void performQuery(APIQueryParameters params,IProgressMonitor progressmonitor) throws JobCancelledByUserException{
		try { // disable datasource selection option from properites page
			MainView.enablePropTabcontents(false);
			resultColl = null; // Resetting previous searches.

			ISearchMethodExtension currSelExt = UserSettings.getInstance()
					.getCurrentlySelectedSearchMethod();
		if (	currSelExt.isAsyncQueryPreferred())
		{
			
		Object obj =	UserSettings.getInstance()
			.getCurrentlySelectedSearchMethod();
		
		resultColl =  ((AbstractSearchMethodExtension)obj).runAPIQuery(params, progressMonitor);
		 
		
		   
		}
		else
		{
		   
			resultColl = currSelExt.runAPIQuery(params);
		}

			// enable dataSelection source feature
		} finally {

			MainView.enablePropTabcontents(true);
		}
	}
	
	
	
}
