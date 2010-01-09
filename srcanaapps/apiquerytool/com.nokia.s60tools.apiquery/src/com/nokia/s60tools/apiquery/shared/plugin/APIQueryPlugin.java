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
 
 
package com.nokia.s60tools.apiquery.shared.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.nokia.s60tools.apiquery.settings.IUserSettingsListener;
import com.nokia.s60tools.apiquery.settings.UserSettings;
import com.nokia.s60tools.apiquery.shared.common.ProductInfoRegistry;
import com.nokia.s60tools.apiquery.shared.resources.ImageKeys;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtension;
import com.nokia.s60tools.apiquery.shared.searchmethod.ISearchMethodExtensionInfo;
import com.nokia.s60tools.apiquery.shared.searchmethodregistry.SearchMethodExtensionRegistry;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.dialogs.FirstTimePopUpDialog;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * The main plugin class to be used in the desktop.
 */
public class APIQueryPlugin extends AbstractUIPlugin implements IUserSettingsListener {


	/**
	 * API Query plugin ID
	 */
	public static final String PLUGIN_ID ="com.nokia.s60tools.apiquery";//$NON-NLS-1$

	/**
	 * Extension point declared by this plugin the extender
	 * plugins can use for adding new search methods.
	 */
	private static final String SEARCH_METHOD_EXTENSION_POINT_ID = "com.nokia.s60tools.apiquery.searchMethods"; //$NON-NLS-1$
	
	/**
	 * Attribute for the extension point that store the name of the class
	 * that implements a search method extension. 
	 */
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	/**
	 * Default name for file to store user-configured server entries.  
	 */
	private static final String DATA_SOURCES_PROPERTIES_FILE_NAME = "datasources.properties"; //$NON-NLS-1$
	
	/**
	 * Property name for getting selected data source
	 */
	private static final String DATA_SOURCES_PROPERTIES_SELECTED_DATA_SOURCE = "selected_datasource"; //$NON-NLS-1$
	
	/**
	 * Absolute path name for the plug-in specific configuration file.
	 */
	private static String configFilePath;
	
	/**
	 * Shared plugin instance.
	 */
	private static APIQueryPlugin plugin;
	
	/**
	 * Plugin installation location.
	 */
	private static String pluginInstallLocation;
	
	/**
	 * Data Source registry for storing the currently
	 * installed search methods. The registry must be
	 * alive as long as the plugin class is alive.
	 */
	private static SearchMethodExtensionRegistry smeRegistry = SearchMethodExtensionRegistry.getInstance();
	
	/**
	 * User settings instance. The settings instance must be
	 * alive as long as the plugin class is alive.
	 */
	private static UserSettings settings = UserSettings.getInstance();
	
	/**
	 * Storing preferences
	 */
	private static IPreferenceStore prefsStore;
	
	static public boolean isFirstLaunch = false;
	
	/**
	 * The constructor.
	 */
	public APIQueryPlugin() {
		System.out.println("Plugin start");
		isFirstLaunch = true;
		plugin = this;
		DbgUtility.println(DbgUtility.PRIORITY_CLASS, "-- <<create>> --> " + getClass().getName()); //$NON-NLS-1$
	}

	/**
	 * 
	 * @return plugin install path
	 * @throws IOException
	 */
	public String getPluginInstallPath() throws IOException{
		 // URL to the plugin's root ("/")
		URL relativeURL = getBundle().getEntry("/"); //$NON-NLS-1$
		//	Converting into local path
		URL localURL = FileLocator.toFileURL(relativeURL);
		//	Getting install location in correct form
		File f = new File(localURL.getPath());
		String pluginInstallLocation = f.getAbsolutePath();
				
		return pluginInstallLocation;		
	}
	
	/**
	 * Gets images path relative to given plugin install path.
	 * @return Path were image resources are located.
	 */
	private String getImagesPath(){
		return pluginInstallLocation
				+ File.separatorChar
				+ ProductInfoRegistry.getImagesDirectoryName();
	}
	

	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//This startup debug println has been left into the code in purpose
		System.out.println("APIQuery Plugin STARTUP..."); //$NON-NLS-1$
				
		pluginInstallLocation = getPluginInstallPath();
		

		//This startup debug println has been left into the code in purpose
		System.out.println("pluginInstallLocation: " +  pluginInstallLocation); //$NON-NLS-1$

		// Call to getImagesPath requires that getPluginInstallPath() is
		// called beforehand.
		String imagesPath = getImagesPath();
		
		//This startup debug println has been left into the code in purpose
		System.out.println("imagesPath: " +  imagesPath); //$NON-NLS-1$
				
		// Seeking for the plugins that extend the SEARCH_METHOD_EXTENSION_POINT_ID
		fetchExistingSearchMethodExtensionInstances();

