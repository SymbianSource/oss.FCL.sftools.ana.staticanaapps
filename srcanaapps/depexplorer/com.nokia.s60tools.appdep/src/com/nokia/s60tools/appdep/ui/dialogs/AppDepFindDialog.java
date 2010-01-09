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
 
 
package com.nokia.s60tools.appdep.ui.dialogs;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.core.IAppDepSettingsChangedListener;
import com.nokia.s60tools.appdep.core.data.ComponentNode;
import com.nokia.s60tools.appdep.find.FindVisitor;
import com.nokia.s60tools.appdep.find.IFindStartNodeProvider;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.ui.views.data.IVisitor;
import com.nokia.s60tools.appdep.ui.views.main.MainView;

/**
 * 
 * Implements find dialog for fetching components or
 * imported/exported functions. 
 *
 * Usage example:
 *
 * <code>
 * <pre>
 * 
 * // Parent shell
 * Shell sh = Display.getCurrent().getActiveShell()
 * 
 * ComponentParentNode rootNode = view.getRootComponentNode();
 * AppDepFindDialog dlg = new AppDepFindDialog(sh, rootNode);
 * dlg.create();
 * dlg.open();			
 *  
 * </pre> 
 * </code>
 * 
 * @see org.eclipse.jface.dialogs.Dialog
 */
