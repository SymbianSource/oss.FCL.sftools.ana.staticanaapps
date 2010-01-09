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
 
 
package com.nokia.s60tools.apiquery.ui.views.main.properties;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.PlatformUI;

import com.nokia.s60tools.apiquery.APIQueryHelpContextIDs;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.preferences.APIQueryPreferences;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;
import com.nokia.s60tools.util.debug.DbgUtility;

public class PropertiesTabComposite extends AbstractUiFractionComposite {

	/**
	 * Amount of columns in the used grid layout.
	 */
	private final int COLUMN_COUNT = 1;
	
	private ComboViewer searchMethodsComboViewer;
	private Group searchMethodSpecificUiFractionGroup;	
	private AbstractUiFractionComposite searchMethodSpecificUiComposite;

	private Button showDataSourceInResultsBtn;
	
	private static Object mutex = new Object();

	private static int serachcount = 0;
	
	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 */
	public PropertiesTabComposite(Composite parentComposite) {
		super(parentComposite);
	}
	
	
public void enablePropTabComponents() {
		

		synchronized (mutex) {
		if((--serachcount)<=0)serachcount=0;
			
			if (serachcount == 0) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						try {
						if (!searchMethodsComboViewer.getControl().isEnabled())
							searchMethodsComboViewer.getControl().setEnabled(
									true);
						if (!searchMethodSpecificUiFractionGroup.isEnabled())
							searchMethodSpecificUiFractionGroup
									.setEnabled(true);
						if (!showDataSourceInResultsBtn.isEnabled())
							showDataSourceInResultsBtn.setEnabled(true);
						}catch (Exception e) {
						e.printStackTrace();
						}
					}
				});
			}
		}

	}

	public void disablePropTabComponents() {

		synchronized (mutex) {
			serachcount++;

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try{
					if (searchMethodsComboViewer.getControl().isEnabled())
						searchMethodsComboViewer.getControl().setEnabled(false);
					if (searchMethodSpecificUiFractionGroup.isEnabled())
						searchMethodSpecificUiFractionGroup.setEnabled(false);
					if (showDataSourceInResultsBtn.isEnabled())
						showDataSourceInResultsBtn.setEnabled(false);
					}catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}

				}
			});

		}

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
	protected Object createLayoutData(){
		return 	new GridData(GridData.FILL_HORIZONTAL);
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.ui.views.main.AbstractTabComposite#createControls()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		
		// Setting layout information
		Composite tabContentsComposite = new Composite(this, SWT.NONE);
		  
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		tabContentsComposite.setLayout(gdl);
		tabContentsComposite.setLayoutData(gd);
		
		// Creating controls
		Label comboTitleLabel = new Label(tabContentsComposite, SWT.LEFT);
		String searchMethodComboName = Messages.getString("PropertiesTabComposite.DataSource_Msg"); //$NON-NLS-1$
		comboTitleLabel.setText(searchMethodComboName + ":");		 //$NON-NLS-1$
		searchMethodsComboViewer = new ComboViewer(tabContentsComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		// Declared as 'final' in order to be able to use in anonymous class using the data 
		final Combo cmb = searchMethodsComboViewer.getCombo();
		cmb.setText(searchMethodComboName);
		cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
				
		SearchMethodExtensionRegistry reg = SearchMethodExtensionRegistry.getInstance();
		
		int currentlySelectedMethodIndex = 0;
		ISearchMethodExtensionInfo currSelInfo = UserSettings.getInstance().getCurrentlySelectedSearchMethodInfo();
		
		for (ISearchMethodExtension ext : reg.getExtensions()) {
			ISearchMethodExtensionInfo info = ext.getExtensionInfo();
			cmb.add(info.getDescription());
			if(info.hasEqualId(currSelInfo)){
				currentlySelectedMethodIndex = cmb.getItemCount()-1;
			}
			
		}		
		cmb.select(currentlySelectedMethodIndex);
		
		//Add button 
		
		String desc = Messages.getString("PropertiesTabComposite.ShowDataSourceInResults_Msg"); //$NON-NLS-1$ 
		showDataSourceInResultsBtn = new Button(tabContentsComposite, SWT.CHECK);			
		showDataSourceInResultsBtn.setText(desc);
		boolean showDataSourceInResults = APIQueryPreferences.getShowDataSourceInResults();
		showDataSourceInResultsBtn.setSelection(showDataSourceInResults);
		showDataSourceInResultsBtn.addSelectionListener(new ShowDataSourceInResultsSelectionListener());
		// Storing to Button array for later reference
				
		// Adding anonymous selection changed listener
		// Possibly taken in future into its own class.
		searchMethodsComboViewer.addSelectionChangedListener(new ISelectionChangedListener(
		){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				int selIndx = cmb.getSelectionIndex();
				SearchMethodExtensionRegistry smeReg = SearchMethodExtensionRegistry.getInstance();
				ISearchMethodExtension ext = smeReg.getByDescription(cmb.getItem(selIndx));
				if(ext != null){
					UserSettings.getInstance().setCurrentlySelectedSearchMethodInfo(ext.getExtensionInfo());
				}
				updateGroupControlContentsBasedOnTheCurrentSelection();
			}
	
		});
		
		// Adding group that will be populated by the search method-specific extensions.
		
		searchMethodSpecificUiFractionGroup = new Group(this, SWT.SHADOW_NONE);
		searchMethodSpecificUiFractionGroup.setText(getGroupDescriptionBasedOnTheCurrentSelection());
		GridLayout gdl2 = new GridLayout(cols, false);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		searchMethodSpecificUiFractionGroup.setLayout(gdl2);
		searchMethodSpecificUiFractionGroup.setLayoutData(gd2);

		// Adding Search method-specific UI fraction.
		searchMethodSpecificUiComposite = getSearchMethodSpecifiUIFraction();
		
		setContextSensitiveHelpIDs(tabContentsComposite);
	}

	
	/**
	 * Class that listens button state changes and updates preferences and results composite
	 */
	private class ShowDataSourceInResultsSelectionListener implements SelectionListener{

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {

			//Setting selection preferences
			APIQueryPreferences.setShowDataSourceInResults(showDataSourceInResultsBtn.getSelection());
			UserSettings.getInstance().settingsChanged();
		}
		
	}
	
	/**
	 * Set context sensitive help ids to components that can have focus	
	 * @param cmb
	 */
	private void setContextSensitiveHelpIDs(Composite cmb) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(cmb, 
				APIQueryHelpContextIDs.API_QUERY_HELP_PROPERTIES_TAB);
	}
	/**
	 * Resolves the group area description based on the current selection.
	 * @return Returns the group area description for the currently selected search method.
	 */
	private String getGroupDescriptionBasedOnTheCurrentSelection(){
		return getSearchMethodDescriptionBasedOnTheCurrentSelection() + "s";		 //$NON-NLS-1$
	}
	
	/**
	 * Resolves the description for the current selection.
	 * @return Returns the descriptipn for the current selection.
	 */
	private String getSearchMethodDescriptionBasedOnTheCurrentSelection(){
		Combo cmb = searchMethodsComboViewer.getCombo();
		return cmb.getItem(cmb.getSelectionIndex());		
	}

	/**
	 * Updates the contents of the group area based on the current selection.
	 */
	private void updateGroupControlContentsBasedOnTheCurrentSelection(){
		
		// Updating UI description
		searchMethodSpecificUiFractionGroup.setText(getGroupDescriptionBasedOnTheCurrentSelection());	

		// Disposing existing UI and re-creating search method specifiv UI fraction.
		if(searchMethodSpecificUiComposite != null){
			searchMethodSpecificUiComposite.dispose();
			searchMethodSpecificUiComposite = getSearchMethodSpecifiUIFraction();
			searchMethodSpecificUiFractionGroup.layout(true);
			int wHint = searchMethodSpecificUiComposite.getWidthHint();
			int hHint = searchMethodSpecificUiComposite.getHeightHint();
			Point size = searchMethodSpecificUiFractionGroup.computeSize(wHint, hHint);
			searchMethodSpecificUiFractionGroup.setSize(size);
		}
	}
	
	/**
	 * Created UI fraction for the currently selected search method.
	 * @return UI fraction as AbstractUiFractionComposite. 
	 */
	private AbstractUiFractionComposite getSearchMethodSpecifiUIFraction(){
		
		String selectedExtDescription = getSearchMethodDescriptionBasedOnTheCurrentSelection();
		SearchMethodExtensionRegistry smeReg = SearchMethodExtensionRegistry.getInstance();
		ISearchMethodExtension selectedExtension = smeReg.getByDescription(selectedExtDescription);
		
		return selectedExtension.createExtensionConfigurationUi(searchMethodSpecificUiFractionGroup);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		super.dispose();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- Dispose() --> " + getClass().getName());		 //$NON-NLS-1$
		
		searchMethodsComboViewer.getCombo().dispose();
		searchMethodSpecificUiFractionGroup.dispose();	
		searchMethodSpecificUiComposite.dispose();
		
	}
	
}
