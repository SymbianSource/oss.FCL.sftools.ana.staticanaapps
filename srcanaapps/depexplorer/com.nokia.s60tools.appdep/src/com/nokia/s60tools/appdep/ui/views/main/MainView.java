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
 
 
package com.nokia.s60tools.appdep.ui.views.main;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.search.SearchConstants;
import com.nokia.s60tools.appdep.ui.actions.AbstractShowSourceFileAction;
import com.nokia.s60tools.appdep.ui.actions.AddSisAndUpdateCacheMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.CacheUpdateMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.CollapseAllMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ComponentIsUsedByMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ComponentPropertiesMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ExpandAllMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ExpandSubtreeMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ExportReportMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.FindMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.FunctionIsUsedByExportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.FunctionIsUsedByImportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.LocateComponentMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.SearchMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.SelectAllFromTableViewerMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.SelectNewRootComponentMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.SelectNewSDKMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.SetNewRootMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ShowMethodCallLocationsImportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ShowSourceFileAndCreateProjectExportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ShowSourceFileAndCreateProjectImportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ShowSourceFileExportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.actions.ShowSourceFileImportFunctionMainViewAction;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.ui.ICopyActionHandler;
import com.nokia.s60tools.ui.ProgrammaticSelection;
import com.nokia.s60tools.ui.S60ToolsTable;
import com.nokia.s60tools.ui.S60ToolsTableColumnData;
import com.nokia.s60tools.ui.S60ToolsTableFactory;
import com.nokia.s60tools.ui.actions.CopyFromTableViewerAction;

/**
 * This class comprises the Main View of the AppDep
 * application.
 */
public class MainView extends ViewPart implements KeyListener {
	
	/**
	 * We can get view ID at runtime once the view is instantiated, but we
	 * also need static access to ID in order to be able to invoke the view.
	 */
	public static final String ID = "com.nokia.s60tools.appdep.ui.views.main.MainView"; //$NON-NLS-1$
		
	/**
	 * We will disable show source functionality for some "method" names,
	 * which are no real method names. List for prefixes is stored in here and 
	 * all clients of {@link MainView} can be used them. 
	 */
	private static final String [] DISABLE_SHOW_SOURCE_PREFIXES = {
		"BC break", //$NON-NLS-1$
		"vtable for", //$NON-NLS-1$
		"typeinfo", //$NON-NLS-1$
		"\"_._.absent_export_"}; //$NON-NLS-1$
		
	//
	// Actions
	//
	
	private Action actionSetAsNewRoot;
	private Action actionComponentIsUsedBy;

	private Action actionImportFunctionIsUsedBy;
	private Action actionImportFunctionDataCopy;
	private Action actionSelectAllImportFunctions;

	private Action actionExportFunctionIsUsedBy;
	private Action actionExportFunctionDataCopy;
	private Action actionSelectAllExportFunctions;
	
	private Action actionCompPropertyDataCopy;
	private Action actionSelectAllComponentProperties;
	private Action actionExpandAll = null;
	private Action actionCollapseAll = null;
	private Action actionExpandSubtree = null;
	private Action actionSelectNewRootComponent = null;
	private Action actionComponentProperties;
	private Action actionSelectNewSDK;
	private Action actionExportReport;
	private Action actionComponentFind;
	private Action actionLocateComponent;
	
	private Action actionCacheUpdate;
	private Action addSisFilesAndUpdateCache;
	private Action actionComponentSearch;
	private Action actionImportFunctionSearch;
	private Action actionExportFunctionSearch;
	
	private AbstractShowSourceFileAction actionImportShowSource;	
	private AbstractShowSourceFileAction actionExportShowSource;
	
	private ShowMethodCallLocationsImportFunctionMainViewAction actionImportShowMethodLoc;	
		
	private ShowSourceFileAndCreateProjectExportFunctionMainViewAction actionExportShowSourceInProject;
	private ShowSourceFileAndCreateProjectImportFunctionMainViewAction actionImportShowSourceInProject;

	/**
	 * This flag is updated when the view is fully populated
	 * i.e. all the indirect dependencies are searched.
	 * This flag is set to <code>true</code> by population
	 * progress listener when population has either finished
	 * or aborted by the user. The value of this flag is used, 
	 * for instance, by selection listener.
	 */
	static private boolean isDependencySearchOngoing = false;

	//
	// Controls and related classes (providers etc.)
	// 
	
	private TreeViewer componentTreeViewer;
	private DrillDownAdapter drillDownAdapter;
	private MainViewComponentTreeContentProvider compTreeViewerContentProv = null;
					
	private CTabItem importFunctionsTab;
	
	private S60ToolsTable importFunctionsViewer;

	private MainViewImportFunctionsTabContentProvider importFunctionsTabContentProvider;

	private CTabItem componentPropertiesTab;
	
	private SashForm componentPropertiesSashForm;
	
	private TableViewer componentPropertiesViewer;

	private MainViewComponentPropertiesTabContentProvider componentPropertiesTabContentProvider;
	
	private ArrayList<ImportFunctionData> importFunctionsArrayList;	
	
	private ComponentPropertiesData selectedComponentPropertiesData = null;

	private CTabItem exportFunctionsTab;

	private S60ToolsTable exportFunctionsViewer;

	private MainViewExportFunctionsTabContentProvider exportFunctionsTabContentProvider;
		
	private ArrayList<ExportFunctionData> exportFunctionsArrayList;	

	/**
	 * 'Go Into' actions looses the selection from the tree view, and there is
	 * no way to hook the action, therefore we store the recently selected
	 * component in order to know for which the selected function is bound to.
	 * 
	 * This is used for "Is Used By..."-actions and also 
	 * for "Show Source.."- and "Show Method.."-actions. 
	 */
	private Object mostRecentlySelectedComponentNode = null;

	
	/**
	 * The constructor.
	 */
	public MainView() {		
		setTitleToolTip(ProductInfoRegistry.getProductName());
	}

