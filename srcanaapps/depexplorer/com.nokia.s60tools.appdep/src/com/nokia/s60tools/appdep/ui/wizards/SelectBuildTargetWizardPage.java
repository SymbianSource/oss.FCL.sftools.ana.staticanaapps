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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ICacheIndexListener;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.core.job.IJobCompletionListener;
import com.nokia.s60tools.appdep.core.job.IManageableJob;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AddSISFilesDialog;
import com.nokia.s60tools.appdep.ui.wizards.BuildTargetEntry.BuildTargetStatusEnum;
import com.nokia.s60tools.appdep.ui.wizards.BuildTargetSelectionBuildTypeFilter.BuildTargetFilterModeEnum;
import com.nokia.s60tools.appdep.util.LogUtils;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.ui.S60ToolsTable;
import com.nokia.s60tools.ui.S60ToolsTableColumnData;
import com.nokia.s60tools.ui.S60ToolsTableFactory;
import com.nokia.s60tools.ui.wizards.S60ToolsWizardPage;
import com.nokia.s60tools.util.debug.DbgUtility;


/**
 * Wizard page showing the available targets among which 
 * the user select a target(s) for seeking components to analyze. 
 */
public class SelectBuildTargetWizardPage extends S60ToolsWizardPage implements SelectionListener,
												   IJobCompletionListener,
                                                   FocusListener,
                                                   ICacheIndexListener, 
                                                   IRefreshable{	
	//
	// Constants
	//
	
	/**
	 * Build type text for release (urel) builds.
	 */
	private static final String BUILD_TYPE_RELEASE_STR = Messages.getString("SelectBuildTargetWizardPage.BuildType_Release_InfoMsg");	 //$NON-NLS-1$
	
	/**
	 * Build type text for release (urel) builds.
	 */
	private static final String BUILD_TYPE_DEBUG_STR = Messages.getString("SelectBuildTargetWizardPage.BuildType_Debug_InfoMsg");	 //$NON-NLS-1$

	/**
	 * Style bits used for read only labels.
	 */
	private static final int READ_ONLY_LABEL_FIELD_STYLEBITS = SWT.READ_ONLY | SWT.NO_FOCUS;
	  
	
	/**
	 * States used to store the button state
	 * result based on the multiselection.
	 * The actual resolution of button statuses
	 * is done after the state for current selection
	 * is resolved.
	 * 
	 * Cancel can be pressed in wizard regardless of the state.
	 * The order of the state's under this enumerator is IMPORTANT!
	 * Because values run from 0...N the states with bigger value
	 * are regarded as more important problems to be reported to 
	 * a user in case there are more than one problem issue in 
	 * multiselection. I.e. problems that prohibit proceeding 
	 * in the wizard.
	 */
	private enum TargetSelStateEnum {
						 EStateNotSet,                        // Initial state that should not exist after target check.
						 EAllTargetCachesUpToDate, 	          // => Can press Next not Finish,
						 ECacheNotGeneratedForSomeTarget,     // => Can press Next or Finish 
						 ECacheNeedsUpdateForSomeTarget,      // => Can press Next or Finish 
						 ESISFileCacheNeedsToBeGenerated, 	  // => Can press Next or Finish, generation will be started
						 EDumpsisDoesNotExist,                // => Cannot press Next nor Finish, dumpsis.exe does not exist, checked when in SIS analysis mode
						 EElftranDoesNotExist,                 // => Cannot press Next nor Finish
						 ECachesIsBeingIndexedForSomeTarget,  // => Cannot press Next nor Finish
						 ENoComponentsForSomeTarget,          // => Cannot press Next nor Finish
						 EInvalidToolchainMix,                // => Cannot press Next nor Finish
						 ESomeTargetNotSupported,             // => Cannot press Next nor Finish
						 ESomeTargetStateNotResolved,         // => Cannot press Next nor Finish
						 ECacheIsBeingGeneratedForSomeTarget // => Cannot press Next nor Finish, severity changed into this level because cache generation forbids actions for whole SDK 
		 								};
	
	/**
	 * Title for the currently selected SDK.
	 */
	private Text sdkNameFieldTitleText;
	
	/**
	 * Name of the currently selected SDK.
	 */
	private Text sdkNameFieldValueText;
		 								
	/**
	 * Title for the currently used build type.
	 */
	private Text buildTypeFieldTitleText;
	
	/**
	 * Name of the currently the currently used build type.
	 */
	private Text buildTypeFieldValueText;
		
	/**
	 * Viewer component for showing available targets.
	 */
	private CheckboxTableViewer buildTargetViewer;
	
	/**
	 * Checkbox that can be used to toggle between showing
	 * of release and debug build type targets.
	 */
	private Button showDebugTargetsInsteadOfReleaseTargetsCheckbox;
	
	/**
	 * Checkbox that can be used to limit amount of showed 
	 * targets into supported and non-empty ones.
	 */
	private Button showOnlySupportedAndNonEmptyTargetsCheckbox;
	
	/**
	 * Content provider for the viewer component.
	 */
	private SelectBuildTargetWizardPageContentProvider contentProvider;
	
	/**
	 * This field determines the current status for the selection of multiple
	 * targets.
	 * IMPORTANT: Value should be set always by using setSelectionState method, not directly
	 *            unless purpose is to initialize, or re-initialize the value!
	 */
	private TargetSelStateEnum selectionState = TargetSelStateEnum.EStateNotSet;

	/**
	 * This filter is used to filter target viewer elements based on the build type.
	 */
	private BuildTargetSelectionBuildTypeFilter buildTypeFilter;

	/**
	 * Accepts in Build Target Selection -wizard page only the targets
	 * that are non-empty or supported. 
	 */
	private BuildTargetSelectionNonEmptyAndNonSuppTargetFilter nonEmptyAndNonSuppTargetFilter;

	/**
	 * Set to <code>true</code> if programmatic selection is ongoing, otherwise <code>false</code>.
	 * If programmatic selection is ongoing and therefore
	 * not need to trigger similar event handling as due to UI selection.
	 */
	private boolean isProgrammaticSelectionOngoingFlag;
	
	/**
	 * Open Add SIS files dialog -button
	 */
	private Button addSISFilesButton = null;
	
	 /**
	 * Constructor.
	 */
	public SelectBuildTargetWizardPage(){
		super(Messages.getString("SelectBuildTargetWizardPage.WizardPageName")); //$NON-NLS-1$

		setTitle(Messages.getString("SelectBuildTargetWizardPage.WizardPageTitle"));			 //$NON-NLS-1$
		setDescription(Messages.getString("SelectBuildTargetWizardPage.WizardPageDescription"));  //$NON-NLS-1$

		// User cannot finish the page before some valid 
		// selection is made.
		setPageComplete(false);
	 }

	/**
	 * Creates checkbox viewer component for showing build targets. 
	 * @param parent Parent composite for the created composite.
	 * @return New <code>CheckboxTableViewer</code> object instance.
	 */
	protected CheckboxTableViewer createBuildTargetTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectBuildTargetWizardPage.TargetType_ColumnText"), //$NON-NLS-1$
														120,
														BuildTargetEntry.TARGET_TYPE_COLUMN_INDEX,
														BuildTargetTableViewerSorter.CRITERIA_TARGET_TYPE));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectBuildTargetWizardPage.ComponentCount_ColumnText"),  //$NON-NLS-1$
														100,
														BuildTargetEntry.COMPONENT_COUNT_COLUMN_INDEX,
														BuildTargetTableViewerSorter.CRITERIA_COMPONENT_COUNT,
														SWT.RIGHT));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("SelectBuildTargetWizardPage.Status_ColumnText"), //$NON-NLS-1$
														160,
														BuildTargetEntry.STATUS_COLUMN_INDEX,
														BuildTargetTableViewerSorter.CRITERIA_STATUS));
		
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.createCheckboxTable(parent, arr);
		
		CheckboxTableViewer tblViewer = new CheckboxTableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tblViewer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
	
	  // Creating one column only sub composite for controls 	
	  Composite c = new Composite(parent, SWT.NONE); 	  
	  final int cols = 1;	  
	  GridLayout gdl = new GridLayout(cols, false);	  
	  GridData gd = new GridData(GridData.FILL_BOTH);
	  c.setLayout(gdl);
	  c.setLayoutData(gd);
	  
	  // Creating grid layout composite for information labels
	  Composite titleAreaComposite = new Composite(c, SWT.NONE);
	  GridLayout gdl2 = new GridLayout(2, false);
	  GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
	  titleAreaComposite.setLayout(gdl2);
	  titleAreaComposite.setLayoutData(gd2);
	  
	  // Adding SDK and build type information labels
	  addSDKInformationLabels(titleAreaComposite);	  
	  addBuildTypeInformationLabels(titleAreaComposite);
	  
	  // Creating build target viewer component
	  buildTargetViewer = createBuildTargetTableViewer(c);
	  GridData targetViewerGd = new GridData(GridData.FILL_BOTH);
	  buildTargetViewer.getControl().setLayoutData(targetViewerGd);
	  buildTargetViewer.setSorter(new BuildTargetTableViewerSorter());
	  
	  // Adding checkboxes and Add SIS files -button inside a new composite object
	  final int compBottomControlsCols = 2;
	  Composite compBottomControls = new Composite(c, SWT.NONE);
	  GridLayout gLToBottomControls = new GridLayout(compBottomControlsCols, false);
	  compBottomControls.setLayout(gLToBottomControls);
	  compBottomControls.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	  
	  // Checkbox for showing debug build type targets instead of release targets
	  String checkBoxLabelText = Messages.getString("SelectBuildTargetWizardPage.ShowDebugBuild_CheckboxCaptionText"); //$NON-NLS-1$
	  // Creating viewer filter for doing filtering according build type	  
	  buildTypeFilter = new BuildTargetSelectionBuildTypeFilter(BuildTargetFilterModeEnum.EShowReleaseTargets);
	  showDebugTargetsInsteadOfReleaseTargetsCheckbox = addCheckBoxControl(compBottomControls, checkBoxLabelText, false, 
			  															   buildTypeFilter, null);	 
	  
	  // Adding SIS file addition dialog launching button		
	  addSISFilesButton = new Button(compBottomControls, SWT.PUSH );
	  addSISFilesButton.setText(Messages.getString("SelectBuildTargetWizardPage.AddSISFile_BtnCaptionText"));  //$NON-NLS-1$
	  addSISFilesButton.addSelectionListener(this);
	  GridData gDToButton = new GridData(GridData.HORIZONTAL_ALIGN_END);
	  addSISFilesButton.setLayoutData(gDToButton);
	  
	  // Checkbox for showing only supported and non-empty targets
	  checkBoxLabelText = Messages.getString("SelectBuildTargetWizardPage.ShowSupportedAndNonEmptyTargets_CheckboxCaptionText"); //$NON-NLS-1$
	  // Creating viewer filter for checkbox  
	  nonEmptyAndNonSuppTargetFilter = new BuildTargetSelectionNonEmptyAndNonSuppTargetFilter(true);
	  GridData cbLayoutData2 = new GridData(GridData.GRAB_HORIZONTAL);
	  showOnlySupportedAndNonEmptyTargetsCheckbox = addCheckBoxControl(compBottomControls, checkBoxLabelText, true, 
			  														   nonEmptyAndNonSuppTargetFilter, cbLayoutData2);	 
	  
	  // Providers cannot be created before all the controls have been created
	  setProvidersAndListenersForBuildTargetViewer();
	  
	  // Setting initial focus of the wizard page
	  setInitialFocus();
	  
	  // Setting control for this page
	  setControl(c);
	
	  // Setting context help ID	
      AppDepPlugin.setContextSensitiveHelpID(getControl(), AppDepHelpContextIDs.APPDEP_WIZARD_PAGE_BUILD_TARGET_SELECT);
	}

	/**
	 * Adds checkbox control with given parameters.
	 * @param compBottomControls Parent composite.
	 * @param checkBoxLabelText	 Label text for the checkbox.
	 * @param initialSelectionStatus set to <code>true</code> if initially selected, otherwise <code>false</code>. 
	 * @param filter Filter to be applied related to checkbox, or <code>null</code> if not used.
	 * @param gdData grid data to be applied related to checkbox, or <code>null</code> if not used.
	 */
	private Button addCheckBoxControl(Composite compBottomControls, String checkBoxLabelText, boolean initialSelectionStatus, 
									  ViewerFilter filter, GridData gdData) {
		  Button checkboxControl  = new Button(compBottomControls, SWT.CHECK);
		  checkboxControl.setText(checkBoxLabelText);
		  checkboxControl.setSelection(initialSelectionStatus); // By default showing release targets
		  checkboxControl.addSelectionListener(this);
		  if(filter != null){
			  buildTargetViewer.addFilter(filter);			  
		  }
		  if(gdData != null){
			  checkboxControl.setLayoutData(gdData);
		  }
		  return checkboxControl;
	}

	/**
	 * Adds SDK information labels.
	 * @param titleAreaComposite parent composite to labels.
	 */
	private void addSDKInformationLabels(Composite titleAreaComposite) {
		  sdkNameFieldTitleText = new Text(titleAreaComposite, READ_ONLY_LABEL_FIELD_STYLEBITS);	  
		  String titleText = Messages.getString("SelectBuildTargetWizardPage.SDK_Str") + ": "; //$NON-NLS-1$ //$NON-NLS-2$
		  sdkNameFieldTitleText.setText(titleText); 
		  sdkNameFieldTitleText.addFocusListener(this);
		  sdkNameFieldValueText = new Text(titleAreaComposite, READ_ONLY_LABEL_FIELD_STYLEBITS);	  	  
		  sdkNameFieldValueText.addFocusListener(this);
		}

	/**
	 * Adds build type information labels.
	 * @param titleAreaComposite parent composite to labels.
	 */
	private void addBuildTypeInformationLabels(Composite titleAreaComposite) {
		buildTypeFieldTitleText = new Text(titleAreaComposite, READ_ONLY_LABEL_FIELD_STYLEBITS);
		buildTypeFieldTitleText.setText(Messages.getString("SelectBuildTargetWizardPage.BuildType_Str") + ": ");	   //$NON-NLS-1$ //$NON-NLS-2$
		buildTypeFieldTitleText.addFocusListener(this);	  
		buildTypeFieldValueText = new Text(titleAreaComposite, READ_ONLY_LABEL_FIELD_STYLEBITS);
		// Setting default build type value...
		buildTypeFieldValueText.setText(BUILD_TYPE_RELEASE_STR);
		// ...and storing the default value also to dialog settings
		updateDialogSettings(SelectSDKWizard.BUILD_TYPE_DESCR_DLG_SETTING_KEY, BUILD_TYPE_RELEASE_STR);
		buildTypeFieldValueText.addFocusListener(this);
		}

	/**
	 * Sets providers and listeners for build target viewer control.
	 */
	private void setProvidersAndListenersForBuildTargetViewer() {
		contentProvider = new SelectBuildTargetWizardPageContentProvider(this);
		  buildTargetViewer.setContentProvider(contentProvider); 
		  buildTargetViewer.setLabelProvider(new SelectBuildTargetWizardPageLabelProvider());
		  buildTargetViewer.setInput(contentProvider);
		  
		  // Adding selection change listener
		  buildTargetViewer.addSelectionChangedListener(new ISelectionChangedListener(){
				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
				 */
				public void selectionChanged(SelectionChangedEvent event) {
					// Triggering status recalculation only if non-programmatic selection
					if(! isProgrammaticSelectionOngoing()){
						 // Skipping programmatic selection because we are sure that event came from UI selection
						recalculateButtonStates(true, null); 								
					}
				}

		  });

	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#setInitialFocus()
	 */
	public void setInitialFocus() {
		buildTargetViewer.getTable().setFocus();			
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#recalculateButtonStates()
	 */
	public void recalculateButtonStates() {
		// By default we check, if programmatic selection is possible
		recalculateButtonStates(false, null);		
	}
	
	/**
	 * Page specific recalculate buttons implementation.
	 * @param skipProgrammaticSelectionCheck set to <code>true</code> if needed to skip, automatic programmatic
	 * 										 selection based on current settings, otherwise set to <code>false</code>.
	 * @param forceCheckedEntriesArr If this is not <code>null</code>, using these entries instead of current selection
	 * 								 if current selection do not have any components (used in double-click event).			
	 */
	public void recalculateButtonStates(boolean skipProgrammaticSelectionCheck, Object[] forceCheckedEntriesArr) {

		try {			
			SelectSDKWizard wiz = (SelectSDKWizard) getWizard();
			AppDepSettings settings = wiz.getSettings();
			Object[] checkedEntriesArr = buildTargetViewer.getCheckedElements();; // Getting all checked targets
			if(checkedEntriesArr.length == 0 && forceCheckedEntriesArr != null){
				// Using forced target set instead
				checkedEntriesArr = forceCheckedEntriesArr;								
			}
			
			// Re-initializing selection state
			selectionState = TargetSelStateEnum.EStateNotSet;
			
			// If programmatic selection is not skipped and there are no target nodes selected...
			if( checkedEntriesArr.length == 0){
				// Trying to make selection based on the settings if allowed according boolean flag
				if(!skipProgrammaticSelectionCheck && makeProgrammaticElementCheckingBasedOnSettings(settings)){
					// Getting all targets checked by programmatic selection
					checkedEntriesArr = buildTargetViewer.getCheckedElements(); 
					if(checkedEntriesArr.length == 0){
						// Trying to avoid internal errors, but this is something that should be trapped during 
						// development time. Internal error messages are not localized.
						String errMsg = "Programmatic selection failed unexpectedly in class '" //$NON-NLS-1$
										+ SelectBuildTargetWizardPage.class.getSimpleName()
										+ "'."; //$NON-NLS-1$
						LogUtils.logInternalErrorAndThrowException(errMsg);
						return;
					}
				}
				else{
					// Failed to make programmatic selection, or programmatic selection was skipped.
					// Making sure that no targets are selected in settings
					settings.clearCurrentlyUsedTargetPlatforms();
					// And showing initial message, because no items are selected currently
					showInitialStateMessage(wiz);
					//.. and returning
					return; 
				}
			}

			// Going through all the selected nodes one by one and setting selection state.	
			// State can be multiple times during method execution because only more severe
			// error state will override existing selection state. 
			// See comment block from TargetSelStateEnum enumeration for detailed info.
			List<BuildTargetEntry> selectedTargetsArr = new ArrayList<BuildTargetEntry>();
			// SDK information is needed for finding out SDK supports required tools (dumpsis.exe and elftran.exe)
			SdkInformation sdkInfo = getSelectedSdk();
			
			// Checking if we are in SIS mode and settings selection state if needed
			checkSISModeStatusAndSetSelectionStateIfNeeded(settings, sdkInfo);
						
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, "----- Checked items START ==>"); // //$NON-NLS-1$
			
			//Recalculate state based on the selection made by a user
			for (int i = 0; i < checkedEntriesArr.length; i++) {
				// Casting selected node to correct type
				BuildTargetEntry targetEntry = (BuildTargetEntry) checkedEntriesArr[i];
				// Debug printing checked items
				DbgUtility.println(DbgUtility.PRIORITY_LOOP, targetEntry.getTargetDescription()); 
				// Added target to target selection array
				selectedTargetsArr.add(targetEntry);
				// Resolves selection state based on target's status
				BuildTargetStatusEnum status = targetEntry.getStatus();
				setSelectionStateBasedOnTargetStatus(settings, sdkInfo, status, targetEntry.getTargetName());
				
				// In case we are about to generate/update cache files...
				if(selectionState == TargetSelStateEnum.ECacheNotGeneratedForSomeTarget
				    ||
				   selectionState == TargetSelStateEnum.ECacheNeedsUpdateForSomeTarget
				    ||
				   selectionState == TargetSelStateEnum.ESISFileCacheNeedsToBeGenerated){					
					//... we finally check, if we cannot start cache generation due to fact
					// that there is already a cache generation process for this SDK ongoing.
					if(settings.isCacheGenerationOngoingForSdk(sdkInfo)){
						setSelectionState(TargetSelStateEnum.ECacheIsBeingGeneratedForSomeTarget);
					}					
				}				
			} 
			
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, "<== Checked items END -----"); // //$NON-NLS-1$
			
			// Checking that the selected combination is otherwise valid.
			// Updates selection state accordingly. Must be called before
			// page completion check and before settings button statuses.
			CheckValidityOfTargetSelection(selectedTargetsArr, settings);
			
			// Storing user selection if we have a valid selection
			boolean isPageCompleted = checkPageCompleteStatus();
			
			//If page is completed it means there is a valid selection existing
			if(isPageCompleted){
				// Storing user selection
				wiz.setUserSelection(sdkInfo, selectedTargetsArr.toArray(new BuildTargetEntry[0]));
			}			

			// Setting buttons states and visible messages according selection state.
			// Must be called before calling updateInformationForNextPage.
			setUserMessageAndButtonState(wiz, isPageCompleted, selectedTargetsArr);		

		} catch (Exception e) {
			e.printStackTrace(); // Development time trace.
			String errMsg = e.getMessage();
			LogUtils.logStackTrace(errMsg , e); // Trace for production environment
			throw new RuntimeException(errMsg); // Mapping exception to anonymous run-time error
		}		
	}

	/**
	 * Shows initial message in state when there are no checked items.
	 * @param wiz Wizard instance for setting button statuses.
	 */
	private void showInitialStateMessage(SelectSDKWizard wiz) {
		this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Startup_InfoMsg"));  //$NON-NLS-1$
		this.setErrorMessage(null);
		setPageComplete(false);
		// Finishing is forbidden
		wiz.disableCanFinish();																								
	}

	/**
	 * Checks possible SIS mode and sets selections state if needed.
	 * @param settings settings used to check mode
	 * @param sdkInfo selected SDK
	 */
	private void checkSISModeStatusAndSetSelectionStateIfNeeded(AppDepSettings settings, SdkInformation sdkInfo) {
		//If we are about to add SIS files to be analyzed
		if(settings.isInSISFileAnalysisMode()){
			//Setting initial state as SIS generation (in the following for loop only 
			//more serious errors will overwrite this state)
			if(!settings.isDumpsisAvailable(sdkInfo)){
				//If we are in SIS Analysis mode and there is no dumpsis.exe available
				setSelectionState(TargetSelStateEnum.EDumpsisDoesNotExist);
			}
			else{
				// Dumpsis available and we can continue into cache creation.
				setSelectionState(TargetSelStateEnum.ESISFileCacheNeedsToBeGenerated);					
			}
		}
	}

	/**
	 * Makes programmatic checking of elements based on the given settings if possible.
	 * @param settings settings to be used for resolving the targets to be checked.
	 * @return <code>true</code> if selection was made successfully, otherwise <code>false</code>.
	 */
	private boolean makeProgrammaticElementCheckingBasedOnSettings(
			AppDepSettings settings) {
		// ...checking if we can create a selection based on the
		// currently used SDK and target platform information.
		Object[] objectArr = resolveTargetsBasedOnCurrentSettings(settings);
		if(objectArr.length  > 0){			
			// No normal events triggered on programmatic selection
			setProgrammaticSelectionOngoingFlag(true);
			for (int i = 0; i < objectArr.length; i++) {
				Object object = objectArr[i];
				buildTargetViewer.setChecked(object, true);				
			}
			// Back to normal event handling
			setProgrammaticSelectionOngoingFlag(false); 
			return true; // Selection was made successfully
		}

		return false; // Could not make selection
	}

	/**
	 * Checks build target's status and sets current selection state flag accordingly.
	 * @param settings current settings
	 * @param sdkInfo Selected SDK
	 * @param status build target status
	 * @param buildTargetId Build target id to based on which set status.
	 */
	private void setSelectionStateBasedOnTargetStatus(AppDepSettings settings,
			SdkInformation sdkInfo, BuildTargetStatusEnum status, String buildTargetId) throws Exception{
		
		switch (status) {
		
		case ENotSupported:
			// If the selected target is not supported, there is no sense to check
			// other selections, because this is the most severe selection error.
			setSelectionState(TargetSelStateEnum.ESomeTargetNotSupported);
			break;

		case EEmptyTarget:
			setSelectionState(TargetSelStateEnum.ENoComponentsForSomeTarget);
			break;

		case ECacheNeedsUpdate:
			if(settings.areToolsRequiredByCoreAvailable(sdkInfo, buildTargetId)){
				// Informing about cache update need if elftran.exe available
				setSelectionState(TargetSelStateEnum.ECacheNeedsUpdateForSomeTarget);									
			}
			else{
				// Otherwise informing used that elftran is not available
				setSelectionState(TargetSelStateEnum.EElftranDoesNotExist);														
			}
			break;

		case ENoCache:
			if(settings.areToolsRequiredByCoreAvailable(sdkInfo, buildTargetId)){
				// Informing about cache creation need if elftran.exe available
				setSelectionState(TargetSelStateEnum.ECacheNotGeneratedForSomeTarget);							
			}
			else{
				// Otherwise informing used that elftran is not available
				setSelectionState(TargetSelStateEnum.EElftranDoesNotExist);														
			}
			break;

		case ECachesIsBeingIndexed:
			setSelectionState(TargetSelStateEnum.ECachesIsBeingIndexedForSomeTarget);
			break;

		case ECacheIsBeingGenerated:
			setSelectionState(TargetSelStateEnum.ECacheIsBeingGeneratedForSomeTarget);
			break;

		case ECacheReady:
			setSelectionState(TargetSelStateEnum.EAllTargetCachesUpToDate);
			break;

		default:
			// Trying to avoid internal errors, but this is something that should be trapped during development time.
			// Because this method should be capable of handling all enum values and should be modified
			// accordingly if new enum values are added. Internal error messages are not localised.
			String errMsg = "Unexpected enum value for enumerator '" //$NON-NLS-1$
							+ BuildTargetStatusEnum.class.getSimpleName()
							+ "': " //$NON-NLS-1$
							+ status.toString();
			LogUtils.logInternalErrorAndThrowException(errMsg);
		}
	}

	/**
	 * Checks that the selected target combination is valid.
	 * @param selectedTargetsArr Selected nodes to be checked for validity.
	 * @param st Settings object to query settings related information from.
	 * @return <code>true</code> if we have valid target selection
	 */
	private void CheckValidityOfTargetSelection(List<BuildTargetEntry> selectedTargetsArr, AppDepSettings st) {
		// Only sensible to check if there is multiselection
		if(selectedTargetsArr.size() > 1){
			
			// Checking that each target use the same default toolchain
			String targetNameFirst = selectedTargetsArr.get(0).getTargetName();			
			try {
				String toolchainFirst = st.getDefaultToolchainForTarget(targetNameFirst).getToolchainName();
				for (int i = 1; i < selectedTargetsArr.size(); i++) {
					String targetName = selectedTargetsArr.get(i).getTargetName();
					String toolchain = st.getDefaultToolchainForTarget(targetName).getToolchainName();
					if(!toolchain.equals(toolchainFirst)){
						setSelectionState(TargetSelStateEnum.EInvalidToolchainMix);
					}				
				}
			} catch (InvalidCmdLineToolSettingException e) {
				// We might get this exception when user has selected platform that is not supported.
				// Just debug printing for development purposes for logging possible internal errors.
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, e.getMessage());
			}
			
		}		
	}

	/**
	 * Checks page completion status based on selection state.
	 * Return status tells if flip to next page is allowed.
	 * @return <code>true</code> if page can be completed, otherwise <code>false</code>.
	 */
	boolean checkPageCompleteStatus(){
		
		boolean pageCompleteStatus = false;

		if(selectionState.equals(TargetSelStateEnum.EAllTargetCachesUpToDate)){
			// Can proceed to component selection
			pageCompleteStatus = true;
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheNotGeneratedForSomeTarget)){
			// Can proceed for cache generation options
			pageCompleteStatus = true;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheNeedsUpdateForSomeTarget)){
			// Can proceed for cache generation options
			pageCompleteStatus = true;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ESISFileCacheNeedsToBeGenerated)){
			// Can proceed for cache generation options or start SIS cache generation
			pageCompleteStatus = true;				
		}				
		else if(selectionState.equals(TargetSelStateEnum.EDumpsisDoesNotExist)){
			// Cannot proceed for cache generation options
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.EElftranDoesNotExist)){
			// Cannot proceed for cache generation options
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ECachesIsBeingIndexedForSomeTarget)){
			// Cannot proceed until index has been created
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheIsBeingGeneratedForSomeTarget)){
			// Cannot proceed until cache has been created for this SKD's target
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ENoComponentsForSomeTarget)){
			// Cannot proceed if some selection does not have components
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.ESomeTargetNotSupported)){
			// Cannot proceed if some target is not supported
			pageCompleteStatus = false;				
		}
		else if(selectionState.equals(TargetSelStateEnum.EInvalidToolchainMix)){
			// Cannot proceed if toolchain mix is not allowed
			pageCompleteStatus = false;				
		}
		else{
			// Trying to avoid internal errors, but this is something that should be trapped during 
			// development time. Internal error messages are not localized.
			LogUtils.logInternalErrorAndThrowException("Unexpected target selection state:" + selectionState.ordinal()); //$NON-NLS-1$
		}
		
		return pageCompleteStatus;
	}
	
	/**
	 * Sets user messages and button enable/disable states according the current selection state.
	 * @param wiz Wizard object needed for settings button enable/disable states.
	 * @param isPageCompleted Page completion status returned by <code>checkPageCompleteStatus</code>.
	 * @param selectedTargetsArr Currently selected targets.
	 */
	private void setUserMessageAndButtonState(SelectSDKWizard wiz, boolean isPageCompleted, List<BuildTargetEntry> selectedTargetsArr) throws Exception{
		
		//
		// Settings user messages and button state according the selection state
		//
		if(selectionState.equals(TargetSelStateEnum.EAllTargetCachesUpToDate)){
			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Press_Next_To_Continue")); //$NON-NLS-1$
			this.setErrorMessage(null);
			setPageComplete(isPageCompleted);
			// Finishing is forbidden
			wiz.disableCanFinish();																								
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheNotGeneratedForSomeTarget)){
			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Cache_Generation_Needed") //$NON-NLS-1$
					+ Messages.getString("SelectBuildTargetWizardPage.Press_Finish_To_Start_Generation") //$NON-NLS-1$
					+ Messages.getString("SelectBuildTargetWizardPage.Press_Next_To_To_Modify_Generation_Options"),  //$NON-NLS-1$
					    IMessageProvider.WARNING);
			this.setErrorMessage(null);
			
			// Updating available cache generation options
			wiz.setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget();
			
			setPageComplete(isPageCompleted);				
			// Pressing Finish in order to 
			// start cache generation is allowed.
			wiz.enableCanFinish(ISelectSDKWizard.FINISH_CACHE_CREATION);			
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheNeedsUpdateForSomeTarget)){
			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Cache_Needs_Update"),  //$NON-NLS-1$
					    IMessageProvider.WARNING);
			this.setErrorMessage(null);
			// Updating available cache generation options
			wiz.setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget();

			setPageComplete(isPageCompleted);				
			// User is also allowed to press Finish, 
			// and start cache update with default generation
			// options.
			wiz.enableCanFinish(ISelectSDKWizard.FINISH_CACHE_CREATION);										
		}
		else if(selectionState.equals(TargetSelStateEnum.ESISFileCacheNeedsToBeGenerated)){
			// Can proceed for cache generation options or start SIS cache generation
			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Cache_Generation_Needed_For_SIS_Files") //$NON-NLS-1$
					+ Messages.getString("SelectBuildTargetWizardPage.Press_Finish_To_Start_Generation") //$NON-NLS-1$
					+ Messages.getString("SelectBuildTargetWizardPage.Press_Next_To_To_Modify_Generation_Options"),  //$NON-NLS-1$
					    IMessageProvider.WARNING);
			this.setErrorMessage(null);
			
			// Updating available cache generation options
			wiz.setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget();
			
			setPageComplete(isPageCompleted);				
			// Pressing Finish in order to 
			// start cache generation is allowed.
			wiz.enableCanFinish(ISelectSDKWizard.FINISH_CACHE_CREATION);			
		}					
		else if(selectionState.equals(TargetSelStateEnum.EDumpsisDoesNotExist)){
			this.setMessage(null);
			this.setErrorMessage(Messages.getString("SelectBuildTargetWizardPage.Dumpsis.exe_Does_Not_Exist_ErrorMsg")); //$NON-NLS-1$
			// Cannot proceed for cache generation options
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();			
		}
		else if(selectionState.equals(TargetSelStateEnum.EElftranDoesNotExist)){
			this.setMessage(null);
			// If we end-up here, there is at least a single target selected
			String targetId = selectedTargetsArr.get(0).getTargetName(); // and all selected targets must use same toolchain (=dump tool)
			String errMsgFormatString = Messages.getString("SelectBuildTargetWizardPage.BinaryDumpTool_Does_Not_Exist_ErrorMsg"); //$NON-NLS-1$		
			this.setErrorMessage(String.format(errMsgFormatString, wiz.getSettings().getCurrentlyUsedCoreDumpToolName(targetId))); 
			// Cannot proceed for cache generation options
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();			
		}
		else if(selectionState.equals(TargetSelStateEnum.ECachesIsBeingIndexedForSomeTarget)){
			this.setMessage(null);

			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Cache_Index_Not_Yet_Created"),  //$NON-NLS-1$
				    IMessageProvider.WARNING);
		    this.setErrorMessage(null);
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();										
		}
		else if(selectionState.equals(TargetSelStateEnum.ECacheIsBeingGeneratedForSomeTarget)){
			this.setMessage(Messages.getString("SelectBuildTargetWizardPage.Cache_Is_Being_Generated"),  //$NON-NLS-1$
					    IMessageProvider.WARNING);
			this.setErrorMessage(null);
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();			
		}
		else if(selectionState.equals(TargetSelStateEnum.ENoComponentsForSomeTarget)){
			// Disabling operations if there are not components built
			this.setMessage(null);
			this.setErrorMessage(Messages.getString("SelectBuildTargetWizardPage.Selected_Target_Does_Not_Have_BuiltComponents")); //$NON-NLS-1$
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();			
		}
		else if(selectionState.equals(TargetSelStateEnum.ESomeTargetNotSupported)){
			this.setMessage(null);
			this.setErrorMessage(Messages.getString("SelectBuildTargetWizardPage.Target_Not_Supported")); //$NON-NLS-1$
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();			
		}
		else if(selectionState.equals(TargetSelStateEnum.EInvalidToolchainMix)){
			// Disabling operations if there cross-SDK/Platform target selections 
			this.setMessage(null);
			this.setErrorMessage(Messages.getString("SelectBuildTargetWizardPage.Invalid_Toolchain_Mix_ErrorMsg")); //$NON-NLS-1$
			setPageComplete(isPageCompleted);				
			// Finishing is forbidden
			wiz.disableCanFinish();									
		}
		else{
			// Trying to avoid internal errors, but this is something that should be trapped during 
			// development time. Internal error messages are not localized.
			LogUtils.logInternalErrorAndThrowException("Unexpected target selection state:" + selectionState.ordinal()); //$NON-NLS-1$
		}
	}
	
	/**
	 * Resolve the target node based on the given settings.
	 * @param settings Settings to get environment data from,
	 * @return Wizard node matching with current settings, or 
	 *         <code>null</code> if not found. 
	 */
	private Object[] resolveTargetsBasedOnCurrentSettings(AppDepSettings settings) {
		ArrayList<Object> objs = new ArrayList<Object>();
		SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();
		ITargetPlatform[] targetPlatforms = settings.getCurrentlyUsedTargetPlatforms();
		String buildTypeString = settings.getBuildType().getBuildTypeName();
		if(sdkInfo != null && targetPlatforms.length > 0){
			// Trying to resolve correct nodes
			for (int i = 0; i < targetPlatforms.length; i++) {
				ITargetPlatform platform = targetPlatforms[i];
				String targetPlatformId = platform.getId();
				if(!targetPlatformId.equals(AppDepSettings.TARGET_TYPE_ID_SIS)){
					Object obj = contentProvider.find(sdkInfo.getSdkId(),targetPlatformId,
	                        buildTypeString);
	                if(obj!= null){
	                	objs.add(obj);
	                }									
				}
			}
		}
		return objs.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		try {
			
			final Widget w = event.widget;
			
			// Addi SIS files -button pressed?
			if(w.equals(addSISFilesButton)){
				addSISFilesButtonPressed();
				return;
			}
			
			// Otherwise we have been registered only to listen checkboxes, so we are always sure 
			// that we do not get any other events than those already checked above.
			Runnable checkboxRunnable = new Runnable(){

				public void run() {
					if (w.equals(showOnlySupportedAndNonEmptyTargetsCheckbox)) {
							boolean isSelected = showOnlySupportedAndNonEmptyTargetsCheckbox.getSelection();
							if(isSelected){
								nonEmptyAndNonSuppTargetFilter.setFilterEnabled(true);
							}
							else{
								nonEmptyAndNonSuppTargetFilter.setFilterEnabled(false);
							}
							// Refreshing page contents
							refresh();
							// And updating button statuses	and message area
							recalculateButtonStates();
						}
						else if (w.equals(showDebugTargetsInsteadOfReleaseTargetsCheckbox)) {
							// Always clearing already made target platform selections when switching mode
							SelectSDKWizard wiz = (SelectSDKWizard) getWizard();
							wiz.getSettings().clearCurrentlyUsedTargetPlatforms();
							// Switching mode based on the checkbox selection
							boolean isSelected = showDebugTargetsInsteadOfReleaseTargetsCheckbox.getSelection();
							if(isSelected){
								buildTypeFilter.setFilterMode(BuildTargetFilterModeEnum.EShowDebugTargets);
								buildTypeFieldValueText.setText(BUILD_TYPE_DEBUG_STR);
								updateDialogSettings(SelectSDKWizard.BUILD_TYPE_DESCR_DLG_SETTING_KEY, BUILD_TYPE_DEBUG_STR);
								buildTypeFieldValueText.pack();
							}
							else{
								buildTypeFilter.setFilterMode(BuildTargetFilterModeEnum.EShowReleaseTargets);
								buildTypeFieldValueText.setText(BUILD_TYPE_RELEASE_STR);
								updateDialogSettings(SelectSDKWizard.BUILD_TYPE_DESCR_DLG_SETTING_KEY, BUILD_TYPE_RELEASE_STR);
								buildTypeFieldValueText.pack();
							}
							// Refreshing page contents
							refresh();
							// And updating button statuses	and message area	
							recalculateButtonStates();
						}
					}

			};

			// Showing busy cursor while updating UI because may take some time
			Display d = getShell().getDisplay();
			BusyIndicator.showWhile(d, checkboxRunnable);			
			
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
		
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.job.IJobCompletionListener#backgroundJobCompleted(com.nokia.s60tools.appdep.core.job.IManageableJob)
	 */
	public void backgroundJobCompleted(IManageableJob jobObject) {		
		refreshTreeViewerAndButtonsStatusesInUIThread();
	}

	/**
	 * Refreshes tree view and updates button statuses.
	 */
	private void refreshTreeViewerAndButtonsStatusesInUIThread() {
		
		Runnable refreshTreeViewerAndButtonsStatusesRunnable = new Runnable(){
			public void run(){
				try {
					// Refreshing page contents
					refresh();
					recalculateButtonStates();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		// Update request will be done in its own thread
		// in order not to cause invalid thread access
		Display.getDefault().asyncExec(refreshTreeViewerAndButtonsStatusesRunnable);		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		Widget w = e.widget;
		if(w.equals(sdkNameFieldTitleText)
			||
			w.equals(sdkNameFieldValueText)	
			||
			w.equals(buildTypeFieldTitleText)	
			||
			w.equals(buildTypeFieldValueText)	
				){
			setInitialFocus();			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		// No need to do anything		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.ICacheIndexListener#cacheIndexCreationCompleted(com.nokia.s60tools.appdep.core.data.CacheIndex)
	 */
	public void cacheIndexCreationCompleted(CacheIndex cacheIndexObj) {
		refreshTreeViewerAndButtonsStatusesInUIThread();
	}

	/**
	 * @return the selectionState
	 */
	public TargetSelStateEnum getSelectionState() {
		return selectionState;
	}

	/**
	 * @param selectionState the selectionState to set
	 */
	public void setSelectionState(TargetSelStateEnum selectionState) {
		// Changing current selection state ordinal is greater than previous
		if(selectionState.ordinal() > this.selectionState.ordinal()){
			this.selectionState = selectionState;			
		}
	}

	/**
	 * Gets build target viewer component.
	 * @return build target viewer component.
	 */
	public CheckboxTableViewer getBuildTargetViewer() {
		return buildTargetViewer;
	}

	/**
	 * Gets the SDK selected for the wizard page.
	 * @return SDK selected for the wizard page.
	 */
	public SdkInformation getSelectedSdk() {
		AppDepSettings settings = getSettings();
		return settings.getCurrentlyUsedSdk();
	}

	/**
	 * Gets settings used for this wizard page.
	 * @return settings used for this wizard page.
	 */
	public AppDepSettings getSettings() {
		SelectSDKWizard wiz = (SelectSDKWizard) getWizard();
		return wiz.getSettings();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.IRefreshable#refresh()
	 */
	public void refresh() {
		// Refreshing selected SDK text if needed
		String sdkId = getSelectedSdk().getSdkId();
		if(!sdkId.equals(sdkNameFieldValueText.getText())){
			sdkNameFieldValueText.setText(sdkId);
			sdkNameFieldValueText.pack();			
		}
		
		// Refreshing target table
		buildTargetViewer.refresh();				
	}

	/**
	 * Checking if programmatic selection is ongoing and therefore
	 * not need to trigger similar event handling as due to UI selection.
	 * @return <code>true</code> if programmatic selection is ongoing, otherwise <code>false</code>.
	 */
	private boolean isProgrammaticSelectionOngoing() {
		return isProgrammaticSelectionOngoingFlag;
	}	
	
	/**
	 * Sets or unsets programmatic selection flag.
	 * @param isProgrammaticSelectionOngoingFlag <code>true</code> if programmatic selection is ongoing, otherwise <code>false</code>.
	 */
	public void setProgrammaticSelectionOngoingFlag(
			boolean isProgrammaticSelectionOngoingFlag) {
		this.isProgrammaticSelectionOngoingFlag = isProgrammaticSelectionOngoingFlag;
	}
	
	/**
	 * Handles Add SIS files -button press event.
	 */
	public void addSISFilesButtonPressed() {
		Shell sh = AppDepPlugin.getCurrentlyActiveWbWindowShell();
		AddSISFilesDialog entryDialog = new AddSISFilesDialog(sh);
		entryDialog.create();
		// If already selected some set of SIS files getting the list...
		if(getSettings().isInSISFileAnalysisMode()){
			//...and setting the initial list
			String[] filesForAnalysis = getSettings().getSISFilesForAnalysis();
			entryDialog.setInitialSISFileSet(filesForAnalysis);
		}
		int userSelection = entryDialog.open();
		if(userSelection == Window.OK){
			// Getting selected SIS files
			String[] selectedSISFiles = entryDialog.getSelectedSISFiles();
			if(selectedSISFiles.length > 0){
				// Entering to SIS file mode...
				getSettings().setIsInSISFileAnalysisMode(true);
				// ...and setting files to current settings
				getSettings().setSISFilesForAnalysis(selectedSISFiles );
			}
			else{
				// Disabling SIS file mode
				getSettings().setIsInSISFileAnalysisMode(false);
				getSettings().setSISFilesForAnalysis(null);					
			}	
			// SIS analysis mode may have toggled, therefore needing to update button status.
			recalculateButtonStates();
		}
	}

	/**
	 * Stores value for given key into dialog settings.
	 * @param key key for the setting
	 * @param value value for the setting
	 */
	private void updateDialogSettings(String key, String value) {
		SelectSDKWizard wiz = (SelectSDKWizard) getWizard();
		wiz.updateDialogSettings(key, value);
	}
	
	public SelectBuildTargetWizardPageContentProvider getContentProvider(){
		return contentProvider;
	}
	
} 

