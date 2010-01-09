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

import com.nokia.s60tools.appdep.core.model.ImportFunctionData;
import com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler;

/**
 * Clipboard copy handler for import functions tab.
 */
public class ImportFunctionsClipboardCopyHandler extends AbstractTextClipboardCopyHandler {
	
	/**
	 * Function data to be copied.
	 */
	ImportFunctionData[] funcDataArr = null;

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.ui.AbstractTextClipboardCopyHandler#buildTextString()
	 */
	protected String buildTextString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (int i = 0; i < funcDataArr.length; i++) {
			ImportFunctionData f = funcDataArr[i];
			strBuf.append(f.getFunctionOrdinal() + "\t"); //$NON-NLS-1$
			strBuf.append(f.getFunctionName());
			if(f.isVirtual()){
				strBuf.append("\t" + f.getFunctionOffset() + "\r\n");				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			else{
				strBuf.append("\r\n");								 //$NON-NLS-1$
			}
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
				if(!(objectsToCopy.get(0) instanceof ImportFunctionData)){
					return false;
				}
			}
			else{
				// No objects to copy
				return false;
			}
			funcDataArr = (ImportFunctionData[]) objectsToCopy.toArray(new ImportFunctionData[0]);
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
