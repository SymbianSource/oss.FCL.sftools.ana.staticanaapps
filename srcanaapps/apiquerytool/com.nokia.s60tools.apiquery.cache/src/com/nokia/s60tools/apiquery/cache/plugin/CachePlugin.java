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
 
package com.nokia.s60tools.apiquery.cache.plugin;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.nokia.s60tools.apiquery.cache.configuration.CacheEntryStorage;
import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.util.debug.DbgUtility;


/**
 * The extension plugin class for API Query Metadata information from the SDK Plug-in.
 * This plug-in adds a data source to API Query Plug-in. 
 */
public class CachePlugin extends AbstractUIPlugin implements IConfigurationChangedListener {

	//The shared instance.
	private static CachePlugin plugin;
	
	/**
	 * Instantiating if sheet entry storage singleton class, and therefore
	 * making sure that it is available during the existence of the whole 
	 * plug-in's lifetime.
	 */
	private static CacheEntryStorage entryStorage = CacheEntryStorage.getInstance();
	
	/**
	 * Absolute path name for the plug-in specific configuration file.
	 */
	private static String configFilePath;
	
	/**
	 * Entrys file name
	 */
	private static final String CACHE_ENTRY_CONFIG_FILE_DEFAULT_NAME = "metadata_cache_entries.xml";//$NON-NLS-1$
	
	
	/**
	 * The constructor.
	 */
	public CachePlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// Starting to listen for configuration change events
		entryStorage.addConfigurationChangeListener(this);
		// Loading server information stored by the user
		String wsPath = getPluginWorkspacePath();
		configFilePath = wsPath + File.separator + CACHE_ENTRY_CONFIG_FILE_DEFAULT_NAME;
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Metadata information from the SDK method's config file: " + configFilePath);//$NON-NLS-1$
		File configFile = new File(configFilePath);
		if(configFile.exists()){
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Metadata information from the SDK method/loading configuration");//$NON-NLS-1$
			entryStorage.load(configFilePath);
		}		
		else{
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Metadata information from the SDK configuration file not found: " +configFilePath);//$NON-NLS-1$
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CachePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.nokia.s60tools.apiquery.cache.plugin", path); //$NON-NLS-1$
	}
	/**
	 * Returns runtime workspace directory path for the plugin, which
	 * can be used e.g. to store plugin/workspace-specific configuration data.
	 * 
	 * @return Workspace directory path for the plugin.
	 */
	public static String getPluginWorkspacePath(){		
		IPath runtimeWorkspacePath = plugin.getStateLocation();
		runtimeWorkspacePath.makeAbsolute();		
		return runtimeWorkspacePath.toOSString();
	}

	/**
	 * Saves the configuration for an external file.
	 * @throws IOException
	 */
	private static void saveConfiguration() throws IOException{
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Metadata information from the SDK/Storing configuration...");//$NON-NLS-1$
		entryStorage.save(configFilePath);
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Metadata information from the SDK/Configuration stored!");//$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.shared.datatypes.config.IConfigurationChangedListener#configurationChanged(int)
	 */
	public void configurationChanged(int eventType) {
		try {
			saveConfiguration();
		} catch (IOException e) {
			String errMsg = Messages.getString("CacheEntry.Save_Configuration_ErrMsg")  //$NON-NLS-1$
	            + " ( " + e.getMessage() + ").";//$NON-NLS-1$ //$NON-NLS-2$
			APIQueryMessageBox msgBox = new APIQueryMessageBox(errMsg, SWT.OK | SWT.ICON_ERROR);
			msgBox.open();		
		}		
	}

public static String getconfigFilePath()
{
	return configFilePath;
}

}