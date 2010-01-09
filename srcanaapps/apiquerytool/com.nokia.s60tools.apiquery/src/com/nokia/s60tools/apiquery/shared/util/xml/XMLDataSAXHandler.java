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

import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * SAX parser callback class that parses the XML into
 * into XMLElementData object array. The set of elements
 * and attributes to be parsed are specified in constructor.
 * 
 * This class can parse elements that do not themselves contain 
 * any child elements. I.e. the elements to be parsed must be 
 * of flat XML. The parsed elements themselves can, of course, 
 * be contained by some parent element(s) that are not parsed 
 * by this handler.
 */
public class XMLDataSAXHandler extends DefaultHandler{

	/**
	 * Set of element names we are interested in.
	 * Other elements are ignored.
	 */
	private final Set<String> elemNameSet;
	
	/**
	 * Vector for storing the parsed elements.
	 */
	private final Vector<XMLElementData> parsedElementsVector;
	
	/**
	 * Flag is set to true when we are inside an element that is recognized.
	 */
	private boolean isInsideAcceptedElement = false;
	
	/**
	 * Name for the element currently under parsing.
	 */
	private String parsedElementName = null;
	
	/**
	 * Value for the element currently under parsing.
	 */
	private String parsedElementValue = null;

	/**
	 * Array for storing attribute data found for the element.
	 */
	private Map<String, String> parsedAttributeData = null;
	
	/**
	 * Element -> Attribute set map containg attributes
	 * that should be checked for the element.
	 */
	private final Map<String, Map<String, String>> attributeMap;

	/**
	 * Parent restrictions for the elements.
	 */
	private final Map<String, String> parentElementRestrictionMap;	

	/**
	 * Storing elements to stack in order to resolve parent
	 * for the current element.
	 */
	private final Stack<String> elementStack;	

	/**
	 * Constructor.
	 * @param elemNameSet Set of element names that this handler
	 *                    will take into account.
	 * @param attributeMap Element -> Attribute set map containg attributes
	 *                     that should be checked for the element.
	 * @param parentElementRestrictionMap Parent element restrictions for parsing.
	 */
	public XMLDataSAXHandler(Set<String> elemNameSet, Map<String,Map<String, String>> attributeMap, 
			                 Map<String, String> parentElementRestrictionMap){
		this.elemNameSet = elemNameSet;
		this.attributeMap = attributeMap;
		this.parentElementRestrictionMap = parentElementRestrictionMap;
		parsedElementsVector = new Vector<XMLElementData>();
		parsedAttributeData = new LinkedHashMap<String, String>(); 
		elementStack = new Stack<String>();	
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		
		//Storing parent for the current element
		String parentElement = null;
		try {
			parentElement = elementStack.peek();
		} catch (EmptyStackException e) {
			// Ignoring this exception
		}
		// Parent element is stored => We can add current element to stack.
		elementStack.push(localName);
		
		if(elemNameSet.contains(localName)){
			
			// Checking if possible parent element 
			// restriction should be taken into account.
			boolean parentElementCheckOk = true; // By default there is no restrictions
			String parentRestriction = parentElementRestrictionMap.get(localName);
			if(parentRestriction != null){
								
				if(parentElement == null || (! parentElement.equalsIgnoreCase(parentRestriction))){
					// Parent restriction is required, and does not match
					parentElementCheckOk = false;
				}
			}		
			
			if(parentElementCheckOk){
				// This is an element we are interested in
				isInsideAcceptedElement = true;
				parsedElementName = localName;
				
				// Checking for possible attributes
				Map<String, String> attrMap = attributeMap.get(localName);
				if(attrMap != null && attrMap.size() > 0){

					Set<String> attrSet = attrMap.keySet();			
					for (int i = 0; i < attributes.getLength(); i++) {
						String attrName = attributes.getLocalName(i);				
						if(attrSet.contains(attrName)){
							String attrValue = attributes.getValue(i);
							parsedAttributeData.put(attrMap.get(attrName), attrValue);
						}
					}//for
				}				
			}			
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		String characterDataString = new String(ch, start, length);

		if(isInsideAcceptedElement){
			if(parsedElementValue == null){
				parsedElementValue = characterDataString;				
			}
			else{
				parsedElementValue = parsedElementValue + characterDataString;								
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		elementStack.pop(); // Updating element stack
		
		if(isInsideAcceptedElement){
			// Adding element into parsed elements
			if(parsedElementValue == null){
				// If element did not contain value => using an empty string instead
				parsedElementValue = ""; //$NON-NLS-1$
			}
			parsedElementsVector.add(new XMLElementData(parsedElementName, 
					                 					parsedElementValue,
					                 					parsedAttributeData));
			isInsideAcceptedElement = false;
			parsedElementName = null;
			parsedElementValue = null;
			// For the next element there is competely new data object reserved
			parsedAttributeData = new LinkedHashMap<String, String>();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {			
		String xmlParseFailedUserMsg = Messages.getString("XMLDataSAXHandler.XML_Parse_Error_ConsoleMsg" //$NON-NLS-1$
		                                                  + "( " + getCurrentParsePath() + ")");		 //$NON-NLS-1$ //$NON-NLS-2$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
				xmlParseFailedUserMsg);
		APIQueryConsole.getInstance().println(xmlParseFailedUserMsg + e.getMessage(), APIQueryConsole.MSG_ERROR);
		throw new SAXException(e.getMessage());			
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		String xmlParseFailedUserMsg = Messages.getString("XMLDataSAXHandler.XML_Parse_FatalError_ConsoleMsg") //$NON-NLS-1$
														  + "( " + getCurrentParsePath() + ")";  //$NON-NLS-1$ //$NON-NLS-2$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
				xmlParseFailedUserMsg);
		APIQueryConsole.getInstance().println(xmlParseFailedUserMsg + e.getMessage(), APIQueryConsole.MSG_ERROR);
		throw new SAXException(e.getMessage());			
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException e) throws SAXException {
		String xmlParseFailedUserMsg = Messages.getString("XMLDataSAXHandler.XML_Parse_Warning_ConsoleMsg" //$NON-NLS-1$ 
				+ "( " + getCurrentParsePath() + ")");  //$NON-NLS-1$ //$NON-NLS-2$
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, 
				xmlParseFailedUserMsg);
		APIQueryConsole.getInstance().println(xmlParseFailedUserMsg + e.getMessage(), APIQueryConsole.MSG_WARNING);
	} 
	
	/**
	 * Element data that was parsed.
	 * @return Elements parsed.
	 */
	public XMLElementData[] getParsedElements(){
		return parsedElementsVector.toArray(new XMLElementData[0]);
	}

	/**
	 * Returns current parse path based on the information stored in the stack.
	 */
	private String getCurrentParsePath(){
		StringBuffer path = new StringBuffer("/"); //$NON-NLS-1$
		for (ListIterator<String> iter = elementStack.listIterator(); iter.hasNext();) {
			String elemName = (String) iter.next();
			path.append(elemName + "/");			 //$NON-NLS-1$
		}
		return path.toString();
	}
	
}
