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
 
package com.nokia.s60tools.apiquery.shared.util.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.nokia.s60tools.apiquery.shared.datatypes.APIDetailField;
import com.nokia.s60tools.apiquery.shared.datatypes.APIDetails;
import com.nokia.s60tools.apiquery.shared.datatypes.APIShortDescription;
import com.nokia.s60tools.apiquery.shared.datatypes.XMLToUIMappingRules;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * XML Transformation utils for transforming XML data into
 * object formats supported by user interface components.
 * This class contains static utility methods of making 
 * conversion.
 */
public class XMLUtils {
	
	//
	//Public static variables to XML elements 
	//These element names is not ment to show in UI, 
	//but are element names only in XML document.
	//
	

	/**
	 * Element name in XML <code>api</code>
	 */
	public static final String API_ELEMENT = "api"; //$NON-NLS-1$ 
	/**
	 * Element name in XML <code>source</code>
	 */
	public static final String API_ELEMENT_SOURCE_ATTRIBUTE = "source"; //$NON-NLS-1$
	/**
	 * Element name in XML <code>filename</code>
	 */
	public static final String API_ELEMENT_FILENAME = "filename";//$NON-NLS-1$
	/**
	 * Element name in XML <code>name</code>
	 */
	public static final String API_ELEMENT_NAME = "name";//$NON-NLS-1$
	/**
	 * Element name in XML <code>clients</code>
	 */
	public static final String API_ELEMENT_CLIENTS = "clients";//$NON-NLS-1$
	/**
	 * Element name in XML <code>client_org</code>
	 */
	public static final String API_ELEMENT_CLIENT_ORG = "client_org";//$NON-NLS-1$
	/**
	 * Element name in XML <code>note</code>
	 */
	public static final String API_ELEMENT_NOTE = "note";//$NON-NLS-1$
	/**
	 * Element name in XML <code>owner</code>
	 */
	public static final String API_ELEMENT_OWNER = "owner";//$NON-NLS-1$
	/**
	 * Element name in XML <code>base_service</code>
	 */
	public static final String API_ELEMENT_BASE_SERVICE = "base_service";//$NON-NLS-1$
	/**
	 * Element name in XML <code>partner_contacts</code>
	 */
	public static final String API_ELEMENT_PARTNER_CONTACTS = "partner_contacts";//$NON-NLS-1$
	/**
	 * Element name in XML <code>partners</code>
	 */
	public static final String API_ELEMENT_PARTNERS = "partners";//$NON-NLS-1$
	/**
	 * Element name in XML <code>extended_sdk_removed</code>
	 */
	public static final String API_ELEMENT_EXTENDED_SDK_REMOVED = "extended_sdk_removed";//$NON-NLS-1$
	/**
	 * Element name in XML <code>extended_sdk_deprecated</code>
	 */
	public static final String API_ELEMENT_EXTENDED_SDK_DEPRECATED = "extended_sdk_deprecated";//$NON-NLS-1$
	/**
	 * Element name in XML <code>extended_sdk_since</code>
	 */
	public static final String API_ELEMENT_EXTENDED_SDK_SINCE = "extended_sdk_since";//$NON-NLS-1$
	/**
	 * Element name in XML <code>for_adaptation_removed</code>
	 */
	public static final String API_ELEMENT_FOR_ADAPTATION_REMOVED = "for_adaptation_removed";//$NON-NLS-1$
	/**
	 * Element name in XML <code>for_adaptation_deprecated</code>
	 */
	public static final String API_ELEMENT_FOR_ADAPTATION_DEPRECATED = "for_adaptation_deprecated";//$NON-NLS-1$
	/**
	 * Element name in XML <code>for_adaptation_since</code>
	 */
	public static final String API_ELEMENT_FOR_ADAPTATION_SINCE = "for_adaptation_since";//$NON-NLS-1$
	/**
	 * Element name in XML <code>removed</code>
	 */
	public static final String API_ELEMENT_REMOVED = "removed";//$NON-NLS-1$
	/**
	 * Element name in XML <code>deprecated</code>
	 */
	public static final String API_ELEMENT_DEPRECATED = "deprecated";//$NON-NLS-1$
	/**
	 * Element name in XML <code>subsystem</code>
	 */
	public static final String API_ELEMENT_SUBSYSTEM = "subsystem";//$NON-NLS-1$
	/**
	 * Element name in XML <code>other</code>
	 */
	public static final String API_ELEMENT_OTHER = "other";//$NON-NLS-1$
	/**
	 * Element name in XML <code>import_lib</code>
	 */
	public static final String API_ELEMENT_IMPORT_LIB = "import_lib";//$NON-NLS-1$
	/**
	 * Element name in XML <code>dll</code>
	 */
	public static final String API_ELEMENT_DLL = "dll";//$NON-NLS-1$
	/**
	 * Element name in XML <code>specification</code>
	 */
	public static final String API_ELEMENT_SPECIFICATION = "specification";//$NON-NLS-1$
	/**
	 * Element name in XML <code>adaptation</code>
	 */
	public static final String API_ELEMENT_ADAPTATION = "adaptation";//$NON-NLS-1$
	/**
	 * Element name in XML <code>private</code>
	 */
	public static final String API_ELEMENT_PRIVATE = "private";//$NON-NLS-1$
	/**
	 * Element name in XML <code>internal</code>
	 */
	public static final String API_ELEMENT_INTERNAL = "internal";//$NON-NLS-1$
	/**
	 * Element name in XML <code>domain</code>
	 */
	public static final String API_ELEMENT_DOMAIN = "domain";//$NON-NLS-1$
	/**
	 * Element name in XML <code>sdk</code>
	 */
	public static final String API_ELEMENT_SDK = "sdk";//$NON-NLS-1$
	/**
	 * Element name in XML <code>responsible</code>
	 */
	public static final String API_ELEMENT_RESPONSIBLE = "responsible";//$NON-NLS-1$
	/**
	 * Element name in XML <code>type</code>
	 */
	public static final String API_ELEMENT_TYPE = "type";//$NON-NLS-1$
	/**
	 * Element name in XML <code>purpose</code>
	 */
	public static final String API_ELEMENT_PURPOSE = "purpose";//$NON-NLS-1$
	/**
	 * Element name in XML <code>key_name</code>
	 */
	public static final String API_ELEMENT_KEY_NAME = "key_name";//$NON-NLS-1$
	/**
	 * Element name in XML <code>value</code>
	 */
	public static final String API_ELEMENT_VALUE = "value";//$NON-NLS-1$
	/**
	 * Element name in XML <code>header</code>
	 */
	public static final String API_ELEMENT_HEADER = "header";//$NON-NLS-1$	
	
