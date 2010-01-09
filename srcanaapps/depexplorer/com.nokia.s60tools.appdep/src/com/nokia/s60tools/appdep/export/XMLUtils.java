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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * XML utilities for the tool's export report functionality..
 */
public class XMLUtils {

	
	/**
	 * Processes XML by applying XSL transformation, and stores result to an HTML file.
	 * @param xmlIn XML to process.
	 * @param xslInURI XSL transformation to apply.
	 * @param htmlOutURI Destination HTML file URI.
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public static void parseXML( 
			String xmlIn, String xslInURI,  String htmlOutURI ) 
		throws FileNotFoundException, TransformerException{
		
		String key = "javax.xml.transform.TransformerFactory"; //$NON-NLS-1$
		String value = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"; //$NON-NLS-1$
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);

		// Instantiate the TransformerFactory, and use it with a StreamSource
		TransformerFactory factory = TransformerFactory.newInstance();
		Templates translet = factory.newTemplates(new StreamSource(xslInURI));
		Transformer transformer = translet.newTransformer();
		transformer.transform(new StreamSource(new StringReader(xmlIn)),
				new StreamResult(new FileOutputStream(htmlOutURI)));
	}
	
	/**
	 * Processes XML by applying XSL transformation, and stores result to an HTML file.
	 * The used XSLT file is queried from settings by using the method:
	 *  <code>AppDepSettings.getActiveSettings().getXSLFileName()</code>.
	 * @param xmlIn XML to process.
	 * @param htmlOutURI Destination HTML file URI
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public static void parseXML( String xmlIn, String htmlOutURI ) 
		throws FileNotFoundException, TransformerException{
		
		String key = "javax.xml.transform.TransformerFactory"; //$NON-NLS-1$
		String value = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"; //$NON-NLS-1$
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);

		//Instantiate the TransformerFactory, and use it with a StreamSource
		TransformerFactory factory = TransformerFactory.newInstance();
		String xslPath = AppDepSettings.getActiveSettings().getResourcesPath()
			+ System.getProperty("file.separator") //$NON-NLS-1$
			+ AppDepSettings.getActiveSettings().getXSLFileName();
		DbgUtility.println(DbgUtility.PRIORITY_OPERATION, "XSL path="+xslPath); //$NON-NLS-1$
		Templates translet = factory.newTemplates(new StreamSource(xslPath));
		Transformer transformer = translet.newTransformer();
		transformer.transform(new StreamSource(new StringReader(xmlIn)),
				new StreamResult(new FileOutputStream(htmlOutURI)));
		
	}


}
