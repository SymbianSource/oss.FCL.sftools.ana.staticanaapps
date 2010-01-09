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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.core.runtime.jobs.Job;

import com.nokia.s60tools.appdep.common.ProductInfoRegistry;
import com.nokia.s60tools.appdep.core.data.CacheDataConstants;
import com.nokia.s60tools.appdep.exceptions.InvalidCmdLineToolSettingException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.cmdline.CmdLineCommandExecutorFactory;
import com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutor;
import com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver;
import com.nokia.s60tools.util.cmdline.ICustomLineReader;
import com.nokia.s60tools.util.cmdline.UnsupportedOSException;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.resource.FileUtils;


/**
 * The purpose of this class is offer as a facade for
 * the appdep core services (command line tool).
 */
public class AppDepCoreFacade implements ICmdLineCommandExecutorObserver {

	private String options = null;
	private String commands = null;
	
    private ICmdLineCommandExecutor cmdLineExecutor = null;
    private AppDepSettings settings = null;
    private ICmdLineCommandExecutorObserver observer = null;
    private Job currentJobContext = null;
    
    /**
     * Separator character used between targets platforms when generating
     * cache for multiple targets in single command.
     */
    private static final String TARGET_SEPARATOR = "+"; //$NON-NLS-1$
    
    /**
     * folder where SIS cache files is located under cache base dir 
     */
	private static final String SIS_PATH = "sis";//$NON-NLS-1$
    
    /**
     * Stroring list of targets that is currentlu selected 
     */
    private ITargetPlatform[] currentlySelectedTargets = null;

    /**
     * This flag is <code>true</code> when the settings should to be
	 * checked from cache generation options instead from default settings.
     */
    private boolean checkFromCacheGenerOpts = false;
            
	public AppDepCoreFacade(AppDepSettings settings,
							ICmdLineCommandExecutorObserver observer
							) throws UnsupportedOSException{		
        
		// Storing the client observer
		this.observer = observer;
		// And passing ourselves as real observers
		cmdLineExecutor = CmdLineCommandExecutorFactory.CreateOsDependentCommandLineExecutor(this, AppDepConsole.getInstance());
        this.settings = settings;

        options = new String(""); //$NON-NLS-1$
        commands = new String(""); //$NON-NLS-1$
	}
	
	/**
	 * Constructs command line tool parameters based on the current settings and given arguments. 
	 * @param targetPlatformIdentifiers Id of the used target platform(s) separated 
	 *                                  with TARGET_SEPARATOR character.
	 * @return Parameters for the commoand.
	 * @throws InvalidCmdLineToolSettingException
	 */
	private String prepareParameters(String targetPlatformIdentifiers) throws InvalidCmdLineToolSettingException{

		CacheGenerationOptions cacheGenerOpts = settings.getCacheGenerOptions();
		String toolchainNameStr = null;	
		String parameters = new String(""); //$NON-NLS-1$

		if(checkFromCacheGenerOpts && cacheGenerOptionsAreSet()){
			toolchainNameStr = cacheGenerOpts.getUsedToolchain().getToolchainName();
		}
		else{
			toolchainNameStr = settings.getCurrentlyUsedToolChain().getToolchainName();			
		}
		
		if( toolchainNameStr.compareToIgnoreCase( "RVCT" ) == 0 ){ //$NON-NLS-1$
			
			// An example RVCT command line
	        //  set CMD_LINE=appdep RVCT -cfilt %CFILT_DIR%\cfilt.exe 
	        //  -tools %RVCT_TOOLS% -cache %CACHE_DIR% -release %SDK_ROOT% 
	        //  -targets ARMV5 %OPT% %COMMAND% %COMPONENT%
			
	        parameters = "-tools " + "\"" + settings.getRvctToolsDir() + "\""  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						 + " -cfilt " + "\"" + settings.getCfiltProgramPathName() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						 + " -cache " + "\"" + settings.getCacheBaseDirForCurrentlyUsedSdk() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						 + " -release " + "\"" + settings.getSdkRootDir() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -targets " + targetPlatformIdentifiers;			 //$NON-NLS-1$
		}
		else if( toolchainNameStr.compareToIgnoreCase( "GCCE" ) == 0 ){ //$NON-NLS-1$

			// An example GCCE command line
			// appdep GCCE -tools %GCCE_TOOLS% -cache %CACHE_DIR% -release %SDK_ROOT% 
			// -targets GCCE %OPT% %COMMAND% %COMPONENT%
			
	        parameters = "-tools " + "\"" + settings.getGcceToolsDir() + "\""  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -cache " + "\"" + settings.getCacheBaseDirForCurrentlyUsedSdk() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -release " + "\"" + settings.getSdkRootDir() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -targets " + targetPlatformIdentifiers;			 //$NON-NLS-1$
		}
		else if( toolchainNameStr.compareToIgnoreCase( "GCC" ) == 0 ){ //$NON-NLS-1$

			// An example GCC command line
			// appdep GCC -tools %GCC_TOOLS% -cache %CACHE_DIR% -release %SDK_ROOT% 
			// -targets THUMB %OPT% %COMMAND% %COMPONENT%
			
	        parameters = "-tools " + "\"" + settings.getGccToolsDir() + "\""  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -cache " + "\"" + settings.getCacheBaseDirForCurrentlyUsedSdk() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -release " + "\"" + settings.getSdkRootDir() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        			 + " -targets " + targetPlatformIdentifiers;			 //$NON-NLS-1$
		}
		else{
			// This is an internal error if we ever get into here
			throw new InvalidCmdLineToolSettingException( 
					Messages.getString("AppDepCoreFacade.UnexpectedException_In_prepareParameters_Method")  //$NON-NLS-1$
					+ ". "  //$NON-NLS-1$
					+ Messages.getString("AppDepCoreFacade.Toolchain_Str")  //$NON-NLS-1$
					+ "'"  //$NON-NLS-1$
					+ toolchainNameStr  
					+ "' "  //$NON-NLS-1$
					+ Messages.getString("AppDepCoreFacade.Is_Not_Supported_Msg_End")  //$NON-NLS-1$
					+ "!" );			 //$NON-NLS-1$
		}
		
		if(settings.getBuildType() instanceof BuildTypeDebug){
			parameters += " --useudeb "; //$NON-NLS-1$
		}
		
		if(checkFromCacheGenerOpts && cacheGenerOptionsAreSet()){
			if(cacheGenerOpts.getUsedLibraryType() == CacheGenerationOptions.USE_LIB_FILES
					&&
			    // This option is unnecessary for GCC toolchain
			   ! toolchainNameStr.equalsIgnoreCase(AppDepSettings.STR_GCC)){
				parameters += " --uselibs ";				 //$NON-NLS-1$
			}
		}

		return parameters;
	}
	
