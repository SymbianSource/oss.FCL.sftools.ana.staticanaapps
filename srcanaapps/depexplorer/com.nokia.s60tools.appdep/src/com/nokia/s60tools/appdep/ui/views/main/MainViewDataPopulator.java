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
 
 
package com.nokia.s60tools.appdep.ui.views.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheDataManager;
import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.data.ICacheDataManager;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode.CompBindType;
import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.core.model.UsedComponentData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.preferences.DEPreferences;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;


/**
 * This singleton class handles the population of main view.
 */
public class MainViewDataPopulator {
	
	/**
	 * Reference to singleton instance.
	 */
	private static MainViewDataPopulator instance = null;
	
	/**
	 * This vector is used to store all the currently
	 * ongoing searches.
	 */
	private static Vector<SearchInstance> searchInstancesVector = null;

	/**
	 * Root node for the component tree to be modified.
	 */
	private static ComponentParentNode currentRootNode;
	

	/**
	 * Search instance for currently ongoing data population.
	 */
	private static SearchInstance currentSearchInstance;

	/**
	 * Worker threads register themselves
	 * when they start running and deregister itselves
	 * when they are done their work. In this way we are able
	 * to found out when all the threads have completed their
	 * work.
	 */
	private class ThreadRegister{
		
		private List<String> registerList = null;
		private final SearchInstance searchInst;
		private final MainViewPopulateProgressListener searchProgressListener;
		
		public ThreadRegister(SearchInstance searchInst, 
				              MainViewPopulateProgressListener searchProgressListener){
			this.searchInst = searchInst;
			this.searchProgressListener = searchProgressListener;
			registerList = Collections.synchronizedList(new ArrayList<String>());
		}
		
		public void register(String threadIdString){
			registerList.add(threadIdString);
		}

		public void deregister(String threadIdString){
			registerList.remove(threadIdString);
			if(registerList.size() == 0){
				// This search is over and can be removed from 
				// the list of ongoing searches
				searchInstancesVector.remove(searchInst);
				if(searchInst.isSearchAborted()){
					searchProgressListener.searchAborted(searchInst.getFoundComponentsCount());					
				}
				else{
					searchProgressListener.searchFinished(searchInst.getFoundComponentsCount());					
				}
			}
		}
	}

	/**
	 * There might be temporarily multiple ongoing
	 * searches. Therefore we must identify all the
	 * searches and separate their data.
	 */
	private class SearchInstance{
		
		
		/**
		 * Map containing string-node pairs for already added components.
		 * If a component can be found from the map, it wil be added as
		 * link leaf node instead of as parent node.
		 * The map is syncronized in order to enable multithread access.
		 */
		private Map<String, ComponentParentNode> componentMap = null;
			
		/**
		 * This interface is used to report about search progress.
		 */
		MainViewPopulateProgressListener searchProgressListener = null;		
		
		/**
		 * This member is used only for debugging purposes. It is used
		 * to store start time of population, which enables the calculation
		 * of total time that is spent for different threads that are doing
		 * the actual tree population. Start time of the search can
		 * be also used to identify uniquely different search instances.
		 */
		private final long startTime;
		
		/**
		 * All threads checks time to time for this flag in order
		 * to know if the search has been aborted by some reason.
		 * For example, plugin is deactivated or user has aborted
		 * the searching.
		 */
		private boolean isSearchAborted = false;
		
		/**
		 * Thread register instance. A new instance is created
		 * for each populateView call.
		 */
		private ThreadRegister threadReg = null;;		
		
		/**
		 * Constructs search instance with unique identifier.
	     * @param searchProgressListener Search progress listener.
 		 * @param initialMap initial values to componentMap, use in update mode only.
		 */		
		public SearchInstance(MainViewPopulateProgressListener searchProgressListener, Map<String,  ComponentParentNode> initialMap){
			this(searchProgressListener);	
			synchronized(componentMap){
				componentMap.putAll(initialMap);
			}
			
		}		
		