public class AppDepFindDialog extends TrayDialog implements SelectionListener,
														  ModifyListener, 
														  IAppDepSettingsChangedListener{
	//
	// Constants
	//
	
	/**
	 * Default width.
	 */
	private static final int DEFAULT_WIDTH = 300;

	/**
	 * Default height.
	 */
	private static final int DEFAULT_HEIGHT = 50;
	
	/**
	 * Default column count for grid layouts.
	 */
	private final int DEFAULT_COLUMN_COUNT = 1;

	/**
	 * Find button ID
	 */
	private static final int FIND_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;
	
    /**
     * 'Find Nex't button finding for next matching component.
     */
	private Button findNextButton;
    
    /**
     * Close button closes the dialog.
     */
	private Button closeButton;
	
	/**
	 * Find string entering field. 
	 */
	private Text findStringTxtField;
	
	/**
	 * Start node provider for the find. Needed because component
	 * tree can change while dialog is opened.
	 */
	private final IFindStartNodeProvider startNodeProvider;

	/**
	 * Reference to main view showing the actual data to find for.
	 */
	private final MainView view;
		
	/**
	 * List storing the latest find values. Set to <code>null</code> 
	 * if there are no currently active find results.
	 */
	private List<ComponentNode> foundComponentsList = null;
	
	/**
	 * Points to the index in found component list 
	 * that is activated for user when 'Find Next'
	 * is pressed for next time. 
	 */
	private int foundComponentIndex = 0;
	
	/**
	 * Reference to current settings in order to listen for setting change events.
	 */
	private AppDepSettings currentSettings;
	
	/**
	 * Constructor.
	 * @param parentShell Parent shell.
	 * @param startNodeProvider Start node provider for the find.
	 * @param view Reference to main view.
	 */
	public AppDepFindDialog(Shell parentShell, 
							  IFindStartNodeProvider startNodeProvider,
			                  MainView view) {
		super(parentShell);
		this.startNodeProvider = startNodeProvider;
		this.view = view;
        setShellStyle(parentShell.getStyle() | SWT.MODELESS);
        currentSettings = AppDepSettings.getActiveSettings();
        currentSettings.addSettingsListener(this);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		// Creating dialog area composite
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);		
	
		//
		// Defining dialog area layout
		//
		GridLayout gdl = new GridLayout(DEFAULT_COLUMN_COUNT, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = DEFAULT_WIDTH;
		gd.heightHint = DEFAULT_HEIGHT;
		gd.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		gd.verticalIndent = IDialogConstants.VERTICAL_MARGIN;
		dialogAreaComposite.setLayout(gdl);
		dialogAreaComposite.setLayoutData(gd);		
		
		//
		// Adding find label and find text box
		//
		Composite findFieldsComposite = new Composite(dialogAreaComposite, SWT.NONE);
		final int findFieldsColumnCount = 2;
		GridLayout gdlComposite = new GridLayout(findFieldsColumnCount, false);
		GridData gdComposite = new GridData(GridData.FILL_HORIZONTAL);
		findFieldsComposite.setLayout(gdlComposite);
		findFieldsComposite.setLayoutData(gdComposite);
		
		// Find string label
		Label findStringTxtFieldLabel = new Label(findFieldsComposite, SWT.HORIZONTAL);		
		findStringTxtFieldLabel.setText(Messages.getString("AppDepFindDialog.FindWhat_FindDialog_LabelText")); //$NON-NLS-1$

		// Find string input fields
		final int textFieldStyleBits = SWT.LEFT | SWT.SINGLE | SWT.BACKGROUND | SWT.BORDER;
		findStringTxtField = new Text(findFieldsComposite, textFieldStyleBits);
		findStringTxtField.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
		findStringTxtField.setEditable(true);
		findStringTxtField.addModifyListener(this);
		findStringTxtField.addSelectionListener(this);
				
		//
		// And finally adding separator above the dialog button array
		// 
		Label separatorLine = new Label(dialogAreaComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separatorLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		
	    // Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(findStringTxtField, AppDepHelpContextIDs.APPDEP_FIND_DIALOG);
	      
		return dialogAreaComposite;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.getString("AppDepFindDialog.Find_Components_FindDialog_TitleText"));  //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Creating Find button
		findNextButton = createButton(parent, FIND_BUTTON_ID, Messages.getString("AppDepFindDialog.FindNext_FindDialog_ButtonText"),true); //$NON-NLS-1$
		findNextButton.setEnabled(false); // By default disabled
		findNextButton.addSelectionListener(this);
        //Creating Close button
		closeButton = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
		closeButton.addSelectionListener(this);
		
	    // Setting context help IDs		
	    AppDepPlugin.setContextSensitiveHelpID(findNextButton, AppDepHelpContextIDs.APPDEP_FIND_DIALOG);
	    AppDepPlugin.setContextSensitiveHelpID(closeButton, AppDepHelpContextIDs.APPDEP_FIND_DIALOG);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		if(e.widget == findStringTxtField){
			//  <code>widgetDefaultSelected</code> is typically called 
			// when ENTER is pressed in a single-line text.
			if(findStringTxtField.getText().length() > 0){
				// Searching only when there is valid search string
				performFindNextRunnableWrapper();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Widget w = e.widget;
		
		if(w.equals(findNextButton)){
			performFindNextRunnableWrapper();
		}		
		else if(w.equals(closeButton)){
			// Otherwise it must be Close button
			// Stop listening setting changes...
			currentSettings.removeSettingsListener(this);
			// ... and closing the dialog
			this.close();
		}
		
	}

	/**
	 * Wraps find next functionality inside runnable for showing busy cursor.
	 */
	private void performFindNextRunnableWrapper() {
		Runnable findNextQueryRunnable = new Runnable(){
			public void run(){
				performFindNext();
			}
		};
		
		// Showing busy cursor during operation			
		Display d = getShell().getDisplay();
		BusyIndicator.showWhile(d, findNextQueryRunnable);
	}
       
	/**
	 * Performs the find next based on the current find context.
	 */
	private void performFindNext() {
		
		// Do we have existing find context?
		if(foundComponentsList == null){
			// No we do not have => making new find
			String searchString = findStringTxtField.getText();
			foundComponentsList = findComponents(startNodeProvider.getSearchStartNode(), 
					                                           searchString);			
			// Did we found anything
			if(foundComponentsList.size() == 0){
				new AppDepMessageBox(Messages.getString("AppDepFindDialog.Cannot_Find_Components_FindDialog_InfoMsg")  //$NON-NLS-1$
						              + " \"" + searchString + "\".",  //$NON-NLS-1$ //$NON-NLS-2$
						              SWT.ICON_INFORMATION | SWT.OK).open();
				resetFind();
				return;
			}
		}
		
		// Do we have ended our search?
		if(foundComponentIndex == foundComponentsList.size()){
			// End reached
			AppDepMessageBox mbox = new AppDepMessageBox(Messages.getString("AppDepFindDialog.EndOfTreeReached_ContinueFromBeginningQuery_FindDialog_InfoQueryMsg"), SWT.ICON_INFORMATION | SWT.YES | SWT.NO); //$NON-NLS-1$
			int response = mbox.open();
			if(response == SWT.YES){
				// Resettings the index => find starts from beginning
				foundComponentIndex = 0;				
				findNextComponent();			
			}
		}
		else{
			// End not reached => Activating next found component from component tree			
			findNextComponent();			
		}
	}

	/**
	 * Finds next component pointed by <code>foundComponentIndex</code> and
	 * increments index count.
	 */
	private void findNextComponent() {
		view.activateTreeViewComponent(foundComponentsList.get(foundComponentIndex));
		// Incrementing count
		foundComponentIndex++;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		
		// Text field modification resets the current find
		resetFind();
		
		// Settings button statuses according the text field contents
		if(findStringTxtField.getText().length() > 0){
			findNextButton.setEnabled(true);
		}else{
			findNextButton.setEnabled(false);
		}
	}
		
	/**
	 * Resets current find context.
	 */
	private void resetFind(){
		foundComponentsList = null;
		foundComponentIndex = 0;
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.core.IAppDepSettingsChangedListener#settingsChanged()
	 */
	public void settingsChanged(boolean isTargetBuildChanged) {		
		// Resetting find context when settings have been changed
		resetFind();		
	}
	
	/**
	 * Finds the components for given search string.
	 * @param startNode Start node for the search.
	 * @param searchString String to search for
	 * @return List of component nodes matching to given search string
	 */
	private List<ComponentNode> findComponents(ComponentNode startNode, String searchString){
		List<ComponentNode> searchResultList = new ArrayList<ComponentNode>();
		IVisitor v = new FindVisitor(searchString, searchResultList);
		startNode.accept(v);
		return searchResultList;
	}
}