	//
	//Public static variables to UI elements.
	//These variables matches to XML element names, but is ment to show in UI.	
	//	
	
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Clients</code>
	 */
	public static final String DESCRIPTION_CLIENTS = "Clients";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Client Org</code>
	 */
	public static final String DESCRIPTION_CLIENT_ORG = "Client Org";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Note</code>
	 */
	public static final String DESCRIPTION_NOTE = "Note";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Owner</code>
	 */
	public static final String DESCRIPTION_OWNER = "Owner";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Base service</code>
	 */
	public static final String DESCRIPTION_BASE_SERVICE = "Base service";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Partner contacts</code>
	 */
	public static final String DESCRIPTION_PARTNER_CONTACTS = "Partner contacts";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Partners</code>
	 */
	public static final String DESCRIPTION_PARTNERS = "Partners";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Extended SDK Removed</code>
	 */
	public static final String DESCRIPTION_EXTENDED_SDK_REMOVED = "Extended SDK Removed";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Extended SDK Deprecated</code>
	 */
	public static final String DESCRIPTION_EXTENDED_SDK_DEPRECATED = "Extended SDK Deprecated";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Extended SDK Since</code>
	 */
	public static final String DESCRIPTION_EXTENDED_SDK_SINCE = "Extended SDK Since";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>For Adaptation Removed</code>
	 */
	public static final String DESCRIPTION_FOR_ADAPTATION_REMOVED = "For Adaptation Removed";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>For Adaptation Deprecated</code>
	 */
	public static final String DESCRIPTION_FOR_ADAPTATION_DEPRECATED = "For Adaptation Deprecated";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Other</code>
	 */
	public static final String DESCRIPTION_OTHER = "Other";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>LIBs</code>
	 */
	public static final String DESCRIPTION_LIBS = "LIBs";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>DLLs</code>
	 */
	public static final String DESCRIPTION_DLLS = "DLLs";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Specification</code>
	 */
	public static final String DESCRIPTION_SPECIFICATION = "Specification";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Adaptation</code>
	 */
	public static final String DESCRIPTION_ADAPTATION = "Adaptation";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Responsible</code>
	 */
	public static final String DESCRIPTION_RESPONSIBLE = "Responsible";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Type</code>
	 */
	public static final String DESCRIPTION_TYPE = "Type";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Purpose</code>
	 */
	public static final String DESCRIPTION_PURPOSE = "Purpose";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Key Name</code>
	 */
	public static final String DESCRIPTION_KEY_NAME = "Key Name";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Subsystem</code>
	 */
	public static final String DESCRIPTION_SUBSYSTEM = "Subsystem";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Removed</code>
	 */
	public static final String DESCRIPTION_REMOVED = "Removed";	//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>For Adaptation Since</code>
	 */
	public static final String DESCRIPTION_FOR_ADAPTATION_SINCE = "For Adaptation Since";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Deprecated</code>
	 */
	public static final String DESCRIPTION_DEPRECATED = "Deprecated";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Private</code>
	 */
	public static final String DESCRIPTION_PRIVATE = "Private";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Internal</code>
	 */
	public static final String DESCRIPTION_INTERNAL = "Internal";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Domain</code>
	 */
	public static final String DESCRIPTION_DOMAIN = "Domain";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Sdk</code>
	 */
	public static final String DESCRIPTION_SDK = "Sdk";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>API Name</code>
	 */
	public static final String DESCRIPTION_API_NAME = "API Name";//$NON-NLS-1$ 
	/**
	 * XML element name ment to show in UI and used as key in object model <code>Headers</code>
	 */
	public static final String DESCRIPTION_HEADERS = "Headers";//$NON-NLS-1$ 

