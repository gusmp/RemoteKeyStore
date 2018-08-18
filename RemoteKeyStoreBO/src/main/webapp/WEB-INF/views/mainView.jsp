<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags"  prefix="spring" %>
<!DOCTYPE html>
<html>
    <head>
        
		<c:url var="root"  value="/" />
		
		<link rel="stylesheet" href="${root}${themeUrl}"  type="text/css" charset="utf-8">
		
		<script src="<c:url value="/resources/js/webix/webix.js"/>" type="text/javascript" charset="utf-8"></script>
		
		<style>
			.webix_table_checkbox{
			    width:22px;
			    height:22px;
			    margin-top:5px;
			 },
			 
			 .grey_color {
			 	color: #666;
			 }
			
		</style>
		
		
    </head>
    <body>
        <script type="text/javascript" charset="utf-8">
            webix.ready(function() {
			
				function sendAddCertificateHandler(certificateRowId, certificateId) {
					
					$$("certificateFileUploader").data.formData.alias = $$("alias").getValue();
					$$("certificateFileUploader").data.formData.pin = $$("pin").getValue()
					$$("certificateFileUploader").data.formData.certificateId = certificateId;
					$$("certificateFileUploader").data.formData.enabled = true;
					
					if ($$("certificateFileUploader").files.count() == 0) {
						
						webix.message('<spring:message code="mainView.error.noUploadFile" javaScriptEscape="true"/>',"debug");
						return;
					}
					
					$$("certificateFileUploader").send(function(response){
						
						if (response == undefined) {
							webix.message('<spring:message code="mainView.error.repeatUploadFile" javaScriptEscape="true"/>',"error");
							return;
						}
						
						
						if (response.error == false) {
							$$('addOrEditCertificateWindow').close();
							
							$$("certificateTable").hideOverlay();
							$$('certificateTable').add(response);
							
							webix.message("OK");
						}
						else {
							webix.message(response.msg,"error");
						}
					});
				}
				
				function sendEditCertificateHandler(certificateRowId, certificateId) {
					
					$$("certificateFileUploader").data.formData.alias = $$("alias").getValue();
					$$("certificateFileUploader").data.formData.pin = $$("pin").getValue();
					$$("certificateFileUploader").data.formData.certificateId = certificateId;
					
					if ($$("certificateFileUploader").files.count() == 0) {
						
						webix.ajax().post($$("certificateFileUploader").data.upload, 
								$$("certificateFileUploader").data.formData,
								function(text, data, XmlHttpRequest) {
									
									if (data.json().status == 'server') {
										$$('certificateTable').updateItem(certificateRowId ,data.json());
										$$('addOrEditCertificateWindow').close();
										webix.message("OK");
									} else {
										webix.message(data.json().msg,"error");
									}
								}
						);
					} else {
						$$("certificateFileUploader").send(function(response){
							if (response.status == 'server') {
								$$('certificateTable').updateItem(certificateRowId ,response);
								$$('addOrEditCertificateWindow').close();
								webix.message("OK");
							}
							else {
								webix.message(response.msg,"error");
							}
						});
					}
				}
				
				function addOrEditCertificateHandler(targetUrl, sendButonCallback, certificateRowId) {
					
					certificateInfo = {};
					certificateId = null;
					isEdit = false;
					if (certificateRowId != null) { 
						certificateInfo = $$("certificateTable").data.getItem(certificateRowId);
						certificateId = certificateInfo.certificateId; 
						isEdit = true;
					}
					
					webix.ui({
						view:"popup",
						id:"addOrEditCertificateWindow",
						modal:true,
						position:"center",
						body: {
							rows: [
								{
									view: "form",
									id: "addOrEditCertificateForm",
									elements: [
										{
											cols: [
											{
												view:"fieldset", label: '<spring:message code="mainView.field.keyStoreDetails"/>', 
												body: {
													rows: [
														{ view:"text",  labelWidth: 75, width:250, readonly: isEdit, align:"center", id: "alias", label:'<spring:message code="mainView.field.alias"/>', value: certificateInfo.alias }, 
														{ view:"text",  labelWidth: 75, width:250, readonly: isEdit, align:"center", id: "pin", label:'<spring:message code="mainView.field.pin"/>', value: certificateInfo.pin },
														{ view:"label", labelWidth: 75, width:250, label: ""},
													]
												}
											},
											{
												view:"fieldset", label: '<spring:message code="mainView.field.certificateDetails"/>', 
												body: {
													rows: [
														{ view:"textarea", labelWidth: 115, width:525, readonly:true, align:"center", label: '<spring:message code="mainView.field.subject"/>', value: certificateInfo.subject },
														{ view:"textarea", labelWidth: 175, width:525, readonly:true, align:"center", label: '<spring:message code="mainView.field.issuer"/>', value: certificateInfo.issuer },
														{ view:"text",     labelWidth: 115, width:525, readonly:true, align:"center", label: '<spring:message code="mainView.field.expireDate"/>', value: certificateInfo.expiredate }
													]
												}
											}]
										},
										{ view:"list", id:"certificateFile",  type:"uploader", autoheight:true,  borderless:true },
										{ 
									   		cols: [
												{ 
													view:"uploader", 
													id:"certificateFileUploader", 
													value: '<spring:message code="mainView.button.selectKeyStore"/>', 
													link:"certificateFile", 
													multiple: false, 
													autosend: false,
													disabled: isEdit,
													formData:{
												        alias: certificateInfo.alias,
												        pin: certificateInfo.pin
													}, 
													accept:"application/x-pkcs12",
													upload: targetUrl
												},
									        ]
									    }, 
										{ cols: [
											{ view:"button", label:'<spring:message code="mainView.button.save"/>', type:"form", disabled: isEdit, click: function() { sendButonCallback(certificateRowId, certificateId); } },
											{ view:"button", label:'<spring:message code="mainView.button.cancel"/>', click:"$$('addOrEditCertificateWindow').close();" }
										]}
									]
									
								}
							]
						}
					}).show();
					
					$$("addOrEditCertificateForm").setValues(certificateInfo);
				}
				
				function custom_checkbox(obj, common, value){

					if (value){
                            return "<input class='webix_table_checkbox' type='checkbox' checked='true' />";
                    }
                    else{
                    		return "<input class='webix_table_checkbox' type='checkbox' />";
                    }
                };
                
                function showLogs(type, certificateId) {
                	
                	url = "";
                	if (certificateId == null) {
                		url = '<c:url value="/getLogs"/>';
                	} else {
                		url = '<c:url value="/getLogs"/>/'+certificateId;
                	}
                	
                	webix.ui({
						view:"popup",
						id:"logWindow",
						modal:true,
						position:"center",
						height: screen.height/2,
						width: screen.width,
						body: {
							rows: [
								{ 
									view:"datatable", 
									id: "logTable",
									url: url,
									pager: "logPager",
									resizeColumn:true,
									scrollX:true,
									columns: [
										{ id:"host",     width: 100, header: {text:'<spring:message code="mainView.logView.host"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"ip",       width: 120, header: {text:'<spring:message code="mainView.logView.ip"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"userName", width: 100, header: {text:'<spring:message code="mainView.logView.userName"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"password", width: 100, header: {text:'<spring:message code="mainView.logView.password"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"label",    width: 100, header: {text:'<spring:message code="mainView.logView.label"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"timestamp",width: 150, header: {text:'<spring:message code="mainView.logView.timestamp"/>', css:{"text-align":"center"} },  css:{'text-align':'center'} },
										{ id:"message",  fillspace:true, header: {text:'<spring:message code="mainView.logView.message"/>', css:{"text-align":"center"} },  css:{'text-align':'center'}},
									],
									on: { 
										onBeforeLoad:function(){
					                        this.showOverlay('<spring:message code="mainView.logView.table.overlay.loading"/>');
					                    },
					                    onAfterLoad:function(){
					                    	 if (this.count() == 0) {
												this.showOverlay('<spring:message code="mainView.logView.table.overlay.noData"/>');
					                    	 } else {
					                        	this.hideOverlay();
					                    	 }
					                    }
									}
								},
								{
									view:"pager",
									template:" {common.prev()} {common.pages()} {common.next()}",
									id: "logPager",
									size:20,
									group:3
								},
								{ view:"button", label:'<spring:message code="mainView.button.cancel"/>', click:"$$('logWindow').close();" }
							]
						}
					}).show();
                }
			
				/* 
				   Latest Webix v5.3 uses Font Awesome v4.7.0
				   https://fontawesome.com/icons?d=gallery 
				*/
				webix.ui({
					rows: [
						{
							view: "template", template: '<spring:message code="mainView.title"/>', type:"header"
						},
						{ 
							view:"toolbar", id:"toolbar", height: 70, elements:[
								{ 
									view:"button", type:"iconButtonTop", icon:"plus-square", 
									click: function() { addOrEditCertificateHandler('<c:url value="/saveOrUpdateCertificate"/>', sendAddCertificateHandler, null); }, 
									id: "addCertificateBt", label:'<spring:message code="mainView.toolbar.addCertificate"/>', width: 110
								},
								{
									view:"button", type:"iconButtonTop", icon:"search-plus", 
									click: function() { showLogs('UNKNOWN', null); }, 
									id: "showUnknownLogBt", label:'<spring:message code="mainView.toolbar.showUnknownLog"/>', width: 110
								},
								{},
								{
									view: "combo", id: "themeCombo", width: 300, label: '<spring:message code="mainView.toolbar.theme"/>',
									value: '${currentTheme}', labelWidth: 50,
									options: [ "AIR", "AIRCOMPACT", "CLOUDS", "COMPACT", "CONTRAST", "FLAT", "GLAMOUR", "LIGHT", "METRO", "TERRACE", "TOUCH", "WEB", "WEBIX" ],
									on: {
										onChange: function(newTheme, oldTheme){
											webix.send( '<c:url value="/"/>'+newTheme);
										}
									}
								},
								{},
								{
									view:"button", type:"iconButtonTop", icon:"minus-circle", 
									click: function() { window.location.href =this.config.href; }, 
									href: '<c:url value="/logout"/>',
									id: "logoutBt", label:'<spring:message code="mainView.toolbar.logout"/>', width: 110
								}
							]
						}, {
							cols: [
								{ 
									view:"datatable", 
									id: "certificateTable",
									url: '<c:url value="/getCertificates"/>',
									pager: "pager",
									resizeColumn:true,
									scrollX:false,
									columns: [
										{ id:"certificateId", hidden:true },
										{ id:"pin",           hidden:true },
										{ id:"subject",        fillspace:5, header: {text:'<spring:message code="mainView.table.header.subject"/>', css:{"text-align":"center"} },                  css:{'text-align':'center'} },
										{ id:"alias",          fillspace:2, header: {text:'<spring:message code="mainView.table.header.alias"/>', css:{"text-align":"center"} },                     css:{'text-align':'center'} },
										{ id:"expiredate",     fillspace:3, header: {text:'<spring:message code="mainView.table.header.expireDate"/>', css:{"text-align":"center"} },            css:{'text-align':'center'} },
										{ id:"issuer",         fillspace:3, header: {text:'<spring:message code="mainView.table.header.issuer"/>', css:{"text-align":"center"} }, css:{'text-align':'center'} },
										{ id:"enabled",        fillspace:1, header: {text:'<spring:message code="mainView.table.header.enabled"/>', css:{"text-align":"center"} },                     css:{'text-align':'center'}, template:custom_checkbox },
										{ id:"edit",           fillspace:1, header:"", template:"{common.editIcon()}",                                css:{'text-align':'center'} },
								        { id:"delete",         fillspace:1, header:"", template:"{common.trashIcon()}",                                       css:{'text-align':'center'} },
								        { id:"downloadCert",   fillspace:1, header:"", 
								        		template: function(certificateInfo) {
								        			url = '<c:url value="/getCertificate"/>/' + certificateInfo.certificateId;
								        			return "<a href='" + url + "'><span class='webix_icon fa-arrow-circle-down' style='color: #666'></span></a>";     
								        		},
								        		css:{'text-align':'center'} },
								        
								        { id:"downloadPkcs12", fillspace:1, header:"",
								        		template: function(certificateInfo) {
									        		url = '<c:url value="/getFullCertificate"/>/' + certificateInfo.certificateId;
									        		return "<a href='" + url + "'><span class='webix_icon fa-key' style='color: #666'></span></a>";     
									        	},
								        		css:{'text-align':'center'} },
								        
								        { id:"showLogs", fillspace:1, header:"",
									        	template: function(certificateInfo) {
										        	return "<span class='webix_icon fa-search-plus' style='color: #666'></span>";     
										        },
									        	css:{'text-align':'center'} },
								        { header:"", fillspace:true }
									],
									on: { 
										onCheck:function(id, column, state) {
					                    	certificateInfo = $$("certificateTable").data.getItem(id);
											
											webix.ajax().post('<c:url value="/statusCertificate"/>/'+certificateInfo.certificateId + "/" + certificateInfo.enabled, 
													function(text, data, XmlHttpRequest){
													    if (data.json().status == "server") {
													    	webix.message("OK");
													    	
													    } else {
													    	webix.message("ERROR","error");
													    }
													}
											);
					                  	},
					                    onBeforeLoad:function(){
					                        this.showOverlay('<spring:message code="mainView.table.overlay.loading"/>');
					                    },
					                    onAfterLoad:function(){
					                    	 if (this.count() == 0) {
												this.showOverlay('<spring:message code="mainView.table.overlay.noData"/>');
					                    	 } else {
					                        	this.hideOverlay();
					                    	 }
					                    }
									},
									onClick: {
										"fa-pencil": function(e, id, trg) {
											addOrEditCertificateHandler('<c:url value="/saveOrUpdateCertificate"/>', sendEditCertificateHandler, id);
								        },
								        "fa-trash": function(e, id, trg) {
								        	
								        	webix.confirm({
								        		
								        	    title:'<spring:message code="mainView.delete.title"/>',
								        	    ok:"Yes", 
								        	    cancel:"No",
								        	    text: '<spring:message code="mainView.delete.text"/>',
								        	    callback:function(result) {

								        	    	if (result == true) {
								        	    		
								        	    		certificateInfo = $$("certificateTable").data.getItem(id);
														
														webix.ajax().post('<c:url value="/deleteCertificate"/>/'+certificateInfo.certificateId, function(text, data, XmlHttpRequest){
														    if (data.json().status == "server") {
														    	certificateInfo = $$("certificateTable").data.remove(id);
														    	
														    	if ($$("certificateTable").data.count() == 0) {
															    	$$("certificateTable").showOverlay('<spring:message code="mainView.table.overlay.noData"/>');
														    	}
														    	webix.message("OK");
														    	
														    } else {
														    	webix.message("ERROR","error");
														    }
														});
								        	    	}
								        	   }
								        	});
								        },
								        "fa-search-plus" : function(e, id, trg) {
								        	
								        	certificateInfo = $$("certificateTable").data.getItem(id);
								        	showLogs('APPLICATION', certificateInfo.certificateId); 
								        }
								    },
								    onDblClick:{
								    	"webix_cell": function(e, id, trg) {
								    		addOrEditCertificateHandler('<c:url value="/saveOrUpdateCertificate"/>', sendEditCertificateHandler, id);
								    	}
								    }
								}
							]
						},
						{
							view:"pager",
							template:" {common.prev()} {common.pages()} {common.next()}",
							id: "pager",
							size:20,
							group:3
						}
					]
				});
			});
		</script>
	</body>
</html>