	/**
	 * Guard for accessing cache generation options.
	 * @return <code>true</code> if options can be accessed safely, otherwise <code>false</code>.
	 */
	private boolean cacheGenerOptionsAreSet() {
		return (settings.getCacheGenerOptions() != null);
	}

	public void generateCache(Job jobContext) throws InvalidCmdLineToolSettingException{
		
		// Enabling the check from cache generation options
        checkFromCacheGenerOpts = true;

        ICustomLineReader stdErrReader = null;
        
        ITargetPlatform[] toStripTargetList = settings.getCurrentlyUsedTargetPlatforms();
		// Stripping away SIS target
		ArrayList<ITargetPlatform> strippedTargetList = new ArrayList<ITargetPlatform>();
        for (int i = 0; i < toStripTargetList.length; i++) {
			ITargetPlatform platform = toStripTargetList[i];
			if(! platform.getId().equalsIgnoreCase(AppDepSettings.TARGET_TYPE_ID_SIS)){
		        checkCacheDirectoryExistenceAndCreateIfNeeded(platform.getId());
		        strippedTargetList.add(platform);
			}
		}
        
		currentlySelectedTargets = strippedTargetList.toArray(new TargetPlatform[0]);
        
        // Setting up fields that are used to build the command 
        options = "--refresh";	// Forcing cache refres	 //$NON-NLS-1$
        
        stdErrReader = new CacheCreationProgressLineReader();
        if(settings.isInSISFileAnalysisMode()){
        	String sisOptions = getSISGenerationOptions();
        	commands = sisOptions;
        }else{        
        	commands = ""; // No command is needed, generating only cache //$NON-NLS-1$
        }
        
        executeCommand(null, stdErrReader, jobContext);
        
		// Disabling the check from cache generation options
        checkFromCacheGenerOpts = false;
	}

	/**
	 * Get -sisfiles "<files user has been selected>" -options for SIS cache generation.
	 * @return -sisfiles "<files user has been selected>". User selected files are semi
	 * comma separated.
	 */
	private String getSISGenerationOptions(){
		
		String[] sisFiles = settings.getSISFilesForAnalysis();
		
		StringBuffer sisCommand = new StringBuffer();
		sisCommand.append("-sisfiles \""); //$NON-NLS-1$
		
		for (int i = 0; i < sisFiles.length; i++) {
			sisCommand.append(sisFiles[i]);
			if(i != sisFiles.length -1){
				sisCommand.append(";"); //$NON-NLS-1$
			}
		}
		
		sisCommand.append("\""); //$NON-NLS-1$
		
		return sisCommand.toString();
	}
	
