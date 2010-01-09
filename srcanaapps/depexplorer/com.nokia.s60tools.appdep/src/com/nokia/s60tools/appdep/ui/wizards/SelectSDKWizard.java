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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;

import com.nokia.s60tools.appdep.core.AppDepCacheIndexManager;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.CacheGenerationOptions;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.core.job.AppDepJobManager;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.dialogs.AppDepMessageBox;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.ui.views.listview.ListView;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.ui.UiUtils;
import com.nokia.s60tools.ui.wizards.S60ToolsWizard;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * SDK Selection wizard implementation.
 */
public class SelectSDKWizard extends S60ToolsWizard  implements ISelectSDKWizard {

	//
	// Constants
	//
	/**
	 * Dialog settings key used for storing currently selected/used build type.
	 */
	public static final String BUILD_TYPE_DESCR_DLG_SETTING_KEY = "BuildTypeDescrKey"; //$NON-NLS-1$
	
	//
	// Private members
	//	
	private boolean canFinish = false;
	private int exitStatus = ISelectSDKWizard.CANCEL;
	private SelectSDKWizardPage sdkPage = null;
	private SelectBuildTargetWizardPage buildTargetPage = null;
	private CacheGenerationOptionsWizardPage cacheGenerOptPage = null;
	private SelectComponentWizardPage compSelPage = null;
	AppDepSettings settings = null;	
	private AppDepJobManager backgroundJobManager = null;
	private AppDepCacheIndexManager cacheIndexMgr = null;
	private final boolean openSDKSelectionPage;
	static private final ImageDescriptor bannerImgDescriptor = UiUtils.getBannerImageDescriptor(ImageResourceManager.getImageDescriptor(ImageKeys.WIZARD_BANNER));
	private final boolean showDuplicateComponentInfo;
	
