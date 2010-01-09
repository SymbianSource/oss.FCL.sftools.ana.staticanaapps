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

package com.nokia.s60tools.apiquery.cache.searchmethod.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.apiquery.cache.CacheHelpContextIDs;
import com.nokia.s60tools.apiquery.cache.configuration.CacheEntry;
import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.core.job.SeekMetaXMLFilesJob;
import com.nokia.s60tools.apiquery.cache.core.job.UpdateSDKSelectionJob;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.cache.util.SDKFinder;
import com.nokia.s60tools.apiquery.cache.util.SDKUtil;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.settings.UserSettingListenerNotificator;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.sdk.SdkEnvInfomationResolveFailureException;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.sdk.SdkManager;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

public class LocalCacheUIComposite extends AbstractUiFractionComposite
		implements IJobChangeListener {

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 1;

	public static CCombo SDKCombo;

	private Button refreshBtn;

	static final String CACHED = "   |SDK CACHED";

	static final String UNCACHED = "   |SDK  UNCACHED";

	private Combo headerfileSource;

	public static String headerSource;

	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 */
	public LocalCacheUIComposite(Composite parentComposite) {
		super(parentComposite);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayout()
	 */
	protected Layout createLayout() {
		return new GridLayout(COLUMN_COUNT, false);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createLayoutData()
	 */
	protected Object createLayoutData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}

	/**
	 * Set context sensitive help ids to components that can have focus	
	 * @param tbl
	 */
	private void setContextSensitiveHelpIDs() {
		//When all links point to same direction, we only need set context sensitive link id to composite.
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				CacheHelpContextIDs.CACHE_HELP_PROPERTIES_TAB);
	}

	public static void updateSDKCacheInfoList() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				SDKCombo.setItems(getSDKs());

			}
		});

	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls()
	 */
	protected void createControls() {

		RGB rgbWhite = new RGB(255, 255, 255);
		Color white = new Color(null, rgbWhite);

		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		Label label = new Label(this, SWT.HORIZONTAL | SWT.LEFT);
		label.setText(Messages.getString("LocalCacheUIComposite.SelectSDK")); //$NON-NLS-1$
		SDKCombo = new CCombo(this, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);

		SDKCombo.setBackground(white);
		SDKCombo.setItems(getSDKs());

		String comboSelection = getSDKComboSelection();
		System.out.println("combo selection" + comboSelection);
		SDKCombo.setText(comboSelection);
		SDKCombo.addSelectionListener(new SDKComboSelectionListener());

		refreshBtn = new Button(this, SWT.PUSH);
		refreshBtn.setText(Messages
				.getString("LocalCacheUIComposite.RefreshCache")); //$NON-NLS-1$
		refreshBtn.addSelectionListener(new RefreshBtnSelectionListener());

		//Setting context sensitive help IDs
		setContextSensitiveHelpIDs();

		(new Label(this, SWT.LEFT)).setText(" ");
		Label comboTitleLabel1 = new Label(this, SWT.LEFT);
		//		 Creating controls

		comboTitleLabel1.setText("Select Source For Headers"); //$NON-NLS-1$

		headerfileSource = new Combo(this, SWT.READ_ONLY);
		final Combo myCombo = headerfileSource;
		String[] cached  =   SDKUtil.getCachedSDKs();
	       String[] webSources = {    "http://s60lxr", "http://developer.symbian.org/xref/oss"}; 
	       String[] headersources  = new String[cached.length+webSources.length];
	       System.arraycopy(webSources, 0, headersources,0 , webSources.length);       
	       System.arraycopy(cached, 0, headersources,2 , cached.length);
		
		myCombo.setItems(headersources);


String defaultSource = "http://s60lxr";
if(headersources.length!=0)	defaultSource = comboSelection;
	
	headerfileSource.setText(defaultSource);
	
		headerSource = myCombo.getText();
		myCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				headerSource = myCombo.getText();
				//System.out.println("you selected me: " + myCombo.getText());
			}
		});

	}

	/**
	 * Refresh SDK
	 */
	private class RefreshBtnSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			//not needed
		}

		/**
		 * Refrenshing selected SDK...
		 */
		public void widgetSelected(SelectionEvent arg0) {
			String selectedSDK = SDKCombo.getText();
			int index = selectedSDK.indexOf(CACHED);

			if (index == -1)
				index = selectedSDK.indexOf(UNCACHED);
			selectedSDK = selectedSDK.substring(0, index).trim();

			try {
				DbgUtility.println(DbgUtility.PRIORITY_LOOP,
						" Refreshing SDK: " + selectedSDK); //$NON-NLS-1$
				SdkInformation info = SDKFinder.getSDKInformation(selectedSDK);
				CacheEntryStorage storage = CacheEntryStorage.getInstance();
				//removing selected entrys
				storage.removeSelectedEntrys(selectedSDK);

				//update (seek and load) selected SDK
				selectSDKAndSeekFilesIfNeeded(info);
			} catch (Exception e) {

				e.printStackTrace();
				String message = Messages
						.getString("LocalCacheUIComposite.SDKSelectionErrMsg_Part1") + selectedSDK //$NON-NLS-1$
						+ Messages
								.getString("LocalCacheUIComposite.SDKSelectionErrMsg_Part2"); //$NON-NLS-1$
				APIQueryConsole
						.getInstance()
						.println(
								message
										+ Messages
												.getString("LocalCacheUIComposite.SDKSelectionErrMsg_Part3") + e, APIQueryConsole.MSG_ERROR); //$NON-NLS-1$
				APIQueryMessageBox box = new APIQueryMessageBox(message,
						SWT.ICON_ERROR | SWT.OK);
				box.open();
			}
		}

	}

	/**
	 * Get selection for SDK combo
	 * @return SDK ID or <code>null</code>
	 */
	private String getSDKComboSelection() {
		String currentlySelectedSDKID = CacheEntryStorage.getInstance()
				.getCurrentlySelectedSDKID();
		if (currentlySelectedSDKID == null) {
			currentlySelectedSDKID = ""; //$NON-NLS-1$
		}
		return currentlySelectedSDKID;
	}

	/**
	 * Select SDK
	 */
	private class SDKComboSelectionListener implements SelectionListener,
			ActionListener {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			//Not needed

		}

		/**
		 * selecting new SDK
		 */
		public void widgetSelected(SelectionEvent arg0) {

			String selectedSDK = SDKCombo.getText();
			
			//update the headersource

			int index = selectedSDK.indexOf(CACHED);
			if(index!=-1){
				//update header source selection list
			String headersrc=selectedSDK.substring(0,selectedSDK.indexOf(CACHED)).trim();
				headerfileSource.setText(headersrc);
			}

			if (index == -1)
				index = selectedSDK.indexOf(UNCACHED);
			selectedSDK = selectedSDK.substring(0, index).trim();

			DbgUtility.println(DbgUtility.PRIORITY_LOOP,
					" Selecting SDK: " + selectedSDK); //$NON-NLS-1$
			SdkInformation info = SDKFinder.getSDKInformation(selectedSDK);
			if (info != null) {
				selectSDKAndSeekFilesIfNeeded(info);

			} else {
				String message = Messages
						.getString("LocalCacheUIComposite.SDKDoesNotExistErrMsg_Part1") + selectedSDK //$NON-NLS-1$
						+ Messages
								.getString("LocalCacheUIComposite.SDKDoesNotExistErrMsg_Part2"); //$NON-NLS-1$
				APIQueryConsole.getInstance().println(message,
						APIQueryConsole.MSG_ERROR);
				APIQueryMessageBox box = new APIQueryMessageBox(message,
						SWT.ICON_ERROR | SWT.OK);
				box.open();
			}
		}

		public void actionPerformed(ActionEvent arg0) {

		}

	}

	/**
	 * Selecting dataSource and seeking file from SDK if not seeked allready
	 * @param info
	 */
	private void selectSDKAndSeekFilesIfNeeded(SdkInformation info) {

		//If selected SDK allready added, just updating data stor
		if (isSDKAllreadySeeked(info)) {
			updateDataStore(info);
		}
		//else we need to seek files under SDK and then update storage (job will do that)
		else {
			DbgUtility.println(DbgUtility.PRIORITY_LOOP,
					" Starting to seek files from SDK: " + info.getSdkId()); //$NON-NLS-1$
			SeekMetaXMLFilesJob job = new SeekMetaXMLFilesJob(
					Messages
							.getString("LocalCacheUIComposite.SeekingMetadataFilesMsg") + info.getSdkId(), info); //$NON-NLS-1$
			job.setPriority(Job.DECORATE);
			job.addJobChangeListener(this);
			job.schedule();
		}

		//When SDK is changed, it really means that data source is changed, and we want to clear existing search results
		UserSettingListenerNotificator.notifyListeners();
	}

	/**
	 * Returning previous selection to SDK if cancelled.
	 */
	private void doCancel() {
		Runnable doCancel = new Runnable() {
			public void run() {
				SDKCombo.setText(getSDKComboSelection());
			}
		};
		Display.getDefault().asyncExec(doCancel);
	}

	/**
	 * Check if selected SDK is allready added to data store
	 * @param info
	 * @return <code>true</code> if found, <code>false</code> otherwise.
	 */