		/**
		 * Constructs search instance with unique identifier.
	     * @param searchProgressListener Search progress listener.
		 */
		public SearchInstance(MainViewPopulateProgressListener searchProgressListener){
			startTime = System.currentTimeMillis();
			this.searchProgressListener = searchProgressListener;
			// Component map has to be synchronized for thread access
			componentMap = Collections.synchronizedMap(new HashMap<String, ComponentParentNode>());
		}

		/**
		 * Checks if we have already once added the component, and based
		 * on the information adds the component either as parent or 
		 * leaf link node.
		 * @param parent Parent node under which to add the new node,
		 * @param parentNodeList If component was parent node, it is added into this list.	
		 * @param cmpName Name of the component that has been found.
		 */
		public void checkIfAlreadyAddedAndAddNode(ComponentParentNode parent, 
												   ArrayList<ComponentParentNode> parentNodeList, String cmpName) {

			ComponentParentNode child = null;
			ComponentLinkLeafNode leaf = null;

			//We needs to syncronize the map of already found components during check 
			// and add operation for enabling safe multithread access into the map.
			synchronized(componentMap){
			
				ComponentParentNode node = componentMap.get(cmpName);
		
				if(node != null){
					// Component already added to map => leaf node
					leaf = new ComponentLinkLeafNode(node);
					parent.addChild(leaf);
				}			
				else if(parent.getRootNode().getName().equalsIgnoreCase(cmpName)){
					// Component already added to map => leaf node
					leaf = new ComponentLinkLeafNode(parent.getRootNode());
					parent.addChild(leaf);				
				}
				else{
					// Component is added to tree for the first time
					child = new ComponentParentNode(cmpName);
					// Always adding first into component map
					componentMap.put(child.getName(), child);
					child.setTargetPlatform(parent.getTargetPlatform());
					parent.addChild(child);		
					parentNodeList.add(child);
					if(searchProgressListener != null){
						searchProgressListener.componentAdded(child, componentMap.size());	
					}
				}
				
			} // synchronized
			
		}
		
		/**
		 * @return Returns the isSearchAborted.
		 */
		public boolean isSearchAborted() {
			return isSearchAborted;
		}

		/**
		 * @param isSearchAborted The isSearchAborted to set.
		 */
		public void setSearchAborted(boolean isSearchAborted) {
			this.isSearchAborted = isSearchAborted;
		}

		/**
		 * @return Returns the startTime.
		 */
		public long getStartTime() {
			return startTime;
		}

		/**
		 * @return Returns the componentMap.
		 */
		public int getFoundComponentsCount() {
			return componentMap.size();
		}

		public void createThreadRegister() {
			threadReg = new ThreadRegister(this, searchProgressListener);
		}

		/**
		 * @return Returns the searchProgressListener.
		 */
		public MainViewPopulateProgressListener getSearchProgressListener() {
			return searchProgressListener;
		}

		/**
		 * Informs search progress listener that search has finished successfully.
		 */
		public void searchFinished() {
			searchProgressListener.searchFinished(getFoundComponentsCount());
		}

		/**
		 * Registers dependency search thread.
		 */
		public void registerThread(String threadId) {
			threadReg.register(threadId);
		}

		/**
		 * Deregisters dependency search thread.
		 */
		public void deregisterThread(String threadId) {
			threadReg.deregister(threadId);
		}
	
	}
	
	/**
	 * Singleton instance accessor. Sets given parameter also into current instance
	 * if needed.
	 * @return Returns singleton class instance.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private static MainViewDataPopulator getInstance(){
		if( instance == null ){
			instance = new MainViewDataPopulator();
		}
		return instance;		
	}	
	
	/**
	 * Default constructor 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private MainViewDataPopulator(){	
		searchInstancesVector = new Vector<SearchInstance>();
	}
	
	/**
	 * Static delegate method for populateViewImpl.
	 * @param invisibleRoot Invisible root node to add actual component
	 *                      dependency tree.
	 * @param searchProgressListener Search progress listener.
	 * @param compToSearchFor Name of the component to search for
	 * @param currentlyAnalyzedComponentTargetPlatform Target platform for the component to search for, or <code>null</code>
	 *                                                 if target platform does not matter.
	 */
	public static void populateView(ComponentParentNode invisibleRoot, 
									MainViewPopulateProgressListener searchProgressListener, 
				                    String compToSearchFor, ITargetPlatform currentlyAnalyzedComponentTargetPlatform) {
		// Storing root node for further reference
		MainViewDataPopulator.currentRootNode = invisibleRoot;
		// Making sure that instance is generated
		MainViewDataPopulator inst = getInstance();
		// Creating search instance and storing for further reference
		currentSearchInstance = inst.new SearchInstance(searchProgressListener);
		searchInstancesVector.add(currentSearchInstance);
		// Populating the view
		inst.populateViewImpl(currentSearchInstance, currentRootNode, compToSearchFor, currentlyAnalyzedComponentTargetPlatform);
	}
	

