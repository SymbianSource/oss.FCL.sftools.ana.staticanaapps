<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:lxslt="http://xml.apache.org/xslt">

	<!-- 
	*******************************************************************************
	* Copyright (c) 2006 Nokia Corporation and/or its subsidiary(-ies). 
	* All rights reserved.
	* This component and the accompanying materials are made available
	* under the terms of the License "Symbian Foundation License v1.0"
	* which accompanies this distribution, and is available
	* at the URL "http://www.symbianfoundation.org/legal/sfl-v10.html".
	*
	* Initial Contributors:
	* Nokia Corporation - initial contribution.
	*
	* Contributors:
	*
	* Description:
	*******************************************************************************
	-->  

<xsl:template match="/">
  <html>
  <head>
  
	  <title>  
	    <xsl:value-of select="report/isUsedByData/component/@name"/> is used by;
		SDK Name: <xsl:value-of select="report/info/sdk/@name"/>
		Target:  <xsl:value-of select="report/info/sdk/@target"/>
		Build: <xsl:value-of select="report/info/sdk/@build"/>		
	  </title>
  

	<!-- 
	*******************************************************************************
	*
	* Styles
	*	
	*******************************************************************************
	-->  
  
  	<style type="text/css">

	body {
		background-color: white;
		font-family: Verdana;
		font-size: 10px;
	}
	p {
		font-family="Verdana";
		font-size: 10px;
	}
	p.right {
		font-family="Verdana";
		font-size: 10px;
		text-align: right;
	}
	td {
		font-family="Verdana";
		font-size: 10px;
		text-align: left;
	}
	td.right {
		font-family="Verdana";
		font-size: 10px;
		text-align: right;
	}

	th {
		font-family="Verdana";
		font-size: 10px;
		font-weight: bold;
		text-align: left;		
	}
	th.properties {
		font-family="Verdana";
		font-size: 10px;
		font-weight: bold;
		text-align: left;
		width: 90px;		
	}
	
	th.isUsedBy {
		font-family="Verdana";
		font-size: 10px;
		font-weight: bold;
		text-align: left;		
	}	
	th.isUsedBySorted {
		font-family="Verdana";
		font-size: 10px;
		font-weight: bold;
		text-align: left;	
		background-color: silver;	
	}	
	
	caption {
		font-family="Verdana";
		font-size: 10px;
		font-weight: normal;
		font-style: italic;
		text-align: left;
		
	}	
	dt {
		font-family="Verdana";
		font-size: 10px;
		font-weight: normal
		text-align: left;
	}	
	
	h1 {
		font-family="Verdana";
		font-size: 18px;
	}		
	h2 {
		font-family="Verdana";
		font-size: 16px;
	}		
	h3 {
		font-family="Verdana";
		font-size: 14px;
	}		
	h4 {
		font-family="Verdana";
		font-size: 12px;
	}	
 	h5 {
		font-family="Verdana";
		font-size: 11px;
	}



	</style>

  	
  </head>
  <body>
  			
  
	<!-- 
	*******************************************************************************
	*
	* Report information "Header"
	*	
	*******************************************************************************
	-->
  
  <a name="top"></a>
  
  <h2>Report information</h2>  
  	
    <table border="0" cellspacing="1" cellpadding="3">
	    <tr><th>SDK Name </th><td> <xsl:value-of select="report/info/sdk/@name"/></td></tr>
	    <tr><th>Target </th><td> <xsl:value-of select="report/info/sdk/@target"/></td></tr>
	    <tr><th>Build </th><td> <xsl:value-of select="report/info/sdk/@build"/></td></tr>
	    <tr><th>Component </th><td> <xsl:value-of select="report/isUsedByData/component/@name"/></td></tr>
	</table>
         

  
  <hr></hr>
    
  <h2><xsl:value-of select="report/isUsedByData/component/@name"/> is used by</h2>
  
	<!-- 
	*******************************************************************************
	*
	* Report information "Header"
	*	
	*******************************************************************************
	-->  
  
	<xsl:variable name="sortCriteria">
		<xsl:value-of select="report/isUsedByData/component/@sortCriteria"/> 
	</xsl:variable> 
	
	
  <table border="1" cellspacing="0" cellpadding="0" width="100%">	
  	<tr>
  	
		<!--
			public static final int CRITERIA_NAME = 1;
			public static final int CRITERIA_BIN_FORMAT = 2;
			public static final int CRITERIA_UID1 = 3;
			public static final int CRITERIA_UID2 = 4;
			public static final int CRITERIA_UID3 = 5;
			public static final int CRITERIA_SECURE_ID = 6;
			public static final int CRITERIA_VENDOR_ID = 7;
			public static final int CRITERIA_MIN_HEAP = 8;
			public static final int CRITERIA_MAX_HEAP = 9;
			public static final int CRITERIA_STACK_SIZE = 10;
		-->
  	  	
		<xsl:choose>
			<xsl:when test="$sortCriteria='1'">	    	
				<th class="isUsedBySorted">Filename</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
				<th class="isUsedBy">Filename</th>
			</xsl:otherwise>
		</xsl:choose>    	

		<xsl:choose>
			<xsl:when test="$sortCriteria='2'">	    	
				<th class="isUsedBySorted">Binary format</th>
			</xsl:when>
			<xsl:otherwise>	    	
				<th class="isUsedBy">Binary format</th>	    											
			</xsl:otherwise>
		</xsl:choose>    	
		    	    	
		<xsl:choose>
			<xsl:when test="$sortCriteria='3'">	    	
				<th class="isUsedBySorted">UID1</th>
			</xsl:when>
			<xsl:otherwise>	  
				<th class="isUsedBy">UID1</th>  		    											
			</xsl:otherwise>
		</xsl:choose>    	
    	    	
		<xsl:choose>
			<xsl:when test="$sortCriteria='4'">	    	
				<th class="isUsedBySorted">UID2</th>
			</xsl:when>
			<xsl:otherwise>	    		    								
				<th class="isUsedBy">UID2</th>
			</xsl:otherwise>
		</xsl:choose>    	    	    	
    	
		<xsl:choose>
			<xsl:when test="$sortCriteria='5'">	    	
			<th class="isUsedBySorted">UID3</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">UID3</th>
			</xsl:otherwise>
		</xsl:choose>    	
    	    	
		<xsl:choose>
			<xsl:when test="$sortCriteria='6'">	    	
			<th class="isUsedBySorted">Secure ID</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">Secure ID</th>
			</xsl:otherwise>
		</xsl:choose>    	

		<xsl:choose>
			<xsl:when test="$sortCriteria='7'">	    	
			<th class="isUsedBySorted">Vendor ID</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">Vendor ID</th>
			</xsl:otherwise>
		</xsl:choose>    	

		<xsl:choose>
			<xsl:when test="$sortCriteria='8'">	    	
			<th class="isUsedBySorted">Min Heap Size</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">Min Heap Size</th>
			</xsl:otherwise>
		</xsl:choose>    	

		<xsl:choose>
			<xsl:when test="$sortCriteria='9'">	    	
			<th class="isUsedBySorted">Max Heap Size</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">Max Heap Size</th>
			</xsl:otherwise>
		</xsl:choose>    	

		<xsl:choose>
			<xsl:when test="$sortCriteria='10'">	    	
			<th class="isUsedBySorted">Stack Size</th>	
			</xsl:when>
			<xsl:otherwise>	    		    								
			<th class="isUsedBy">Stack Size</th>
			</xsl:otherwise>
		</xsl:choose>    	

    </tr>
    			
    <xsl:for-each select="report/isUsedByData/component/isUsedBy">
		<tr>
			<td><xsl:value-of select="./Filename"/></td>
		    <td><xsl:value-of select="./Binary_format"/></td>
		    <td><xsl:value-of select="./UID1"/></td>
		    <td><xsl:value-of select="./UID2"/></td>	    		
		    <td><xsl:value-of select="./UID3"/></td>
		    <td><xsl:value-of select="./Secure_ID"/></td>
		    <td><xsl:value-of select="./Vendor_ID"/></td>
		    <td><xsl:value-of select="./Min_Heap_Size"/></td>
		    <td><xsl:value-of select="./Max_Heap_Size"/></td>
		    <td><xsl:value-of select="./Stack_Size"/></td>
		</tr>
	</xsl:for-each>    

	    			    		
  </table>   

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>