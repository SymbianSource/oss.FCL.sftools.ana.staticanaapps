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

import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.DirType149;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ImportanceType155;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.KbdataPackage;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.LinkType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.LinklistType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.LinkpoolType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.RelatedLinksType;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.RoleType1;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.ScopeType4;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.StatusType102;
import com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.TranslateType163;

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
 * An implementation of the model object '<em><b>Related Links Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getLink <em>Link</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getLinklist <em>Linklist</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getLinkpool <em>Linkpool</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getAudience <em>Audience</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getBase <em>Base</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getClass_ <em>Class</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getConref <em>Conref</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getDir <em>Dir</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getImportance <em>Importance</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getLang <em>Lang</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getOtherprops <em>Otherprops</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getOtherrole <em>Otherrole</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getOutputclass <em>Outputclass</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getPlatform <em>Platform</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getProps <em>Props</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getRev <em>Rev</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getRole <em>Role</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getScope <em>Scope</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getStatus <em>Status</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getTranslate <em>Translate</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getXtrc <em>Xtrc</em>}</li>
 *   <li>{@link com.nokia.carbide.cpp.internal.codescanner.gen.Kbdata.impl.RelatedLinksTypeImpl#getXtrf <em>Xtrf</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelatedLinksTypeImpl extends EObjectImpl implements RelatedLinksType {
	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group;

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
	protected static final Object CLASS_EDEFAULT = "- topic/related-links ";

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
	protected static final DirType149 DIR_EDEFAULT = DirType149.LTR;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected DirType149 dir = DIR_EDEFAULT;

	/**
	 * This is true if the Dir attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean dirESet;

	/**
	 * The default value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected static final Object FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected Object format = FORMAT_EDEFAULT;

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
	protected static final ImportanceType155 IMPORTANCE_EDEFAULT = ImportanceType155.OBSOLETE;

	/**
	 * The cached value of the '{@link #getImportance() <em>Importance</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportance()
	 * @generated
	 * @ordered
	 */
	protected ImportanceType155 importance = IMPORTANCE_EDEFAULT;

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
	 * The default value of the '{@link #getOtherrole() <em>Otherrole</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherrole()
	 * @generated
	 * @ordered
	 */
	protected static final Object OTHERROLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOtherrole() <em>Otherrole</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherrole()
	 * @generated
	 * @ordered
	 */
	protected Object otherrole = OTHERROLE_EDEFAULT;

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
	 * The default value of the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected static final RoleType1 ROLE_EDEFAULT = RoleType1.PARENT;

	/**
	 * The cached value of the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected RoleType1 role = ROLE_EDEFAULT;

	/**
	 * This is true if the Role attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean roleESet;

	/**
	 * The default value of the '{@link #getScope() <em>Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScope()
	 * @generated
	 * @ordered
	 */
	protected static final ScopeType4 SCOPE_EDEFAULT = ScopeType4.LOCAL;

	/**
	 * The cached value of the '{@link #getScope() <em>Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScope()
	 * @generated
	 * @ordered
	 */
	protected ScopeType4 scope = SCOPE_EDEFAULT;

	/**
	 * This is true if the Scope attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean scopeESet;

	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final StatusType102 STATUS_EDEFAULT = StatusType102.NEW;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected StatusType102 status = STATUS_EDEFAULT;

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
	protected static final TranslateType163 TRANSLATE_EDEFAULT = TranslateType163.YES;

	/**
	 * The cached value of the '{@link #getTranslate() <em>Translate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTranslate()
	 * @generated
	 * @ordered
	 */
	protected TranslateType163 translate = TRANSLATE_EDEFAULT;

	/**
	 * This is true if the Translate attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean translateESet;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final Object TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected Object type = TYPE_EDEFAULT;

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
	protected RelatedLinksTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return KbdataPackage.eINSTANCE.getRelatedLinksType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, KbdataPackage.RELATED_LINKS_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<LinkType> getLink() {
		return getGroup().list(KbdataPackage.eINSTANCE.getRelatedLinksType_Link());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<LinklistType> getLinklist() {
		return getGroup().list(KbdataPackage.eINSTANCE.getRelatedLinksType_Linklist());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<LinkpoolType> getLinkpool() {
		return getGroup().list(KbdataPackage.eINSTANCE.getRelatedLinksType_Linkpool());
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__AUDIENCE, oldAudience, audience));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__BASE, oldBase, base));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__CLASS, oldClass, class_, !oldClassESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__CLASS, oldClass, CLASS_EDEFAULT, oldClassESet));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__CONREF, oldConref, conref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirType149 getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(DirType149 newDir) {
		DirType149 oldDir = dir;
		dir = newDir == null ? DIR_EDEFAULT : newDir;
		boolean oldDirESet = dirESet;
		dirESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__DIR, oldDir, dir, !oldDirESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDir() {
		DirType149 oldDir = dir;
		boolean oldDirESet = dirESet;
		dir = DIR_EDEFAULT;
		dirESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__DIR, oldDir, DIR_EDEFAULT, oldDirESet));
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
	public Object getFormat() {
		return format;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFormat(Object newFormat) {
		Object oldFormat = format;
		format = newFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__FORMAT, oldFormat, format));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportanceType155 getImportance() {
		return importance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImportance(ImportanceType155 newImportance) {
		ImportanceType155 oldImportance = importance;
		importance = newImportance == null ? IMPORTANCE_EDEFAULT : newImportance;
		boolean oldImportanceESet = importanceESet;
		importanceESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE, oldImportance, importance, !oldImportanceESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetImportance() {
		ImportanceType155 oldImportance = importance;
		boolean oldImportanceESet = importanceESet;
		importance = IMPORTANCE_EDEFAULT;
		importanceESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE, oldImportance, IMPORTANCE_EDEFAULT, oldImportanceESet));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__LANG, oldLang, lang));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__OTHERPROPS, oldOtherprops, otherprops));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getOtherrole() {
		return otherrole;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOtherrole(Object newOtherrole) {
		Object oldOtherrole = otherrole;
		otherrole = newOtherrole;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__OTHERROLE, oldOtherrole, otherrole));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__OUTPUTCLASS, oldOutputclass, outputclass));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__PLATFORM, oldPlatform, platform));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__PRODUCT, oldProduct, product));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__PROPS, oldProps, props));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__REV, oldRev, rev));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RoleType1 getRole() {
		return role;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRole(RoleType1 newRole) {
		RoleType1 oldRole = role;
		role = newRole == null ? ROLE_EDEFAULT : newRole;
		boolean oldRoleESet = roleESet;
		roleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__ROLE, oldRole, role, !oldRoleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRole() {
		RoleType1 oldRole = role;
		boolean oldRoleESet = roleESet;
		role = ROLE_EDEFAULT;
		roleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__ROLE, oldRole, ROLE_EDEFAULT, oldRoleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRole() {
		return roleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScopeType4 getScope() {
		return scope;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setScope(ScopeType4 newScope) {
		ScopeType4 oldScope = scope;
		scope = newScope == null ? SCOPE_EDEFAULT : newScope;
		boolean oldScopeESet = scopeESet;
		scopeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__SCOPE, oldScope, scope, !oldScopeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetScope() {
		ScopeType4 oldScope = scope;
		boolean oldScopeESet = scopeESet;
		scope = SCOPE_EDEFAULT;
		scopeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__SCOPE, oldScope, SCOPE_EDEFAULT, oldScopeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetScope() {
		return scopeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StatusType102 getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStatus(StatusType102 newStatus) {
		StatusType102 oldStatus = status;
		status = newStatus == null ? STATUS_EDEFAULT : newStatus;
		boolean oldStatusESet = statusESet;
		statusESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__STATUS, oldStatus, status, !oldStatusESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStatus() {
		StatusType102 oldStatus = status;
		boolean oldStatusESet = statusESet;
		status = STATUS_EDEFAULT;
		statusESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__STATUS, oldStatus, STATUS_EDEFAULT, oldStatusESet));
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
	public TranslateType163 getTranslate() {
		return translate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTranslate(TranslateType163 newTranslate) {
		TranslateType163 oldTranslate = translate;
		translate = newTranslate == null ? TRANSLATE_EDEFAULT : newTranslate;
		boolean oldTranslateESet = translateESet;
		translateESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE, oldTranslate, translate, !oldTranslateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTranslate() {
		TranslateType163 oldTranslate = translate;
		boolean oldTranslateESet = translateESet;
		translate = TRANSLATE_EDEFAULT;
		translateESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE, oldTranslate, TRANSLATE_EDEFAULT, oldTranslateESet));
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
	public Object getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(Object newType) {
		Object oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__TYPE, oldType, type));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__XTRC, oldXtrc, xtrc));
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
			eNotify(new ENotificationImpl(this, Notification.SET, KbdataPackage.RELATED_LINKS_TYPE__XTRF, oldXtrf, xtrf));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case KbdataPackage.RELATED_LINKS_TYPE__GROUP:
				return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
			case KbdataPackage.RELATED_LINKS_TYPE__LINK:
				return ((InternalEList<?>)getLink()).basicRemove(otherEnd, msgs);
			case KbdataPackage.RELATED_LINKS_TYPE__LINKLIST:
				return ((InternalEList<?>)getLinklist()).basicRemove(otherEnd, msgs);
			case KbdataPackage.RELATED_LINKS_TYPE__LINKPOOL:
				return ((InternalEList<?>)getLinkpool()).basicRemove(otherEnd, msgs);
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
			case KbdataPackage.RELATED_LINKS_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case KbdataPackage.RELATED_LINKS_TYPE__LINK:
				return getLink();
			case KbdataPackage.RELATED_LINKS_TYPE__LINKLIST:
				return getLinklist();
			case KbdataPackage.RELATED_LINKS_TYPE__LINKPOOL:
				return getLinkpool();
			case KbdataPackage.RELATED_LINKS_TYPE__AUDIENCE:
				return getAudience();
			case KbdataPackage.RELATED_LINKS_TYPE__BASE:
				return getBase();
			case KbdataPackage.RELATED_LINKS_TYPE__CLASS:
				return getClass_();
			case KbdataPackage.RELATED_LINKS_TYPE__CONREF:
				return getConref();
			case KbdataPackage.RELATED_LINKS_TYPE__DIR:
				return getDir();
			case KbdataPackage.RELATED_LINKS_TYPE__FORMAT:
				return getFormat();
			case KbdataPackage.RELATED_LINKS_TYPE__ID:
				return getId();
			case KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE:
				return getImportance();
			case KbdataPackage.RELATED_LINKS_TYPE__LANG:
				return getLang();
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERPROPS:
				return getOtherprops();
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERROLE:
				return getOtherrole();
			case KbdataPackage.RELATED_LINKS_TYPE__OUTPUTCLASS:
				return getOutputclass();
			case KbdataPackage.RELATED_LINKS_TYPE__PLATFORM:
				return getPlatform();
			case KbdataPackage.RELATED_LINKS_TYPE__PRODUCT:
				return getProduct();
			case KbdataPackage.RELATED_LINKS_TYPE__PROPS:
				return getProps();
			case KbdataPackage.RELATED_LINKS_TYPE__REV:
				return getRev();
			case KbdataPackage.RELATED_LINKS_TYPE__ROLE:
				return getRole();
			case KbdataPackage.RELATED_LINKS_TYPE__SCOPE:
				return getScope();
			case KbdataPackage.RELATED_LINKS_TYPE__STATUS:
				return getStatus();
			case KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE:
				return getTranslate();
			case KbdataPackage.RELATED_LINKS_TYPE__TYPE:
				return getType();
			case KbdataPackage.RELATED_LINKS_TYPE__XTRC:
				return getXtrc();
			case KbdataPackage.RELATED_LINKS_TYPE__XTRF:
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
			case KbdataPackage.RELATED_LINKS_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINK:
				getLink().clear();
				getLink().addAll((Collection<? extends LinkType>)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINKLIST:
				getLinklist().clear();
				getLinklist().addAll((Collection<? extends LinklistType>)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINKPOOL:
				getLinkpool().clear();
				getLinkpool().addAll((Collection<? extends LinkpoolType>)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__AUDIENCE:
				setAudience(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__BASE:
				setBase(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__CLASS:
				setClass(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__CONREF:
				setConref(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__DIR:
				setDir((DirType149)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__FORMAT:
				setFormat(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__ID:
				setId((String)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE:
				setImportance((ImportanceType155)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LANG:
				setLang((String)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERPROPS:
				setOtherprops(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERROLE:
				setOtherrole(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OUTPUTCLASS:
				setOutputclass(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PLATFORM:
				setPlatform(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PRODUCT:
				setProduct(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PROPS:
				setProps(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__REV:
				setRev(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__ROLE:
				setRole((RoleType1)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__SCOPE:
				setScope((ScopeType4)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__STATUS:
				setStatus((StatusType102)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE:
				setTranslate((TranslateType163)newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__TYPE:
				setType(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__XTRC:
				setXtrc(newValue);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__XTRF:
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
			case KbdataPackage.RELATED_LINKS_TYPE__GROUP:
				getGroup().clear();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINK:
				getLink().clear();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINKLIST:
				getLinklist().clear();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LINKPOOL:
				getLinkpool().clear();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__AUDIENCE:
				setAudience(AUDIENCE_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__BASE:
				setBase(BASE_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__CLASS:
				unsetClass();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__CONREF:
				setConref(CONREF_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__DIR:
				unsetDir();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__ID:
				setId(ID_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE:
				unsetImportance();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__LANG:
				setLang(LANG_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERPROPS:
				setOtherprops(OTHERPROPS_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERROLE:
				setOtherrole(OTHERROLE_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__OUTPUTCLASS:
				setOutputclass(OUTPUTCLASS_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PLATFORM:
				setPlatform(PLATFORM_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PRODUCT:
				setProduct(PRODUCT_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__PROPS:
				setProps(PROPS_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__REV:
				setRev(REV_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__ROLE:
				unsetRole();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__SCOPE:
				unsetScope();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__STATUS:
				unsetStatus();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE:
				unsetTranslate();
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__XTRC:
				setXtrc(XTRC_EDEFAULT);
				return;
			case KbdataPackage.RELATED_LINKS_TYPE__XTRF:
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
			case KbdataPackage.RELATED_LINKS_TYPE__GROUP:
				return group != null && !group.isEmpty();
			case KbdataPackage.RELATED_LINKS_TYPE__LINK:
				return !getLink().isEmpty();
			case KbdataPackage.RELATED_LINKS_TYPE__LINKLIST:
				return !getLinklist().isEmpty();
			case KbdataPackage.RELATED_LINKS_TYPE__LINKPOOL:
				return !getLinkpool().isEmpty();
			case KbdataPackage.RELATED_LINKS_TYPE__AUDIENCE:
				return AUDIENCE_EDEFAULT == null ? audience != null : !AUDIENCE_EDEFAULT.equals(audience);
			case KbdataPackage.RELATED_LINKS_TYPE__BASE:
				return BASE_EDEFAULT == null ? base != null : !BASE_EDEFAULT.equals(base);
			case KbdataPackage.RELATED_LINKS_TYPE__CLASS:
				return isSetClass();
			case KbdataPackage.RELATED_LINKS_TYPE__CONREF:
				return CONREF_EDEFAULT == null ? conref != null : !CONREF_EDEFAULT.equals(conref);
			case KbdataPackage.RELATED_LINKS_TYPE__DIR:
				return isSetDir();
			case KbdataPackage.RELATED_LINKS_TYPE__FORMAT:
				return FORMAT_EDEFAULT == null ? format != null : !FORMAT_EDEFAULT.equals(format);
			case KbdataPackage.RELATED_LINKS_TYPE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case KbdataPackage.RELATED_LINKS_TYPE__IMPORTANCE:
				return isSetImportance();
			case KbdataPackage.RELATED_LINKS_TYPE__LANG:
				return LANG_EDEFAULT == null ? lang != null : !LANG_EDEFAULT.equals(lang);
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERPROPS:
				return OTHERPROPS_EDEFAULT == null ? otherprops != null : !OTHERPROPS_EDEFAULT.equals(otherprops);
			case KbdataPackage.RELATED_LINKS_TYPE__OTHERROLE:
				return OTHERROLE_EDEFAULT == null ? otherrole != null : !OTHERROLE_EDEFAULT.equals(otherrole);
			case KbdataPackage.RELATED_LINKS_TYPE__OUTPUTCLASS:
				return OUTPUTCLASS_EDEFAULT == null ? outputclass != null : !OUTPUTCLASS_EDEFAULT.equals(outputclass);
			case KbdataPackage.RELATED_LINKS_TYPE__PLATFORM:
				return PLATFORM_EDEFAULT == null ? platform != null : !PLATFORM_EDEFAULT.equals(platform);
			case KbdataPackage.RELATED_LINKS_TYPE__PRODUCT:
				return PRODUCT_EDEFAULT == null ? product != null : !PRODUCT_EDEFAULT.equals(product);
			case KbdataPackage.RELATED_LINKS_TYPE__PROPS:
				return PROPS_EDEFAULT == null ? props != null : !PROPS_EDEFAULT.equals(props);
			case KbdataPackage.RELATED_LINKS_TYPE__REV:
				return REV_EDEFAULT == null ? rev != null : !REV_EDEFAULT.equals(rev);
			case KbdataPackage.RELATED_LINKS_TYPE__ROLE:
				return isSetRole();
			case KbdataPackage.RELATED_LINKS_TYPE__SCOPE:
				return isSetScope();
			case KbdataPackage.RELATED_LINKS_TYPE__STATUS:
				return isSetStatus();
			case KbdataPackage.RELATED_LINKS_TYPE__TRANSLATE:
				return isSetTranslate();
			case KbdataPackage.RELATED_LINKS_TYPE__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case KbdataPackage.RELATED_LINKS_TYPE__XTRC:
				return XTRC_EDEFAULT == null ? xtrc != null : !XTRC_EDEFAULT.equals(xtrc);
			case KbdataPackage.RELATED_LINKS_TYPE__XTRF:
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
		result.append(" (group: ");
		result.append(group);
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
		result.append(", format: ");
		result.append(format);
		result.append(", id: ");
		result.append(id);
		result.append(", importance: ");
		if (importanceESet) result.append(importance); else result.append("<unset>");
		result.append(", lang: ");
		result.append(lang);
		result.append(", otherprops: ");
		result.append(otherprops);
		result.append(", otherrole: ");
		result.append(otherrole);
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
		result.append(", role: ");
		if (roleESet) result.append(role); else result.append("<unset>");
		result.append(", scope: ");
		if (scopeESet) result.append(scope); else result.append("<unset>");
		result.append(", status: ");
		if (statusESet) result.append(status); else result.append("<unset>");
		result.append(", translate: ");
		if (translateESet) result.append(translate); else result.append("<unset>");
		result.append(", type: ");
		result.append(type);
		result.append(", xtrc: ");
		result.append(xtrc);
		result.append(", xtrf: ");
		result.append(xtrf);
		result.append(')');
		return result.toString();
	}

} //RelatedLinksTypeImpl