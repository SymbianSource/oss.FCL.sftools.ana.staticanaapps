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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.apiquery.APIQueryHelpContextIDs;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.QueryErrorInfo;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.preferences.APIQueryPreferences;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.debug.DbgUtility;


/**
 * UI composite containing controls that show the query results.
 */
class QueryResultsComposite extends AbstractUiFractionComposite {

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 1;

	/**
	 * Search results label text to be shown for user.
	 */
	private static final String SEARCH_RESULTS_LABEL_TEXT = Messages
			.getString("QueryResultsComposite.SearchResults_Msg"); //$NON-NLS-1$

	private static final String SEARCH_RESULTS_LABEL_API_TEXT = Messages
			.getString("QueryResultsComposite.APIs_Msg"); //$NON-NLS-1$

	/**
	 * Preferred size for the search result list control. Targeted currently to
	 * show 10 items without need for scrolling.
	 */
	private final int SEARCH_RESULT_FIELD_HEIGHT = 150;

	private TableViewer tableViewer;

	private APIDataTaskList taskList = null;

	// private APIShortDescription shortDescription = null;

	//
	// Setting static data section for HTML data shown in the browser component
	//
	private final String HTML_DATA_BODY_STYLE = "body {margin-left: 0px;margin-right: 0px;margin-top: 0px;margin-bottom: 0px;}"; //$NON-NLS-1$

	private final String HTML_DATA_TABLE_STYLE = "table{border-collapse: collapse; font-family: Tahoma; font-size: 11px}"; //$NON-NLS-1$

	private final String HTML_DATA_STYLE = "<style>" + HTML_DATA_BODY_STYLE + HTML_DATA_TABLE_STYLE + "</style>"; //$NON-NLS-1$ //$NON-NLS-2$

	private final String HTML_DATA_HEAD = "<head>" + HTML_DATA_STYLE + "</head>"; //$NON-NLS-1$ //$NON-NLS-2$

	private final String HTML_DATA_HEADER = "<html>" + HTML_DATA_HEAD + "<body>"; //$NON-NLS-1$ //$NON-NLS-2$

	private final String HTML_DATA_FOOTER = "</body></html>"; //$NON-NLS-1$

	private final String HTML_DATA_TABLE_START = "<table border=1 width=\"100%\" ><colgroup span=\"2\"><col width=\"30%\"></col><col width=\"70%\"></col></colgroup>"; //$NON-NLS-1$

	private final String HTML_DATA_TABLE_END = "</table>"; //$NON-NLS-1$

	private final String EMPTY_HTML_DOC = HTML_DATA_HEADER + HTML_DATA_FOOTER;

	private static final String COMMA = ",";//$NON-NLS-1$

	// boolean sourceNeeded = false;
	// boolean onlyAPIInfo = false;
	// int columns = 0;

	int apiIndex = -1;

	int otherFiledIndex = -1;

	int sourceIndex = -1;

	/**
	 * Listener for changing how the results is shown
	 */
	private IResultSettingsListener resultSettingsListener;

	/**
	 * List item for showing list of APIs
	 */
	// private List searchResultList;
	/**
	 * Button to change how results are shown
	 */
	private Button showOnlyAPINamesBtn;

	/**
	 * Map that stores indexes for searchResults data by searchResultList name
	 * (matches UI text to {@link APIShortDescription} id).
	 */
	private Map<String, Integer> searchResultIndexes;

	/**
	 * Label item to be updated with API count.
	 */
	Label searchResultsLabel;

	/**
	 * Summary data to be shown in the API list.
	 */
	private APIShortDescription[] searchResultData;

	/**
	 * Details data to be shown in browser control.
	 */
	private APIDetails details;

	static int TABLE_STYLE_BITS = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL
			| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.RESIZE;

	/**
	 * Browser control for showing API details data.
	 */
	private Browser browserControl;

	private APIQueryParameters params;

	private Table table;
	

	String[] src = { "API NAME", "SUBSYSTEM NAME", " DLL NAME", "LIB NAME",
			"HEADER NAME", "CRPS KEY NAME", " ", "SOURCE" };

