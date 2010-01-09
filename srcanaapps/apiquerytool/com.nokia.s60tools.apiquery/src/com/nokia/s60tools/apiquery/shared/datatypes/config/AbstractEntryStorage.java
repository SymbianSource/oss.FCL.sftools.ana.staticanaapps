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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nokia.s60tools.apiquery.shared.resources.Messages;

/**
 * Abstract entry storage base class that contains only 
 * the methods that are common for all entry types.
 * 
 * To be subclassed in order to create concrete entry storage.
 */
public abstract class AbstractEntryStorage {

	/**
	 * Entry storage Map.
	 */
	protected Map<String, AbstractEntry> entriesMap = new LinkedHashMap<String, AbstractEntry>();
	/**
	 * Configuration change listener list.
	 */
	protected ArrayList<IConfigurationChangedListener> configChangeListeners = new ArrayList<IConfigurationChangedListener>();

	/**
	 * Constructor
	 */
	protected AbstractEntryStorage(){		
	}
	
	/**
	 * Adds configuration change listener.
	 * @param listener Configuration change listener.
	 */
	public void addConfigurationChangeListener(IConfigurationChangedListener listener) {
		configChangeListeners.add(listener);
	}

	/**
	 * Removes configuration change listener.
	 * @param listener Configuration change listener.
	 */
	public void removeConfigurationChangeListener(IConfigurationChangedListener listener) {
		configChangeListeners.remove(listener);
	}

	/**
	 * Notifies listeners about the configuration change.
	 * @param eventType Event type .
	 */
	public void notifyConfigurationChangeListeners(int eventType) {
		for (IConfigurationChangedListener listener : configChangeListeners) {
			listener.configurationChanged(eventType);
		}
	}

	/**
	 * Adds an entry to the storage.
	 * @param entry Entry to be added.
	 * @throws DuplicateEntryException 
	 */
	public void addEntry(AbstractEntry entry) throws DuplicateEntryException {
		if(entriesMap.get(entry.getId()) != null){
			String duplicateEntriesErrMsg = Messages.getString("AbstractEntryStorage.Duplicate_ErrMsg"); //$NON-NLS-1$
			throw new DuplicateEntryException(duplicateEntriesErrMsg);
		}
		entriesMap.put(entry.getId(), entry);
		notifyConfigurationChangeListeners(IConfigurationChangedListener.ENTRY_ADDED);
	}

	/**
	 * Updates an entry to the storage.
	 * @param entryWithNewData Entry object containing new data.
	 * @throws EntryNotFoundException 
	 */
	public void updateEntry(AbstractEntry entryWithNewData) throws EntryNotFoundException {
		AbstractEntry entryWithOldData = (AbstractEntry) entriesMap.get(entryWithNewData.getId());
		if(entryWithOldData == null){
			String nonExistingEntryMsg = Messages.getString("AbstractEntryStorage.NonExistingEntry_ErrMsg") + entryWithNewData.getId(); //$NON-NLS-1$
			throw new EntryNotFoundException(nonExistingEntryMsg);
		}
		// Updating data fields (which triggers notification to configuration change listeners)
		entryWithOldData.updateDataFields(entryWithNewData);
	}	
		
	/**
	 * Removes the given entry from the storage.
	 * @param entry Entry.
	 */
	public void removeEntry(AbstractEntry entry) {
		entriesMap.remove(entry.getId());
		notifyConfigurationChangeListeners(IConfigurationChangedListener.ENTRY_REMOVED);		
	}
	