	/**
	 * Constructor. 
	 * @param settings Settings object used for the wizard.
	 * @param openSDKSelectionPage	<code>true</code> if we SDK selection page is the starting page, otherwise component selection page is shown.
	 * @param showDuplicateComponentInfo set to <code>true</code> if one wants to inform user about the duplicate components.
	 */	
	public SelectSDKWizard(AppDepSettings settings,
							boolean openSDKSelectionPage, boolean showDuplicateComponentInfo) {
		super(bannerImgDescriptor);
		this.settings = settings;
		this.openSDKSelectionPage = openSDKSelectionPage;
		this.showDuplicateComponentInfo = showDuplicateComponentInfo;
		// Creating dialog settings object with root section
		setDialogSettings(new DialogSettings("DialogSettingRootSection")); //$NON-NLS-1$
		
		backgroundJobManager = AppDepJobManager.getInstance();
		cacheIndexMgr = AppDepCacheIndexManager.getInstance();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.wizards.S60ToolsWizard#addPages()
	 */
	public void addPages() {
		
		//By default, the order of the pages are added is also
		// the order they are represented to the user.
		sdkPage = new SelectSDKWizardPage();
		addPage(sdkPage);
		
		compSelPage = new SelectComponentWizardPage(showDuplicateComponentInfo);
		
		cacheGenerOptPage = new CacheGenerationOptionsWizardPage();
		addPage(cacheGenerOptPage);
		
		buildTargetPage = new SelectBuildTargetWizardPage();
		addPage(buildTargetPage);
		cacheIndexMgr.addListener(buildTargetPage);
		backgroundJobManager.addListener(buildTargetPage);
		
		addPage(compSelPage);
		setWindowTitle(sdkPage.getName());
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getStartingPage()
     */
    public IWizardPage getStartingPage() {
    	
    	// Checking if user has especially asked to open
    	// SDK selection page
    	if(openSDKSelectionPage){
    		return sdkPage;
    	}
    	
    	// Otherwise selection depends on the current settings.
    	// Checking if currently used SDK/Target Platform 
    	// has been already configured...
		SdkInformation sdkInfo = settings.getCurrentlyUsedSdk();
		ITargetPlatform[] targets =	settings.getCurrentlyUsedTargetPlatforms();
		if(sdkInfo == null  || targets.length == 0){
			// Not configured => using default start page (Firstly added page)
			return super.getStartingPage();
		}			
    	// SDK/Target Platform has been configured, and
    	// therefore going directly to component selection 
		// and forcing the update of page contents before...
		compSelPage.refresh();
		//...setting initial focus for the page
		compSelPage.setInitialFocus();
    	// ...returning the page as result
    	return compSelPage;
    }
	        
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		
		//If we are handling SIS adding, and we are at SIS page, we go forward to SDK selection
		if(page.equals(sdkPage)){
			return buildTargetPage;
		}
		else if(page.equals(buildTargetPage)){
			if(this.exitStatus == ISelectSDKWizard.FINISH_CACHE_CREATION){
				return cacheGenerOptPage;
			}
			else{
				return compSelPage;
			}
		}
		
		// Otherwise we are already in last page
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getPreviousPage(IWizardPage page) {
		
		if(
		   page.equals(compSelPage)
			||
		   page.equals(cacheGenerOptPage)
		   ){
			return buildTargetPage;
		}
		else if(page.equals(buildTargetPage)){
			return sdkPage;
		}
		
		
		// Otherwise we are already in first page
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		backgroundJobManager.removeListener(buildTargetPage);
		cacheIndexMgr.removeListener(buildTargetPage);
		
		// Storing cache generation options, if needed
		IWizardPage page = getContainer().getCurrentPage();
		if(page != null){			
			if(page.equals(buildTargetPage) || page.equals(cacheGenerOptPage)){
				CacheGenerationOptions cacheGenerOpts;
				try {
					
					cacheGenerOpts = new CacheGenerationOptions(cacheGenerOptPage.getCacheGenerationToolchain(),
							                                    cacheGenerOptPage.getCacheGenerationLibType());
					settings.setCacheGenerOptions(cacheGenerOpts);					
					
				} catch (InvalidCmdLineToolSettingException e) {
					e.printStackTrace();
					String msg = Messages.getString("SelectSDKWizard.Failed_To_Set_Gener_Options") //$NON-NLS-1$
									+ e.getMessage();
					AppDepMessageBox msgBox = new AppDepMessageBox(this.getShell(), msg, 
							                                       SWT.ICON_ERROR | SWT.OK);	
					msgBox.open();
				}
			}
		}
		ListView.clear();
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel() {
		backgroundJobManager.removeListener(buildTargetPage);		
		cacheIndexMgr.removeListener(buildTargetPage);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		return canFinish;
	}
	
	/**
	 * Stores the user selection made in SDK selection page.
	 * @param selectedSdkNode The selectedSdk node.
	 */
	public void setUserSelection(SdkTreeViewNode selectedSdkNode) {
		SdkInformation sdkInfo = selectedSdkNode.getSdkInfo();
		SdkInformation currentlyUsedSdk = settings.getCurrentlyUsedSdk();
		if(currentlyUsedSdk != null && !sdkInfo.getSdkId().equals(currentlyUsedSdk.getSdkId())){
			// New SDK selected resettings old target platform settings
			settings.clearCurrentlyUsedTargetPlatforms();				
		}
		buildTargetPage.getContentProvider().clearBuildTargetsList();
		// Storing SDK selection setting
		settings.setCurrentlyUsedSdk(sdkInfo);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.ISelectSDKWizard#setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget()
	 */
	public void setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget() {
		try{
			cacheGenerOptPage.setDefaultCacheGenerationOptionsBasedOnTheSelectedTarget();
		}
		 catch (Exception e) {
				AppDepConsole.getInstance().println(Messages.getString("SelectSDKWizard.Failed_To_Set_Default_Gener_Options"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}		
	}

	/**
	 * Sets canFinish flag to <code>true</code> and updates exit status.
	 * @param exitStatus New exist status.
	 */
	public void enableCanFinish(int exitStatus) {
		this.canFinish = true;
		this.exitStatus = exitStatus;
		IWizardContainer container = getContainer();
		container.updateButtons();
	}

	/**
	 * Sets canFinish flag <code>false</code> and updates exit status
	 * by default to <code>IAppDepWizard.CANCEL</code>.
	 */
	public void disableCanFinish() {
		this.canFinish = false;
		this.exitStatus = ISelectSDKWizard.CANCEL;
		getContainer().updateButtons();
	}	
	
	/**
	 * Updates currently selected component name.
	 * @param componentName Name of the component to be set as selected one.
	 * @param targetPlatform Target platform of the component to be set as selected one. 
	 */
	public void updateAnalyzedComponentSelection(String componentName, ITargetPlatform targetPlatform){
		settings.setCurrentlyAnalyzedComponentName(componentName);
		settings.setCurrentlyAnalyzedComponentTargetPlatform(targetPlatform);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getWindowTitle()
     */
    public String getWindowTitle() {
    	//
    	// Overriding default implementation and getting the
    	// window title from the current page instead.
    	//
    	IWizardContainer container = getContainer();
    	if(container != null){
    		IWizardPage currPage = container.getCurrentPage();
    		if(currPage != null){
    			return currPage.getName();
    		}
        	else{
        		return super.getWindowTitle();
        	}
    	}
    	else{
    		return super.getWindowTitle();
    	}
    }
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.IAppDepWizard#getExitStatus()
	 */
	public int getExitStatus() {
		return exitStatus;
	}

	/**
	 * Gets component iterator for the currently configured/selected.
	 * SDK/Target Platform.
	 * @param duplicateItemsList Out parameter that contains the list of duplicate
	 *                           components found from the selected targets.
	 * @return Component Iterator, or <code>null</code> if component 
	 *         iterator cannot be built.
	 */
	public Iterator<ComponentListNode> getComponentIterator(List<String> duplicateItemsList) {
		// Delegating request further
		try {
			return CacheIndex.getComponentIteratorForGivenSettings(settings, duplicateItemsList).iterator();			
		} catch (CacheFileDoesNotExistException e) {
			// Cache is just not yet created for some SDK/Target platform
			// that is currently selected in the selection wizard.
			return null;
		} catch (IOException e) {
			// This denotes some error that should not happen, and needs stack trace
			e.printStackTrace();
			AppDepConsole.getInstance().println(e.getMessage(), AppDepConsole.MSG_ERROR);
			return null;
		}
	}

	/**
	 * @return Settings object used by this wizard instance.
	 */
	public AppDepSettings getSettings() {
		return settings;
	}

	/**
	 * Saves current user selection to currently used settings.
	 * @param sdkInfo selected SDK
	 * @param selectedBuildTargetEntries selected build target entries.
	 */
	public void setUserSelection(SdkInformation sdkInfo, BuildTargetEntry[] selectedBuildTargetEntries) {
		try {
			// Making target platform name arrays list
			ArrayList<String> buildTargetNames = new ArrayList<String>();			
			for (int i = 0; i < selectedBuildTargetEntries.length; i++) {
				buildTargetNames.add(selectedBuildTargetEntries[i].getTargetName());				
			}

			//Making sure that SIS target is included in selection in case in SIS mode
			addSISTargetIfNeeded(buildTargetNames);
			
			settings.updateCurrentlyUsedSDKAndTargetPlatforms(
														sdkInfo,
					                                    buildTargetNames.toArray(new String[0]),
					                                    // Both targets have the same build type
					                                    selectedBuildTargetEntries[0].getBuildType()
					                                        );
		} catch (InvalidCmdLineToolSettingException e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Failed_To_Update_Current_Settings"), IConsolePrintUtility.MSG_ERROR); //$NON-NLS-1$
		}
	}

	/**
	 * Adding SIS target to given target platform array if we are on SIS mode.
	 * @param targetPlatformNames ArrayList to add SIS target.
	 */
	public void addSISTargetIfNeeded(ArrayList<String> targetPlatformNames) {
		// In SIS mode we need to add an extra target
		if(settings.isInSISFileAnalysisMode()){
			if(! targetPlatformNames.contains(AppDepSettings.TARGET_TYPE_ID_SIS)){
				targetPlatformNames.add(AppDepSettings.TARGET_TYPE_ID_SIS);				
			}
		}
	}

	/**
	 * Stores value for given key into dialog settings.
	 * @param key key for the setting
	 * @param value value for the setting
	 */
	public void updateDialogSettings(String key, String value) {
		IDialogSettings dialogSettings = getDialogSettings();
		dialogSettings.put(key, value);			
	}

	/**
	 * Gets value for given key from dialog settings.
	 * @param key key for the setting
	 */
	public String getDialogSetting(String key) {
		IDialogSettings dialogSettings = getDialogSettings();
		String value = dialogSettings.get(key);
		if(value == null){
			// Returning empty string if settings not yet available
			return "";  //$NON-NLS-1$
		}
		return value;
	}

}