	/**
	 * Static delegate method for populateViewImpl. Populates only part of the view, not whole view.
	 * @param parentNodeWhereToPopulatePartOfView a root node where populating starts
	 * @param newComponentName a name of new component to be added to parentNodeWhereToPopulatePartOfView
	 * @param searchProgressListener Search progress listener.
	 * @param parentNodes nodes all ready added to tree
	 * @param childToRemove a child to be removed from newComponentName or null if none
	 */
	public static void populatePartOfView(ComponentParentNode parentNodeWhereToPopulatePartOfView,
			String newComponentName,
			MainViewPopulateProgressListener searchProgressListener, 									
			Map<String, ComponentParentNode> parentNodes, 
			ComponentNode childToRemove) {
		
		// Making sure that instance is generated
		MainViewDataPopulator inst = getInstance();
		// Creating search instance and storing for further reference
		currentSearchInstance = inst.new SearchInstance(searchProgressListener, parentNodes);
		searchInstancesVector.add(currentSearchInstance);
		//Cache must be already loaded, because this comes from user action
		inst.populatePartOfViewImpl(currentSearchInstance, parentNodeWhereToPopulatePartOfView, newComponentName, childToRemove);
	}

	/**
	 * Creates a new cache data manager object based on the currently
	 * active settings.
	 * @return New cache reader object
	 * @throws CacheFileDoesNotExistException
	 * @throws IOException
	 */
	private static ICacheDataManager createNewCacheDataManagerBasedOnActiveSettings() throws CacheFileDoesNotExistException, IOException{
		return createNewCacheDataManagerBasedOnGivenSettings(AppDepSettings.getActiveSettings());
	}
		
	/**
	 * Creates a new cache data manager object based on the currently
	 * active settings.
	 * @param st Settings to be used for creating cache data reader.
	 * @return New cache reader object
	 * @throws CacheFileDoesNotExistException
	 * @throws IOException
	 */
	private static ICacheDataManager createNewCacheDataManagerBasedOnGivenSettings(AppDepSettings st) throws CacheFileDoesNotExistException, IOException{
		
		ICacheDataManager cacheMgr = null;
		
		try {
			// Making sure that instance is generated
			getInstance();
			
			// Fetching current parameters
			cacheMgr = CacheDataManager.getInstance();
		} catch (CacheFileDoesNotExistException e) {
    		AppDepConsole.getInstance().println(
							Messages.getString("MainViewDataPopulator.Cache_File_Not_Found_Msg_Start") //$NON-NLS-1$
							+ e.getMessage()
							+ Messages.getString("MainViewDataPopulator.Cache_File_Not_Found_Msg_End"), //$NON-NLS-1$
    						IConsolePrintUtility.MSG_ERROR);
			throw e;
			
		} catch (IOException e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(
							Messages.getString("MainViewDataPopulator.Failed_To_Create_Cache_Reader_Msg") //$NON-NLS-1$
							+ e.getMessage(),
							IConsolePrintUtility.MSG_ERROR);	
			throw e;
		}
		
		return cacheMgr;
	}
	
