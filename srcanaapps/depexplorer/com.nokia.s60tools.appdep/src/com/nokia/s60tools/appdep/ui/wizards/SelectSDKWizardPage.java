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
 
 
package com.nokia.s60tools.appdep.ui.wizards;




import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.core.AppDepSettings;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.LogUtils;
import com.nokia.s60tools.ui.ProgrammaticSelection;


/**
 * Wizard page showing the available SDKs. 
 */
public class SelectSDKWizardPage extends AbstractSelectSDKWizardPage implements FocusListener{	
	
	/**
	 * Wizard page's title.
	 */
	private Text sdkViewerTitle;
	
	/**
	 * Viewer component for showing available targets.
	 */
	private TableViewer sdkViewer;
	
	/**
	 * Content provider for the viewer component.
	 */
	private SelectSDKWizardPageContentProvider contentProvider;
	
	 /**
	 * Constructor.
	 */
	public SelectSDKWizardPage(){		
		super(Messages.getString("SelectSDKWizardPage.WizardPageName")); //$NON-NLS-1$
		
		setTitle(Messages.getString("SelectSDKWizardPage.WizardPageTitle"));			 //$NON-NLS-1$
		setDescription(Messages.getString("SelectSDKWizardPage.WizardPageDescription"));  //$NON-NLS-1$
		
		// User cannot finish the page before some valid 
		// selection is made.
		setPageComplete(false);
	 }
	 
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
	  Composite c = new Composite(parent, SWT.NONE);
	  
	  final int cols = 1;	  
	  GridLayout gdl = new GridLayout(cols, false);
	  GridData gd = new GridData(GridData.FILL_BOTH);
	  c.setLayout(gdl);
	  c.setLayoutData(gd);
	  
	  //
	  // Creating controls
	  //
	  final int readOnlyLabelFieldStyleBits = SWT.READ_ONLY | SWT.NO_FOCUS;
	  
	  sdkViewerTitle = new Text(c, readOnlyLabelFieldStyleBits); 

	  sdkViewerTitle.setText(Messages.getString("SelectSDKWizardPage.SDKListViewerComponentTitle")); //$NON-NLS-1$
	  sdkViewerTitle.addFocusListener(this);
	  
	  sdkViewer = new TableViewer(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	  sdkViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	  
	  // Providers cannot be created before all the controls have been created
	  // Content provider uses the information provided by the checkbox control
	  contentProvider = new SelectSDKWizardPageContentProvider();
	  sdkViewer.setContentProvider(contentProvider); 
	  sdkViewer.setLabelProvider(new SelectSDKWizardPageLabelProvider2());
	  sdkViewer.setInput(contentProvider);
	  
	  // Adding selection change listener
	  sdkViewer.addSelectionChangedListener(new ISelectionChangedListener(){
				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
				 */
				public void selectionChanged(SelectionChangedEvent event) {
					recalculateButtonStates();			
				}		  
		  });
	  
	  // Enabling flipping to next page with double-click
	  sdkViewer.addDoubleClickListener(new IDoubleClickListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
		public void doubleClick(DoubleClickEvent event) {
			recalculateButtonStates();
			// If allowed to flip next page...
			if(isPageComplete()){
				IWizardPage nextPage = getNextPage();
				// And refreshing contents
				((IRefreshable)nextPage).refresh();				
				// Showing next page
				getContainer().showPage(nextPage);
			}			
		}});

	  // By default collapsing the tree
	  setInitialFocus();
	  
	  // Setting control for this page
	  setControl(c);
	
	  // Setting context help IDs		
      AppDepPlugin.setContextSensitiveHelpID(getControl(), AppDepHelpContextIDs.APPDEP_WIZARD_PAGE_SDK_SELECT);
	}

	
	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#setInitialFocus()
	 */
	public void setInitialFocus() {
		sdkViewer.getTable().setFocus();			
	}

	/* (non-Javadoc)
	 * @see com.nokia.s60tools.appdep.ui.wizards.S60ToolsWizardPage#recalculateButtonStates()
	 */
	public void recalculateButtonStates() {

		try {
			SelectSDKWizard wiz = (SelectSDKWizard) getWizard();
			AppDepSettings settings = wiz.getSettings();
			IStructuredSelection selection = (IStructuredSelection )sdkViewer.getSelection();
			Object obj = selection.getFirstElement();
			
			// There is no selection made (e.g. coming with Back from previous page)
			if(obj == null){
				// ...checking if we can create a selection based on the currently used SDK
				obj = contentProvider.find(settings.getCurrentlyUsedSdk().getSdkId());
				if(obj  != null){
					// Match was found
					ProgrammaticSelection newSelection = null;
					newSelection = new ProgrammaticSelection(new Object[]{obj});
					sdkViewer.setSelection(newSelection, true);
					selection = (IStructuredSelection )sdkViewer.getSelection();
					if(selection == null){
						// Trying to avoid internal errors, but this is something that should be trapped during 
						// development time. Internal error messages are not localized.
						String errMsg = "Programmatic selection failed unexpectedly in class '" //$NON-NLS-1$
										+ SelectSDKWizardPage.class.getSimpleName()
										+ "'."; //$NON-NLS-1$
						LogUtils.logInternalErrorAndThrowException(errMsg);						
						return;
					}
				}
				else{
					// No match was found
					return;
				}				
			}
			
			// Otherwise SDK is selected
			SdkTreeViewNode sdkNode = (SdkTreeViewNode) selection.toArray()[0]; // Getting all selected SDK node

			if(! sdkNode.getSdkInfo().epocRootExists()){
				// Non-existing SDK node selected
				this.setMessage(null);
				this.setErrorMessage(Messages.getString("SelectSDKWizardPage.EPOCROOT_NotFound_ErrMsg")); //$NON-NLS-1$
				// Flipping to next page and finishing is forbidden
				setPageComplete(false);				
				wiz.disableCanFinish();										
			}
			else{
				// Existing SDK node selected
				this.setMessage(Messages.getString("SelectSDKWizardPage.Press_Next_To_Continue")); //$NON-NLS-1$
				this.setErrorMessage(null);
				// Flipping to next page is possible
				setPageComplete(true);
				// Finishing is forbidden
				wiz.disableCanFinish();		
				// Storing user selection and ...
				wiz.setUserSelection(sdkNode);
			}
									
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
				
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		Widget w = e.widget;
		if(w.equals(sdkViewerTitle)){
			setInitialFocus();			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		// No need to do anything		
	}

} 

