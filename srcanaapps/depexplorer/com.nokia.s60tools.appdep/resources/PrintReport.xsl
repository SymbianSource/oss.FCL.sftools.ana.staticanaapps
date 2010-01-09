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
		SDK Name: <xsl:value-of select="report/info/sdk/@name"/>
		Target:  <xsl:value-of select="report/info/sdk/@target"/>
		Build: <xsl:value-of select="report/info/sdk/@build"/>
		Root component: <xsl:value-of select="report/components/rootComponent/@name"/>  
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
	    <tr><th>Root component </th><td> <xsl:value-of select="report/components/rootComponent/@name"/></td></tr>
	</table>
         
  <hr></hr>
  
	<!-- 
	*******************************************************************************
	*
	* Table of contents
	*	
	*******************************************************************************
	-->
    <h2>Table of contents</h2>
    <dl>
	<dd><b><a href="#components">Components</a></b></dd>
	<dl>
	    <xsl:for-each select="report/components/rootComponent">
	      
	    	<!-- 
	    		Calling templates for recursively tructure (nested looping)
	    		@see </xsl:template> <xsl:template match="component"> part at end of file
	    	 -->
	    	<xsl:apply-templates mode="TOC"/>         	    
	  
	  	</xsl:for-each>  		

	</dl>
	<dd><b><a href="#properties">Properties</a></b></dd>
	<dd><b><a href="#exportedFunctions">Exported functions</a></b></dd>
	
	</dl>

	
  
  <hr></hr>
  
	<!-- 
	*******************************************************************************
	*
	* Components
	*	
	*******************************************************************************
	-->
  	<a name="components"></a>
  	<h2>Components</h2>
    <xsl:for-each select="report/components/rootComponent">
         	
    	<!-- 
    		Calling templates for recursively tructure (nested looping)
    		@see </xsl:template> <xsl:template match="component"> part at end of file
    	 -->
    	
    	<xsl:apply-templates mode="COMPONENTS"/>   
  
  	</xsl:for-each>  
  
  	<hr></hr>
  
 
  
	<!-- 
	*******************************************************************************
	*
	* Part for properties
	*	
	*******************************************************************************
	-->
  
  <hr></hr>
  
  <a name="properties"></a>  
  <h2>Properties</h2>
  
  <xsl:for-each select="report/properties/component">
  
	<xsl:variable name="propertyName">
		<xsl:value-of select="@name"/>
	</xsl:variable> 
	
	  
  	<dl>

  		<dt>
  			<table border="0" cellspacing="0" cellpadding="0" width="100%">
  				<tr>
  					<td>
  						<!-- if component founds as parent in this report showing link to it -->
				    	<xsl:choose>
							<xsl:when test="@foundAsParent='true'">	    	
								<a href="#{$propertyName}"><xsl:value-of select="$propertyName"/> </a>
			          		</xsl:when>
			          		<!-- Otherwise showing only its name -->
			          		<xsl:otherwise>	    		    								
								<xsl:value-of select="$propertyName"/>
				          </xsl:otherwise>
			    	    </xsl:choose>  					
  					
  						
  						<a name="property_{$propertyName}"></a>  						
			    	    <!-- Links to exported functions -->
			    	    &#160;&#160;
			    	    <i><a href="#exportedFunction_{$propertyName}">&#60;Exported functions&#62;</a></i>
  						
  					</td>
  				<td class="right"><a href="#top">Top</a></td></tr>
  			</table>
  		</dt>
  		<dd>
    		<table border="1" cellspacing="0" cellpadding="0" width="400px">	
	    		<tr><th class="properties">Directory</th><td><xsl:value-of select="./directory"/></td></tr>
	    		<tr><th class="properties">Filename</th><td><xsl:value-of select="./filename"/></td></tr>
	    		<tr><th class="properties">BinaryFormat</th><td><xsl:value-of select="./binaryFormat"/></td></tr>
	    		<tr><th class="properties">UID1</th><td><xsl:value-of select="./UID1"/></td></tr>
	    		<tr><th class="properties">UID2</th><td><xsl:value-of select="./UID2"/></td></tr>	    		
	    		<tr><th class="properties">UID3</th><td><xsl:value-of select="./UID3"/></td></tr>
	    		<tr><th class="properties">Secure ID</th><td><xsl:value-of select="./secureID"/></td></tr>
	    		<tr><th class="properties">Vendor ID</th><td><xsl:value-of select="./vendorID"/></td></tr>
	    		<tr><th class="properties">Capabilities</th><td><xsl:value-of select="./capabilities"/></td></tr>
	    		<tr><th class="properties">Min Heap Size</th><td><xsl:value-of select="./minHeapSize"/></td></tr>
	    		<tr><th class="properties">Max Heap Size</th><td><xsl:value-of select="./maxHeapSize"/></td></tr>
	    		<tr><th class="properties">Stack Size</th><td><xsl:value-of select="./stackSize"/></td></tr>
	    		<tr><th class="properties">Dll Ref Table Count</th><td><xsl:value-of select="./dllRefTableCount"/></td></tr>	    			    		
	      	</table>   
		</dd>
  	</dl>		
    
  </xsl:for-each>     <!-- report/properties/component -->   

  
	<!-- 
	*******************************************************************************
	*
	* Part for exported functions
	*	
	*******************************************************************************
	-->
  
  <hr></hr>
  <hr></hr>
  
  <a name="exportedFunctions"></a>  
  <h2>Exported Functions</h2>
  
  <xsl:for-each select="report/exportedFunctions/component">
  
	<xsl:variable name="exporteFunctionName">
		<xsl:value-of select="@name"/>
	</xsl:variable> 
  
  	<dl>
  		<!-- Header and anchor to this property -->
  		<dt>
  			<table border="0" cellspacing="0" cellpadding="0" width="100%">
  				<tr>
  					<td>
  						<!-- if component founds as parent in this report showing link to it -->
				    	<xsl:choose>
							<xsl:when test="@foundAsParent='true'">	    	
								<a href="#{$exporteFunctionName}"><xsl:value-of select="$exporteFunctionName"/> </a>
			          		</xsl:when>
			          		<!-- Otherwise showing only its name -->
			          		<xsl:otherwise>	    		    								
								<xsl:value-of select="$exporteFunctionName"/>
				          </xsl:otherwise>
			    	    </xsl:choose>    					  						
  						
  						<a name="exportedFunction_{$exporteFunctionName}"></a>
			    	    <!-- Links to properties -->
			    	    &#160;&#160;
			    	    <i><a href="#property_{$exporteFunctionName}">&#60;Properties&#62;</a></i>
  						
  						</td>
  					<td class="right"><a href="#top">Top</a></td>
  				</tr>
  			</table>
  		</dt>
  		<dd>
    		<table border="1" cellspacing="0" cellpadding="0" width="400px">	
    			<tr><th>Ordinal</th><th>Name</th></tr>
    			<xsl:for-each select="function">
	    			<tr>
	    				<td width="45px"><xsl:value-of select="@ordinal"/></td>
	    				<td><xsl:value-of select="@name"/></td>
	    			</tr>
	    		</xsl:for-each>     <!-- function -->     
	      	</table>   
		</dd>
  	</dl>		
    
  </xsl:for-each>     <!-- report/properties/component -->     
  
    
  </body>
  </html>
  
  
  
