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
 
 
package com.nokia.s60tools.appdep.core.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepCacheIndexManager;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.ComponentListNode;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This cache index class stores timestamp for each component 
 * that is defined in the cache. The storing of the timestamp
 * info enables possibility to check if cache file needs to
 * be updated when compared with timestamp of real components
 * residing in underneath release directory.
 * 
 * Because the idea is to generate cache index per cache file, the class
 * stores indexInstances of already created indexes and return the already
 * created index object when matching cache file is passed into static
 * <code>getInstance</code> method.
 */
public class CacheIndex {

	/**
	 * Conversion factor from seconds to milliseconds.
	 */
	private static final long TO_MILLISEC = 1000;

	/**
	 * Default and initial value used for cache file version strings.
	 * Just making sure that version number is always a legal version string value.
	 */
	private static final String DEFAULT_VERSION_VALUE_STRING = "000"; //$NON-NLS-1$

	/**
	 * Reference to the known indexInstances of this class. 
	 */
	private static Map<String, CacheIndex> indexInstances = Collections.synchronizedMap(new HashMap<String, CacheIndex>());

	/**
	 * This map stores the following information
	 * gained from main cache file:
	 * - key: component name with extension (equals to component name in lowercase letters)
	 * - IndexData class instance: byte offset from the start of the file, and
	 *                             last component modification timestamp.
	 */
	private Map<String, IndexData> componentCacheIndexDataMap = null;
	
	/**
	 * Reference to cache file to be indexed.
	 */
	private File cacheFile = null;	
		
	/**
	 * Cache build status is originally <code>false</code>, and set to <code>false</code>
	 * after the background thread that has built the index is terminated successfully.
	 */
	private boolean isCacheIndexBuilt = false;
	
	/**
	 * Build directory for this cache file instance.
	 */
	private String buildDirectory = null;
	
	/**
	 * This flag is set to <code>false</code> when 
	 * some one uses first time getInstance() method for
	 * for getting instance that is already existing.
	 * This flag is used to prevent unnecessary update
	 * attempts.
	 */
	private boolean justCreated = true;		
	
	/**
	 * Storing version information for the cache
	 */
	private String versionInfo = null;
		
	/**
	 * Flag if the dependencies cache is non-corrupted or not.
	 */
	private boolean isDependenciesFileNonCorrupted = false;
	
	/**
	 * Flag if the dependencies cache is non-corrupted or not.
	 */
	private boolean isSymbolsFileNonCorrupted = false;
	
	/**
	 * Data that is indexed for each component.
	 */
	private class IndexData{
		
		/**
		 * Component modification timestamp.
		 */
		private final long timestamp;

		/**
		 * Constructor.
		 * @param timestamp Component modification timestamp.
		 */
		public IndexData(long timestamp){
			this.timestamp = timestamp;			
		}

		/**
		 * Gets component modification timestamp.
		 * @return component modification timestamp.
		 */
		public long getTimestamp() {
			return timestamp;
		}
	}	
	
	/**
	 * Returns cache index for the given cache file. Creates a new
	 * cache index object, if no index was previously created.
	 * @param cacheFile Valid cache File object to get/create cache index from.
	 * @param buildDirectory Build directory string to be used as seek string to cache file.
	 * @return Returns cache index class instance.
	 * @throws IOException
	 */
	public static CacheIndex getCacheIndexInstance(File cacheFile, String buildDirectory) throws IOException{
		
		CacheIndex instance = null;
		
		synchronized(indexInstances){
			
			// Do we already have index for this cache file
			instance = (CacheIndex) indexInstances.get(cacheFile.getAbsolutePath());
			
			if( instance == null ){
				// No => Creating a new cache index instance.
				instance = new CacheIndex(cacheFile, buildDirectory);
				indexInstances.put(cacheFile.getAbsolutePath(), instance);
			}
			else{
				instance.setJustCreated(false);
			}
			
		} // synchronized
		
		// Returning corresponding instance object
		return (CacheIndex) instance;
	}
	