	/**
	 * This is a callback that will allow us
	 * to create the componentTreeViewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		// Creating commonly used data structures
		importFunctionsArrayList = new ArrayList<ImportFunctionData>();								
		exportFunctionsArrayList = new ArrayList<ExportFunctionData>();								
		
		//
		// Actions invoked by content providers may set enable/disable
		// states for the actions, therefore all the action has to be
		// created before creating the controls. This makes sure that
		// it is safe to refer to all the actions after this point.
		//
		createOnlyMainViewDependentActions();
		
		//
		// Creating controls
		//

		// The left side contains component hierarchy tree view
		SashForm componentHierarchySashForm = new SashForm(parent, SWT.HORIZONTAL);		
		createComponentTreeViewControl(componentHierarchySashForm);
		
		// The right side contains tabbed panes for showing...
		CTabFolder sidePaneTabFolder = new CTabFolder(componentHierarchySashForm, SWT.BOTTOM);		
		//... parent imported functions
		createImportFunctionsTabControl(sidePaneTabFolder);
		// ...exported functions for the selected module
		createExportFunctionsTabControl(sidePaneTabFolder);
		//...component properties
		createComponentPropertiesTabControl(sidePaneTabFolder);
		
		// Default selection for tab folder
		sidePaneTabFolder.setSelection(importFunctionsTab);
		
		//
		// Doing other initializations that may refer to the component
		// that has been created above.
		//
		hookContextMenu();
		contributeToActionBars();
		
		// Adding listeners
		componentTreeViewer.getControl().addKeyListener(this);
		
		// Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(componentTreeViewer.getControl(), 
	    		AppDepHelpContextIDs.APPDEP_MAIN_VIEW);

	    AppDepPlugin.setContextSensitiveHelpID(importFunctionsViewer.getTableInstance(), 
	    		AppDepHelpContextIDs.APPDEP_IMPORTED_FUNCTIONS);		

	    AppDepPlugin.setContextSensitiveHelpID(exportFunctionsViewer.getTableInstance(), 
	    		AppDepHelpContextIDs.APPDEP_EXPORTED_FUNCTIONS);		

	    AppDepPlugin.setContextSensitiveHelpID(componentPropertiesViewer.getTable(), 
	    		AppDepHelpContextIDs.APPDEP_PROPERTIES);		
	}

	/**
	 * Creates component properties tab.
	 * @param sidePaneTabFolder Parent tab folder.
	 */
	private void createComponentPropertiesTabControl(CTabFolder sidePaneTabFolder) {		
		componentPropertiesSashForm = new SashForm(sidePaneTabFolder, SWT.VERTICAL);		
		componentPropertiesTab = new CTabItem(sidePaneTabFolder, SWT.NONE);
		componentPropertiesTab.setControl(componentPropertiesSashForm);
		componentPropertiesTab.setText(Messages.getString("MainView.ComponentProperties_TabTitle")); //$NON-NLS-1$
		// and viewer for those
		componentPropertiesViewer = createComponentPropertiesTableViewer(componentPropertiesSashForm);		
		// Creating pop-up menu actions that require the existence of componentPropertiesViewer
		createComponentPropertiesTabPopUpMenuActions();
		componentPropertiesTabContentProvider = new MainViewComponentPropertiesTabContentProvider(this);
		componentPropertiesViewer.setContentProvider(componentPropertiesTabContentProvider);
		componentPropertiesViewer.setLabelProvider(new MainViewComponentPropertiesTabLabelProvider());
		componentPropertiesViewer.setInput(componentPropertiesTabContentProvider);
		componentPropertiesViewer.setSorter(new PropertyDataSorter());
	}

	/**
	 * Creates export functions tab.
	 * @param sidePaneTabFolder Parent tab folder.
	 */
	private void createExportFunctionsTabControl(CTabFolder sidePaneTabFolder) {
		SashForm exportFunctionsSashForm = new SashForm(sidePaneTabFolder, SWT.VERTICAL);		
		exportFunctionsTab = new CTabItem(sidePaneTabFolder, SWT.NONE);
		exportFunctionsTab.setControl(exportFunctionsSashForm);
		exportFunctionsTab.setText(Messages.getString("MainView.ExportedFunctions_TabTitle"));		 //$NON-NLS-1$
		// and viewer for those
		exportFunctionsViewer = createExportFunctionsTableViewer(exportFunctionsSashForm);
		// Creating pop-up menu actions that require the existence of exportFunctionsViewer
		createExportFunctionsTabPopUpMenuActions();
		exportFunctionsTabContentProvider = new MainViewExportFunctionsTabContentProvider(exportFunctionsArrayList);
		exportFunctionsViewer.getHostingViewer().setContentProvider(exportFunctionsTabContentProvider);
		exportFunctionsViewer.getHostingViewer().setLabelProvider(new MainViewExportFunctionsTabLabelProvider());
		exportFunctionsViewer.getHostingViewer().setInput(exportFunctionsTabContentProvider);
		exportFunctionsViewer.getHostingViewer().setSorter(new ExportFunctionDataSorter());
		ExportedFunctionsTabSelectionChangedListener selListener = new ExportedFunctionsTabSelectionChangedListener(this);
		exportFunctionsViewer.getHostingViewer().addSelectionChangedListener(selListener);
	}
	
	
	/**
	 * Creates import functions tab.
	 * @param sidePaneTabFolder Parent tab folder.
	 */
	private void createImportFunctionsTabControl(CTabFolder sidePaneTabFolder) {
		SashForm importFunctionsSashForm = new SashForm(sidePaneTabFolder, SWT.VERTICAL);		
		importFunctionsTab = new CTabItem(sidePaneTabFolder, SWT.NONE);
		importFunctionsTab.setControl(importFunctionsSashForm);
		importFunctionsTab.setText(Messages.getString("MainView.ImportedFunctions_TabTitle"));		 //$NON-NLS-1$
		// and viewer for those
		importFunctionsViewer = createImportFunctionsTableViewer(importFunctionsSashForm);
		// Creating pop-up menu actions that require the existence of importFunctionsViewer
		createImportFunctionsTabPopUpMenuActions();
		importFunctionsTabContentProvider = new MainViewImportFunctionsTabContentProvider(importFunctionsArrayList);
		importFunctionsViewer.getHostingViewer().setContentProvider(importFunctionsTabContentProvider);
		importFunctionsViewer.getHostingViewer().setLabelProvider(new MainViewImportFunctionsTabLabelProvider());
		importFunctionsViewer.getHostingViewer().setInput(importFunctionsTabContentProvider);
		importFunctionsViewer.getHostingViewer().setSorter(new ImportFunctionDataSorter());
		ImportedFunctionsTabSelectionChangedListener selListener = new ImportedFunctionsTabSelectionChangedListener(this);
		importFunctionsViewer.getHostingViewer().addSelectionChangedListener(selListener);
	}