</xsl:template>


	<!-- 
	*******************************************************************************
	*
	* Template to create components
	*	
	*******************************************************************************
	-->

      <xsl:template match="component" mode="COMPONENTS">
      
    	<dl>
    		
    		<!-- Creating component name -->    	
	    	<dt>
			<xsl:variable name="baseComponentName">
		  		<xsl:value-of select="@name"/>
			</xsl:variable>    	    	
	    	<!-- if type is base, an ancor will be created for reference types to point it -->
	    	<xsl:choose>
				<xsl:when test="@type='base'">	    	
	    			<a name="{$baseComponentName}"> 
	    			
	    			</a>
	    			<b><xsl:value-of select="@name"/></b> 
          		</xsl:when>
          		<xsl:otherwise>	    		    
					<!-- else if type is reference, an link will be created to point base -->    	  	
	    			<a href="#{$baseComponentName}">	    			
	    			<i> <b><xsl:value-of select="@name"/></b> </i>
	    			</a>
	          </xsl:otherwise>
    	    </xsl:choose>
    	    
    	    <!-- Unique anchor for TOC -->
			<xsl:variable name="fullName">
		  		<xsl:value-of select="@fullName"/>
			</xsl:variable>      	    
			<a name="{$fullName}"></a>
    	    
    	    <!-- Links to properties and exported functions -->
    	    &#160;&#160;
    	    <i><a href="#property_{$baseComponentName}">&#60;Properties&#62;</a></i>
    	    &#160;&#160;
    	    <i><a href="#exportedFunction_{$baseComponentName}">&#60;Exported functions&#62;</a></i>
			
    	    </dt>
    	  	<!-- Creating component name ends --> 
			    	  	

    	  	<dd>  	    	
    	  		
    			<table border="1" cellspacing="0" cellpadding="0">
    			<caption>Imported functions</caption>	
	    			<tr>
	    			<th>Ordinal</th><th>Name</th><th>Offset</th>
	    			</tr>
	    	  
		        	<xsl:for-each select="importedFunctions/function">
		    			<tr>
		    			
				    	<xsl:choose>
							<xsl:when test="@virtual='0'">
								<!-- if offset is not set function is not virtual -->
				        		<td><xsl:value-of select="@ordinal"/></td>
					      		<td><xsl:value-of select="@name"/></td>
					      		<td><xsl:value-of select="@offset"/>&#160; </td>			
			          		</xsl:when>
			          		<xsl:otherwise>	    		    
								<!-- else function is virtual -->    	  	
				        		<td><i><xsl:value-of select="@ordinal"/></i></td>
					      		<td><i><xsl:value-of select="@name"/></i></td>
					      		<td><i><xsl:value-of select="@offset"/>&#160; </i></td>
								
				          </xsl:otherwise>		    			
						</xsl:choose>		    			
						
			      		</tr>
		    		</xsl:for-each>      <!-- importedFunctions/function -->  
	      		</table>    
	      		<br></br>	    
			</dd>
			

			<!-- Link to top align to right -->
			<p class="right"><a href="#top">Top</a></p>
			
			<!-- 
				Recursivily call same function until "<component>" -element found
				Every <component> under <component> will be tabulated
			-->	    	
			
	    	<dl>
	    		<xsl:apply-templates mode="COMPONENTS"/>
	    	</dl>
	    	
	    		    	        
    	</dl>    	
    	  		
  		
  	</xsl:template>

	<!-- 
	*******************************************************************************
	*
	* Template to create Table Of Contens for components, looping like 	
	* components part, but not printing imported functions
	*	
	*******************************************************************************
	-->  	
  	
      <xsl:template match="component" mode="TOC">
      
    	<dl>
    			
	    	<dt>
			<xsl:variable name="baseComponentName">
		  		<xsl:value-of select="@name"/>
			</xsl:variable>    	 
    	    <!-- Unique anchor for TOC -->
			<xsl:variable name="fullName">
		  		<xsl:value-of select="@fullName"/>
			</xsl:variable>      	    
						   	
	    	<!-- if type is base, an link will be regular font -->  
	    	<xsl:choose>
				<xsl:when test="@type='base'">	    	
	    			<a href="#{$fullName}">	    			
	    			 <xsl:value-of select="@name"/>
	    			</a>
	    			
          		</xsl:when>
          		<xsl:otherwise>	    		    
					<!-- else if type is reference, an link will be italic -->    	  	
	    			<a href="#{$fullName}">	    			
	    			<i> <xsl:value-of select="@name"/> </i>
	    			</a>
	          </xsl:otherwise>
    	    </xsl:choose>
    	    
    	    </dt>

			<!-- 
				Recursivily call same function until "<component>" -element found
				Every <component> under <component> will be tabulated
			-->	    	
	    	<dl>
	    		<xsl:apply-templates mode="TOC"/>
	    	</dl>
	    	
	    		    	        
    	</dl>    	
  		
  		
  	</xsl:template>
  	
  	
</xsl:stylesheet>