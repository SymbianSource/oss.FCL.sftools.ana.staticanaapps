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
 
 
package com.nokia.s60tools.appdep.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.data.CacheIndex;
import com.nokia.s60tools.appdep.core.job.AppDepJobManager;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.SdkEnvInfomationResolveFailureException;
import com.nokia.s60tools.sdk.SdkInformation;
import com.nokia.s60tools.sdk.SdkManager;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * This class stores the AppDep tool related settings that are currently
 * in effect and offers services that are related to those settings. 
 * The class is implemented as singleton in order to make sure  
 * that there are no possibility to have conflicting intances
 * of this class created by many parties.
 */
public class AppDepSettings {
	
	//
	// Public available constants
	//
	/**
	 * Name of the executable that is used for RVCT tool chain to resolve
	 * dependency information.
	 */
	public static final String RVCT_FROM_ELF_EXECUTABLE = "fromelf.exe"; //$NON-NLS-1$
		
	/**
	 * Abbreviated name for GNU Compiler Collection.
	 * Used as command line parameter, therefore DO NOT CHANGE
	 * unless the command line interface is changed.
	 */
	public static final String STR_GCC = "GCC"; //$NON-NLS-1$
	/**
	 * Abbreviated name for Arm Toolchain.
	 * Used as command line parameter, therefore DO NOT CHANGE
	 * unless the command line interface is changed.
	 */
	public static final String STR_GCCE = "GCCE"; //$NON-NLS-1$
	/**
	 * Abbreviated name for RealView Compilation Tools.
	 * Used as command line parameter, therefore DO NOT CHANGE
	 * unless the command line interface is changed.
	 */
	public static final String STR_RVCT = "RVCT"; //$NON-NLS-1$

	/**
	 * Epoc32 directory name constant.
	 */
	public static final String STR_EPOC32_DIR = "epoc32";	 //$NON-NLS-1$
	
 	//
	// Private constants
	//
	private static final String STR_RELEASE_DIR = "release";	 //$NON-NLS-1$
	
	//
	// Constants for unsupported targets
	//
	
	// Not supporting targets that start with the following strings
	private static final String STR_WILDCHARD_WIN = "WIN"; //$NON-NLS-1$
	private static final String STR_WILDCHARD_TOOLS = "TOOLS"; //$NON-NLS-1$
	
	// Not supporting targets with following exact matches
	// => There are not currenly any exact math targets defined
	
	// Constants for GCC toolchain targets
	private static final String STR_ARMI = "ARMI"; //$NON-NLS-1$
	private static final String STR_ARM4 = "ARM4"; //$NON-NLS-1$
	private static final String STR_THUMB = "THUMB"; //$NON-NLS-1$
	// Wildchards for GCC toolchain targets
	private static final String STR_WILDCHARD_GCC_M = "M"; //$NON-NLS-1$
	
	/**
	 * Location relative to epoc32 directory for elftran.exe executable 
	 * that is needed for creating cache files.
	 */
	private static final String STR_ELFTRAN_EXE = "elftran.exe"; //$NON-NLS-1$

	/**
	 * Location relative to epoc32 directory for elftran.exe executable 
	 * that is needed for creating cache files.
	 */
	private static final String STR_PETRAN_EXE = "petran.exe"; //$NON-NLS-1$

	/**
	 * Directory where GCC toolchain tools exist.
	 * Directory is relative to SDK's root directory.
	 * @see #getGccToolsDir
	 */
	private static final String GCC_TOOL_REL_DIR = STR_EPOC32_DIR + "\\gcc\\bin"; //$NON-NLS-1$
	
	/**
	 * When SIS file(s) is selected for analysis, Target platform type is "sis".
	 */
	public static final String TARGET_TYPE_ID_SIS = "sis"; //$NON-NLS-1$
	
	private static Vector<AppDepSettings> settingInstancesVector = null;
	
	// Supported toolchain objects...
	private Toolchain toolchainGCC;
	private Toolchain toolchainGCCE;
	private Toolchain toolchainRVCT;

	//.. are stored in vector in order to make checking easier
	private Vector<Toolchain> supportedToolchainsVector = null;
	
	/**
	 * Storing also those target types that are not supported by the tool.
	 */
	private Vector<String> notSupportedTargetsVector = null;	

	/**
	 * And making also wildchard check for the targets starting
	 * with some specified string.
	 */
	private Vector<String> notSupportedTargetWildchardsVector = null;	

	/**
	 * Information for currently selected 
	 * SDK or Platform.
	 */
	private SdkInformation currentlyUsedSdk = null;

	/**
	 * Directory where GCCE toolchain tools exist.
	 * For example, "C:\Program Files\CSL Arm Toolchain\bin"
	 */
	private String gcceToolsDir = null;
	
	/**
	 * Directory where RVCT toolchain tools exist.
	 * For example, "C:\Program Files\ARM\RVCT\Programs\2.2\503\win_32-pentium"
	 */
	private String rvctToolsDir = null;
	
	
	/**
	 * Directory where cfilt.exe is located.
	 */
	String externalProgramsPathName = null;

	/**
	 * Current user preference for the used toolchain.
	 * This updated automatically when the currently
	 * used target platform is set. The default value
	 * is based on the preferred toolchain choice, 
	 * and on the fact that which toolchains are
	 * available in the workstation's environment. 
	 */
	private IToolchain currentlyUsedToolChain = null;	
	
	/**
	 * Preferences to be used for cache generation.
	 */
	private CacheGenerationOptions cacheGenerOptions = null;

	/**
	 * Current user preference for the selected targets.
	 */
	private ArrayList<ITargetPlatform> currentlyUsedTargetPlatforms= null;
	
	/**
	 * The name of the currently analyzed component. 
	 */
	private String currentlyAnalyzedComponentName = null;
	
	/**
	 * Currently supported build types are
	 * - BuildTypeDebug, and
	 * - BuildTypeRelease
	 */
	private IBuildType buildType = null;
	
	/**
	 * This is used to cache the amount of components
	 * for the recently queried platform. The component
	 * query may take a considerable long time, and therefore
	 * caching improves performance.
	 */
	private int mostRecentlyQueriedComponentCount;	
	
	/**
	 * Path where print report is recently exported. 
	 * Used to set as default path when export functionality is used
	 * again during same session.
	 */
	private String exportPrintReportPath = null;
	
	/**
	 * If XML report (file) is generated aswell when Exporting html report.
	 */
	private boolean exportXMLreport = false;

	/**
	 * Path to resources. XSL file is located in there.
	 */
	private String resourcesPath = null;
	
	/**
	 * XSL file name, default value "PrintReport.xsl"
	 */
	private String XSLFileName = null;

	/**
	 * XSL file name, default value "IsUsedByReport.xsl"
	 */
	private String isUsedByXSLFileName = null;	
	
	/**
	 * Listeners who want to know about the changes in the current settings.
	 */
	private Vector<IAppDepSettingsChangedListener> settingListenersVector;
	
	/**
	 * SIS files for analysis
	 */
	private String[] sisFilesForAnalysis = null;
	
	/**
	 * If SDK Selection Wizard is running in SIS analysis mode 
	 */
	private boolean isInSISFileAnalysisMode = false;

	/**
	 * Cache data loading flag.
	 */
	private boolean isCacheDataLoadingOngoing = false;

	/**
	 * Cache update flag is set to <code>true</code> when target platform is not changed 
	 * but target cache has been updated and needs reloading.
	 */
	private boolean cacheUpdated;

	/**
	 * Target platform for the currently analyzed component.
	 */
	private ITargetPlatform currentlyAnalyzedComponentTargetPlatform = null;
	
