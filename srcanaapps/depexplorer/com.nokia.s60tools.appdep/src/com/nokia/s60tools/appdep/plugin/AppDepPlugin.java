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
 
 
package com.nokia.s60tools.appdep.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.AppDepCacheIndexManager;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.job.AppDepJobManager;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.sdk.RVCTToolChainInfo;
import com.nokia.s60tools.sdk.SdkEnvInfomationResolveFailureException;
import com.nokia.s60tools.sdk.SdkManager;
import com.nokia.s60tools.util.console.IConsolePrintUtility;

/**
 * The main plugin class to be used in the desktop.
 */
public class AppDepPlugin extends AbstractUIPlugin {

	/**
	 * The shared instance. 
	 */
	private static AppDepPlugin plugin;
	
	/**
	 * Command line tool settings should not be garbage collected
	 * during the plugin's lifetime. Having a reference in here
	 *  should enforce that singleton class is not garbage collected. 
	 */
	private static AppDepSettings settings;
	
	/**
	 * Also <code>AppDepJobManager</code> instance should not be garbage collected
	 * during the plugin's lifetime. Having a reference in here
	 *  should enforce that singleton class is not garbage collected. 
	 */
	private static AppDepJobManager pluginJobManager;
	
	/**
	 * Also <code>AppDepCacheIndexManager</code> instance should not be garbage collected
	 * during the plugin's lifetime. Having a reference in here
	 * should enforce that singleton class is not garbage collected. 
	 */
	private static AppDepCacheIndexManager cacheIndexManager;
	
	/**
	 * Stores preferences
	 */
	private static IPreferenceStore prefsStore;

	
	/**
	 * The constructor.
	 */
	public AppDepPlugin() {
		plugin = this;
		settings = AppDepSettings.getActiveSettings();
		pluginJobManager = AppDepJobManager.getInstance();		
		cacheIndexManager = AppDepCacheIndexManager.getInstance();		
	}

