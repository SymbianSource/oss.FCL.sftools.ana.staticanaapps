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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.appdep.ui.views.data.PropertyData;

/**
 * Represents the XML report data selected from "is used by" -view with "Export report..." functionality. 
 */
public class IsUsedByXMLReport {
	
	/**
	 * Data to be converted into XML.
	 */
	ArrayList<ComponentPropertiesData> isUsedByData;
	/**
	 * SDK the data is got from.
	 */
	private String sdkName;
	/**
	 * Targets the data is got from.
	 */
	private String targets;
	/**
	 * Build type the data is got from.
	 */
	private String build;
	/**
	 * Component or functions the 'is used by'-relation is asked for.
	 */
	private String usingComponentOrFunction;
	/**
	 * Sort criteria used in the component list -view used to show 
	 * 'is used by'-relation data, and from which query for XML representation
	 * is started from.
	 */
	private int sortCriteria;

	/**
	 * Constructor.
	 * @param isUsedByData Data to be converted into XML
	 * @param sdkName SDK the data is got from
	 * @param targets Targets the data is got from.
	 * @param build Build type the data is got from.
	 * @param usingComponentOrFunction Component or functions the 'is used by'-relation is asked for.
	 * @param sortCriteria Sort criteria used in the component list -view used to show 
	 *                     'is used by'-relation data, and from which query for XML representation
	 *                     is started from.
	 */
	public IsUsedByXMLReport(ArrayList<ComponentPropertiesData> isUsedByData, String sdkName, String targets, String build, String usingComponentOrFunction, int sortCriteria) {
		this.isUsedByData = isUsedByData;
		this.sdkName = sdkName;
		this.targets = targets;
		this.build = build;
		this.usingComponentOrFunction = usingComponentOrFunction;
		this.sortCriteria = sortCriteria;
	}
	
	/**
	 * Gets component properties for the export report functionality.
	 * @return component properties for the export report functionality.
	 */
	private StringBuffer getExportProperties(){
		
		StringBuffer b = new StringBuffer (); 		
		
		ComponentPropertiesData data;
		Iterator<ComponentPropertiesData> it = isUsedByData.iterator();
		while(it.hasNext()){
			data = it.next();
			PropertyData [] props = data.toPropertyDataArray();
			b.append("<isUsedBy>\n"); //$NON-NLS-1$
			for(int i = 0; i<props.length; i++){
				PropertyData prop =  props[i]; 				
				b.append("<"); //$NON-NLS-1$
				b.append(StringUtils.replace(prop.getPropertyDescription()," ", "_" )); //$NON-NLS-1$ //$NON-NLS-2$
				b.append(">"); //$NON-NLS-1$
				b.append(prop.getPropertyValue());
				b.append("</"); //$NON-NLS-1$
				b.append(StringUtils.replace(prop.getPropertyDescription()," ", "_" )); //$NON-NLS-1$ //$NON-NLS-2$
				b.append(">\n"); //$NON-NLS-1$
			
			}
			b.append("</isUsedBy>\n"); //$NON-NLS-1$
		}		
		
		return b;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){		
			
		StringBuffer b = new StringBuffer();
		
		//*********************************************************************
		//Header
		//*********************************************************************
		
		//	 href=\"printReport.xsl\"
		b.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" ?>");			 //$NON-NLS-1$
		b.append("\n<report>\n"); //$NON-NLS-1$
		b.append("<info><sdk name=\""); //$NON-NLS-1$
		b.append(this.sdkName);
		b.append("\" target=\""); //$NON-NLS-1$
		b.append(this.targets);
		b.append("\" build=\""); //$NON-NLS-1$
		b.append(this.build);
		b.append("\"/></info>"); //$NON-NLS-1$
		b.append("\n");	 //$NON-NLS-1$
		
		b.append("\n"); //$NON-NLS-1$
		b.append("<isUsedByData>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append("<component name=\""); //$NON-NLS-1$
		b.append(usingComponentOrFunction);
		b.append("\" sortCriteria= \""); //$NON-NLS-1$
		b.append(sortCriteria);
		b.append("\">\n"); //$NON-NLS-1$
		
		b.append(getExportProperties());

		b.append("\n"); //$NON-NLS-1$
		b.append("</component>");		 //$NON-NLS-1$
		b.append("</isUsedByData>"); //$NON-NLS-1$
		b.append("\n"); //$NON-NLS-1$
		b.append("</report>"); //$NON-NLS-1$
		b.append("\n");	 //$NON-NLS-1$
		return b.toString();
	}
	
	/**
	 * Write data as XML into given destination file.
	 * @param fileName Destination file.
	 */
	public void toFile( String fileName ) {		
		
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
