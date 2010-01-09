/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies). 
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.nokia.s60tools.appdep.AppDepHelpContextIDs;
import com.nokia.s60tools.appdep.plugin.AppDepPlugin;
import com.nokia.s60tools.appdep.resources.ImageKeys;
import com.nokia.s60tools.appdep.resources.ImageResourceManager;
import com.nokia.s60tools.appdep.resources.Messages;
import com.nokia.s60tools.appdep.util.AppDepConsole;
import com.nokia.s60tools.appdep.util.SISFileUtils;
import com.nokia.s60tools.ui.S60ToolsTable;
import com.nokia.s60tools.ui.S60ToolsTableColumnData;
import com.nokia.s60tools.ui.S60ToolsTableFactory;
import com.nokia.s60tools.ui.S60ToolsUIConstants;



/**
 * Dialog for adding components from SIS file for analysis. 
 */
public class AddSISFilesDialog extends TitleAreaDialog  implements SelectionListener{	

	//
	// Private classes
	//
	
	/**
	 * Label provider for table viewer component.
	 */
	class SISFilesViewerLabelProvider extends LabelProvider implements ITableLabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null; // No images used
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String label = element.toString();
			
			SISFileEntry entryData = (SISFileEntry) element;

			switch (columnIndex) {
		
				case SISFileEntry.NAME_COLUMN_INDEX:
					label = entryData.getFileName();
					break;
		
				case SISFileEntry.LOCATION_COLUMN_INDEX:
					label = entryData.getLocation();
					break;
							
				default:
					AppDepConsole.getInstance().println(Messages.getString("GeneralMessages.Unexpected_Column_Index_ErrMsg") + ": " + columnIndex, AppDepConsole.MSG_ERROR); //$NON-NLS-1$ //$NON-NLS-2$
					break;
			}
			