	/**
	 * Private constructor that creates and instance and starts to build
	 * index at background.
	 * @param cacheFile Valid cache File object to create cache index from.
	 * @param buildDirectory Build directory string to be used as seek string to cache file.
	 * @throws IOException
	 */
	private CacheIndex(File cacheFile, String buildDirectory) throws IOException{
		this.cacheFile = cacheFile;
		this.buildDirectory = buildDirectory;
		componentCacheIndexDataMap = Collections.synchronizedMap(new HashMap<String, IndexData>());
		versionInfo = new String(DEFAULT_VERSION_VALUE_STRING);  //$NON-NLS-1$ 
		startCacheIndexBuildThread();
	}

	/**
	 * Triggers the thread that creates the index at background.
	 * Set the cache index build status flag after the cache index
	 * has been created successfully.
	 * @throws IOException
	 */
	private void startCacheIndexBuildThread() throws IOException{
		
		final CacheIndex indexObject = this;
		
		Thread worker = new Thread(){
			public void run() {
				long runMethodStartTime = System.currentTimeMillis();
				long cacheFileSizeInBytes = cacheFile.length();
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() + " IndexBuildThread started: "  //$NON-NLS-1$
						+ new Date(runMethodStartTime).toString()
						+ " for cache file: " + cacheFile.getAbsolutePath() //$NON-NLS-1$
						+ " of size '" + cacheFileSizeInBytes + "' bytes."); //$NON-NLS-1$ //$NON-NLS-2$
				
				AppDepCacheIndexManager indexMgr = AppDepCacheIndexManager.getInstance();

				// We can promote building of bigger cache indices by increasing priority
				boolean increasePriority = false;
				// and slow down building of very small cache indices by increasing priority
				boolean decreasePriority = false;
				
				// Heuristical limit of for enabling higher priority.
				int priorityIncrLimitInMegabytes = 15; // MB
				// Easier to compare in bytes vs. MB because it would need zero integer division check for small cache files
				long priorityIncrLimitInBytes = (1024*1024) * priorityIncrLimitInMegabytes; // bytes
				if(cacheFileSizeInBytes > priorityIncrLimitInBytes){
					increasePriority = true;
				}
								
				// Heuristical limit of for enabling smaller priority.
				int priorityDescrLimitInKilobytes = 100; // KB
				long priorityDecrLimitInBytes = 1024 * priorityDescrLimitInKilobytes; // bytes
				if(cacheFileSizeInBytes < priorityDecrLimitInBytes){
					decreasePriority = true;
				}
				// Building index
				try {
					indexMgr.registerCacheIndexCreationProcess(indexObject);
					//
					// Adjusting thread priorities if needed - check API docs if interested in what
					// are the possible values for thread priorities:
					//  http://java.sun.com/j2se/1.5.0/docs/api/constant-values.html#java.lang.Thread.MIN_PRIORITY
					//  http://java.sun.com/j2se/1.5.0/docs/api/constant-values.html#java.lang.Thread.NORM_PRIORITY
					//  http://java.sun.com/j2se/1.5.0/docs/api/constant-values.html#java.lang.Thread.MAX_PRIORITY
					//
					if(increasePriority){
						this.setPriority(this.getPriority() + 3);
					}
					if(decreasePriority){
						this.setPriority(this.getPriority() - 3);
					}
					buildCacheIndex();
				} catch (Exception e) {
					e.printStackTrace();
					String errMsg = Messages.getString("CacheIndex.CacheIndex_Generate_Failed_Msg")  //$NON-NLS-1$
						            + ": " + e.getMessage(); //$NON-NLS-1$
					AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
					indexMgr.unregisterCacheIndexCreationProcess(indexObject);
					throw new RuntimeException(errMsg);
				}
				
				isCacheIndexBuilt = true;
				indexMgr.unregisterCacheIndexCreationProcess(indexObject);
				
				// Tracing of consumed time
				long endTime = System.currentTimeMillis();
				File parentDirectory = cacheFile.getParentFile();
				String buildTypeNameString = parentDirectory.getName();
				String targetNameString = parentDirectory.getParentFile().getName();
				String platformNameString = parentDirectory.getParentFile().getParentFile().getName();
				String combinedDbgInfoStr = platformNameString + "/" + targetNameString + "/" + buildTypeNameString; //$NON-NLS-1$ //$NON-NLS-2$
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() 
									+ " IndexBuildThread ended: " + new Date(endTime).toString() //$NON-NLS-1$
									+ " for cache file: " + cacheFile.getAbsolutePath()); //$NON-NLS-1$
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() + "TOTAL: " + (endTime-runMethodStartTime)/1000 + " seconds (" + combinedDbgInfoStr + ")!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() + "COMPONENT INDEX SIZE: " + componentCacheIndexDataMap.size() + " (" + combinedDbgInfoStr + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$				
			}					
		};
		// Kicking-off the thread
		worker.start();		
	}
	
	/**
	 * Builds the cache index and checks the integrity of cache files.
	 * @throws IOException
	 */
	private void buildCacheIndex() throws IOException{		
		buildCacheIndexDataFromMainCacheFile();
		checkSymbolsFileIntegrity();
	}

	/**
	 * Gets cache data from main cache file i.e. getting byte offsets for component property line 
	 * locations and cached modification timestamps for the components.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void buildCacheIndexDataFromMainCacheFile() throws FileNotFoundException, IOException {
		
		// Creating reader for main cache file
		BufferedReader bufRdr = setupBufferedReader(cacheFile);
		
		// Reading the 1st header line from the cache file
		String line = bufRdr.readLine();
		versionInfo = parseVersionInfoFromHeaderLine(line); // Getting version information only from main cache file
			
		// Reading the first actual property information line
		line = bufRdr.readLine();		

		// This is just a debugging aid - starting 
		// to read from line 2 forwards
		int lineno = 2;
		
		boolean isEndMarkFound = false;
		boolean isEndOfFile = false;
		
		// After this reading of all line goes similarly
		// Reading file until EOF (i.e. when null is returned)
		for (; line != null; lineno++) {
			
			isEndOfFile = false;
			
			// Successful comparison requires that compared strings have equal case
			if(isComponentDefinitionLineInMainCacheFile(line)){
				// A component was found, adding it to the index
				parseComponentPropertyLineAndAddToIndex(line);
			}
			else{
				if(line.equals(CacheDataConstants.CACHE_FILE_END_MARK)){
					isEndMarkFound = true;
					isEndOfFile = true;
				}
			}
			
			// Reading next line	
			line = bufRdr.readLine();
		}
		
		if(isEndMarkFound && isEndOfFile){
			isDependenciesFileNonCorrupted = true;
		}
	}

	/**
	 * Reads version information from the header line
	 * @param headerLine Header line to be checked version info fromt.
	 * @return Version info string, or default version value if could not parse.
	 */
	private String parseVersionInfoFromHeaderLine(String headerLine) {
		String[] splitArr = headerLine.split(CacheDataConstants.CACHE_VERSION_INFO_SEPARATOR);
		if(splitArr.length == 2){
			return splitArr[1].trim();
		}
		return DEFAULT_VERSION_VALUE_STRING;
	}

	/**
	 * Checks if the current line is a component definition line.
	 * Line is component property line if the field count is the
	 * one that is expected for this kind of line.
	 * @param line Line to be checked.
	 * @return <code>true</code> if component definition line, otherwise <code>false</code>.
	 */
	static boolean isComponentDefinitionLineInMainCacheFile(String line) {	
		int splitCount = splitPopertyLineIntoStringArray(line).length;
		if(splitCount == CacheCompPropertyField.getCompPropertyFieldCount()){
			return true;
		}
		
		return false;
	}
		
	/**
	 * Parses the property line that constains information about the single component 
	 * and stores the byte offset to the index map with component name.
	 * @param line	Property line to parse component's name from.
	 */
	private void parseComponentPropertyLineAndAddToIndex(String line){
		
		String compPropertiesLine = line;
		
		String [] compPropertiesArr = splitPopertyLineIntoStringArray(compPropertiesLine);
		
		// Using the build directory path from the currently 
		// used file system instead of one in the cache file which may not exist
		// in case cache file was created on some another computer/substed drive.
		String cmpName = compPropertiesArr[CacheCompPropertyField.FILENAME_ARR_INDX];
		
		// Storing the modification time of the cached component
		String compModifiedTimestampString = compPropertiesArr[CacheCompPropertyField.CACHE_TIMESTAMP_ARR_INDX];		
		long compModifiedTimestamp = TO_MILLISEC * Long.parseLong(compModifiedTimestampString);		
		
		synchronized(componentCacheIndexDataMap){
									
			// There should not be an already existing component
			if(componentCacheIndexDataMap.get(cmpName.toLowerCase()) != null){
				// In case of duplicate components, only the first one is added.
				// Duplicate instances are ignored, and an error message printed to concole.
				String errMsg = Messages.getString("CacheIndex.DuplicateComponents_Encountered_ErrMsg"); //$NON-NLS-1$
				AppDepConsole.getInstance().println(errMsg + " (" + cmpName.toLowerCase() + ")", AppDepConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else{
				componentCacheIndexDataMap.put(cmpName.toLowerCase(), new IndexData(compModifiedTimestamp));				
			}
			
		} // synchronized
	}

	/**
	 * Creates file object that points to the symbols information file.
	 * @return File object that refers to the symbols information file.
	 */
	private File createSymbolsFileObject(){
		String cacheDirectoryPath = cacheFile.getParentFile().getAbsolutePath();
		String symbolsFilePathName = cacheDirectoryPath
		                             + File.separatorChar
		                             + ProductInfoRegistry.getCacheSymbolsFileName();
		return new File(symbolsFilePathName); 		
	}
	
	/**
	 * Checks that symbols file has header with version number that matches with one
	 * for main cache file, and also checks up that there is correct end mark.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void checkSymbolsFileIntegrity() throws FileNotFoundException, IOException {
		
		// There is some room for improvement in this method because it is shortened
		// from earlier version that also analyzed component data. But keeping this
		// for now on because do not want to break working stuff. Now the extra		
		// unused code portions are cleaned up anyway.
		File symbolsFile = createSymbolsFileObject(); 
		
		if(! symbolsFile.exists()){
			// Symbols file does not exist, and therefore there are 
			// no export functions related information available.
			return;
		}
		
		// Creating reader for symbols cache file
		BufferedReader localBufRdrVar = setupBufferedReader(symbolsFile);
				
		// Reading the 1st header line from the cache file
		String line = null;
		try {
			line = localBufRdrVar.readLine(); 
			
		} catch (IOException e) {
			// File existed but there was no data, or
			// something else failed.			
			return;
		}		
		
		if(line == null){
			// File existed but there was no data
			// (i.e. end of file was reached)
			return;
		}
			
		// Storing  version number from the header line
		String symbolsFileVersion = parseVersionInfoFromHeaderLine(line);
		
		// Reading the first actual property information line
		line = localBufRdrVar.readLine();		

		// This is just a debugging aid - starting 
		// to read from line 2 forwards
		int lineno = 2;
		
		boolean isEndMarkFound = false;
		boolean isEndOfFile = false;
		
		// After this reading of all line goes similarly
		// Reading file until EOF (i.e. when null is returned)
		for (; line != null; lineno++) {
			
			isEndOfFile = false;
			
			if(line.equals(CacheDataConstants.CACHE_FILE_END_MARK)){
				isEndMarkFound = true;
				isEndOfFile = true;
			}
			
			// Reading next line	
			line = localBufRdrVar.readLine();
		}

		// Symbols file is non corrupted if...
		// - end of file is reached, and expected end mark is found
		// - symbols file is created with same appdep core version than main cache file
		if((isEndMarkFound && isEndOfFile) && (symbolsFileVersion.equals(versionInfo))){
			isSymbolsFileNonCorrupted = true;
		}
	}
				
	/**
	 * Splits the property line into a string array containing property line fields.
	 * @param compPropertiesLine Property line to be splitted.
	 * @return A string array containing property line fields.
	 */
	static String[] splitPopertyLineIntoStringArray(String compPropertiesLine){
		String fieldSepRegExp = Pattern.quote(CacheDataConstants.CACHE_FIELD_SEPARATOR);
		
		String[] splitArr = compPropertiesLine.split(fieldSepRegExp);
		return splitArr;		
	}
	
	/**
	 * Returns modification timestamp stored in cache for the given component.
	 * @param cmpName The name of the component.
	 * @return Last modification timestamp for the component as milliseconds.
	 */
	public long getLastModifiedTimeForComponent(String cmpName){		
		Object obj = componentCacheIndexDataMap.get(cmpName.toLowerCase());
		if( obj == null){
			throw new NoSuchElementException();			
		}
		IndexData idt = (IndexData) obj;
		long timestamp = idt.getTimestamp();
		return timestamp;
	}
	
	/**
	 * Gets the amount of components in cache index.
	 * @return The amount of components in cache index.
	 */
	public int getCachedComponentCount(){		
		return componentCacheIndexDataMap.size();
	}
		
	/**
	 * Returns the cache build status.
	 * @return Returns the isCacheIndexBuilt.
	 */
	private boolean isCacheIndexBuiltImpl() {
		return isCacheIndexBuilt;
	}
	
	/**
	 * Gets the component set of all component names that
	 * exist in cache.
	 */
	public Set<String> getComponentNameSet(){
		return componentCacheIndexDataMap.keySet();
	}
	
	/**
	 * Checks from given function info array if the function 
	 * is virtual. 
	 * @param functionInfoArr Function info array
	 * @return <code>true</code> if the function is virtual, 
	 *         otherwise <code>false</code>.
	 */
	public static boolean isVirtualFunctionFlagSet(String[] functionInfoArr){		
		String fVirtualFlag = functionInfoArr[CacheDataConstants.FUNC_IS_VIRTUAL_INDEX];
		if(fVirtualFlag.trim().equals(CacheDataConstants.VIRTUAL_INDEX_FIELD_IS_TRUE_VALUE)){ //$NON-NLS-1$
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Updates cache index with latest available information.
	 * @throws IOException
	 */
	public void update() throws IOException {		
		// If we are acquiring cache index instance
		// that has been already built previously
		// we need to reset and rebuild it.
		// If cache index was just created the
		// building process is still going on.
		if(! isJustCreated()){
			// Clearing the old data
			componentCacheIndexDataMap.clear();
			// Doing re-build
			startCacheIndexBuildThread();						
		}
	}

	/**
	 * Checks if cache index for this object has been created.
	 * @return <code>true</code> if created, otherwise <code>false</code>.
	 */
	private boolean isJustCreated() {
		return justCreated;
	}

	/**
	 * Sets cache index creation status.
	 * @param justCreated set to <code>true</code> if created, otherwise set to <code>false</code>.
	 */
	private void setJustCreated(boolean justCreated) {
		this.justCreated = justCreated;
	}
	
	/**
	 * Checks if there already exists cache index for given cache file. 
	 * @param cacheFileAbsolutePathName  Cache file path name for cache file/ to be checked.
	 * @return <code>true</code> if exists, otherwise <code>false</code>.
	 */
	public static boolean cacheIndexExistsFor(String cacheFileAbsolutePathName){
		
		boolean cacheIndexExists = false;
		
		synchronized(indexInstances){			
			if( indexInstances.get(cacheFileAbsolutePathName) != null){
				cacheIndexExists = true;
			}
		} // synchronized
		
		return cacheIndexExists;
	}

	/**
	 * Returns creation status of cache index that is under creation.
	 * @param cacheFileAbsolutePathName  Cache file path name for cache file/ to be checked.
	 * @return <code>true</code> if created, otherwise <code>false</code>.
	 */
	public static boolean cacheIndexCreatedFor(String cacheFileAbsolutePathName){
		
		boolean cacheIndexCreated = false;
		
		synchronized(indexInstances){
			CacheIndex indxInstance = (CacheIndex) indexInstances.get(cacheFileAbsolutePathName);
			if(indxInstance != null){
				return indxInstance.isCacheIndexBuiltImpl();
			}
		} // synchronized
		
		return cacheIndexCreated;
	}
	
	/**
	 * Sets up the currently used buffered reader. 
	 * @param file File object to user directly or user RAM-disk image for.
	 * @throws FileNotFoundException
	 */
	private BufferedReader setupBufferedReader(File file) throws FileNotFoundException{		
		FileReader fileRdr = new FileReader(file);
		return new BufferedReader(fileRdr);					
	}
	
	/**
	 * Returns regular expression used to split fields in cache data. 
	 * @return regular expression used to split fields in cache data.
	 */
	public static String getCacheFieldSeparatorRegExp(){
		return Pattern.quote(CacheDataConstants.CACHE_FIELD_SEPARATOR);
	}

	/**
	 * Returns version information string found from dependencies file's header.
	 * @return Three-letter string containing version info.
	 */
	public String getVersionInfo() {
		return versionInfo;
	}

	/**
	 * Method for checking if cache is non-corrupted.
	 * @return <code>true</code> if cache is non-corrupted, otherwise <code>false</code>.
	 */
	public boolean isCacheNonCorrupted(){
		return (isDependenciesFileNonCorrupted && isSymbolsFileNonCorrupted);
	}

	/**
	 * Gets build directory bound to the cache index.
	 * @return the buildDirectory
	 */
	public String getBuildDirectory() {
		return buildDirectory;
	}

	/**
	 * Returns component list for the targets pointed by given settings object.
	 * @param settings[in] Settings to get currently selected targets from.
	 * @param duplicateItemsList[out] Out parameter that contains the list of duplicate
	 *                           components found from the selected targets.
	 * @return Component iterator for the currently selected targets.
	 * @throws CacheFileDoesNotExistException 
	 * @throws IOException 
	 */
	public static List<ComponentListNode> getComponentIteratorForGivenSettings(AppDepSettings settings, List<String> duplicateItemsList) throws CacheFileDoesNotExistException, IOException {

		ArrayList<CacheIndex> cacheIndexList = new ArrayList<CacheIndex>();
		
		// Getting all the cache indices referred by the settings
		ITargetPlatform[] currentlyUsedTargetPlatforms = settings.getCurrentlyUsedTargetPlatforms();
		for (int i = 0; i < currentlyUsedTargetPlatforms.length; i++) {
			ITargetPlatform targetPlatform = currentlyUsedTargetPlatforms[i];
			String targetPlatformId = targetPlatform.getId();
			String cacheFileAbsolutePathName = settings.getCacheFileAbsolutePathName(targetPlatformId);
			File cacheFile = new File(cacheFileAbsolutePathName);
			if(!cacheFile.exists()){
				throw new CacheFileDoesNotExistException(cacheFileAbsolutePathName);
			}
			String buildDirectory = settings.getBuildDir(targetPlatformId);
			// Getting existing, or creating a new cache index 
			// for enabling faster access for cache data
			CacheIndex cacheIndx = CacheIndex.getCacheIndexInstance(cacheFile, buildDirectory);
			cacheIndexList.add(cacheIndx);
		}
		
		// Storing all component names here for duplicate component check
		Set<String> allComponentNames = new HashSet<String>();
		
		// Making sure that there are no previously queried results in the passes list
		duplicateItemsList.clear();
		
		// Storing component list nodes into here
		List<ComponentListNode> componentList = new ArrayList<ComponentListNode>();
		
		// Combining component lists from all selected targets
		for (int i = 0; i < cacheIndexList.size(); i++) {
			CacheIndex cacheIndx = cacheIndexList.get(i);
			Set<String> tmpSet = cacheIndx.getComponentNameSet();
			Iterator<String> tmpIter = tmpSet.iterator();
			// Iterating through the whole set
			while (tmpIter.hasNext()) {
				String cmpName = tmpIter.next();
				long lastModificationTimeForComponent = cacheIndx.getLastModifiedTimeForComponent(cmpName);
				componentList.add(new ComponentListNode(cmpName, currentlyUsedTargetPlatforms[i], lastModificationTimeForComponent));
				// Checking for possible duplicate component names
				// and adding found duplicates to the duplicate list
				checkForPossibleComponentDuplicate(duplicateItemsList, allComponentNames, cmpName);
			}						
		}
		return componentList;
	}	

	/**
	 * Checks if the given component has duplicates and adds to the duplicate
	 * list if found.
	 * @param duplicateItemsList Out parameter that is duplicate item list to be updated.
	 * @param allComponentNames	 All component names found so far from the targets.
	 * @param cmpName			 Component name to check for possible duplicate instances.
	 */
	private static void checkForPossibleComponentDuplicate(List<String> duplicateItemsList, Set<String> allComponentNames, String cmpName) {
		if(allComponentNames.contains(cmpName)){
			if(!duplicateItemsList.contains(cmpName)){
				duplicateItemsList.add(cmpName);
			}
		}
		else{
			allComponentNames.add(cmpName);
		}
	}
	
	/**
	 * Updates the cache index for the given cache file.
	 * @param cacheFileAbsolutePathName Absolute path name pointing to the 
	 * 									location of cache data file to be examined.
	 * @param buildDirectory Build directory string to be used as seek string to cache file.
	 * @throws IOException
	 * @throws CacheFileDoesNotExistException 
	 */
	public static void updateCacheIndexFor(String cacheFileAbsolutePathName, String buildDirectory) throws IOException, CacheFileDoesNotExistException{
		File localVarCacheFile = new File(cacheFileAbsolutePathName);
		if(!localVarCacheFile.exists()){
			throw new CacheFileDoesNotExistException(cacheFileAbsolutePathName);
		}
		// Getting existing, or creating a new cache index 
		// for enabling faster access for cache data
		CacheIndex localCacheIndxRef = CacheIndex.getCacheIndexInstance(localVarCacheFile, buildDirectory);
		localCacheIndxRef.update();
	}

	/**
	 * Checks if the cache index has been build for the queried cache file and build directory.
	 * @param cacheFileAbsolutePathName Absolute path name pointing to the 
	 * 									location of cache data file to be examined.
	 * @param buildDirectory Build directory string to be used as seek string to cache file.
	 * @return <code>true</code> if cache index has been build for the queried cache file, otherwise <code>false</code>.
	 * @throws IOException
	 * @throws CacheFileDoesNotExistException
	 */
	public static boolean isCacheIndexBuilt(String cacheFileAbsolutePathName, String buildDirectory) throws IOException, CacheFileDoesNotExistException{
		File cacheFile = new File(cacheFileAbsolutePathName);
		if(!cacheFile.exists()){
			throw new CacheFileDoesNotExistException(cacheFileAbsolutePathName);
		}
		// Getting existing, or creating a new cache index 
		// for enabling faster access for cache data
		CacheIndex cacheIndx = CacheIndex.getCacheIndexInstance(cacheFile, buildDirectory);
		return cacheIndx.isCacheIndexBuiltImpl();
	}
}
