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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheDataConstants;
import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.sdk.SdkInformation;

/**
 * Selection change listener for main view.
 */
public class MainViewSelectionChangedListener implements
		ISelectionChangedListener {

	//
	// Members
	//
	private final MainView view;
	private final ArrayList<ImportFunctionData> importFunctionsArrayList;
	private final ArrayList<ExportFunctionData> exportFunctionsArrayList;
	private boolean propertiesFoundSuccessfully;
	
	/**
	 * Default constructor.
	 * @param view Reference to main view
	 * @param importFunctionsArrayList Import function list to be updated on selection.
	 * @param exportFunctionsArrayList Export function list to be updated on selection.
	 */
	public MainViewSelectionChangedListener(MainView view, 
										    ArrayList<ImportFunctionData> importFunctionsArrayList,
										    ArrayList<ExportFunctionData> exportFunctionsArrayList){
		this.view = view;
		this.importFunctionsArrayList = importFunctionsArrayList;
		this.exportFunctionsArrayList = exportFunctionsArrayList;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		try {			
			
			Object obj = view.getComponentTreeSelectedElement();
			
			// By default component properties data reference
			// is set to null
			view.setSelectedComponentPropertiesData(null);
						
			if(obj == null){
				// We might get null-selections when
				// tree is expanded/collapsed.
				return;
			}
			else{
				// Storing the selection for further reference in case 
				// selection is lost because of the user decides
				// to use 'Go Into' functionality which loses the selection.
				view.setMostRecentlySelectedComponentNode(obj);
			}
						
			// The object is for sure an instance of ComponentNode
			ComponentNode node = (ComponentNode) obj;
			// Storing name of the selected component (that might be name of the concrete component replacing generic one).
			String selectedComponentName = node.getName();
			// Component node instance which is selected or selected leaf node refers to
			ComponentParentNode componentNodeInstance;
			// Storing original component name in here, if we are replacing a generic component
			String selectedComponentOriginalName;
			if(node instanceof ComponentParentNode){
				componentNodeInstance = (ComponentParentNode)node;
			}
			else{
				// We have leaf node 
				ComponentLinkLeafNode leafNode = (ComponentLinkLeafNode)node;
				// Getting referred component
				componentNodeInstance = leafNode.getReferredComponent();
			}
			// Storing name of the original component if needed
			if(componentNodeInstance.wasGenericComponent()){
				selectedComponentOriginalName = componentNodeInstance.getOriginalName();	
			}else{
				// No concrete component replacement done and therefore selected component is the original one.
				selectedComponentOriginalName = selectedComponentName;		
			}
			
			String parentComponentName = node.getParent().getName();
			
			AppDepSettings st = AppDepSettings.getActiveSettings();
			SdkInformation sdkInfo = st.getCurrentlyUsedSdk();

			//Updating tool bar text if the view is fully populated
			// i.e. dependency search is finished.
			if(!MainView.isDependencySearchOngoing()){
				String descr = null;
				if(sdkInfo != null){
					descr = 
							Messages.getString("MainViewSelectionChangedListener.SDK_Prefix") //$NON-NLS-1$
							+ sdkInfo.getSdkId()
							+ " - " //$NON-NLS-1$
							+  st.getCurrentlyUsedTargetPlatformsAsString()
							+ " " //$NON-NLS-1$
							+  st.getBuildType().getBuildTypeDescription()
							+ Messages.getString("MainViewSelectionChangedListener.Component_Prefix") //$NON-NLS-1$
							+ node.getFullName();
							
					view.updateDescription(descr);
				}
			}
			
			// Fetching property information for the selected component
			//For properties, cannot use selectedComponentName, because it may point to generic component
			propertiesFoundSuccessfully = updateComponentPropertyInformation(selectedComponentName, st.getCurrentlyAnalyzedComponentTargetPlatform());
			
			// Clearing old information for imported functions
			importFunctionsArrayList.clear();
			// and also for exported ones
			exportFunctionsArrayList.clear();

			// Is currently used SDK configured?
			if(sdkInfo != null){
				// Export functions array list can be populated
				// if component is not an EXE file, because there
				// might exist EXE and DLL files with the same name,
				// and the export function information is only available
				// for DLLs. If EXE files were passed further, it would
				// give the information for the DLL with the same name.
				if(! isExeFile(selectedComponentName)){
					// For exported functions using always concrete component if available
					updateExportFunctionsArray(selectedComponentName);
				}				
			}
			
			// Checking if root component has been selected
			ComponentParentNode pNode = null;
			if(obj instanceof ComponentParentNode){
				pNode = (ComponentParentNode) obj;
				if(pNode.isRootComponent()){
					// Disabling action that are not valid for root component
					view.disableSetAsNewRootAction();
					// The showing of parent import functions
					// is not applicable for the root component
					// Just refreshing view when old imported
					// functions information has been cleared.
					refreshMainView();
					return;
				}
			}
			else if(obj instanceof ComponentLinkLeafNode){
				ComponentLinkLeafNode linkNode = (ComponentLinkLeafNode) obj;
				pNode = linkNode.getReferredComponent();
			}

			// Also link node can refer to root component
			if(pNode.isMissing() || pNode.isRootComponent()){
				view.disableSetAsNewRootAction();							
				view.enableLocateComponentAction();
			}
			else{
				view.enableSetAsNewRootAction();
				view.disableLocateComponentAction();
			}
			
			// Import functions can be fetched selected node is not root component
			updateImportFunctionsArray(selectedComponentOriginalName, parentComponentName);
			
			// Checks if there are unresolved import function names and if those can be fetched from export function data
			checkForUnresolvedImportFunctionNames();
			
			// Finally asking from main view that content providers gets updated data for showing.
			refreshMainView();
		}
		catch (CacheIndexNotReadyException e) {
			//
			// This may happen when no SDK selection has been made and therefore
			// no cache indexes has been created.
			// There is only single node in tree view with help text available
			// for selection that for sure raises this exception.
			//
			// => can be ignored safely
			//
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks if there are unresolved import function names and if those can be fetched from export function data.
	 * This method does not have any effects if there are no unresolved imported functions, and if there are no
	 * exported function data available.
	 * @precondition if there are unresolved imported function names, there has to be valid exported functions data 
	 *               available for successful operation.
	 * @postcondition names of unresolved imported function names in <code>importFunctionsArrayList</code> are replaced 
	 *                by function names got from <code>exportFunctionsArrayList</code>.
	 */
	private void checkForUnresolvedImportFunctionNames() {
		// In case there is no exported data available, there is no use to continue the checking
		if(exportFunctionsArrayList.size() == 0) return;
		
		// Otherwise checking imported functions array for unresolved function names
		for (int i = 0; i < importFunctionsArrayList.size(); i++) {
			ImportFunctionData importFunc = importFunctionsArrayList.get(i);
			if(importFunc.getFunctionName().endsWith(CacheDataConstants.FUNC_NAME_NOT_RESOLVED)){
				String funcName = getFuncNameFromExportedFunctions(importFunc.getFunctionOrdinalAsInt()); 
				if(funcName !=  null){
					importFunc.setFunctionName(funcName);
				}
			}
		}
	}

	/**
	 * Gets function name from exported function array list with given ordinal. 
	 * @param ordinal function ordinal to get name for.
	 * @return Function name or <code>null</code> if cannot be resolved.
	 */
	private String getFuncNameFromExportedFunctions(int ordinal) {
		// If we have valid ordinal that exists also in exported functions data...
		if(ordinal > 0 && ordinal <= exportFunctionsArrayList.size()){
			//...returning the function name for it
			return exportFunctionsArrayList.get(ordinal-1).getFunctionName();
		}
		//  Could not found match from exported function data 
		return null;
	}

	/**
	 * Checks is given file name has EXE extension, or not.
	 * @param fileNameStr File name to be checked for.
	 * @return Returns <code>true</code> if file has EXE extension, 
	 *         otherwise <code>false</code>.
	 */
	private boolean isExeFile(String fileNameStr) {
		int extIndex = fileNameStr.lastIndexOf("."); //$NON-NLS-1$
		if(extIndex != -1){
			String extStr = fileNameStr.substring(extIndex+1, fileNameStr.length());
			if(extStr.equalsIgnoreCase("EXE")){ //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	/**
	 * Performs refresh and other operation needed to
	 * run after refresh. 
	 */
	private void refreshMainView() {
		view.refresh();
		if(propertiesFoundSuccessfully){
			// This resizes value column which enables the seeing
			// of all the capabilities defined for the component.
			view.performValueColumnPackToPropertiesTab();			
		}
	}

	/**
	 * Updates importFunctionsArrayList with information that was found
	 * for the selected component.
	 * @param selectedComponentName Name of the component that has been selected.
	 * @param parentComponentName Parent component of the selected component.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 * @throws CacheFileDoesNotExistException 
	 */
	private void updateImportFunctionsArray(String selectedComponentName, String parentComponentName) throws FileNotFoundException, IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException {
		
		importFunctionsArrayList.clear(); // Making sure that array is cleared
		try {
			// Non-root component => seeking for the parent imported functions
			importFunctionsArrayList.addAll(
											MainViewDataPopulator.getParentImportedFunctionsForComponent(
																	parentComponentName,
																	selectedComponentName)
											);												             												
		} catch (Exception e) {
			// Catching exceptions here, because throwing them upper level would prevent fetching of component properties.
			e.printStackTrace();
		}
	}

	/**
	 * Updates property information for the currently
	 * selected component.
	 * @param selectedComponentName Name of the component that has been selected.
	 * @param targetPlatform Target platform restriction, or <code>null</code> if target platform does not matter.
	 * @return Returns <code>true</code> of component properties was found successfully, 
	 *         otherwise returns <code>false</code>.
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 */
	private boolean updateComponentPropertyInformation(String selectedComponentName, ITargetPlatform targetPlatform) throws IOException, CacheIndexNotReadyException {
		
		boolean propertiesFound = false;
		
		try {
			
			ComponentPropertiesData comPropData = MainViewDataPopulator
														.getComponentPropertyArrayForComponent(selectedComponentName, targetPlatform);
			propertiesFound = true;
			view.setSelectedComponentPropertiesData(comPropData);
		} catch (NoSuchElementException e1) {
			// This can be ignored because there may be components 
			// that does not exist in cache at all
		} catch (java.lang.NullPointerException e1) {
			// This can be ignored because we'll get this if
			// currently used SDK is not yet configured and we
			// select the root node that just advices the user
			// to double-click the root node.
		} catch (CacheFileDoesNotExistException e2) {
			// This might happen during dialog to view transitions
			// When cache file does not exist yet.
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return propertiesFound;
	}

	/**
	 * Updates exportFunctionsArrayList with information that was found
	 * for the selected component.
	 * @param selectedComponentName Name of the component that has been selected.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 * @throws CacheFileDoesNotExistException 
	 */
	private void updateExportFunctionsArray(String selectedComponentName) throws FileNotFoundException, IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException {

		try {
			exportFunctionsArrayList.clear(); // Making sure that earlier results are destroyed
			// Populating with new data
			exportFunctionsArrayList.addAll(MainViewDataPopulator.getExportedFunctionsForComponent(selectedComponentName));

		} catch (NoSuchElementException e) {
			// The selected component does necessary
			// have any data about exported functions.
			// Therefore we can ignore this exception
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}