	/**
	 * Populates the tree view from the data gained from cache file.
	 * @param searchInst Search instance to start population for.
	 * @param invisibleRoot Invisible root node to add actual component
	 *                      dependency tree.
	 * @param compToSearchFor Name of the component to search for
	 * @param targetPlatform Target platform for the component to search for, or <code>null</code>
	 *                                                 if target platform does not matter.
	 */
	private void populateViewImpl(SearchInstance searchInst,
			                      ComponentParentNode invisibleRoot, 
			                      String compToSearchFor, ITargetPlatform targetPlatform) {
		
		try {
			
			// Removing also old children from the invisible root
			invisibleRoot.removeAllChildren();
			
			ComponentParentNode root =  new ComponentParentNode(compToSearchFor);
			root.setRootComponent(true);
			root.setTargetPlatform(targetPlatform);
			invisibleRoot.addChild(root);
			ComponentParentNode parent = root;			
			
			searchInst.createThreadRegister();
			
			ICacheDataManager cacheMgr = createNewCacheDataManagerBasedOnActiveSettings();

			searchInst.getSearchProgressListener().searchStarted();		
			
			getDirectDependenciesForComponent(searchInst, cacheMgr, compToSearchFor, parent, true);			
			
		} catch (Exception e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(
							Messages.getString("MainViewDataPopulator.View_Population_Failed_Msg") //$NON-NLS-1$
							+ e.getMessage(),
							IConsolePrintUtility.MSG_ERROR);				
		}
	}	
	
	/**
	 * Populates the tree view from the data gained from cache file.
	 * @param searchInst Search instance to start population for.
	 * @param nodeWhereToPopulate a node where populating starts
	 * @param newComponentName a name of new component to be added to newComponentName
	 * @param childToRemove a child to be removed from newComponentName
	 * 
	 */
	private void populatePartOfViewImpl(SearchInstance searchInst,
			                      ComponentParentNode nodeWhereToPopulate, String newComponentName, ComponentNode childToRemove) {
		
		try {			

			// Component to be analyzed was set and we can continue, first we must create new component with name of the old component...
			ComponentParentNode newComponent = new ComponentParentNode(childToRemove.getName());
			//Set as concrete component - that will change the name of component to new, and set old component name as generic component name
			newComponent.setConcreteName(newComponentName, CompBindType.USER_BIND);

			//Replacing component with new. Component goes to same place as the old one.
			nodeWhereToPopulate.replaceChild(childToRemove, newComponent);
			
			searchInst.createThreadRegister();
			
			ICacheDataManager cacheMgr = createNewCacheDataManagerBasedOnActiveSettings();

			searchInst.getSearchProgressListener().searchStarted();		
			
			getDirectDependenciesForComponent(searchInst, cacheMgr, newComponentName, newComponent, true);			
			
		} catch (Exception e) {
			e.printStackTrace();
			AppDepConsole.getInstance().println(
							Messages.getString("MainViewDataPopulator.View_Population_Failed_Msg") //$NON-NLS-1$
							+ e.getMessage(),
							IConsolePrintUtility.MSG_ERROR);				
		}
	}

	/**
	 * Uses cache reader to get direct dependencies for the given component name.
	 * @param cacheMgr Cache reader object to be used for reading cache.
	 * @param compToSearchFor Name of the component to seek from cache.
	 * @param parent Parenet node under which to add dependent components. 
	 * @param isFirstLevel Flag value is set to <code>true</code> if we are resolving
	 *                     dependencies for the highest level, otherwise set to 
	 *                     <code>false</code>.
	 * @throws IOException
	 */
	private void getDirectDependenciesForComponent(SearchInstance searchInst,
			                                       ICacheDataManager cacheMgr, 
												   String compToSearchFor, 
												   ComponentParentNode parent, 
												   boolean isFirstLevel) throws IOException {
		List<UsedComponentData> directDepComponents = null;
		
		// Is search aborted?
		if(searchInst.isSearchAborted()){
			// If it is, just returning without doing anything.
			return;
		}

		try {
			directDepComponents = cacheMgr.getDirectlyDependentComponentsFor(compToSearchFor);						
			addToTreeDataStructure(searchInst, cacheMgr, directDepComponents, parent, isFirstLevel);
		} catch (NoSuchElementException e) {
			//Try to get missing component by predefined component prefix list
			getDirectDependenciesForMissingComponentByPrefixList(searchInst, cacheMgr, compToSearchFor, parent, isFirstLevel);			
		}
	}
	
