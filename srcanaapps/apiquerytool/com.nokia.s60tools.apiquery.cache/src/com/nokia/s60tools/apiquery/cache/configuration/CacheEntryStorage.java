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

package com.nokia.s60tools.apiquery.cache.configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;

import com.nokia.s60tools.apiquery.cache.plugin.CachePlugin;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry;
import com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage;
import com.nokia.s60tools.apiquery.shared.datatypes.config.DuplicateEntryException;
import com.nokia.s60tools.apiquery.shared.datatypes.config.EntryNotFoundException;
import com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener;
import com.nokia.s60tools.apiquery.shared.exceptions.XMLNotValidException;
import com.nokia.s60tools.apiquery.shared.job.JobCancelledByUserException;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLElementData;
import com.nokia.s60tools.apiquery.shared.util.xml.XMLUtils;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.resource.FileUtils;

/**
 * Singleton class that is created on plugin startup, and is kept active as long
 * as plugin is active.
 * 
 * The purpose of this class is to store If Sheet entries configured by user.
 * 
 * The format of used XML:
 * 
 * <?xml version="1.0" encoding="UTF-8" ?> <entries> <metadata id="<drive>:\<path>\my_api.metaxml"
 * selected="true" sdkid="<DEVICE ID>" size="665" last_modified="1228305614126" />
 * <metadata id="<drive2>:\<path>\my_api2.metaxml" selected="false" sdkid="<DEVICE2
 * ID>" size="802" last_modified="1228305614126" /> </entries>
 * 
 */
public class CacheEntryStorage extends AbstractEntryStorage {

	/**
	 * Singleton instance.
	 */
	static private CacheEntryStorage instance = null;

	/**
	 * Public Singleton instance accessor.
	 * 
	 * @return Returns instance of this singleton class-
	 */
	public static CacheEntryStorage getInstance() {
		if (instance == null) {
			instance = new CacheEntryStorage();
		}
		return instance;
	}

	//
	// Variables for creating XML file from server entry data.
	//
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";//$NON-NLS-1$

	private static final String XML_ROOT_START_ELEMENT = "<entries>";//$NON-NLS-1$

	private static final String XML_ROOT_END_ELEMENT = "</entries>";//$NON-NLS-1$

	private static final String XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START = "=\"";//$NON-NLS-1$

	private static final String CARRIAGE_RETURN_AND_NEWLINE = "\r\n";//$NON-NLS-1$

	private static final String SINGLE_SPACE = " ";//$NON-NLS-1$

	private static final String QUOTE_AND_SINGLE_SPACE = "\" ";//$NON-NLS-1$

	private static final String ELEMENT_START_STR = "<";//$NON-NLS-1$

	private static final String ELEMENT_END_STR = "/>"; //$NON-NLS-1$

	//
	// XML element and attribute names used for storing configuration
	//	
	private static final String METADATA_ELEMENT = "metadata";//$NON-NLS-1$

	private static final String ID_ATTRIBUTE = "id";//$NON-NLS-1$

	private static final String SDK_ID_ATTRIBUTE = "sdkid";//$NON-NLS-1$

	private static final String SIZE_ATTRIBUTE = "size";//$NON-NLS-1$

	private static final String SELECTION_ATTRIBUTE = "selected";//$NON-NLS-1$	

	private static final String DATE_ATTRIBUTE = "last_modified";//$NON-NLS-1$

	private static final String API_NAME = "api_name";//$NON-NLS-1$

	private Vector<XMLNotValidException> loadErrors = null;

	private boolean isLoaded = false;

