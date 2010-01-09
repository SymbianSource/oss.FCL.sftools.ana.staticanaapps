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
 
 
package com.nokia.s60tools.appdep.ui.views.listview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.actions.ExportReportListViewAction;
import com.nokia.s60tools.appdep.ui.actions.SelectAllFromTableViewerComponentListViewAction;
import com.nokia.s60tools.appdep.ui.actions.SetNewRootComponentListViewAction;
import com.nokia.s60tools.appdep.ui.utils.UiUtils;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.ui.ICopyActionHandler;
import com.nokia.s60tools.ui.S60ToolsTable;
import com.nokia.s60tools.ui.S60ToolsTableColumnData;
import com.nokia.s60tools.ui.S60ToolsTableFactory;
import com.nokia.s60tools.ui.actions.CopyFromTableViewerAction;

/**
 * This class comprises the IsUsed By -component List view of the tool.
 */
public class ListView extends ViewPart {
	
	/**
	 * We can get view ID at runtime once the view is instantiated, but we
	 * also need static access to ID in order to be able to invoke the view.
	 */
	public static final String ID = "com.nokia.s60tools.appdep.ui.views.listview.ListView"; //$NON-NLS-1$
		
	//
	// Controls and related classes (providers etc.)
	// 
		
	private SashForm listViewSashForm;

	private TableViewer listItemsViewer;
	private Action actionCompPropertiesDataCopy;
	private Action actionSelectAll;
	private Action actionSetAsNewRoot;
	private ExportReportListViewAction actionExportReport;
	private ListViewContentProvider listViewContentProvider;

	/**
	 * Name of the component to query 'Is Use By' relationships for.
	 */
	private String componentName;
		
	/**
	 * Name of the function to query 'Is Use By' relationships for.
	 */
	private String functionName;
	
	/**
	 * The constructor.
	 */
	public ListView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the componentTreeViewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		//
		// Creating controls
		//

		// The left side contains component hierarchy tree view
		listViewSashForm = new SashForm(parent, SWT.HORIZONTAL);

		// List view viewer
		listItemsViewer = createListViewTableViewer(listViewSashForm);
				
		listViewContentProvider = new ListViewContentProvider();
		listItemsViewer.setContentProvider(listViewContentProvider);
		listItemsViewer.setLabelProvider(new ListViewLabelProvider());
		listItemsViewer.setInput(listViewContentProvider);
		listItemsViewer.setSorter(new ComponentListViewSorter());
		listItemsViewer.addSelectionChangedListener(new ListViewSelectionChangedListener(this));
		// On double-click setting clicked component as new root component
		listItemsViewer.addDoubleClickListener(new IDoubleClickListener(){

				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
				 */
				public void doubleClick(DoubleClickEvent event) {
					IStructuredSelection sel = (IStructuredSelection) event.getSelection();
					if(sel.size() == 1){
						Object firstElement = sel.getFirstElement();
						if(firstElement instanceof ComponentPropertiesData){
							ComponentPropertiesData propData = (ComponentPropertiesData)firstElement;
							String selectedComponentName = propData.getFilename();
							try {
								MainView view = MainView.getViewInstance();
								UiUtils.setComponentAsNewRootInMainView(view , selectedComponentName, propData.getTargetPlatform());				
							} catch (PartInitException e) {
								e.printStackTrace();
							}						
						}
					}
				} // doubleClick

			} // new IDoubleClickListener()
		);
		
		//
		// Doing other initializations
		//
		createActions();
		hookContextMenu();
		contributeToActionBars();
		
		// Updating initial state of menu actions
		updateViewActionEnabledStates();
		
		// Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(listItemsViewer.getControl(), 
	    		AppDepHelpContextIDs.APPDEP_COMPONENT_LIST_VIEW);
	}

	/**
	 * Creates table viewer for import functions tab item. 
	 * @return New <code>TableViewer</code> object instance.
	 */
	private TableViewer createListViewTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indeces must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.Name_TableColumnTitle"), //$NON-NLS-1$
														150,
														ComponentPropertiesData.NAME_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_NAME));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.BinaryFormat_TableColumnTitle"), //$NON-NLS-1$
														150,
														ComponentPropertiesData.BIN_FORMAT_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_BIN_FORMAT));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.UID1_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.UID1_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_UID1));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.UID2_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.UID2_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_UID2));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.UID3_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.UID3_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_UID3));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.SecureId_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.SECURE_ID_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_SECURE_ID));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.VendorID_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.VENDOR_ID_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_VENDOR_ID));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.MinHeapSize_TableColumnTitle"), //$NON-NLS-1$
														100,
														ComponentPropertiesData.MIN_HEAP_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_MIN_HEAP));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.MaxHeapSize_TableColumnTitle"), //$NON-NLS-1$
														100,
														ComponentPropertiesData.MAX_HEAP_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_MAX_HEAP));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("ListView.StackSize_TableColumnTitle"), //$NON-NLS-1$
														100,
														ComponentPropertiesData.STACK_SIZE_COLUMN_INDEX,
														ComponentListViewSorter.CRITERIA_STACK_SIZE));
		
		S60ToolsTableColumnData[] arr 
				= (S60ToolsTableColumnData[]) columnDataArr.toArray(
											   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tblViewer;
	}
		
	/**
	 * Creating context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ListView.this.fillViewContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(listItemsViewer.getControl());
		listItemsViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, listItemsViewer);
	}

	/**
	 * Contributing items to view menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillViewPullDownMenu(bars.getMenuManager());
		fillViewToolBar(bars.getToolBarManager());
	}

	/**
	 * Fills view menu.
	 * @param manager Menu manager.
	 */
	private void fillViewPullDownMenu(IMenuManager manager) {
	}

	/**
	 * Fills view's context menu.
	 * @param manager Menu manager.
	 */
	private void fillViewContextMenu(IMenuManager manager) {
		manager.add(actionCompPropertiesDataCopy);
		manager.add(actionSelectAll);
		manager.add(new Separator());
		manager.add(actionSetAsNewRoot);
		manager.add(actionExportReport);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Fills view's toolbar.
	 * @param manager Menu manager.
	 */
	private void fillViewToolBar(IToolBarManager manager) {
	}

	/**
	 * Creating actions for the view.
	 */
	private void createActions() {
		
		ComponentPropertiesClipboardCopyHandler copyHandler = new ComponentPropertiesClipboardCopyHandler();		
		actionCompPropertiesDataCopy = new CopyFromTableViewerAction(listItemsViewer,
                												new ICopyActionHandler[]{ copyHandler }
					                                           );
		actionSelectAll = new SelectAllFromTableViewerComponentListViewAction(this);
		actionSetAsNewRoot = new SetNewRootComponentListViewAction(this);
		actionExportReport = new ExportReportListViewAction(this);
	}
	
	/**	 
	 * Passing the focus request to the listItemsViewer's control.
	 */
	public void setFocus() {
		listItemsViewer.getControl().setFocus();
	}
	
	/**
	 * Allows other classes to update content description.
	 * @param newContentDescription New description.
	 */
	public void updateDescription(String newContentDescription){
		setContentDescription(newContentDescription);
		IToolBarManager tbManager = getViewSite().getActionBars().getToolBarManager();
		tbManager.update(true);
	}

	/**
	 * Refreshes the view.
	 */
	public void refresh(){
		listItemsViewer.refresh();
	}

	/**
	 * Returns currently selected element from list view.
	 * @return Returns currently selected element or <code>null</code> 
	 *         if there are no selection made.
	 */
	public Object getComponentListSelectedElement() {
		ISelection selection = listItemsViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		return obj;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * Sets input for the list view.
	 * @param usingCompPropArrayList List of component properties data to set.
	 */
	public void setInput(List<ComponentPropertiesData> usingCompPropArrayList) {
		listItemsViewer.setInput(usingCompPropArrayList);
		actionExportReport.setIsUsedByData(usingCompPropArrayList);
	}
	
	/**
	 * Gets component list viewer.
	 * @return component list viewer.
	 */
	public TableViewer getComponentListViewer(){
		return listItemsViewer;
	}

	/**
	 * Sets name of the component that was searched for using components.
	 * @param componentName Component name
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;		
	}
	
	/**
	 * Gets name of the component that was searched for using components.
	 * @return name of the component that was searched for using components.
	 */
	public String getComponentName() {
		return componentName;		
		
	}

	/**
	 * Enables to get reference of the main view
	 * from the classes that do not actually
	 * have reference to the main view instance.
	 * @throws PartInitException 
	 */
	private static ListView getViewInstance() throws PartInitException{
		
		IWorkbenchPage page = AppDepPlugin.getCurrentlyActivePage();
		
		IViewPart viewPart = null;
		
		IViewReference[] viewRefs = page.getViewReferences();
		if(viewRefs == null){
			return null;
		}
		for (int i = 0; i < viewRefs.length; i++) {
			IViewReference reference = viewRefs[i];
			String id = reference.getId();
			if(id.equalsIgnoreCase(ListView.ID)){
				viewPart = reference.getView(true);
			}
		}
		if(viewPart == null){
			return null;
		}
		return ((ListView) viewPart);
	}

	/**
	 * Clears list view (By updating it with empty List).
	 */
	public static void clear(){
		try {
			ListView view = getViewInstance();
			if(view != null){
				ArrayList<ComponentPropertiesData> emptyData = new ArrayList<ComponentPropertiesData>();				
				view.setInput(emptyData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the name of the function that was searched for using components.
	 * @return the name of the function that was searched for using components.
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * Sets the name of the function that was searched for using components.
	 * @param functionName the functionName to set
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	/**
	 * Checks if the component list has currently any selections.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasComponentListSelection() {
		int selectionCount = getListViewSelectionCount();
		return (selectionCount > 0);
	}

	/**
	 * Checks if only single component is selected.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasComponentListSingleSelection() {
		int selectionCount = getListViewSelectionCount();
		return (selectionCount == 1);
	}	
	
	/**
	 * Gets component list viewer selection count.
	 * @return component list viewer selection count.
	 */
	private int getListViewSelectionCount() {
		return listItemsViewer.getTable().getSelectionCount();
	}

	/**
	 * Checks if all components has been selected.
	 * @return <code>true</code> if all selected, otherwise <code>false</code>.
	 */
	private boolean isAllComponentsSelected() {
		return (getListViewItemCount() == getListViewSelectionCount());
	}

	/**
	 * Gets count of all elements in component list view.
	 * @return count of all elements in component list view.
	 */
	private int getListViewItemCount() {
		return listItemsViewer.getTable().getItemCount();
	}

	/**
	 * Checks if the component list has currently any components.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasComponentsOnList() {
		return (listItemsViewer.getTable().getItemCount() > 0);
	}
	
	/**
	 * Sets enabled/disabled states for actions commands
	 * on this view, based on the current application state.
	 * This method should be called whenever an operation is
	 * started or stopped that might have effect on action 
	 * button states.
	 */
	public void updateViewActionEnabledStates(){
		
		// Resolving current state		
		boolean isRootComponentSelectedForAnalysis = AppDepSettings.isRootComponentSelectedForAnalysis();
		boolean isValidComponentSelection = isRootComponentSelectedForAnalysis && (getComponentListSelectedElement() != null);
		boolean isComponentListActionsEnabled = isValidComponentSelection && hasComponentsOnList();
		boolean hasComponentListSelection = isComponentListActionsEnabled && hasComponentListSelection();
		boolean isAllComponentsSelected = isComponentListActionsEnabled && isAllComponentsSelected();	
		boolean hasComponentListSingleSelection = isComponentListActionsEnabled && hasComponentListSingleSelection();
		
		// Updating action enable/disable statuses
		setEnableState(actionCompPropertiesDataCopy, hasComponentListSelection);
		setEnableState(actionSelectAll, isComponentListActionsEnabled && !isAllComponentsSelected);
		setEnableState(actionSetAsNewRoot, hasComponentListSingleSelection);
		setEnableState(actionExportReport, hasComponentListSingleSelection);		
	}

	/**
	 * Sets given enable state for an action if it is non <code>null</code>.
	 * @param action Action to set enable status for.
	 * @param enableStatus <code>true</code> if enabled, otherwise <code>false</code>.
	 */
	private void setEnableState(Action action, boolean enableStatus) {
		if(action != null){
			action.setEnabled(enableStatus);			
		}
	}	
}