			return label;
		}
		
	}
	
	//
	// Private constants
	//
	
	/**
	 * Columns in the container area.
	 */
	private static final int COLUMN_COUNT = 2;	  

	/**
	 * Dialog width.
	 */
	private static final int DIALOG_WIDTH = 425;
	
	/**
	 * Dialog height.
	 */
	private static final int DIALOG_HEIGHT = 425;
		
	/**
	 * Percentage as decimal number how much table viewer is taking space hirizontally. 
	 */
	private static final double TABLE_VIEWER_WIDTH_PERCENTAGE = 0.8;
	
	/**
	 * Default guiding message shown to the user in add new mode.
	 */
	private static final String DEFAULT_MESSAGE = Messages.getString("AddSISFilesDialog.Default_InfoMessage"); //$NON-NLS-1$
	
	/**
	 * Complete message shown to the user in add new mode.
	 */
	private static final String COMPLETE_MESSAGE = Messages.getString("AddSISFilesDialog.Complete_InfoMessage");  //$NON-NLS-1$
	
	//
	// Member data
	//
	
	/**
	 * Flag used to make sure that create() and open() are called in correct order.
	 */
	private boolean isCreateCalled = false;
	
	/**
	 * Stores the currently selected SIS file entries.
	 */
	Map<String, SISFileEntry> sisFileEntries;

	//
	// UI Controls
	//	
	
	/**
	 * Container area for individual fields for the user for entering information.
	 */
	private Composite container;

	/**
	 * Reference to OK button that can be disabled/enabled 
	 * due to information entered..
	 */
	private Button okActionBtn;

	/**
	 * Button for opening file dialog for selecting SIS file(s).
	 */
	private Button addButton;

	/**
	 * Button for removing currently selected SIS file from table viewer, 
	 */
	private Button removeButton;

	/**
	 * Button for removing all SIS files from table viewer,
	 */
	private Button removeAllButton;

	/**
	 * Viewer showing currently selected SIS file entries.
	 */
	private TableViewer sisFilesViewer;

	
	/**
	 * Constructor. Used to open dialog in order to add new entry.
	 * @param parentShell Parent shell for the dialog.
	 */
	public AddSISFilesDialog(Shell parentShell) {
		super(parentShell);
		sisFileEntries = new HashMap<String, SISFileEntry>();
		// Setting banner image
		String bannerImage = ImageKeys.WIZARD_BANNER;
		setTitleImage(ImageResourceManager.getImage(bannerImage));
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL,
                true);     
		okActionBtn = getButton(IDialogConstants.OK_ID);
		disableOk();        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.getString("AddSISFilesDialog.DialogTitleText"));			  //$NON-NLS-1$
    }    
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		
		//
		// Creating container and layout for it
		//
		container = new Composite(dialogAreaComposite, SWT.NONE);
		GridLayout gdl = new GridLayout(COLUMN_COUNT, false);
		// Settings margins according Carbide branding guideline
		gdl.marginLeft = S60ToolsUIConstants.MARGIN_BTW_FRAME_AND_CONTENTS;
		gdl.marginRight = S60ToolsUIConstants.MARGIN_BTW_FRAME_AND_CONTENTS;
		gdl.marginTop = S60ToolsUIConstants.MARGIN_BTW_FRAME_AND_CONTENTS;
		gdl.marginBottom = S60ToolsUIConstants.MARGIN_BTW_FRAME_AND_CONTENTS;		
		container.setLayout(gdl);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//
		// Creating table viewer for showing SIS file entries
		//
		
		sisFilesViewer = createSISFilesTableViewer(container);
	    GridData sisFilesViewerGd = new GridData(GridData.FILL_BOTH);
	    // Spanning as many rows as there are actions buttons on the right
	    sisFilesViewerGd.verticalSpan = 3;
	    sisFilesViewerGd.widthHint = (int) (TABLE_VIEWER_WIDTH_PERCENTAGE * DIALOG_WIDTH);
		sisFilesViewer.getControl().setLayoutData(sisFilesViewerGd);
		sisFilesViewer.setSorter(new SISFileEntryTableViewerSorter());
		// Adding selection change listener
		sisFilesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				// Just settings remove button enable/disable state based on the selection state
				if(selection != null && selection.getFirstElement() != null){
					removeButton.setEnabled(true);
				}
				else{
					removeButton.setEnabled(false);					
				}				
			}
			
		});
		
		//
		// Creating buttons
		//
		
		int buttonGridDataStyleBits = GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING;
		addButton = createButtonControl(Messages.getString("AddSISFilesDialog.Add_BtnCaptionText"), true, new GridData(buttonGridDataStyleBits)); //$NON-NLS-1$
		removeButton = createButtonControl(Messages.getString("AddSISFilesDialog.Remove_BtnCaptionText"), false, new GridData(buttonGridDataStyleBits));		 //$NON-NLS-1$
		removeAllButton = createButtonControl(Messages.getString("AddSISFilesDialog.RemoveAll_BtnCaptionText"), false, new GridData(buttonGridDataStyleBits)); //$NON-NLS-1$
		
		//
		// Setting providers for table viewer
		//
		
		// Creating content provider
		IStructuredContentProvider sisFilesViewerContentProvider = new IStructuredContentProvider(){

					/* (non-Javadoc)
					 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
					 */
					public Object[] getElements(Object inputElement) {
						return sisFileEntries.values().toArray();
					}
		
					/* (non-Javadoc)
					 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
					 */
					public void dispose() {
						// Not needed but needs to be implemented
					}
		
					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {				
						// Not used but needs to be implemented				
					}
	
				};
				
		// Setting content provider
		sisFilesViewer.setContentProvider(sisFilesViewerContentProvider);
		sisFilesViewer.setInput(sisFilesViewerContentProvider);
		
		// Label provider
		sisFilesViewer.setLabelProvider(new SISFilesViewerLabelProvider());

		// Setting context-sensitive help ID
		AppDepPlugin.setContextSensitiveHelpID(dialogAreaComposite, AppDepHelpContextIDs.APPDEP_DIALOG_ADD_SIS_FILES);
		
		// Dialog are composite ready
		return dialogAreaComposite;
	}

	/**
	 * Creates button with given label text.
	 * @param labelText Label for the button.
	 * @param isEnabled Set to <code>true</code> if button is enabled initially, otherwise <code>false</code>.
	 * @param gridData Grid data to be applied to the button.
	 * @return new instance of Button object.
	 */
	private Button createButtonControl(String labelText, boolean isEnabled, GridData gridData) {
		  Button btn = new Button(container, SWT.PUSH );
		  btn.setText(labelText); 
		  btn.addSelectionListener(this);
		  // What room is left horizontally from table viewer is given for buttons 
		  gridData.widthHint = (int) (0.95 * (1-TABLE_VIEWER_WIDTH_PERCENTAGE) * DIALOG_WIDTH);
		  btn.setLayoutData(gridData);
		  btn.setEnabled(isEnabled);
		  return btn;
	}    
		
	 /* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	protected Point getInitialSize() {
			return new Point(DIALOG_WIDTH, DIALOG_HEIGHT);
	    }
	 
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	public void create() {
		super.create();
		// Currently just does creation by super call and stores status
		isCreateCalled = true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open(){
		try {
			// Making sure that create is called
			if(!isCreateCalled){
				create();
			}
			showDefaultMessage();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.open();
	}

	/**
	 * Resets possible error messages and show the default message.
	 */
	private void showDefaultMessage() {
		setErrorMessage(null);
		setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);			
	}

	/**
	 * Informs user that parameters are valid and dialog can be
	 * dismissed with OK button..
	 */
	private void setCompleteOkMessage() {
		setErrorMessage(null);
		setMessage(COMPLETE_MESSAGE, IMessageProvider.INFORMATION);			
	}

	/**
	 * Disables OK button.
	 * This method is guarded against call during construction
	 * when button row has not been created yet and widget is <code>null</code>.
	 */
	private void disableOk() {
		if(okActionBtn != null){
			okActionBtn.setEnabled(false);			
		}
	}

	/**
	 * Enables OK button.
	 * This method is guarded against call during construction
	 * when button row has not been created yet and widget is <code>null</code>.
	 */
	private void enableOk() {
		if(okActionBtn != null){
			okActionBtn.setEnabled(true);
		}
	}	
			
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// Not needed in here, but needs to be implemented		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Widget widget = e.widget;
		
		if(widget.equals(addButton)){
			queryAndAddNewEntries();
		}
		else if(widget.equals(removeAllButton)){
			removeAllEntries();
		}
		else if(widget.equals(removeButton)){
			// We have made sure by enabling/disabling button in selection 
			// changed listener code that we have valid selection.
			removeSelectedEntries();
		}
	}

	/**
	 * Launches SIS file selection and adds the selected and valid 
	 * SIS files to the entries.
	 */
	private void queryAndAddNewEntries() {
		List<String> illegalSISfilesList = new ArrayList<String>();
		String[] selectedSISFiles = invokeAddSISFilesDialog(getShell());
		for (int i = 0; i < selectedSISFiles.length; i++) {
			String absolutePathFilename = selectedSISFiles[i];
			if(checkSISFileValidity(absolutePathFilename, illegalSISfilesList)){
				addEntry(absolutePathFilename);					
			}
		}
		// Report user about the illegal SIS files that are not added to the table viewer
		notifyIllegalSISFile(illegalSISfilesList);
	}

	/**
	 * Shows an error dialog to user if there are any items 
	 * in given list of illegal SIS files. 
	 * @param illegalSISfilesList List of illegal SIS files.
	 */
	private void notifyIllegalSISFile(List<String> illegalSISfilesList) {
		if(illegalSISfilesList.size() > 0){
			String errMsg = Messages.getString("AddSISFilesDialog.NotSupportedSISFileDialog_ErrMsg"); //$NON-NLS-1$
			for (int i = 0; i < illegalSISfilesList.size(); i++) {
				String fileName = illegalSISfilesList.get(i);
				errMsg = errMsg + "\n" + fileName; //$NON-NLS-1$
			}
			AppDepMessageBox msgBox = new AppDepMessageBox(getShell(), errMsg, SWT.ICON_ERROR);
			msgBox.open();
		}
	}

	/**
	 * Checks validity of SIS file and in case file is not valid 
	 * adds it into list of illegal SIS files passed as parameter. 
	 * @param absolutePathFilename absolute file name to a SIS file.
	 * @param illegalSISfilesList list of illegal SIS files 
	 * @return <code>true</code> if file was a valid and supported SIS file. 
	 */
	private boolean checkSISFileValidity(String absolutePathFilename, List<String> illegalSISfilesList) {
		boolean valid_9x_SISFile = SISFileUtils.isValid_9x_SISFile(absolutePathFilename);
		if(!valid_9x_SISFile){
			illegalSISfilesList.add(absolutePathFilename);
		}
		return valid_9x_SISFile;
	}

	/**
	 * Removes the currently selected entries.
	 * Precondition for calling this method to make sure 
	 * that a valid selection exists.
	 */
	private void removeSelectedEntries() {
		IStructuredSelection selection = (IStructuredSelection) sisFilesViewer.getSelection();
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
			SISFileEntry fileEntry = (SISFileEntry) iterator.next();
			sisFileEntries.remove(fileEntry.getFullPathFileName());
		}
		notifyEntriesModified();
	}

	/**
	 * Removes all entries from SIS files table viewer.
	 */
	private void removeAllEntries() {
		sisFileEntries.clear();
		notifyEntriesModified();		
	}

	/**
	 * Adds SIS file entry and update buttons statuses.
	 * @param absolutePathFilename absolute pathname to SIS file to be added.
	 */
	private void addEntry(String absolutePathFilename) {
		File file = new File(absolutePathFilename);
		String location = file.getParentFile().getAbsolutePath();
		String sisFileName = file.getName();
		SISFileEntry fileEntry = new SISFileEntry(location, sisFileName);
		sisFileEntries.put(fileEntry.getFullPathFileName(), fileEntry);
		notifyEntriesModified();
	}
		
	/**
	 * Updates buttons statuses according the current 
	 * status of table entry viewer contents.
	 */
	private void notifyEntriesModified() {
		if(sisFileEntries.size() > 0){
			removeAllButton.setEnabled(true);
			enableOk();
			setCompleteOkMessage();
		}
		else{
			removeAllButton.setEnabled(false);
			showDefaultMessage();			
		}		
		sisFilesViewer.refresh();
	}

	/**
	 * Creates viewer component for showing selected SIS files. 
	 * @param parent Parent composite for the created composite.
	 * @return New <code>TableViewer</code> object instance.
	 */
	protected TableViewer createSISFilesTableViewer(Composite parent) {
		
		ArrayList<S60ToolsTableColumnData> columnDataArr = new ArrayList<S60ToolsTableColumnData>();
		
		//
		// NOTE: Column indices must start from zero (0) and
		// the columns must be added in ascending numeric
		// order.
		//
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("AddSISFilesDialog.Name_ColumnTitleText"), //$NON-NLS-1$
														140,
														SISFileEntry.NAME_COLUMN_INDEX,
														SISFileEntryTableViewerSorter.CRITERIA_NAME));
		columnDataArr.add(new S60ToolsTableColumnData(Messages.getString("AddSISFilesDialog.Location_ColumnTitleText"),  //$NON-NLS-1$
														140,
														SISFileEntry.LOCATION_COLUMN_INDEX,
														SISFileEntryTableViewerSorter.CRITERIA_LOCATION));
		
		S60ToolsTableColumnData[] arr 
				= columnDataArr.toArray(
									   new S60ToolsTableColumnData[0]);
		
		S60ToolsTable tbl = S60ToolsTableFactory.create(parent, arr);
		
		TableViewer tblViewer = new TableViewer(tbl.getTableInstance());
		tbl.setHostingViewer(tblViewer);
		
		return tblViewer;
	}
	
	/**
	 * Opens the Open File(s) dialog.
	 * @param sh parent shell
	 * @return file name(s) selected, or <code>null</code> if canceled or errors occurs.
	 */
	public static String [] invokeAddSISFilesDialog(Shell sh){
		//Open file dialog
		FileDialog fdia = new FileDialog(sh, SWT.MULTI);	
		fdia.setText(Messages.getString("AddSISFilesDialog.SISFileBrowseDialog_TitleText")); //$NON-NLS-1$
		String [] extensions = new String[] {"*.SIS", "*.SISX"}; //$NON-NLS-1$ //$NON-NLS-2$
		fdia.setFilterExtensions(extensions);
		String file = fdia.open();
		//If there is at least one file selected
		if(file != null){
			//get all selected file names
			String [] files = fdia.getFileNames();
			String path = fdia.getFilterPath();//Path (dir) where file(s) are
			String [] filesWithPath = new String[files.length];
			//Create absolute file paths <path>\<filename>
			for (int i = 0; i < files.length; i++) {
				filesWithPath[i] = path + File.separatorChar + files[i];
			}
			return filesWithPath;
		}
		else{
			//User has canceled the operation
			return null;
		}
	}

	/**
	 * Returns currently selected SIS files as string array containing absolute path names.
	 * @return currently selected SIS files.
	 */
	public String[] getSelectedSISFiles() {
		return sisFileEntries.keySet().toArray(new String[0]);
	}

	/**
	 * Sets initial set of SIS file to be shown for the user.
	 * Should be called before calling <code>open()</code> method.
	 * @param initialSISFileSet initial file set to be shown for the user.
	 */
	public void setInitialSISFileSet(String[] initialSISFileSet) {
		if(initialSISFileSet != null && initialSISFileSet.length > 0){
			enableOk(); // Enabling OK because there is some content available
			setCompleteOkMessage();
			for (int i = 0; i < initialSISFileSet.length; i++) {
				String fileFullPathName = initialSISFileSet[i];
				addEntry(fileFullPathName);
			}					
		}
	}
	
}
