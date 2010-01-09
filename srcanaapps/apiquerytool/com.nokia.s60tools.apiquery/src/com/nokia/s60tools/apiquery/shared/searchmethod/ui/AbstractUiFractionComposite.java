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
 
 
package com.nokia.s60tools.apiquery.shared.searchmethod.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.nokia.s60tools.apiquery.shared.resources.Messages;

/**
 * Abstract class that defines an abstract template 
 * for the composites that should be used to populate
 * the contents a tab area or a sub area inside a tab
 * (or inside any other UI element). Just utilizing
 * Eclipse UI functionality, but making it simpler
 * and more compact to use UI Composites from at the
 * source code level.
 */
public abstract class AbstractUiFractionComposite extends Composite {
		
	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 * @param style				The used style for the composite.
	 */
	public AbstractUiFractionComposite(Composite parentComposite, int style) {
		super(parentComposite, style);
		checkParent(parentComposite);
		initComposite();
	}
	
	/**
	 * Checks that parent has a valid display.
	 * @param parentComposite Parent composite for the created composite. 
	 */
	private void checkParent(Composite parentComposite) {
		if (parentComposite.getDisplay() == null){
			throw new RuntimeException(Messages.getString("AbstractUiFractionComposite.ParentDisplayNULL_ErrMsg")); //$NON-NLS-1$
		}
	}

	/**
	 * Constructor.
	 * @param parentComposite	Parent composite for the created composite.
	 */
	public AbstractUiFractionComposite(Composite parentComposite) {
		super(parentComposite, SWT.NONE);
		initComposite();
	}
	
	/**
	 * Performs template methods that take care
	 * of finalizing composite construction.
	 */
	private void initComposite(){
		setLayout(createLayout());
		setLayoutData(createLayoutData());
		createControls();
	}
	
	/**
	 * Sub classes can override this.
	 * @return Width hint for the composite.
	 */
	public int getWidthHint(){
		return SWT.DEFAULT;
	}
	
	/**
	 * Sub classes can override this.
	 * @return Height hint for the composite.
	 */
	public int getHeightHint(){
		return SWT.DEFAULT;
	}
	
	/**
	 * Template method to be implementd in sub classes.
	 * Sub class should initialize the composite's layout
	 * in the implementation of this method.
	 * @return Layout for the composite.
	 */
	protected abstract Layout createLayout();

	/**
	 * Template method to be implementd in sub classes.
	 * Sub class should initialize the composite's layout data
	 * in the implementation of this method.
	 * @return Layout for the composite.
	 */
	protected abstract Object createLayoutData();

	/**
	 * Template method to be implementd in sub classes.
	 * Sub class should create all the composite's controls
	 * in the implementation of this method.
	 */
	protected abstract void createControls();
}
