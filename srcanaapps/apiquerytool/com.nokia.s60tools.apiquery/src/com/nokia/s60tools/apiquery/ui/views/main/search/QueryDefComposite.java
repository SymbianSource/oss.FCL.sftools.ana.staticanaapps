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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.apiquery.APIQueryHelpContextIDs;
import com.nokia.s60tools.apiquery.shared.plugin.APIQueryPlugin;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * UI composite that shows the search field and Search button in Search tab.
 * 
 */
class QueryDefComposite extends AbstractUiFractionComposite implements
		SelectionListener, ModifyListener {

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 2;

	private static String CACHE_NAME = "Cache.txt";

	/**
	 * Group giving common layout and containing controls-
	 */
	private Group searchStringDefinitionGroup;

	/**
	 * Search string entering field.
	 */
	private Text searchStringTxtField;

	/**
	 * Action button for starting the query.
	 */
	private Button runQueryBtn;

	/**
	 * 
	 */
	private IQueryDefCompositeListener queryActionListener = null;
	static DataOutputStream dos;
	public static String cacheFileName = APIQueryPlugin.getPluginWorkspacePath() + File.separator + CACHE_NAME;
	public static ArrayList<String> wordsCaches = new ArrayList<String>();
	public AutoCompleteField auto;
	
	
	/**
	 * Constructor.
	 * 
	 * @param parentComposite
	 *            Parent composite for the created composite.
	 */
	public QueryDefComposite(Composite parentComposite) {
		super(parentComposite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayout
	 * ()
	 */
	protected Layout createLayout() {
		return new GridLayout(COLUMN_COUNT, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#
	 * createLayoutData()
	 */
	protected Object createLayoutData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}

	/**
	 * Set context sensitive help ids to components that can have focus
	 * 
	 */
	private void setContextSensitiveHelpIds() {
		try {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(
					searchStringTxtField,
					APIQueryHelpContextIDs.API_QUERY_HELP_SEARCH_TAB);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(runQueryBtn,
					APIQueryHelpContextIDs.API_QUERY_HELP_SEARCH_TAB);
		} catch (Exception e) {
			e.printStackTrace();
			APIQueryConsole
					.getInstance()
					.println(
							Messages
									.getString("QueryDefComposite.Context_ErrMsg") + e, APIQueryConsole.MSG_ERROR); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls
	 * ()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		searchStringDefinitionGroup = new Group(this, SWT.SHADOW_NONE);
		searchStringDefinitionGroup.setText(Messages
				.getString("QueryDefComposite.SearchString_Msg")); //$NON-NLS-1$

		GridLayout gdl2 = new GridLayout(COLUMN_COUNT, false);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);

		searchStringDefinitionGroup.setLayout(gdl2);
		searchStringDefinitionGroup.setLayoutData(gd2);

		final int textFieldStyleBits = SWT.LEFT | SWT.SINGLE | SWT.BACKGROUND
				| SWT.BORDER;
		searchStringTxtField = new Text(searchStringDefinitionGroup,
				textFieldStyleBits);
		searchStringTxtField.setLayoutData((new GridData(
				GridData.FILL_HORIZONTAL)));
		searchStringTxtField.setEditable(true);
		searchStringTxtField.addModifyListener(this);
		searchStringTxtField.addSelectionListener(this);

		runQueryBtn = new Button(searchStringDefinitionGroup, SWT.PUSH);
		runQueryBtn.setText(Messages.getString("QueryDefComposite.Search_Msg")); //$NON-NLS-1$
		runQueryBtn.addSelectionListener(this);

		// Query enabled by default
		enableQuery();

		// if it really exists
		if (!isFileExists(cacheFileName)) {
			writeToFile(cacheFileName, " \n", false);
		} else {// load the entries into the array list
			fileToHashTable(cacheFileName, wordsCaches);
		}

		auto = new AutoCompleteField(searchStringTxtField, new TextContentAdapter(), getKeys());

		setContextSensitiveHelpIds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		if (event.widget == searchStringTxtField) {
			// <code>widgetDefaultSelected</code> is typically called
			// when ENTER is pressed in a single-line text.
			if (queryActionListener != null) {
				queryActionListener.queryStarted(searchStringTxtField.getText(), false);
				String value = searchStringTxtField.getText();
				putKey(value);
				auto.setProposals(getKeys());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget == runQueryBtn) {
			if (queryActionListener != null) {
				queryActionListener.queryStarted(searchStringTxtField.getText(), false);
				String value = searchStringTxtField.getText();
				putKey(value);
				auto.setProposals(getKeys());
			}
			/*else if (event.widget == searchStringTxtField) {
				// <code>widgetDefaultSelected</code> is typically called
				// when ENTER is pressed in a single-line text.
				if (queryActionListener != null) {
					String value = searchStringTxtField.getText();
					queryActionListener.queryStarted(
							searchStringTxtField.getText(), false);
					putKey(wordsCaches, value, cacheFileName);
					//auto.setProposals(getKeys(wordsCaches));
				}
			}*/

		}
	}

	/**
	 * Adds listener that listens for query actions.
	 * 
	 * @param queryActionListener
	 *            Query action listener.
	 */
	public void setCompositeListener(
			IQueryDefCompositeListener queryActionListener) {
		this.queryActionListener = queryActionListener;
	}

	/**
	 * Disables query button.
	 */
	public void disableQuery() {
		runQueryBtn.setEnabled(false);
	}

	/**
	 * Enables query button.
	 */
	public void enableQuery() {
		runQueryBtn.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events
	 * .ModifyEvent)
	 */
	public void modifyText(ModifyEvent arg0) {
		if (queryActionListener != null) {
			queryActionListener.queryModified(searchStringTxtField.getText());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,"-- Dispose() --> " + getClass().getName());
		searchStringDefinitionGroup.dispose();
		searchStringTxtField.dispose();
		runQueryBtn.dispose();
	}

	/**
	 * Sets the query string.
	 * 
	 * @param queryString
	 *            Query string to be set.
	 */
	public void setQueryString(String queryString) {
		searchStringTxtField.setText(queryString);
	}

	public boolean writeToFile(String fileName, String dataLine, boolean isAppendMode) {

		try {
			File outFile = new File(fileName);
			if (isAppendMode) {
				dos = new DataOutputStream(new FileOutputStream(fileName, true));
			} else {
				dos = new DataOutputStream(new FileOutputStream(outFile));
			}

			dos.writeBytes(dataLine);
			dos.close();
		} catch (FileNotFoundException ex) {
			return (false);
		} catch (IOException ex) {
			return (false);
		}
		return (true);

	}

	public boolean isFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		return file.delete();
	}

	public void fileToHashTable(String fileName, ArrayList<String> arr) {
		String inputLine;
		try {
			File inFile = new File(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			while ((inputLine = br.readLine()) != null) {
				if(wordsCaches.indexOf(inputLine.trim())==-1)
					wordsCaches.add(inputLine.trim());
			}
			br.close();
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// puts key to both file and hash table
	public void putKey(String value) {
		if (wordsCaches.contains(value))
			return;
		wordsCaches.add(value.trim());
		writeToFile(cacheFileName, value.trim() + "\n", true);
	}

	// readhashTableKeys
	public String[] getKeys() {
		return wordsCaches.toArray(new String[wordsCaches.size()]);
	}
}