	/**
	 * Private default constructor.
	 */
	private CacheEntryStorage() {
		super();
		DbgUtility.println(DbgUtility.PRIORITY_CLASS,
				"-- <<create>> --> " + getClass().getName()); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage#save(java.lang.String)
	 */
	public void save(String destinationFileAbsolutePathName) throws IOException {
		// System.out.println("save" + destinationFileAbsolutePathName);

		StringBuffer xmlDataBuf = new StringBuffer();

		File f = new File(destinationFileAbsolutePathName);
		// Deleting possibly old
		if (f.exists()) {
			if (!f.canWrite()) {
				String cannotWriteToFileErrMsg = Messages
						.getString("CacheEntry.Destination_File_Is_Write_Protected_ErrMsg") //$NON-NLS-1$
						+ destinationFileAbsolutePathName;
				APIQueryConsole.getInstance().println(cannotWriteToFileErrMsg,
						APIQueryConsole.MSG_ERROR);
				throw new RuntimeException(cannotWriteToFileErrMsg);
			}
			if (!f.delete()) {
				String cannotWriteToFileErrMsg = Messages
						.getString("CacheEntry.Destination_File_Is_In_Use_ErrMsg") //$NON-NLS-1$
						+ destinationFileAbsolutePathName;
				APIQueryConsole.getInstance().println(cannotWriteToFileErrMsg,
						APIQueryConsole.MSG_ERROR);
				throw new RuntimeException(cannotWriteToFileErrMsg);
			}
		}

		FileOutputStream fos = new FileOutputStream(f);

		BufferedOutputStream bos = new BufferedOutputStream(fos);

		xmlDataBuf.append(XML_HEADER);
		xmlDataBuf.append(CARRIAGE_RETURN_AND_NEWLINE);
		xmlDataBuf.append(XML_ROOT_START_ELEMENT);
		xmlDataBuf.append(CARRIAGE_RETURN_AND_NEWLINE);
		Collection<AbstractEntry> entriesColl = getEntries();
		// Create <ifheet>...</ifheet> XML -elements from entries
		for (AbstractEntry entryBeforeCast : entriesColl) {
			CacheEntry entry = (CacheEntry) entryBeforeCast;
			xmlDataBuf.append(ELEMENT_START_STR + METADATA_ELEMENT
					+ SINGLE_SPACE);
			xmlDataBuf.append(ID_ATTRIBUTE
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START + entry.getId()
					+ QUOTE_AND_SINGLE_SPACE);
			xmlDataBuf.append(SELECTION_ATTRIBUTE
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START
					+ Boolean.toString(entry.isSelected())
					+ QUOTE_AND_SINGLE_SPACE);
			xmlDataBuf.append(SDK_ID_ATTRIBUTE
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START
					+ entry.getSDKID() + QUOTE_AND_SINGLE_SPACE);
			xmlDataBuf.append(SIZE_ATTRIBUTE
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START
					+ entry.getSize() + QUOTE_AND_SINGLE_SPACE);
			xmlDataBuf.append(DATE_ATTRIBUTE
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START
					+ entry.getDate() + QUOTE_AND_SINGLE_SPACE);
			xmlDataBuf.append(API_NAME
					+ XML_ATTRIBUTE_ASSIGNMENT_WITH_QUOTE_START
					+ entry.getAPIName() + QUOTE_AND_SINGLE_SPACE);
			//System.out.println("apiname" +entry.getAPIName());
			xmlDataBuf.append(ELEMENT_END_STR);
			xmlDataBuf.append(CARRIAGE_RETURN_AND_NEWLINE);
		}
		xmlDataBuf.append(XML_ROOT_END_ELEMENT);
		xmlDataBuf.append(CARRIAGE_RETURN_AND_NEWLINE);

		// Writing data to file
		byte[] writeData = xmlDataBuf.toString().getBytes();
		bos.write(writeData, 0, writeData.length);
		bos.flush();
		bos.close();
		fos.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage#load(java.lang.String)
	 */
	public void load(String storageFilePathName) throws IOException {
		//System.out.println("storage file path" + storageFilePathName);

		// Setting elements to be parsed
		Set<String> elemNameSet = new LinkedHashSet<String>();
		elemNameSet.add(METADATA_ELEMENT); // server element
		// Setting attributes to be parsed for server element
		Map<String, String> attrNameSet = new LinkedHashMap<String, String>();
		attrNameSet.put(ID_ATTRIBUTE, ID_ATTRIBUTE);
		attrNameSet.put(SELECTION_ATTRIBUTE, SELECTION_ATTRIBUTE);
		attrNameSet.put(SDK_ID_ATTRIBUTE, SDK_ID_ATTRIBUTE);
		attrNameSet.put(SIZE_ATTRIBUTE, SIZE_ATTRIBUTE);
		attrNameSet.put(DATE_ATTRIBUTE, DATE_ATTRIBUTE);
		attrNameSet.put(API_NAME, API_NAME);
		Map<String, Map<String, String>> attributeMap = new LinkedHashMap<String, Map<String, String>>();
		attributeMap.put(METADATA_ELEMENT, attrNameSet);

		try {
			// Loading XML data into memory
			StringBuffer xmlData = FileUtils
					.loadDataFromFile(storageFilePathName);
			// Parsing elements from the XML data and adding found server
			// entries to storage
			XMLElementData[] elementArr = XMLUtils.parseXML(xmlData.toString(),
					elemNameSet, attributeMap);
			ArrayList<CacheEntry> foundEntries = convertElementDataToEntryList(elementArr);
			// Removing the old server entries and adding new ones
			// When we are really sure that the whole load opearation was
			// successful.
			entriesMap.clear();
			for (AbstractEntry entry : foundEntries) {
				// CacheEntry ent = (CacheEntry) entry;

				entriesMap.put(entry.getId(), entry);
			}

		} catch (Exception e) {
			e.printStackTrace();
			String msg = Messages
					.getString("CacheEntry.LoadFailed_Part_1_ErrMsg") + e.getMessage() //$NON-NLS-1$
					+ ". " + Messages.getString("CacheEntry.LoadFailed_Part_2_ErrMsg"); //$NON-NLS-1$ //$NON-NLS-2$
			APIQueryConsole.getInstance().println(msg,
					APIQueryConsole.MSG_ERROR);
			File f = new File(storageFilePathName);
			if (f.exists()) {
				f.delete();
			}
		}
	}

	/**
	 * Converts element data into server entry list.
	 * 
	 * @param elementArr
	 *            XML Element data to be converted.
	 * @return Server entry list.
	 */
	private ArrayList<CacheEntry> convertElementDataToEntryList(
			XMLElementData[] elementArr) {
		ArrayList<CacheEntry> foundEntries = new ArrayList<CacheEntry>();

		// Temporary data used during attribute fetching
		String id = null;
		String sdkid = null;
		boolean isSelected = false;
		String apiName = null;
		long size = -1;
		long date = -1;

		for (int i = 0; i < elementArr.length; i++) {
			XMLElementData data = elementArr[i];
			Map<String, String> params = data.getAttributes();

			try {

				id = params.get(ID_ATTRIBUTE);
				isSelected = Boolean.parseBoolean(params
						.get(SELECTION_ATTRIBUTE));
				sdkid = params.get(SDK_ID_ATTRIBUTE);
				size = Long.parseLong(params.get(SIZE_ATTRIBUTE));
				date = Long.parseLong(params.get(DATE_ATTRIBUTE));
				apiName = params.get(API_NAME);
				//System.out.println("api name" + apiName);
			} catch (Exception e) {
				throw new RuntimeException(
						Messages
								.getString("CacheEntry.Unexpected_Attribute_ErrMsg") + data.getElementName()); //$NON-NLS-1$
			}

			File file = new File(id);
			if (file.exists()) {
				String fileName = file.getName();
				// Adding an entry
				foundEntries.add(new CacheEntry(id, fileName, sdkid,
						isSelected, size, date, apiName));
			}
		}
		return foundEntries;
	}

	/**
	 * Get currentry selected SDK ID
	 * 
	 * @return SDK ID or <code>null</code> if not found.
	 */
	public String getCurrentlySelectedSDKID() {
		for (AbstractEntry entry : getEntries()) {
			CacheEntry cacheEntry = (CacheEntry) entry;
			if (cacheEntry.getSDKID() != null && cacheEntry.isSelected()) {
				return cacheEntry.getSDKID();
			}
		}
		return null;
	}

	/**
	 * Updates an entry to the storage. Additional not recommended possibility
	 * to update entry without notifying listeners. It's not recommended to use
	 * this, but if used, make sure that listeners is notified afterwards by
	 * using
	 * {@link AbstractEntryStorage#notifyConfigurationChangeListeners(int)}.
	 * 
	 * @param entryWithNewData
	 *            Entry object containing new data.
	 * @throws EntryNotFoundException
	 * @param dontNotifyListeners
	 *            if <code>true</code> listeners will not be notified.
	 */
	public void updateEntry(AbstractEntry entryWithNewData,
			boolean dontNotifyListeners) throws EntryNotFoundException {

		if (dontNotifyListeners) {
			AbstractEntry entryWithOldData = (AbstractEntry) entriesMap
					.get(entryWithNewData.getId());
			if (entryWithOldData == null) {
				String nonExistingEntryMsg = Messages
						.getString("AbstractEntryStorage.NonExistingEntry_ErrMsg") + entryWithNewData.getId(); //$NON-NLS-1$
				throw new EntryNotFoundException(nonExistingEntryMsg);
			}
			// Updating data fields (which triggers notification to
			// configuration change listeners)
			entryWithOldData
					.updateEntryTypeSpecificDataFields(entryWithNewData);
		} else {
			updateEntry(entryWithNewData);
		}
		this.isLoaded = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage#updateEntry(com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry)
	 */
	public void updateEntry(AbstractEntry entryWithNewData)
			throws EntryNotFoundException {
		super.updateEntry(entryWithNewData);
		this.isLoaded = false;

	}

	/**
	 * Adds an entry to the storage. Additional not recommended possibility to
	 * add entry without notifying listeners. It's not recommended to use this,
	 * but if used, make sure that listeners is notified afterwards by using
	 * {@link AbstractEntryStorage#notifyConfigurationChangeListeners(int)}.
	 * 
	 * @param entry
	 *            Entry to be added.
	 * @param dontNotifyListeners
	 *            if <code>true</code> listeners will not be notified.
	 * @throws DuplicateEntryException
	 */
	public void addEntry(AbstractEntry entry, boolean dontNotifyListeners)
			throws DuplicateEntryException {

		if (!dontNotifyListeners) {
			super.addEntry(entry);
		} else {
			if (entriesMap.get(entry.getId()) != null) {
				String duplicateEntriesErrMsg = Messages
						.getString("AbstractEntryStorage.Duplicate_ErrMsg"); //$NON-NLS-1$
				throw new DuplicateEntryException(duplicateEntriesErrMsg);
			}
			entriesMap.put(entry.getId(), entry);
		}
		this.isLoaded = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntryStorage#addEntry(com.nokia.s60tools.apiquery.shared.datatypes.config.AbstractEntry)
	 */
	public void addEntry(AbstractEntry entry) throws DuplicateEntryException {
		super.addEntry(entry);
		this.isLoaded = false;
	}

	/**
	 * Load all selected entrys to storage. Parsing XML:s for all items, by
	 * calling {@link CacheEntry#load()}.
	 * 
	 * Also {@link CacheEntry#unload()} for entry if not selected.
	 * 
	 * If want to know if there was some errors, ask it by
	 * {@link CacheEntryStorage#isLoadErros()}. If there was errors, ask them
	 * by {@link CacheEntryStorage#getLoadErrors()}.
	 * 
	 * @param monitor
	 *            to check for cancellations
	 * @param newSelectedSDKInfo
	 *            SDK info to be selected
	 * @throws JobCancelledByUserException
	 *             if canceled by user
	 * 
	 */

	public void selectSDKAndLoadAllSelectedDatasToMemory(
			IProgressMonitor monitor, SdkInformation newSelectedSDKInfo)
			throws JobCancelledByUserException {
		selectSDKAndLoadAllSelectedDatasToMemory(monitor, newSelectedSDKInfo,
				true);
	}

	/**
	 * Load all selected entrys to storage. Parsing XML:s for all items, by
	 * calling {@link CacheEntry#load()}.
	 * 
	 * Also {@link CacheEntry#unload()} for entry if not selected.
	 * 
	 * If want to know if there was some errors, ask it by
	 * {@link CacheEntryStorage#isLoadErros()}. If there was errors, ask them
	 * by {@link CacheEntryStorage#getLoadErrors()}.
	 * 
	 * @param monitor
	 *            to check for cancellations
	 * @param newSelectedSDKInfo
	 *            SDK info to be selected
	 * @param selectSDK
	 *            if newSelectedSDKInfo is to be selected as new selected SDK
	 * @throws JobCancelledByUserException
	 *             if canceled by user
	 * 
	 */
	private void selectSDKAndLoadAllSelectedDatasToMemory(
			IProgressMonitor monitor, SdkInformation newSelectedSDKInfo,
			boolean selectSDK) throws JobCancelledByUserException {

		// set to data store that all are deselected
		Collection<AbstractEntry> entrys = getEntries();

		// Store current situation for restoring it in cases of cancel
		Set<String> keys = entriesMap.keySet();
		Map<String, AbstractEntry> storedEntriesMap = new LinkedHashMap<String, AbstractEntry>(
				entriesMap.size());
		for (String key : keys) {
			CacheEntry ent = (CacheEntry) entriesMap.get(key);
			APIDetails det = ent.getAPIDetails();// If details is null, it
			// must not be loaded now,
			// because of lot of time
			// taken
			CacheEntry ent_ = new CacheEntry(ent.getId(), ent.getName(), ent
					.getSDKID(), ent.isSelected(), ent.getSize(),
					ent.getDate(), ent.getAPIName());
			ent_.setAPIDetails(det);
			storedEntriesMap.put(new String(key), ent_);
		}

		try {

			// select new SDK
			if (selectSDK) {
				selectNewSDK(monitor, newSelectedSDKInfo, entrys);
			}

			// For XML validity errors
			loadErrors = null;
			Vector<CacheEntry> entrysToBeUnloaded = new Vector<CacheEntry>();
			// Loading all selected entrys to to memory by using entry.load()
			for (AbstractEntry entry : entrys) {
				// If canceled, throwing exception and catch will handle data
				// restore
				if (monitor.isCanceled()) {
					throw new JobCancelledByUserException(
							Messages
									.getString("CacheEntryStorage.JobCanceledByUserMsg"));
				}
				CacheEntry ce = (CacheEntry) entry;
				if (ce.isSelected()) {
					try {
						ce.load();
					} catch (XMLNotValidException e) {
						addLoadError(e);
					}
				}
				// If entry is not selected, unloading it from memory.
				else {
					entrysToBeUnloaded.add(ce);
				}
			}
			// If canceled during operation, we unload entrys just when all is
			// loaded, unload does not really take any time.
			// So for here cancel wont occur anymore
			for (CacheEntry ce : entrysToBeUnloaded) {
				ce.unload();
			}

			this.isLoaded = true;

		} catch (JobCancelledByUserException e) {
			entriesMap = storedEntriesMap;
			throw e;
		}
	}

	/**
	 * Sets selected sdk as true and un selects not selected SDK
	 * 
	 * @param monitor
	 * @param newSelectedSDKInfo
	 * @param entrys
	 * @throws JobCancelledByUserException
	 */
	private void selectNewSDK(IProgressMonitor monitor,
			SdkInformation newSelectedSDKInfo, Collection<AbstractEntry> entrys)
			throws JobCancelledByUserException {
		for (AbstractEntry entry : entrys) {
			if (monitor.isCanceled()) {
				// If canceled, throwing exception and catch will handle data
				// restore
				throw new JobCancelledByUserException(Messages
						.getString("CacheEntryStorage.JobCanceledByUserMsg"));
			}
			CacheEntry ce = (CacheEntry) entry;
			if (ce.getSDKID().equalsIgnoreCase(newSelectedSDKInfo.getSdkId())) {
				entry.setSelected(true, true);
			} else {
				ce.setSelected(false, true);
			}
		}
	}

	/**
	 * Load all selected entrys to storage. Parsing XML:s for all items, by
	 * calling {@link CacheEntry#load()}.
	 * 
	 * Also {@link CacheEntry#unload()} for entry if not selected.
	 * 
	 * If want to know if there was some errors, ask it by
	 * {@link CacheEntryStorage#isLoadErros()}. If there was errors, ask them
	 * by {@link CacheEntryStorage#getLoadErrors()}.
	 * 
	 * @param monitor
	 *            to check for cancellations
	 * @throws JobCancelledByUserException
	 *             if canceled by user
	 */
	public void loadAllSelectedDatasToMemory(IProgressMonitor monitor)
			throws JobCancelledByUserException {

		selectSDKAndLoadAllSelectedDatasToMemory(monitor, null, false);

	}

	/**
	 * Check if there was load errors.
	 * 
	 * @return <code>true</code> if there was load errors <code>false</code>
	 *         otherwise.
	 */
	public boolean isLoadErros() {
		return loadErrors != null;
	}

	/**
	 * Get load errors
	 * 
	 * @return load errors or <code>null</code> if there was no errors on
	 *         CacheEntryStorage#loadAllSelectedDatasToMemory().
	 */
	public Vector<XMLNotValidException> getLoadErrors() {
		return loadErrors;
	}

	/**
	 * Adds load error to errors
	 * 
	 * @param e
	 */
	private void addLoadError(XMLNotValidException e) {

		if (loadErrors == null) {
			loadErrors = new Vector<XMLNotValidException>();
		}
		loadErrors.add(e);

	}

	/**
	 * Removes all selected entrys.
	 */
	public void removeSelectedEntrys(String currentlySelectedSDKID) {

		Collection<AbstractEntry> entrys = getEntries();
		Vector<String> ids = new Vector<String>();
		for (AbstractEntry aEntry : entrys) {
			CacheEntry entry = (CacheEntry) aEntry;
			if (entry.isSelected()
					|| entry.getSDKID().equals(currentlySelectedSDKID)) {
				ids.add(entry.getId());
			}

		}
		for (String id : ids) {
			entriesMap.remove(id);
		}

		notifyConfigurationChangeListeners(IConfigurationChangedListener.ALL_SELECTED_ENTRYS_REMOVED);
	}

	/**
	 * Unloads all entrys
	 */
	public void unload() {
		Collection<AbstractEntry> entrys = getEntries();
		// For XML validity errors
		loadErrors = null;// Now there are no loads -> no load Errors eather
		// Unload all items
		for (AbstractEntry entry : entrys) {
			CacheEntry ce = (CacheEntry) entry;
			ce.unload();
		}
		this.isLoaded = false;
	}

	/**
	 * Check if source is loaded.
	 * 
	 * @return <code>true</code> if is loaded, false otherwise
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * Return the path to xml file.
	 * @param sdkID : SDK name as given in metadata_cache_entries.xml file
	 * @param APIName : API Name
	 * @param headerFile : header file name
	 * @return
	 */
	
	public String getID(String sdkID, String APIName, String headerFile) {
		// System.out.println("SDK ID" +sdkID + "apiame " + APIName + "
		// headerfile : " + headerFile);
		try {
			//CacheEntryStorage instance = getInstance();
			//instance.load(CachePlugin.getconfigFilePath());

			Set<String> elemNameSet = new LinkedHashSet<String>();
			elemNameSet.add(METADATA_ELEMENT); // server element
			// Setting attributes to be parsed for server element
			Map<String, String> attrNameSet = new LinkedHashMap<String, String>();
			attrNameSet.put(ID_ATTRIBUTE, ID_ATTRIBUTE);
			attrNameSet.put(SELECTION_ATTRIBUTE, SELECTION_ATTRIBUTE);
			attrNameSet.put(SDK_ID_ATTRIBUTE, SDK_ID_ATTRIBUTE);
			attrNameSet.put(SIZE_ATTRIBUTE, SIZE_ATTRIBUTE);
			attrNameSet.put(DATE_ATTRIBUTE, DATE_ATTRIBUTE);
			attrNameSet.put(API_NAME, API_NAME);
			Map<String, Map<String, String>> attributeMap = new LinkedHashMap<String, Map<String, String>>();
			attributeMap.put(METADATA_ELEMENT, attrNameSet);

			// Loading XML data into memory
			StringBuffer xmlData = FileUtils.loadDataFromFile(CachePlugin
					.getconfigFilePath());
			// Parsing elements from the XML data and adding found server
			// entries to storage
			XMLElementData[] elementArr = XMLUtils.parseXML(xmlData.toString(),
					elemNameSet, attributeMap);
			for (int i = 0; i < elementArr.length; i++) {
				XMLElementData data = elementArr[i];
				Map<String, String> params = data.getAttributes();

				if (params.get(API_NAME).equalsIgnoreCase(APIName)
						&& params.get(SDK_ID_ATTRIBUTE).equalsIgnoreCase(sdkID)) {
					//System.out.println("found");
					// serach in the list of avaliable folders
					String id = params.get(ID_ATTRIBUTE);
					// c:\.....\xyz.xml

					String folder = id.substring(0, id.lastIndexOf("\\"))
							+ "\\inc";
					//System.out.println( "Folder" + folder);

					File directory = new File(folder);

					if (directory.isDirectory()) { // check to make sure it
						// is a directory
						String filenames[] = directory.list(); // make array of
						// filenames.

						for (int j = 0; j < filenames.length; j++) {
							if (filenames[j].equalsIgnoreCase(headerFile))
								return id;
						}

					} // is directory

				}
			} //forloop
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		 

		return null;
	}

}
