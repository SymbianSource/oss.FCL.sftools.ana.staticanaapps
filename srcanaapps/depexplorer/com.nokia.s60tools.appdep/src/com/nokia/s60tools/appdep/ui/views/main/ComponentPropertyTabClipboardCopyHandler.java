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

import java.util.List;

import com.nokia.s60tools.appdep.ui.views.data.PropertyData;
import com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler;

/**
 * Clipboard copy handler for component properties tab.
 */
public class ComponentPropertyTabClipboardCopyHandler extends AbstractTextClipboardCopyHandler {
	
	/**
	 * Property data to be copied.
	 */
	PropertyData[] propDataArr = null;

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler#buildTextString()
	 */
	protected String buildTextString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (int i = 0; i < propDataArr.length; i++) {
			PropertyData propData = propDataArr[i];
			strBuf.append(propData.getPropertyDescription()
					      + "\t" //$NON-NLS-1$
	  				      + propData.getPropertyValue()
					      + "\r\n" //$NON-NLS-1$
					      );								
		}
		return strBuf.toString();
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.ICopyActionHandler#acceptAndCopy(java.util.List)
	 */
	public boolean acceptAndCopy(List<Object> objectsToCopy) {
		
		try {
			// Trying avoid unnecessary exceptions
			
			if(objectsToCopy.size() > 0){
				if(!(objectsToCopy.get(0) instanceof PropertyData)){
					return false;
				}
			}
			else{
				// No objects to copy
				return false;
			}
			propDataArr = (PropertyData[]) objectsToCopy.toArray(new PropertyData[0]);
			// Class cast succeeded, and we can perform copy
			performCopy();
			return true;
		} catch (ClassCastException e) {
			// We can ignore this, this means just that we d
			// do not suppor the given object type
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
