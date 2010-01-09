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
 
 
package com.nokia.s60tools.appdep.ui.dialogs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.data.CacheDataManager;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.data.ICacheDataManager;
import com.nokia.s60tools.appdep.core.model.AbstractFunctionData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.locatecomponent.SeekParentNodesService;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.search.MatchType;
import com.nokia.s60tools.appdep.search.SearchConstants;
import com.nokia.s60tools.appdep.search.MatchType.MatchTypes;
import com.nokia.s60tools.appdep.search.SearchConstants.SearchType;
import com.nokia.s60tools.appdep.ui.actions.SelectAllFromListAction;
import com.nokia.s60tools.appdep.ui.actions.ShowMethodCallLocationsSearchDialogAction;
import com.nokia.s60tools.appdep.ui.utils.UiUtils;
import com.nokia.s60tools.appdep.ui.views.main.MainView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.ui.ICopyActionHandler;
import com.nokia.s60tools.ui.IStringProvider;
import com.nokia.s60tools.ui.StringArrayClipboardCopyHandler;
import com.nokia.s60tools.ui.actions.CopyFromStringProviderToClipboardAction;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * 
 * Implements search dialog for fetching components or
 * imported/exported functions. 
 *
 * Usage example:
 *
 * <code>
 * <pre>
 * 
 * // Parent shell
 * Shell sh = Display.getCurrent().getActiveShell()
 * 
 * ComponentParentNode rootNode = view.getRootComponentNode();
 * AppDepSearchDialog dlg = new AppDepSearchDialog(sh, SearchType.SEARCH_COMPONENTS, rootNode);
 * dlg.create();
 * dlg.open();			
 *  
 * </pre> 
 * </code>
 * 
 * @see org.eclipse.jface.dialogs.Dialog
 */
