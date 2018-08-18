<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags"  prefix="spring" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="<c:url value="/resources/js/webix/webix.css"/>" type="text/css" charset="utf-8">
        <!-- <link rel="stylesheet" href="<c:url value="/resources/js/webix/skins/contrast.css"/>"  type="text/css" charset="utf-8"> -->
		<script src="<c:url value="/resources/js/webix/webix.js"/>" type="text/javascript" charset="utf-8"></script>
		
		<style>
			.error { font-size: 13px; color: #fd595f }
		</style>
		
    </head>
    <body>
    	<p style="text-align:center; font-size:25px; font-family:'verdana'; ">
    		<strong>
    		<em>
    			<spring:message code="mainView.title"/>
    		</em>
    		</strong>
    	</p>
    	<div id="data_container" style="margin:auto"></div>
        
        <script type="text/javascript" charset="utf-8">
        webix.ready(function() {
        	
	        webix.ui(
	        		{
				    	view:"form", 
				        id:"loginForm",
				        container:"data_container",
				        css: { margin:"auto" },
				        padding:120,
				        scroll:false,
				        borderless:false,
				        width: 550,
				        elements:
				        [
				        	{ view: "label", id: "warningCredentials", hidden:true, label: '<spring:message code="loginView.error.wrongCredentials" javaScriptEscape="true"/>', css:"error" },
				        	{ view:"text", labelWidth: 130, label:'<spring:message code="loginView.label.userName"/>', name:"username", invalidMessage:'<spring:message code="loginView.error.loginMissing" javaScriptEscape="true"/>' },
				            { view:"text", labelWidth: 130, type:"password", label:'<spring:message code="loginView.label.password"/>', name:"password", invalidMessage:'<spring:message code="loginView.error.passwordMissing"/>'  },
				            { margin:5, cols:
				            	[
					            	{ 
					                	view:"button", value:'<spring:message code="loginView.button.login"/>', type:"form", 
					                   		click: function() 
					                   			{
					                   				$$("warningCredentials").hide();
					                   				if ($$('loginForm').validate()) {
					                   					webix.send( '<c:url value="/login"/>', $$('loginForm').getValues());
					                   				}
					                   			}
					                },
					                { 
					                	view:"button", value:'<spring:message code="loginView.button.cancel"/>',
					                	click: function() { $$('loginForm').setValues({}); }
					                }
				            	]
				           }
				        ],
				        rules:
				        {
				        	username: webix.rules.isNotEmpty,
				        	password: webix.rules.isNotEmpty
				        }
				   }
	        )
	        
	        if (window.location.href.search("error") != -1) {
        		$$("warningCredentials").show();
        	}
	        
	    });
		</script>
	</body>
</html>
