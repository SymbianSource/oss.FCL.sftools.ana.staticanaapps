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
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.ui.S60ToolsTable;
import com.nokia.s60tools.ui.S60ToolsTableColumnData;
import com.nokia.s60tools.ui.S60ToolsTableFactory;
import com.nokia.s60tools.ui.dialogs.S60ToolsListBoxDialog;
import com.nokia.s60tools.ui.wizards.S60ToolsWizardPage;

/**
 * Select component wizard page implementation.
 */
public class SelectComponentWizardPage extends S60ToolsWizardPage implements ModifyListener,
                                                                             FocusListener,
                                                                             SelectionListener, 
                                                                             IRefreshable{
	//
	// Constants
	//
	private final int DUPLICATE_COMP_DLG_WIDTH = 350;
	private final int DUPLICATE_COMP_DLG_HEIGHT = 250;
	
	//
	// Members
	//
	private Text searchFieldTitle = null;
	private Text searchField = null;
	private Text buildTypeTitle = null;
	private Text buildTypeValue = null;
	private TableViewer componentViewer = null;
	private S60ToolsTable componentViewerAsS60Table = null;
	private Text showSISTitle = null;
	private Button showSISCheckBox = null;
	
	private SelectComponentWizardPageContentProvider contentProvider = null;
	private List<String> duplicateComponentList = null;
	private boolean showDuplicateComponentInfo;
	
	 /**
	 * Constructor.
	 * @param showDuplicateComponentInfo <code>true</code> if we want to inform user about duplicate components, 
	 * 									 otherwise <code>false</code>.
	 */
	public SelectComponentWizardPage(boolean showDuplicateComponentInfo){
			super(Messages.getString("SelectComponentWizardPage.Window_Title")); //$NON-NLS-1$
			setTitle(Messages.getString("SelectComponentWizardPage.Title")); //$NON-NLS-1$			
			setDescription(Messages.getString("SelectComponentWizardPage.SelectComponent_User_InfoMessage")); //$NON-NLS-1$
			
			// This is used to store information about the possible duplicate components
			duplicateComponentList = new ArrayList<String>();
			this.showDuplicateComponentInfo = showDuplicateComponentInfo;
			
			// User cannot finish the page before a selection is made.
			setPageComplete(false);
	 }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
	  GridData gd = new GridData(GridData.FILL_BOTH);
	  Composite c = createGridLayoutComposite(parent, SWT.NONE, 1, gd); 
		  
	  final int readOnlyLabelFieldStyleBits = SWT.READ_ONLY | SWT.NO_FOCUS;
	  
	  searchFieldTitle = new Text(c, readOnlyLabelFieldStyleBits); 
	  searchFieldTitle.setText(Messages.getString("SelectComponentWizardPage.Search_Field_Title")); //$NON-NLS-1$
	  searchFieldTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	  searchFieldTitle.addFocusListener(this);

	  searchField = new Text(c, SWT.SINGLE | SWT.BORDER); 
	  searchField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	  searchField.addModifyListener(this);

	  // Adding two column composite for build type info text.
	  GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
	  Composite titleAreaComposite = createGridLayoutComposite(c, SWT.NONE, 2, gd2); 
	  
	  buildTypeTitle = new Text(titleAreaComposite, readOnlyLabelFieldStyleBits); 
	  buildTypeTitle.setText(Messages.getString("SelectComponentWizardPage.BuildTypeTitle_Text")); //$NON-NLS-1$
	  buildTypeTitle.setLayoutData(new GridData(GridData.BEGINNING));	  
	  buildTypeTitle.addFocusListener(this);
	  
	  buildTypeValue = new Text(titleAreaComposite, readOnlyLabelFieldStyleBits); 
	  buildTypeValue.setText(""); // Value for this is set during run time //$NON-NLS-1$
	  buildTypeValue.setLayoutData(new GridData(GridData.BEGINNING));	  
	  buildTypeValue.addFocusListener(this);
	  
	  componentViewerAsS60Table = createComponentTableViewer(c);
	  componentViewer = componentViewerAsS60Table.getHostingViewer();
	  componentViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	  contentProvider = new SelectComponentWizardPageContentProvider(this);
	  componentViewer.setContentProvider(contentProvider); 
	  componentViewer.setLabelProvider(new SelectComponentWizardPageLabelProvider());
	  componentViewer.setInput(contentProvider);
	  componentViewer.addSelectionChangedListener(new SelectComponentWizardPageTableViewerSelectionChangedListener(this));
	  componentViewer.addDoubleClickListener(new SelectComponentWizardPageTableViewerDoubleClickListener(this));
	  componentViewer.setSorter(new ComponentTableViewerSorter());
	  componentViewer.getTable().addFocusListener(this);
	  	  
	  //Add check box and label for SIS components if this is a SIS wizard
	  final ISelectSDKWizard wiz = (ISelectSDKWizard) getWizard();
	  if (wiz.getSettings().isInSISFileAnalysisMode()) {

			Composite cSIS = new Composite(c, SWT.NONE);

			int colsSIS = 2;
			GridLayout gdlSIS = new GridLayout(colsSIS, false);
			GridData gdSIS = new GridData();
			cSIS.setLayout(gdlSIS);
			cSIS.setLayoutData(gdSIS);

			showSISCheckBox = new Button(cSIS, SWT.CHECK);
			showSISCheckBox.setLayoutData(new GridData());
			// Add listener to button, to set components visible or not (Show only SIS components or not)
			showSISCheckBox.addSelectionListener(this);
			showSISCheckBox.setSelection(true);
			
			showSISTitle = new Text(cSIS, readOnlyLabelFieldStyleBits);
			showSISTitle.setText(Messages.getString("SelectComponentWizardPage.ShowOnlySISFiles_Msg")); //$NON-NLS-1$
			showSISTitle.setLayoutData(new GridData());
		}

		// Setting control for this page
		setControl(c);
		  
		// Setting context help ID		
		AppDepPlugin.setContextSensitiveHelpID(getControl(), AppDepHelpContextIDs.APPDEP_WIZARD_PAGE_COMP_SELECT);
	}

	/**
	 * Creates new composite object using GridLayout.
	 * @param parent parent composite
	 * @param style Style bits for the composite.
	 * @param cols column count
	 * @param gd grid data attached to composite
	 * @return new composite object using GridLayout.
	 */
	private Composite createGridLayoutComposite(Composite parent, int style, int cols, GridData gd) {
		Composite c =   new Composite(parent, style);		  
		GridLayout gdl = new GridLayout(cols, false);
		c.setLayout(gdl);
		c.setLayoutData(gd);
		  
		return c;
	}

	/**
	 * Creates viewer component for showing selected SIS files. 
	 * @param parent Parent composite for the created composite.
	 * @return New <code>S60ToolsTable</code> object instance.
	 */
	protected S60ToolsTable createComponentTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectComponentWizardPage.Name_ColumnText"), //$NON-NLS-1$
														200,
														ComponentListNode.NAME_COLUMN_INDEX,
														ComponentTableViewerSorter.CRITERIA_NAME, 
														true));

		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectComponentWizardPage.Target_ColumnText"),  //$NON-NLS-1$
														60,
														ComponentListNode.TARGET_TYPE_COLUMN_INDEX,
														ComponentTableViewerSorter.CRITERIA_TARGET_TYPE,
														SWT.CENTER));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectComponentWizardPage.DateModified_ColumnText"),  //$NON-NLS-1$
														120,
														ComponentListNode.DATE_CACHED_COLUMN_INDEX,
														ComponentTableViewerSorter.CRITERIA_DATE_CACHED));
		
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		int showItemCountInColumn = 0;
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr, showItemCountInColumn);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tbl;
	}
	
	/**
	 * Refreshes wizard page components.
	 */
	public void refresh() {
		//Updating build type value field if it is changed
		String currBuildType = buildTypeValue.getText();
		SelectSDKWizard wizard = (SelectSDKWizard) getWizard();
		String buildTypedialogSetting = wizard.getDialogSetting(SelectSDKWizard.BUILD_TYPE_DESCR_DLG_SETTING_KEY);
		if(!currBuildType.equals(buildTypedialogSetting)){
			buildTypeValue.setText(buildTypedialogSetting);
			buildTypeValue.pack();
		}		
		// Refreshing controls 
		componentViewer.refresh();	
		componentViewerAsS60Table.refreshHostingViewer(contentProvider.getElementCount());
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#setInitialFocus()
	 */
	public void setInitialFocus(){
		// Clearing search text field
		searchField.setText(""); //$NON-NLS-1$
	    // Informing about duplicate components if showDuplicateComponentInfo is set.
	    informUserAboutDuplicateComponentsIfExists();		
		searchField.setFocus();			
	}
	
	/**
	 * Checks and informs user with dialog if there are duplicate components
	 */
	private void informUserAboutDuplicateComponentsIfExists() {
		// Only showing message if wanted and there is reason for it because of duplicate components
		if(showDuplicateComponentInfo && duplicateComponentList.size() > 0){
			Shell sh = AppDepPlugin.getCurrentlyActiveWbWindowShell();
			
			String listBoxContentMsg = Messages.getString("SelectComponentWizardPage.Duplicate_Components_InfoMsg_Header") + "\n";//$NON-NLS-1$ //$NON-NLS-2$
			
			for (String cmpName : duplicateComponentList) {
				listBoxContentMsg += "\n- " + cmpName; //$NON-NLS-1$
			}

			listBoxContentMsg += "\n\n" + Messages.getString("SelectComponentWizardPage.Duplicate_Components_InfoMsg_Footer");//$NON-NLS-1$ //$NON-NLS-2$
			
			String textAboveListBoxMessagesStr = Messages.getString("SelectComponentWizardPage.Duplicate_Components_WarningMsg"); //$NON-NLS-1$
			
			String dialogTitle = ProductInfoRegistry.getProductName(); //$NON-NLS-1$
			S60ToolsListBoxDialog mbox = new S60ToolsListBoxDialog(sh, 
						dialogTitle, 
						listBoxContentMsg, 
						false, // not resizable
						true,  // has vertical scroll bar
						true,  // has horizontal scroll bar
						DUPLICATE_COMP_DLG_WIDTH,  // default width
						DUPLICATE_COMP_DLG_HEIGHT, // default height
						false, //No Cancel button
						textAboveListBoxMessagesStr
	                );		
			mbox.create();
			mbox.open();			
		}		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.wizards.S60ToolsWizardPage#recalculateButtonStates()
	 */
	public void recalculateButtonStates() {
		ISelection selection = componentViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		
		ISelectSDKWizard wiz = (ISelectSDKWizard) getWizard();
		
		if(obj == null){
			wiz.updateAnalyzedComponentSelection(null, null);
			wiz.disableCanFinish();
			return;
		}
		
		setMessage(Messages.getString("SelectComponentWizardPage.OnFinish_User_InfoMessage")); //$NON-NLS-1$
		setErrorMessage(null);
		ComponentListNode compListNodeObj = (ComponentListNode) obj;
		wiz.updateAnalyzedComponentSelection(compListNodeObj.toString(), compListNodeObj.getBuildTargetType());
		wiz.enableCanFinish(ISelectSDKWizard.FINISH_COMPONENT_SELECTED);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent event) {
		if(event.widget.equals(searchField)){
			contentProvider.setFilter(searchField.getText());;
			refresh();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		Widget w = e.widget;
		if(w.equals(searchFieldTitle)){
			searchField.setFocus();
		}
		else if(w.equals(buildTypeTitle) || w.equals(buildTypeValue)){
			componentViewer.getTable().setFocus();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		// No need to do anything		
	}
	
	/**
	 * Gets the items currently visible in the table viewer's table
	 * and selects the first item that is visible in the table.
	 */
	public void selectFirstVisibleComponentFromList(){
		Table table = componentViewer.getTable();
		TableItem[] items = table.getItems();
		if(items.length > 0){
			table.setSelection(0);
		}
		recalculateButtonStates();
	}

	/**
	 * Gets data member used to store information about duplicate component instances.
	 * @return Data member used to store information about duplicate component instances.
	 */
	public List<String> getDuplicateComponentListInstance() {
		return duplicateComponentList;
	}

	/**
	 * Checks if only SIS components should be shown.
	 * @return <code>true</code> If user has been selected "Show only components from SIS files"
	 */
	public boolean showOnlySISComponents() {
		final ISelectSDKWizard wiz = (ISelectSDKWizard) getWizard();
		if (wiz.getSettings().isInSISFileAnalysisMode()) {
			return showSISCheckBox.getSelection();
		}
		else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		//Not needed, but Interface implementation needs this.		
	}

	/* Occurs when "Show only components from SIS file" -check box was clicked.
	 * Occurrence will update component list. 
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		contentProvider.setFilter(searchField.getText());;
		refresh();
	}
	
	/**
	 * Sets show duplicate component info flag.
	 * This is reset to true in case user goes back in wizard.
	 * @param showDuplicateComponentInfo <code>true</code> if we want to inform user about duplicate components.
	 */
	public void setShowDuplicateComponentInfo(boolean showDuplicateComponentInfo) {
		this.showDuplicateComponentInfo = showDuplicateComponentInfo;
	}

} // class SelectComponentWizardPage
