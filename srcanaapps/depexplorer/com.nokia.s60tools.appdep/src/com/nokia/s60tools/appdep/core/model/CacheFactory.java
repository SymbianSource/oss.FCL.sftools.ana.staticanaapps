/*
* Copyright (c) 2008 Nokia Corporation and/or its subsidiary(-ies). 
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

package com.nokia.s60tools.appdep.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.data.CacheCompPropertyField;
import com.nokia.s60tools.appdep.core.data.CacheDataConstants;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.exceptions.InvalidModelDataException;
import com.nokia.s60tools.appdep.exceptions.ZeroFunctionOrdinalException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.LogUtils;
import com.nokia.s60tools.util.debug.DbgUtility;


/**
 * Singleton class that parses cache files and creates TargetCache object based 
 * on the parsed cache data.
 */
public class CacheFactory {
	
	// Indices for using imported component definition line in dependencies cache that
	// is of the following format: 
	//
	// component_name.dll|61
	//
	public static final int USED_COMPONENT_NAME_FIELD_INDEX = 0;
	public static final int IMPORTED_FUNCTION_CNT_FIELD_INDEX = 1;
	
	// Indices for using library file definition line in symbols cache that
	// is of the following format: 
	//
	// W:\epoc32\release\armv5\lib\|component_name.dso|1188902018|2229
	//
	public static final int LIB_NAME_FIELD_INDEX = 1;
	public static final int LIB_TIMESTAMP_FIELD_INDEX = 2;
	public static final int EXPORT_FUNC_COUNT_FIELD_INDEX = 3;
	private static final int LIB_DEF_LINE_FIELD_COUNT = 4;;
		
	/**
	 * This regular expression is used to split cache data lines.
	 */
	private static final String CACHE_FIELD_SEPARATOR_REGEXP = getCacheFieldSeparatorRegExp();

	/**
	 * Private exception used to notify about ending of cache file while doing reading.
	 */
	private class EOFException extends Exception{

		private static final long serialVersionUID = -7745437229230851377L;

		public EOFException(){
			super();
		}
	}
	
	/**
	 * Private helper class used for reading cache files.
	 */
	private class CacheFileReader{
		
		private BufferedReader br;
		private FileReader fileRdr;

		/**
		 * Constructor. 
		 * @param filePathName Absolute path name for the cache file to read from.
		 * @throws FileNotFoundException 
		 */
		public CacheFileReader(String filePathName) throws FileNotFoundException{
			File f = new File(filePathName);
			fileRdr = new FileReader(f);
			br = new BufferedReader(fileRdr);
		}		

		/**
		 * Reads the contents of a file line by line. 
		 * @throws EOFException 
		 * @throws IOException 
		 */
		public String nextLine() throws EOFException, IOException{
			String line = br.readLine();
			if(line == null){
				// End-of-file reached
				br.close();
				fileRdr.close();
				throw new EOFException();
			}
			return line;
		}		
	}
	
	/**
	 * Singleton instance of the factory class.
	 */
	private static CacheFactory instance;

	/**
	 * Private constructor.
	 */
	private CacheFactory(){	
	}
	
	/**
	 * Singleton class accessor method.
	 * @return Singleton instance of the class.
	 */
	public static CacheFactory getInstance(){
		if(instance == null){
			instance = new CacheFactory();
		}
		return instance;		
	}
	
