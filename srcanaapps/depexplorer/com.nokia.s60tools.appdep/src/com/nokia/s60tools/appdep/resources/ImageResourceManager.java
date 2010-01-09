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

 
package com.nokia.s60tools.appdep.resources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nokia.s60tools.appdep.plugin.AppDepPlugin;

/**
 * Manages image resources for the tool.
 */
public class ImageResourceManager {

	public static void loadImages(String imagesPath){
		
    	Display disp = Display.getCurrent();
    	
    	ImageRegistry imgReg = AppDepPlugin.getDefault().getImageRegistry();
    	
    	//
    	// Storing images to image registry.
    	//
    	Image img = new Image( disp, imagesPath + "\\appdep.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.IMG_APP_ICON, img );

        img = new Image( disp, imagesPath + "\\select_sdk_action.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.SELECT_SDK, img );
        
    	img = new Image( disp, imagesPath + "\\appdep_wizard_banner.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.WIZARD_BANNER, img );

    	img = new Image( disp, imagesPath + "\\cached_target.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.CACHED_TARGET, img );

    	img = new Image( disp, imagesPath + "\\non_cached_target.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.NON_CACHED_TARGET, img );

    	img = new Image( disp, imagesPath + "\\not_supported_target.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.NOT_SUPPORTED_TARGET, img );
        
    	img = new Image( disp, imagesPath + "\\cache_warning.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.CACHE_WARNING, img );

    	img = new Image( disp, imagesPath + "\\cache_update.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.CACHE_UPDATE_ACTION, img );
        
    	img = new Image( disp, imagesPath + "\\find.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.FIND_ACTION, img );
       
    	img = new Image( disp, imagesPath + "\\folder_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.FOLDER_OBJ, img );
        
        /*******************************************************************************
         * The following pieces of the graphic are taken from from Eclipse 3.1 platform 
         * and CDT project in Eclipse community, which are made available under 
         * the terms of the Eclipse Public License v1.0.
         * A copy of the EPL is provided at http://www.eclipse.org/legal/epl-v10.html
         * 
         * Note that Eclipse 3.1.2 installation was enhanced with CDT 3.0 plug-ins 
         * and many graphics actually comes from CDT.
         * 
         *******************************************************************************/
        
        // Original location of the graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\bin_obj.gif
    	img = new Image( disp, imagesPath + "\\bin_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.BIN_OBJ, img );              
        
        // Original location of the graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\function_obj.gif
    	img = new Image( disp, imagesPath + "\\function_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.FUNCTION_OBJ, img );
        
        // Original location of the graphic:
        // eclipse3.1.2\plugins\org.eclipse.help.webapp_3.1.0\advanced\images\plus.gif
    	img = new Image( disp, imagesPath + "\\expand_subtree.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.EXPAND_SUBTREE, img );

        // Original location of the graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.debug.ui_3.0.2\icons\elcl16\collapseall.gif
    	img = new Image( disp, imagesPath + "\\collapse_all.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.COLLAPSE_ALL_ACTION, img );
        
        /*******************************************************************************
         * The following pieces of the graphic are modified from the graphic taken from Eclipse 3.1 
         * platform and CDT project in Eclipse community, which are made available under 
         * the terms of the Eclipse Public License v1.0.
         * A copy of the EPL is provided at http://www.eclipse.org/legal/epl-v10.html
         *
         * Original location of the graphic (unless otherwise mentioned):
         * eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\bin_obj.gif
         *  
         * Note that Eclipse 3.1.2 installation was enhanced with CDT 3.0 plug-ins 
         * and many graphics actually comes from CDT.
         * 
         *******************************************************************************/
    	img = new Image( disp, imagesPath + "\\bin_obj_error.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.BIN_OBJ_ERROR, img );
	
    	img = new Image( disp, imagesPath + "\\bin_obj_link.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.BIN_OBJ_LINK, img );
	
    	img = new Image( disp, imagesPath + "\\bin_obj_warning.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.BIN_OBJ_WARNING, img );

    	img = new Image( disp, imagesPath + "\\bin_obj_bind.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.BIN_OBJ_BIND, img );          
        
    	img = new Image( disp, imagesPath + "\\new_root_action.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.NEW_ROOT_ACTION, img );
	
    	img = new Image( disp, imagesPath + "\\root_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.ROOT_OBJ, img );
        
    	img = new Image( disp, imagesPath + "\\is_used_by_action.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.IS_USED_BY_ACTION, img );	        
        
    	img = new Image( disp, imagesPath + "\\root_obj_error.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.ROOT_OBJ_ERROR, img );
	
    	img = new Image( disp, imagesPath + "\\expand_all.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.EXPAND_ALL_ACTION, img );

        img = new Image( disp, imagesPath + "\\search.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.SEARCH, img );          
        
        //The image is modified based on following original graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\function_obj.gif
    	img = new Image( disp, imagesPath + "\\virtual_func_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.VIRTUAL_FUNCTION_OBJ, img );
        
        //The image is modified based on following original graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\function_obj.gif
    	img = new Image( disp, imagesPath + "\\traffic_light_green_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.TRAFFIC_LIGHT_GREEN_OBJ, img );
        
        //The image is modified based on following original graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\function_obj.gif
    	img = new Image( disp, imagesPath + "\\traffic_light_yellow_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.TRAFFIC_LIGHT_YELLOW_OBJ, img );
        
        //The image is modified based on following original graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\obj16\function_obj.gif
    	img = new Image( disp, imagesPath + "\\traffic_light_red_obj.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.TRAFFIC_LIGHT_RED_OBJ, img );
        
        //The image is modified based on following original graphic:
        // eclipse3.1.2\plugins\org.eclipse.cdt.ui_3.0.2.5\icons\ovr16\error_co.gif
    	img = new Image( disp, imagesPath + "\\folder_obj_err.png" );        	 //$NON-NLS-1$
        imgReg.put( ImageKeys.FOLDER_OBJ_ERR, img );
	}
	
	/**
	 * Gets image descriptor for the given key.
	 * @param key Image key.
	 * @return image descriptor for the given key
	 */
	public static ImageDescriptor getImageDescriptor( String key ){
    	ImageRegistry imgReg = AppDepPlugin.getDefault().getImageRegistry();
    	return  imgReg.getDescriptor( key );		
	}	

	/**
	 * Gets image for the given key.
	 * @param key Image key.
	 * @return image for the given key
	 */
	public static Image getImage( String key ){
    	ImageRegistry imgReg = AppDepPlugin.getDefault().getImageRegistry();
    	return  imgReg.get(key);		
	}	
}