	/**
	 * Accessor for currently active settings.
	 * @return Returns currently active instance.
	 */
	public static AppDepSettings getActiveSettings(){
		
		AppDepSettings instance = null;
		
		if( settingInstancesVector == null ){
			settingInstancesVector = new Vector<AppDepSettings>();
			instance = new AppDepSettings();
			settingInstancesVector.add(instance);			
		}
		else{
			instance = (AppDepSettings) settingInstancesVector.lastElement();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */	
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object clone(){
		AppDepSettings clone = new AppDepSettings();
		
		// Doing actual cloning
		clone.toolchainGCC = toolchainGCC;
		clone.toolchainGCCE = toolchainGCCE;
		clone.toolchainRVCT = toolchainRVCT;
		clone.supportedToolchainsVector = (Vector<Toolchain>) supportedToolchainsVector.clone();
		clone.notSupportedTargetsVector  = (Vector<String>) notSupportedTargetsVector .clone();
		clone.notSupportedTargetWildchardsVector  = (Vector<String>) notSupportedTargetWildchardsVector .clone();
		
		clone.currentlyUsedSdk = currentlyUsedSdk;
		clone.gcceToolsDir = gcceToolsDir;
		clone.rvctToolsDir = rvctToolsDir;
		clone.externalProgramsPathName = externalProgramsPathName;
		clone.currentlyUsedToolChain = currentlyUsedToolChain;
		clone.cacheGenerOptions = cacheGenerOptions;
		ITargetPlatform[] targetPlatformArr = getCurrentlyUsedTargetPlatforms();
		for (int i = 0; i < targetPlatformArr.length; i++) {
			ITargetPlatform targetPlatformId = targetPlatformArr[i];
			clone.currentlyUsedTargetPlatforms.add(targetPlatformId);
			
		}
		clone.buildType = buildType;
		clone.currentlyAnalyzedComponentName = currentlyAnalyzedComponentName;
		clone.currentlyAnalyzedComponentTargetPlatform = currentlyAnalyzedComponentTargetPlatform;
		clone.exportPrintReportPath = exportPrintReportPath;
		clone.resourcesPath = resourcesPath;
		clone.XSLFileName = XSLFileName;
		clone.isUsedByXSLFileName = isUsedByXSLFileName;		
		clone.settingListenersVector = (Vector<IAppDepSettingsChangedListener>) settingListenersVector.clone();
		
		if(sisFilesForAnalysis != null){
			clone.sisFilesForAnalysis = new String[sisFilesForAnalysis.length];
			for(int i = 0; i<sisFilesForAnalysis.length; i++){
				clone.sisFilesForAnalysis[i] = new String( sisFilesForAnalysis[i] );
			}
		}else{
			clone.sisFilesForAnalysis = null;
		}
		clone.isInSISFileAnalysisMode = isInSISFileAnalysisMode;
		clone.cacheUpdated = cacheUpdated;
		
		return clone;
	}
	
	/**
	 * Clones the currently active instance and sets
	 * clone as new active instance.
	 * @return Returns cloned instance.
	 */
	public static AppDepSettings cloneAndAddAsNewActiveInstance(){
		AppDepSettings existing = null;
		AppDepSettings cloned = null;
		// We trust here that there is at least a single active instance
		existing = (AppDepSettings) settingInstancesVector.lastElement();
		cloned = (AppDepSettings) existing.clone();
		settingInstancesVector.add(cloned);				
		return cloned;
	}
	
	/**
	 * Sets given settings instance as currently active settings.
	 * @param newActiveSettings New settings to be set as current
	 *                          active settings.
	 * @return Returns the settings that were just set as currently
	 *         active settings.
	 */
	public static AppDepSettings setAsNewActiveInstance(AppDepSettings newActiveSettings){
		settingInstancesVector.add(newActiveSettings);	
		return newActiveSettings;
	}	

	/**
	 * Creates a new uninitialized instance and sets it
	 * as active instance. 
	 * @return Returns a new instance.
	 */
	public static AppDepSettings newActiveInstance(){
		AppDepSettings instance = null;		
		if( settingInstancesVector == null ){
			settingInstancesVector = new Vector<AppDepSettings>();
		}		
		instance = new AppDepSettings();
		settingInstancesVector.add(instance);				
		return instance;
	}

	/**
	 * Check for the existence of previous active instances.
	 * @return Returns <code>true</code> if there is an earlier instance, 
	 *         otherwise <code>false</code>.
	 */
	public static boolean hasPreviousActiveInstance(){
		if( settingInstancesVector != null ){
			return (settingInstancesVector.size() > 1);
		}		
		return false;
	}

	public static void removePreviousInstances(){
		if( settingInstancesVector != null ){
			if(settingInstancesVector.size() > 0){
				Vector<AppDepSettings> preserveThese = new Vector<AppDepSettings>();
				preserveThese.add(settingInstancesVector.lastElement());
				// Deleting all the other elements than the last one
				settingInstancesVector.retainAll(preserveThese);				
			}
		}		
	}
	
	/**
	 * Returns the previously active instance and removes the
	 * currently active instance.
	 * @return Returns the previously active instance.
	 */
	public static AppDepSettings restorePreviousActiveInstance(){
		
		if(! hasPreviousActiveInstance()){
			throw new RuntimeException(Messages.getString("AppDepSettings.Restore_NonExisting_Instance")); //$NON-NLS-1$
		}
		
		AppDepSettings instance = null;
		Object currentlyActive = settingInstancesVector.lastElement();
		settingInstancesVector.remove(currentlyActive);			
		instance = (AppDepSettings) settingInstancesVector.lastElement();
		return instance;
	}
	
	/**
	 * Default constructor 
	 */
	private AppDepSettings(){		
		gcceToolsDir = new String(""); //$NON-NLS-1$
		rvctToolsDir = new String(""); //$NON-NLS-1$
		externalProgramsPathName = new String(""); //$NON-NLS-1$
		currentlyUsedTargetPlatforms = new ArrayList<ITargetPlatform>();
		settingListenersVector = new Vector<IAppDepSettingsChangedListener>();
		buildType = new BuildTypeRelease();
		initializeSupportedToolchains();
		initializeNotSupportedTargets();
	}

	private void initializeSupportedToolchains(){
		supportedToolchainsVector = new Vector<Toolchain>();
		
		// Creating toolchain instances
		toolchainGCC = new Toolchain(STR_GCC, Messages.getString("AppDepSettings.GNU_Comp_Coll"), false, true); //$NON-NLS-1$
		// GCCE is used as preferred default toolchain when it can be used
		toolchainGCCE = new Toolchain(STR_GCCE, Messages.getString("AppDepSettings.CSL_Arm_Toolchain"), true); //$NON-NLS-1$
		toolchainRVCT = new Toolchain(STR_RVCT, Messages.getString("AppDepSettings.RVCT_Comp_Tools"), false); //$NON-NLS-1$
		
		supportedToolchainsVector.add(toolchainGCC);
		supportedToolchainsVector.add(toolchainGCCE);
		supportedToolchainsVector.add(toolchainRVCT);
	}

	/**
	 * Building here a list of not supported targets.
	 */
	private void initializeNotSupportedTargets(){
		
		// Initializing exact match targets
		notSupportedTargetsVector = new Vector<String>();
		// => There are not currenly any exact math targets defined
		
		// Initializing startsWith match targets
		notSupportedTargetWildchardsVector = new Vector<String>();
		notSupportedTargetWildchardsVector.add(STR_WILDCHARD_WIN);
		notSupportedTargetWildchardsVector.add(STR_WILDCHARD_TOOLS);
	}

	/**
	 * Returns tools directory for the currently used SDK/Platform. 
	 * @return Tools directory for the currently used SDK/Platform.
	 */
	private String getToolsDirForCurrentlyUsedSdk() {		
		return removeEndingBackslash (getCurrentlyUsedSdk().getEpoc32ToolsDir());
	}

    /**
     * Returns path to cache directory relative to tools directory.
	 * The directory name MUST not contain ending backslash!!!
     * @return Path to cache directory relative to tools directory.
     */
    private String getCachePathRelativeToToolsDir(){
    	return ProductInfoRegistry.getS60RndToolsDir()
				+ File.separatorChar 
				+ ProductInfoRegistry.getAppDepCacheDir();
    }
		
	/**
	 * Returns cache base directory for the currently used SDK/Platform.
	 * The directory name MUST not contain ending backslash!!!
	 * @return Cache base directory for the currently used SDK/Platform.
	 */
	public String getCacheBaseDirForCurrentlyUsedSdk() {		
		return getToolsDirForCurrentlyUsedSdk() 
				+ File.separatorChar
				+ getCachePathRelativeToToolsDir();
	}
	
	/**
	 * Returns the cache directories pointed by current settings. 
	 * @return Returns the cacheBaseDir.
	 */
	public String[] getCacheDirs() {
		ITargetPlatform[] targets = getCurrentlyUsedTargetPlatforms();
		int targetCount = targets.length;
		String[] resultArr = new String[targetCount];
		for (int i = 0; i < targets.length; i++) {
			String targetPlatformId = targets[i].getId();
			resultArr[i] = getCacheDirForTarget (targetPlatformId);
		}
		return resultArr;	
	}	
	
	/**
	 * Returns the cache directory for given target. 
	 * @param targetPlatformId Id for target platform to be used in directory name.
	 * @return Returns the cache directory for given target.
	 */
	public String getCacheDirForTarget(String targetPlatformId) {
		if(targetPlatformId.equalsIgnoreCase(AppDepSettings.TARGET_TYPE_ID_SIS)){
			return getCacheBaseDirForCurrentlyUsedSdk()
				+ File.separatorChar
				+ removeEndingBackslash (targetPlatformId);
		}
		else{
			return getCacheBaseDirForCurrentlyUsedSdk()
				+ File.separatorChar
				+ removeEndingBackslash (targetPlatformId)
				+ File.separatorChar
				+ removeEndingBackslash (getBuildType().getBuildTypeName());	
		}
	}	

	/**
	 * Returns the cache directory for given arguments. 
	 * @return Returns the cache directory for given arguments.
	 */	
	private String getCacheDir(SdkInformation sdkInfo, String targetPlatformName, IBuildType buildType) {
		String cacheDir = removeEndingBackslash (sdkInfo.getEpoc32ToolsDir()) 
					+ File.separatorChar
					+ getCachePathRelativeToToolsDir()
					+ File.separatorChar					
					+ removeEndingBackslash (targetPlatformName )
					+ File.separatorChar
					+ removeEndingBackslash (buildType.getBuildTypeName());
		return cacheDir;	
	}	

	/**
	 * Returns absolute pathname to the currently 
	 * used cache file for given target.
	 * @param targetPlatformId Id for target platform to be used in directory name.
	 * @return Returns currently used cache file's path name.
	 */
	public String getCacheFileAbsolutePathName(String targetPlatformId) {
		return getCacheDirForTarget(targetPlatformId)
			+ File.separatorChar 
			+ ProductInfoRegistry.getCacheFileName();
	}

	/**
	 * Returns absolute pathname to the symbols table file of the 
	 * currently used cache directory  for given target.
	 * @param targetPlatformId Id for target platform to be used in directory name.
	 * @return Returns currently used symbols table file's path name.
	 */
	public String getCacheSymbolsTableFileAbsolutePathName(String targetPlatformId) {
		return getCacheDirForTarget(targetPlatformId)
			+ File.separatorChar 
			+ ProductInfoRegistry.getCacheSymbolsFileName();
	}
	
	/**
	 * Returns absolute pathname to the cache file from the
	 * SDK ID and Target Platform name given as parameters.
	 * @param sdkInfo SDK information object. 
	 * @param targetPlatformId Target Platform Name string
	 * @param buildType Build type.
	 * @return Absolute pathname to the cache file.
	 */
	public String getCacheFileAbsolutePathNameForSdkAndPlatform(SdkInformation sdkInfo, 
																String targetPlatformId,
																IBuildType buildType) {
		return 
		getCacheDir(sdkInfo,targetPlatformId,buildType) 
				+ File.separatorChar 
				+ ProductInfoRegistry.getCacheFileName();
	}
	
	/**
	 * Checks is the given Target Platform is cached.
	 * 
	 * @param sdkId
	 *            SDK ID string.
	 * @param targetPlatformName
	 *            Target Platform Name string
	 * @param buildType
	 *            Build type.
	 * @return Returns <code>true</code> if the target platform is cached,
	 *         otherwise <code>false</code>.
	 */	
	public boolean isTargetPlatformCached(String sdkId,
			String targetPlatformName, IBuildType buildType) {
		SdkInformation[] infos;
		
		try {
			infos = SdkManager.getSdkInformation();
			for (int i = 0; i < infos.length; i++) {
				SdkInformation info = infos[i];
				if (sdkId.equals(info.getSdkId())) {
					return isTargetPlatformCached(info, targetPlatformName,
							buildType);
				}
			}

		} catch (SdkEnvInfomationResolveFailureException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks is the given Target Platform is cached.
	 * 
	 * @param sdkInfo
	 *             SDK information object.
	 * @param targetPlatformName
	 *            Target Platform Name string
	 * @param buildType
	 *            Build type.
	 * @return Returns <code>true</code> if the target platform is cached,
	 *         otherwise <code>false</code>.
	 */
	public boolean isTargetPlatformCached(SdkInformation sdkInfo,
			String targetPlatformName, IBuildType buildType) {

		String cacheFilePath = getCacheFileAbsolutePathNameForSdkAndPlatform(
				sdkInfo, targetPlatformName, buildType);
		File cacheFile = new File(cacheFilePath);
		if (cacheFile.exists()) {
			// There is no temporary file and cache file exists
			return true;
		}

		// Cache file does not exist
		return false;
	}
	

	/**
	 * Returns the absolute path pointing to cfilt program.
	 * @return Returns pathname to cfilt program
	 */
	public String getCfiltProgramPathName() {
		return getExternalProgramsPathName()
				+ File.separatorChar 
				+ ProductInfoRegistry.getCfiltBinaryName();
	}

	/**
	 * Returns the absolute path pointing to AppDep
	 * command line executable.
	 * @return Returns pathname to appdep program
	 */
	public String getAppDepProgramPathName() {
		return getExternalProgramsPathName()
				+ File.separatorChar 
				+ ProductInfoRegistry.getAppDepBinaryName();
	}

	/**
	 * @return Returns the externalProgramsPathName.
	 */
	public String getExternalProgramsPathName() {
		return externalProgramsPathName;
	}
	
	/**
	 * @param externalProgramsPathName The externalProgramsPathName to set.
	 */
	public void setExternalProgramsPathName(String externalProgramsPathName) {
		this.externalProgramsPathName = removeEndingBackslash(externalProgramsPathName);
	}

	/**
	 * Returns the currently used target platforms.
	 * @return Array of currently used target platforms.
	 */
	public ITargetPlatform[]  getCurrentlyUsedTargetPlatforms() {
		return currentlyUsedTargetPlatforms.toArray(new TargetPlatform[0]);
	}

	/**
	 * Checks if the given target platform is supported.
	 * @param targetPlatform Target platform to be checked.
	 * @return Returns <code>true</code> if the given target platform 
	 * is supported, otherwise <code>false</code>.
	 */
	public boolean isSupportedTargetPlatform(String targetPlatform){
		// Target is not supported if it is listed in predefined list
		for (Iterator<String> iter = notSupportedTargetsVector.iterator(); iter.hasNext();) {
			String str = iter.next();
			if( str.compareToIgnoreCase( targetPlatform ) == 0 ){
				return false;
			}
		}
		
		// Target is also not supported if it is listed in predefined list of startsWith matches
		for (Iterator<String> iter = notSupportedTargetWildchardsVector.iterator(); iter.hasNext();) {
			String str = iter.next().toLowerCase();
			if( targetPlatform.toLowerCase().startsWith(str) ){
				return false;
			}
		}
		
		// Not defined as unsupported, therefore supported
		return true;
	}
	
	/**
	 * Sets new targets as currently used ones.
	 * Updates also currently used toolchain accordingly.
	 * This method must be called with targets that maps
	 * to the same toolchain, and all given targets
	 * must be supported.
	 * @param targetPlatformNameArr The new target set to be set as currently ised targets..
	 */
	public void setCurrentlyUsedTargetPlatforms(String[] targetPlatformNameArr)
										throws InvalidCmdLineToolSettingException {

		// Supported one ...clearing old settings
		clearCurrentlyUsedTargetPlatforms();
		// Temporary variable to find out correct toolchain settings
		IToolchain toolchainSetting = null;
		
		// Checking that no empty array was passed
		if(targetPlatformNameArr.length > 0){
			toolchainSetting = getDefaultToolchainForTarget(targetPlatformNameArr[0]);
		}
		else{
			throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Target_Array_Is_Empty") );  //$NON-NLS-1$
		}
		// Checking validity of the settings and storing the given targets
		for (int i = 0; i < targetPlatformNameArr.length; i++) {
			String targetPlatformId = targetPlatformNameArr[i];
			if(! isSupportedTargetPlatform(targetPlatformId)){
				// Not found, regarded as unsupported
				throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Target")  //$NON-NLS-1$
						+ targetPlatformNameArr  
						+ Messages.getString("AppDepSettings.NotSupported") ); //$NON-NLS-1$
			}			
			// Check was OK => storing the target information
			currentlyUsedTargetPlatforms.add(new TargetPlatform(targetPlatformId));
			// All items must map to the same toolchain
			IToolchain toolchainSettingTmp = getDefaultToolchainForTarget(targetPlatformId);
			if(! toolchainSettingTmp.equals(toolchainSetting)){
				throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Targets_Does_Not_Map_To_Same_Toolchain") );  //$NON-NLS-1$				
			}
		}
		// Finally setting the used toolchain accordingly
		setCurrentlyUsedToolChain(toolchainSetting);							
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * Returns the used toolchain.
	 * @return Returns the currentlyUsedToolChain.
	 */
	public IToolchain getCurrentlyUsedToolChain() {
		return currentlyUsedToolChain;
	}

	/**
	 * Returns the used toolchain's name.
	 * @return Returns the currently used toolchains name.
	 */
	public String getCurrentlyUsedToolChainName() {
		return currentlyUsedToolChain.getToolchainName();
	}
	
	/**
	 * Sets new toolchain as currently used one.
	 * @param newToolChainSetting The currentlyUsedToolChain to set.
	 */
	private void setCurrentlyUsedToolChain(IToolchain newToolChainSetting) 
										throws InvalidCmdLineToolSettingException {
		// Can we found the toolchain among the supported ones
		for (Iterator<Toolchain> iter = supportedToolchainsVector.iterator(); iter.hasNext();) {
			Toolchain tch = iter.next();
			String str = tch.getToolchainName();
			if( str.compareToIgnoreCase( newToolChainSetting.getToolchainName() ) == 0 ){
				this.currentlyUsedToolChain = newToolChainSetting;
				return;
			}
		}
		// Not found, regarded as unsupported
		throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Toolchain")  //$NON-NLS-1$
				+ newToolChainSetting  
				+ Messages.getString("AppDepSettings.NotSupported") ); //$NON-NLS-1$
	}
	
	/**
	 * Returns the used directory for GCCE tools.
	 * @return Returns the gcceToolsDir.
	 */
	public String getGcceToolsDir() {
		return gcceToolsDir;
	}

	/**
	 * @param gcceToolsDir The gcceToolsDir to set.
	 */
	public void setGcceToolsDir(String gcceToolsDir) {
		this.gcceToolsDir = removeEndingBackslash(gcceToolsDir);
	}

	/**
	 * Gets GCCE toolchain installation status.
	 * @return Returns <code>true</code> if toolchain is installed,
	 *         otherwise <code>false</code>.
	 */
	public boolean isGcceToolsInstalled() {
		return toolchainGCCE.isInstalled();
	}

	/**
	 * Sets GCCE toolchain installation status.
	 * @param isGcceToolsInstalled The installation status to set.
	 */
	public void setGcceToolsInstalled(boolean isGcceToolsInstalled) {
		toolchainGCCE.setIsInstalled(isGcceToolsInstalled);
	}

	/**
	 * Gets RVCT toolchain installation status.
	 * @return Returns <code>true</code> if toolchain is installed,
	 *         otherwise <code>false</code>.
	 */
	public boolean isRvctToolsInstalled() {
		return toolchainRVCT.isInstalled();
	}

	/**
	 * Sets RVCT toolchain installation status.
	 * @param isRvctToolsInstalled The installation status to set.
	 */
	public void setRvctToolsInstalled(boolean isRvctToolsInstalled) {
		toolchainRVCT.setIsInstalled(isRvctToolsInstalled);
	}

	/**
	 * Returns the used root directory of SDK.
	 * @return Returns the sdkRootDir.
	 */
	public String getSdkRootDir() {
		if(currentlyUsedSdk != null){
			return removeEndingBackslash(currentlyUsedSdk.getEpocRootDir());
		}
		return null;
	}

	/**
	 * Returns the used directory for RVCT tools.
	 * @return Returns the rvctToolsDir.
	 */
	public String getRvctToolsDir() {
		return rvctToolsDir;
	}

	/**
	 * Sets the used directory for RVCT tools.
	 * @param rvctToolsDir The rvctToolsDir to set.
	 */
	public void setRvctToolsDir(String rvctToolsDir) {
		this.rvctToolsDir = removeEndingBackslash(rvctToolsDir);
	}

	/**
	 * @return Returns the buildType.
	 */
	public IBuildType getBuildType() {
		return buildType;
	}
	
	/**
	 * Checks that given parameter referes to supported build
	 * type, and sets the new type if it is supported.
	 * @param buildTypeString The name of build type to set.
	 * @throws InvalidCmdLineToolSettingException If the given build type is not supported.
	 */		
	public void setBuildType(String buildTypeString) throws InvalidCmdLineToolSettingException {
		if( buildTypeString.equalsIgnoreCase(BuildTypeRelease.NAME)){
			this.buildType = new BuildTypeRelease();			
		}
		else if( buildTypeString.equalsIgnoreCase(BuildTypeDebug.NAME)){
			this.buildType = new BuildTypeDebug();			
		}
		else{
			// Not found, regarded as unsupported
			throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Build_Type")  //$NON-NLS-1$
					+ buildTypeString  
					+ Messages.getString("AppDepSettings.NotSupported") );			 //$NON-NLS-1$
		}
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * Checks that given parameter referes to supported build
	 * type, and sets the new type if it is supported.
	 * @param buildType The build type object to set.
	 * @throws InvalidCmdLineToolSettingException If the given build type is not supported.
	 */	
	public void setBuildType(IBuildType buildType) throws InvalidCmdLineToolSettingException {
		if( (buildType instanceof BuildTypeDebug)
			||
			(buildType instanceof BuildTypeRelease) 	
			){
			this.buildType = buildType;			
		}
		else{
			// Not found, regarded as unsupported
			throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Build_Type")  //$NON-NLS-1$
					+ buildType.getBuildTypeName()  
					+ Messages.getString("AppDepSettings.NotSupported") );			 //$NON-NLS-1$
		}
		// Notifying possible listeners
		notifySettingsListeners(false);
	}	
	
	/**
	 * Maps the given build type string into corresponding
	 * build object type if the given type is supported..
	 * @param buildTypeString The name of build type to set.
	 * @throws InvalidCmdLineToolSettingException If the given build type is not supported.
	 */		
	public IBuildType getBuildTypeFromString(String buildTypeString) throws InvalidCmdLineToolSettingException {
		if( buildTypeString.equalsIgnoreCase(BuildTypeRelease.NAME)){
			return new BuildTypeRelease();			
		}
		else if( buildTypeString.equalsIgnoreCase(BuildTypeDebug.NAME)){
			return new BuildTypeDebug();			
		}
		
		// Not found, regarded as unsupported
		throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Build_Type")  //$NON-NLS-1$
				+ buildTypeString  
				+ Messages.getString("AppDepSettings.NotSupported") );			 //$NON-NLS-1$
	}

	/**
	 * Builds release dir based on the already known directory
	 * components.
	 * @return Returns the release directory.
	 */
	public String getReleaseDir() {
		return 	getSdkRootDir()
					+ File.separator
					+ STR_EPOC32_DIR
					+ File.separator
					+ STR_RELEASE_DIR;
	}

	/**
	 * Builds the build based on the already known directory
	 * components.
	 * @param targetPlatformId Target platform to get build directory for.
	 * @return Returns the build directory.
	 */
	public String getBuildDir(String targetPlatformId) {
		return 	getReleaseDir()
					+ File.separator
					+ targetPlatformId
					+ File.separator
					+ getBuildType().getBuildTypeName();
	}

	/**
	 * Builds the build based on the already known 
	 * directory components.
	 * @return Returns the build directories 
	 *          as set for currently selected target.
	 */
	public Set<String> getBuildDirsAsSet() {
		Set<String> buildDirSet = new HashSet<String>();
		ITargetPlatform[] targets = getCurrentlyUsedTargetPlatforms();
		for (int i = 0; i < targets.length; i++) {
			ITargetPlatform platform = targets[i];
			String buildDir = getReleaseDir()
									+ File.separator
									+ platform.getId()
									+ File.separator
									+ getBuildType().getBuildTypeName();
			buildDirSet.add(buildDir);
		}
		return buildDirSet;	
	}
	
	/**
	 * @return Returns the currentlyUsedSdk.
	 */
	public SdkInformation getCurrentlyUsedSdk() {
		return currentlyUsedSdk;
	}

	/**
	 * @param currentlyUsedSdk The currentlyUsedSdk to set.
	 */
	public void setCurrentlyUsedSdk(SdkInformation currentlyUsedSdk) {
		this.currentlyUsedSdk = currentlyUsedSdk;
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * Returns build directory for the given Sdk information node
	 * and target platform name.
	 * @param sdkInfo Sdk information object.
	 * @param targetPlatformName Target platform name.
	 * @param buildType Build type.
	 * @return Build directory string.
	 */
	public String getBuildDirectoryForSdkAndPlatform(SdkInformation sdkInfo, 
													 String targetPlatformName,
													 IBuildType buildType) {
		return 	sdkInfo.getReleaseRootDir()
				+ File.separator
				+ targetPlatformName
				+ File.separator
				+ buildType.getBuildTypeName();
	}

	public void updateCurrentlyUsedSDKAndTargetPlatforms(SdkInformation currentlyUsedSdk,
												   String[] currentlyUsedTargetPlatforms,
												   IBuildType currentlyUsedBuildType
												   ) throws InvalidCmdLineToolSettingException{
		setCurrentlyUsedSdk(currentlyUsedSdk);
		setCurrentlyUsedTargetPlatforms(currentlyUsedTargetPlatforms);
		setBuildType(currentlyUsedBuildType);
	}

	/**
	 * The method adds given target to the list of currently used ones.
	 * The method checks if the toolChain of given platform maps with the
	 * default toolchain (of all other platforms). If not, the method throws an exception.
	 * @param targetPlatformId The new target to be added to currently used targets.
	 */
	public void addTargetPlatform(String targetPlatformId) throws InvalidCmdLineToolSettingException
	{
		if(! isSupportedTargetPlatform(targetPlatformId)){
			// Given target is not found in the list of supported targets. So, regarded as unsupported
			throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Target")  //$NON-NLS-1$
					+ targetPlatformId  
					+ Messages.getString("AppDepSettings.NotSupported") ); //$NON-NLS-1$
		}
		
		// Check was OK => adding the target information
		currentlyUsedTargetPlatforms.add(new TargetPlatform(targetPlatformId));
		// The toolchain of given platform must match with the toolchain of all existing platforms.
		IToolchain toolchainSettingTmp = getDefaultToolchainForTarget(targetPlatformId);
		if(toolchainSettingTmp == null || !toolchainSettingTmp.getToolchainName().equalsIgnoreCase(getCurrentlyUsedToolChainName())){
			throw new InvalidCmdLineToolSettingException( Messages.getString("AppDepSettings.Targets_Does_Not_Map_To_Same_Toolchain") );  //$NON-NLS-1$				
		}
		
		notifySettingsListeners(false);
	}
	/**
	 * @return Returns the currentlyAnalyzedComponentName.
	 */
	public String getCurrentlyAnalyzedComponentName() {
		return currentlyAnalyzedComponentName;
	}

	/**
	 * Sets currently analyzed component name and resets
	 * target platform setting. Target platform can be set after this call 
	 * by calling method <code>setCurrentlyAnalyzedComponentTargetPlatform</code>.
	 * @param currentlyAnalyzedComponentName The currentlyAnalyzedComponentName to set.
	 */
	public void setCurrentlyAnalyzedComponentName(
			                                String currentlyAnalyzedComponentName) {		
		this.currentlyAnalyzedComponentName = currentlyAnalyzedComponentName;
		this.currentlyAnalyzedComponentTargetPlatform = null; // not set by default
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * Sets the target platform for the currently analyzed component.
	 * Should be called after <code>setCurrentlyAnalyzedComponentName</code> is called if needed.
	 * @param currentlyAnalyzedComponentTargetPlatform The target platform for the currently analyzed component.
	 */
	public void setCurrentlyAnalyzedComponentTargetPlatform(
			                                ITargetPlatform currentlyAnalyzedComponentTargetPlatform) {		
		this.currentlyAnalyzedComponentTargetPlatform = currentlyAnalyzedComponentTargetPlatform;
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * Gets target platform for the currently analyzed component.
	 * @return target platform for the currently analyzed component, or <code>null</code> if not set.
	 */
	public ITargetPlatform getCurrentlyAnalyzedComponentTargetPlatform() {
		return currentlyAnalyzedComponentTargetPlatform;
	}

	/**
	 * Checks if cache data loading is ongoing.
	 * @return <code>true</code> if cache data loading is ongoing, otherwise <code>false</code>.
	 */
	public boolean isCacheDataLoadingOngoing(){
		return isCacheDataLoadingOngoing;
	}
	
	/**
	 * Checks if caching is ongoing for the given target.
	 * @param sdkInfo SDK information object 
	 * @param targetPlatformName Target Platform Name string
	 * @param buildType Build type.
	 * @return Returns <code>true</code> if caching is ongoing, 
	 *         otherwise <code>false</code>.
	 */
	public boolean isCacheGenerationOngoingForTarget(SdkInformation sdkInfo, 
										 String targetPlatformName,
										 IBuildType buildType) {	
		return AppDepJobManager.getInstance().hasCacheGenerationJobForTarget(sdkInfo.getSdkId(),
																			targetPlatformName,
																			buildType);
	}
	
	/**
	 * Checks if caching is ongoing for the given SDK.
	 * @param sdkInfo SDK information object 
	 * @return Returns <code>true</code> if caching is ongoing, 
	 *         otherwise <code>false</code>.
	 */
	public boolean isCacheGenerationOngoingForSdk(SdkInformation sdkInfo) {	
		return AppDepJobManager.getInstance().hasCacheGenerationJobForSdk(sdkInfo.getSdkId());
	}
	
	/**
	 * Returns component file objects for the SDK and Target Platform 
	 * given as parameters.
	 * @param sdkInfo SDK information object. 
	 * @param targetPlatformName Target Platform Name string
	 * @param buildType Build type.
	 * @return File object array of given components.
	 */
	private File[] getComponentsForSdkAndPlatform(SdkInformation sdkInfo, 
												  String targetPlatformName,
												  IBuildType buildType) {
		
		String releaseDirPath = 
							sdkInfo.getReleaseRootDir() 
							+ File.separatorChar 
							+ targetPlatformName
							+ File.separatorChar 
							+ buildType.getBuildTypeName();
		File releaseDir = new File(releaseDirPath);
		if(releaseDir.exists())
			return releaseDir.listFiles(new ComponentFileFilter());
		else{
			return null;
		}
	}

	/**
	 * Returns component count for the SDK and Target Platform 
	 * given as parameters.
	 * @param sdkInfo SDK information object. 
	 * @param targetPlatformName Target Platform Name string
	 * @param buildType Build type.
	 * @return Component count
	 */
	public int getComponentCountForSdkAndPlatform(SdkInformation sdkInfo, 
												  String targetPlatformName,
												  IBuildType buildType) {
		
		mostRecentlyQueriedComponentCount = 0;
		
		String releaseDirPath = 
							sdkInfo.getReleaseRootDir() 
							+ File.separatorChar 
							+ targetPlatformName
							+ File.separatorChar 
							+ buildType.getBuildTypeName();
		File releaseDir = new File(releaseDirPath);
		if(releaseDir.exists()){
			String[] fileArr = releaseDir.list(new ComponentFileFilter());
			mostRecentlyQueriedComponentCount = fileArr.length;
			return mostRecentlyQueriedComponentCount;			
		}
		// Directory was not found => no components.
		return mostRecentlyQueriedComponentCount;
	}
	
	/**
	 * Check if cache needs to be updated i.e. there is newer
	 * files existing in build directory. Note that this method returns
	 * <code>true</code> whenever it meets the first target requiring update.
	 * @param sdkInfo SDK information object. 
	 * @param targets Target to check cache update need for
	 * @param buildType Build type.
	 * @return <code>true</code> if cache needs update, otherwise <code>false</code>.
	 */
	public boolean cacheNeedsUpdate(SdkInformation sdkInfo, 
									ITargetPlatform[] targets, IBuildType buildType){

		// Checking through all the targets
		for (int i = 0; i < targets.length; i++) {
			ITargetPlatform platform = targets[i];
			String targetPlatformName = platform.getId();
			
			if(targetPlatformName.equals(TARGET_TYPE_ID_SIS)){
				continue;
			}
			
			String cacheFilePath = getCacheFileAbsolutePathNameForSdkAndPlatform(
																				sdkInfo, 
																				targetPlatformName,
																				buildType);
			File cacheFile = new File(cacheFilePath);
			if(!cacheFile.exists()){
				// We should not encounter this situation, but if there is no
				// cache file, then it surely needs update.
				return true;
			}
			
			String buildDir = getBuildDirectoryForSdkAndPlatform(sdkInfo, 
																 targetPlatformName,
																 buildType);
			
			CacheIndex cacheIndx = null;
		    try {
				cacheIndx = CacheIndex.getCacheIndexInstance(cacheFile,
						                                                buildDir);
			} catch (IOException e) {
				e.printStackTrace();
				// We should no encounter in this situation, but then
				// it is better to enable the regeneration of the cache.
				return true;
			}

			// Checking that cache is not corrupted. 
			if(! cacheIndx.isCacheNonCorrupted()){
				// If corrupted, then needs update
				return true;
			}
			
			// Checking that cache is of correct version
			if(! cacheIndx.getVersionInfo().equals(ProductInfoRegistry.getSupportedCacheFileVersionInfoString())){
				// If non-supported version, then needs update
				return true;
			}			
			File[] fileArr = null;
			
		    fileArr = getComponentsForSdkAndPlatform(sdkInfo, 
					  									   targetPlatformName,
					  									   buildType);
		    // Comparing found components against cache information
			if(fileArr != null && isComponentFileArrayChangedWhenComparingWithCache(cacheIndx, fileArr)){
				return true;
			}
			
		}//for
		
	    return false;
	}

	/**
	 * Checks if given component file array has been changed in respect to cache file.
	 * @param cacheIndx Cache index used to check for timestamps stored in cache.
	 * @param componentFileArr File array to be checked against cache information.
	 * @return <code>true</code> if cache needs update, otherwsise <code>false</code>.
	 */
	private boolean isComponentFileArrayChangedWhenComparingWithCache(CacheIndex cacheIndx, File[] componentFileArr) {
		
		File f = null;
		for (int j = 0; j < componentFileArr.length; j++) {
			f = componentFileArr[j];
			String basename = f.getName();
			try {
				long fileModifiedAsMillisecAccuracy = f.lastModified();
				// Flooring timestamp into second level accuracy. The unit is still milliseconds.
				long fileModifiedAsSecAccuracy = floorTimestampIntoSecondLevelAccuracy(fileModifiedAsMillisecAccuracy);
				// Unit for cache timestamp got from cache index is also milliseconds 
				long cacheTimestamp = cacheIndx.getLastModifiedTimeForComponent(basename);
				
				if (fileModifiedAsSecAccuracy != cacheTimestamp) {	
					
					// Absolute value of the difference in milliseconds
					long diff = Math.abs((fileModifiedAsSecAccuracy - cacheTimestamp));
					
					//
					// lastModified() method of File-class does not
					// always produce correct results in Windows environment.
					// Sometimes it gives time stamps that are exactly 
					// one hour wrong due to daylight savings:
					//
					// For details, see 
					// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4860999
					//
					// Therefore ignoring exact 1 hour differences in timestamps.
					//					
					final int DL_SAVINGS_BUG_CHECK_INTERVAL = (1000 * 60 * 60);
					
					// Allowing a  range N seconds when identifying if the cache information of
					// a component is up-to-date. This is because if the cache file has been
					// generated with different operating system, the time stamps may not be exactly
					// same. This line declares the currently used N.
					final int DIFF_TOLERANCE = (1000 * 2); // 1000 * N
					
					// Checking the timestamp, first via bug filter
					// Ignoring timestamps possibly caused due to the bug when adjusted with tolerance
					if(
						diff < (DL_SAVINGS_BUG_CHECK_INTERVAL - DIFF_TOLERANCE)
						||
						diff > (DL_SAVINGS_BUG_CHECK_INTERVAL + DIFF_TOLERANCE)						
							){
						// If the bug filter was passed we can continue checking...						
						
						if(diff > DIFF_TOLERANCE){
							// Found component that is newer that cache
							DbgUtility.println(DbgUtility.PRIORITY_LOOP, 
									"Time stamps differ for component '" //$NON-NLS-1$
									+ basename + "' " //$NON-NLS-1$
									+ fileModifiedAsSecAccuracy 
									+ " != " + cacheTimestamp); //$NON-NLS-1$
							DbgUtility.println(DbgUtility.PRIORITY_LOOP, 
									"Time stamps differ for component '" //$NON-NLS-1$
									+ basename + "' " //$NON-NLS-1$
									+ new Date(fileModifiedAsSecAccuracy).toString() 
									+ " != " + new Date(cacheTimestamp).toString() ); //$NON-NLS-1$
							return true;													
						}
					}
				} 
			} catch (NoSuchElementException e) {
				// A new component has been added, and therefore
				// cache file needs update.
				DbgUtility.println(DbgUtility.PRIORITY_LOOP, 
						           "Component not found from cache index: " + basename); //$NON-NLS-1$
					return true;
				}			
		}//for
		
		return false;
	}
	
	/**
	 * Floors milliseconds timestamp into nearest second.
	 * @param millisecTimestamp Timestamp with millisecond accuracy.
	 * @return Timestamp with second accuracy.
	 */
	private long floorTimestampIntoSecondLevelAccuracy(long millisecTimestamp){
		long seconds = millisecTimestamp/1000;
		return (1000 * seconds);
	}
	
	/**
	 * Filter filenames that are regarded as valid components
	 * for static dependency analysis.
	 */
	private class ComponentFileFilter implements FilenameFilter{

		public boolean accept(File dir, String name) {
			
			// Filtering is made based on file extension.
			// Accepting the following extensions.
			String[] allowedExtensions = { "dll", //$NON-NLS-1$
						                    "exe", //$NON-NLS-1$
						                    "tsy", //$NON-NLS-1$
						                    "csy", //$NON-NLS-1$
						                    "fsy", //$NON-NLS-1$
						                    "dsy", //$NON-NLS-1$
						                    "prt", //$NON-NLS-1$
						                    "app", //$NON-NLS-1$
						                    "psy", //$NON-NLS-1$
						                    "fep", //$NON-NLS-1$
						                    "agt", //$NON-NLS-1$
						                    "fxt" //$NON-NLS-1$
			                             };
			
			String regExp = Pattern.quote("."); //$NON-NLS-1$
			String[] splitArr = name.split(regExp);
			int itemCount = splitArr.length;
			if(itemCount > 1){
				String extension = splitArr[itemCount-1];
				for (int i = 0; i < allowedExtensions.length; i++) {
					String allowedExt = allowedExtensions[i];
					if(extension.equalsIgnoreCase(allowedExt)){
						return true;
					}					
				}				
			}
			
			return false;
		}
	}

	/**
	 * Removes ending backslash from directory if such exists.
	 * @param directoryStr Directory path name to be checked for removal.
	 * @return Returns directory path name without ending backslash.
	 */
	private String removeEndingBackslash(String directoryStr) {
		if(directoryStr.endsWith(File.separator)){
			return directoryStr.substring(0, (directoryStr.length()-1));
		}
		// No backslash => returning string as it was
		return directoryStr;
	}
		
	/**
	 * Goes through the directory given in <code>File</code> object and deletes
	 * all the only partially created cache files.
	 * @param directoryToBeChecked
	 * @param parentDirectoryPath
	 */
	private void deletePartiallyCreatedCacheFiles(File directoryToBeChecked, String parentDirectoryPath) {
		File[] fileArr = directoryToBeChecked.listFiles();
		for (int i = 0; i < fileArr.length; i++) {
			File f = fileArr[i];
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, parentDirectoryPath + f.getName());	
			if(f.isDirectory()){
				// Traversing also sub directories
				deletePartiallyCreatedCacheFiles(f, parentDirectoryPath + f.getName() + File.separatorChar);
			}
			else{
				
				File cacheFile = new File(f.getParent() 
		                  + File.separatorChar
		                  + ProductInfoRegistry.getCacheFileName());
				File symbolsTableFile = new File(f.getParent() 
		                  + File.separatorChar
		                  + ProductInfoRegistry.getCacheSymbolsFileName());

				//
				// Cache files are considered as partially created, if
				// - either of the cache info files is of zero size
				//	
				boolean cacheFileIsZeroSized = cacheFile.exists() && (cacheFile.length() == 0);
				boolean symbolsTableFileIsZeroSized = symbolsTableFile.exists() && (symbolsTableFile.length() == 0);
				
				if(cacheFileIsZeroSized || symbolsTableFileIsZeroSized){					

					// Partially created cache ...

					// Deleting cache file first
					if(cacheFile.exists()){
						DbgUtility.println(DbgUtility.PRIORITY_LOOP, "\tDeleting cache file!!!"); //$NON-NLS-1$
						if(!cacheFile.delete()){
							AppDepConsole.getInstance().println(Messages.getString("AppDepSettings.Failed_to_Delete_Cache_File")  //$NON-NLS-1$
										       + cacheFile.getAbsolutePath(), IConsolePrintUtility.MSG_ERROR);							
						}
					}
					// Then deleting the symbol tables file, if it exists
					if(symbolsTableFile.exists()){
						DbgUtility.println(DbgUtility.PRIORITY_LOOP, "\tDeleting symbol tables cache file!!!"); //$NON-NLS-1$
						if(!symbolsTableFile.delete()){
							AppDepConsole.getInstance().println(Messages.getString("AppDepSettings.Failed_to_Delete_Symbols_File") //$NON-NLS-1$
									           + f.getAbsolutePath(), IConsolePrintUtility.MSG_ERROR);
						}						
					}
				}
			}
		}
	}
	
	/**
	 * Clean-up cache files that are only partially created.
	 */
	public void cleanupPartiallyCreatedCacheFiles(){
		
		SdkInformation[] infos;
		File cacheDir;
		String strCacheDir;
		//Found all installed SDK:s, search through all of them and clean 
		try {
			infos = SdkManager.getSdkInformation();			
			for (int i = 0; i < infos.length; i++) {
				SdkInformation info = infos[i];
				strCacheDir = 
					removeEndingBackslash (info.getEpoc32ToolsDir()) 
					+ File.separatorChar
					+ getCachePathRelativeToToolsDir();
				cacheDir = new File(strCacheDir);
				if(cacheDir.exists()){
					DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "cleanupPartiallyCreatedCacheFiles for '" + cacheDir.getAbsolutePath() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
					deletePartiallyCreatedCacheFiles(cacheDir, cacheDir.getName() + File.separatorChar);
				}
			}

		} catch (SdkEnvInfomationResolveFailureException e) {
			e.printStackTrace();
		}		
		
		
	}

	/**
	 * @return Returns the mostRecentlyQueriedComponentCount.
	 */
	public int getMostRecentlyQueriedComponentCount() {
		return mostRecentlyQueriedComponentCount;
	}
	
	/**
	 * @return Returns the gccToolsDir.
	 */
	public String getGccToolsDir() {
		return getCurrentlyUsedSdk().getEpocRootDir()
				+ GCC_TOOL_REL_DIR;
	}
	
	/**
	 * Gets array of all supported toolchains.
	 * @return Array of all supported toolchains.
	 */
	public IToolchain[] getAllSupportedToolchains(){
		return (IToolchain[]) supportedToolchainsVector.toArray(new IToolchain[0]);
	}
	
	/**
	 * Gets array of supported toolchains for currently selected target.
	 * @return Array of supported toolchains for currently selected target..
	 * @throws InvalidCmdLineToolSettingException
	 */
	public IToolchain[] getSupportedToolchainsForCurrentlyUsedTargets() throws InvalidCmdLineToolSettingException{
		// In setCurrentlyUsedTargetPlatforms it is made sure that all targets
		// platforms will map to the same toolchains => therefore we can
		// safely use just the first target platform instance.
		String targetPlatformId = getCurrentlyUsedTargetPlatforms()[0].getId();		
		return getSupportedToolchainsForGivenTargetPlatformId(targetPlatformId);
	}

	
	/**
	 * Gets array of supported toolchains for given target platform id.
	 * @param targetPlatformId Target platform id.
	 * @return Array of supported toolchains for given target platform id.
	 * @throws InvalidCmdLineToolSettingException
	 */
	public IToolchain[] getSupportedToolchainsForGivenTargetPlatformId(String targetPlatformId) throws InvalidCmdLineToolSettingException{
		
		ArrayList<Toolchain> toolchainsForTargetArrayList = new ArrayList<Toolchain>();
		
		// Checking GCC targets
		if(targetPlatformId.equalsIgnoreCase(STR_ARMI)
				||
				targetPlatformId.equalsIgnoreCase(STR_ARM4)
				||
				targetPlatformId.equalsIgnoreCase(STR_THUMB)
				||
				targetPlatformId.toLowerCase().startsWith(STR_WILDCHARD_GCC_M.toLowerCase())
				){
			toolchainsForTargetArrayList.add(toolchainGCC);		
		}
		// Using these for all others
		else{
			toolchainsForTargetArrayList.add(toolchainGCCE);		
			toolchainsForTargetArrayList.add(toolchainRVCT);		
		}
		
		return (IToolchain[]) toolchainsForTargetArrayList.toArray(new IToolchain[0]);
	}

	/**
	 * Gets default toolchain for currently the given target.
	 * @param targetId Target to check for default toolchain.
	 * @return Default toolchain for currently the given target.
	 * @throws InvalidCmdLineToolSettingException
	 */
	public IToolchain getDefaultToolchainForTarget(String targetId) throws InvalidCmdLineToolSettingException{
		
		IToolchain defaultToolchain = null;
					
		// Checking for supported GCC toolchain targets
		if(targetId.equalsIgnoreCase(STR_ARMI)
				||
				targetId.equalsIgnoreCase(STR_ARM4)
				||
				targetId.equalsIgnoreCase(STR_THUMB)
				||
				targetId.toLowerCase().startsWith(STR_WILDCHARD_GCC_M.toLowerCase())
				){
			defaultToolchain = toolchainGCC;		
		}
		else{
			if(isGcceToolsInstalled()){
				// This is preferred toolchain
				defaultToolchain = toolchainGCCE;		
			}
			else{
				// This is alternate toolchain for same purpose
				defaultToolchain = toolchainRVCT;		
			}
		}

		return defaultToolchain;
	}
	
	/**
	 * @return Returns the cacheGenerOptions.
	 */
	public CacheGenerationOptions getCacheGenerOptions() {
		return cacheGenerOptions;
	}

	/**
	 * @param cacheGenerOptions The cacheGenerOptions to set.
	 */
	public void setCacheGenerOptions(CacheGenerationOptions cacheGenerOptions) {
		this.cacheGenerOptions = cacheGenerOptions;
		// Notifying possible listeners
		notifySettingsListeners(false);
	}
	
	/**
	 * If SDK selection wizard is launched with SIS page option.
	 * @return <code>true</code> if Add SIS files page is launched and Wizard is in SIS Analysis mode.
	 */
	public boolean isInSISFileAnalysisMode() {
		return isInSISFileAnalysisMode;
	}
	/**
	 * Set if SDK selection wizard is launched with SIS page option.
	 * @param set Wizard to SIS analysis mode.
	 */
	public void setIsInSISFileAnalysisMode(boolean isInSISFileAnalysisMode) {
		this.isInSISFileAnalysisMode = isInSISFileAnalysisMode;
		if(!isInSISFileAnalysisMode){
			sisFilesForAnalysis = null;
		}
	}	
	
	/**
	 * Get SIS files selected in SIS selection page.
	 * @return SIS files selected in SIS selection page if this wizard is SIS wizard
	 * (this.isInSISFileAnalysisMode() returns <code>true</code>, otherwise null. 
	 * Returned files names is absolutely file names with path.
	 */
	public String[] getSISFilesForAnalysis() {
		if(isInSISFileAnalysisMode()){
			return sisFilesForAnalysis;
		}
		else{
			return null;
		}
	}		
	/**
	 * Set selected SIS files for analysis
	 * @param valid Symbian 9.x sisFiles (absolutely file names with path)
	 */
	public void setSISFilesForAnalysis(String[] sisFiles) {
		this.sisFilesForAnalysis = sisFiles;
	}		

	/**
	 * @return Returns the exportPrintReportPath.
	 */
	public String getExportPrintReportPath() {
		return exportPrintReportPath;
	}

	/**
	 * @param exportPrintReportPath The exportPrintReportPath to set.
	 */
	public void setExportPrintReportPath(String exportPrintReportPath) {
		this.exportPrintReportPath = exportPrintReportPath;
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * @return Returns the exportXMLreport.
	 */
	public boolean isExportXMLreport() {
		return exportXMLreport;
	}

	/**
	 * @param exportXMLreport The exportXMLreport to set.
	 */
	public void setExportXMLreport(boolean exportXMLreport) {
		this.exportXMLreport = exportXMLreport;
		// Notifying possible listeners
		notifySettingsListeners(false);
	}

	/**
	 * @return Returns the resourcesPath.
	 */
	public String getResourcesPath() {
		return resourcesPath;
	}

	/**
	 * @param resourcesPath The resourcesPath to set.
	 */
	public void setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
	}

	/**
	 * @return Returns the XSLFileName.
	 */
	public String getXSLFileName() {
		return XSLFileName;
	}

	/**
	 * @param fileName The XSLFileName to set.
	 */
	public void setXSLFileName(String fileName) {
		this.XSLFileName = fileName;
	}

	/**
	 * @param cacheUpdated The cacheUpdated to set.
	 */
	public void cacheWasUpdated() {
		cacheUpdated = true;
	}

	/**
	 * @return Returns the isUsedByXSLFileName.
	 */
	public String getIsUsedByXSLFileName() {
		return isUsedByXSLFileName;
	}

	/**
	 * @param isUsedByXSLFileName The isUsedByXSLFileName to set.
	 */
	public void setIsUsedByXSLFileName(String isUsedByXSLFileName) {
		this.isUsedByXSLFileName = isUsedByXSLFileName;
	}

	/**
	 * Returns currently used targets as string representation.
	 * @return
	 */
	public String getCurrentlyUsedTargetPlatformsAsString() {
		ITargetPlatform[] targets = getCurrentlyUsedTargetPlatforms();
		String targetPlatformStrList = ""; //$NON-NLS-1$
		if(targets.length > 0){
			targetPlatformStrList = targets[0].getId();
			for (int i = 1; i < targets.length; i++) {
				String targetPlatformId = targets[i].getId();
				targetPlatformStrList = targetPlatformStrList + "+" + targetPlatformId;  //$NON-NLS-1$
			}			
		}
		return targetPlatformStrList;
	}

	/**
	 * Checks if Elftran is available for given SDK or platform.
	 * @param sdkInfo  SDK information object. 
	 * @return <code>true</code> if elftran is available, otherwise <code>false</code>.
	 */
	public boolean isElftranAvailable(SdkInformation sdkInfo){
		String absolutePath = 	sdkInfo.getEpoc32ToolsDir() + File.separatorChar + STR_ELFTRAN_EXE;
		File elftranFile = new File(absolutePath);
		if(elftranFile.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if Petran is available for given SDK or platform.
	 * @param sdkInfo  SDK information object. 
	 * @return <code>true</code> if petran is available, otherwise <code>false</code>.
	 */
	public boolean isPetranAvailable(SdkInformation sdkInfo){
		String absolutePath = 	sdkInfo.getEpoc32ToolsDir() + File.separatorChar + STR_PETRAN_EXE;
		File petranFile = new File(absolutePath);
		if(petranFile.exists()){
			return true;
		}
		return false;
	}

	/**
	 * Checks if Dumpsis.exe is available for given SDK or platform.
	 * @param sdkInfo  SDK information object. 
	 * @return <code>true</code> if Dumpsis.exe is available, otherwise <code>false</code>.
	 */
	public boolean isDumpsisAvailable(SdkInformation sdkInfo){
		String absolutePath = 	sdkInfo.getEpoc32ToolsDir() + File.separatorChar + ProductInfoRegistry.getDumpsisExeFileName();
		File dumpsisFile = new File(absolutePath);
		if(dumpsisFile.exists()){
			return true;
		}
		return false;		
	}
	
	/**
	 * Adds a new settings change listener object.
	 * @param listener Listener to be added.
	 */
	public void addSettingsListener(IAppDepSettingsChangedListener listener){
		settingListenersVector.add(listener);
	}
	
	/**
	 * Removes a new settings change listener object.
	 * @param listener Listener to be removed.
	 */
	public void removeSettingsListener(IAppDepSettingsChangedListener listener){
		settingListenersVector.remove(listener);
	}
	
	/**
	 * Notifies listeners that settings has been changed.
	 * @param isTargetBuildChanged Should set to <code>true</code> by the caller if, the currently
	 * 							   used target build settings has been changed, otherwise set to 
	 * 							   <code>false</code> by the caller (for example, if only currently
	 *                             analyzed component has been changed). 
	 */
	private void notifySettingsListeners(boolean isTargetBuildChanged){
		for (IAppDepSettingsChangedListener listener : settingListenersVector) {
			listener.settingsChanged(isTargetBuildChanged);
		}		
	}
	
	/**
	 * Checks if currently selected caches require update.
	 * @return <code>true</code> if some of the currently selected caches
	 *         needs update, otherwise <code>false</code>.
	 */
	public boolean currentlySelectedCachesNeedsUpdate(){
		return cacheNeedsUpdate(getCurrentlyUsedSdk(), 
								getCurrentlyUsedTargetPlatforms(), 
					            getBuildType());
	}

	/**
	 * Checks if two setting instances had equal target platform selection.
	 * @param settingsToCompareThisTo Settings object to compare this instance against.
	 * @return <code>true</code> if two setting instances had equal target platform selection, otherwise <code>false</code>.
	 */
	public boolean hasEqualTargetPlatformSelections(AppDepSettings settingsToCompareThisTo) {
		
		//
		// Doing actual comparison 
		//
		if(settingsToCompareThisTo.currentlyUsedSdk.getSdkId() != currentlyUsedSdk.getSdkId()) return false;
		
		ITargetPlatform[] targetPlatformArrThis = getCurrentlyUsedTargetPlatforms();
		ITargetPlatform[] targetPlatformArrThisCompareTo = settingsToCompareThisTo.getCurrentlyUsedTargetPlatforms();
		
		if(targetPlatformArrThisCompareTo.length != targetPlatformArrThis.length) return false;
		
		for (int i = 0; i < targetPlatformArrThisCompareTo.length; i++) {
			ITargetPlatform targetPlatformIdThisCompareTo = targetPlatformArrThisCompareTo[i];
			ITargetPlatform targetPlatformIdThis = targetPlatformArrThis[i];
			// If any of the selected targets differ => no match
			if(targetPlatformIdThisCompareTo.getId() != targetPlatformIdThis.getId()) return false;
		}
		// Also build type must match
		if(settingsToCompareThisTo.buildType.getBuildTypeName() != buildType.getBuildTypeName()) return false;
		
		return true;
	}

	/**
	 * @param isCacheDataLoadingOngoing the isCacheDataLoadingOngoing to set
	 */
	public void setCacheDataLoadingOngoing(boolean isCacheDataLoadingOngoing) {
		this.isCacheDataLoadingOngoing = isCacheDataLoadingOngoing;
	}

	/**
	 * @return the cacheUpdated
	 */
	public boolean isCacheUpdated() {
		return cacheUpdated;
	}

	/**
	 * Resets cache update flag back to <code>false</code>.
	 */
	public void resetCacheUpdateFlag() {
		this.cacheUpdated = false;
	}

	/**
	 * Returns string representation of currently selected SDK and target platforms.
	 * @return String representation of currently selected SDK and target platforms.
	 */
	public String getCurrentlyUsedTargetsAsString() {
		return "'"  //$NON-NLS-1$
				+ getCurrentlyUsedSdk().getSdkId()
				+ " - " //$NON-NLS-1$
				+ getCurrentlyUsedTargetPlatformsAsString()
				+ " " //$NON-NLS-1$
				+ getBuildType().getBuildTypeDescription()
				+ "'"; //$NON-NLS-1$;
	}

	/**
	 * Clear currently used target platforms from settings.
	 */
	public void clearCurrentlyUsedTargetPlatforms() {
		currentlyUsedTargetPlatforms.clear();
	}

	/**
	 * Check if root component has been already selected under analysis.
	 * @return <code>true</code> if root component has been selected, 
	 */
	public static boolean isRootComponentSelectedForAnalysis() {
		String currentRootComponent = null;
		AppDepSettings activeSettings = getActiveSettings();
		if(activeSettings != null){
			currentRootComponent =  activeSettings.getCurrentlyAnalyzedComponentName();					
			if(currentRootComponent != null){
				// Root component selected
				return true;
			}			
		}
		// Root component not selected
		return false;
	}

	/**
	 * Sets version of the RVCT toolchain.
	 * @param version string representation of toolchain version.
	 */
	public void setRvctToolsVersion(String version) {
		toolchainRVCT.setVersion(version);
	}

	/**
	 * Gets currently set RVCT tools version.
	 * @return string representation of toolchain version, or <code>null</code> if version info is not available. 
	 */
	public String getRvctToolsVersion() {
		return toolchainRVCT.getVersion();
	}

	/**
	 * Checks that command-line tools needed by AppDep Core are available in order
	 * to run cache generation. Currently required tools include petran.exe for GCC toolchain
	 * and elftran.exe for other toolchains.
	 * @param sdkInfo SDK information object.
	 * @param targetPlatformID Target platform id used to get matching toolchain setting.
	 * @return <code>true</code> if all required tools are available, otherwise <code>false</code>.
	 * @throws InvalidCmdLineToolSettingException 
	 */
	public boolean areToolsRequiredByCoreAvailable(SdkInformation sdkInfo, String targetPlatformID) throws InvalidCmdLineToolSettingException {
		IToolchain[] supportedToolchainsForGivenTargetPlatformId = getSupportedToolchainsForGivenTargetPlatformId(targetPlatformID);		
		if(supportedToolchainsForGivenTargetPlatformId[0].getToolchainName().equals(STR_GCC)){
			return isPetranAvailable(sdkInfo);
		}
		return isElftranAvailable(sdkInfo);
	}
	
	/**
	 * Gets currently used binary dump tool name based on the currently
	 * used toolchain setting.
	 * @param targetPlatformID Target platform id used to get matching toolchain setting.
	 * @return STR_PETRAN_EXE if GCC toolchain is selected, otherwise STR_ELFTRAN_EXE.
	 * @throws InvalidCmdLineToolSettingException 
	 */
	public String getCurrentlyUsedCoreDumpToolName(String targetPlatformID) throws InvalidCmdLineToolSettingException{
		IToolchain[] supportedToolchainsForGivenTargetPlatformId = getSupportedToolchainsForGivenTargetPlatformId(targetPlatformID);
		if(supportedToolchainsForGivenTargetPlatformId[0].getToolchainName().equals(STR_GCC)){
			return STR_PETRAN_EXE;
		}
		return STR_ELFTRAN_EXE;		
	}
}
