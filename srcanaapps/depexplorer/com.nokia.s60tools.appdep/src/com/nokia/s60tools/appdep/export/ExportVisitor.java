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


package com.nokia.s60tools.appdep.export;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.core.data.ComponentParentNode;
import com.nokia.s60tools.appdep.core.job.IJobProgressStatus;
import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.core.model.ExportFunctionData;
import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.appdep.exceptions.CacheFileDoesNotExistException;
import com.nokia.s60tools.appdep.exceptions.CacheIndexNotReadyException;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;
import com.nokia.s60tools.appdep.ui.views.main.MainViewDataPopulator;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;
import com.nokia.s60tools.util.exceptions.JobCancelledByUserException;

/**
 * Visitor class from Export report -functionality.
 */
public class ExportVisitor implements IVisitor {
	
	//
	// Private members and constants.
	// 
	private String sdkName;
	private String targets;
	private String build;
	private String rootComponentName;
	private String rootComponentFullName;
	private StringBuffer components;
	private StringBuffer properties;
	private StringBuffer exportedFunctions;
	private HashMap<String, String> componentNames;
	private static final String LEAF_NODE = "leafNode"; //$NON-NLS-1$
	private static final String PARENT_NODE = "parentNode"; //$NON-NLS-1$
	
	private IJobProgressStatus observer;
	//Other progress percentage @see ExportJob.java
	private int COMPONENTS_EXPORT_PERCENTAGE = 85;	
	private int progressBarProgressedCount = 1;
	private int progressBarComponentsCount = 1;
	private boolean isExportFromRoot = true;
		
	/**
	 * Constructor.
	 * @param sdkName SDK the data is got for the export.
	 * @param targets Targets the data is got for the export.
	 * @param build Build type the data is got for the export.
	 * @param rootComponentName Root component short name.
	 * @param rootComponentFullName Root component fully qualified name.
	 * @param observer Job progress observer
	 */
	public ExportVisitor(String sdkName, 
			String targets, String build, String rootComponentName, 		
			String rootComponentFullName,
			IJobProgressStatus observer){
		
		this.sdkName = sdkName;
		this.targets = targets;
		this.build = build;		
		this.rootComponentName = rootComponentName;
		this.rootComponentFullName = rootComponentFullName;
		
		this.observer = observer;;
		
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
				           "Visitor; this.rootComponentName = " +rootComponentName); //$NON-NLS-1$
		