	/**
	 * Constructor.
	 * 
	 * @param parentComposite
	 *            Parent composite for the created composite.
	 */
	public QueryResultsComposite(Composite parentComposite) {
		super(parentComposite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayout()
	 */
	protected Layout createLayout() {
		return new GridLayout(COLUMN_COUNT, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayoutData()
	 */
	protected Object createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}

	/**
	 * Set context sensitive help ids to components that can have focus
	 * 
	 */
	private void setContextSensitiveHelpIds() {

		PlatformUI.getWorkbench().getHelpSystem().setHelp(showOnlyAPINamesBtn,
				APIQueryHelpContextIDs.API_QUERY_HELP_SEARCH_TAB);
	}

	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);

	}

	private void createTable(Composite parent, int numberOfColumns,
			String[] columnNames) {

		table = new Table(parent, TABLE_STYLE_BITS);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = SEARCH_RESULT_FIELD_HEIGHT;
		table.setLayoutData(gridData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		table.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				getAPIDetailsData();
			}

		});

		int size = 150;
		for (int i = 0; i < numberOfColumns; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT, i);
			column.setText(columnNames[i]);
			column.setWidth(size);
			size = size - 50;
			final int j = i;
			column.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					TableColumn sortedColumn = table.getSortColumn();
					TableColumn currentSelected = (TableColumn)event.widget;
					
					int dir = table.getSortDirection();
					if(sortedColumn == currentSelected){
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					}
					else{
						table.setSortColumn(currentSelected);
						dir = SWT.UP;
					}
					
					if(currentSelected == table.getColumn(j))
					{
						tableViewer.setSorter(new DataSorter(dir, j));
					}
					
