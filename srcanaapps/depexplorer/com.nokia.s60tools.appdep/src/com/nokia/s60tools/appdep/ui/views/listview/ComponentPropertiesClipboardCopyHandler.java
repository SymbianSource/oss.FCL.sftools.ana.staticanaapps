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
 
 
package com.nokia.s60tools.appdep.ui.views.listview;

import java.util.List;

import com.nokia.s60tools.appdep.core.model.ComponentPropertiesData;
import com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler;

/**
 * Clipboard copy handler for component properties.
 */
public class ComponentPropertiesClipboardCopyHandler extends AbstractTextClipboardCopyHandler{
	
	/**
	 * Properties data to be copied.
	 */
	ComponentPropertiesData[] propDataArr = null;

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler#buildTextString()
	 */
	protected String buildTextString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (int i = 0; i < propDataArr.length; i++) {
			ComponentPropertiesData prop = propDataArr[i];
			
			strBuf.append(prop.getFilename() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getBinaryFormat() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getUid1() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getUid2() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getUid3() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getSecureId() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getVendorId() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getMinHeapSize() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getMaxHeapSize() + "\t"); //$NON-NLS-1$
			strBuf.append(prop.getStackSize() + "\r\n"); //$NON-NLS-1$
			
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
				if(!(objectsToCopy.get(0) instanceof ComponentPropertiesData)){
					return false;
				}
			}
			else{
				// No objects to copy
				return false;
			}
			propDataArr = (ComponentPropertiesData[]) objectsToCopy.toArray(new ComponentPropertiesData[0]);
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
