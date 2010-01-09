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
 
 
package com.nokia.s60tools.appdep.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.nokia.s60tools.appdep.core.ITargetPlatform;
import com.nokia.s60tools.appdep.core.TargetPlatform;
import com.nokia.s60tools.appdep.core.data.CacheCompPropertyField;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.PropertyData;


/**
 * Stores the properties and all the used components listed 
 * for a certain component in dependencies cache file.
 */
public class ComponentPropertiesData {

	
	//
	// NOTE: Column indices must start from zero (0) and
	// the columns must be added in ascending numeric
	// order.
	//
	
	// These are column indices for a simple property view 
	public static final int PROPERTY_COLUMN_INDEX = 0;
	public static final int VALUE_COLUMN_INDEX = 1;
	
	// These are column indices for a full list view of properties 
	public static final int NAME_COLUMN_INDEX = 0;
	public static final int BIN_FORMAT_COLUMN_INDEX = 1;
	public static final int UID1_COLUMN_INDEX = 2;
	public static final int UID2_COLUMN_INDEX = 3;
	public static final int UID3_COLUMN_INDEX = 4;
	public static final int SECURE_ID_COLUMN_INDEX = 5;
	public static final int VENDOR_ID_COLUMN_INDEX = 6;
	public static final int MIN_HEAP_COLUMN_INDEX = 7;
	public static final int MAX_HEAP_COLUMN_INDEX = 8;
	public static final int STACK_SIZE_COLUMN_INDEX = 9;	
	
	/**
	 * Descriptions used for the different property fields
	 */
	public static final String[] DESCRIPT_ARR = {   Messages.getString("ComponentPropertiesData.PropFieldDesc_Directory"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_Filename"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_BinaryFormat"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_UID1"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_UID2"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_UID3"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_SecureID"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_VendorID"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_Capabilities"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_MinHeapSize"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_MaxHeapSize"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_StackSize"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_CacheTimestamp"), //$NON-NLS-1$
													Messages.getString("ComponentPropertiesData.PropFieldDesc_DllRefTableCount") //$NON-NLS-1$
												};
	
	//
	// Component properties fields
	//
	private String directory;
	private String filename;
	private String binaryFormat;
	private String uid1;
	private String uid2;
	private String uid3;
	private String secureId;
	private String vendorId;
	private String[] capabilities;
	private String minHeapSize;
	private String maxHeapSize;
	private String stackSize;
	private String cacheTimestamp;
	private String dllRefTableCount;
	
	/**
	 * List of components that are used directly by this component.
	 * The list of components are in the order they have been added into the list.
	 * All map keys should be stored in lower case.
	 */
	private Map<String, UsedComponentData> usedComponents;
	
	/**
	 * Constructs the object based on the given array 
	 * of properties.
	 * @param propertyValueArray Value array containing property information.
	 */
	public ComponentPropertiesData(String[] propertyValueArray){
		// We want to preserve the order the used components are inserted into the map.
		usedComponents = new LinkedHashMap<String, UsedComponentData>();
		setPropertyData(propertyValueArray);
	}

	/**
	 * Sets objects data based on the given array 
	 * of properties.
	 * @param propertyValueArray Value array containing property information.
	 */
	private void setPropertyData(String[] propertyValueArray){
		directory = propertyValueArray[CacheCompPropertyField.DIRECTORY_ARR_INDX];
		filename = propertyValueArray[CacheCompPropertyField.FILENAME_ARR_INDX];
		binaryFormat = propertyValueArray[CacheCompPropertyField.BINARY_FORMAT_ARR_INDX];
		uid1 = propertyValueArray[CacheCompPropertyField.UID1_ARR_INDX];
		uid2 = propertyValueArray[CacheCompPropertyField.UID2_ARR_INDX];
		uid3 = propertyValueArray[CacheCompPropertyField.UID3_ARR_INDX];
		secureId = propertyValueArray[CacheCompPropertyField.SECURE_ID_ARR_INDX];
		vendorId = propertyValueArray[CacheCompPropertyField.VENDOR_ID_ARR_INDX];
		parseCapabilities(propertyValueArray[CacheCompPropertyField.CAPABILITIES_ARR_INDX]);
		minHeapSize = propertyValueArray[CacheCompPropertyField.MIN_HEAP_SIZE_ARR_INDX];
		maxHeapSize = propertyValueArray[CacheCompPropertyField.MAX_HEAP_SIZE_ARR_INDX];
		stackSize = propertyValueArray[CacheCompPropertyField.STACK_SIZE_ARR_INDX];
		cacheTimestamp = propertyValueArray[CacheCompPropertyField.CACHE_TIMESTAMP_ARR_INDX];
		dllRefTableCount = propertyValueArray[CacheCompPropertyField.DLL_REF_TABLE_COUNT_ARR_INDX];		
	}
	