	public void getComponentsThatUsesComponent(String componentName, 
			                                   ArrayList<String> resultLinesArrayList,
			                                   Job jobContext) throws InvalidCmdLineToolSettingException{
		
        ICustomLineReader stdOutReader = null;
        
		currentlySelectedTargets = settings.getCurrentlyUsedTargetPlatforms();		
		
        options = "";	// No special options are needed //$NON-NLS-1$
        stdOutReader = new LinesToStringArrayListCustomLineReader(resultLinesArrayList);
        commands = "-dependson " + componentName; //$NON-NLS-1$
        
        executeCommand(stdOutReader, null, jobContext);
	}

	public void getComponentsThatUsesFunction(String componentName, 
											  String functionOrdinal,
									          ArrayList<String> resultLinesArrayList,
									          Job jobContext) throws InvalidCmdLineToolSettingException{

		ICustomLineReader stdOutReader = null;
		
		currentlySelectedTargets = settings.getCurrentlyUsedTargetPlatforms();		

		options = "";	// No special options are needed //$NON-NLS-1$
		stdOutReader = new LinesToStringArrayListCustomLineReader(resultLinesArrayList);
		commands = "-usesfunction " + componentName + "@" + functionOrdinal; //$NON-NLS-1$ //$NON-NLS-2$
		
		executeCommand(stdOutReader, null, jobContext);
	}
	
	
	/**
	 * Check the existence of cache directory, and creates 
	 * a new directory (whole path) if needed.
	 * @param targetPlatformId Target plaform to check cache existence from.
	 */
	private void checkCacheDirectoryExistenceAndCreateIfNeeded(String targetPlatformId) {
		String cacheDirCreateFailedMsg = Messages.getString("AppDepCoreFacade.Cache_Dir_Create_Failed_Msg"); //$NON-NLS-1$
        File cacheDir = new File(settings.getCacheDirForTarget(targetPlatformId));
        if(cacheDir.exists()&& cacheDir.isFile()){
        	// If there already exists a file with same name => deleting it
        	deleteFile(cacheDir, false);
        }
        
        if(!cacheDir.exists()){
        	// If cache directory does not exist, creating it
        	if(!cacheDir.mkdirs()){
        		AppDepConsole.getInstance().println(cacheDirCreateFailedMsg, IConsolePrintUtility.MSG_ERROR);
        		throw new RuntimeException(cacheDirCreateFailedMsg);
        	}
        }
	}

	/**
	 * Deletes the file given as argument.
	 * @param fileToBeDeleted File to be deleted.
	 * @param ignoreDeleteFailure If this is set to <code>false</code> an exception is raised 
	 * when delete attempt has failed, if set to <code>true</code> raises and exception if
	 * delete has failed.
	 */
	private void deleteFile(File fileToBeDeleted, boolean ignoreDeleteFailure) {
    	if(!fileToBeDeleted.delete()){
    		if(!ignoreDeleteFailure){
        		String fileDeleteFailedMsg = Messages.getString("AppDepCoreFacade.File_Delete_Failed_Msg") + ": " + fileToBeDeleted.getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$
        		AppDepConsole.getInstance().println(fileDeleteFailedMsg, IConsolePrintUtility.MSG_ERROR);
        		throw new RuntimeException(fileDeleteFailedMsg);    			
    		}
    	}
	}
	
