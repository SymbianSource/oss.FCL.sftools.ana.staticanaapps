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
 
 
package com.nokia.s60tools.appdep.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.preferences.DEPreferencePage;

/**
 * Dialog for selecting real implementation of selected generic component 
 * @see org.eclipse.jface.dialogs.Dialog
 */
public class LocateComponentDialog extends Dialog {
		

	/**
	 * List box dialog text control for showing the content.
	 */
	private List componentsList = null;
	
	/**
	 * Components to be shown in the dialog.
	 */
	private String[] components = null;	

	/**
	 * Index of the selection.
	 */
	private int selectionIndex;
	
	// Fixed Width and height parameters to set UI components 
	// precisely 
	private static final int COMPONENT_LIST_ITEMS_HEIGHT_HINT = 8;		
	public static final int COMPONENT_LIST_WIDTH = 300;
	
	/**
	 * Generic component.
	 */
	private String componentName = null;

	/**
	 * Constructor
	 * @param parentShell Parent shell
	 * @param components Component array.
	 * @param componentName Name of the generic component.
	 */
	public LocateComponentDialog(Shell parentShell, String[] components, String componentName) {
		super(parentShell);
		this.components = components;
		this.componentName = componentName;
	}
	
	/**
	 * Constructor
	 * @param parentShell Parent shell.
	 */
	@SuppressWarnings("unused")
	private LocateComponentDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Constructor
	 * @param parentShell Parent shell provider.
	 */
	@SuppressWarnings("unused")
	private LocateComponentDialog(IShellProvider parentShell) {
		super(parentShell);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Creating just OK button
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL,
                true);     
		Button ok = getButton(IDialogConstants.OK_ID);
		ok.setEnabled(false);
        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText( Messages.getString("LocateComponentDialog.Shell_Txt") );//$NON-NLS-1$ 
    }    
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);			

		//Label
		Label label = new Label(dialogAreaComposite,SWT.HORIZONTAL);
		label.setText(Messages.getString("LocateComponentDialog.Label_Txt") +" "  +componentName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$				

		//Layout data for the component list
		final int cols = 1;	  
		GridLayout gdl = new GridLayout(cols, false);
		GridData gd = new GridData(GridData.FILL_BOTH);

		dialogAreaComposite.setLayout(gdl);
		dialogAreaComposite.setLayoutData(gd);
	
		GridData gd2 = new GridData(GridData.FILL_BOTH);
	
		dialogAreaComposite.setLayoutData(gd2);

		//Controls for the component list
		final int listBoxStyleBits = SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
		componentsList = new List(dialogAreaComposite,listBoxStyleBits);
		int listHeight = componentsList.getItemHeight() * COMPONENT_LIST_ITEMS_HEIGHT_HINT;
		Rectangle trim = componentsList.computeTrim(0, 0, 0, listHeight);
		GridData listData = new GridData(COMPONENT_LIST_WIDTH, SWT.DEFAULT);
		listData.horizontalAlignment = GridData.FILL;
		listData.grabExcessHorizontalSpace = true;
		listData.verticalAlignment = GridData.FILL;
		listData.grabExcessVerticalSpace = true;
		listData.heightHint = trim.height;		
		
		listData.heightHint = trim.height;		
		componentsList.setLayoutData(listData);
		if(components != null){
			componentsList.setItems(components);
		}
		
		// Adding selection listener for the component list
		componentsList.addSelectionListener(new SelectionListener(){
			
			public void widgetDefaultSelected(SelectionEvent e) {
				//Not needed
			}

			public void widgetSelected(SelectionEvent e) {
				selectionIndex = componentsList.getSelectionIndex();
				if(selectionIndex != -1){
					setOKEnabled();					
				}
			}			
		});		
		
		//Button for opening preferences page
		Link openPreferencePageLink = new Link(dialogAreaComposite, SWT.NULL);
		openPreferencePageLink.setText("<a>" + Messages.getString("LocateComponentDialog.Link_Txt") +"</a>");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		openPreferencePageLink.setToolTipText(Messages.getString("LocateComponentDialog.Link_ToolTip_Txt"));//$NON-NLS-1$
		GridData btnData = new GridData();
		openPreferencePageLink.setLayoutData(btnData);
		
		//listener for the add button
		openPreferencePageLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				openPreferencePage();
			}
		});				
 				
	    // Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(dialogAreaComposite, AppDepHelpContextIDs.APPDEP_LOCATE_COMPONENT_DIALOG);
	    
		return dialogAreaComposite;
	}    
	
	/**
	 * Sets OK button enabled
	 */
	private void setOKEnabled(){
		Button ok = getButton(IDialogConstants.OK_ID);
		ok.setEnabled(true);		
	}
	
	/**
	 * Opens Component prefix search order preference page.
	 */
	private void openPreferencePage(){
		   IPreferencePage page = new DEPreferencePage();
		   PreferenceManager mgr = new PreferenceManager();
		   IPreferenceNode node = new PreferenceNode("1", page);//$NON-NLS-1$
		   mgr.addToRoot(node);
		   PreferenceDialog dialog = new PreferenceDialog(getShell(), mgr);
		   dialog.create();
		   dialog.setMessage(page.getTitle());
		   dialog.open();		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open(){
		selectionIndex=-1;
		return super.open();
	}
	
	/**
	 * Get selection index from the concrete component list.
	 * @return index of selected component or -1 if not selected
	 */
	public int getSelectionIndex(){
		return selectionIndex;
	}	
	
}