		components = new StringBuffer();
		properties = new StringBuffer();
		exportedFunctions = new StringBuffer();
		componentNames = new HashMap<String, String>();		
	}

	/**
	 * Adds a single import function node.
	 * @param node Component node.
	 */
	private void addImportedFunctions(ComponentNode node){
		try {
			addImportedFunctionsToComponent(node.getParent().getName(), node.getName());
		} catch (NoSuchElementException e) {
			addErrorCommentToComponents(node, e);
			
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
					"Not found imported functions for: " +node.getFullName()  //$NON-NLS-1$
					+". Error was: " +e); //$NON-NLS-1$
		}	
		catch (Exception e) {
			addErrorCommentToComponents(node, e);
			e.printStackTrace();		
		}			
		
	}

	/**
	 * Adding error message info related to the component in question. 
	 * @param node Component node in question.
	 * @param e Exception encountered.
	 */
	private void addErrorCommentToComponents(ComponentNode node, Exception e) {
		components.append("<!-- ERROR while getting Imported functions for: "); //$NON-NLS-1$
		components.append(node.getName());
		components.append(", parent: "); //$NON-NLS-1$
		components.append(node.getParent().getName());
		components.append("\nError was:\n"); //$NON-NLS-1$
		components.append(e);
		components.append("\n-->"); //$NON-NLS-1$
	}
	
	/**
	 * Adding node name as key 
	 * and this.PARENT_NODE as value if node is ComponentParentNode
	 * or this.LEAF_NODE as value if node is ComponentLinkLeafNode 
	 * if not exist in this.PARENT_NODE already
	 * to this.componentNames
	 * 
	 * If adding this.PARENT_NODE and this.LEAF_NODE already exist, replacing
	 * 
	 * This is doing to know if a node exist as a parent in report or not,
	 * this has a affect to generate anchors and links in html 
	 * (from properties and external functions "<component name>" link points to
	 * that component only if it exist as parent node
	 * 
	 * @param node Node to to be added as key, if it does not exist already.
	 */
	private void addNodeName(ComponentNode node){
		
		boolean isParent = false;
		if(node instanceof ComponentParentNode){
			isParent = true;
		}
				
		//Collection all names of components for getting properties and exported functions
		if(!componentNames.containsKey(node.getName())){
			if(isParent){
				componentNames.put(node.getName(),PARENT_NODE);
			}
			else {
				componentNames.put(node.getName(),LEAF_NODE);
			}
			
		}else{
			String tmp = (String) componentNames.get(node.getName());
			if(isParent && tmp.equals(LEAF_NODE)){
				componentNames.put(node.getName(),PARENT_NODE);
			}

		}
	}
	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentLinkLeafNode)
	 */
	public void visit(ComponentLinkLeafNode node) {		
		// Exporting XML for this node only because it is leaf
		if(observer.isCanceled()){
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
					           "Cancelled by User when prosessing node: " +node.getName()); //$NON-NLS-1$
			return;
		}
		addNodeName(node);
		addComponent(node, true);
		addImportedFunctions(node);
		updateProgress(node);
		components.append("</component>"); //$NON-NLS-1$
				
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.views.data.IVisitor#visit(com.nokia.s60tools.appdep.core.data.ComponentParentNode)
	 */
	public void visit(ComponentParentNode node) {
		
		if(observer.isCanceled()){
			return;
		}
		
		DbgUtility.println(DbgUtility.PRIORITY_LOOP,"Visiting in node: " +node.getFullName()); //$NON-NLS-1$
		if(node.getFullName().equals(rootComponentFullName)){
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, "Found ROOT!"); //$NON-NLS-1$
		}
		
		addNodeName(node);
		
		//when found root, counting 2nd and 3rd level components for progress bar
		if ( node.isRootComponent() || node.getFullName().equals(rootComponentFullName)) {			
			countComponents(node);
		}	

		addComponent(node, false);

		addImportedFunctions(node);

		// For progress bar assuming that components under root is equal sized
		updateProgress(node);

		// Exporting XML for this node and for all its children
		ComponentNode[] childArray = node.getChildren();
		for (int i = 0; i < childArray.length; i++) {
			ComponentNode childNode = childArray[i];
			childNode.accept(this);
		}

		components.append("</component>"); //$NON-NLS-1$

		
	}

	/**
	 * Sets this.isExportFromRoot false if Export is not selected to start from root
	 * Sets progressBarComponentsCount
	 * @param node Parent where to start counting, counts nodes under that node
	 *        and nodes under those nodes
	 */
	private void countComponents(ComponentParentNode node) {
		// update progress
		int secondLevelComponentsCount = 0;
		int rootLevelComponentsCount = 0;

		try {
			
			if(!node.isRootComponent()){
				isExportFromRoot = false;
				DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
						           "Selected Export node was not Root node"); //$NON-NLS-1$
			}
			
			rootLevelComponentsCount = node.getChildren().length;
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
			                    "Export found "+ rootLevelComponentsCount  //$NON-NLS-1$
			                    + " 2nd level components under root: "  //$NON-NLS-1$
			                    + node.getFullName());

			if(node.hasChildren()){
				ComponentNode [] nodes = node.getChildren();
				ComponentNode tmp;
				ComponentParentNode parentTmp;
				for(int i=0; i<nodes.length; i++){
					tmp = nodes[i];
					if(tmp instanceof ComponentParentNode){
						parentTmp = (ComponentParentNode)tmp;
						secondLevelComponentsCount += parentTmp.getChildren().length;
						DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
								           "Adding: "+parentTmp.getChildren().length  //$NON-NLS-1$
								           + " when name: " +parentTmp.getFullName()); //$NON-NLS-1$
					}
					//else it is a LeafNode, so under that there aren't any nodes
				}
			}
		} catch (Exception e) {			
			String msg = Messages.getString("ExportVisitor.Could_Not_Count_Components_Msg"); //$NON-NLS-1$
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION, msg);
			AppDepConsole.getInstance().println(msg, IConsolePrintUtility.MSG_WARNING);
			e.printStackTrace();
		}
		
		progressBarComponentsCount = rootLevelComponentsCount + secondLevelComponentsCount;
		
	}

	/**
	 * Adds one component to this.components.
	 * @param node node to be added.
	 * @param isReference if component is reference it will be typed on "reference" else for "base"
	 */
	private void addComponent(ComponentNode node, boolean isReference ) {
		//<component> must close after calling, because under components will be imported functions 
		components.append("<component name=\""); //$NON-NLS-1$
		components.append(node.getName());
		components.append("\" "); //$NON-NLS-1$
		components.append(" fullName=\""); //$NON-NLS-1$
		components.append(node.getFullName());
		components.append("\" ");		 //$NON-NLS-1$
		components.append(" type=\""); //$NON-NLS-1$
		if(isReference){
			components.append("reference"); //$NON-NLS-1$
		}else{
			components.append("base"); //$NON-NLS-1$
		}		
				
		components.append("\" "); //$NON-NLS-1$
		components.append(">"); //$NON-NLS-1$
		components.append("\n"); //$NON-NLS-1$
	}	
	
	/**
	 * Updates progress status.
	 * @param node  Node currently under exporting.
	 */
	private void updateProgress(ComponentNode node){

		try {			
			
			if(node instanceof ComponentParentNode){
				
				ComponentParentNode tmp = (ComponentParentNode)node;
			
				if (isExportFromRoot && !tmp.isRootComponent() && 
						(tmp.getParent().isRootComponent() || tmp.getParent().getParent().isRootComponent() )) {
					//updateProgress(node.getName());
					updateProgress(tmp.getName());									
				}
				else if(!isExportFromRoot){
					//if parent or parents parent is selected export root
					if( tmp.getParent().getFullName().equals(rootComponentFullName )
							|| ( !tmp.getParent().isRootComponent() && tmp.getParent().getParent().getFullName().equals(rootComponentFullName) ) ){
						updateProgress(tmp.getName());
					}
				}
			}
			else {
				ComponentLinkLeafNode tmp = (ComponentLinkLeafNode)node;
				if (isExportFromRoot && (tmp.getParent().isRootComponent() || tmp.getParent().getParent().isRootComponent() )) {
					updateProgress(tmp.getName());					
				}				
				else if(!isExportFromRoot){
					//if parent or parents parent is selected export root
					if( tmp.getParent().getFullName().equals(rootComponentFullName )
							|| ( !tmp.getParent().isRootComponent() && tmp.getParent().getParent().getFullName().equals(rootComponentFullName) ) ){
						updateProgress(tmp.getName());
					}
				}

			}
		} catch (JobCancelledByUserException e) {
			AppDepConsole.getInstance().println(Messages.getString("ExportVisitor.Process_Cancelled_ByUser_ConsoleMsg")  //$NON-NLS-1$
									+ node.getName(), IConsolePrintUtility.MSG_NORMAL);
		} catch (Exception e) {
			// No exceptions forwarded if error occurs when updating progress
			AppDepConsole.getInstance().println(Messages.getString("ExportVisitor.Progress_Update_Failed_ConsoleMsg") + e, //$NON-NLS-1$
					                            IConsolePrintUtility.MSG_ERROR);
		}					
		
	}

	/**
	 * Notifies observers about the progress.
	 * @param componentName Component name currently at hand.
	 * @throws Exception
	 */
	private void updateProgress(String componentName) throws Exception {
		int percentage = 
			COMPONENTS_EXPORT_PERCENTAGE * 
			progressBarProgressedCount / progressBarComponentsCount;
			
		DbgUtility.println(DbgUtility.PRIORITY_LOOP,
							"Updating progress to: " + percentage  //$NON-NLS-1$
							+ " progressed so far: " +progressBarProgressedCount  //$NON-NLS-1$
							+ ", all cout: " +progressBarComponentsCount); //$NON-NLS-1$

		observer.progress(percentage, componentName);
		//Avoid > 100% progressed just in case
		if(progressBarProgressedCount < progressBarComponentsCount){
			progressBarProgressedCount++;
		}
	}
	
	/**
	 * Adds new imported function data for the selected component.
	 * @param parentComponentName parent component for the selected component
	 * @param selectedComponentName selected component.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CacheIndexNotReadyException
	 * @throws CacheFileDoesNotExistException
	 */
	private void addImportedFunctionsToComponent(
			String parentComponentName, String selectedComponentName ) 
			throws FileNotFoundException, 
				IOException, 
				CacheIndexNotReadyException, 
				CacheFileDoesNotExistException
	{		
		Collection<ImportFunctionData> importedFunctionsColl 
		= MainViewDataPopulator.
			getParentImportedFunctionsForComponent(
					parentComponentName,
					selectedComponentName);		

		
		String fOrdinal = null;
		String fName = null;
		boolean fVirtualFlag = false;
		String fOffset = null;
		
		//Using own buffer, so the XML will be well formed even if fails
		StringBuffer importedFunctions = new StringBuffer();
		
		importedFunctions.append("<importedFunctions>");		 //$NON-NLS-1$
		
		for (ImportFunctionData importFunctionData : importedFunctionsColl) {
			fOrdinal = importFunctionData.getFunctionOrdinal();
			fName = importFunctionData.getFunctionName();
			fVirtualFlag = importFunctionData.isVirtual();	
			
			importedFunctions.append("\n"); //$NON-NLS-1$
			importedFunctions.append("<function ordinal=\""); //$NON-NLS-1$
			importedFunctions.append(fOrdinal);
			importedFunctions.append("\" name=\""); //$NON-NLS-1$
			importedFunctions.append( StringUtils.replaceForbiddenCharacters( fName ));
			importedFunctions.append("\" "); //$NON-NLS-1$			
			
			if(fVirtualFlag){
				fOffset = importFunctionData.getFunctionOffsetAsString();
				importedFunctions.append(" offset=\""); //$NON-NLS-1$
				importedFunctions.append(fOffset);
				importedFunctions.append("\" "); //$NON-NLS-1$
				importedFunctions.append(" virtual=\"1\" "); //$NON-NLS-1$
			}
			else{
				// Non-virtual methods do not have offset
				fOffset = "";				 //$NON-NLS-1$
				importedFunctions.append(" virtual=\"0\" "); //$NON-NLS-1$
			}
			importedFunctions.append("/>\n");			 //$NON-NLS-1$
		}
		importedFunctions.append("</importedFunctions>\n");		 //$NON-NLS-1$
		components.append(importedFunctions);

	}	
	
	/**
	 * Created XML data blocks for component properties.
	 */
	public void createProperties(){
		
		if(observer.isCanceled()){
			DbgUtility.println(DbgUtility.PRIORITY_OPERATION,
					           Messages.getString("ExportVisitor.Process_Cancelled_ByUser_When_Processing_ConsoleMsg")); //$NON-NLS-1$
			return;
		}
		
		Collection<String> col = componentNames.keySet();
		Iterator<String> it = col.iterator();
		String name;
		ComponentPropertiesData comPropData = null;
		String [] cababilities;
		
		while(it.hasNext()){
			name = (String)it.next();
			properties.append("<component name=\""); //$NON-NLS-1$
			properties.append(name);
			
			properties.append("\" foundAsParent=\""); //$NON-NLS-1$
			properties.append(isComponentFoundAsParent(name));
			
			
			properties.append("\" type=\"properties\">"); //$NON-NLS-1$
			properties.append("\n"); //$NON-NLS-1$
		

			try {
				comPropData = MainViewDataPopulator.getComponentPropertyArrayForComponent(name, null);

				properties.append("<directory>"); //$NON-NLS-1$
				properties.append(comPropData.getDirectory());
				properties.append("</directory>");   //$NON-NLS-1$
				properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<filename>"); //$NON-NLS-1$
		        properties.append(comPropData.getFilename());
		        properties.append("</filename>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<binaryFormat>"); //$NON-NLS-1$
		        properties.append(comPropData.getBinaryFormat());
		        properties.append("</binaryFormat>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<UID1>"); //$NON-NLS-1$
		        properties.append(comPropData.getUid1());
		        properties.append("</UID1>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<UID2>"); //$NON-NLS-1$
		        properties.append(comPropData.getUid2());
		        properties.append("</UID2>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<UID3>"); //$NON-NLS-1$
		        properties.append(comPropData.getUid3());
		        properties.append("</UID3>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<secureID>"); //$NON-NLS-1$
		        properties.append(comPropData.getSecureId());
		        properties.append("</secureID>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<vendorID>"); //$NON-NLS-1$
		        properties.append(comPropData.getVendorId());
		        properties.append("</vendorID>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<capabilities>"); //$NON-NLS-1$
		        cababilities = comPropData.getCapabilities();
		        for(int j=0; j<cababilities.length; j++){
		        	properties.append(cababilities[j]);
		        	properties.append("\n"); //$NON-NLS-1$
		        }
		        properties.append("</capabilities>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<minHeapSize>"); //$NON-NLS-1$
		        properties.append(comPropData.getMinHeapSize());
		        properties.append("</minHeapSize>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<maxHeapSize>"); //$NON-NLS-1$
		        properties.append(comPropData.getMaxHeapSize());
		        properties.append("</maxHeapSize>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<stackSize>"); //$NON-NLS-1$
		        properties.append(comPropData.getStackSize());
		        properties.append("</stackSize>"); //$NON-NLS-1$
		        properties.append("\n");	 //$NON-NLS-1$
		        properties.append("<dllRefTableCount>"); //$NON-NLS-1$
		        properties.append(comPropData.getDllRefTableCount());
		        properties.append("</dllRefTableCount>");				 //$NON-NLS-1$
				properties.append("\n");					 //$NON-NLS-1$
				
			} catch (NoSuchElementException e) {
				// The selected component does necessary
				// have any data about exported functions.
				// Therefore we can ignore this exception
				//e.printStackTrace();
				properties.append("<!-- Not found properties for: "); //$NON-NLS-1$
				properties.append(name);
				properties.append(" -->"); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
				
				properties.append("<!-- Error: "); //$NON-NLS-1$
				properties.append(e);
				properties.append(" occurs when founding properties for: "); //$NON-NLS-1$
				properties.append(name);
				properties.append(" -->");				 //$NON-NLS-1$
			}
			
			properties.append("\n"); //$NON-NLS-1$
			properties.append("</component>"); //$NON-NLS-1$
			properties.append("\n"); //$NON-NLS-1$
		}		
		
	}
	
	/**
	 * Checks if there is component parent node available for given component name.
	 * @param name component name.
	 * @return <code>true</code> if found, otherwise <code>false</code>.
	 */
	private String isComponentFoundAsParent(String name){
		
		String tmp = (String) componentNames.get(name);
		
		return (tmp.equals(PARENT_NODE)) ? "true" : "false" ; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Creates XML data blocks for exported functions.
	 */
	public void createExportedFunctions(){
		
		if(observer.isCanceled()){
			DbgUtility.println(DbgUtility.PRIORITY_LOOP,Messages.getString("ExportVisitor.Process_Cancelled_ByUser_When_Processing_ExpFuncs_ConsoleMsg")); //$NON-NLS-1$
			return;
		}
		
		Collection<String> col = componentNames.keySet();		
		Iterator<String> it = col.iterator();
		String name;
		
		while(it.hasNext()){
			name = (String)it.next();
			exportedFunctions.append("<component name=\""); //$NON-NLS-1$
			exportedFunctions.append(name);
			
			exportedFunctions.append("\" foundAsParent=\""); //$NON-NLS-1$
			exportedFunctions.append(isComponentFoundAsParent(name));
			
			exportedFunctions.append("\" type=\"exportedFunctions\">"); //$NON-NLS-1$
			exportedFunctions.append("\n"); //$NON-NLS-1$


			try {
				Collection<ExportFunctionData> exp = MainViewDataPopulator.getExportedFunctionsForComponent(name);

				for (ExportFunctionData exportFunctionData : exp) {
					exportedFunctions.append("<function ordinal=\""); //$NON-NLS-1$
					exportedFunctions.append(exportFunctionData.getFunctionOrdinal());
					exportedFunctions.append("\" name=\""); //$NON-NLS-1$
					exportedFunctions.append(
							StringUtils.replaceForbiddenCharacters( 
									exportFunctionData.getFunctionName() ));
					exportedFunctions.append("\"/>"); //$NON-NLS-1$
					exportedFunctions.append("\n");					 //$NON-NLS-1$					
				}
				
			} catch (NoSuchElementException e) {
				// The selected component does necessary
				// have any data about exported functions.
				// Therefore we can ignore this exception
				//e.printStackTrace();
				exportedFunctions.append("<!-- Not found exported functions for: "); //$NON-NLS-1$
				exportedFunctions.append(name);
				exportedFunctions.append(" -->"); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
				
				exportedFunctions.append("<!-- Error: "); //$NON-NLS-1$
				exportedFunctions.append(e);
				exportedFunctions.append(" occurs when founding exported functions for: "); //$NON-NLS-1$
				exportedFunctions.append(name);
				exportedFunctions.append(" -->");				 //$NON-NLS-1$
			}
			
			exportedFunctions.append("\n"); //$NON-NLS-1$
			exportedFunctions.append("</component>"); //$NON-NLS-1$
			exportedFunctions.append("\n"); //$NON-NLS-1$
		}
		
	}		
	
	public String toString(){
		
		if(observer.isCanceled()){
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, "Cancelled by User when prosessing String "); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		
		StringBuffer b = new StringBuffer();
		
		//*********************************************************************
		//Header
		//*********************************************************************
		
//		 href=\"printReport.xsl\"
		b.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" ?><report>");			 //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append("<info><sdk name=\""); //$NON-NLS-1$
		b.append(this.sdkName);
		b.append("\" target=\""); //$NON-NLS-1$
		b.append(this.targets);
		b.append("\" build=\""); //$NON-NLS-1$
		b.append(this.build);
		b.append("\"/></info>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		//*********************************************************************
		//components
		//*********************************************************************
		b.append("<components>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append("<rootComponent name=\""); //$NON-NLS-1$
		b.append(this.rootComponentName);
		b.append("\">"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		//add all components
		b.append(components);
		
		b.append("\n"); //$NON-NLS-1$
		b.append("</rootComponent></components>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		//*********************************************************************
		//properties
		//*********************************************************************
		b.append("<properties>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append(properties);
		b.append("\n"); //$NON-NLS-1$
		b.append("</properties>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		//*********************************************************************
		//exported functions
		//*********************************************************************		
		b.append("<exportedFunctions>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append( exportedFunctions );
		b.append("\n"); //$NON-NLS-1$
		b.append("</exportedFunctions>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		//*********************************************************************
		//footer
		//*********************************************************************
		b.append("</report>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		
		return b.toString();
	}	
	
	/**
	 * Writes XML data to given destination file.
	 * @param fileName destination file.
	 */
	public void toFile( String fileName ) {
		
		if(observer.isCanceled()){
			DbgUtility.println(DbgUtility.PRIORITY_LOOP, Messages.getString("ExportVisitor.Process_Cancelled_ByUser_When_Processing_File_ConsoleMsg")); //$NON-NLS-1$
			return;
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					fileName));
			out.write(toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
}