public class AppDepSearchDialog extends TrayDialog implements SelectionListener,
														  ModifyListener,
														  IStringProvider{

	/**
	 * Tooltip shown to user when searching components. This is default message at start up when default values are selected from dialog.
	 */
	private static final String TOOLTIP_MSG_COMPONENT_QUERY = Messages.getString("AppDepSearchDialog.Tooltip_Msg_MatchTypeCombo_ComponentQuery"); //$NON-NLS-1$

	/**
	 * Tooltip shown to user when searching functions and 'StartsWith', 'EndsWith', or 'ExactMatch' is selected from math type selection combo box.
	 */
	private static final String TOOLTIP_MSG_USE_FUNC_BASE_NAME = Messages.getString("AppDepSearchDialog.Tooltip_Msg_MatchTypeCombo_FunctionQuery_UsingBaseName"); //$NON-NLS-1$
	
	/**
	 * Tooltip shown to user when searching functions and 'Contains', or 'RegularExpression' is selected from math type selection combo box.
	 */
	private static final String TOOLTIP_MSG_USE_COMPLETE_FUNC_NAME = Messages.getString("AppDepSearchDialog.Tooltip_Msg_MatchTypeCombo_FunctionQuery_UsingLongName"); //$NON-NLS-1$
	
	/**
	 * Maximum amount of data items that are reasonable to show in search result list without making user wait for too long time.
	 * In case maximum result size is exceeded, an informative message is shown for a user.
	 */
	private static final int MAX_RESULTS_SIZE = 30000;

	//
	// Private types
	//
	private Action actionCompPropertiesDataCopy;
	private Action actionSelectAllFromList;
	private Action actionImportShowMethodLoc;	
	
	/**
	 * Match type is attached with description shown in the UI
	 */	
	private class MatchTypeItem{

		private final MatchTypes matchType;
		private final String matchTypeDescription;

		public MatchTypeItem(MatchTypes matchType, String matchTypeDescription){
			this.matchType = matchType;
			this.matchTypeDescription = matchTypeDescription;
			
		}

		/**
		 * @return the matchType
		 */
		public MatchTypes getMatchType() {
			return matchType;
		}

		/**
		 * @return the matchTypeDescription
		 */
		public String getMatchTypeDescription() {
			return matchTypeDescription;
		}

	}
	
	/**
	 * Single result item object in search result list box. 
	 */
	private class SearchResultItem{
		
		/**
		 * Component name
		 */
		private String componentName = null;
		/**
		 * Function name
		 */
		private String functionName = null;
		/**
		 * Function ordinal
		 */
		private String functionOrdinal = null;
		
		/**
		 * Constructor.
		 */
		public SearchResultItem(){			
		}
		
		/**
		 * Gets component name.
		 * @return component name
		 */
		public String getComponentName() {
			return componentName;
		}

		/**
		 * Sets component name
		 * @param componentName component name
		 */
		public void setComponentName(String componentName) {
			this.componentName = componentName;
		}

		/**
		 * Gets function name
		 * @return function name
		 */
		public String getFunctionName() {
			return functionName;
		}

		/**
		 * Sets function name
		 * @param functionName function name
		 */
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}

		/**
		 * Gets function ordinal.
		 * @return function ordinal
		 */
		public String getFunctionOrdinal() {
			return functionOrdinal;
		}

		/**
		 * Sets function ordinal
		 * @param functionOrdinal function ordinal
		 */
		public void setFunctionOrdinal(String functionOrdinal) {
			this.functionOrdinal = functionOrdinal;
		}

	}
	
	//
	// Constants
	//
	
	/**
	 * Default width.
	 */
	private static final int DEFAULT_WIDTH = 500;

	/**
	 * Default height.
	 */
	private static final int DEFAULT_HEIGHT = 350;
	
	/**
	 * Default column count for grid layouts.
	 */
	private final int DEFAULT_COLUMN_COUNT = 1;

	/**
	 * Search button ID
	 */
	private static final int SEARCH_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;
	
	//
	// Members
	//
	
	/**
	 * Possible match types available in search dialog.
	 */
	MatchTypeItem[] matchTypeItemArr = {
											new MatchTypeItem(MatchTypes.CONTAINS, Messages.getString("AppDepSearchDialog.Contains_Msg")),  //$NON-NLS-1$
											new MatchTypeItem(MatchTypes.STARTS_WITH, Messages.getString("AppDepSearchDialog.Starts_With_Msg")),  //$NON-NLS-1$
											new MatchTypeItem(MatchTypes.ENDS_WITH, Messages.getString("AppDepSearchDialog.Ends_With_Msg")),  //$NON-NLS-1$
											new MatchTypeItem(MatchTypes.EXACT_MATCH, Messages.getString("AppDepSearchDialog.Exact_Match_Msg")),  //$NON-NLS-1$											
											new MatchTypeItem(MatchTypes.REGULAR_EXPRESSION, Messages.getString("AppDepSearchDialog.Regular_Expression_Msg")) //$NON-NLS-1$
											};
	
    /**
     * Search button starts the search.
     */
	private Button searchButton;
    
    /**
     * Close button closes the dialog.
     */
	private Button closeButton;
	
	/**
	 * Radio button for components search type 
	 */
	Button searchForComponentsRadioBtn;
	
	/**
	 * Radio button for imported functions search type 
	 */
	Button searchForImportedFunctionsRadioBtn;
	
	/**
	 * Radio button for exported functions search type 
	 */
	Button searchForExportedFunctionsRadioBtn;
	
	/**
	 * Combobox for selecting the used match criterion.
	 */
	private Combo matchTypeSelCombo;
	
	/**
	 * Background color for non-editable widgets. 
	 */
	Color nonEditableFieldBkgColor;

	/**
	 * Case sensitivity selection checkbox
	 */
	Button caseSensitivityOptionCheckbox;
	/**
	 * Search string entering field. 
	 */
	private Text searchStringTxtField;
	
	/**
	 * List item for showing list of APIs 
	 */
	private List searchResultList;	
			
	/**
	 * Search type used for actual search.
	 */
	private SearchType selectedSearchType;	
		
	/**
	 * Reference to main view showing the actual data to search for.
	 */
	private final MainView view;

	/**
	 * Map where is results in list as keys and component name, where result belongs as value.
	 * Really needed when results are exported or imported functions, and item in list is not really 
	 * component name, but "<function name>   [<component name>]".  We need to know component name
	 * when double click is performed. Also we need to . 
	 */
	private Map<String, SearchResultItem> resultsMap;
	
	/**
	 * Keeps track if searchResultList contains data of imported functions.
	 */
	private boolean isImportedFunctionsSearched = false;
		
	/**
	 * Constructor.
	 * @param parentShell Parent shell.
	 * @param searchType Default search type for this dialog instance at start-up.
	 * @param view Reference to main view.
	 */
	public AppDepSearchDialog(Shell parentShell, SearchConstants.SearchType searchType,
			                  MainView view) {
		super(parentShell);
    	int shellStyle = getShellStyle() | SWT.RESIZE;
    	setShellStyle(shellStyle);	
		this.selectedSearchType = searchType;
		this.view = view;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		// Creating dialog area composite
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);		
		//
		// Defining dialog area layout
		//
		GridLayout gdl = new GridLayout(DEFAULT_COLUMN_COUNT, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = DEFAULT_WIDTH;
		gd.heightHint = DEFAULT_HEIGHT;
		gd.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		gd.verticalIndent = IDialogConstants.VERTICAL_MARGIN;
		dialogAreaComposite.setLayout(gdl);
		dialogAreaComposite.setLayoutData(gd);		
		// Allocating new color => should be dispose at close
		nonEditableFieldBkgColor = getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		
		//
		// Adding search label and search text box
		//
		Composite searchFieldsComposite = new Composite(dialogAreaComposite, SWT.NONE);
		final int searchFieldsColumnCount = 2;
		GridLayout gdlComposite = new GridLayout(searchFieldsColumnCount, false);
		GridData gdComposite = new GridData(GridData.FILL_HORIZONTAL);
		searchFieldsComposite.setLayout(gdlComposite);
		searchFieldsComposite.setLayoutData(gdComposite);
		
		// Search string label
		Label searchStringTxtFieldLabel = new Label(searchFieldsComposite, SWT.HORIZONTAL);		
		searchStringTxtFieldLabel.setText(Messages.getString("AppDepSearchDialog.Search_String_Msg")); //$NON-NLS-1$

		// Search string input fields
		final int textFieldStyleBits = SWT.LEFT | SWT.SINGLE | SWT.BACKGROUND | SWT.BORDER;
		searchStringTxtField = new Text(searchFieldsComposite, textFieldStyleBits);
		searchStringTxtField.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
		searchStringTxtField.setEditable(true);
		searchStringTxtField.addModifyListener(this);
		searchStringTxtField.addSelectionListener(this);
		
		//
		// Search type
		//
		Group searchTypeGroup = new Group(dialogAreaComposite, SWT.SHADOW_NONE);
		searchTypeGroup.setText(Messages.getString("AppDepSearchDialog.Search_For_Msg"));				 //$NON-NLS-1$
		GridLayout gdl2 = new GridLayout(DEFAULT_COLUMN_COUNT , false);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);				
		searchTypeGroup.setLayout(gdl2);
		searchTypeGroup.setLayoutData(gd2);

		// Component search radio button
		searchForComponentsRadioBtn = new Button(searchTypeGroup, SWT.RADIO);		
		searchForComponentsRadioBtn.setText(Messages.getString("AppDepSearchDialog.Components_Msg")); //$NON-NLS-1$
		searchForComponentsRadioBtn.addSelectionListener(this);
				
		// Imported functions search radio button
		searchForImportedFunctionsRadioBtn = new Button(searchTypeGroup, SWT.RADIO);		
		searchForImportedFunctionsRadioBtn.setText(Messages.getString("AppDepSearchDialog.Imported_Functions_Msg")); //$NON-NLS-1$
		searchForImportedFunctionsRadioBtn.addSelectionListener(this);
		
		// Exported functions search radio button
		searchForExportedFunctionsRadioBtn = new Button(searchTypeGroup, SWT.RADIO);		
		searchForExportedFunctionsRadioBtn.setText(Messages.getString("AppDepSearchDialog.Exported_Functions_Msg")); //$NON-NLS-1$
		searchForExportedFunctionsRadioBtn.addSelectionListener(this);

		// Updating radio button selection states accoring currenlty selected search type
		updateSearchTypeSelectionBtns();
		
		//
		// Options group
		//
		Group searchOptionsGroup = new Group(dialogAreaComposite, SWT.SHADOW_NONE);
		searchOptionsGroup.setText(Messages.getString("AppDepSearchDialog.Options_Msg"));				 //$NON-NLS-1$
		final int optionsGroupColumnCount = 3;
		GridLayout gdl3 = new GridLayout(optionsGroupColumnCount, false);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);				
		searchOptionsGroup.setLayout(gdl3);
		searchOptionsGroup.setLayoutData(gd3);
		
		// Match type combo label
		Label matchTypeLabel = new Label(searchOptionsGroup, SWT.HORIZONTAL | SWT.LEFT);		
		matchTypeLabel.setText(Messages.getString("AppDepSearchDialog.Match_Type_Msg"));		 //$NON-NLS-1$
		
		// Match type combo 
		matchTypeSelCombo = new Combo(searchOptionsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		matchTypeSelCombo.setText(Messages.getString("CacheGenerationOptionsWizardPage.ToolchainSelCombo_Text")); //$NON-NLS-1$
		matchTypeSelCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		matchTypeSelCombo.setBackground(nonEditableFieldBkgColor);		
		
		for (int i = 0; i < matchTypeItemArr.length; i++) {
			MatchTypeItem matchType = matchTypeItemArr[i];
			matchTypeSelCombo.add(matchType.getMatchTypeDescription());			
		}
		matchTypeSelCombo.select(0); // Selecting first match type by default
		matchTypeSelCombo.addSelectionListener(this);
		
		// Case sensitivity selection checbox 
		caseSensitivityOptionCheckbox = new Button(searchOptionsGroup, SWT.CHECK);
		caseSensitivityOptionCheckbox.setText(Messages.getString("AppDepSearchDialog.Case_Sensitive_Msg")); //$NON-NLS-1$
		caseSensitivityOptionCheckbox.setSelection(false);
		caseSensitivityOptionCheckbox.addSelectionListener(this);		
		
		//
		// Search Results 
		//
		Label searchResultsLabel = new Label(dialogAreaComposite, SWT.HORIZONTAL);		
		searchResultsLabel.setText(Messages.getString("AppDepSearchDialog.Search_Results_Msg"));		 //$NON-NLS-1$
		
		// List box for results
		final int listBoxStyleBits = SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
		searchResultList = new List(dialogAreaComposite, listBoxStyleBits);
		GridData listData1 = new GridData(GridData.FILL_BOTH);
		searchResultList.setLayoutData(listData1);
		searchResultList.setBackground(nonEditableFieldBkgColor);
		
		// Listening for user selections
		MouseListener searchResultListSelListener = new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
				searchListObjectDoubleClicked();
			}
			public void mouseDown(MouseEvent e) {
				// No action				
			}
			public void mouseUp(MouseEvent e) {
				// No action				
			}
		};
		searchResultList.addMouseListener(searchResultListSelListener);
		
		// Listening for list selections to prevent using Show Method Call Location with multiple selections.
		SelectionListener searchResultListSelectionListener = new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				// No action
			}

			public void widgetSelected(SelectionEvent e) {
				// Enabling action if needed.
				boolean enableMethodCallLocationsAction = (searchResultList.getSelectionCount() == 1) && isImportedFunctionsSearched; 
				actionImportShowMethodLoc.setEnabled(enableMethodCallLocationsAction);
			}
		};
		searchResultList.addSelectionListener(searchResultListSelectionListener);

		//Adding context menu to list.
		createActionMenu();
		
		// Updating tooltip texts according the current selection status
		updateToolTipTexts();
		
		//
		// And finally adding separator above the dialog button array
		// 
		Label separatorLine = new Label(dialogAreaComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separatorLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		
	    // Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(dialogAreaComposite, AppDepHelpContextIDs.APPDEP_SEARCH_DIALOG);
		
		return dialogAreaComposite;
	}
	

	/**
	 * Creating context menu
	 */
	private void createActionMenu(){

		createActions();
		
		MenuManager menuMgr = new MenuManager("#PopupCopyMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AppDepSearchDialog.this.fillViewContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(searchResultList);
		searchResultList.setMenu(menu);
			
	}

	/**
	 * Adds items to context menu.
	 * @param manager Menu manager
	 */
	private void fillViewContextMenu(IMenuManager manager) {
		manager.add(actionCompPropertiesDataCopy);
		manager.add(actionSelectAllFromList);
		manager.add(actionImportShowMethodLoc);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}	
	
	/**
	 * Creates actions for context menu
	 */
	private void createActions() {
		
		StringArrayClipboardCopyHandler copyHandler = new StringArrayClipboardCopyHandler();		
		actionCompPropertiesDataCopy = new CopyFromStringProviderToClipboardAction(this /* listItemsViewer*/,
                												new ICopyActionHandler[]{ copyHandler }
					                                           );
		actionSelectAllFromList = new SelectAllFromListAction(searchResultList);
		actionImportShowMethodLoc = new ShowMethodCallLocationsSearchDialogAction(this, view);
	}
	
	/**
	 * Updates radio buttons states insided search type group
	 * according the current selection.
	 */
	private void updateSearchTypeSelectionBtns() {
		if(selectedSearchType.equals(SearchType.SEARCH_COMPONENTS)){
			searchForComponentsRadioBtn.setSelection(true);			
		}
		else{
			searchForComponentsRadioBtn.setSelection(false);
		}
		if(selectedSearchType.equals(SearchType.SEARCH_IMPORTED_FUNCTIONS)){
			searchForImportedFunctionsRadioBtn.setSelection(true);			
		}
		else{
			searchForImportedFunctionsRadioBtn.setSelection(false);
		}
		if(selectedSearchType.equals(SearchType.SEARCH_EXPORTED_FUNCTION)){
			searchForExportedFunctionsRadioBtn.setSelection(true);			
		}
		else{
			searchForExportedFunctionsRadioBtn.setSelection(false);
		}
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {    	
        super.configureShell(shell);
        String dialogTopic = getDialogName();
		shell.setText(dialogTopic);
    }

	/**
	 * Gets the dialog name.
	 * @return dialog name
	 */
	private String getDialogName() {
        AppDepSettings st = AppDepSettings.getActiveSettings();

		String currentlyUsedSDK = st.getCurrentlyUsedSdk().getSdkId();
        String variant = st.getCurrentlyUsedTargetPlatformsAsString();
        String build = st.getBuildType().getBuildTypeName();        
        String usedSDKInfo = currentlyUsedSDK + " - " + variant + " " +build;//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
        String dialogTopic = Messages.getString("AppDepSearchDialog.Search_Msg") +" " +usedSDKInfo ;//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return dialogTopic;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Creating Search button
		searchButton = createButton(parent, SEARCH_BUTTON_ID, Messages.getString("AppDepSearchDialog.Search_Btn_Msg"),true); //$NON-NLS-1$
		searchButton.setEnabled(false); // By default disabled
		searchButton.addSelectionListener(this);
        //Creating Close button
		closeButton = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
		closeButton.addSelectionListener(this);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		if(e.widget == searchStringTxtField){
			//  <code>widgetDefaultSelected</code> is typically called 
			// when ENTER is pressed in a single-line text.
			}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Widget w = e.widget;
		
		if(w.equals(searchButton)){
			Runnable performAPIDetailsQueryRunnable = new Runnable(){
				public void run(){
					performSearch();
				}
			};
			
			// Showing busy cursor during operation			
			Display d = getShell().getDisplay();
			BusyIndicator.showWhile(d, performAPIDetailsQueryRunnable);
		}		
		else if(w.equals(closeButton)){
			// Otherwise it must be Close button
			this.close(); // close() method is overridden in the end of the file for making clean-up
			return;
		}
		else if(w.equals(matchTypeSelCombo)){
			int selIndex = matchTypeSelCombo.getSelectionIndex();
			MatchTypes selType = matchTypeItemArr[selIndex].getMatchType();
			// Setting enable/disable state of case sensitivity checkbox according the selected match type 
			if(selType.equals(MatchTypes.REGULAR_EXPRESSION)){
				caseSensitivityOptionCheckbox.setEnabled(false);
				caseSensitivityOptionCheckbox.setSelection(false);
			}
			else{
				caseSensitivityOptionCheckbox.setEnabled(true);				
			}
		}
		else if(w.equals(searchForComponentsRadioBtn)){
			selectedSearchType = SearchType.SEARCH_COMPONENTS;
			updateSearchTypeSelectionBtns();
		}
		else if(w.equals(searchForImportedFunctionsRadioBtn)){
			selectedSearchType = SearchType.SEARCH_IMPORTED_FUNCTIONS;
			updateSearchTypeSelectionBtns();
		}
		else if(w.equals(searchForExportedFunctionsRadioBtn)){
			selectedSearchType = SearchType.SEARCH_EXPORTED_FUNCTION;
			updateSearchTypeSelectionBtns();
		}
		
		// Updating tooltip texts according the current selection status
		// This must be called here because selected search type has 
		// to be set before calling the method.
		updateToolTipTexts();
	}

	/**
	 * Sets tooltip text according the selected match type 
	 * and the used query type.
	 * Calling of this method requires that all the referenced 
	 * widgets are created/initialized properly.
	 */
	private void updateToolTipTexts() {
		int selIndex = matchTypeSelCombo.getSelectionIndex();
		MatchTypes selType = matchTypeItemArr[selIndex].getMatchType();
		// Are we querying function data?
		if(selectedSearchType == SearchType.SEARCH_IMPORTED_FUNCTIONS || selectedSearchType == SearchType.SEARCH_EXPORTED_FUNCTION){
			if(selType.equals(MatchTypes.REGULAR_EXPRESSION) || selType.equals(MatchTypes.CONTAINS)){
				matchTypeSelCombo.setToolTipText(TOOLTIP_MSG_USE_COMPLETE_FUNC_NAME);
				return;
			}
			else{
				matchTypeSelCombo.setToolTipText(TOOLTIP_MSG_USE_FUNC_BASE_NAME);
				return;
				}			
		}
		// Otherwise component data is to be queried
		matchTypeSelCombo.setToolTipText(TOOLTIP_MSG_COMPONENT_QUERY);					
	}
       
	/**
	 * Performs the search based on the current dialog parameters.
	 */
	private void performSearch() {

		String searchString = null;
		try {
			// Clearing old results
			searchResultList.removeAll();
			
			// Disabling Show Method Call Locations functionality when list is cleared.
			actionImportShowMethodLoc.setEnabled(false);
			isImportedFunctionsSearched = false;
			
			ICacheDataManager manager = CacheDataManager.getInstance();
			
			searchString = searchStringTxtField.getText();
			MatchType matchType = new MatchType( matchTypeItemArr[matchTypeSelCombo.getSelectionIndex()].matchType );
			//Adding case sensitive parameters, only when its not regular expression 
			if(matchType.getMatchType() != MatchTypes.REGULAR_EXPRESSION){
				boolean isCaseSensitiveSearch = caseSensitivityOptionCheckbox.getSelection();
				matchType.setCaseSensitiveSearch(isCaseSensitiveSearch);
			}
			
			//Get results from manager
			Map<String, java.util.List<AbstractFunctionData>> results = manager.searchCache(searchString, selectedSearchType, matchType);

			if(results != null){
				//Generate results map, where is list strings and component names
				resultsMap = getResultMap(results, selectedSearchType);
				//check if there is not too much results. Checked with imported functions search "L" 
				//that 200000 results takes way too long to set dialog List items, but "newL" search with 13000 result is ok.
				int size = resultsMap.size();
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Search returned "  //$NON-NLS-1$
									+ size + " data items as result when searching with string '" + searchString + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				if(size > MAX_RESULTS_SIZE){
					showTooManyResultsErrorMsg(size);
				}else{
					Set<String> listItems = resultsMap.keySet();
					String[] resultString = (String[]) listItems.toArray(new String[0]);
					searchResultList.setItems(resultString);
					
					// Enabling Show Method Call Locations functionality if imported functions were successfully searched. 
					if(selectedSearchType == SearchType.SEARCH_IMPORTED_FUNCTIONS){
						actionImportShowMethodLoc.setEnabled(true);
						isImportedFunctionsSearched = true;
					}
				}
			}			
			
		} catch (CacheFileDoesNotExistException e) {
			// write to console, show error
			e.printStackTrace();
			String msg = Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part1")  //$NON-NLS-1$
				+ searchString  //$NON-NLS-1$
				+Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part2") //$NON-NLS-1$
				+ e;
			AppDepConsole.getInstance().println(
					msg, AppDepConsole.MSG_ERROR);
			showErrorDialog();
		} catch (IOException e) {
			//  write to console, show error
			e.printStackTrace();
			String msg = Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part1")  //$NON-NLS-1$
				+ searchString  //$NON-NLS-1$
				+Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part2") //$NON-NLS-1$
				+ e;
			AppDepConsole.getInstance().println(msg, AppDepConsole.MSG_ERROR);
			showErrorDialog();
		}
		catch (Exception e) {
			//  write to console, show error
			e.printStackTrace();
			String msg = Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part1")  //$NON-NLS-1$
				+ searchString  //$NON-NLS-1$
				+Messages.getString("AppDepSearchDialog.Search_Console_Err_Msg_Part2") //$NON-NLS-1$
				+ e;
			AppDepConsole.getInstance().println(msg, AppDepConsole.MSG_ERROR);
			showErrorDialog();
		}		
		
	}

	/**
	 * This message is shown in case query returns too many data items 
	 * that cannot be shown in dialog withouth compromising user experience.
	 * @param resultsSize Amount of data items returned as a results.
	 */
	private void showTooManyResultsErrorMsg(int resultsSize) {
		String msg = Messages.getString("AppDepSearchDialog.TooManyResults_ErrMsg_Part1")//$NON-NLS-1$
			+resultsSize + Messages.getString("AppDepSearchDialog.TooManyResults_ErrMsg_Part2");//$NON-NLS-1$
		AppDepMessageBox msgBox = new AppDepMessageBox(msg , SWT.ICON_INFORMATION | SWT.OK );
		msgBox.open();
	}
	
	/**
	 * Gets search results as map (converts the input data).
	 * @param results Original search results to be converted.
	 * @param searchType Type of search performed.
	 * @return Search results as map (converted from the input data).
	 */
	private Map<String, SearchResultItem> getResultMap(Map<String, java.util.List<AbstractFunctionData>> results, SearchType searchType) {
		
		Set<String> keys = results.keySet();
		Map<String, SearchResultItem> resultsArray = new HashMap<String, SearchResultItem>();
		switch (searchType) {
		case SEARCH_COMPONENTS:
			for (String compName : keys) {
				SearchResultItem compData = new SearchResultItem();
				compData.setComponentName(compName);
				resultsArray.put(compName, compData);					
			}
			break;
		case SEARCH_EXPORTED_FUNCTION:
		case SEARCH_IMPORTED_FUNCTIONS:
			for (String compName : keys) {
				java.util.List<AbstractFunctionData> datas = results.get(compName);
				for (AbstractFunctionData data : datas) {
					String functionNameAndCompName = data.getFunctionName() + "   [" + compName +"]"; //$NON-NLS-1$//$NON-NLS-2$
					SearchResultItem compData = new SearchResultItem();
					compData.setComponentName(compName);
					compData.setFunctionName(data.getFunctionName());
					compData.setFunctionOrdinal(data.getFunctionOrdinal());
					resultsArray.put(functionNameAndCompName, compData);
				}
			}
			break;
		default:
			break;		
		}
		return resultsArray;
		
	}

	/**
	 * Shows an error dialog
	 */
	private void showErrorDialog() {
		String msg = Messages.getString("AppDepSearchDialog.Search_User_Err_Msg"); //$NON-NLS-1$
		AppDepMessageBox msgBox = new AppDepMessageBox(msg , SWT.ICON_ERROR | SWT.OK );
		msgBox.open();
	}

	/**
	 * Checks what is selected object in the search results list
	 * and activates and selects it from the UI.
	 */
	private void searchListObjectDoubleClicked() {

		String selectedComponentName = null;
		//get selected component name
		try {
			
			String [] results = searchResultList.getItems();
			//Get selected component name from List:s object data by selection index
			String listItemId = results[searchResultList.getSelectionIndex()];
			SearchResultItem value = resultsMap.get(listItemId);
			selectedComponentName = value.getComponentName();
			
			//get root node of tree
			ComponentParentNode root = getRootNode();
			
			//Seek all parent nodes of tree to find if we have selected component all ready in tree
			Map<String, ComponentParentNode> parentNodes = SeekParentNodesService.findParentNodes(root);
			
			//If component can be found from current component tree, activating selection
			if(parentNodes.containsKey(selectedComponentName.toLowerCase())){			
				ComponentParentNode selectedNode = parentNodes.get(selectedComponentName.toLowerCase());			
				view.activateTreeViewComponent(selectedNode);		
			}
			else{
				//If component cannot be found from current component tree, setting selection as new root
				UiUtils.setComponentAsNewRootInMainView(view, selectedComponentName, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = Messages.getString("AppDepSearchDialog.SetComponent_Console_Err_Msg_Part1")  //$NON-NLS-1$
				+ selectedComponentName  //$NON-NLS-1$
				+Messages.getString("AppDepSearchDialog.SetComponent_Console_Err_Msg_Part2") //$NON-NLS-1$
				+ e;
			AppDepConsole.getInstance().println(msg, AppDepConsole.MSG_ERROR);
			showErrorDialog();			
		}
	}

	/**
	 * Gets current root node
	 * @return root node
	 */
	private ComponentParentNode getRootNode() {		
		//Getting new root node from view, not from start node!
		ComponentParentNode root = view.getRootComponentNode();		
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		if(searchStringTxtField.getText().length() > 0){
			searchButton.setEnabled(true);
		}else{
			searchButton.setEnabled(false);
		}
	}

	/**
	 * Gets selected String from list.
	 */
	public String getString() {
		String[] selectedItems = searchResultList.getSelection();
		if(selectedItems.length > 0){
			StringBuffer items = new StringBuffer();
			
			// There should be at least one item.
			items.append(selectedItems[0]);
			// Adding rest of the strings with new line.
			for(int i = 1;i < selectedItems.length;i++){
				items.append("\r\n"); //$NON-NLS-1$
				items.append(selectedItems[i]);
			}
			return items.toString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#close()
	 */
	@Override
	public boolean close() {
		// clean-up
		nonEditableFieldBkgColor.dispose();
		searchStringTxtField.removeSelectionListener(this);
		searchStringTxtField.removeModifyListener(this);
		// Calling super close
		return super.close();
	}

	/**
	 * Get currently selected function.
	 * @return String or <code>null</code>.
	 */
	public String getSelectedFunction() {
		if(searchResultList.getSelectionIndex() >= 0){
			//Get selected function name from list.
			String listItemId = searchResultList.getItem(searchResultList.getSelectionIndex());
			SearchResultItem value = resultsMap.get(listItemId);
			String functionName = value.getFunctionName();
			return functionName;
		}
		return null;
	}

	/**
	 * Get component for currently selected function.
	 * @return String or<code>null</code>.
	 */
	public String getComponentForSelectedFunction() {
		if(searchResultList.getSelectionIndex() >= 0){
			//Get selected component name from list.
			String listItemId = searchResultList.getItem(searchResultList.getSelectionIndex());
			SearchResultItem value = resultsMap.get(listItemId);
			String componentName = value.getComponentName();
			return componentName;
		}
		return null;
	}

	/**
	 * Gets the ordinal of selected function in search results list.
	 * @return ordinal of selected function in search results list, if 
	 * 					a function is selected, otherwise <code>null</code>
	 * 					(i.e. component has been selected or there is 
	 * 					 no selection made).
	 */
	public String getSelectedFunctionOrdinal() {
		if(searchResultList.getSelectionIndex() >= 0){
			//Get selected function ordinal from list.
			String listItemId = searchResultList.getItem(searchResultList.getSelectionIndex());
			SearchResultItem value = resultsMap.get(listItemId);
			String functionOrdinal = value.getFunctionOrdinal();
			return functionOrdinal;
		}
		return null;
	}
	
}