	/**
	 * Parses and transforms capabilities from numeric format into string array.
	 * @param capabilitiesInNumericFormatString capabilities in numeric format
	 */
	private void parseCapabilities(String capabilitiesInNumericFormatString) {
		
		int allDefinedCapabilitiesAsInt = Integer.parseInt(capabilitiesInNumericFormatString);

		// All the capabilities that was found for this executable
        ArrayList<String> foundCapabilities = new ArrayList<String>();
		
		// Capabilities to compare against
        ArrayList<String> symbianCaps = new ArrayList<String>();
        symbianCaps.add("TCB"); //$NON-NLS-1$
        symbianCaps.add("CommDD"); //$NON-NLS-1$
        symbianCaps.add("PowerMgmt"); //$NON-NLS-1$
        symbianCaps.add("MultimediaDD"); //$NON-NLS-1$
        symbianCaps.add("ReadDeviceData"); //$NON-NLS-1$
        symbianCaps.add("WriteDeviceData"); //$NON-NLS-1$
        symbianCaps.add("DRM"); //$NON-NLS-1$
        symbianCaps.add("TrustedUI"); //$NON-NLS-1$
        symbianCaps.add("ProtServ"); //$NON-NLS-1$
        symbianCaps.add("DiskAdmin"); //$NON-NLS-1$
        symbianCaps.add("NetworkControl"); //$NON-NLS-1$
        symbianCaps.add("AllFiles"); //$NON-NLS-1$
        symbianCaps.add("SwEvent"); //$NON-NLS-1$
        symbianCaps.add("NetworkServices"); //$NON-NLS-1$
        symbianCaps.add("LocalServices"); //$NON-NLS-1$
        symbianCaps.add("ReadUserData"); //$NON-NLS-1$
        symbianCaps.add("WriteUserData"); //$NON-NLS-1$
        symbianCaps.add("Location"); //$NON-NLS-1$
        symbianCaps.add("SurroundingsDD"); //$NON-NLS-1$
        symbianCaps.add("UserEnvironment"); //$NON-NLS-1$
        
        for (int shift=0; shift<symbianCaps.size(); shift++){
            if ((allDefinedCapabilitiesAsInt & (1<<(shift&31))) > 0){
            	foundCapabilities.add(symbianCaps.get(shift));            	
            }
        }
        capabilities = (String[]) foundCapabilities.toArray(new String[0]);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append(DESCRIPT_ARR[CacheCompPropertyField.DIRECTORY_ARR_INDX] + ": "); //$NON-NLS-1$
		strBuf.append(directory);			
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.FILENAME_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(filename);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.BINARY_FORMAT_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(binaryFormat);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.UID1_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(uid1);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.UID2_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(uid2);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.UID3_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(uid3);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.SECURE_ID_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(secureId);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.VENDOR_ID_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(vendorId);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.CAPABILITIES_ARR_INDX] + ": \n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < capabilities.length; i++) {
			strBuf.append("\t" + capabilities[i] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		strBuf.append(DESCRIPT_ARR[CacheCompPropertyField.MIN_HEAP_SIZE_ARR_INDX] + ": "); //$NON-NLS-1$
		strBuf.append(minHeapSize);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.MAX_HEAP_SIZE_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(maxHeapSize);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.STACK_SIZE_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(stackSize);
		strBuf.append("\n" + DESCRIPT_ARR[CacheCompPropertyField.DLL_REF_TABLE_COUNT_ARR_INDX] + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		strBuf.append(dllRefTableCount);	
		
		// Returning resulting buffer
		return strBuf.toString();
	}

	/**
	 * Gets binary format for the component.
	 * @return Returns the binaryFormat.
	 */
	public String getBinaryFormat() {
		return binaryFormat;
	}

	/**
	 * Gets capabilities as string array.
	 * @return Returns the capabilities string array.
	 */
	public String[] getCapabilities() {
		return capabilities;
	}

	/**
	 * Gets amount of references in dll reference table
	 * (i.e. amount of direct dependencies for the component).
	 * @return Returns the dllRefTableCount.
	 */
	public String getDllRefTableCount() {
		return dllRefTableCount;
	}

	/**
	 * Gets directory.
	 * @return Returns the directory.
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * Gets file name.
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Gets maximum heap size.
	 * @return Returns the maxHeapSize.
	 */
	public String getMaxHeapSize() {
		return maxHeapSize;
	}

	/**
	 * Gets minimum heap size.
	 * @return Returns the minHeapSize.
	 */
	public String getMinHeapSize() {
		return minHeapSize;
	}

	/**
	 * Gets secure id.
	 * @return Returns the secureId.
	 */
	public String getSecureId() {
		return secureId;
	}

	/**
	 * Gets stack size.
	 * @return Returns the stackSize.
	 */
	public String getStackSize() {
		return stackSize;
	}

	/**
	 * Gets UID1
	 * @return Returns the uid1.
	 */
	public String getUid1() {
		return uid1;
	}

	/**
	 * Gets UID2
	 * @return Returns the uid2.
	 */
	public String getUid2() {
		return uid2;
	}

	/**
	 * Gets UID3
	 * @return Returns the uid3.
	 */
	public String getUid3() {
		return uid3;
	}

	/**
	 * Gets vendor id. 
	 * @return Returns the vendorId.
	 */
	public String getVendorId() {
		return vendorId;
	}

	/**
	 * Converts a selected set of properties into property
	 * data array.
	 * @return Property data array.
	 */
	public PropertyData[] toPropertyDataArray() {
		ArrayList<PropertyData> propDataArrayList = new ArrayList<PropertyData>();
		
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.DIRECTORY_ARR_INDX], directory));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.FILENAME_ARR_INDX], filename));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.BINARY_FORMAT_ARR_INDX], binaryFormat));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.UID1_ARR_INDX], uid1));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.UID2_ARR_INDX], uid2));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.UID3_ARR_INDX], uid3));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.SECURE_ID_ARR_INDX], secureId));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.VENDOR_ID_ARR_INDX], vendorId));

		StringBuffer strBuf = new StringBuffer();	
		for (int i = 0; i < capabilities.length-1; i++) {
			strBuf.append(capabilities[i] + " "); //$NON-NLS-1$
		}
		if(capabilities.length > 0){
			strBuf.append(capabilities[capabilities.length-1]);			
		}
		
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.CAPABILITIES_ARR_INDX], strBuf.toString()));
		
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.MIN_HEAP_SIZE_ARR_INDX], minHeapSize));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.MAX_HEAP_SIZE_ARR_INDX], maxHeapSize));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.STACK_SIZE_ARR_INDX], stackSize));
		propDataArrayList.add(new PropertyData(DESCRIPT_ARR[CacheCompPropertyField.DLL_REF_TABLE_COUNT_ARR_INDX], dllRefTableCount));
		
		// Returning resulting array
		return (PropertyData[]) propDataArrayList.toArray(new PropertyData[0]);
	}

	/**
	 * Gets cache timestamp.
	 * @return Returns the cacheTimestamp.
	 */
	public String getCacheTimestamp() {
		return cacheTimestamp;
	}

	/**
	 * Adds a new used component data item instance for the component.
	 * @param usedCmpData Used component data item instance to be added.
	 */
	public void addUsedComponentData(UsedComponentData usedCmpData){
		// All map keys are stored in lower case
		String componentName = usedCmpData.getComponentName().toLowerCase();
		UsedComponentData usedComponentData = usedComponents.get(componentName);
		if(usedComponentData != null){
			throw new IllegalArgumentException(Messages.getString("ComponentPropertiesData.ComponentAlreadyAdded_ErrMsg_Part1") //$NON-NLS-1$
											   + Messages.getString("ComponentPropertiesData.ComponentAlreadyAdded_ErrMsg_Part2")  //$NON-NLS-1$
											   + componentName + "."); //$NON-NLS-1$
		}
		usedComponents.put(componentName, usedCmpData);
	}
	
	/**
	 * Gets the list of components that are used directly by this component.
	 * The list of components are in the order they have been added into the list.
	 * @return the list of components that are used directly by this component.
	 * @see ComponentPropertiesData#addUsedComponentData
	 */
	public List<UsedComponentData> getUsedComponentList() {
		return new ArrayList<UsedComponentData>(usedComponents.values());
	}

	/**
	 * Checks if this component used the component given as parameter.
	 * @param componentName Name of the component to be checked for usage.
	 * @return <code>true</code> if this component uses the given component, otherwise <code>false</code>.
	 */
	public boolean usesComponent(String componentName) {
		return usedComponents.containsKey(componentName);
	}

	/**
	 * Gets used component data for the given component.
	 * @param componentName Component to get data for.
	 * @return used component data for the given component.
	 */
	public UsedComponentData getUsedComponent(String componentName) {
		if(!usesComponent(componentName)){
			throw new IllegalArgumentException(Messages.getString("ComponentPropertiesData.ComponentIsNotUsed_ErrMsg_Part1")  //$NON-NLS-1$
					+ componentName +Messages.getString("ComponentPropertiesData.ComponentIsNotUsed_ErrMsg_Part2")  //$NON-NLS-1$
					+ getFilename() +").");			 //$NON-NLS-1$
		}
		return usedComponents.get(componentName);
	}

	/**
	 * Returns dllRefTableCount as integer.
	 * @return dllRefTableCount as integer.
	 */
	public int getDllRefTableCountAsInt() {
		return Integer.parseInt(getDllRefTableCount());
	}

	/**
	 * Parsed target platform Id from directory property.
	 * @return target platform, or <code>null</code> if target platform
	 *         cannot be identified for the component.
	 */
	public ITargetPlatform getTargetPlatform(){
		TargetPlatform targetPlatform = null;
		try {
			File f = new File(directory);
			String id = f.getParentFile().getName();
			targetPlatform = new TargetPlatform(id);
		} catch (Exception e) {
			// We might end up here in case filename is not defined
			// e.g. for component that are added from SIS file 
			// in case can be ignored. 
			e.printStackTrace();			
		}
		
		return targetPlatform;
	}
}