public 	static boolean isSDKAllreadySeeked(SdkInformation info) {
		CacheEntryStorage storage = CacheEntryStorage.getInstance();
		Collection<AbstractEntry> entrys = storage.getEntries();
		for (AbstractEntry entry : entrys) {
			CacheEntry ce = (CacheEntry) entry;
			if (ce.getSDKID().equalsIgnoreCase(info.getSdkId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * When known that data source allready found (files seeked under SDK) 
	 * just updating datastore selections so that selected SDK is selected.
	 * @param info
	 */
	private void updateDataStore(SdkInformation info) {

		UpdateSDKSelectionJob job = new UpdateSDKSelectionJob(
				Messages
						.getString("LocalCacheUIComposite.UpdatingDataSourceMsg_Part1") + info.getSdkId() + Messages.getString("LocalCacheUIComposite.UpdatingDataSourceMsg_Part2"), info); //$NON-NLS-1$ //$NON-NLS-2$
		if (job.isAllreadyRunning()) {
			return;
		}
		job.setPriority(Job.DECORATE);
		job.addJobChangeListener(this);
		job.schedule();

	}

	/**
	 * Get SDK:s
	 * @return sdk:s
	 */
public static String[] getSDKs() {

		try {
			SdkInformation[] sdkInfoColl = SdkManager.getSdkInformation();
			String[] sdks = new String[sdkInfoColl.length];

			for (int i = 0; i < sdkInfoColl.length; i++) {
				
				sdks[i] = sdkInfoColl[i].getSdkId()
						+ ((isSDKAllreadySeeked(SDKFinder
								.getSDKInformation(sdkInfoColl[i].getSdkId()))) ? CACHED
								: UNCACHED);

			}

			return sdks;

		} catch (SdkEnvInfomationResolveFailureException e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().println(e.getMessage(),
					IConsolePrintUtility.MSG_ERROR);
			return null;
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				"-- Dispose() --> " + getClass().getName()); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		// not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		// not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		//If cancelled, select selection was before started!
		IStatus status = event.getResult();
		if (status.getCode() != IStatus.OK) {
			doCancel();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// not needed		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		// not needed	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// not needed		
	}
	
	
	
}
