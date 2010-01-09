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
 
 
package com.nokia.s60tools.appdep.common;

/**
 * This class stores product information such as product name, 
 * version, console view name etc.  
 * The idea is to have the product information defined
 * in one place and used via single access point.
 */
public class ProductInfoRegistry {

	//
	// Product info fields get their data from properties file.
	//
	private static final String PRODUCT_NAME = Product.getString("ProductInfoRegistry.Product_Name"); //$NON-NLS-1$
	private static final String CONSOLE_WINDOW_NAME = PRODUCT_NAME + " " + Product.getString("ProductInfoRegistry.Console_Window_Name");	 //$NON-NLS-1$ //$NON-NLS-2$
	private static final String CACHE_FILE_NAME = Product.getString("ProductInfoRegistry.Cache_File_Name"); //$NON-NLS-1$
	private static final String CACHE_TEMP_FILE_NAME = Product.getString("ProductInfoRegistry.Cache_Temp_File_Name"); //$NON-NLS-1$
	private static final String CACHE_SYMBOLS_FILE_NAME = Product.getString("ProductInfoRegistry.Cache_Symbols_File_Name"); //$NON-NLS-1$
	private static final String IMAGES_DIRECTORY = Product.getString("ProductInfoRegistry.Images_Directory");	 //$NON-NLS-1$
	private static final String BINARIES_RELATIVE_PATH = Product.getString("ProductInfoRegistry.Binaries_Relative_Path");	 //$NON-NLS-1$
	private static final String CFILT_WIN32OS_BINARY_NAME = Product.getString("ProductInfoRegistry.CFilt_Win32OS_Binary_Name"); //$NON-NLS-1$
	private static final String APPDEP_WIN32OS_BINARY_NAME=Product.getString("ProductInfoRegistry.Core_Win32OS_Binary_Name"); //$NON-NLS-1$
	private static final String APPDEP_RESOURCES_DIRECTORY = Product.getString("ProductInfoRegistry.Resources_Directory"); //$NON-NLS-1$
	private static final String APPDEP_EXPORT_XSL_FILE_NAME = Product.getString("ProductInfoRegistry.Export_Report_Xsl_File_Name"); //$NON-NLS-1$
	private static final String APPDEP_IS_USED_BY_XSL_FILE_NAME = Product.getString("ProductInfoRegistry.Export_Report_IsUsedBy_Xsl_File_Name"); //$NON-NLS-1$
	private static final String STR_S60_RND_TOOLS_DIR = Product.getString("ProductInfoRegistry.S60_RnD_Tools_Dir"); //$NON-NLS-1$
	private static final String STR_APPDEP_CACHE_DIR = Product.getString("ProductInfoRegistry.AppDepCacheDir"); //$NON-NLS-1$	
	private static final String DUMPSIS_EXE_FILE_NAME = Product.getString("ProductInfoRegistry.dumbsis.exe_File_Name");//$NON-NLS-1$ 
	
	/**
	 * Currently used and supported cache version info.
	 */
	private static final String STR_SUPPORTED_CACHE_VERSION_INFO = "101";  //$NON-NLS-1$
	
	/**
	 * Start of the first line of cache symbol tables file
	 */
	private static final String STR_CACHE_SYMBOL_TABLES_FILE_CONTENT_PREFIX = "appdep symbol tables cache version:"; //$NON-NLS-1$
	
	/**
	 * Returns name used for the console window
	 * @return Returns the CONSOLE_WINDOW_NAME.
	 */
	public static String getConsoleWindowName() {
		return CONSOLE_WINDOW_NAME;
	}
	/**
	 * Returns product name.
	 * @return Returns the PRODUCT_NAME.
	 */
	public static String getProductName() {
		return PRODUCT_NAME;
	}
	/**
	 * Returns cache file name.
	 * @return Returns the CACHE_FILE_NAME.
	 */
	public static String getCacheFileName() {
		return CACHE_FILE_NAME;
	}
	
	/**
	 * Returns image for storing images.
	 * @return Returns the IMAGES_DIRECTORY.
	 */
	public static String getImagesDirectoryName() {
		return IMAGES_DIRECTORY;
	}
	/**
	 * Returns temporary file name used for cache creation.
	 * @return Returns the CACHE_TEMP_FILE_NAME.
	 */
	public static String getCacheTempFileName() {
		return CACHE_TEMP_FILE_NAME;
	}
	/**
	 * Returns relative path to directory used to store os-specific binaries.
	 * @return Returns the BINARIES_RELATIVE_PATH.
	 */
	public static String getWin32BinariesRelativePath() {
		return BINARIES_RELATIVE_PATH;
	}
	/**
	 * Returns name of the cfilt binary.
	 * @return Returns the CFILT_WIN32OS_BINARY_NAME.
	 */
	public static String getCfiltBinaryName() {
		return CFILT_WIN32OS_BINARY_NAME;
	}
	/**
	 * Returns name of the appdep core binary.
	 * @return Returns the APPDEP_WIN32OS_BINARY_NAME.
	 */
	public static String getAppDepBinaryName() {
		return APPDEP_WIN32OS_BINARY_NAME;
	}
	/**
	 * @return Returns the CACHE_SYMBOLS_FILE_NAME.
	 */
	public static String getCacheSymbolsFileName() {
		return CACHE_SYMBOLS_FILE_NAME;
	}
	/**
	 * Returns name of the directory used to store resources.
	 * @return Returns the APPDEP_RESOURCES_DIRECTORY.
	 */
	public static String getAppDepResourcesDirectory() {
		return APPDEP_RESOURCES_DIRECTORY;
	}
	/**
	 * Returns file used for XSL transformation in export report functionality 
	 * when applied to component tree,
	 * @return Returns the APPDEP_EXPORT_XSL_FILE_NAME.
	 */
	public static String getAppDepExportXSLFileName() {
		return APPDEP_EXPORT_XSL_FILE_NAME;
	}
	
	/**
	 * Returns file used for XSL transformation in export report functionality 
	 * when applied to component list view.
	 * @return Returns the APPDEP_IS_USED_BY_XSL_FILE_NAME
	 */
	public static String getAppDepIsUsedByXSLFileName() {
		return APPDEP_IS_USED_BY_XSL_FILE_NAME;
	}	
	
	/**
	 * Returns relative path to the cache directory.
	 * @return Returns STR_APPDEP_CACHE_DIR
	 */
	public static String getAppDepCacheDir() {
		return STR_APPDEP_CACHE_DIR;
	}

	/**
	 * Returns name of the S60 RnD tools directory under which cache data is stored.
	 * @return Returns STR_S60_RND_TOOLS_DIR
	 */
	public static String getS60RndToolsDir() {
		return STR_S60_RND_TOOLS_DIR;
	}
	/**
	 * Return cache file version we currently support.
	 * @return Cache file version we currently support.
	 */
	public static String getSupportedCacheFileVersionInfoString() {
		return STR_SUPPORTED_CACHE_VERSION_INFO;
	}	

	/**
	 * Get dumpsis.exe file name
	 * @return "dumbsis.exe"
	 */
	public static String getDumpsisExeFileName() {
		return DUMPSIS_EXE_FILE_NAME;
	}		

	/**
	 * Get prefix contents for file appdep-cache_symbol_tables.txt
	 * @return prefix contents for appdep-cache_symbol_tables.txt file
	 */
	public static String getCacheSymbolTablesFileContentPrefix() {
		return STR_CACHE_SYMBOL_TABLES_FILE_CONTENT_PREFIX;
	}		
	
}
