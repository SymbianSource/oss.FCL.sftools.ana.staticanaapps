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
 
package com.nokia.s60tools.apiquery.cache.xml;


import java.util.LinkedHashMap;
import java.util.Map;

import com.nokia.s60tools.apiquery.cache.resources.Messages;
import com.nokia.s60tools.apiquery.shared.datatypes.XMLToUIMappingRules;


/**
 * XML to UI Mapping rules for .metaxml files.
 */
public class MetadataXMLToUIMappingRules extends XMLToUIMappingRules {
	
	private static final String NAME_PARAM = "name"; //$NON-NLS-1$

	//
	// UI Texts
	//	
	public static final String RELEASE_DEPRECATED_SINCE_VERSION = Messages.getString("MetadataXMLToUIMappingRules.DeprecatedSince"); //$NON-NLS-1$
	public static final String RELEASE_SINCE_VERSION = Messages.getString("MetadataXMLToUIMappingRules.ReleaseSince"); //$NON-NLS-1$
	public static final String RELEASE_CATEGORY = Messages.getString("MetadataXMLToUIMappingRules.ReleaseCategory"); //$NON-NLS-1$
	public static final String ADAPTATION = Messages.getString("MetadataXMLToUIMappingRules.Adaptiotion"); //$NON-NLS-1$
	public static final String HTML_DOC_PROVIDED = Messages.getString("MetadataXMLToUIMappingRules.HtmlDocProvided"); //$NON-NLS-1$
	public static final String SUBSYSTEM = Messages.getString("MetadataXMLToUIMappingRules.Subsystem"); //$NON-NLS-1$
	public static final String TYPE = Messages.getString("MetadataXMLToUIMappingRules.Type"); //$NON-NLS-1$
	public static final String DESCRIPTION = Messages.getString("MetadataXMLToUIMappingRules.Description"); //$NON-NLS-1$
	public static final String NAME = Messages.getString("MetadataXMLToUIMappingRules.Name"); //$NON-NLS-1$
	public static final String LIBS = Messages.getString("MetadataXMLToUIMappingRules.Libs"); //$NON-NLS-1$
	public static final String HEADERS = Messages.getString("MetadataXMLToUIMappingRules.Headers"); //$NON-NLS-1$
	
	//Extended SDK is removed in metadata version 2.0.
	public static final String EXTENDED_SDK = Messages.getString("MetadataXMLToUIMappingRules.ExtendedSDK"); //$NON-NLS-1$
	
	
	/**
	 * Collection replaces {@link MetadataXMLToUIMappingRules#SUBSYSTEM} in metadata version 2.0.
	 */
	public static final String COLLECTION = Messages.getString("MetadataXMLToUIMappingRules.Collection"); //$NON-NLS-1$

	public MetadataXMLToUIMappingRules(){
		init();
	}

	/**
	 * Set rules for mapping .metaxml file to UI.
	 * 
	 */
	private void init() {
		
		/*
		 * 1.0 version of XML:
		 * <code>
		 * <?xml version="1.0" ?>
		 * <api id="3d801aa532c1cec3ee82d87a99fdf63f" dataversion="1.0">
		 * <name>temp</name>
		 * <description>temp</description>
		 * <type>c++</type>
		 * <subsystem>temp</subsystem>
		 * <libs><lib name="aa.lib"/>
		 * </libs>
		 * <release category="sdk" sinceversion="1.0" deprecatedsince="1.2"/>
		 * <attributes>
		 * <htmldocprovided>yes</htmldocprovided>
		 * <adaptation>yes</adaptation>
		 * <extendedsdk sinceversion="1.0" deprecatedsince="1.2"/>
		 * </attributes>
		 * </api>
		 * </code>
		 * 2.0 version of XML
		 * 
		 * <?xml version="1.0" ?>
		 * <api id="3d801aa532c1cec3ee82d87a99fdf63f" dataversion="1.0">
		 * <name>temp</name>
		 * <description>temp</description>
		 * <type>c++</type>
		 * <collection>temp</collection>
		 * <libs><lib name="aa.lib"/>
		 * </libs>
		 * <release category="public" sinceversion="1.0" deprecatedsince="1.2"/>
		 * <attributes>
		 * <htmldocprovided>yes</htmldocprovided>
		 * <adaptation>yes</adaptation>
		 * </attributes>
		 * </api>
		 */

		addRule(NAME_PARAM, NAME);
		addRule("description", DESCRIPTION);//$NON-NLS-1$		
		addRule("type", TYPE);//$NON-NLS-1$		
		addRule("subsystem", SUBSYSTEM);//$NON-NLS-1$		
		addRule("collection", COLLECTION);//$NON-NLS-1$		//Collection replaces subsystem in version 2.0
		
		//Libs parameters
		Map<String, String> libAttributes = new LinkedHashMap <String,String>();
		libAttributes.put(NAME_PARAM, NAME_PARAM); //$NON-NLS-1$		
		addRule("lib", LIBS, "libs", libAttributes);//$NON-NLS-1$		//$NON-NLS-2$		
		
		//release parameters
		//@see e.g. Z:\s60\mw\cameraengines\cameraengines_dom\custom_onboard_camera_api\custom_onboard_camera_api.metaxml for release attributes		
		Map<String, String> releasAttributes = new LinkedHashMap <String, String>();
		releasAttributes.put("category", RELEASE_CATEGORY); //$NON-NLS-1$		//$NON-NLS-2$		
		releasAttributes.put("sinceversion", RELEASE_SINCE_VERSION); //$NON-NLS-1$ //$NON-NLS-2$		
		releasAttributes.put("deprecatedsince", RELEASE_DEPRECATED_SINCE_VERSION); //$NON-NLS-1$ //$NON-NLS-2$				
		addRule("release", Messages.getString("MetadataXMLToUIMappingRules.Release"), releasAttributes); //$NON-NLS-1$ //$NON-NLS-2$

		addRule("htmldocprovided", HTML_DOC_PROVIDED);//$NON-NLS-1$		
		addRule("adaptation", ADAPTATION);//$NON-NLS-1$		

		//Extended SDK parameters
		//Extended SDK is removed in metadata version 2.0.
		//Developer edited by hand file: Z:\s60\mw\ahle\ahle_dom\adaptive_history_list_api\adaptive_history_list_api.metaxml Where is extended SDK params to tests - This comment can be removed.
		Map<String, String> extendedAttributes = new LinkedHashMap <String, String>();
		extendedAttributes.put("sinceversion", "Extended SDK since version"); //$NON-NLS-1$ //$NON-NLS-2$		
		extendedAttributes.put("deprecatedsince", "Extended SDK deprecated since version"); //$NON-NLS-1$ //$NON-NLS-2$		
		addRule("extendedsdk", EXTENDED_SDK, extendedAttributes);//$NON-NLS-1$		
		
		
	}

}