		// User settings initialization has to be done after fetching search methods.
		initializeUserSettings();	
	
		
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
	 * Loading the previously stored user settings if any.
	 */
	private void initializeUserSettings() {
		// In here will be implemented the load part of the functionality.
		
			try {
				String wsPath = getPluginWorkspacePath();
				configFilePath = wsPath + File.separator + DATA_SOURCES_PROPERTIES_FILE_NAME;
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Data Source's config file: " + configFilePath); //$NON-NLS-1$
				File configFile = new File(configFilePath);
				//If User setting has not been chaged, there is no configuration file
				if(configFile.exists()){
					Properties props = new Properties();
					props.load( new FileInputStream(configFile));
					String currentDataSource = props.getProperty(DATA_SOURCES_PROPERTIES_SELECTED_DATA_SOURCE);
					DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "Currently selected data source is: " +currentDataSource); //$NON-NLS-1$
					
					//Getting search extension by id
					ISearchMethodExtension ext = smeRegistry.getById(currentDataSource);
					//If no errors exist, saving currently selected Data Source to Runtime from propertie file.
					if(ext != null){
						ISearchMethodExtensionInfo info = ext.getExtensionInfo();
						UserSettings.getInstance().setCurrentlySelectedSearchMethodInfo(info);
					}
					
				}
				//If there is no configuration file, we know that this is the first time
				//user is starting application (at least in this work space, if user is cleaned workspace
				//or created a new works space he/she might been started application before, but we can't know that).
				else{
					//Create a config file by calling userSettingsChanged()
					userSettingsChanged();
					//Launching first time popup (quick help)
					FirstTimePopUpDialog.open(getCurrentlyActiveWbWindowShell());
					
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
	                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
	                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			}catch (Exception e) {
				e.printStackTrace();
				APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
	                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			}
	}



