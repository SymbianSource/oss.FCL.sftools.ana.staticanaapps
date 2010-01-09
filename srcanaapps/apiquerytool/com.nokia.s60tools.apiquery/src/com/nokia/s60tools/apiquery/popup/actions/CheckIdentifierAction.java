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
 
package com.nokia.s60tools.apiquery.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.nokia.s60tools.apiquery.shared.datatypes.APIQueryParameters;
import com.nokia.s60tools.apiquery.shared.exceptions.QueryOperationFailedException;
import com.nokia.s60tools.apiquery.shared.resources.Messages;
import com.nokia.s60tools.apiquery.shared.ui.dialogs.APIQueryMessageBox;
import com.nokia.s60tools.apiquery.shared.util.SourceCodeParsingUtilities;
import com.nokia.s60tools.apiquery.shared.util.console.APIQueryConsole;
import com.nokia.s60tools.apiquery.ui.views.main.MainView;
import com.nokia.s60tools.util.console.IConsolePrintUtility;
import com.nokia.s60tools.util.debug.DbgUtility;

/**
 * Runs API query on the identifier that user has selected from an editor.
 */
public class CheckIdentifierAction implements IObjectActionDelegate{
	
	//
	// Static members
	//
	
	/**
	 * Common ID path for the all identifier actions
	 */
	private static final String ACTION_ID_PATH = "com.nokia.s60tools.apiquery.popup.actions.CheckIdentifierAction."; //$NON-NLS-1$
	
	/**
	 * Actions ID for 
	 */
	private static final String FOR_CPRS_KEY_NAME_ID = ACTION_ID_PATH + "CRPSKey"; //$NON-NLS-1$

	/**
	 * Actions ID for 
	 */
	private static final String FOR_LIB_NAME_ID = ACTION_ID_PATH + "LIBName"; //$NON-NLS-1$

	/**
	 * Actions ID for 
	 */
	private static final String FOR_HEADER_NAME_ID = ACTION_ID_PATH + "HeaderName"; //$NON-NLS-1$
	
	//
	// Non-static members
	//
	
	/**
	 * The identifier that user has selected for the query, or multiple
	 * identifiers separated with ; character.
	 */
	private String queryString;

	
	