	/**
	 * Creates component tree view control.
	 * @param componentHierarchySashForm Parent SashForm for the tree view control.
	 */
	private void createComponentTreeViewControl(SashForm componentHierarchySashForm) {
		componentTreeViewer = new TreeViewer(componentHierarchySashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(componentTreeViewer);
		compTreeViewerContentProv = new MainViewComponentTreeContentProvider(this);
		componentTreeViewer.setContentProvider(compTreeViewerContentProv);
		componentTreeViewer.setLabelProvider(new MainViewComponentTreeLabelProvider());
		componentTreeViewer.setInput(compTreeViewerContentProv.getInput());
		componentTreeViewer.addDoubleClickListener(new MainViewDoubleClickListener(this, 
																				   drillDownAdapter));
		ISelectionChangedListener selChangedListener 
									= new MainViewSelectionChangedListener(this, 
																		   importFunctionsArrayList,
																		   exportFunctionsArrayList);
		componentTreeViewer.addSelectionChangedListener(selChangedListener);
	}

	/**
	 * Creates table viewer for import functions tab item. 
	 * @return New <code>S60ToolsTable</code> object instance.
	 */
	private S60ToolsTable createImportFunctionsTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Ordinal_TableColumnTitle"), //$NON-NLS-1$
														60,
														ImportFunctionData.ORDINAL_COLUMN_INDEX,
														ImportFunctionDataSorter.CRITERIA_ORDINAL));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Name_TableColumnTitle"), //$NON-NLS-1$
														340,
														ImportFunctionData.NAME_COLUMN_INDEX,
														ImportFunctionDataSorter.CRITERIA_NAME, true));
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Offset_TableColumnTitle"), //$NON-NLS-1$
														60,
														ImportFunctionData.OFFSET_COLUMN_INDEX,
														ImportFunctionDataSorter.CRITERIA_OFFSET));
		
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr, 1);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tbl;
	}

	/**
	 * Creates table viewer for export functions tab item. 
	 * @return New <code>S60ToolsTable</code> object instance.
	 */
	private S60ToolsTable createExportFunctionsTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Ordinal_TableColumnTitle"), //$NON-NLS-1$
														60,
														ExportFunctionData.ORDINAL_COLUMN_INDEX,
														ExportFunctionDataSorter.CRITERIA_ORDINAL));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Name_TableColumnTitle"), //$NON-NLS-1$
														340,
														ExportFunctionData.NAME_COLUMN_INDEX,
														ExportFunctionDataSorter.CRITERIA_NAME, true));
				
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr, 1);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tbl;
	}
	
	
	/**
	 * Creates table viewer for component properties tab item. 
	 * @return New <code>TableViewer</code> object instance.
	 */
	private TableViewer createComponentPropertiesTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Property_TableColumnTitle"), //$NON-NLS-1$
														80,
														ComponentPropertiesData.PROPERTY_COLUMN_INDEX,
														PropertyDataSorter.CRITERIA_PROPERTY));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("MainView.Value_TableColumnTitle"), //$NON-NLS-1$
														380,
														ComponentPropertiesData.VALUE_COLUMN_INDEX,
														PropertyDataSorter.CRITERIA_VALUE));
		
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tblViewer;
	}
	

	/**
	 * Hooks view's context menu.
	 */
	private void hookContextMenu() {
		//
		// Context menu for tree viewer
		//
		MenuManager menuMgr = new MenuManager("#TreeViewPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainView.this.fillTreeViewContextMenu(manager);
			}
		});		
		Menu menu = menuMgr.createContextMenu(componentTreeViewer.getControl());
		componentTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, componentTreeViewer);
		
		//
		// Context menu for import functions pane
		//
		MenuManager menuMgr2 = new MenuManager("#ImportFunctionsPopupMenu"); //$NON-NLS-1$
		menuMgr2.setRemoveAllWhenShown(true);
		menuMgr2.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainView.this.fillImportFunctionsTabContextMenu(manager);
			}
		});

		Menu menu2 = menuMgr2.createContextMenu(importFunctionsViewer.getHostingViewer().getControl());
		importFunctionsViewer.getHostingViewer().getControl().setMenu(menu2);
		getSite().registerContextMenu(menuMgr2, importFunctionsViewer.getHostingViewer());
		
		//
		// Context menu for export functions pane
		//
		MenuManager menuMgr3 = new MenuManager("#ExportFunctionsPopupMenu"); //$NON-NLS-1$
		menuMgr3.setRemoveAllWhenShown(true);
		menuMgr3.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainView.this.fillExportFunctionsTabContextMenu(manager);
			}
		});

		Menu menu3 = menuMgr3.createContextMenu(exportFunctionsViewer.getHostingViewer().getControl());
		exportFunctionsViewer.getHostingViewer().getControl().setMenu(menu3);
		getSite().registerContextMenu(menuMgr3, exportFunctionsViewer.getHostingViewer());
		
		//
		// Context menu for component properties pane
		//
		MenuManager menuMgr4 = new MenuManager("#ComponentPropertiesPopupMenu"); //$NON-NLS-1$
		menuMgr4.setRemoveAllWhenShown(true);
		menuMgr4.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MainView.this.fillComponentPropertiesTabContextMenu(manager);
			}
		});

		Menu menu4 = menuMgr4.createContextMenu(componentPropertiesViewer.getControl());
		componentPropertiesViewer.getControl().setMenu(menu4);
		getSite().registerContextMenu(menuMgr4, componentPropertiesViewer);		
		
	}

	/**
	 * Contributes to view's main menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillViewPullDownMenu(bars.getMenuManager());
		fillViewToolBar(bars.getToolBarManager());
	}

	/**
	 * Fills view's main menu.
	 * @param manager Menu manager.
	 */
	private void fillViewPullDownMenu(IMenuManager manager) {
		manager.add(actionSelectNewSDK);
		manager.add(actionSelectNewRootComponent);
		manager.add(actionCacheUpdate);
		manager.add(addSisFilesAndUpdateCache);
		manager.add(new Separator());
		manager.add(actionExpandAll);
		manager.add(actionCollapseAll);
		
		// Finally updating action states
		updateViewActionEnabledStates();
	}

	/**
	 * Checks if given node is parent node.
	 * @return <code>true</code> if parent node, otherwise <code>false</code>.
	 */
	private boolean isParentNode(){
		Object obj = getComponentTreeSelectedElement();		
		if(obj != null &&  obj instanceof ComponentParentNode){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Fills tree view's context menu.
	 * @param manager Menu manager.
	 */
	private void fillTreeViewContextMenu(IMenuManager manager) {
		manager.add(actionSetAsNewRoot);
		manager.add(actionComponentIsUsedBy);
		manager.add(actionComponentSearch);
		manager.add(actionComponentFind);
		manager.add(actionLocateComponent);
		//Can't export only LeafNode
		if(isParentNode()){
			manager.add(actionExportReport);
		}
		manager.add(new Separator());
		manager.add(actionExpandSubtree);
		manager.add(actionExpandAll);
		manager.add(actionCollapseAll);
		manager.add(new Separator());
		manager.add(actionComponentProperties);		
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		// Finally updating action states
		updateViewActionEnabledStates();
	}

	/**
	 * Fills context menu for import functions tab.
	 * @param manager
	 */
	private void fillImportFunctionsTabContextMenu(IMenuManager manager) {
		manager.add(actionImportFunctionIsUsedBy);
		manager.add(actionImportShowSource);
		manager.add(actionImportShowSourceInProject);
		manager.add(actionImportShowMethodLoc);		
		manager.add(actionImportFunctionSearch);
		manager.add(actionImportFunctionDataCopy);
		manager.add(actionSelectAllImportFunctions);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	
		// Finally updating action states
		updateViewActionEnabledStates();
	}
	
	/**
	 * Fills context menu for export functions tab.
	 * @param manager Menu manager.
	 */
	private void fillExportFunctionsTabContextMenu(IMenuManager manager) {
		manager.add(actionExportFunctionIsUsedBy);
		manager.add(actionExportShowSource);		
		manager.add(actionExportShowSourceInProject);
		manager.add(actionExportFunctionSearch);
		manager.add(actionExportFunctionDataCopy);
		manager.add(actionSelectAllExportFunctions);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// Finally updating action states
		updateViewActionEnabledStates();
	}
	
	/**
	 * Fills context menu for component properties tab. 
	 * @param manager Menu manager.
	 */
	private void fillComponentPropertiesTabContextMenu(IMenuManager manager) {
		manager.add(actionCompPropertyDataCopy);
		manager.add(actionSelectAllComponentProperties);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		// Finally updating action states
		updateViewActionEnabledStates();
	}

	/**
	 * Fills toolbar.
	 * @param manager Toolbar manager.
	 */
	private void fillViewToolBar(IToolBarManager manager) {
		
		manager.add(actionSelectNewSDK);
		manager.add(actionSelectNewRootComponent);
		manager.add(actionCacheUpdate);
		manager.add(addSisFilesAndUpdateCache);
		manager.add(actionComponentFind);
		manager.add(actionComponentSearch);
		manager.add(new Separator());
		manager.add(actionExpandAll);
		manager.add(actionCollapseAll);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		
		// Finally updating action states
		updateViewActionEnabledStates();
	}

	/**
	 * Creates context menu actions for import functions tab.
	 */
	private void createImportFunctionsTabPopUpMenuActions(){
		actionImportFunctionIsUsedBy = new FunctionIsUsedByImportFunctionMainViewAction(this);		
		actionImportShowSource = new ShowSourceFileImportFunctionMainViewAction(this);
		actionImportShowSourceInProject = new ShowSourceFileAndCreateProjectImportFunctionMainViewAction(this);
		actionImportShowMethodLoc = new ShowMethodCallLocationsImportFunctionMainViewAction(this);
		ImportFunctionsClipboardCopyHandler funcCopyHandler = new ImportFunctionsClipboardCopyHandler();
		actionImportFunctionDataCopy = new CopyFromTableViewerAction(importFunctionsViewer.getHostingViewer(),
                                                               new ICopyActionHandler[]{ funcCopyHandler }
																);
		actionSelectAllImportFunctions = new SelectAllFromTableViewerMainViewAction(this, importFunctionsViewer.getHostingViewer());		
	}
	
	/**
	 * Creates context menu actions for export functions tab.
	 */
	private void createExportFunctionsTabPopUpMenuActions(){

		actionExportFunctionIsUsedBy = new FunctionIsUsedByExportFunctionMainViewAction(this);
		actionExportShowSource = new ShowSourceFileExportFunctionMainViewAction(this);		
		actionExportShowSourceInProject = new ShowSourceFileAndCreateProjectExportFunctionMainViewAction(this);
		ExportFunctionsClipboardCopyHandler funcCopyHandler = new ExportFunctionsClipboardCopyHandler();
		actionExportFunctionDataCopy = new CopyFromTableViewerAction(exportFunctionsViewer.getHostingViewer(),
                                                               new ICopyActionHandler[]{ funcCopyHandler }
																);
		actionSelectAllExportFunctions = new SelectAllFromTableViewerMainViewAction(this, exportFunctionsViewer.getHostingViewer());		
	}

	/**
	 * Creates context menu actions for component properties tab.
	 */
	private void createComponentPropertiesTabPopUpMenuActions(){
		ComponentPropertyTabClipboardCopyHandler propCopyHandler = new ComponentPropertyTabClipboardCopyHandler();
		actionCompPropertyDataCopy = new CopyFromTableViewerAction(componentPropertiesViewer,
                                                               new ICopyActionHandler[]{ propCopyHandler }
																);	
		actionSelectAllComponentProperties = new SelectAllFromTableViewerMainViewAction(this, componentPropertiesViewer);
	}
	
	/**
	 * Creates those actions that should be created very early in view creation
	 * in order to be able to set their enable/disable states.
	 */
	private void createOnlyMainViewDependentActions() {
		actionSetAsNewRoot = new SetNewRootMainViewAction(this); 		
		actionComponentIsUsedBy = new ComponentIsUsedByMainViewAction(this);
		actionComponentSearch = new SearchMainViewAction(this, SearchConstants.SearchType.SEARCH_COMPONENTS);
		actionImportFunctionSearch = new SearchMainViewAction(this, SearchConstants.SearchType.SEARCH_IMPORTED_FUNCTIONS);
		actionExportFunctionSearch = new SearchMainViewAction(this, SearchConstants.SearchType.SEARCH_EXPORTED_FUNCTION);
		actionComponentFind = new FindMainViewAction(this);
		actionLocateComponent = new LocateComponentMainViewAction(this);
		actionExpandAll = new ExpandAllMainViewAction(this);
		actionCollapseAll =  new CollapseAllMainViewAction(this);
		actionExpandSubtree = new ExpandSubtreeMainViewAction(this);
		actionSelectNewRootComponent = new SelectNewRootComponentMainViewAction(this);
		actionSelectNewSDK = new SelectNewSDKMainViewAction(this);
		actionCacheUpdate = new CacheUpdateMainViewAction(this);
		addSisFilesAndUpdateCache = new AddSisAndUpdateCacheMainViewAction(this);
		actionComponentProperties = new ComponentPropertiesMainViewAction(this);
		actionExportReport =  new ExportReportMainViewAction(this);
	}
	
	/**
	 * Passing the focus request to the componentTreeViewer's control.
	 */
	public void setFocus() {
		componentTreeViewer.getControl().setFocus();
	}

	/**
	 * @return Returns the componentTreeViewer.
	 */
	public TreeViewer getComponentTreeViewer() {
		return componentTreeViewer;
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
	 * Refereshes view contents.
	 */
	public void refresh(){
		componentTreeViewer.refresh();
		importFunctionsViewer.refreshHostingViewer(importFunctionsArrayList.size());
		exportFunctionsViewer.refreshHostingViewer(exportFunctionsArrayList.size());
		componentPropertiesViewer.refresh();
	}

	/**
	 * @return Returns the isDependencySearchOngoing.
	 */
	static public boolean isDependencySearchOngoing() {
		return isDependencySearchOngoing;
	}

	/**
	 * Informs to main view that search was started.
	 * Toolbar is updated accordingly.
	 */
	public void searchStarted() {
		isDependencySearchOngoing = true;
		
		// Updating action states
		updateViewActionEnabledStates();		
	}
	
	/**
	 * Informs to main view that search was ended.
	 * Toolbar is updated accordingly.
	 */
	public void searchCompleted() {
		
		isDependencySearchOngoing = false;
		
		// Updating action states
		updateViewActionEnabledStates();		
	}

	/**
	 * Can be used to notify view that it's input data have changed.
	 * Triggers actions for refreshing view contents properly. 
	 */
	public void inputUpdated() {
		if(isDependencySearchOngoing){
			// Aborting possible ongoing background searches
			MainViewDataPopulator.abortCurrentSearch();			
		}
		componentTreeViewer.setInput(compTreeViewerContentProv.getInput());
		// The previously shown function lists are no more valid
		importFunctionsArrayList.clear();	
		exportFunctionsArrayList.clear();
		refresh();		
		// Expanding tree view enabling the showing of 1st level of the
		// used components (that is actually 2nd level in the tree).
		componentTreeViewer.expandToLevel(2);	
	    componentTreeViewer.getTree().notifyListeners(SWT.Selection, new Event());
	}

	
	/**
	 * Checks if the root node is selected.
	 * @return Returns <code>true</code> if root node is selected
	 *         otherwise <code>false</code>.
	 */
	public boolean isRootNodeSelected(){
		Object obj = getComponentTreeSelectedElement();
		if(obj != null){
			if(obj instanceof ComponentParentNode){
				ComponentParentNode parentNode = (ComponentParentNode) obj;
				if(parentNode.isRootComponent()){
					return true;
				}
			}			
		}
		return false;
	}

	/**
	 * Checks if the selected component has subtree.
	 * @return Returns <code>true</code> if selected component has subtree,
	 *         otherwise <code>false</code> (also false returned in error situations).
	 */
	public boolean hasSelectedComponentSubtree(){
		Object obj = getComponentTreeSelectedElement();
		if(obj != null){
			if(obj instanceof ComponentParentNode){
				return componentTreeViewer.isExpandable(obj);
			}
		}
		return false;
	}	
	
	/**
	 * Checks if the selected component exists.
	 * @return Returns <code>true</code> if selected component exists,
	 *         otherwise <code>false</code> (also false returned in error situations).
	 */
	public boolean selectedComponentExist(){
		Object obj = getComponentTreeSelectedElement();
		ComponentParentNode nodeToBeChecked;
		if(obj != null){
			if(obj instanceof ComponentParentNode){
				nodeToBeChecked = (ComponentParentNode) obj;
			}
			else if(obj instanceof ComponentLinkLeafNode){
				ComponentLinkLeafNode link = (ComponentLinkLeafNode) obj;
				nodeToBeChecked = link.getReferredComponent();				
			}
			else{
				return false;
			}
			// Checking if component exists in cache
			if(!nodeToBeChecked.isMissing()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns currently selected element from 
	 * component tree
	 * @return Returns currently selected element or <code>null</code> 
	 *         if there are no selection made.
	 */
	public Object getComponentTreeSelectedElement() {
		ISelection selection = componentTreeViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		return obj;
	}

	/**
	 * Returns currently selected element among 
	 * the imported functions.
	 * @return Returns currently selected element or <code>null</code> 
	 *         if there are no selection made.
	 */
	public Object getSelectedImportFunction() {
		ISelection selection = importFunctionsViewer.getHostingViewer().getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		return obj;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		if(isDependencySearchOngoing){
			// Aborting possible ongoing background searches
			MainViewDataPopulator.abortCurrentSearch();			
		}
		
		super.dispose();
	}

	/**
	 * @return Returns the selectedComponentPropertiesData.
	 */
	public ComponentPropertiesData getSelectedComponentPropertiesData() {
		return selectedComponentPropertiesData;
	}

	/**
	 * @param selectedComponentPropertiesData The selectedComponentPropertiesData to set.
	 */
	public void setSelectedComponentPropertiesData(
			ComponentPropertiesData selectedComponentPropertiesData) {
		this.selectedComponentPropertiesData = selectedComponentPropertiesData;
	}

	/**
	 * @return Returns the importFunctionsViewer.
	 */
	public TableViewer getImportFunctionsViewer() {
		return importFunctionsViewer.getHostingViewer();
	}

	/**
	 * Updates context menu for import functions tab.
	 * @param isMultipleSelection <code>true</code> in case of multiple selection
	 *                            otherwise <code>false</code>.
	 */
	public void updateImportFunctionsContextMenuStates(boolean isMultipleSelection) {
		if(isMultipleSelection){
			actionImportFunctionIsUsedBy.setEnabled(false);
			enableImportShowSourceAction();
		}
		else{
			actionImportFunctionIsUsedBy.setEnabled(true);
			disableImportShowSourceAction();
		}
	}
	
	/**
	 * Enables show source functionality
	 */
	public void enableImportShowSourceAction() {
		actionImportShowSource.setEnabled(true);
		actionImportShowMethodLoc.setEnabled(true);
		actionImportShowSourceInProject.setEnabled(true);
	}

	/**
	 * Disables show source functionality
	 */
	public void disableImportShowSourceAction() {
		actionImportShowSource.setEnabled(false);
		actionImportShowMethodLoc.setEnabled(false);
		actionImportShowSourceInProject.setEnabled(false);
	}
	
	/**
	 * Disables Locate source action.
	 */
	public void disableLocateComponentAction(){
		actionLocateComponent.setEnabled(false);			
	}
	
	/**
	 * Enables Locate source action.
	 */
	public void enableLocateComponentAction(){
		actionLocateComponent.setEnabled(true);
	}	
	
	/**
	 * Disables actionSetAsNewRoot action.
	 */
	public void disableSetAsNewRootAction(){
		actionSetAsNewRoot.setEnabled(false);			
	}

	/**
	 * Enables actionSetAsNewRoot action.
	 */
	public void enableSetAsNewRootAction(){
		actionSetAsNewRoot.setEnabled(true);
	}
	
	/**
	 * Enables to get reference of the main view
	 * from the classes that do not actually
	 * have reference to the main view instance.
	 * This method opens activates/opens up the 
	 * view if it was not visible.
	 * @throws PartInitException 
	 */
	public static MainView getViewInstance() throws PartInitException{
		
		IWorkbenchPage page = AppDepPlugin.getCurrentlyActivePage();
		
		boolean viewAlreadyVisible = false;
		IViewPart viewPart = null;
		
		// Checking if view is already open
		IViewReference[] viewRefs = page.getViewReferences();
		for (int i = 0; i < viewRefs.length; i++) {
			IViewReference reference = viewRefs[i];
			String id = reference.getId();
			if(id.equalsIgnoreCase(MainView.ID)){
				viewAlreadyVisible = true;
				// Found, restoring the view
				viewPart = reference.getView(true);
				page.activate(viewPart);
			}
		}
		// View was not opened
		if(! viewAlreadyVisible){
			viewPart = page.showView(MainView.ID);							
		}	
		return ((MainView) viewPart);
	}

	/**
	 * Enables update request for the main view
	 * also from the classes that do not actually
	 * have reference to the main view instance.
	 */
	public static void update(){
		try {
			
			getViewInstance().inputUpdated();
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Aborts currently ongoing search (if dependency search is ongoing).
	 */
	public static void abortCurrentlyOngoingSearches(){
		if(isDependencySearchOngoing){
			MainViewDataPopulator.abortCurrentSearch();
		}
	}
	
	/**
	 * Because Window's native table component does not support
	 * text wrapping, this method offers possibility to resize
	 * Value column according to data. Resizing brings about
	 * scroll bars that enable seeing of all the data.
	 */
	public void performValueColumnPackToPropertiesTab(){
		Table table = componentPropertiesViewer.getTable();
		TableColumn column = table.getColumn(ComponentPropertiesData.VALUE_COLUMN_INDEX);
		column.pack();	
	}

	/**
	 * Updates context menu for export functions tab.
	 * @param isMultipleSelection <code>true</code> in case of multiple selection
	 *                            otherwise <code>false</code>.
	 */
	public void updateExportFunctionsContextMenuStates(boolean isMultipleSelection) {
		if(isMultipleSelection){
			actionExportFunctionIsUsedBy.setEnabled(false);
			disableExportShowSourceAction();
		}
		else{
			actionExportFunctionIsUsedBy.setEnabled(true);
			enableExportShowSourceAction();
		}		
	}

	/**
	 * Enables show source functionality
	 */
	public void enableExportShowSourceAction() {
		actionExportShowSource.setEnabled(true);
		actionExportShowSourceInProject.setEnabled(true);
	}

	/**
	 * Disables show source functionality
	 */
	public void disableExportShowSourceAction() {
		actionExportShowSource.setEnabled(false);
		actionExportShowSourceInProject.setEnabled(false);
	}
	
	/**
	 * @return Returns the exportFunctionsViewer.
	 */
	public TableViewer getExportFunctionsViewer() {
		return exportFunctionsViewer.getHostingViewer();
	}

	/**
	 * Gets currently selected export function.
	 * @return currently selected export function.
	 */
	public Object getSelectedExportFunction() {
		ISelection selection = getExportFunctionsViewer().getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		return obj;
	}

	/**
	 * Gets the most recently selected component from tree view.
	 * @return Returns the mostRecentlySelectedComponentNode.
	 */
	public Object getMostRecentlySelectedComponentNode() {
		return mostRecentlySelectedComponentNode;
	}

	/**
	 * Sets the most recently selected component from tree view.
	 * @param mostRecentlySelectedComponentNode The mostRecentlySelectedComponentNode to set.
	 */
	public void setMostRecentlySelectedComponentNode(
			Object mostRecentlySelectedComponentNode) {
		this.mostRecentlySelectedComponentNode = mostRecentlySelectedComponentNode;
	}
	
	/**
	 * Gets current root component node.
	 * @return Current root component node.
	 */
	public ComponentParentNode getRootComponentNode() {
		return (ComponentParentNode) compTreeViewerContentProv.getRootComponentNode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		// Key presses are only enabled when there is no dependency search ongoing
		if(isDependencySearchOngoing){
			String infoMsg = Messages.getString("MainView.KeyPresses_Disabled_While_Search_Is_Ongoing_InfoMsg"); //$NON-NLS-1$
			new AppDepMessageBox(infoMsg, SWT.OK | SWT.ICON_INFORMATION).open();
		}
		else{			
			//
			//  Checking if Ctrl+F was pressed and triggering find action, if pressed
			//
			final int CTRL_F = 0x6;
			int charValue = e.character;
			boolean ctrlFPressed = charValue == CTRL_F; // This should be enough
			boolean ctrlPressed = (e.stateMask & SWT.CTRL) != 0; // But still checking that Ctrl is also pressed
			if(ctrlPressed & ctrlFPressed){
				// Triggering find action
				actionComponentFind.run();
			}			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		// Not needed, but has to be implemented
	}
	
	/**
	 * Activates the given node in Main view's component tree.
	 * The given node is set as current selection.
	 * @param node Component node to be activated.
	 */
	public void activateTreeViewComponent(ComponentNode node) {
		
		// Making sure that referred component can be found
		if(drillDownAdapter.canGoHome()){
			drillDownAdapter.goHome();
		}
		
		ProgrammaticSelection newSelection = null;
		newSelection = new ProgrammaticSelection(
											new ComponentNode[]{
																node
																}
												);
		getComponentTreeViewer().setSelection(newSelection, true);
		refresh();
	}

	/**
	 * We will disable show source functionality for some "method" names,
	 * which are no real method names. List for prefixes is stored in here and 
	 * all clients of {@link MainView} can be used them. 
	 *
	 * @return the DISABLE_SHOW_SOURCE_PREFIXES
	 */
	public String[] getDisableShowSourcePrefixes() {
		return DISABLE_SHOW_SOURCE_PREFIXES;
	}

	/**
	 * Checks if the imported functions tab has currently any imported functions.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasImportFunctions() {
		return (importFunctionsArrayList.size() > 0);
	}

	/**
	 * Checks if the imported functions tab has currently any selections.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasImportFunctionSelection() {
		int selectionCount = importFunctionsSelectionCount();
		return (selectionCount > 0);
	}
	
	/**
	 * Checks if only single imported functions is selected.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasImportFunctionSingleSelection() {
		int selectionCount = importFunctionsSelectionCount();
		return (selectionCount == 1);
	}
	
	/**
	 * Gets import functions viewer selection count.
	 * @return import functions viewer selection count.
	 */
	private int importFunctionsSelectionCount() {
		return importFunctionsViewer.getSelectionCount();
	}

	/**
	 * Checks if all import functions has been selected.
	 * @return <code>true</code> if all selected, otherwise <code>false</code>.
	 */
	private boolean isAllImportFunctionsSelected() {
		return (importFunctionsArrayList.size() == importFunctionsSelectionCount());
	}	

	/**
	 * Checks if the exported functions tab has currently any exported functions.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasExportFunctions() {
		return (exportFunctionsArrayList.size() > 0);
	}

	/**
	 * Checks if the exported functions tab has currently any selections.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasExportFunctionSelection() {
		int selectionCount = exportFunctionsSelectionCount();
		return (selectionCount > 0);
	}
	
	/**
	 * Checks if only single exported functions is selected.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasExportFunctionSingleSelection() {
		int selectionCount = exportFunctionsSelectionCount();
		return (selectionCount == 1);
	}
	
	/**
	 * Gets export functions viewer selection count.
	 * @return export functions viewer selection count.
	 */
	private int exportFunctionsSelectionCount() {
		return exportFunctionsViewer.getSelectionCount();
	}

	/**
	 * Checks if all export functions has been selected.
	 * @return <code>true</code> if all selected, otherwise <code>false</code>.
	 */
	private boolean isAllExportFunctionsSelected() {
		return (exportFunctionsArrayList.size() == exportFunctionsSelectionCount());
	}	
	
	/**
	 * Checks if the component properties tab has currently any selections.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasCompPropertiesSelection() {
		int selectionCount = componentPropertiesSelectionCount();
		return (selectionCount > 0);
	}

	/**
	 * Gets component properties viewer selection count.
	 * @return component properties viewer selection count.
	 */
	private int componentPropertiesSelectionCount() {
		return componentPropertiesViewer.getTable().getSelectionCount();
	}

	/**
	 * Checks if all component properties has been selected.
	 * @return <code>true</code> if all selected, otherwise <code>false</code>.
	 */
	private boolean isAllCompPropertiesSelected() {
		return (ComponentPropertiesData.DESCRIPT_ARR.length == componentPropertiesSelectionCount());
	}

	/**
	 * Checks if the component properties tab has currently any properties.
	 * @return <code>true</code> if has, otherwise <code>false</code>.
	 */
	private boolean hasCompProperties() {
		return (componentPropertiesViewer.getTable().getItemCount() > 0);
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
		boolean isValidComponentSelection = isRootComponentSelectedForAnalysis && (getComponentTreeSelectedElement() != null);
		boolean isCurrentSelectionRootComponent = isRootNodeSelected();
		
		// Main menu & toolbar & common actions
		setEnableState(actionSelectNewSDK, !isDependencySearchOngoing());		
		setEnableState(actionSelectNewRootComponent, !isDependencySearchOngoing() && isRootComponentSelectedForAnalysis);
		setEnableState(actionCacheUpdate, isRootComponentSelectedForAnalysis);
		setEnableState(addSisFilesAndUpdateCache, isRootComponentSelectedForAnalysis);
		setEnableState(actionComponentFind, isRootComponentSelectedForAnalysis);
		setEnableState(actionComponentSearch, isRootComponentSelectedForAnalysis);
		setEnableState(actionExpandAll, isRootComponentSelectedForAnalysis);
		setEnableState(actionCollapseAll, isRootComponentSelectedForAnalysis);
		
		// Tree view
		boolean selectedComponentExist = selectedComponentExist();
		setEnableState(actionSetAsNewRoot, isValidComponentSelection && !isCurrentSelectionRootComponent && selectedComponentExist);
		setEnableState(actionComponentIsUsedBy, isValidComponentSelection);
		setEnableState(actionLocateComponent, isValidComponentSelection && !selectedComponentExist);
		setEnableState(actionExportReport, isValidComponentSelection);
		setEnableState(actionExpandSubtree, isValidComponentSelection && hasSelectedComponentSubtree());
		setEnableState(actionComponentProperties, isValidComponentSelection && selectedComponentExist);		

		// Import function
		boolean isImportActionsEnabled = isValidComponentSelection && hasImportFunctions();
		boolean hasImportFunctionSelection = isImportActionsEnabled && hasImportFunctionSelection();
		boolean hasImportFunctionSingleSelection = isImportActionsEnabled && hasImportFunctionSingleSelection();		
		boolean isAllImportFunctionsSelected = isImportActionsEnabled && isAllImportFunctionsSelected();		
		setEnableState(actionImportFunctionIsUsedBy, hasImportFunctionSingleSelection);
		setEnableState(actionImportShowSource, hasImportFunctionSingleSelection);
		setEnableState(actionImportShowSourceInProject, hasImportFunctionSingleSelection);
		setEnableState(actionImportShowMethodLoc, hasImportFunctionSingleSelection);		
		setEnableState(actionImportFunctionSearch, isImportActionsEnabled);
		setEnableState(actionImportFunctionDataCopy, hasImportFunctionSelection);
		setEnableState(actionSelectAllImportFunctions, isImportActionsEnabled && !isAllImportFunctionsSelected);

		// Export functions
		boolean isExportActionsEnabled = isValidComponentSelection && hasExportFunctions();
		boolean hasExportFunctionSelection = isExportActionsEnabled && hasExportFunctionSelection();
		boolean hasExportFunctionSingleSelection = isExportActionsEnabled && hasExportFunctionSingleSelection();		
		boolean isAllExportFunctionsSelected = isExportActionsEnabled && isAllExportFunctionsSelected();		
		setEnableState(actionExportFunctionIsUsedBy, hasExportFunctionSingleSelection);
		setEnableState(actionExportShowSource, hasExportFunctionSingleSelection);		
		setEnableState(actionExportShowSourceInProject, hasExportFunctionSingleSelection);
		setEnableState(actionExportFunctionSearch, isExportActionsEnabled);
		setEnableState(actionExportFunctionDataCopy, hasExportFunctionSelection);
		setEnableState(actionSelectAllExportFunctions, isExportActionsEnabled && !isAllExportFunctionsSelected);

		// Component properties
		boolean isCompPropActionsEnabled = isValidComponentSelection && hasCompProperties();
		boolean hasCompPropertiesSelection = isCompPropActionsEnabled && hasCompPropertiesSelection();
		boolean isAllCompPropertiesSelected = isCompPropActionsEnabled && isAllCompPropertiesSelected();		
		setEnableState(actionCompPropertyDataCopy, hasCompPropertiesSelection);
		setEnableState(actionSelectAllComponentProperties, isCompPropActionsEnabled && !isAllCompPropertiesSelected);
		
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