	/**
	 * Search missing (possible generic) components real concrete implementation
	 * and if found, uses that as component. Uses DE preference page to get prefixes.
	 * 
	 * Uses cache reader to get direct dependencies for the given component name.
	 * @param cacheMgr Cache reader object to be used for reading cache.
	 * @param compToSearchFor Name of the component to seek from cache.
	 * @param parent Parent node under which to add dependent components. 
	 * @param isFirstLevel Flag value is set to <code>true</code> if we are resolving
	 *                     dependencies for the highest level, otherwise set to 
	 *                     <code>false</code>.
	 * @throws IOException
	 */
	private void getDirectDependenciesForMissingComponentByPrefixList(SearchInstance searchInst,
			                                       ICacheDataManager cacheMgr, 
												   String compToSearchFor, 
												   ComponentParentNode parent, 
												   boolean isFirstLevel) throws IOException {
		
		List<UsedComponentData> directDepComponents = null;
		
		// Is search aborted?
		if(searchInst.isSearchAborted()){
			// If it is, just returning without doing anything.
			return;
		}


		//Try to get missing component by predefined component prefix list
		List<String> searchOrderPrefixsList = DEPreferences.getSearchOrderPrefixsList();


		boolean setMissingAndReturn = searchOrderPrefixsList.isEmpty();

		//If list is empty, there is no preferences added, just stop populating tree and returning

		String concreteComponentName = null;
		

		try {
			//Getting possible match by prefix list
			//If found, setting component and keep on populating data, if not, set as missing and resolved and return
			
			if(!setMissingAndReturn){
				ICacheDataManager manager = CacheDataManager.getInstance();
				concreteComponentName = manager.searchComponentWithPrefix(searchOrderPrefixsList, parent.getName());
			}
			if(concreteComponentName==null){
				setMissingAndReturn = true;
			}
			if(setMissingAndReturn){
				parent.setMissing(true);
				// No more searching further. Therefore, all the 
				// children nodes can thought to be resolved.
				parent.setDirectChildrensResolved(true);
				return;		
			}				

			//Setting new concrete name for component and component to search for
			parent.setConcreteName(concreteComponentName, ComponentParentNode.CompBindType.AUTO_BIND);
			compToSearchFor = concreteComponentName;
			directDepComponents = cacheMgr.getDirectlyDependentComponentsFor(compToSearchFor);						
			addToTreeDataStructure(searchInst, cacheMgr, directDepComponents, parent, isFirstLevel);
		} catch (NoSuchElementException e) {
			parent.setMissing(true);
			// No more searching further. Therefore, all the 
			// children nodes can thought to be resolved.
			parent.setDirectChildrensResolved(true);
			return;
		} catch (CacheFileDoesNotExistException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		
	
	}	
	
	/**
	 * Goes through the component name array, and checks if already exists in the
	 * component map. If not, added as a parent node, otherwise added as a leaf link 
	 * node to the tree data structure. 
	 * @param cacheMgr Cache reader object to be used for reading cache.
	 * @param directDepComponents Component name string array to be examined.
	 * @param parent Parent node to add newly created component nodes.
	 * @param isFirstLevel Flag value is set to <code>true</code> if we are resolving
	 *                     dependencies for the highest level, otherwise set to 
	 *                     <code>false</code>.
	 * @throws IOException 
	 */
	private void addToTreeDataStructure(SearchInstance searchInst,
										ICacheDataManager cacheMgr,
										List<UsedComponentData> directDepComponents, 
										ComponentParentNode parent, 
										boolean isFirstLevel) throws IOException {
		
		ArrayList<ComponentParentNode> parentNodeList = new ArrayList<ComponentParentNode>();
		
		// Is search aborted?
		if(searchInst.isSearchAborted()){
			// If it is, just returning without doing anything.
			return;
		}
		
		for (int i = 0; i < directDepComponents.size(); i++) {
			String cmpName = directDepComponents.get(i).getComponentName().toLowerCase();
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, "directDepComponents[" + i + "]: " + cmpName);			 //$NON-NLS-1$ //$NON-NLS-2$
			searchInst.checkIfAlreadyAddedAndAddNode(parent, parentNodeList, cmpName);
		}

		//Now we have successfully added all the childrens to the parent
		parent.setDirectChildrensResolved(true);
		
		// If there were no components at first level, the following
		// for loop is unnecessary and we are already finished searching
		// in this early stage because there were no dependencies.
		if(isFirstLevel && parentNodeList.size() == 0){
			searchInst.searchFinished();
			return;
		}
		
		// Going through all the found parent nodes
		for (Iterator<ComponentParentNode> iter = parentNodeList.iterator(); iter.hasNext();) {
			ComponentParentNode node = iter.next();
			
			if(isFirstLevel){
				final ComponentParentNode parentNode = node;
				final SearchInstance currentSearchInstance = searchInst;
				// Creating thread that resolves this sub-tree
				Thread worker = new Thread(){
					public void run() {

						try {
							
							// Registering the thread
							currentSearchInstance.registerThread(this.toString());							
							
							long runMethodStartTime = System.currentTimeMillis();
							DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
												this.toString() + " started (" //$NON-NLS-1$
												+ parentNode.getName() + " sub-tree): "  //$NON-NLS-1$
												+ new Date(runMethodStartTime).toString());	

							getDirectDependenciesForComponent(currentSearchInstance,
															  CacheDataManager.getInstance(),
															  parentNode.getName(), 
															  parentNode, false);							
						} catch (IOException e) {
							e.printStackTrace();
							throw new NoSuchElementException();
						} catch (CacheFileDoesNotExistException e) {
							e.printStackTrace();
							String errMsg = Messages.getString("MainViewDataPopulator.Cache_File_Not_Found_Msg_Start") + e.getMessage() + Messages.getString("MainViewDataPopulator.Cache_File_Not_Found_Msg_End"); //$NON-NLS-1$ //$NON-NLS-2$
							AppDepConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
						}				
						long endTime = System.currentTimeMillis();
						
						DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
											this.toString() 
											+ " ended: " + new Date(endTime).toString()); //$NON-NLS-1$
						DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() + "TOTAL: "  //$NON-NLS-1$
								           + (endTime-currentSearchInstance.getStartTime())/1000 + " seconds!"); //$NON-NLS-1$
						DbgUtility.println(DbgUtility.PRIORITY_OPERATION, this.toString() + "Parent component count: "  //$NON-NLS-1$
								           + currentSearchInstance.getFoundComponentsCount());
						
						// Deregistering the thread
						currentSearchInstance.deregisterThread(this.toString());
					}					
				};
				// Setting priority into lower that normal
				// in order not to disturb UI threads.
				worker.setPriority(Thread.NORM_PRIORITY-2);
				// Kicking-off the thread
				worker.start();
			}
			else{
				getDirectDependenciesForComponent(searchInst, cacheMgr, node.getName(), node, false);								
			}
		}			
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.CacheDataManager#getParentImportedFunctionsForComponent(java.lang.String, java.lang.String)
	 */
	public static Collection<ImportFunctionData> getParentImportedFunctionsForComponent(String parentCmpName, String importedCmpName) throws FileNotFoundException, IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException  {
		// Concurrent use of the current data reader may cause problems
		// Therefore, creating a new reader instance.
		ICacheDataManager localMgr = createNewCacheDataManagerBasedOnActiveSettings();
		return localMgr.getParentImportedFunctionsForComponent(parentCmpName, importedCmpName);
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.data.CacheDataManager#getParentExportedFunctionsForComponent(java.lang.String, java.lang.String)
	 */
	public static Collection<ExportFunctionData> getExportedFunctionsForComponent(String componentNameWithExtension) throws FileNotFoundException, IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException  {
		// Concurrent use of the current data reader may cause problems
		// Therefore, creating a new reader instance.
		ICacheDataManager localMgr = createNewCacheDataManagerBasedOnActiveSettings();
		return localMgr.getExportedFunctionsForComponent(componentNameWithExtension);
	}

	/**
	 * Public method available for aborting the search.
	 */
	static public void abortCurrentSearch() {
		getInstance().abortCurrentSearchImpl();
	}
		
	/**
	 * Sets abort flag to <code>true</code> in order to abort
	 * the current search.
	 */
	private void abortCurrentSearchImpl() {
		if(searchInstancesVector.size() > 0){
			SearchInstance si = searchInstancesVector.lastElement();
			si.setSearchAborted(true);
		}
	}

	/**
	 * Gets component property array.
	 * @param cmpName Component name (mandatory)
	 * @param targetPlatform Target platform restriction, or <code>null</code> if target platform does not matter.
	 * @return component properties for the given component.
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 * @throws CacheFileDoesNotExistException
	 */
	public static ComponentPropertiesData getComponentPropertyArrayForComponent(String cmpName, ITargetPlatform targetPlatform) throws IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException  {
		// Concurrent use of the current data reader may cause problems
		// Therefore, creating a new reader instance.
		ICacheDataManager localMgr = createNewCacheDataManagerBasedOnActiveSettings();
		return localMgr.getComponentPropertyArrayForComponent(cmpName, targetPlatform);
	}

	/**
	 * Gets the component properties for the components
	 * that are using the given component. Delegates request further 
	 * to CacheDataManager class.
	 * @param settings Settings to be used for creating cache data reader.
	 * @param progressCallback Job progress callback interface.
	 * @param resultComponentsArrayList Array list object to return resulting components into. 
	 * @param componentName Name of the component to search using components for. 
	 * @param functionOrdinal Ordinal of the function to search using components for. 
	 *                        This parameter can be set to <code>null</code> if we are
	 *                        only interested in components that are using the given component.
	 * @return Returns components with their properties that are using 
	 * 		   the component given as parameter.
	 * @throws IOException 
	 * @throws CacheFileDoesNotExistException 
	 * @throws JobCancelledByUserException 
	 * @see com.nokia.s60tools.appdep.core.data.ICacheDataManager#getUsingComponents
	 */
	public static void getUsingComponents(AppDepSettings settings,
									   IJobProgressStatus progressCallback,	
									   ArrayList<ComponentPropertiesData> resultComponentsArrayList,
									   String componentName,
									   String functionOrdinal) throws CacheFileDoesNotExistException, 
									   								  IOException, 
									   								  JobCancelledByUserException {
		// Concurrent use of the current data reader may cause problems
		// Therefore, creating a new reader instance.
		ICacheDataManager localManager = createNewCacheDataManagerBasedOnGivenSettings(settings);
		localManager.getUsingComponents(progressCallback,
									   resultComponentsArrayList, 
									   componentName, 
				                       functionOrdinal);
	}

	/**
	 * Gets target platform ID string for the given component.
	 * In case of multitarget selection gets data from component properties, otherwise
	 * uses currently active target platform.
	 * @param settings settings object to be used for getting target platform data
	 * @param componentName  component to search target platform id string for
	 * @return target platform ID string for the given component.
	 * @throws CacheFileDoesNotExistException 
	 * @throws CacheIndexNotReadyException 
	 * @throws IOException 
	 */
	public static String getTargetPlatformIdStringForComponent(AppDepSettings settings, String componentName) throws IOException, CacheIndexNotReadyException, CacheFileDoesNotExistException {
		String targetPlatformId;
		ITargetPlatform[] usedPlatforms = settings.getCurrentlyUsedTargetPlatforms();
		
		// Finding out the used target platform (=build variant)
		if(usedPlatforms.length > 1){
			// Multitarget selection, or 'SIS file + single target' selection
			// Finding target platform from the component properties
			ComponentPropertiesData comPropData = getComponentPropertyArrayForComponent(componentName, null);
			targetPlatformId = comPropData.getTargetPlatform().getId();
		}
		else{
			// Only single target is selected => string representation can be used directly.
			targetPlatformId = settings
			.getCurrentlyUsedTargetPlatformsAsString();						
		}

		return targetPlatformId;
	}

}