	/**
	 * Load a single cache data from dependencies and symbols cache file into internal data structure.
	 * @param cacheDirectoryAbsolutePath Absolute path name to the directory cache files for 
	 *                                   the target reside. 
	 * @param targetPlatformId Id for the target platform to be loaded.
	 * @return Reference to data target data model service method interface.
	 * @param notifierRequestor If not <code>null</code> the client is requesting notifications about component loading process.
	 * @throws IOException 
	 */
	public ITargetCache loadCache(String cacheDirectoryAbsolutePath, String targetPlatformId, ICacheLoadProgressNotification notifierRequestor) throws IOException{
		TargetCache targetCache = new TargetCache(targetPlatformId);
		String dependenciesCacheVersion = parseAndPopulateDependencies(targetCache, cacheDirectoryAbsolutePath + File.separator + ProductInfoRegistry.getCacheFileName(), notifierRequestor);
		String symbolsCacheVersion = parseAndPopulateSymbols(targetCache, cacheDirectoryAbsolutePath + File.separator + ProductInfoRegistry.getCacheSymbolsFileName());
		// Cache file versions must match
		if(! dependenciesCacheVersion.equals(symbolsCacheVersion)){
			String errMsg = Messages.getString("CacheFactory.CacheDataIntegriryFailure_ErrMsg")  //$NON-NLS-1$
										+ " " + Messages.getString("CacheFactory.CacheDataIntegriryFailure_Version1_ErrMsg") //$NON-NLS-1$ //$NON-NLS-2$ 
										+ dependenciesCacheVersion + Messages.getString("CacheFactory.CacheDataIntegriryFailure_Version2_ErrMsg")  //$NON-NLS-1$
										+ symbolsCacheVersion + Messages.getString("CacheFactory.CacheDataIntegriryFailure_NoMatch_ErrMsg");//$NON-NLS-1$
			LogUtils.logStackTrace(errMsg, null);
			throw new RuntimeException(errMsg); 
		}
		targetCache.setVersion(dependenciesCacheVersion); // Storing cache version, if all versions are matching
		return targetCache;
	}
	