	/**
	 * Constructor for Action1.
	 */
	public CheckIdentifierAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		// Resolving the correct query type
		if (action.getId().equals(FOR_CPRS_KEY_NAME_ID)){
			startQuery(APIQueryParameters.QUERY_BY_CRPS_KEY_NAME);
		} else if (action.getId().equals(FOR_LIB_NAME_ID)){
			startQuery(APIQueryParameters.QUERY_BY_LIB_NAME);
		} else if (action.getId().equals(FOR_HEADER_NAME_ID)){
			startQuery(APIQueryParameters.QUERY_BY_HEADER_NAME);
		}
		else //if (action.getId().equals(FOR_HEADER_NAME_ID))
			{
			startQuery(APIQueryParameters.QUERY_BY_API_NAME);
		}		
	}
	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
		// By default the action is disables
		boolean isActionEnabled = false;
		
		try {
			if(selection != null){
				
				// Parameter 'selection' is actually File-object instance
				// of the selected file, but we are interested instead if
				// We have an open editor, and text selection in there.
								
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart activeEditor = activePage.getActiveEditor();
				//Active editor can be Text Editor or FormEditor, in case of FormEditor it's really
				//com.nokia.carbide.cpp.mmpEditor.MMPEditor, but that Class cannot be used outside of that package.
				if(activeEditor != null && (activeEditor instanceof TextEditor || activeEditor instanceof FormEditor)){
					
					ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
					
					ISelection textSel = selectionProvider.getSelection();
					if(textSel instanceof TextSelection){
						TextSelection currEditorTextSel = (TextSelection) textSel;
						int endLine = currEditorTextSel.getEndLine();
						int startLine = currEditorTextSel.getStartLine();
						boolean isMultipleLinesSelection = ((endLine - startLine) > 0);
						String currSelStr = currEditorTextSel.getText().toString().trim();
						
						// Header name action is a special case among the other actions
						if (action.getId().equals(FOR_HEADER_NAME_ID)){

							IDocument doc = null;
							if(activeEditor instanceof TextEditor){
								// This is actually an instance of 'org.eclipse.cdt.internal.ui.editor.CEditor' 
								// that extends org.eclipse.ui.editors.text.TextEditor								
								TextEditor cEditor = (TextEditor) activeEditor;
								doc =  getDocument(cEditor);
							}else if(activeEditor instanceof FormEditor){
								// This is actually an instance of com.nokia.carbide.cpp.mmpEditor.MMPEditor, 
								// but that Class cannot be used outside of that package, and it extends FormEditor class.
								FormEditor fEditor = (FormEditor)activeEditor;
								doc =  getDocument(fEditor);
							}
							isActionEnabled = handleHeaderNameActionStateCheck(doc, startLine, isMultipleLinesSelection, currSelStr);
						}
						// Other action types get same kind of treatment.
						else{
							isActionEnabled = handleStateCheckForOtherActions(isMultipleLinesSelection, currSelStr);					
						}
						
					} // if a text selection intance
				} // if activeEditor is not null					
			}// if selection is not null
			
		} catch (Exception e) {
			// Just making easier to find out problems during dev. time.
			e.printStackTrace();
		}
		
		// Enabling/disabling the action
		action.setEnabled(isActionEnabled);			
		
	}

	/**
	 * Checks the enable/disable state for other actions.
	 * @param isMultipleLinesSelection <code>true</code> in case of multiline selection, otherwise <code>false</code>.
	 * @param currSelStr	User's text selection from the editor.
	 * @return Action's enablement state.
	 */
	private boolean handleStateCheckForOtherActions(boolean isMultipleLinesSelection, String currSelStr) {

		boolean isActionEnabled = false;
		
		// For other actions, the valid selection is not empty 
		// and not a selection of multiple lines.
		if(currSelStr.length() > 0
			&& 
		   !isMultipleLinesSelection
		   ){
			// We have a valid selection => let's store it...
			this.queryString = currSelStr;			
			// ... and enable the action.
			isActionEnabled = true;
		}
		return isActionEnabled;
	}

	/**
	 * Checks the enable/disable state for header name action.
	 * @param cEditor Text editor instance user for browsing editor contents, if needed.
	 * @param startLine Start line for the curren selection (=line for current cursor location).
	 * @param isMultipleLinesSelection <code>true</code> in case of multiline selection, otherwise <code>false</code>.
	 * @param currSelStr	User's text selection from the editor.
	 * @return Action's enablement state.
	 */
	private boolean handleHeaderNameActionStateCheck(IDocument doc, int startLine, boolean isMultipleLinesSelection, String currSelStr) {
		
		boolean isActionEnabled = false;
		
		// Checking if we have a selection containing text
		if( currSelStr.length() > 0){
			if(isMultipleLinesSelection){
				// Parsing multiline selection for directives
				if(checkSelectionForIncludeDirectives(currSelStr)){
					isActionEnabled = true;
				}
			}
			else{
				// Otherwise regarding the selection as an identifier.
				queryString = currSelStr;
				isActionEnabled = true;
			}
		}
		else{
			// There is no selection made => parsing the current line
			if(doc != null){
				// Does the line contain any include directives?
				if(checkForIncludeDirectiveFromGivenLine(doc, startLine)){
					isActionEnabled = true;
				}
			}																
		}
		return isActionEnabled;
	}
		
	/**
	 * Checks if there is/are (an) include directive(s) in the given text selection
	 * and stores the header names as the selected identifier(s).
	 * @param selectedMultilineText Selected text to check for (may contain multiple lines)
	 * @return <code>true</code> if a valid include definitions were stored.
	 */
	private boolean checkSelectionForIncludeDirectives(String selectedMultilineText) {
		
		boolean hasIncludeDirectives = false;
		
		String[] lineArr = selectedMultilineText.split("\n"); //$NON-NLS-1$
		String headerFileName = null;
		// Resettings the identifier storage string
		queryString = ""; //$NON-NLS-1$
		
		for (int i = 0; i < lineArr.length; i++) {
			String line = lineArr[i];
			headerFileName = SourceCodeParsingUtilities.parseIncludeFromLine(line);
			if(headerFileName != null){
				hasIncludeDirectives = true;
				// Incrementing the found directive into the identifier list
				if(queryString.length() > 0){
					queryString = queryString 
					                    + APIQueryParameters.SEARCH_ITEM_SEPARATOR_CHAR
					                    + headerFileName;					
				}
				else{
					queryString = headerFileName;
				}
			}
		}
		return hasIncludeDirectives;
	}

	/**
	 * Checks if there is an include directive in the given line
	 * and stores the header name as the selected identifier.
	 * @param doc Document to check for.
	 * @param lineno Line number in document to check for.
	 * @return <code>true</code> if a valid include definitions was stored
	 */
	private boolean checkForIncludeDirectiveFromGivenLine(IDocument doc, int lineno) {

		IRegion lineRegion;
		try {
			lineRegion = doc.getLineInformation(lineno);
			String line = doc.get(lineRegion.getOffset(), lineRegion.getLength());
			
			if(SourceCodeParsingUtilities.hasIncludes(line)){
				queryString = SourceCodeParsingUtilities.parseIncludeFromLine(line);
				return true;
			}
		} catch (BadLocationException e) {
			// Ignoring possible bad location exceptions 
			// when this method returns false.
		}
		
		return false;
	}

	/**
	 * Returns the document interface for the currently active document 
	 * in the given editor.
	 * @param editor Editor to ask currently active document from. 
	 * @return Document interface if found, otherwise <code>null</code>.
	 */
	private IDocument getDocument(TextEditor editor) {
		
		TextFileDocumentProvider  documentProvider = (TextFileDocumentProvider) editor.getDocumentProvider();
		if(documentProvider != null){
			return  documentProvider.getDocument(editor.getEditorInput());
			}								
		return null;
	}
	
	/**
	 * Returns the document interface for the currently active document 
	 * in the given editor.
	 * @param editor Editor to ask currently active document from. 
	 * @return Document interface if found, otherwise <code>null</code>.
	 */
	private IDocument getDocument(FormEditor editor) {
		try {
			IEditorPart part = editor.getActiveEditor();
			ITextEditor textEditor = (ITextEditor) part;
			
			TextFileDocumentProvider  documentProvider = (TextFileDocumentProvider) textEditor.getDocumentProvider();
			if(documentProvider != null){
				return  documentProvider.getDocument(editor.getEditorInput());
			}
		} catch (Exception e) {
			// No operation if fails, just returning null, if document is null, false will return from handleHeaderNameActionStateCheck(...)
			e.printStackTrace();
		}								
		return null;
	}	

	/**
	 * Starts query for the current identifier selection with 
	 * the given query type.
	 * @param queryType
	 */
	private void startQuery(int queryType){
		startQuery(queryType, false);
	}
	
	/**
	 * Starts query for the current identifier selection with 
	 * the given query type.
	 * @param queryType
	 * @param useExactMatch <code>true</code> if search string will be searched with exact match 
	 * instead of contains.
	 */
	private void startQuery(int queryType, boolean useExactMatch){
		DbgUtility.println(DbgUtility.PRIORITY_CLASS, "Starting '" //$NON-NLS-1$
														+ APIQueryParameters.getDescriptionForQueryType(queryType)
														+ "' query with identifier '"  //$NON-NLS-1$
														+ this.queryString + "'.");  //$NON-NLS-1$
		try {
			MainView.runAPIQueryFromExternalClass(queryType, this.queryString, useExactMatch);
		} catch (QueryOperationFailedException e) {
			e.printStackTrace();
			String errMsg = Messages.getString("CheckIdentifierAction.Query_Failed_From_Context_Menu_ErrorMsg") + e.getMessage(); //$NON-NLS-1$
			APIQueryConsole.getInstance().println(errMsg, IConsolePrintUtility.MSG_ERROR);
			new APIQueryMessageBox(errMsg, SWT.ICON_ERROR | SWT.OK).open();			
		}
	}	

	
	/**
	 * Run API Query action directly with using action class.
	 * @param quertString queried string
	 * @param queryType query type
	 */
	public void runAPIQuery(String quertString, int queryType) {
		this.queryString = quertString;		
		startQuery(queryType, true);		
	}

}
