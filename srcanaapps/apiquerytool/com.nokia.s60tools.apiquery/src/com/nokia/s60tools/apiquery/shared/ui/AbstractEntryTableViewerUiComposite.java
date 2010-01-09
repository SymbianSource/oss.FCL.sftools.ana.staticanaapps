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
 
package com.nokia.s60tools.apiquery.shared.ui;

import java.util.Collection;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;

import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage;
import com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener;
import com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Common abstract base class for entry table viewer UI composites.
 */
public abstract class AbstractEntryTableViewerUiComposite extends AbstractUiFractionComposite implements ICheckStateListener, 
																								IConfigurationChangedListener{
	/**
	 * Entry check state listener reference.
	 * @see com.nokia.s60tools.apiquery.web.searchmethod.ui.IEntryCheckStateListener
	 */
	protected IEntryCheckStateListener entryCheckStateListener = null;
	protected CheckboxTableViewer entriesViewer;

	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 * @param style				The used style for the composite.
	 */
	public AbstractEntryTableViewerUiComposite(Composite parentComposite, int style) {
		super(parentComposite, style);
	}
	
	/**
	 * Creates concrete entry viewer specific storage entry table viewer instance. 
	 * @param parent Parent composite for the created composite.
	 * @return New <code>CheckboxTableViewer</code> object instance.
	 */
	protected abstract CheckboxTableViewer createEntryTableViewer(Composite parent);
	
	/**
	 * Gets concrete entry viewer specific storage instance.
	 * @return 	Concrete entry viewer specific storage instance.
	 */
	protected abstract AbstractEntryStorage getEntryStorageInstance();
	
	/**
	 * Gets concrete entry viewer specific viewer sorter.
	 * @return 	Concrete entry viewer specific viewer sorter.
	 */
	protected abstract ViewerSorter createViewerSorter();

	/**
	 * Gets concrete entry viewer specific label provider.
	 * @return 	Concrete entry viewer specific label provider.
	 */
	protected abstract IBaseLabelProvider createLabelProvider();

	/**
	 * Gets concrete entry viewer specific content provider.
	 * @return 	Concrete entry viewer specific content provider.
	 */
	protected abstract IContentProvider createContentProvider();


	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.searchmethod.ui.AbstractUiFractionComposite#createControls()
	 */
	protected void createControls() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "-- createControls() --> " + getClass().getName()); //$NON-NLS-1$
		
		// Concrete viewer instantiation is done by sub classes
		entriesViewer = createEntryTableViewer(this);
		
		// As well as creation of concrete providers and concrete entry storage
		IContentProvider entryContentProvider = createContentProvider();
		entriesViewer.setContentProvider(entryContentProvider);
		IBaseLabelProvider labelProvider = createLabelProvider();
		entriesViewer.setLabelProvider(labelProvider);
		entriesViewer.setInput(entryContentProvider);
		ViewerSorter entrySorter = createViewerSorter();
		entriesViewer.setSorter(entrySorter);
		// Storing entry storage instance reference.
		AbstractEntryStorage entryConfigStorage = getEntryStorageInstance();
		// Adding checked statuses before adding check state listener...	
		entriesViewer.setCheckedElements(entryConfigStorage.getSelectedEntries());
		// ...and start listening for status changes.
		entriesViewer.addCheckStateListener(this);
		// Listening for configuration change events for enabling UI refresh
		entryConfigStorage.addConfigurationChangeListener(this);
	}

	/**
	 * Refreshes the entry UI.
	 */
	public void refresh() {
		try {
			entriesViewer.refresh();	
			// Check state might have been changed due to refresh
			notifyEntryCheckStateListener();
		} catch (SWTException e) {
			// We might get time-to-time non-fatal SWTException: Widget is disposed, 
			// which can be ignored.
		} catch (Exception e) {
			// Stack trace is shown for other exceptions 
			e.printStackTrace();
		}
	}

	/**
	 * Gets entry table viewer instance.
	 * @return Entry table viewer instance.
	 */
	public TableViewer getEntryViewer() {
		return entriesViewer;
	}

	/**
	 * Adds check mark to all entries.
	 */
	private void checkAll() {		
		// Removing possible earlier check status listener
		entriesViewer.removeCheckStateListener(this);	
		entriesViewer.setAllChecked(true);			
		// and start listening for status changes again.
		entriesViewer.addCheckStateListener(this);
		if(entryCheckStateListener != null){
			entryCheckStateListener.allEntriesChecked();
		}
	}

	/**
	 * Removes check mark from all entries.
	 */
	private void uncheckAll() {		
		// Removing possible earlier check status listener
		entriesViewer.removeCheckStateListener(this);
		// Clearing all previous check marks
		entriesViewer.setAllChecked(false);			
		// and start listening for status changes again.
		entriesViewer.addCheckStateListener(this);
		if(entryCheckStateListener != null){
			entryCheckStateListener.allEntriesUnchecked();
		}
	}

	/**
	 * Adds selection listener to the table viewer.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {		
		entriesViewer.addSelectionChangedListener(listener);
	}

	/**
	 * Removes selection listener fromthe table viewer.
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {		
		entriesViewer.removeSelectionChangedListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
	 */
	public void checkStateChanged(CheckStateChangedEvent event) {
		// Updating checked state information for the entry
		AbstractEntry entry = (AbstractEntry) event.getElement();
		entry.setSelected(event.getChecked());
		notifyEntryCheckStateListener();
	}

	public void configurationChanged(int eventType) {
		// Updating check state information to UI
		if(eventType == IConfigurationChangedListener.ALL_ENTRIES_CHECKED){
			checkAll();			
		}
		else if(eventType == IConfigurationChangedListener.ALL_ENTRIES_UNCHECKED){
			uncheckAll();
		}
		else{
			// In other check cases UI must be refreshed...
			refresh();
			// ...before querying the latest information from storage
			Collection<AbstractEntry> entryColl = getEntryStorageInstance().getEntries();
			for (AbstractEntry entry : entryColl) {
				entriesViewer.setChecked(entry, entry.isSelected());
			}			
		}
		
		// Refreshing UI always when configuration has been changed.
		refresh();		
	}

	/**
	 * Sets entry check state listener.
	 * @param listener Listener to be set.
	 */
	public void setEntryCheckStateListener(IEntryCheckStateListener listener) {
		entryCheckStateListener = listener;
		// Checking and notifying the current check status for possible listeners
		notifyEntryCheckStateListener();
	}

	/**
	 * Checks current check status and notifies to listener
	 * if such is set.		
	 */
	private void notifyEntryCheckStateListener() {
		if(entryCheckStateListener != null){
			int checkElemCount = entriesViewer.getCheckedElements().length;
			int elemCount = entriesViewer.getTable().getItemCount();
			if(checkElemCount == 0){
				entryCheckStateListener.allEntriesUnchecked();				
			}
			else if(checkElemCount == elemCount){
				entryCheckStateListener.allEntriesChecked();								
			}
			else{
				entryCheckStateListener.someEntriesChecked();												
			}
		}
	}

	/**
	 * Adds a listener for double-clicks for the entry viewer.
	 * Call is delegated further to viewer class.
	 * @param listener a double-click listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#addDoubleClickListener           
	 */
	public void addDoubleClickListener(IDoubleClickListener listener) {
		entriesViewer.addDoubleClickListener(listener);
	}

	/**
	 * Removes the given double-click listener from this viewer. 
	 * Call is delegated further to viewer class.
	 * @param listener a double-click listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#removeDoubleClickListener         
	 */
	public void removeDoubleClickListener(IDoubleClickListener listener) {
		entriesViewer.removeDoubleClickListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose(){
		super.dispose();
		AbstractEntryStorage entryConfigStorage = getEntryStorageInstance();
		if(entryConfigStorage != null){
			entryConfigStorage.removeConfigurationChangeListener(this);			
		}
	}
}
