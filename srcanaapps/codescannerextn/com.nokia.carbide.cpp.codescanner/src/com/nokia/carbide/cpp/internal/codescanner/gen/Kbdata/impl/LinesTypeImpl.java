/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of the License "Eclipse Public License v1.0"
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

package com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl;

import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ApinameType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.BType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.BooleanType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.CiteType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.CmdnameType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.CodephType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.DataAboutType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.DataType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.DirType49;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.DraftCommentType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ExpanseType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.FilepathType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.FnType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ForeignType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.FrameType3;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.IType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ImportanceType51;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.IndextermType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.IndextermrefType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.KbdataPackage;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.KeywordType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.LinesType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.MenucascadeType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.MsgnumType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.MsgphType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.OptionType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ParmnameType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.PhType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.QType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.RequiredCleanupType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ScaleType7;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.SpaceType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.StateType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.StatusType26;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.SubType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.SupType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.SynphType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.SystemoutputType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.TermType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.TmType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.TranslateType49;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.TtType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.UType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.UicontrolType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.UnknownType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.UserinputType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.VarnameType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.WintitleType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.XrefType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lines Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getPh <em>Ph</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getUicontrol <em>Uicontrol</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getMenucascade <em>Menucascade</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getB <em>B</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getU <em>U</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getI <em>I</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getTt <em>Tt</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSup <em>Sup</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSub <em>Sub</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getCodeph <em>Codeph</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSynph <em>Synph</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getFilepath <em>Filepath</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getMsgph <em>Msgph</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getUserinput <em>Userinput</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSystemoutput <em>Systemoutput</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getTerm <em>Term</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getXref <em>Xref</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getCite <em>Cite</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getQ <em>Q</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getBoolean <em>Boolean</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getState <em>State</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getKeyword <em>Keyword</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getWintitle <em>Wintitle</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getOption <em>Option</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getParmname <em>Parmname</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getApiname <em>Apiname</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getCmdname <em>Cmdname</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getMsgnum <em>Msgnum</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getVarname <em>Varname</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getTm <em>Tm</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getDraftComment <em>Draft Comment</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getRequiredCleanup <em>Required Cleanup</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getFn <em>Fn</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getIndextermref <em>Indextermref</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getIndexterm <em>Indexterm</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getData <em>Data</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getDataAbout <em>Data About</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getForeign <em>Foreign</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getUnknown <em>Unknown</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getAudience <em>Audience</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getBase <em>Base</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getConref <em>Conref</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getDir <em>Dir</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getExpanse <em>Expanse</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getFrame <em>Frame</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getImportance <em>Importance</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getLang <em>Lang</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getOtherprops <em>Otherprops</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getOutputclass <em>Outputclass</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getPlatform <em>Platform</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getProps <em>Props</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getRev <em>Rev</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSpace <em>Space</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getSpectitle <em>Spectitle</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getStatus <em>Status</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getTranslate <em>Translate</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getXtrc <em>Xtrc</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.LinesTypeImpl#getXtrf <em>Xtrf</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LinesTypeImpl extends EObjectImpl implements LinesType {
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed;

	/**
	 * The default value of the '{@link #getAudience() <em>Audience</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAudience()
	 * @generated
	 * @ordered
	 */
	protected static final Object AUDIENCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAudience() <em>Audience</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAudience()
	 * @generated
	 * @ordered
	 */
	protected Object audience = AUDIENCE_EDEFAULT;

	/**
	 * The default value of the '{@link #getBase() <em>Base</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBase()
	 * @generated
	 * @ordered
	 */
	protected static final Object BASE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBase() <em>Base</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBase()
	 * @generated
	 * @ordered
	 */
	protected Object base = BASE_EDEFAULT;

	/**
	 * The default value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected static final Object CLASS_EDEFAULT = "- topic/lines ";

	/**
	 * The cached value of the '{@link #getClass_() <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClass_()
	 * @generated
	 * @ordered
	 */
	protected Object class_ = CLASS_EDEFAULT;

	/**
	 * This is true if the Class attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean classESet;

	/**
	 * The default value of the '{@link #getConref() <em>Conref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConref()
	 * @generated
	 * @ordered
	 */
	protected static final Object CONREF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConref() <em>Conref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConref()
	 * @generated
	 * @ordered
	 */
	protected Object conref = CONREF_EDEFAULT;

	/**
	 * The default value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected static final DirType49 DIR_EDEFAULT = DirType49.LTR;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected DirType49 dir = DIR_EDEFAULT;

	/**
	 * This is true if the Dir attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean dirESet;

	/**
	 * The default value of the '{@link #getExpanse() <em>Expanse</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpanse()
	 * @generated
	 * @ordered
	 */
	protected static final ExpanseType EXPANSE_EDEFAULT = ExpanseType.PAGE;

	/**
	 * The cached value of the '{@link #getExpanse() <em>Expanse</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpanse()
	 * @generated
	 * @ordered
	 */
	protected ExpanseType expanse = EXPANSE_EDEFAULT;

	/**
	 * This is true if the Expanse attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean expanseESet;

	/**
	 * The default value of the '{@link #getFrame() <em>Frame</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrame()
	 * @generated
	 * @ordered
	 */
	protected static final FrameType3 FRAME_EDEFAULT = FrameType3.TOP;

	/**
	 * The cached value of the '{@link #getFrame() <em>Frame</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrame()
	 * @generated
	 * @ordered
	 */
	protected FrameType3 frame = FRAME_EDEFAULT;

	/**
	 * This is true if the Frame attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean frameESet;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getImportance() <em>Importance</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportance()
	 * @generated
	 * @ordered
	 */
	protected static final ImportanceType51 IMPORTANCE_EDEFAULT = ImportanceType51.OBSOLETE;

	/**
	 * The cached value of the '{@link #getImportance() <em>Importance</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportance()
	 * @generated
	 * @ordered
	 */
	protected ImportanceType51 importance = IMPORTANCE_EDEFAULT;

	/**
	 * This is true if the Importance attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean importanceESet;

	/**
	 * The default value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected static final String LANG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected String lang = LANG_EDEFAULT;

	/**
	 * The default value of the '{@link #getOtherprops() <em>Otherprops</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherprops()
	 * @generated
	 * @ordered
	 */
	protected static final Object OTHERPROPS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOtherprops() <em>Otherprops</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherprops()
	 * @generated
	 * @ordered
	 */
	protected Object otherprops = OTHERPROPS_EDEFAULT;

	/**
	 * The default value of the '{@link #getOutputclass() <em>Outputclass</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputclass()
	 * @generated
	 * @ordered
	 */
	protected static final Object OUTPUTCLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOutputclass() <em>Outputclass</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputclass()
	 * @generated
	 * @ordered
	 */
	protected Object outputclass = OUTPUTCLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getPlatform() <em>Platform</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlatform()
	 * @generated
	 * @ordered
	 */
	protected static final Object PLATFORM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPlatform() <em>Platform</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlatform()
	 * @generated
	 * @ordered
	 */
	protected Object platform = PLATFORM_EDEFAULT;

	/**
	 * The default value of the '{@link #getProduct() <em>Product</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProduct()
	 * @generated
	 * @ordered
	 */
	protected static final Object PRODUCT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProduct() <em>Product</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProduct()
	 * @generated
	 * @ordered
	 */
	protected Object product = PRODUCT_EDEFAULT;

	/**
	 * The default value of the '{@link #getProps() <em>Props</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProps()
	 * @generated
	 * @ordered
	 */
	protected static final Object PROPS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProps() <em>Props</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProps()
	 * @generated
	 * @ordered
	 */
	protected Object props = PROPS_EDEFAULT;

	/**
	 * The default value of the '{@link #getRev() <em>Rev</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRev()
	 * @generated
	 * @ordered
	 */
	protected static final Object REV_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRev() <em>Rev</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRev()
	 * @generated
	 * @ordered
	 */
	protected Object rev = REV_EDEFAULT;

	/**
	 * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected static final ScaleType7 SCALE_EDEFAULT = ScaleType7._50;

	/**
	 * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected ScaleType7 scale = SCALE_EDEFAULT;

	/**
	 * This is true if the Scale attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean scaleESet;

	/**
	 * The default value of the '{@link #getSpace() <em>Space</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpace()
	 * @generated
	 * @ordered
	 */
	protected static final SpaceType SPACE_EDEFAULT = SpaceType.PRESERVE;

	/**
	 * The cached value of the '{@link #getSpace() <em>Space</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpace()
	 * @generated
	 * @ordered
	 */
	protected SpaceType space = SPACE_EDEFAULT;

	/**
	 * This is true if the Space attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean spaceESet;

	/**
	 * The default value of the '{@link #getSpectitle() <em>Spectitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpectitle()
	 * @generated
	 * @ordered
	 */
	protected static final Object SPECTITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSpectitle() <em>Spectitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpectitle()
	 * @generated
	 * @ordered
	 */
	protected Object spectitle = SPECTITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final StatusType26 STATUS_EDEFAULT = StatusType26.NEW;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected StatusType26 status = STATUS_EDEFAULT;

	/**
	 * This is true if the Status attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean statusESet;

	/**
	 * The default value of the '{@link #getTranslate() <em>Translate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTranslate()
	 * @generated
	 * @ordered
	 */
	protected static final TranslateType49 TRANSLATE_EDEFAULT = TranslateType49.YES;

	/**
	 * The cached value of the '{@link #getTranslate() <em>Translate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTranslate()
	 * @generated
	 * @ordered
	 */
	protected TranslateType49 translate = TRANSLATE_EDEFAULT;

	/**
	 * This is true if the Translate attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean translateESet;

	/**
	 * The default value of the '{@link #getXtrc() <em>Xtrc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXtrc()
	 * @generated
	 * @ordered
	 */
	protected static final Object XTRC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getXtrc() <em>Xtrc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXtrc()
	 * @generated
	 * @ordered
	 */
	protected Object xtrc = XTRC_EDEFAULT;

	/**
	 * The default value of the '{@link #getXtrf() <em>Xtrf</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXtrf()
	 * @generated
	 * @ordered
	 */
	protected static final Object XTRF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getXtrf() <em>Xtrf</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXtrf()
	 * @generated
	 * @ordered
	 */
	protected Object xtrf = XTRF_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LinesTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return KbdataPackage.eINSTANCE.getLinesType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, KbdataPackage.LINES_TYPE__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		return (FeatureMap)getMixed().<FeatureMap.Entry>list(KbdataPackage.eINSTANCE.getLinesType_Group());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PhType> getPh() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Ph());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UicontrolType> getUicontrol() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Uicontrol());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MenucascadeType> getMenucascade() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Menucascade());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<BType> getB() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_B());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UType> getU() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_U());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<IType> getI() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_I());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<TtType> getTt() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Tt());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SupType> getSup() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Sup());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SubType> getSub() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Sub());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<CodephType> getCodeph() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Codeph());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SynphType> getSynph() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Synph());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<FilepathType> getFilepath() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Filepath());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MsgphType> getMsgph() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Msgph());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UserinputType> getUserinput() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Userinput());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SystemoutputType> getSystemoutput() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Systemoutput());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<TermType> getTerm() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Term());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<XrefType> getXref() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Xref());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<CiteType> getCite() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Cite());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<QType> getQ() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Q());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<BooleanType> getBoolean() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Boolean());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<StateType> getState() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_State());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<KeywordType> getKeyword() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Keyword());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WintitleType> getWintitle() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Wintitle());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OptionType> getOption() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Option());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ParmnameType> getParmname() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Parmname());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ApinameType> getApiname() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Apiname());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<CmdnameType> getCmdname() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Cmdname());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MsgnumType> getMsgnum() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Msgnum());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<VarnameType> getVarname() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Varname());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<TmType> getTm() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Tm());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DraftCommentType> getDraftComment() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_DraftComment());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RequiredCleanupType> getRequiredCleanup() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_RequiredCleanup());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<FnType> getFn() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Fn());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<IndextermrefType> getIndextermref() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Indextermref());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<IndextermType> getIndexterm() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Indexterm());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DataType> getData() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Data());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DataAboutType> getDataAbout() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_DataAbout());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ForeignType> getForeign() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Foreign());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UnknownType> getUnknown() {
		return getGroup().list(KbdataPackage.eINSTANCE.getLinesType_Unknown());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getAudience() {
		return audience;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAudience(Object newAudience) {
		Object oldAudience = audience;
		audience = newAudience;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__AUDIENCE, oldAudience, audience));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getBase() {
		return base;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBase(Object newBase) {
		Object oldBase = base;
		base = newBase;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__BASE, oldBase, base));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getClass_() {
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClass(Object newClass) {
		Object oldClass = class_;
		class_ = newClass;
		boolean oldClassESet = classESet;
		classESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__CLASS, oldClass, class_, !oldClassESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetClass() {
		Object oldClass = class_;
		boolean oldClassESet = classESet;
		class_ = CLASS_EDEFAULT;
		classESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__CLASS, oldClass, CLASS_EDEFAULT, oldClassESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetClass() {
		return classESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getConref() {
		return conref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConref(Object newConref) {
		Object oldConref = conref;
		conref = newConref;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__CONREF, oldConref, conref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirType49 getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(DirType49 newDir) {
		DirType49 oldDir = dir;
		dir = newDir == null ? DIR_EDEFAULT : newDir;
		boolean oldDirESet = dirESet;
		dirESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__DIR, oldDir, dir, !oldDirESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDir() {
		DirType49 oldDir = dir;
		boolean oldDirESet = dirESet;
		dir = DIR_EDEFAULT;
		dirESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__DIR, oldDir, DIR_EDEFAULT, oldDirESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDir() {
		return dirESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExpanseType getExpanse() {
		return expanse;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpanse(ExpanseType newExpanse) {
		ExpanseType oldExpanse = expanse;
		expanse = newExpanse == null ? EXPANSE_EDEFAULT : newExpanse;
		boolean oldExpanseESet = expanseESet;
		expanseESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__EXPANSE, oldExpanse, expanse, !oldExpanseESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExpanse() {
		ExpanseType oldExpanse = expanse;
		boolean oldExpanseESet = expanseESet;
		expanse = EXPANSE_EDEFAULT;
		expanseESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__EXPANSE, oldExpanse, EXPANSE_EDEFAULT, oldExpanseESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExpanse() {
		return expanseESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FrameType3 getFrame() {
		return frame;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFrame(FrameType3 newFrame) {
		FrameType3 oldFrame = frame;
		frame = newFrame == null ? FRAME_EDEFAULT : newFrame;
		boolean oldFrameESet = frameESet;
		frameESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__FRAME, oldFrame, frame, !oldFrameESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetFrame() {
		FrameType3 oldFrame = frame;
		boolean oldFrameESet = frameESet;
		frame = FRAME_EDEFAULT;
		frameESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__FRAME, oldFrame, FRAME_EDEFAULT, oldFrameESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetFrame() {
		return frameESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportanceType51 getImportance() {
		return importance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImportance(ImportanceType51 newImportance) {
		ImportanceType51 oldImportance = importance;
		importance = newImportance == null ? IMPORTANCE_EDEFAULT : newImportance;
		boolean oldImportanceESet = importanceESet;
		importanceESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__IMPORTANCE, oldImportance, importance, !oldImportanceESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetImportance() {
		ImportanceType51 oldImportance = importance;
		boolean oldImportanceESet = importanceESet;
		importance = IMPORTANCE_EDEFAULT;
		importanceESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__IMPORTANCE, oldImportance, IMPORTANCE_EDEFAULT, oldImportanceESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetImportance() {
		return importanceESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLang(String newLang) {
		String oldLang = lang;
		lang = newLang;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__LANG, oldLang, lang));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getOtherprops() {
		return otherprops;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOtherprops(Object newOtherprops) {
		Object oldOtherprops = otherprops;
		otherprops = newOtherprops;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__OTHERPROPS, oldOtherprops, otherprops));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getOutputclass() {
		return outputclass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutputclass(Object newOutputclass) {
		Object oldOutputclass = outputclass;
		outputclass = newOutputclass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__OUTPUTCLASS, oldOutputclass, outputclass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getPlatform() {
		return platform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPlatform(Object newPlatform) {
		Object oldPlatform = platform;
		platform = newPlatform;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__PLATFORM, oldPlatform, platform));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getProduct() {
		return product;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProduct(Object newProduct) {
		Object oldProduct = product;
		product = newProduct;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__PRODUCT, oldProduct, product));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getProps() {
		return props;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProps(Object newProps) {
		Object oldProps = props;
		props = newProps;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__PROPS, oldProps, props));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getRev() {
		return rev;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRev(Object newRev) {
		Object oldRev = rev;
		rev = newRev;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__REV, oldRev, rev));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaleType7 getScale() {
		return scale;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setScale(ScaleType7 newScale) {
		ScaleType7 oldScale = scale;
		scale = newScale == null ? SCALE_EDEFAULT : newScale;
		boolean oldScaleESet = scaleESet;
		scaleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__SCALE, oldScale, scale, !oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetScale() {
		ScaleType7 oldScale = scale;
		boolean oldScaleESet = scaleESet;
		scale = SCALE_EDEFAULT;
		scaleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__SCALE, oldScale, SCALE_EDEFAULT, oldScaleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetScale() {
		return scaleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SpaceType getSpace() {
		return space;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpace(SpaceType newSpace) {
		SpaceType oldSpace = space;
		space = newSpace == null ? SPACE_EDEFAULT : newSpace;
		boolean oldSpaceESet = spaceESet;
		spaceESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__SPACE, oldSpace, space, !oldSpaceESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSpace() {
		SpaceType oldSpace = space;
		boolean oldSpaceESet = spaceESet;
		space = SPACE_EDEFAULT;
		spaceESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__SPACE, oldSpace, SPACE_EDEFAULT, oldSpaceESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSpace() {
		return spaceESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getSpectitle() {
		return spectitle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpectitle(Object newSpectitle) {
		Object oldSpectitle = spectitle;
		spectitle = newSpectitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__SPECTITLE, oldSpectitle, spectitle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StatusType26 getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStatus(StatusType26 newStatus) {
		StatusType26 oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		boolean oldStatusESet = statusESet;
		statusESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__STATUS, oldStatus, status, !oldStatusESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStatus() {
		StatusType26 oldStatus = status;
		boolean oldStatusESet = statusESet;
		status = STATUS_EDEFAULT;
		statusESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__STATUS, oldStatus, STATUS_EDEFAULT, oldStatusESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStatus() {
		return statusESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TranslateType49 getTranslate() {
		return translate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTranslate(TranslateType49 newTranslate) {
		TranslateType49 oldTranslate = translate;
		translate = newTranslate == null ? TRANSLATE_EDEFAULT : newTranslate;
		boolean oldTranslateESet = translateESet;
		translateESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__TRANSLATE, oldTranslate, translate, !oldTranslateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTranslate() {
		TranslateType49 oldTranslate = translate;
		boolean oldTranslateESet = translateESet;
		translate = TRANSLATE_EDEFAULT;
		translateESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.LINES_TYPE__TRANSLATE, oldTranslate, TRANSLATE_EDEFAULT, oldTranslateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTranslate() {
		return translateESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getXtrc() {
		return xtrc;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setXtrc(Object newXtrc) {
		Object oldXtrc = xtrc;
		xtrc = newXtrc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__XTRC, oldXtrc, xtrc));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getXtrf() {
		return xtrf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setXtrf(Object newXtrf) {
		Object oldXtrf = xtrf;
		xtrf = newXtrf;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.LINES_TYPE__XTRF, oldXtrf, xtrf));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case KbdataPackage.LINES_TYPE__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__GROUP:
				return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__PH:
				return ((InternalEList<?>)getPh()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__UICONTROL:
				return ((InternalEList<?>)getUicontrol()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__MENUCASCADE:
				return ((InternalEList<?>)getMenucascade()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__B:
				return ((InternalEList<?>)getB()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__U:
				return ((InternalEList<?>)getU()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__I:
				return ((InternalEList<?>)getI()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__TT:
				return ((InternalEList<?>)getTt()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__SUP:
				return ((InternalEList<?>)getSup()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__SUB:
				return ((InternalEList<?>)getSub()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__CODEPH:
				return ((InternalEList<?>)getCodeph()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__SYNPH:
				return ((InternalEList<?>)getSynph()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__FILEPATH:
				return ((InternalEList<?>)getFilepath()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__MSGPH:
				return ((InternalEList<?>)getMsgph()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__USERINPUT:
				return ((InternalEList<?>)getUserinput()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__SYSTEMOUTPUT:
				return ((InternalEList<?>)getSystemoutput()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__TERM:
				return ((InternalEList<?>)getTerm()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__XREF:
				return ((InternalEList<?>)getXref()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__CITE:
				return ((InternalEList<?>)getCite()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__Q:
				return ((InternalEList<?>)getQ()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__BOOLEAN:
				return ((InternalEList<?>)getBoolean()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__STATE:
				return ((InternalEList<?>)getState()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__KEYWORD:
				return ((InternalEList<?>)getKeyword()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__WINTITLE:
				return ((InternalEList<?>)getWintitle()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__OPTION:
				return ((InternalEList<?>)getOption()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__PARMNAME:
				return ((InternalEList<?>)getParmname()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__APINAME:
				return ((InternalEList<?>)getApiname()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__CMDNAME:
				return ((InternalEList<?>)getCmdname()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__MSGNUM:
				return ((InternalEList<?>)getMsgnum()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__VARNAME:
				return ((InternalEList<?>)getVarname()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__TM:
				return ((InternalEList<?>)getTm()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__DRAFT_COMMENT:
				return ((InternalEList<?>)getDraftComment()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__REQUIRED_CLEANUP:
				return ((InternalEList<?>)getRequiredCleanup()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__FN:
				return ((InternalEList<?>)getFn()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__INDEXTERMREF:
				return ((InternalEList<?>)getIndextermref()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__INDEXTERM:
				return ((InternalEList<?>)getIndexterm()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__DATA:
				return ((InternalEList<?>)getData()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__DATA_ABOUT:
				return ((InternalEList<?>)getDataAbout()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__FOREIGN:
				return ((InternalEList<?>)getForeign()).basicRemove(otherEnd, msgs);
			case KbdataPackage.LINES_TYPE__UNKNOWN:
				return ((InternalEList<?>)getUnknown()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case KbdataPackage.LINES_TYPE__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case KbdataPackage.LINES_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case KbdataPackage.LINES_TYPE__PH:
				return getPh();
			case KbdataPackage.LINES_TYPE__UICONTROL:
				return getUicontrol();
			case KbdataPackage.LINES_TYPE__MENUCASCADE:
				return getMenucascade();
			case KbdataPackage.LINES_TYPE__B:
				return getB();
			case KbdataPackage.LINES_TYPE__U:
				return getU();
			case KbdataPackage.LINES_TYPE__I:
				return getI();
			case KbdataPackage.LINES_TYPE__TT:
				return getTt();
			case KbdataPackage.LINES_TYPE__SUP:
				return getSup();
			case KbdataPackage.LINES_TYPE__SUB:
				return getSub();
			case KbdataPackage.LINES_TYPE__CODEPH:
				return getCodeph();
			case KbdataPackage.LINES_TYPE__SYNPH:
				return getSynph();
			case KbdataPackage.LINES_TYPE__FILEPATH:
				return getFilepath();
			case KbdataPackage.LINES_TYPE__MSGPH:
				return getMsgph();
			case KbdataPackage.LINES_TYPE__USERINPUT:
				return getUserinput();
			case KbdataPackage.LINES_TYPE__SYSTEMOUTPUT:
				return getSystemoutput();
			case KbdataPackage.LINES_TYPE__TERM:
				return getTerm();
			case KbdataPackage.LINES_TYPE__XREF:
				return getXref();
			case KbdataPackage.LINES_TYPE__CITE:
				return getCite();
			case KbdataPackage.LINES_TYPE__Q:
				return getQ();
			case KbdataPackage.LINES_TYPE__BOOLEAN:
				return getBoolean();
			case KbdataPackage.LINES_TYPE__STATE:
				return getState();
			case KbdataPackage.LINES_TYPE__KEYWORD:
				return getKeyword();
			case KbdataPackage.LINES_TYPE__WINTITLE:
				return getWintitle();
			case KbdataPackage.LINES_TYPE__OPTION:
				return getOption();
			case KbdataPackage.LINES_TYPE__PARMNAME:
				return getParmname();
			case KbdataPackage.LINES_TYPE__APINAME:
				return getApiname();
			case KbdataPackage.LINES_TYPE__CMDNAME:
				return getCmdname();
			case KbdataPackage.LINES_TYPE__MSGNUM:
				return getMsgnum();
			case KbdataPackage.LINES_TYPE__VARNAME:
				return getVarname();
			case KbdataPackage.LINES_TYPE__TM:
				return getTm();
			case KbdataPackage.LINES_TYPE__DRAFT_COMMENT:
				return getDraftComment();
			case KbdataPackage.LINES_TYPE__REQUIRED_CLEANUP:
				return getRequiredCleanup();
			case KbdataPackage.LINES_TYPE__FN:
				return getFn();
			case KbdataPackage.LINES_TYPE__INDEXTERMREF:
				return getIndextermref();
			case KbdataPackage.LINES_TYPE__INDEXTERM:
				return getIndexterm();
			case KbdataPackage.LINES_TYPE__DATA:
				return getData();
			case KbdataPackage.LINES_TYPE__DATA_ABOUT:
				return getDataAbout();
			case KbdataPackage.LINES_TYPE__FOREIGN:
				return getForeign();
			case KbdataPackage.LINES_TYPE__UNKNOWN:
				return getUnknown();
			case KbdataPackage.LINES_TYPE__AUDIENCE:
				return getAudience();
			case KbdataPackage.LINES_TYPE__BASE:
				return getBase();
			case KbdataPackage.LINES_TYPE__CLASS:
				return getClass_();
			case KbdataPackage.LINES_TYPE__CONREF:
				return getConref();
			case KbdataPackage.LINES_TYPE__DIR:
				return getDir();
			case KbdataPackage.LINES_TYPE__EXPANSE:
				return getExpanse();
			case KbdataPackage.LINES_TYPE__FRAME:
				return getFrame();
			case KbdataPackage.LINES_TYPE__ID:
				return getId();
			case KbdataPackage.LINES_TYPE__IMPORTANCE:
				return getImportance();
			case KbdataPackage.LINES_TYPE__LANG:
				return getLang();
			case KbdataPackage.LINES_TYPE__OTHERPROPS:
				return getOtherprops();
			case KbdataPackage.LINES_TYPE__OUTPUTCLASS:
				return getOutputclass();
			case KbdataPackage.LINES_TYPE__PLATFORM:
				return getPlatform();
			case KbdataPackage.LINES_TYPE__PRODUCT:
				return getProduct();
			case KbdataPackage.LINES_TYPE__PROPS:
				return getProps();
			case KbdataPackage.LINES_TYPE__REV:
				return getRev();
			case KbdataPackage.LINES_TYPE__SCALE:
				return getScale();
			case KbdataPackage.LINES_TYPE__SPACE:
				return getSpace();
			case KbdataPackage.LINES_TYPE__SPECTITLE:
				return getSpectitle();
			case KbdataPackage.LINES_TYPE__STATUS:
				return getStatus();
			case KbdataPackage.LINES_TYPE__TRANSLATE:
				return getTranslate();
			case KbdataPackage.LINES_TYPE__XTRC:
				return getXtrc();
			case KbdataPackage.LINES_TYPE__XTRF:
				return getXtrf();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case KbdataPackage.LINES_TYPE__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case KbdataPackage.LINES_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case KbdataPackage.LINES_TYPE__PH:
				getPh().clear();
				getPh().addAll((Collection<? extends PhType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__UICONTROL:
				getUicontrol().clear();
				getUicontrol().addAll((Collection<? extends UicontrolType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__MENUCASCADE:
				getMenucascade().clear();
				getMenucascade().addAll((Collection<? extends MenucascadeType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__B:
				getB().clear();
				getB().addAll((Collection<? extends BType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__U:
				getU().clear();
				getU().addAll((Collection<? extends UType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__I:
				getI().clear();
				getI().addAll((Collection<? extends IType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__TT:
				getTt().clear();
				getTt().addAll((Collection<? extends TtType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SUP:
				getSup().clear();
				getSup().addAll((Collection<? extends SupType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SUB:
				getSub().clear();
				getSub().addAll((Collection<? extends SubType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__CODEPH:
				getCodeph().clear();
				getCodeph().addAll((Collection<? extends CodephType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SYNPH:
				getSynph().clear();
				getSynph().addAll((Collection<? extends SynphType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__FILEPATH:
				getFilepath().clear();
				getFilepath().addAll((Collection<? extends FilepathType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__MSGPH:
				getMsgph().clear();
				getMsgph().addAll((Collection<? extends MsgphType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__USERINPUT:
				getUserinput().clear();
				getUserinput().addAll((Collection<? extends UserinputType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SYSTEMOUTPUT:
				getSystemoutput().clear();
				getSystemoutput().addAll((Collection<? extends SystemoutputType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__TERM:
				getTerm().clear();
				getTerm().addAll((Collection<? extends TermType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__XREF:
				getXref().clear();
				getXref().addAll((Collection<? extends XrefType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__CITE:
				getCite().clear();
				getCite().addAll((Collection<? extends CiteType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__Q:
				getQ().clear();
				getQ().addAll((Collection<? extends QType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__BOOLEAN:
				getBoolean().clear();
				getBoolean().addAll((Collection<? extends BooleanType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__STATE:
				getState().clear();
				getState().addAll((Collection<? extends StateType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__KEYWORD:
				getKeyword().clear();
				getKeyword().addAll((Collection<? extends KeywordType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__WINTITLE:
				getWintitle().clear();
				getWintitle().addAll((Collection<? extends WintitleType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__OPTION:
				getOption().clear();
				getOption().addAll((Collection<? extends OptionType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__PARMNAME:
				getParmname().clear();
				getParmname().addAll((Collection<? extends ParmnameType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__APINAME:
				getApiname().clear();
				getApiname().addAll((Collection<? extends ApinameType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__CMDNAME:
				getCmdname().clear();
				getCmdname().addAll((Collection<? extends CmdnameType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__MSGNUM:
				getMsgnum().clear();
				getMsgnum().addAll((Collection<? extends MsgnumType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__VARNAME:
				getVarname().clear();
				getVarname().addAll((Collection<? extends VarnameType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__TM:
				getTm().clear();
				getTm().addAll((Collection<? extends TmType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__DRAFT_COMMENT:
				getDraftComment().clear();
				getDraftComment().addAll((Collection<? extends DraftCommentType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__REQUIRED_CLEANUP:
				getRequiredCleanup().clear();
				getRequiredCleanup().addAll((Collection<? extends RequiredCleanupType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__FN:
				getFn().clear();
				getFn().addAll((Collection<? extends FnType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__INDEXTERMREF:
				getIndextermref().clear();
				getIndextermref().addAll((Collection<? extends IndextermrefType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__INDEXTERM:
				getIndexterm().clear();
				getIndexterm().addAll((Collection<? extends IndextermType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__DATA:
				getData().clear();
				getData().addAll((Collection<? extends DataType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__DATA_ABOUT:
				getDataAbout().clear();
				getDataAbout().addAll((Collection<? extends DataAboutType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__FOREIGN:
				getForeign().clear();
				getForeign().addAll((Collection<? extends ForeignType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__UNKNOWN:
				getUnknown().clear();
				getUnknown().addAll((Collection<? extends UnknownType>)newValue);
				return;
			case KbdataPackage.LINES_TYPE__AUDIENCE:
				setAudience(newValue);
				return;
			case KbdataPackage.LINES_TYPE__BASE:
				setBase(newValue);
				return;
			case KbdataPackage.LINES_TYPE__CLASS:
				setClass(newValue);
				return;
			case KbdataPackage.LINES_TYPE__CONREF:
				setConref(newValue);
				return;
			case KbdataPackage.LINES_TYPE__DIR:
				setDir((DirType49)newValue);
				return;
			case KbdataPackage.LINES_TYPE__EXPANSE:
				setExpanse((ExpanseType)newValue);
				return;
			case KbdataPackage.LINES_TYPE__FRAME:
				setFrame((FrameType3)newValue);
				return;
			case KbdataPackage.LINES_TYPE__ID:
				setId((String)newValue);
				return;
			case KbdataPackage.LINES_TYPE__IMPORTANCE:
				setImportance((ImportanceType51)newValue);
				return;
			case KbdataPackage.LINES_TYPE__LANG:
				setLang((String)newValue);
				return;
			case KbdataPackage.LINES_TYPE__OTHERPROPS:
				setOtherprops(newValue);
				return;
			case KbdataPackage.LINES_TYPE__OUTPUTCLASS:
				setOutputclass(newValue);
				return;
			case KbdataPackage.LINES_TYPE__PLATFORM:
				setPlatform(newValue);
				return;
			case KbdataPackage.LINES_TYPE__PRODUCT:
				setProduct(newValue);
				return;
			case KbdataPackage.LINES_TYPE__PROPS:
				setProps(newValue);
				return;
			case KbdataPackage.LINES_TYPE__REV:
				setRev(newValue);
				return;
			case KbdataPackage.LINES_TYPE__SCALE:
				setScale((ScaleType7)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SPACE:
				setSpace((SpaceType)newValue);
				return;
			case KbdataPackage.LINES_TYPE__SPECTITLE:
				setSpectitle(newValue);
				return;
			case KbdataPackage.LINES_TYPE__STATUS:
				setStatus((StatusType26)newValue);
				return;
			case KbdataPackage.LINES_TYPE__TRANSLATE:
				setTranslate((TranslateType49)newValue);
				return;
			case KbdataPackage.LINES_TYPE__XTRC:
				setXtrc(newValue);
				return;
			case KbdataPackage.LINES_TYPE__XTRF:
				setXtrf(newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case KbdataPackage.LINES_TYPE__MIXED:
				getMixed().clear();
				return;
			case KbdataPackage.LINES_TYPE__GROUP:
				getGroup().clear();
				return;
			case KbdataPackage.LINES_TYPE__PH:
				getPh().clear();
				return;
			case KbdataPackage.LINES_TYPE__UICONTROL:
				getUicontrol().clear();
				return;
			case KbdataPackage.LINES_TYPE__MENUCASCADE:
				getMenucascade().clear();
				return;
			case KbdataPackage.LINES_TYPE__B:
				getB().clear();
				return;
			case KbdataPackage.LINES_TYPE__U:
				getU().clear();
				return;
			case KbdataPackage.LINES_TYPE__I:
				getI().clear();
				return;
			case KbdataPackage.LINES_TYPE__TT:
				getTt().clear();
				return;
			case KbdataPackage.LINES_TYPE__SUP:
				getSup().clear();
				return;
			case KbdataPackage.LINES_TYPE__SUB:
				getSub().clear();
				return;
			case KbdataPackage.LINES_TYPE__CODEPH:
				getCodeph().clear();
				return;
			case KbdataPackage.LINES_TYPE__SYNPH:
				getSynph().clear();
				return;
			case KbdataPackage.LINES_TYPE__FILEPATH:
				getFilepath().clear();
				return;
			case KbdataPackage.LINES_TYPE__MSGPH:
				getMsgph().clear();
				return;
			case KbdataPackage.LINES_TYPE__USERINPUT:
				getUserinput().clear();
				return;
			case KbdataPackage.LINES_TYPE__SYSTEMOUTPUT:
				getSystemoutput().clear();
				return;
			case KbdataPackage.LINES_TYPE__TERM:
				getTerm().clear();
				return;
			case KbdataPackage.LINES_TYPE__XREF:
				getXref().clear();
				return;
			case KbdataPackage.LINES_TYPE__CITE:
				getCite().clear();
				return;
			case KbdataPackage.LINES_TYPE__Q:
				getQ().clear();
				return;
			case KbdataPackage.LINES_TYPE__BOOLEAN:
				getBoolean().clear();
				return;
			case KbdataPackage.LINES_TYPE__STATE:
				getState().clear();
				return;
			case KbdataPackage.LINES_TYPE__KEYWORD:
				getKeyword().clear();
				return;
			case KbdataPackage.LINES_TYPE__WINTITLE:
				getWintitle().clear();
				return;
			case KbdataPackage.LINES_TYPE__OPTION:
				getOption().clear();
				return;
			case KbdataPackage.LINES_TYPE__PARMNAME:
				getParmname().clear();
				return;
			case KbdataPackage.LINES_TYPE__APINAME:
				getApiname().clear();
				return;
			case KbdataPackage.LINES_TYPE__CMDNAME:
				getCmdname().clear();
				return;
			case KbdataPackage.LINES_TYPE__MSGNUM:
				getMsgnum().clear();
				return;
			case KbdataPackage.LINES_TYPE__VARNAME:
				getVarname().clear();
				return;
			case KbdataPackage.LINES_TYPE__TM:
				getTm().clear();
				return;
			case KbdataPackage.LINES_TYPE__DRAFT_COMMENT:
				getDraftComment().clear();
				return;
			case KbdataPackage.LINES_TYPE__REQUIRED_CLEANUP:
				getRequiredCleanup().clear();
				return;
			case KbdataPackage.LINES_TYPE__FN:
				getFn().clear();
				return;
			case KbdataPackage.LINES_TYPE__INDEXTERMREF:
				getIndextermref().clear();
				return;
			case KbdataPackage.LINES_TYPE__INDEXTERM:
				getIndexterm().clear();
				return;
			case KbdataPackage.LINES_TYPE__DATA:
				getData().clear();
				return;
			case KbdataPackage.LINES_TYPE__DATA_ABOUT:
				getDataAbout().clear();
				return;
			case KbdataPackage.LINES_TYPE__FOREIGN:
				getForeign().clear();
				return;
			case KbdataPackage.LINES_TYPE__UNKNOWN:
				getUnknown().clear();
				return;
			case KbdataPackage.LINES_TYPE__AUDIENCE:
				setAudience(AUDIENCE_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__BASE:
				setBase(BASE_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__CLASS:
				unsetClass();
				return;
			case KbdataPackage.LINES_TYPE__CONREF:
				setConref(CONREF_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__DIR:
				unsetDir();
				return;
			case KbdataPackage.LINES_TYPE__EXPANSE:
				unsetExpanse();
				return;
			case KbdataPackage.LINES_TYPE__FRAME:
				unsetFrame();
				return;
			case KbdataPackage.LINES_TYPE__ID:
				setId(ID_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__IMPORTANCE:
				unsetImportance();
				return;
			case KbdataPackage.LINES_TYPE__LANG:
				setLang(LANG_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__OTHERPROPS:
				setOtherprops(OTHERPROPS_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__OUTPUTCLASS:
				setOutputclass(OUTPUTCLASS_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__PLATFORM:
				setPlatform(PLATFORM_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__PRODUCT:
				setProduct(PRODUCT_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__PROPS:
				setProps(PROPS_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__REV:
				setRev(REV_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__SCALE:
				unsetScale();
				return;
			case KbdataPackage.LINES_TYPE__SPACE:
				unsetSpace();
				return;
			case KbdataPackage.LINES_TYPE__SPECTITLE:
				setSpectitle(SPECTITLE_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__STATUS:
				unsetStatus();
				return;
			case KbdataPackage.LINES_TYPE__TRANSLATE:
				unsetTranslate();
				return;
			case KbdataPackage.LINES_TYPE__XTRC:
				setXtrc(XTRC_EDEFAULT);
				return;
			case KbdataPackage.LINES_TYPE__XTRF:
				setXtrf(XTRF_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case KbdataPackage.LINES_TYPE__MIXED:
				return mixed != null && !mixed.isEmpty();
			case KbdataPackage.LINES_TYPE__GROUP:
				return !getGroup().isEmpty();
			case KbdataPackage.LINES_TYPE__PH:
				return !getPh().isEmpty();
			case KbdataPackage.LINES_TYPE__UICONTROL:
				return !getUicontrol().isEmpty();
			case KbdataPackage.LINES_TYPE__MENUCASCADE:
				return !getMenucascade().isEmpty();
			case KbdataPackage.LINES_TYPE__B:
				return !getB().isEmpty();
			case KbdataPackage.LINES_TYPE__U:
				return !getU().isEmpty();
			case KbdataPackage.LINES_TYPE__I:
				return !getI().isEmpty();
			case KbdataPackage.LINES_TYPE__TT:
				return !getTt().isEmpty();
			case KbdataPackage.LINES_TYPE__SUP:
				return !getSup().isEmpty();
			case KbdataPackage.LINES_TYPE__SUB:
				return !getSub().isEmpty();
			case KbdataPackage.LINES_TYPE__CODEPH:
				return !getCodeph().isEmpty();
			case KbdataPackage.LINES_TYPE__SYNPH:
				return !getSynph().isEmpty();
			case KbdataPackage.LINES_TYPE__FILEPATH:
				return !getFilepath().isEmpty();
			case KbdataPackage.LINES_TYPE__MSGPH:
				return !getMsgph().isEmpty();
			case KbdataPackage.LINES_TYPE__USERINPUT:
				return !getUserinput().isEmpty();
			case KbdataPackage.LINES_TYPE__SYSTEMOUTPUT:
				return !getSystemoutput().isEmpty();
			case KbdataPackage.LINES_TYPE__TERM:
				return !getTerm().isEmpty();
			case KbdataPackage.LINES_TYPE__XREF:
				return !getXref().isEmpty();
			case KbdataPackage.LINES_TYPE__CITE:
				return !getCite().isEmpty();
			case KbdataPackage.LINES_TYPE__Q:
				return !getQ().isEmpty();
			case KbdataPackage.LINES_TYPE__BOOLEAN:
				return !getBoolean().isEmpty();
			case KbdataPackage.LINES_TYPE__STATE:
				return !getState().isEmpty();
			case KbdataPackage.LINES_TYPE__KEYWORD:
				return !getKeyword().isEmpty();
			case KbdataPackage.LINES_TYPE__WINTITLE:
				return !getWintitle().isEmpty();
			case KbdataPackage.LINES_TYPE__OPTION:
				return !getOption().isEmpty();
			case KbdataPackage.LINES_TYPE__PARMNAME:
				return !getParmname().isEmpty();
			case KbdataPackage.LINES_TYPE__APINAME:
				return !getApiname().isEmpty();
			case KbdataPackage.LINES_TYPE__CMDNAME:
				return !getCmdname().isEmpty();
			case KbdataPackage.LINES_TYPE__MSGNUM:
				return !getMsgnum().isEmpty();
			case KbdataPackage.LINES_TYPE__VARNAME:
				return !getVarname().isEmpty();
			case KbdataPackage.LINES_TYPE__TM:
				return !getTm().isEmpty();
			case KbdataPackage.LINES_TYPE__DRAFT_COMMENT:
				return !getDraftComment().isEmpty();
			case KbdataPackage.LINES_TYPE__REQUIRED_CLEANUP:
				return !getRequiredCleanup().isEmpty();
			case KbdataPackage.LINES_TYPE__FN:
				return !getFn().isEmpty();
			case KbdataPackage.LINES_TYPE__INDEXTERMREF:
				return !getIndextermref().isEmpty();
			case KbdataPackage.LINES_TYPE__INDEXTERM:
				return !getIndexterm().isEmpty();
			case KbdataPackage.LINES_TYPE__DATA:
				return !getData().isEmpty();
			case KbdataPackage.LINES_TYPE__DATA_ABOUT:
				return !getDataAbout().isEmpty();
			case KbdataPackage.LINES_TYPE__FOREIGN:
				return !getForeign().isEmpty();
			case KbdataPackage.LINES_TYPE__UNKNOWN:
				return !getUnknown().isEmpty();
			case KbdataPackage.LINES_TYPE__AUDIENCE:
				return AUDIENCE_EDEFAULT == null ? audience != null : !AUDIENCE_EDEFAULT.equals(audience);
			case KbdataPackage.LINES_TYPE__BASE:
				return BASE_EDEFAULT == null ? base != null : !BASE_EDEFAULT.equals(base);
			case KbdataPackage.LINES_TYPE__CLASS:
				return isSetClass();
			case KbdataPackage.LINES_TYPE__CONREF:
				return CONREF_EDEFAULT == null ? conref != null : !CONREF_EDEFAULT.equals(conref);
			case KbdataPackage.LINES_TYPE__DIR:
				return isSetDir();
			case KbdataPackage.LINES_TYPE__EXPANSE:
				return isSetExpanse();
			case KbdataPackage.LINES_TYPE__FRAME:
				return isSetFrame();
			case KbdataPackage.LINES_TYPE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case KbdataPackage.LINES_TYPE__IMPORTANCE:
				return isSetImportance();
			case KbdataPackage.LINES_TYPE__LANG:
				return LANG_EDEFAULT == null ? lang != null : !LANG_EDEFAULT.equals(lang);
			case KbdataPackage.LINES_TYPE__OTHERPROPS:
				return OTHERPROPS_EDEFAULT == null ? otherprops != null : !OTHERPROPS_EDEFAULT.equals(otherprops);
			case KbdataPackage.LINES_TYPE__OUTPUTCLASS:
				return OUTPUTCLASS_EDEFAULT == null ? outputclass != null : !OUTPUTCLASS_EDEFAULT.equals(outputclass);
			case KbdataPackage.LINES_TYPE__PLATFORM:
				return PLATFORM_EDEFAULT == null ? platform != null : !PLATFORM_EDEFAULT.equals(platform);
			case KbdataPackage.LINES_TYPE__PRODUCT:
				return PRODUCT_EDEFAULT == null ? product != null : !PRODUCT_EDEFAULT.equals(product);
			case KbdataPackage.LINES_TYPE__PROPS:
				return PROPS_EDEFAULT == null ? props != null : !PROPS_EDEFAULT.equals(props);
			case KbdataPackage.LINES_TYPE__REV:
				return REV_EDEFAULT == null ? rev != null : !REV_EDEFAULT.equals(rev);
			case KbdataPackage.LINES_TYPE__SCALE:
				return isSetScale();
			case KbdataPackage.LINES_TYPE__SPACE:
				return isSetSpace();
			case KbdataPackage.LINES_TYPE__SPECTITLE:
				return SPECTITLE_EDEFAULT == null ? spectitle != null : !SPECTITLE_EDEFAULT.equals(spectitle);
			case KbdataPackage.LINES_TYPE__STATUS:
				return isSetStatus();
			case KbdataPackage.LINES_TYPE__TRANSLATE:
				return isSetTranslate();
			case KbdataPackage.LINES_TYPE__XTRC:
				return XTRC_EDEFAULT == null ? xtrc != null : !XTRC_EDEFAULT.equals(xtrc);
			case KbdataPackage.LINES_TYPE__XTRF:
				return XTRF_EDEFAULT == null ? xtrf != null : !XTRF_EDEFAULT.equals(xtrf);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(", audience: ");
		result.append(audience);
		result.append(", base: ");
		result.append(base);
		result.append(", class: ");
		if (classESet) result.append(class_); else result.append("<unset>");
		result.append(", conref: ");
		result.append(conref);
		result.append(", dir: ");
		if (dirESet) result.append(dir); else result.append("<unset>");
		result.append(", expanse: ");
		if (expanseESet) result.append(expanse); else result.append("<unset>");
		result.append(", frame: ");
		if (frameESet) result.append(frame); else result.append("<unset>");
		result.append(", id: ");
		result.append(id);
		result.append(", importance: ");
		if (importanceESet) result.append(importance); else result.append("<unset>");
		result.append(", lang: ");
		result.append(lang);
		result.append(", otherprops: ");
		result.append(otherprops);
		result.append(", outputclass: ");
		result.append(outputclass);
		result.append(", platform: ");
		result.append(platform);
		result.append(", product: ");
		result.append(product);
		result.append(", props: ");
		result.append(props);
		result.append(", rev: ");
		result.append(rev);
		result.append(", scale: ");
		if (scaleESet) result.append(scale); else result.append("<unset>");
		result.append(", space: ");
		if (spaceESet) result.append(space); else result.append("<unset>");
		result.append(", spectitle: ");
		result.append(spectitle);
		result.append(", status: ");
		if (statusESet) result.append(status); else result.append("<unset>");
		result.append(", translate: ");
		if (translateESet) result.append(translate); else result.append("<unset>");
		result.append(", xtrc: ");
		result.append(xtrc);
		result.append(", xtrf: ");
		result.append(xtrf);
		result.append(')');
		return result.toString();
	}

} //LinesTypeImpl