	/**
	 * Gets plug-in install path.
	 * @return plug-in install path
	 * @throws IOException
	 */
	private String getPluginInstallPath() throws IOException{
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
	 * @param pluginInstallPath Plugin installation path.
	 * @return Path were image resources are located.
	 * @throws IOException
	 */
	private String getImagesPath(String pluginInstallPath) throws IOException{
		return pluginInstallPath
				+ File.separatorChar
				+ ProductInfoRegistry.getImagesDirectoryName();
	}
	
	/**
	 * This method is called upon plug-in activation
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {		
		super.start(context);
		
		//Startup sequence starting message
		//This debug println has been left into the code in purpose
		System.out.println("*** AppDep Plugin STARTUP..."); //$NON-NLS-1$
		
		//Plug-in install location
		String pluginInstallLocation = getPluginInstallPath();
		//This debug println has been left into the code in purpose
		System.out.println("pluginInstallLocation: " +  pluginInstallLocation); //$NON-NLS-1$

		//Images path
		String imagesPath = getImagesPath(pluginInstallLocation);		
		//This debug println has been left into the code in purpose
		System.out.println("imagesPath: " +  imagesPath); //$NON-NLS-1$
		
		// Loading images required by this plug-in
		ImageResourceManager.loadImages(imagesPath);
		
		initializeSettings(pluginInstallLocation);

		// Removing inconsistent partially created 
		// cache files, if such exist
		settings.cleanupPartiallyCreatedCacheFiles();
						
		//Startup sequence completed message
		//This debug println has been left into the code in purpose
		System.out.println("*** AppDep Plugin STARTED."); //$NON-NLS-1$
	}

	/**
	 * Initializing tool settings.
	 * @param pluginInstallPath Plugin installation path.
	 */
	private void initializeSettings(String pluginInstallPath) {

		//
		// Settings gained from SDK Support Plug-in
		//
		
		// RVCT tools
		resolveDefaultRvctToolBinaries();
		//This debug println has been left into the code in purpose
		System.out.println("RVCT Tool Binaries Directory: " + settings.getRvctToolsDir()); //$NON-NLS-1$
		System.out.println("RVCT Tool Version: " + settings.getRvctToolsVersion()); //$NON-NLS-1$

		// GCCE tools
		try {
			settings.setGcceToolsDir(SdkManager.getCSLArmToolchainInstallPathAndCheckReqTools());
			settings.setGcceToolsInstalled( true );
			//This debug println has been left into the code in purpose
			System.out.println("GCCE Tool Binaries Directory: " + settings.getGcceToolsDir()); //$NON-NLS-1$
		} catch (SdkEnvInfomationResolveFailureException e1) {
			e1.printStackTrace();
			settings.setGcceToolsInstalled( false );
			AppDepConsole.getInstance().println(Messages.getString("AppDepPlugin.StartupFail_Msg_GCCE_Install_Location_Not_Found") //$NON-NLS-1$
											+ e1.getMessage(),
											IConsolePrintUtility.MSG_ERROR);    				
		}

		// Checking that at least GCCE or RVCT tools are installed.
		// If not installed, showing an error message to user and aborting 
		// plugin start by throwing an exception		
		boolean isRequiredToolsInstalled = (settings.isGcceToolsInstalled() 
										  || settings.isRvctToolsInstalled());
		
		if(! isRequiredToolsInstalled ){
			String errorMsg = Messages.getString("AppDepPlugin.StartupFail_Msg_Could_Not_Detect_Required_Toolchains") //$NON-NLS-1$
							  + Messages.getString("AppDepPlugin.StartupFail_Msg_Required_Tools_Tip") //$NON-NLS-1$
							  + Messages.getString("AppDepPlugin.StartupFail_Msg_Install_Required_Tools_Tip_Cont") //$NON-NLS-1$
							  + "\n\n" //$NON-NLS-1$
							  + Messages.getString("AppDepPlugin.StartupFail_Msg_Timeout_Network_Check"); //$NON-NLS-1$
			Shell sh = this.getWorkbench().getDisplay().getActiveShell();

			String[] dlgButtonArr = { Messages.getString("AppDepPlugin.OK_Button_Label") }; //$NON-NLS-1$
			MessageDialog dlg = new MessageDialog(sh,
												  ProductInfoRegistry.getProductName(),
												  ImageResourceManager.getImage(ImageKeys.IMG_APP_ICON),
												  errorMsg,
												  MessageDialog.ERROR,
												  dlgButtonArr,
												  0
												  );
			dlg.create();
			dlg.open();
			throw new RuntimeException(errorMsg);
		}		
		
		//
		// Setting location for external binaries
		//					
		String externalProgramsPath = pluginInstallPath
										+ File.separatorChar
										+ ProductInfoRegistry.getWin32BinariesRelativePath();
		//External programs are to be found from this path
		//This debug println has been left into the code in purpose
		System.out.println("externalProgramsPath: " + externalProgramsPath); //$NON-NLS-1$
		settings.setExternalProgramsPathName(externalProgramsPath);		
		
		// Resources path
		String resourcesPath = pluginInstallPath 
			+ File.separatorChar 
			+ ProductInfoRegistry.getAppDepResourcesDirectory();
		//This debug println has been left into the code in purpose
		System.out.println("resourcesPath: " + resourcesPath); //$NON-NLS-1$
		settings.setResourcesPath(resourcesPath);
		
		// Resolving XSL filename
		String xslFileName =
			 ProductInfoRegistry.getAppDepExportXSLFileName();
		settings.setXSLFileName(xslFileName);
		String isUsedByXSLFileName =
			 ProductInfoRegistry.getAppDepIsUsedByXSLFileName();
		settings.setIsUsedByXSLFileName(isUsedByXSLFileName);				
	}

	/**
	 * Resolves which RVCT Tool Binaries should be used by default. 
	 * The selection of the used RVCT toolchain is made based on 
	 * the following criteria:
	 *  - If there is several possibilities. 
	 *    Using the binaries with newest timestamp. 
	 *  - If only one possibility was available, using it. If none reporting 
	 *    it to Console output.
	 */
	private void resolveDefaultRvctToolBinaries() {
		
		//Get all toolchains
		RVCTToolChainInfo[] rvctTools = SdkManager.getInstalledRVCTTools();		
		
		if(rvctTools.length > 1){

			// Environment variable is not defined or
			// there was no match => Using the newest binaries.
			useTheNewestAvailableRvctToolbinaries(rvctTools);							
		}
		else if(rvctTools.length == 1){
			// There is only one possibility
			useTheFirstAvailableToolchain(rvctTools);
		}
		else{
			settings.setRvctToolsInstalled( false );

			AppDepConsole.getInstance().println(Messages.getString("AppDepPlugin.StartupFail_Msg_RVCT_Install_Location_Not_Found"), //$NON-NLS-1$
					 IConsolePrintUtility.MSG_ERROR);   
		}
	}

	/**
	 * Sets first encountered toolchain as used toolchain.
	 * @param rvctTools RVCT toolchain info object array.
	 */
	private void useTheFirstAvailableToolchain(RVCTToolChainInfo[] rvctTools) {
		String rvctToolBinariesDirectory = rvctTools[0].getRvctToolBinariesDirectory();
		settings.setRvctToolsDir(rvctToolBinariesDirectory);
		settings.setRvctToolsVersion(rvctTools[0].getRvctToolsVersion()); 
		settings.setRvctToolsInstalled( true );
	}

	/**
	 * Called when there is more than one available toolchain binaries.
	 * Resolves which one is the newest, and sets it as default.
	 * @param rvctTools Array to RVCT tool info structure.
	 */
	private void useTheNewestAvailableRvctToolbinaries(RVCTToolChainInfo[] rvctTools) {
		String binDirStored = null;
		String versionInfoStored = null;
		long lastModifiedStored = 0;
		for (int i = 0; i < rvctTools.length; i++) {
			String binDirTmp = rvctTools[i].getRvctToolBinariesDirectory();
			String elfProg = binDirTmp
							 + File.separatorChar 
							 + AppDepSettings.RVCT_FROM_ELF_EXECUTABLE;
			File elfFile = new File(elfProg);
			if(elfFile.exists()){
				long modified = elfFile.lastModified();
				if(modified > lastModifiedStored){
					lastModifiedStored = modified;
					binDirStored = binDirTmp;
					versionInfoStored = rvctTools[i].getRvctToolsVersion();
				}
			}
		}
		if(binDirStored != null){
			settings.setRvctToolsDir(binDirStored);
			settings.setRvctToolsVersion(versionInfoStored); 
			settings.setRvctToolsInstalled( true );			
		}		
		else{
			useTheFirstAvailableToolchain(rvctTools);										
		}
	}
	
	/**
	 * This method is called when the plug-in is stopped
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		//Starting plugin shutdown sequence
		//This debug println has been left into the code in purpose
		System.out.println("AppDep Plugin SHUTDOWN..."); //$NON-NLS-1$
		
		// Shutting down registered ongoing background jobs
		pluginJobManager.shutdown();
		
		// Informing also cache index manager about the shutdown
		cacheIndexManager.shutdown();
		
		// Removing inconsistent partially created 
		// cache files, if such exist
		settings.cleanupPartiallyCreatedCacheFiles();
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static AppDepPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.nokia.s60tools.appdep.plugin", path); //$NON-NLS-1$
	}
	
	/**
	 * This must be called from UI thread. If called
	 * from non-UI thread this returns <code>null</code>.
	 * @return Currently active workbench page.
	 */
	public static IWorkbenchPage getCurrentlyActivePage(){
		return getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * This must be called from UI thread. If called
	 * from non-UI thread this returns <code>null</code>.
	 * @return The shell of the currently active workbench window..
	 */
	public static Shell getCurrentlyActiveWbWindowShell(){
		IWorkbenchPage page = getCurrentlyActivePage();
		if(page != null){
			return page.getWorkbenchWindow().getShell();
		}
		return null;
	}
	
	/**
	 * Returns the PreferenceStore where plugin preferences are stored
	 * @return the PreferenceStore where plugin preferences are stored
	 */
	public static IPreferenceStore getPrefsStore(){
		if (prefsStore == null){
			prefsStore = getDefault().getPreferenceStore();
		}
		
		return prefsStore;
	}	

	/**
	 * Bounds given context sensitive help ID into given UI control.
	 * @param composite UI control to which bind given context-sensitive help ID.
	 * @param contextHelpID Context-sensitive help ID.
	 */
	public static void setContextSensitiveHelpID(
			Control composite, String contextHelpID) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, contextHelpID);
	}

}