	/**
	 * Parses and populates dependencies cache data.
	 * @param targetCache Target cache to set dependencies cache data.
	 * @param dependenciesCacheAbsoluteFilePathName Absolute path name to cache data file.
	 * @param notifierRequestor If not <code>null</code> the client is requesting notifications about component loading process.
	 * @return Version number of the cache file.
	 * @throws IOException 
	 */
	private String parseAndPopulateDependencies(TargetCache targetCache,
			String dependenciesCacheAbsoluteFilePathName, ICacheLoadProgressNotification notifierRequestor) throws IOException {
		
		DependenciesCache dependenciesCache = new DependenciesCache();
		
		String cacheVersion = null;  
		String line = null;
		String prevLine = null;
		ComponentPropertiesData currentCmpPropData = null;

		try {
			CacheFileReader cacheRdr = new CacheFileReader(dependenciesCacheAbsoluteFilePathName);
			line = cacheRdr.nextLine();
			cacheVersion = getVersionInfoFromHeader(line, dependenciesCacheAbsoluteFilePathName);
			
			while(true){ // Reading lines until EOFException or other exception encountered
				prevLine = line;
				line = cacheRdr.nextLine();
				String[] stringArrayCandidate = splitPopertyLineIntoStringArray(line);
				if(isComponentDefinitionLineInMainCacheFile(stringArrayCandidate)){
					currentCmpPropData =  new ComponentPropertiesData(stringArrayCandidate);
					handleComponentReferences(cacheRdr, currentCmpPropData);					
					dependenciesCache.addComponentPropertiesData(currentCmpPropData,targetCache.getId());
					if(notifierRequestor != null){
						notifierRequestor.componentLoaded(currentCmpPropData.getFilename());						
					}
				}
				
			}
		} catch (EOFException e) {
			// Cache file must end with CacheIndex.CACHE_FILE_END_MARK in order being fully generated and valid one
			if(!prevLine.equals(CacheDataConstants.CACHE_FILE_END_MARK)){
				String errMsg = Messages.getString("CacheFactory.CacheDataIntegriryFailure_ErrMsg")  //$NON-NLS-1$ 
											+ " " + Messages.getString("CacheFactory.CacheDataIntegriryFailure_version_NoEOF_Mark_ErrMsg") //$NON-NLS-1$ //$NON-NLS-2$ 
											+ dependenciesCacheAbsoluteFilePathName + ").";//$NON-NLS-1$
				LogUtils.logStackTrace(errMsg, e);
				throw new RuntimeException(errMsg); 
			}
		} catch (Exception e) {
			String errMsg = Messages.getString("CacheFactory.CacheDataLoadFailure_ErrMsg")  //$NON-NLS-1$
										+ dependenciesCacheAbsoluteFilePathName + Messages.getString("CacheFactory.CacheDataLoadFailure_Unexpected_ErrMsg")  //$NON-NLS-1$
										+ e.getClass().getSimpleName() 
										+ "): " //$NON-NLS-1$
										+ e.getMessage();
			LogUtils.logStackTrace(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		
		// Setting dependencies cache data
		targetCache.setDependenciesCache(dependenciesCache);
		
		return cacheVersion;
	}

	/**
	 * Parses and populates symbols cache data.
	 * @param targetCache Target cache to set dependencies cache data.
	 * @param symbolsCacheAbsoluteFilePathName Absolute path name to cache data file.
	 * @return Version number of the cache file.
	 * @throws IOException 
	 */
	private String parseAndPopulateSymbols(TargetCache targetCache,
			String symbolsCacheAbsoluteFilePathName) throws IOException {
		
		SymbolsCache symbolsCache = new SymbolsCache();
		
		String cacheVersion = null;  
		String prevLine = null;
		ComponentPropertiesData currentCmpPropData = null;
		
		try {
			CacheFileReader cacheRdr = new CacheFileReader(symbolsCacheAbsoluteFilePathName);
			String line = cacheRdr.nextLine();
			cacheVersion = getVersionInfoFromHeader(line, symbolsCacheAbsoluteFilePathName);
			
			while(true){ // Reading lines until EOFException or other exception encountered
				prevLine = line;
				line = cacheRdr.nextLine();
				String[] stringArrayCandidate = splitPopertyLineIntoStringArray(line);
				// Checking if we have library definition line
				if(isComponentDefinitionLineSymbolsCacheFile(stringArrayCandidate)){
					// Getting field from the library definition line 
					String libBaseName = SymbolsCache.removeFileExtension(stringArrayCandidate[LIB_NAME_FIELD_INDEX]);
					long libCachedTimestamp = Long.parseLong(stringArrayCandidate[LIB_TIMESTAMP_FIELD_INDEX]);
					int exportedFuncCount = Integer.parseInt(stringArrayCandidate[EXPORT_FUNC_COUNT_FIELD_INDEX]);
					// Creating library data object
					LibPropertiesData libPropData = new LibPropertiesData(libBaseName, libCachedTimestamp);
					// Adding exported function into library data object
					handleExportedFunctions(cacheRdr, libPropData, exportedFuncCount);
					// Adding to symbols cache
					symbolsCache.addLibPropertiesData(libPropData);
				}
			}
		} catch (EOFException e) {
			// Cache file must end with CacheIndex.CACHE_FILE_END_MARK in order being fully generated and valid one
			if(!prevLine.equals(CacheDataConstants.CACHE_FILE_END_MARK)){
				String errMsg = Messages.getString("CacheFactory.CacheDataIntegriryFailure_ErrMsg")  //$NON-NLS-1$ 
											+ " " + Messages.getString("CacheFactory.CacheDataIntegriryFailure_NoEOF_Mark_ErrMsg") //$NON-NLS-1$ //$NON-NLS-2$ 
											+ symbolsCacheAbsoluteFilePathName + ").";//$NON-NLS-1$
				LogUtils.logStackTrace(errMsg, e);
				throw new RuntimeException(errMsg); 
			}
		} catch (InvalidModelDataException e) {
			String errMsg = Messages.getString("CacheFactory.CacheDataIntegriryFailure_ErrMsg")  //$NON-NLS-1$ 
										+ " " + Messages.getString("CacheFactory.CacheDataIntegriryFailure_InvalidComponent_ErrMsg") //$NON-NLS-1$ //$NON-NLS-2$ 
										+ symbolsCacheAbsoluteFilePathName + Messages.getString("CacheFactory.CacheDataIntegriryFailure_Detailed_ErrMsg") //$NON-NLS-1$
										+ currentCmpPropData;
			LogUtils.logStackTrace(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		
		// Setting symbols cache data
		targetCache.setSymbolsCache(symbolsCache);
		
		return cacheVersion;
	}

	/**
	 * Reads exported functions and stores into model
	 * @param cacheRdr Cache data reader object
	 * @param libPropData Library properties data object to add exported functions.
	 * @param exportedFuncCount Exported function count read from the reader object.
	 * @throws IOException 
	 * @throws EOFException 
	 */
	private void handleExportedFunctions(CacheFileReader cacheRdr, LibPropertiesData libPropData, int exportedFuncCount) throws EOFException, IOException {
		for (int exportFuncOrdinal = 1; exportFuncOrdinal <= exportedFuncCount; exportFuncOrdinal++) {
			String exportFuncName = cacheRdr.nextLine();
			Integer exportFuncOrdinalAsInteger = new Integer(exportFuncOrdinal);
			ExportFunctionData exportFunctionData;
			try {
				exportFunctionData = new ExportFunctionData(exportFuncOrdinalAsInteger.toString(), exportFuncName);
				libPropData.addExportedFunction(exportFunctionData);			
			} catch (ZeroFunctionOrdinalException e) {
				// Zero ordinal functions are just ignored and not added to properties data.
				DbgUtility.println(DbgUtility.PRIORITY_LOOP, "Encountered zero ordinal exported function: " + e.getFunctionName()); //$NON-NLS-1$ 									
			}
		}
	}

	/**
	 * This method is created purely for enabling JUnit testing of ITargetCache interface
	 * with a given set of test data without requiring parsing of 
	 * @param version Cache file data format version.
	 * @param id Id of the target platform this target cache represents 
	 * @param dependenciesCache Dependencies cache data.
	 * @param symbolsCache Library cache data.
	 * @return Reference to data target data model service method interface.
	 */
	public ITargetCache createCacheForUnitTests(String version, String id, DependenciesCache dependenciesCache, SymbolsCache symbolsCache){
		TargetCache cache = new TargetCache(id);
		cache.setDependenciesCache(dependenciesCache);
		cache.setSymbolsCache(symbolsCache);
		cache.setVersion(version);
		return cache;		
	}
	
	/**
	 * Reads version information from the header line
	 * @param headerLine Header line to be checked version info from.
	 * @param cacheFileAbsolutePathName
	 * @return Version string.
	 */
	private String getVersionInfoFromHeader(String headerLine, String cacheFileAbsolutePathName) {
		String[] splitArr = headerLine.split(CacheDataConstants.CACHE_VERSION_INFO_SEPARATOR);
		if(splitArr.length == 2){
			return splitArr[1].trim();
		}
		String errMsg = Messages.getString("CacheFactory.CacheDataIntegriryFailure_ErrMsg")  //$NON-NLS-1$ 
									+ " " + Messages.getString("CacheFactory.CacheDataIntegriryFailure_NoVersionNumber_ErrMsg") //$NON-NLS-1$ //$NON-NLS-2$ 
									+ cacheFileAbsolutePathName + ")."; //$NON-NLS-1$
		LogUtils.logStackTrace(errMsg, null);
		throw new RuntimeException(errMsg); 
	}
	
	
	/**
	 * Checks if the current line is a component definition line array.
	 * Line is component property line if the field count is the
	 * one that is expected for this kind of line.
	 * @param stringArrayCandidate String array candidate.
	 * @return <code>true</code> if component definition line, otherwise <code>false</code>.
	 */
	boolean isComponentDefinitionLineInMainCacheFile(String[] stringArrayCandidate) {	
		if(stringArrayCandidate.length == CacheCompPropertyField.getCompPropertyFieldCount()){
			return true;
		}		
		return false;
	}

	/**
	 * Splits the property line into a string array containing property line fields.
	 * @param compPropertiesLine Property line to be split.
	 * @return A string array containing property line fields.
	 */
	String[] splitPopertyLineIntoStringArray(String compPropertiesLine){
		return compPropertiesLine.split(CACHE_FIELD_SEPARATOR_REGEXP);
	}
	
	/**
	 * Stores all the components from which this component property data object imports functions. 
	 * @param cacheRdr Cache data reader.
	 * @param currentCmpPropData Component properties object into which add imported functions and their components.
	 * @throws IOException 
	 * @throws EOFException 
	 */
	private void handleComponentReferences(CacheFileReader cacheRdr,
			ComponentPropertiesData currentCmpPropData) throws EOFException, IOException {

		// Getting amount of referred components
		int referredCompCount = currentCmpPropData.getDllRefTableCountAsInt();
		// Storing information from the all referred components.
		for (int handledCmpCount = 0; handledCmpCount < referredCompCount; handledCmpCount++) {
			// Getting component name and count of imported functions
			String referredCompLine = cacheRdr.nextLine();		
			String [] referredCompArr = splitPopertyLineIntoStringArray(referredCompLine);		
			String referredCmpName = referredCompArr[USED_COMPONENT_NAME_FIELD_INDEX];
			String importedFuncCountAsString = referredCompArr[IMPORTED_FUNCTION_CNT_FIELD_INDEX];
			int importedFuncCount = Integer.parseInt(importedFuncCountAsString);

			// Adding data model object for storing functions imported from the component
			UsedComponentData usedCmpData = new UsedComponentData(referredCmpName);
			
			// Temporary variables used for access textual cache data
			String importedFuncLine;
			String[] importedFuncLineArr;
			boolean isVirtual;
			
			// Browsing through all the imported functions for the component
			for (int i = 0; i < importedFuncCount; i++) {
				importedFuncLine = cacheRdr.nextLine();
				importedFuncLineArr = splitPopertyLineIntoStringArray(importedFuncLine);
				isVirtual = isVirtualFunctionFlagSet(importedFuncLineArr);				
				ImportFunctionData importFuncData;
				try {
					importFuncData = new ImportFunctionData(
															importedFuncLineArr[ImportFunctionData.ORDINAL_FIELD_INDEX], 
															importedFuncLineArr[ImportFunctionData.NAME_FIELD_INDEX], 
															isVirtual, 
															importedFuncLineArr[ImportFunctionData.OFFSET_FIELD_INDEX] 								
															);
					usedCmpData.addImportedFunction(importFuncData);
				} catch (ZeroFunctionOrdinalException e) {
					// Zero ordinal functions are just ignored and not added to properties data.
					DbgUtility.println(DbgUtility.PRIORITY_LOOP, "Encountered zero ordinal imported function: " //$NON-NLS-1$ 
																 + e.getFunctionName());
				}
			} // for
			// After imported function are added, adding imported component to the using component object
			currentCmpPropData.addUsedComponentData(usedCmpData);
		} // for
		
	}

	/**
	 * Checks from given imported function info array if the function 
	 * is virtual. 
	 * The call is further delegated to CacheIndex class
	 * @param importedFuncLineArr Function info array
	 * @return <code>true</code> if the function is virtual, 
	 *         otherwise <code>false</code>.
	 */
	public static boolean isVirtualFunctionFlagSet(String[] importedFuncLineArr) {
		return CacheIndex.isVirtualFunctionFlagSet(importedFuncLineArr);
	}

	/**
	 * Returns regular expression used to split fields in cache data. 
	 * The call is further delegated to CacheIndex class
	 * @return regular expression used to split fields in cache data.
	 */
	private static String getCacheFieldSeparatorRegExp() {
		return CacheIndex.getCacheFieldSeparatorRegExp();
	}

	/**
	 * Checks if the current line is a library definition line.
	 * @param stringArrayCandidate Line to be checked.
	 * @return <code>true</code> if component definition line, otherwise <code>false</code>.
	 */
	private boolean isComponentDefinitionLineSymbolsCacheFile(String[] stringArrayCandidate) {		
		if(stringArrayCandidate.length == LIB_DEF_LINE_FIELD_COUNT){
			return true;
		}		
		return false;
	}
}
