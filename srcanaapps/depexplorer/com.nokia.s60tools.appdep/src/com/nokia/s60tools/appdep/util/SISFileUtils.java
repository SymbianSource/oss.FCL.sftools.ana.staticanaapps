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
 
package com.nokia.s60tools.appdep.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * SIS file handling utilities needed by SIS file analysis feature.
 */
public class SISFileUtils {

	// 
	// Constants
	//
	private static final int BYTES_TO_READ_OFFSET = 0;
	private static final int BYTES_TO_READ = 4;
	private static final String FILE_MODE_READ = "r"; //$NON-NLS-1$
	/**
	 * If the first four bytes of the file are 7A1A2010, it is a valid supported SIS file.
	 */
	private static final byte [] FIRST_BYTES = new byte[]{0x07A,0x01A, 0x020, 0x010};

	/**
	 * Checks if file is valid/supported SIS file.
	 * If the first four bytes of the file are 7A1A2010, it is a valid 
	 * and supported SIS file.
	 * @param fileName Absolute path name pointing to SIS file.
	 * @return <code>true</code> if it was valid 9.x SIS file, otherwise <code>false</code>.
	 */
	public static boolean isValid_9x_SISFile(String fileName) {
		
		File file = new File(fileName);
		if(!file.exists()){
			return false;
		}
		try {
			RandomAccessFile randFile = new RandomAccessFile(file, FILE_MODE_READ);
			byte [] b = new byte[BYTES_TO_READ];
			int off = BYTES_TO_READ_OFFSET;
			int len = BYTES_TO_READ;
			int read = randFile.read(b, off, len);
			
			if(read != BYTES_TO_READ){
				return false;
			}
			boolean match = true;
			for (int i = 0; i < b.length; i++) {
				if(b[i] != FIRST_BYTES[i]){
					match = false;
				}
			}
			return match;
		} catch (FileNotFoundException e) {
			//Should newer occur, because files was selected from file list
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			//Should newer occur, because files was selected from file list			
			e.printStackTrace();
			return false;
		}
		
	}

}
