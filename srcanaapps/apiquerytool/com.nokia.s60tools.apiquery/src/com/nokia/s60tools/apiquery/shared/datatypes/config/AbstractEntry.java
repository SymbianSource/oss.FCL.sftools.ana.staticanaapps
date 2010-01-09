/*
* Copyright (c) 2007 Nokia Corporation and/or its subsidiary(-ies). 
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
 
package com.nokia.s60tools.apiquery.shared.datatypes.config;

import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;


/**
 * Abstract entry base class that contains only id for the
 * entry and method that are common for all entry types.
 * 
 * To be subclassed in order to create concrete entry types.
 */
public abstract class AbstractEntry {

	/**
	 * User configurable id for the entry. The entries
	 * are identified by there unique id per search method 
	 * configuration storage.
	 */
	protected final String id;
	/**
	 * Set to <code>true</code> if user has selected this entry to
	 * be part of the search, otherwise <code>false</code>,
	 */
	protected boolean isSelected;

	/**
	 * Constructor
	 * @param id Entry id.that is unique per search method. 
	 * @param isSelected Is the entry used for queries by default.
	 */
	protected AbstractEntry(String id, boolean isSelected){
		this.id = id;
		this.isSelected = isSelected;		
	}
	
	/**
	 * @return the id for the entry.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Sets selection status for the entry 
	 * and notifies storage about the modification.
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if(isSelected){
			notifyModification(IConfigurationChangedListener.ENTRY_CHECKED);			
		}
		else{
			notifyModification(IConfigurationChangedListener.ENTRY_UNCHECKED);			
		}
	}
	
	/**
	 * Sets selection status for the entry 
	 * and notifies storage about the modification.
	 * @param isSelected the isSelected to set
	 * @param dontNotifyListeners if <code>true</code> listeners will not be notified.
	 */	
	public void setSelected(boolean isSelected, boolean dontNotifyListeners) {
		if(dontNotifyListeners){
			this.isSelected = isSelected;
		}
		else {
			setSelected(isSelected);
		}
	}	
	
	/**
	 * Notifies storage object that this entry has been modified.
	 * @param eventType Event type of the modification.
	 */
	protected void notifyModification(int eventType){
		ISearchMethodExtensionInfo currSelExtInfo =  UserSettings.getInstance().getCurrentlySelectedSearchMethodInfo();
		String id = currSelExtInfo.getId();
		ISearchMethodExtension currSelExt = SearchMethodExtensionRegistry.getInstance().getById(id);
		AbstractEntryStorage storage = currSelExt.getEntryStorageInstance();
		storage.entryModified(this, eventType);
	}
	
	/**
	 * Calls template method to updates concrete type entry-specific data fields
	 * and notifies listeners about the modification.
	 * @param entryWithUpdatedData Entry with new data.
	 */
	public void updateDataFields(AbstractEntry entryWithUpdatedData){
		updateEntryTypeSpecificDataFields(entryWithUpdatedData);
		notifyModification(IConfigurationChangedListener.ENTRY_MODIFIED);
	}	
	
	/**
	 * The following template method updates concrete type entry-specific data fields.
	 * @param entryWithUpdatedData Entry with new data.
	 */
	public abstract void updateEntryTypeSpecificDataFields(AbstractEntry entryWithUpdatedData);


}