	//
	// Public service methods
	//
	
	/**
	 * Converts XML data to API details object.
	 * @param dataStrXML XML formatted data.
	 * @return API details object.
	 * @throws IOException 
	 */
	public static APIDetails xmlToAPIDetails(String dataStrXML) throws IOException{
		
		XMLToUIMappingRules convRules = getAPIDetailsConversionRules();

		return extractAPIDetailsData(dataStrXML, convRules);
	}

	/**
	 * Get Conversion rules to get API Details from XML.
	 * @return
	 */
	public static XMLToUIMappingRules getAPIDetailsConversionRules() {
		XMLToUIMappingRules convRules = new XMLToUIMappingRules();
		
		//
		// Defining mapping rules API details
		//
		
		// API name field
		convRules.addRule(API_ELEMENT_NAME, DESCRIPTION_API_NAME); //$NON-NLS-1$ //$NON-NLS-2$
		// Headers field
		Map<String, String> headerAttributes = new LinkedHashMap <String, String>();
		headerAttributes.put(API_ELEMENT_FILENAME, API_ELEMENT_FILENAME); //$NON-NLS-1$ //$NON-NLS-2$
		convRules.addRule(API_ELEMENT_HEADER, DESCRIPTION_HEADERS, headerAttributes); //$NON-NLS-1$ //$NON-NLS-2$
		// Key field
		Map<String, String> keyNameAttributes = new LinkedHashMap <String, String>();
		keyNameAttributes.put(API_ELEMENT_VALUE, API_ELEMENT_VALUE); //$NON-NLS-1$ //$NON-NLS-2$
		convRules.addRule(API_ELEMENT_KEY_NAME, DESCRIPTION_KEY_NAME, keyNameAttributes); //$NON-NLS-1$ //$NON-NLS-2$
		// Other fields
		convRules.addRule(API_ELEMENT_PURPOSE, DESCRIPTION_PURPOSE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_TYPE, DESCRIPTION_TYPE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_RESPONSIBLE, DESCRIPTION_RESPONSIBLE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_SDK, DESCRIPTION_SDK, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_DOMAIN, DESCRIPTION_DOMAIN, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_INTERNAL, DESCRIPTION_INTERNAL, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_PRIVATE, DESCRIPTION_PRIVATE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_ADAPTATION, DESCRIPTION_ADAPTATION, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_SPECIFICATION, DESCRIPTION_SPECIFICATION, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_DLL, DESCRIPTION_DLLS, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_IMPORT_LIB, DESCRIPTION_LIBS, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_OTHER, DESCRIPTION_OTHER, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_SUBSYSTEM, DESCRIPTION_SUBSYSTEM, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_DEPRECATED, DESCRIPTION_DEPRECATED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_REMOVED, DESCRIPTION_REMOVED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_FOR_ADAPTATION_SINCE, DESCRIPTION_FOR_ADAPTATION_SINCE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_FOR_ADAPTATION_DEPRECATED, DESCRIPTION_FOR_ADAPTATION_DEPRECATED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_FOR_ADAPTATION_REMOVED, DESCRIPTION_FOR_ADAPTATION_REMOVED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_EXTENDED_SDK_SINCE, DESCRIPTION_EXTENDED_SDK_SINCE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_EXTENDED_SDK_DEPRECATED, DESCRIPTION_EXTENDED_SDK_DEPRECATED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_EXTENDED_SDK_REMOVED, DESCRIPTION_EXTENDED_SDK_REMOVED, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_PARTNERS, DESCRIPTION_PARTNERS, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_PARTNER_CONTACTS, DESCRIPTION_PARTNER_CONTACTS, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_BASE_SERVICE, DESCRIPTION_BASE_SERVICE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$
		convRules.addRule(API_ELEMENT_OWNER, DESCRIPTION_OWNER, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_NOTE, DESCRIPTION_NOTE, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_CLIENT_ORG, DESCRIPTION_CLIENT_ORG, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		convRules.addRule(API_ELEMENT_CLIENTS, DESCRIPTION_CLIENTS, API_ELEMENT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return convRules;
	}
	
	/**
	 * Converts XML data to API details object.
	 * @param dataStrXML XML formatted data.
	 * @param sourceDescription Description for the source shown in the UI.
	 * @return API details object.
	 * @throws IOException 
	 */
	public static Collection<APIShortDescription> xmlToAPIShortDescription(String dataStrXML, String sourceDescription) throws IOException{
		XMLToUIMappingRules convRules = new XMLToUIMappingRules();

		//
		// Defining mapping rules API summary
		//
		
		// API field is the only element to be parsed
		// The element data contains API name and 
		// API_ELEMENT_SOURCE_ATTRIBUTE contains the
		// source for accessing API details information.
		Map<String, String> apiAttributes = new LinkedHashMap <String, String>();
		apiAttributes.put(API_ELEMENT_SOURCE_ATTRIBUTE, API_ELEMENT_SOURCE_ATTRIBUTE); 
		convRules.addRule(API_ELEMENT, "API", apiAttributes); //$NON-NLS-1$ 
 
		return extractAPIShortDescriptionData(dataStrXML, convRules, sourceDescription);
	}

	/**
	 * Checks that there are no forbidden characters and replaces
	 * with valid ones if needed.
	 * @param in String to be checked.
	 * @return Returns checked and corrected string.
	 */
	public static String replaceForbiddenCharacters(String in){
		String out = in.replace("&","&#38;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("<","&#60;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace(">","&#62;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("\"","&#34;"); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replace("'","&#39;");			 //$NON-NLS-1$ //$NON-NLS-2$
		return out;		
	}
	
	//
	// Internal classes
	// 

	
	//
	// Private methods for the class
	//
	
	/**
	 * Parses the given elements from the XML data
	 * and returns the resulting elements. 
	 * @param XMLData     XML data to be parser.
	 * @param elemNameSet Set of element names that this handler
	 *                    will take into account.
	 * @param attributeMap Element -> Attribute set map containg a set of attributes
	 *                     that should be checked for the element.
	 * @return XML elements found. 
	 * @throws IOException 
	 */
	public static XMLElementData[] parseXML(String XMLData, Set<String> elemNameSet, 
			                                Map<String,Map<String, String>>attributeMap) throws IOException{
		return parseXML(XMLData, elemNameSet, attributeMap, new HashMap<String, String>());
	}
	
	/**
	 * Parses the given elements from the XML data
	 * and returns the resulting elements. 
	 * @param XMLData     XML data to be parser.
	 * @param elemNameSet Set of element names that this handler
	 *                    will take into account.
	 * @param attributeMap Element -> Attribute set map containg a set of attributes
	 *                     that should be checked for the element.
	 * @param parentElementRestrictionMap Parent element restrictions for parsing.
	 * @return XML elements found. 
	 * @throws IOException 
	 */
	public static XMLElementData[] parseXML(String XMLData, Set<String> elemNameSet, 
			                                Map<String,Map<String, String>>attributeMap,
			                                Map<String, String> parentElementRestrictionMap) throws IOException{
		
		// Creates SAX handler that takes information into generic data structure
		XMLDataSAXHandler handler = new XMLDataSAXHandler(elemNameSet, attributeMap, parentElementRestrictionMap);
		
		// Triggering actual XML parsing
		try {		
			InputSource dataSource = createInputSourceFromString(XMLData);

			XMLReader parser = XMLReaderFactory.createXMLReader();		
						
			
			parser.setContentHandler(handler);
			parser.setErrorHandler(handler);
			
			parser.parse(dataSource);
						
		} catch (SAXException e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().printStackTrace(e.getStackTrace());
			throw new IOException ("XML parsing failed" + " (" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (IOException e) {
			e.printStackTrace();
			APIQueryConsole.getInstance().printStackTrace(e.getStackTrace());
			throw e;
		}				
		
		return handler.getParsedElements();
	}
	
	/**
	 * Creates input source from XML data string.
	 * @param XMLData XML data string to be created input source from.
	 * @return Input source from the given string.
	 */
	private static InputSource createInputSourceFromString(String XMLData){
		StringReader rdr = new StringReader(XMLData);
		return new InputSource(rdr); 
	}
		
	/**
	 * Parsed API summary objects from given XML data string.
	 * @param XMLData XML data string to be parsed. 
	 * @param mappingRules Mapping rules used to convert XML elements into 
	 *                     corresponding descriptions.
	 * @param sourceDescription Description for the source shown in the UI.
	 * @return Collection of API summary objects extracted from the XML data.
	 * @throws IOException 
	 */
	private static Collection<APIShortDescription> extractAPIShortDescriptionData(String XMLData, 
																XMLToUIMappingRules mappingRules,
																String sourceDescription) throws IOException{
		
		// Printing with loop-priority because there will be a lot of data printed out
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-"); //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "API Summary data: " + XMLData); //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-"); //$NON-NLS-1$
		
		ArrayList<APIShortDescription> summaryList = new ArrayList<APIShortDescription>();
		
		XMLElementData[] elementArr = parseXML(XMLData, 
											   mappingRules.getMapFromKeySet(),
											   mappingRules.getAttributeMap());

		// Mapping data fields into corresponding API summary fields
		for (int i = 0; i < elementArr.length; i++) {
			// Validating first that we have data that we should have
			if(! elementArr[i].getElementName().equals(API_ELEMENT)){
				throw new RuntimeException("Internal error (XMLUtils): Invalid XML, expected '" + API_ELEMENT + "' element, but encountered: " + elementArr[i].getElementName()); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			if(elementArr[i].getAttributes().size() != 1){
				throw new RuntimeException("Internal error (XMLUtils): Invalid XML, '" + API_ELEMENT + "' should be always attached with '" + API_ELEMENT_SOURCE_ATTRIBUTE + "' attribute"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			}

			// Converting values into API summary
			String source = elementArr[i].getAttributes().get(API_ELEMENT_SOURCE_ATTRIBUTE);
			String value = elementArr[i].getElementContent();	// API name			
			summaryList.add(new APIShortDescription(value, source, sourceDescription));
		}
		
		return summaryList;		
	}

	/**
	 * Parsed API details object from given XML data string.
	 * @param XMLData XML data string to be parsed. 
	 * @param mappingRules Mapping rules used to convert XML elements into 
	 *                     corresponding descriptions.
	 * @return API details object extracted from the XML data.
	 * @throws IOException 
	 */
	public static APIDetails extractAPIDetailsData(String XMLData, 
													XMLToUIMappingRules mappingRules) throws IOException{

		// Printing with loop-priority because there will be a lot of data printed out
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-"); //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "API Details data: " + XMLData); //$NON-NLS-1$
		DbgUtility.println(DbgUtility.PRIORITY_LOOP, "-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-"); //$NON-NLS-1$

		APIDetails details = new APIDetails();
		
		XMLElementData[] elementArr = parseXML(XMLData, 
											   mappingRules.getMapFromKeySet(),
											   mappingRules.getAttributeMap(),
											   mappingRules.getParentElementRestrictionMap());
		
		// Mapping data fields into corresponding API detail fields
		for (int i = 0; i < elementArr.length; i++) {
			XMLElementData data = elementArr[i];
			String description = mappingRules.mapFrom(data.getElementName());
			String value = ""; //$NON-NLS-1$
			Map<String, String> attrData = data.getAttributes();
			boolean wasNamedParameters = false;

			//If there is some attributes...
			if(attrData.size()> 0){
				Map <String,String> attrNames = mappingRules.getAttributeNamesMap(data.getElementName());
				
				Set<String> keys = attrData.keySet();

				// Attribute data overrides element data
				// Attributes are used for <key_name>, and <header> elements.
				boolean first = true;
				
				for (String key : keys) {
					String attr = attrData.get(key);
					String attrKey = getKeyByValue(key, attrNames);
					
					//set parameter description if has one!
					if(attrNames != null && attrNames.containsValue(key) && !key.equals(attrKey)){
						details.addOrUpdateField(key, attr);	
						wasNamedParameters = true;
					}	
					//Comma separated values -->					
					else if(first){
						value = attr;													
						first = false;
					}else{
						value = value + APIDetailField.VALUE_FIELD_SEPARATOR + attr;													
					}																	
				}
				
			}
			else{
				 value = data.getElementContent();
			}
			//If there was only attributes set as own rows, must be checked that if there was some other content as well
			// Either addig or updating an existing field.
			//If there was named parameters, and there was no non-named parameters, then element it self is
			//not ment to occur in UI, only independent parameters.
			if(!wasNamedParameters || value.trim().length() > 0){
				details.addOrUpdateField(description, value);
			}
		}
		
		return details;
	}

	/**
	 * Get XML element name by UI element name.
	 * @param value
	 * @param attrNames
	 * @return
	 */
	private static String getKeyByValue(String value,
			Map<String, String> attrNames) {
		Set<String> keys = attrNames.keySet();
		for (String key : keys) {
			String value_ = attrNames.get(key);
			if(value_.equals(value)){
				return key;
			}
		}
		return null;
	}

}