					table.setSortDirection(dir);
				
				}
			});
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		//
		// Creating label and list for showing APIs found based on the query.
		//
		searchResultsLabel = new Label(this, SWT.HORIZONTAL | SWT.LEFT);
		searchResultsLabel.setText(SEARCH_RESULTS_LABEL_TEXT + ":"); //$NON-NLS-1$

		// final int listBoxStyleBits = SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL;
		// searchResultList = new List(this, listBoxStyleBits);
		createTable(this, 3, new String[] { " ", " ", " " });
		createTableViewer();

		searchResultIndexes = new HashMap<String, Integer>();
		GridData listData1 = new GridData(GridData.FILL_HORIZONTAL);
		listData1.heightHint = SEARCH_RESULT_FIELD_HEIGHT;
		
		//
		// Creating button to change how the results are shown
		//
		String desc = Messages
				.getString("QueryResultsComposite.ShowOnlyAPINames_MSg"); //$NON-NLS-1$ "Show only API names";
		showOnlyAPINamesBtn = new Button(this, SWT.CHECK);
		showOnlyAPINamesBtn.setText(desc);

		showOnlyAPINamesBtn.setSelection(APIQueryPreferences
				.getShowOnlyAPINames());
		showOnlyAPINamesBtn
				.addSelectionListener(new ShowOnlyAPINamesSelectionListener());

		//
		// Creating label and browser control for showing API details for the
		// selected API.
		//
		Label apiDetailsLabel = new Label(this, SWT.HORIZONTAL | SWT.LEFT);
		apiDetailsLabel.setText(Messages
				.getString("QueryResultsComposite.APIDetails_MSg")); //$NON-NLS-1$

		// Browser control does not seem to support SWT.BORDER style bit.
		// Therefore need to add separate border component.
		Composite browserBorder = new Composite(this, SWT.BORDER);
		FillLayout borderLayout = new FillLayout(SWT.VERTICAL | SWT.HORIZONTAL);
		browserBorder.setLayout(borderLayout);
		browserBorder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Adding browser control
		browserControl = new Browser(browserBorder, SWT.NONE);

		browserControl.addLocationListener(new LocationListener() {

			public void changed(LocationEvent arg0) {

			}

			public void changing(LocationEvent arg0) {

				// //get the setting
				final ISearchMethodExtension serachMethod = UserSettings
						.getInstance().getCurrentlySelectedSearchMethod();

				if (!arg0.location.contains("about:blank")) {

					arg0.doit = false;
					final String headerName = arg0.location.substring(2,
							arg0.location.length()).trim();
					String apidetailsName = "Name";
					if (serachMethod.isAsyncQueryPreferred())
						apidetailsName = "API Name";
					APIDetailField det = details.getDetail(apidetailsName);

					serachMethod.openHeaderFile(headerName, det.getValue()
							.trim());

				}
			}
		});

		setContextSensitiveHelpIds();
	}

	/**
	 * Class that listens button state changes and updates preferences and
	 * results composite
	 */
	private class ShowOnlyAPINamesSelectionListener implements
			SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {

			// Setting selection preferences
			// APIQueryPreferences.setShowOnlyAPINames(showOnlyAPINamesBtn.getSelection());
			// Update view, do not get information again from data source, but
			// just update data
			resultSettingsListener.resultSettingsChanged();

		}

	}

	/**
	 * Converts API details into html-data.
	 * 
	 * @param details
	 *            API details object to be converted.
	 * @return
	 */
	private String buildHtmlDataFromDetails(APIDetails details) {
		StringBuffer htmlTextBuf = new StringBuffer();

		htmlTextBuf.append(HTML_DATA_HEADER);

		// Appending table start
		htmlTextBuf.append(HTML_DATA_TABLE_START);

		// Converting API details into html data
		for (APIDetailField detailField : details) {
			// System.out.println("datailValue" + detailField.getValue() + "
			// getdesc" + detailField.getDescription());
			convertSingleDetailField(detailField, htmlTextBuf);
		}

		// Appending table end
		htmlTextBuf.append(HTML_DATA_TABLE_END);
		htmlTextBuf.append(HTML_DATA_FOOTER);
		

		return htmlTextBuf.toString();
	}

	/**
	 * @param field
	 * @param htmlTextBuf
	 */
	private void convertSingleDetailField(APIDetailField field,
			StringBuffer htmlTextBuf) {
		String desc = field.getDescription();
		String val = field.getValue();
		// System.out.println("values" +val);

		htmlTextBuf.append("<tr><td><b>"); //$NON-NLS-1$
		htmlTextBuf.append(desc);
		htmlTextBuf.append(":</b></td><td>"); //$NON-NLS-1$

		boolean isSelectedSearchType = getIsSelectedSearchType(desc);
		// String what user was set to be searched
		String[] serachStrings = params.getSearchString().split(";");
		String searchString = params.getSearchString();
		// Checking if empty string was searched (empty search lists all)
		boolean isEmptySearchString = false;
		if (searchString == null || searchString.trim().equals("")) {//$NON-NLS-1$
			isEmptySearchString = true;
		}
		boolean isValueContainingSearchString = false;

		for (int i = 0; i < serachStrings.length; i++) {
			String temp = serachStrings[i].trim();
			isValueContainingSearchString = isValueContainingSearchString(val,
					temp);
			if (isValueContainingSearchString) {
				searchString = temp;
				break;
			}
		}

		// get the setting
		ISearchMethodExtension serachMethod = UserSettings.getInstance()
				.getCurrentlySelectedSearchMethod();

		// Checking search criteria and set matching search string as red when
		// found
		if (!isEmptySearchString && isSelectedSearchType
				&& isValueContainingSearchString) {
			String[] vals = val.split(COMMA);
			for (int i = 0; i < vals.length; i++) {

				if (vals[i].toLowerCase().indexOf(searchString.toLowerCase()) != -1) {
					String s = "<font color=\"red\">" + vals[i] + "</font>";
					if (params.getQueryType() == APIQueryParameters.QUERY_BY_HEADER_NAME
							&& serachMethod.serachHeaderLinkEnable())
						s = "<a href=\"file://" + vals[i] + "\">" + s + "</a>";
					//System.out.println("link" + s);
					htmlTextBuf.append(s);//$NON-NLS-1$ //$NON-NLS-2$
				} else {
					htmlTextBuf.append(vals[i]);
				}
				if (i < (vals.length - 1)) {
					htmlTextBuf.append(COMMA);
				}
			}
		} else {
			htmlTextBuf.append(val);
		}

		htmlTextBuf.append("</td></tr>"); //$NON-NLS-1$

	}

	/**
	 * Check if value contains search string (case in sensitive)
	 * 
	 * @param val
	 * @param searchString
	 * @return <code>true</code> if val contains searchString, false
	 *         othrewise.
	 */
	private boolean isValueContainingSearchString(String val,
			String searchString) {
		return val != null
				&& val.toLowerCase().contains(searchString.toLowerCase());
	}

	/**
	 * Check if given string is same as selected search type
	 * 
	 * @param desc
	 * @return <code>true</code> if is, <code>false</code> otherwise.
	 */
	private boolean getIsSelectedSearchType(String desc) {
		// UI name in API Details for selected search type
		String searchTypeInReport = UserSettings.getInstance()
				.getCurrentlySelectedSearchMethod()
				.getAPIDetailNameInDetailsByQueryType(params.getQueryType());
		// Check if this value is same type as users search criteria
		boolean isSelectedSearchType = searchTypeInReport
				.equalsIgnoreCase(desc);
		return isSelectedSearchType;
	}

	/**
	 * Updates summary field with the queried data. If only one result was
	 * found, API Details for that API is shown.
	 * 
	 * @param summaryDataColl
	 *            API summary data to be shown.
	 */
	public void updateAPIShortDescriptionData(
			Collection<APIShortDescription> summaryDataColl,
			APIQueryParameters params) {

	
		// boolean sourceNeeded = false;
		boolean onlyAPIInfo = false;

		apiIndex = -1;
		otherFiledIndex = -1;
		sourceIndex = -1;

		if (!params.isDetailsMentToAddToDescriptions()
				|| params.getQueryType() == APIQueryParameters.QUERY_BY_API_NAME) {
			onlyAPIInfo = true;
		}

		int columns = 1;

		if (!onlyAPIInfo)
			++columns;

		if (APIQueryPreferences.getShowDataSourceInResults())
			columns++;

		int[] indicies = new int[3];

		indicies[2] = src.length - 2;
		
		if (columns == 3) {
			indicies[0] = (params.getQueryType() - 1);
			indicies[1] = 0;
			indicies[2] = src.length - 1;
			otherFiledIndex = 0;
			apiIndex = 1;
			sourceIndex = 2;

		} else if (columns == 2) {
			if (APIQueryPreferences.getShowDataSourceInResults() && onlyAPIInfo) {

				indicies[0] = 0;
				indicies[1] = src.length - 1;

				apiIndex = 0;
				sourceIndex = 1;
			} else {
				indicies[0] = params.getQueryType() - 1;
				indicies[1] = 0;
				otherFiledIndex = 0;
				apiIndex = 1;
			}

		}

		else {
			indicies[0] = 0;
			indicies[1] = src.length - 2;	
			apiIndex = 0;
		}

		
		TableColumn[] tableColumns = table.getColumns();
		for (int i = 0; i < tableColumns.length; i++) {
			tableColumns[i].setText(src[indicies[i]]);

		}

		// add the contents to table

		this.params = params;
		// Clearing...
		// searchResultList.removeAll();
		searchResultIndexes.clear();

		// Storing the data
		searchResultData = summaryDataColl.toArray(new APIShortDescription[0]);
		taskList = new APIDataTaskList();

		// Populating the list
		for (int i = 0; i < searchResultData.length; i++) {

			APIShortDescription summary = searchResultData[i];
			String shortUIText = summary.getName();

			// Checking if Data source should add to item
			if (APIQueryPreferences.getShowDataSourceInResults()) {

				shortUIText += "   [" + summary.getSourceDescription() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Checking from preferences should we show only API Names or also
			// queried items
			// if we want only API Names, just adding text to list. When API
			// Name is search criteria, only API Names will be added anyway.
			if (!params.isDetailsMentToAddToDescriptions()
					|| params.getQueryType() == APIQueryParameters.QUERY_BY_API_NAME) {

				taskList
						.addTask(new APITask(
								new String[] {
										summary.getName(),
										(APIQueryPreferences
												.getShowDataSourceInResults()) ? summary
												.getSourceDescription()
												: " ", "  " }));

				// searchResultList.add(shortUIText); //$NON-NLS-1$
				// //$NON-NLS-2$
				searchResultIndexes.put(shortUIText, new Integer(i));
			}
			// If details for query type is ment to add for list, getting items
			// that match for query
			else {
				String[] queriedMatchingItems = getQueriedMatchingItemNames(
						summary, params.getSearchString());
				if (queriedMatchingItems != null) {
					for (int j = 0; j < queriedMatchingItems.length; j++) {
						taskList
								.addTask(new APITask(
										new String[] {
												queriedMatchingItems[j],
												summary.getName(),
												(APIQueryPreferences
														.getShowDataSourceInResults()) ? summary
														.getSourceDescription()
														: " " }));
						String longUIText = "[" + queriedMatchingItems[j] + "] in " + shortUIText; //$NON-NLS-1$ //$NON-NLS-2$
						// searchResultList.add(longUIText);
						searchResultIndexes.put(longUIText, new Integer(i));

					}
				}
			}// else
		} // if

		tableViewer.setContentProvider(new APIContentProvider());
		tableViewer.setLabelProvider(new DataLabelProvider());

		// The input for the table viewer is the instance of ExampleTaskList

		tableViewer.setInput(taskList);

		searchResultsLabel.setText(SEARCH_RESULTS_LABEL_TEXT
				+ " (" + Messages.getString("QueryResultsComposite.Found_Msg") //$NON-NLS-1$ //$NON-NLS-2$
				+ table.getItemCount() + " " //$NON-NLS-1$
				+ SEARCH_RESULTS_LABEL_API_TEXT + "):"); //$NON-NLS-1$
		searchResultsLabel.pack(); // Adjusting the label size to match new

		// If we have only one result, that will be selected automatically
		if (table.getItemCount() == 1) {
			table.setSelection(0);
			getAPIDetailsData();
		}

		// update the serachstring for the serached query

	}

	/**
	 * @param summary
	 * @param searchString
	 * @return list of
	 */
	private String[] getQueriedMatchingItemNames(APIShortDescription summary,
			String searchString) {

		String[] ars = searchString.split(";");

		ArrayList<String> results = new ArrayList<String>();
		if (UserSettings.getInstance().getCurrentlySelectedSearchMethod()
				.isAsyncQueryPreferred()) {

			ArrayList<String> data = summary.getSearchedData();
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < ars.length; j++) {
					if (data.get(i).toLowerCase().contains(
							ars[j].trim().toLowerCase()))
						results.add(data.get(i).trim());
				}

			}
			return (String[]) results.toArray(new String[0]);
		}

		// Details can be allreay stored to summary, if so, we don't need to get
		// details again
		APIDetails det = null;
		if (summary.hasAPIDetails()) {
			det = summary.getAPIDetails();
		} else {
			try {
				// Get currenty selected search method extension
				// get api details
				det = UserSettings.getInstance()
						.getCurrentlySelectedSearchMethod().getAPIDetails(
								summary);
			} catch (QueryOperationFailedException e) {
				APIQueryConsole
						.getInstance()
						.println(
								Messages
										.getString("QueryResultsComposite.UnableToGetAPIDetails_ErrMsg_Part1") + summary.getName() + Messages.getString("QueryResultsComposite.UnableToGetAPIDetails_ErrMsg_Part2") + e, APIQueryConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
		}

		// ArrayList<String> results = new ArrayList<String>();

		// Converting API details into fields and checking matching items by API
		// Query params
		for (APIDetailField field : det) {
			// Checking if there matching items
			String desc = field.getDescription();
			String val = field.getValue();
			boolean isSelectedSearchType = getIsSelectedSearchType(desc);
			if (isSelectedSearchType) {
				String[] vals = val.split(COMMA);
				for (int i = 0; i < vals.length; i++) {
					for (int j = 0; j < ars.length; j++) {
						boolean isValueContainingSearchString = isValueContainingSearchString(
								vals[i], ars[j].trim());
						// Add matching item to results
						if (isValueContainingSearchString) {
							// System.out.println("values : "+ vals[i]);
							results.add(vals[i].trim());
						}
					}
				}
			}

		}
		return (String[]) results.toArray(new String[0]);

	}

	/**
	 * Gets API details for the API selected from the API list. Shows busy
	 * cursor to user during the operation.
	 */
	private void getAPIDetailsData() {

		final QueryErrorInfo errInfo = new QueryErrorInfo();

		Runnable performAPIDetailsQueryRunnable = new Runnable() {
			public void run() {
				performAPIDetailsQuery(errInfo);
			}
		};

		// Showing busy cursor during operation
		Display d = APIQueryPlugin.getCurrentlyActiveWbWindowShell()
				.getDisplay();
		BusyIndicator.showWhile(d, performAPIDetailsQueryRunnable);

		// Did we succeed?
		if (details != null) {
			// Populating browser control with the HTML formatted data.
			browserControl.setText(buildHtmlDataFromDetails(details));
		} else {
			// Something failed
			String errMsg = Messages
					.getString("QueryResultsComposite.APIDetailsQueryFailde_ErrMsg") + errInfo.getErrorDescription(); //$NON-NLS-1$
			APIQueryConsole.getInstance().println(errMsg,
					APIQueryConsole.MSG_ERROR);
			APIQueryMessageBox mbox = new APIQueryMessageBox(errMsg,
					SWT.ICON_ERROR | SWT.OK);
			mbox.open();
		}
	}

	/**
	 * Performs the actual API details query.
	 * 
	 * @param errInfo
	 *            Error info to be filled in error situation.
	 */
	private void performAPIDetailsQuery(QueryErrorInfo errInfo) {

		// details = null; // Resetting previous searches
		// check if the details already in localstorage
		// int currSelIndex = searchResultList.getSelectionIndex();

		// details=(APIDetails)apiDetailsStorage.elementAt(currSelIndex);
		// if(details!=null)return;

		// Table data

		TableItem[] ar = table.getSelection();
		String shortUIText = ar[0].getText(apiIndex).trim();
		if (sourceIndex != -1)
			shortUIText += "   [" + ar[0].getText(sourceIndex).trim() + "]";
		if (otherFiledIndex != -1)
			shortUIText = "[" + ar[0].getText(0).trim() + "] in " + shortUIText;

		// //////

		// String s = searchResultList.getItems()[currSelIndex];
		// System.out.println("Text in secrh res infices : " + s);
		// Not getting ID from list directly, because same API can be there
		// multiple times, ID is taken from indexes list
		// int currShortDescIndex = searchResultIndexes.get(searchResultList
		// .getItems()[currSelIndex]);

		int currShortDescIndex = searchResultIndexes.get(shortUIText);

		APIShortDescription summary = searchResultData[currShortDescIndex];
		// shortDescription = summary;

		// API Details can be already stored in summary, if so, we don't need
		// get details again
		if (summary.hasAPIDetails()) {
			details = summary.getAPIDetails();
		}
		// Else get api details by using selected search method extension
		else {
			ISearchMethodExtension currSelExt = UserSettings.getInstance()
					.getCurrentlySelectedSearchMethod();
			try {
				details = currSelExt.getAPIDetails(summary);
				summary.setAPIDetails(details);
			} catch (QueryOperationFailedException e) {
				errInfo.setErrorDescription(e.getMessage());
			}
		}

	}

	/**
	 * Clears all the previously shown data.
	 */
	public void clear() {
		// Clearing summary data
		// searchResultList.removeAll();
		table.removeAll();
		// taskList.removeTaksList();
		// tableViewer.setInput(taskList);
		// Clearing found APIs count
		searchResultsLabel.setText(SEARCH_RESULTS_LABEL_TEXT + ":"); //$NON-NLS-1$
		// Clearing details data
		browserControl.setText(EMPTY_HTML_DOC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"-- Dispose() --> " + getClass().getName()); //$NON-NLS-1$
	}

	/**
	 * Check if show only api names is selected
	 * 
	 * @return <code>true</code> if Show only API names is selected
	 *         <code>false</code> otherwise.
	 */
	public boolean isShowOnlyAPINamesSelected() {
		return showOnlyAPINamesBtn.getSelection();
	}

	/**
	 * Adds listener that listens for result setting changes.
	 * 
	 * @param searchTabUIFieldsMediator
	 *            Result settings changes listener
	 */
	public void setResultSettingsListener(
			IResultSettingsListener resultSettingsListener) {
		this.resultSettingsListener = resultSettingsListener;
	}

	/**
	 * InnerClass that acts as a proxy for the ExampleTaskList providing content
	 * for the Table. It implements the ITaskListViewer interface since it must
	 * register changeListeners with the ExampleTaskList
	 */
	class APIContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}

		public void dispose() {

		}

		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return taskList.getTasks().toArray();
		}

	}

}