	/**
	 * Seeking for the plugins that extend the SEARCH_METHOD_EXTENSION_POINT_ID
	 * and registering them to the <code>SearchMethodExtensionRegistry</code>.
	 */
	private void fetchExistingSearchMethodExtensionInstances() {
		
		try {
			
			System.out.println("Fetching for search method extensions..."); //$NON-NLS-1$
			// Searching for search method extensions
			IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
			IExtensionPoint extPoint = extensionReg
					.getExtensionPoint(SEARCH_METHOD_EXTENSION_POINT_ID);
			IExtension[] searchMethods = extPoint.getExtensions();
			for (int i = 0; i < searchMethods.length; i++) {
				IExtension ext = searchMethods[i];
				IConfigurationElement[] configElements = ext
						.getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					IConfigurationElement configElem = configElements[j];
					
					String searchMethodClassFullName = configElem.getAttribute(CLASS_ATTRIBUTE);
					
					try {
						// Creating search method extension intance
						Object classInstance = configElem
								.createExecutableExtension(CLASS_ATTRIBUTE);

						ISearchMethodExtension searchMethod = (ISearchMethodExtension) classInstance;

						System.out.println("Found search method: " //$NON-NLS-1$
								+ searchMethod.getClass().getName());
						
						ISearchMethodExtensionInfo info = searchMethod.getExtensionInfo();

						System.out.println("\t id: " + info.getId()); //$NON-NLS-1$
						System.out.println("\t description: " //$NON-NLS-1$
								+ info.getDescription());
						
						boolean isDefault =  info.isDefault();
						System.out.println("\t isDefault: " //$NON-NLS-1$
								+ isDefault);

						// Adding the extension to local extension registry
						smeRegistry.addExtension(searchMethod);
					} catch (Exception e) {
						APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Search_Method_Instantiate_Failed_Msg_Start")  //$NON-NLS-1$
								+  searchMethodClassFullName
								+ Messages.getString("APIQueryPlugin.Search_Method_Instantiate_Failed_Msg_End") //$NON-NLS-1$
	                              + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Search_Method_Instantiate_Failed_Console_Msg") //$NON-NLS-1$
                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			e.printStackTrace();
		}
		
		if(!(smeRegistry.getExtensionCount() > 0)){
			String errMsg = Messages.getString("APIQueryPlugin.No_Search_Method_Installed_ErrorMsg_Start")  //$NON-NLS-1$
				            + ProductInfoRegistry.getProductName() + Messages.getString("APIQueryPlugin.No_Search_Method_Installed_ErrorMsg_End"); //$NON-NLS-1$
			APIQueryConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
			new APIQueryMessageBox(errMsg, SWT.ICON_ERROR | SWT.OK).open();
			throw new RuntimeException(errMsg);
		}
		
		// Finding out the search method that is selected initally
		setCurrentlySelectedSearchMethod(smeRegistry.getExtensions());
		
		
		// Listening further changes in the user settings.
		settings.addUserSettingListener(this);
	}

	/**
	 * Sets the default search method as the currently selected search method
	 * if there is no previous user preference.
	 * @param searchMethodExtensions
	 */
	private void setCurrentlySelectedSearchMethod(Collection<ISearchMethodExtension> searchMethodExtensions) {

		// Querying for the current user preference
		ISearchMethodExtensionInfo userSelectedExtensionInfo = settings.getCurrentlySelectedSearchMethodInfo();
		
		// The result of the decision process is stored into the following variable 
		ISearchMethodExtensionInfo selectedExtInfo = null;
		
		if(userSelectedExtensionInfo == null){
			
			// There were no previous user settings existing
			
			// If there is no existing user preference, we select
			// among the available search methods.
			//
			// Current decision algorithm is to select the first one
			// from the list that has default status. If no search
			// methods with default status were not found settings,
			// the last search method found is set as default.

			for (ISearchMethodExtension ext : searchMethodExtensions) {
				selectedExtInfo = ext.getExtensionInfo();
				
				if(selectedExtInfo.isDefault()){
					break;
				}
			}
		}
		else{
			
			// There was a previous user preference
			
			// Browsing through the extension list and setting as default
			// the search that has the id equal with the user preference. 
			//
			// If no search methods that match with the user preference			
			// the last search method found is set as default.
			
			for (ISearchMethodExtension ext : searchMethodExtensions) {
				selectedExtInfo = ext.getExtensionInfo();
				
				if(selectedExtInfo.hasEqualId(settings.getCurrentlySelectedSearchMethodInfo())){
					break;
				}
			}
			
		}
		
		settings.setCurrentlySelectedSearchMethodInfo(selectedExtInfo);		
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		smeRegistry.notifyPluginShutdown();
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static APIQueryPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.nokia.s60tools.apiquery.plugin", path); //$NON-NLS-1$
	}
	
	/**
	 * This must be called from UI thread. If called
	 * from non-ui thread this returns <code>null</code>.
	 * @return Currently active workbench page.
	 */
	public static IWorkbenchPage getCurrentlyActivePage(){
		return getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * This must be called from UI thread. If called
	 * from non-ui thread this returns <code>null</code>.
	 * @return The shell of the currently active workbench window..
	 */
	public static Shell getCurrentlyActiveWbWindowShell(){
		IWorkbenchPage page = getCurrentlyActivePage();
		if(page != null){
			return page.getWorkbenchWindow().getShell();
		}
		return null;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry imgReg) {
    	
    	//
    	// Storing images to plugin's image registry
    	//
    	Display disp = Display.getCurrent();
    	Image img = null;
    	
    	img = new Image( disp, getImagesPath() + "\\apiquery_tsk.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.IMG_APP_ICON, img );
    	
    	img = new Image( disp, getImagesPath() + "\\apiquery_wiz.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.IMG_APP_WIZARD_ICON, img );
    	
    }
    
	/**
	 * Returns image descriptor for the given key from the plugin's image registry.
	 * @param key Key to search descriptor for.
	 * @return Image descriptor for the given key from the plugin's image registry.
	 */
	public static ImageDescriptor getImageDescriptorForKey( String key ){
    	ImageRegistry imgReg = getDefault().getImageRegistry();
    	return  imgReg.getDescriptor( key );		
	}	

	/**
	 * Returns image for the given key from the plugin's image registry.
	 * @param key Key to search image for.
	 * @return Image for the given key from the plugin's image registry.
	 */
	public static Image getImageForKey( String key ){
    	ImageRegistry imgReg = getDefault().getImageRegistry();    	
    	return  imgReg.get(key);		
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.apiquery.settings.IUserSettingsListener#userSettingsChanged()
	 * Saves selected Data Source to configuration file.
	 */
	public void userSettingsChanged() {
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, getClass().getName() + ": User settings has been changed!"); //$NON-NLS-1$

		//Saving selected Data Source Id to .properties file
		try {
			File configFile = new File(configFilePath);
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "API Query loading Data Source's configuration");				 //$NON-NLS-1$
			Properties props = new Properties();
			String value = UserSettings.getInstance().getCurrentlySelectedSearchMethodInfo().getId();
			props.put(DATA_SOURCES_PROPERTIES_SELECTED_DATA_SOURCE, value);
			props.store(new FileOutputStream(configFile), Messages.getString("APIQueryPlugin.DefaultDataSourceSelByUser_Msg") ); //$NON-NLS-1$
		} catch (FileNotFoundException e) {
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			e.printStackTrace();
		} catch (IOException e) {
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			e.printStackTrace();
		}catch (Exception e) {
			APIQueryConsole.getInstance().println(Messages.getString("APIQueryPlugin.Configuration_file_ErrMsg") //$NON-NLS-1$
                    + e.getMessage() , IConsolePrintUtility.MSG_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * Returns the PreferenceStore where plugin preferences are stored
	 * 
	 * @return the PreferenceStore where plugin preferences are stored
	 */
	public static IPreferenceStore getPrefsStore() {
		if (prefsStore == null){
			prefsStore = getDefault().getPreferenceStore();
		}
		
		return prefsStore;
	}

}