	/**
	 * Remove all entrys.
	 */
	public void removeAllEntrys() {
		entriesMap.clear();
		notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRYS_REMOVED);		
	}
		

	/**
	 * Returns the iterator for the currently registered entries.
	 * @return Returns the currently registered entries.
	 */
	public Collection<AbstractEntry> getEntries() {
		return entriesMap.values();
	}

	/**
	 * Gets the amount of currently registered entries.
	 * @return Returns the amount of currently registered entries.
	 */
	public int getEntryCount() {
		return getEntries().size();
	}

	/**
	 * Gets the entry that matches with the given id.
	 * @param entryId Id for the entry.
	 * @return The entry matching the id or <code>null</code> if not found.
	 */
	public AbstractEntry getByEntryId(String entryId) {
		for (AbstractEntry entry : getEntries()) {			
			if(entry.getId().equals(entryId)){
				return entry;
			}
		}
		return null;
	}

	
	/**
	 * Returns those entries that are selected by used from entry storage.
	 * @return Colletion of selected entries.
	 */
	public Collection<AbstractEntry> getSelectedEntriesCollection() {
		ArrayList<AbstractEntry> selectedList = new ArrayList<AbstractEntry>();
		for (Iterator<AbstractEntry> iter = getEntries().iterator(); iter.hasNext();) {
			AbstractEntry entry = (AbstractEntry) iter.next();
			if(entry.isSelected()){
				selectedList.add(entry);
			}
		}
		return selectedList;
	}	
	
	/**
	 * Returns those entries that are selected by used from entry storage.
	 * @return Array of selected entries.
	 */
	public AbstractEntry[] getSelectedEntries() {
		ArrayList<AbstractEntry> selectedList = new ArrayList<AbstractEntry>();
		for (Iterator<AbstractEntry> iter = getEntries().iterator(); iter.hasNext();) {
			AbstractEntry entry = (AbstractEntry) iter.next();
			if(entry.isSelected()){
				selectedList.add(entry);
			}
		}
		return selectedList.toArray(new AbstractEntry[0]);
	}

	/**
	 * Checks if the given entry is part of the storage.
	 * @param entry Entry object.
	 * @return <code>true</code> if part of the storage, otherwise <code>false</code>.
	 */
	public boolean contains(AbstractEntry entry) {
		return entriesMap.containsKey(entry.getId());
	}

	/**
	 * Informs storage that an entry has been modified. Storage
	 * informs further the configuration listeners, if this
	 * entry was part active configuration.
	 * @param entry Entry object.
	 * @param eventType Event type for modification.
	 */
	public void entryModified(AbstractEntry entry, int eventType) {
		if(contains(entry)){
			// Active configuration has been changed.
			notifyConfigurationChangeListeners(eventType);
		}
	}

	/**
	 * Selects all stored entries and notifies listeners.
	 */
	public void selectAll() {
		for (AbstractEntry entry : getEntries()) {
			((AbstractEntry)entry).setSelected(true);
		}
		notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRIES_CHECKED);
	}

	/**
	 * Selects all stored entries and notifies listeners.
	 */
	public void deselectAll() {
		for (AbstractEntry entry : getEntries()) {
			((AbstractEntry)entry).setSelected(false);
		}		
		notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRIES_UNCHECKED);
	}

	/**
	 * Selects all stored entries and notifies listeners.
	 * @param notifyListenersOnlyWhenAllDeselected if <code>true</code> listeners will be
	 * notified only when all entrys is deselected, not one by one when deselecting entrys.
	 */
	public void deselectAll(boolean notifyListenersOnlyWhenAllDeselected) {
		
		if(notifyListenersOnlyWhenAllDeselected){		
			for (AbstractEntry entry : getEntries()) {
				((AbstractEntry)entry).setSelected(false, true);
			}		
			notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_ENTRIES_UNCHECKED);
		}else{
			deselectAll();
		}
		
	}	

	/**
	 * Saves the configuration into an external configuration file.
	 * <b>Note</b>: Overwrites the contents of the external file.
	 * @param destinationFileAbsolutePathName Absolute pathname for the external configuration file to be used.
	 * @throws IOException 
	 */
	public abstract void save(String destinationFileAbsolutePathName) throws IOException;
		
	/**
	 * Loads the configuration into an external configuration file.
	 * @param storageFilePathName Absolute path name for the configuration file.
	 * @throws IOException 
	 */
	public abstract void load(String storageFilePathName) throws IOException;
}
