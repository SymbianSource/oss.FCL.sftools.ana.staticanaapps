/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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


package com.nokia.s60tools.appdep.ui.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AddComponentPrefixSearchOrderDialog;


/**
 * Preference page for DE plugin preferences
 *
 */
public class DEPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage{

	/**
	 * Used for replacement of illegal characters from component
	 * search order prefixes.
	 */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	// Fixed Width and height hints to set UI components precisely 
	private static final int COMPONENT_LIST_ITEMS_HEIGHT_HINT = 8;		
	public static final int COMPONENT_LIST_WIDTH = 300;
	
	/**
	 * Create the preference page
	 */
	public DEPreferencePage() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench arg0) {
	}

	// Controls for prefix search order
	private Button prefixAddBtn;
	private Button prefixRemoveBtn;
	private Button prefixUpBtn;
	private Button prefixDownBtn;	
	private List prefixComponentsList;
	// Don't ask again checkbox
	private Button dontAskAgainBtn;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.SIMPLE);
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayout(gdl);
		container.setLayoutData(gd);		
		//Create preference composite for Prefix search order
		createPrefixSeachOrderComposite(container);			
		
		createDontAskAgainSearchComposite(container);				
		
		getPrefsStoreValues();

		setHelps(parent);
		
		return container;
	}

	/**
	 * Creates Don't ask controls for Search dialog's 'Set as new root component' feature.
	 * @param container Parent container for the controls
	 */
	private void createDontAskAgainSearchComposite(
			Composite container) {

		//Group for all components
		Group askGroup = new Group (container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		askGroup.setLayout(gridLayout);
		gridLayout.makeColumnsEqualWidth = false;		
		askGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		askGroup.setText(Messages.getString("DEPreferencePage.DontAskAgain_Group_Txt"));  //$NON-NLS-1$
		askGroup.setLayout (gridLayout);		
		
		dontAskAgainBtn = new Button(askGroup, SWT.CHECK);
		dontAskAgainBtn.setToolTipText(Messages.getString("DEPreferencePage.DontAskAgain_ToolTip_Txt"));//$NON-NLS-1$
		dontAskAgainBtn.setSelection(DEPreferences.getDontAskSetAsNewRootFromSearch());

		Label label = new Label(askGroup, SWT.HORIZONTAL);
		label.setText(Messages.getString("DEPreferencePage.DontAskAgain_Txt"));//$NON-NLS-1$ 
			
	}
	
	/**
	 * Creates composite for prefix search order.
	 * @param container Parent composite
	 */
	private void createPrefixSeachOrderComposite(
			Composite container) {
						
		//Group for all components
		Group prefixGroup = new Group (container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		prefixGroup.setLayout(gridLayout);
		gridLayout.makeColumnsEqualWidth = false;
		
		prefixGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		prefixGroup.setText(Messages.getString("DEPreferencePage.ComponentPrefixSearchOrder_Group_Txt"));  //$NON-NLS-1$
		prefixGroup.setLayout (gridLayout);
		
		//composite for component list
		Composite listComp = new Composite(prefixGroup,SWT.SIMPLE);
		GridLayout listCompLayout = new GridLayout();
		listCompLayout.numColumns = 1;
		listComp.setLayout(listCompLayout);
		
		final int listBoxStyleBits = SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
		prefixComponentsList = new List(listComp,listBoxStyleBits);
		int listHeight = prefixComponentsList.getItemHeight() * COMPONENT_LIST_ITEMS_HEIGHT_HINT;
		Rectangle trim = prefixComponentsList.computeTrim(0, 0, 0, listHeight);
		GridData listData = new GridData(COMPONENT_LIST_WIDTH, SWT.DEFAULT);
		listData.horizontalAlignment = GridData.FILL;
		listData.grabExcessHorizontalSpace = true;
		listData.verticalAlignment = GridData.FILL;
		listData.grabExcessVerticalSpace = true;
		listData.heightHint = trim.height;		
		
		listData.heightHint = trim.height;		
		prefixComponentsList.setLayoutData(listData);		
		
		Composite btnComp = new Composite(prefixGroup,SWT.SIMPLE);
		GridLayout btnCompLayout = new GridLayout();
		btnCompLayout.numColumns = 1;
		btnCompLayout.makeColumnsEqualWidth = true;
		btnComp.setLayout(btnCompLayout);	
		

		GridData btnData = new GridData();
		btnData.horizontalAlignment = SWT.FILL;
		btnData.grabExcessVerticalSpace = true;
		
		//Add button
		prefixAddBtn = new Button(btnComp, SWT.PUSH);
		prefixAddBtn.setLayoutData(btnData);
		prefixAddBtn.setText(Messages.getString("DEPreferencePage.Prefix_Add_Txt"));  //$NON-NLS-1$
		prefixAddBtn.setToolTipText(Messages.getString("DEPreferencePage.Prefix_Add_tooltip_Txt")); //$NON-NLS-1$
		
		//Add component dialog
		final AddComponentPrefixSearchOrderDialog dialog = new AddComponentPrefixSearchOrderDialog(container.getShell());
		
		//listener for add button
		prefixAddBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
					String compName = dialog.getText();

					//In component name, there cannot be ";" chars, but just in case, replace them.
					if(compName.indexOf(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR) != -1){
						compName.replace(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR, EMPTY_STRING);
					}
					
					prefixComponentsList.add(compName);
				}//else, cancel is pushed
			}
		});				
		
		//remove button
		prefixRemoveBtn = new Button(btnComp, SWT.PUSH);
		prefixRemoveBtn.setText(Messages.getString("DEPreferencePage.Prefix_Remove_Txt"));  //$NON-NLS-1$
		prefixRemoveBtn.setToolTipText(Messages.getString("DEPreferencePage.Prefix_Remove_tooltip_Txt"));  //$NON-NLS-1$
		
		//listener for remove button
		prefixRemoveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				int sel[] = prefixComponentsList.getSelectionIndices();
				if(sel.length > 0){
					prefixComponentsList.remove(sel);
				}
			}
		});			
		
		//move up button
		prefixUpBtn = new Button(btnComp, SWT.PUSH);
		prefixUpBtn.setText(Messages.getString("DEPreferencePage.Prefix_Up_Txt"));  //$NON-NLS-1$
		prefixUpBtn.setToolTipText(Messages.getString("DEPreferencePage.Prefix_Up_tooltip_Txt"));  //$NON-NLS-1$
		prefixUpBtn.setLayoutData(btnData);
		
		//listener for move up botton
		prefixUpBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				int selectionIndex = prefixComponentsList.getSelectionIndex();
				if(selectionIndex != -1 && selectionIndex > 0){
					int newLocationIndex = selectionIndex-1;
					String value = prefixComponentsList.getItem(selectionIndex);
					prefixComponentsList.remove(selectionIndex);
					prefixComponentsList.add(value,newLocationIndex );
					prefixComponentsList.setSelection(newLocationIndex);
				}
			}
		});					
		
		//move down button
		prefixDownBtn = new Button(btnComp, SWT.PUSH);
		prefixDownBtn.setText(Messages.getString("DEPreferencePage.Prefix_Down_Txt")); //$NON-NLS-1$
		prefixDownBtn.setToolTipText(Messages.getString("DEPreferencePage.Prefix_Down_tooltip_Txt"));  //$NON-NLS-1$
		prefixDownBtn.setLayoutData(btnData);
		
		//listener for move down button
		prefixDownBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				int selectionIndex = prefixComponentsList.getSelectionIndex();
				if(selectionIndex != -1 && selectionIndex < (prefixComponentsList.getItemCount() -1 )){
					int newLocationIndex = selectionIndex+1;
					String value = prefixComponentsList.getItem(selectionIndex);
					prefixComponentsList.remove(selectionIndex);
					prefixComponentsList.add(value, newLocationIndex);
					prefixComponentsList.setSelection(newLocationIndex);
				}
			}
		});
	}

	/**
	 * Sets old values back to UI controls.
	 */
	private void getPrefsStoreValues(){
		IPreferenceStore store = AppDepPlugin.getPrefsStore();
		
		//Set Prefix search order values
		String values = store.getString(DEPreferenceConstants.DE_PREFIX_SEARCH_ORDER_VALUES);
		if(values != null && values.trim().length() > 0){
			String [] valuesTable = values.split(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR);
			prefixComponentsList.setItems(valuesTable);
		}
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		// Resetting prefixes values.
		String[] values = new String[0];
		prefixComponentsList.setItems(values);
		// Resetting dontAskAgain value.
		dontAskAgainBtn.setSelection(false);
		// Applies values to screen. Values are saved when Ok button is pressed.
		super.performDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store = AppDepPlugin.getPrefsStore();
	
		savePrefixSeachOrderPreferences(store);
		saveDontAskAgainSearchConfirmation(store);
		
		return super.performOk();
	}

	/**
	 * Saves prefix search order preferences.
	 * @param store Preferences store.
	 */
	private void savePrefixSeachOrderPreferences(IPreferenceStore store) {
		String [] values = prefixComponentsList.getItems();
		StringBuffer valuesList = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			valuesList.append(values[i]);
			if(i < (values.length-1)){
				valuesList.append(DEPreferenceConstants.PREFIX_SEARCH_ORDER_SEPARATOR);//Separator
			}
		}
		store.setValue(DEPreferenceConstants.DE_PREFIX_SEARCH_ORDER_VALUES, valuesList.toString());
	}
	
	/**
	 * Saves prefix search order preferences
	 * @param store Preferences store.
	 */
	private void saveDontAskAgainSearchConfirmation(IPreferenceStore store) {
		
		DEPreferences.setDontAskAtainAsNewRootFromSearch(dontAskAgainBtn.getSelection());
	}
	
	/**
	 * Sets this page's context sensitive help.
	 */
	private void setHelps(Composite helpContainer) {
		AppDepPlugin.setContextSensitiveHelpID(helpContainer,
															AppDepHelpContextIDs.APPDEP_PREF_PAGE);
	}	
	
}