	private String[] buildCommandLine() throws InvalidCmdLineToolSettingException{
		
		Vector<String> cmdLineVector = new Vector<String>();
		
		try {
			// Executable
			cmdLineVector.add(settings.getAppDepProgramPathName());
			// Toolchain
			if(checkFromCacheGenerOpts  && cacheGenerOptionsAreSet()){
				CacheGenerationOptions opt = settings.getCacheGenerOptions();
				IToolchain toolchain = opt.getUsedToolchain();
				cmdLineVector.add(toolchain.getToolchainName());											
			}
			else{
				//Using current settings
				cmdLineVector.add(settings.getCurrentlyUsedToolChain().getToolchainName());			
			}
			// Parameters		
			String targetPlatformIds = currentlySelectedTargets[0].getId();
			// Combining targets ids with separateor character TARGET_SEPARATOR
			for (int i = 1; i < currentlySelectedTargets.length; i++) {
				targetPlatformIds = targetPlatformIds 
									+ TARGET_SEPARATOR 
									+ currentlySelectedTargets[i].getId();
			}
			cmdLineVector.add(prepareParameters(targetPlatformIds));
			
			// Options
			cmdLineVector.add(options);
			// Commands
			cmdLineVector.add(commands);								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cmdLineVector.toArray(new String[0]);
	}
	
	 /**
	 * Builds the command line and runs the given command.
	 * The current version support only one command to be executed
	 * at a time. Therefore we are using flag that prevents
	 * the execution of two commands at the same time.
	 * @param cmdLineArray Command line in an array form.
	 * @param stdOutReader Reference to custom stdout reader, or null if the usage 
	 * 					   of the default reader is what is wanted.
	 * @param stdErrReader Reference to custom stderr reader, or null if the usage 
	 * 					   of the default reader is what is wanted.
	 * @throws InvalidCmdLineToolSettingException 
	 */
	private void executeCommand(ICustomLineReader stdOutReader,
	 		  					ICustomLineReader stdErrReader) throws InvalidCmdLineToolSettingException{
		String[] cmdLineArr = buildCommandLine();
		
		//If we are in SIS Analysis mode, an empty symbolics cache file must be generated
        if(settings.isInSISFileAnalysisMode()){
        	try {        		
				generateEmptyCacheSymbolFile();
			} catch (Exception e) {
				//If an error occurs throwing runtime message and cache generation will not be started
				e.printStackTrace();
				AppDepConsole.getInstance().println(Messages.getString("AppDepCoreFacade.EmptySymbolCahceFileCreation_ErrMsg")  //$NON-NLS-1$
						+e.getMessage(), AppDepConsole.MSG_ERROR);
				throw new RuntimeException(e.getMessage());
			}
        }
        
		cmdLineExecutor.runCommand(cmdLineArr, stdOutReader, stdErrReader, currentJobContext);
		
	}

	/**
	 * Create an empty code>appdep-cache_symbol_tables.txt</code> -file
	 * because <code>appdep.exe</code> in SIS mode does not create one 
	 * and other cache handling logic requires one. 
	 * 
	 * @throws IOException if on error occurs
	 */
	 private void generateEmptyCacheSymbolFile() throws Exception {
		String filePath = settings.getCacheBaseDirForCurrentlyUsedSdk() +
			File.separatorChar + SIS_PATH + 
			File.separatorChar + ProductInfoRegistry.getCacheSymbolsFileName();
		
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,"Generating empty symbol cache file to: " +filePath);//$NON-NLS-1$
		
		StringBuffer b = new StringBuffer();
		b.append(ProductInfoRegistry.getCacheSymbolTablesFileContentPrefix());
		b.append(" ");//$NON-NLS-1$
		b.append(ProductInfoRegistry.getSupportedCacheFileVersionInfoString());
		b.append("\n");//$NON-NLS-1$
		b.append(CacheDataConstants.CACHE_FILE_END_MARK);
		
		FileUtils.writeToFile(filePath, b.toString());
	}

	/**
	 * Wrapper to to the referenced executeComman. Just stores
	 * job context that enables to add created worker thread 
	 * to the job context.
	 * @param cmdLineArray Command line in an array form.
	 * @param stdOutReader Reference to custom stdout reader, or null if the usage 
	 * 					   of the default reader is what is wanted.
	 * @param stdErrReader Reference to custom stderr reader, or null if the usage 
	 * 					   of the default reader is what is wanted.
	 * @param jobContext   Job object under which the command will be executed.
	 * @throws InvalidCmdLineToolSettingException 
	 */
	private void executeCommand(ICustomLineReader stdOutReader,
	 		  					ICustomLineReader stdErrReader, 
	 		  					Job jobContext) throws InvalidCmdLineToolSettingException{
		currentJobContext = jobContext;
		executeCommand(stdOutReader,
					   stdErrReader);
	}
	
	
	/**
	 * Just delegating the call further to the real observer.
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#progress(int)
	 */
	public void progress(int percentage) {
		observer.progress(percentage);		
	}

	/**
	 * Setting down execution flag and delegating 
	 * the call further to the real observer. 
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#completed(int)
	 */
	public void interrupted(String reasonMsg) {
		observer.interrupted(reasonMsg);
	}

	/**
	 * Just delegating the call further to the real observer.
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#progress(int)
	 */
	public void processCreated(Process proc) {
		observer.processCreated(proc);
	}
	
	/**
	 * Setting down execution flag and delegating 
	 * the call further to the real observer. 
	 * @see com.nokia.s60tools.util.cmdline.ICmdLineCommandExecutorObserver#completed(int)
	 */
	public void completed(int exitValue) {
		observer.completed(exitValue);
	}
		
}
