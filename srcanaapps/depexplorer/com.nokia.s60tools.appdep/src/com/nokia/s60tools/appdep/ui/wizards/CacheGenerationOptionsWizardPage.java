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
 
 
package com.nokia.s60tools.appdep.ui.wizards;




import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.CacheGenerationOptions;
import com.nokia.s60tools.appdep.core.IToolchain;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.ui.wizards.S60ToolsWizardPage;


/**
 * Cache generation options wizard implementation.
 */
public class CacheGenerationOptionsWizardPage extends S60ToolsWizardPage implements SelectionListener, 
																					IRefreshable {
	
	//
	// Constants
	//
	private static final int labelFieldStyleBits = SWT.LEFT;
	  
	//
	// Members
	//
	private Label comboTitleLabel;
    private Combo toolchainSelCombo;
    private IToolchain[] toolchainsVisibleInCombo;
	private Label spacerText;
	private Group importLibraryGroup;
	private Button useDsoFiles;
	private Button useLibFiles;

	/**
	 * Constructor
	 */
	public CacheGenerationOptionsWizardPage(){
			super(Messages.getString("CacheGenerationOptionsWizardPage.Window_Title"));		  //$NON-NLS-1$
			
			setTitle(Messages.getString("CacheGenerationOptionsWizardPage.Page_Title")); //$NON-NLS-1$
			
			setDescription(Messages.getString("CacheGenerationOptionsWizardPage.Finish_Button_InfoMsg")); //$NON-NLS-1$

			// User cannot finish the page before some valid 
			// selection is made.
			setPageComplete(false);
	 }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
	  Composite c = new Composite(parent, SWT.NONE);
	  
	  final int cols = 1;	  
	  GridLayout gdl = new GridLayout(cols, false);
	  GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	  c.setLayout(gdl);
	  c.setLayoutData(gd);
	  
	  //
	  // Creating controls
	  //
	  	  
	  // Creating UI for cache generation options
	  createCacheCreationOptionControls(c, cols);
	  
	  setInitialFocus();
	  
	  // Setting control for this page
	  setControl(c);
	  
      // Setting context help IDs		
      AppDepPlugin.setContextSensitiveHelpID(getControl(), AppDepHelpContextIDs.APPDEP_WIZARD_PAGE_CACHE_GEN_OPT);
	}

	/**
	 * Creates UI for cache generation options.
	 * @param parentComposite Parent composite control.
	 * @param cols Number of columns to fit grid layout into.
	 */
	private void createCacheCreationOptionControls(Composite parentComposite, final int cols) {
		
		  // Creating toolchain selection combobox
		  comboTitleLabel = new Label(parentComposite, SWT.LEFT);

		  comboTitleLabel.setText(Messages.getString("CacheGenerationOptionsWizardPage.ToolchainSelCombo_Title_Label_Text")); //$NON-NLS-1$
	  		  
		  toolchainSelCombo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		  toolchainSelCombo.setText(Messages.getString("CacheGenerationOptionsWizardPage.ToolchainSelCombo_Text")); //$NON-NLS-1$
		  toolchainSelCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		  
		  // Creating spacer
		  spacerText = new Label(parentComposite, labelFieldStyleBits);
		  spacerText.setText(" "); //$NON-NLS-1$
		  GridData gdSpacer = new GridData(GridData.FILL_HORIZONTAL);
		  gdSpacer.heightHint = 2;
		  spacerText.setLayoutData(gdSpacer);
		  
		  // Creating import library selection group within contents
		  importLibraryGroup = new Group(parentComposite, SWT.SHADOW_NONE);

		  importLibraryGroup.setText(Messages.getString("CacheGenerationOptionsWizardPage.ImportLibraryGroupd_Text")); //$NON-NLS-1$
		  GridLayout gdl2 = new GridLayout(cols, false);
		  GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		  importLibraryGroup.setLayout(gdl2);
		  importLibraryGroup.setLayoutData(gd2);
		  
		  useDsoFiles = new Button(importLibraryGroup, SWT.RADIO);

		  useDsoFiles.setText(Messages.getString("CacheGenerationOptionsWizardPage.UseDsoFile_RadioButton_Text")); //$NON-NLS-1$
		  useDsoFiles.setSelection(true);
		  useDsoFiles.addSelectionListener(this);
		  useLibFiles = new Button(importLibraryGroup, SWT.RADIO);

		  useLibFiles.setText(Messages.getString("CacheGenerationOptionsWizardPage.UseImportLibrary_RadioButton_Text")); //$NON-NLS-1$
		  useLibFiles.setSelection(false);
		  useLibFiles.addSelectionListener(this);		  		  

		  // Assuring wanted tab navigation order by settings tab list
		  Control[] tabList = new Control[]{ toolchainSelCombo, importLibraryGroup };
		  parentComposite.setTabList(tabList);
	}

	/**
	 * Disables cache the use of cache generation options. 
	 */
	public void disableCacheGenerationOptions() {
		useDsoFiles.setEnabled(false);
		useLibFiles.setEnabled(false);
		toolchainSelCombo.setEnabled(false);
	}	

	/**
	 * Initializes cache generation options that are available based on the 
	 * currently selected build target. 
	 */
	public void setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget() {
		try {
			
		  // Getting supported toolchains
		  AppDepSettings st = AppDepSettings.getActiveSettings();
		  IToolchain[] toolchainArr;
		  toolchainArr = st.getSupportedToolchainsForCurrentlyUsedTargets();
		  
		  // Enabling fields
		  toolchainSelCombo.setEnabled(true);
		  useLibFiles.setEnabled(true);
		  useDsoFiles.setEnabled(true);
		  
		  // There is always at least one toolchain in the array
		  if(toolchainArr.length == 1
		     && 
		     toolchainArr[0].getToolchainName().equalsIgnoreCase(AppDepSettings.STR_GCC)){
			    // GCC toolchain uses always lib-files
				useLibFiles.setSelection(true);
				useDsoFiles.setSelection(false);											  
				useLibFiles.setEnabled(false);
				useDsoFiles.setEnabled(false);
		  }
		  else{
			    // For other the user can select
				useDsoFiles.setSelection(true);
				useLibFiles.setSelection(false);											  
		  }
		  
		  // Clearing and then filling the combobox
		  toolchainSelCombo.removeAll();
		  toolchainsVisibleInCombo = null;
		  ArrayList<IToolchain> installedToolchains = new ArrayList<IToolchain>();
		  int defaultToolchainIndex = 0;
		  for (int i = 0; i < toolchainArr.length; i++) {
			IToolchain toolchain = toolchainArr[i];
			if(toolchain.isInstalled()){
				String toolchainDescription = toolchain.getToolchainDescription();
				String version = toolchain.getVersion();
				if(version != null){
					// Showing also version info, if available
					toolchainDescription = toolchainDescription + " " + version;//$NON-NLS-1$
				}
				toolchainSelCombo.add(toolchainDescription);	
				installedToolchains.add(toolchain);
			}
			if(toolchain.isDefault()){
				defaultToolchainIndex = i;
			}		
		  }	  
		  toolchainSelCombo.select(defaultToolchainIndex);
		  // Storing the list of installed toolchains
		  toolchainsVisibleInCombo = (IToolchain[]) installedToolchains.toArray(new IToolchain[0]);
		  
		} catch (InvalidCmdLineToolSettingException e) {
			e.printStackTrace();
		}
		  
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#setInitialFocus()
	 */
	public void setInitialFocus() {
		toolchainSelCombo.setFocus();			
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#recalculateButtonStates()
	 */
	public void recalculateButtonStates() {
		// This is an abstract method inherited/implemented from S60ToolsWizardPage
		// and has to exist even there is nothing to do when this is called.
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		try {
			if(event.widget.equals(useDsoFiles)){
				useDsoFiles.setSelection(true);
				useLibFiles.setSelection(false);								
			}
			else if(event.widget.equals(useLibFiles)){
				useLibFiles.setSelection(true);
				useDsoFiles.setSelection(false);								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		// We can ignore this		
	}
		
	/**
	 * Gets the selected toolchain.
	 * @return selected toolchain
	 */
	public IToolchain getCacheGenerationToolchain() {
		return toolchainsVisibleInCombo[toolchainSelCombo.getSelectionIndex()];
	}

	/**
	 * Gets the selected library type.
	 * @return selected library type
	 */
	public int getCacheGenerationLibType() {
		if(useDsoFiles.getSelection()){
			return CacheGenerationOptions.USE_DSO_FILES;
		}
		else{
			return CacheGenerationOptions.USE_LIB_FILES;			
		}
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.IRefreshable#refresh()
	 */
	public void refresh() {
		// This is a common interface for all SDK selection wizard pages.
	}
	
} // class CacheGenerationOptionsWizardPage

