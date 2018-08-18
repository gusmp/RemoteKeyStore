package org.gusmp.remotekeystorebo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.gusmp.remotekeystorebo.bean.request.SaveOrUpdateCertificateRequest;
import org.gusmp.remotekeystorebo.bean.response.DeleteCertificateResponse;
import org.gusmp.remotekeystorebo.bean.response.GetCertificatesResponse;
import org.gusmp.remotekeystorebo.bean.response.SaveOrUpdateCertificateResponse;
import org.gusmp.remotekeystorebo.bean.response.SetStatusCertificateResponse;
import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.service.CertificateService;
import org.gusmp.remotekeystorebo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class KeyStoreController {

	private final int PAGE_SIZE = 20;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private CertificateService certificateService;
	
	public enum THEME {
			AIR("skins/air.css"),
			AIRCOMPACT("skins/aircompact.css"), 
			CLOUDS("skins/clouds.css"), 
			COMPACT("skins/compact.css"),
			CONTRAST("skins/contrast.css"),
			FLAT("skins/flat.css"),  
			GLAMOUR("skins/glamour.css"),
			LIGHT("skins/light.css"), 
			METRO("skins/metro.css"),  
			TERRACE("skins/terrace.css"), 
			TOUCH("skins/touch.css"),  
			WEB("skins/web.css"), 
			WEBIX("webix.css");
		
		THEME(String css) { cssFile = css; }
		
		private String cssFile;
		
		public String getCssFile() { return cssFile; }
		
	}
	
	@RequestMapping(value = {"/", "/{theme}"})
	public String mainView(@PathVariable Optional<THEME> theme, Model model) {
		

		if (theme.isPresent() == false) {
			model.addAttribute("themeUrl", "resources/js/webix/" + THEME.AIR.getCssFile());
			model.addAttribute("currentTheme",THEME.AIR.name());
		}
		else {
			switch(theme.get()) {
				case AIR:
				case AIRCOMPACT:
				case CLOUDS:
				case COMPACT:
				case CONTRAST:
				case FLAT:
				case GLAMOUR:
				case LIGHT:
				case METRO:
				case TERRACE:
				case TOUCH:
				case WEB:
					model.addAttribute("themeUrl", "resources/js/webix/" + theme.get().getCssFile());
					model.addAttribute("currentTheme",theme.get().name());
					break;
				default:
					model.addAttribute("themeUrl", "resources/js/webix/" + THEME.WEBIX.getCssFile() );
					model.addAttribute("currentTheme",THEME.WEBIX.name());
			}
		}

		
		return "mainView";
	}
	
	@RequestMapping("/get")
	@ResponseBody
	public String getKeyStore(@RequestBody String request) {
		
		try {
			return messageService.processGetKeyStoreRequest(request);	
		} catch(Exception exc) {
			return null;
		}
	}

	
	@RequestMapping("/saveOrUpdateCertificate")
	@ResponseBody
	public SaveOrUpdateCertificateResponse saveOrUpdateCertificate(SaveOrUpdateCertificateRequest saveOrUpdateCertificateRequest) {
		
		return messageService.saveOrUpdateCertificate(saveOrUpdateCertificateRequest);
	}
	
	@RequestMapping("/deleteCertificate/{certificateId}")
	@ResponseBody
	public DeleteCertificateResponse deleteCertificate(@PathVariable int certificateId) {
		
		return messageService.deleteCertificate(certificateId);
	}
	
	@RequestMapping("/statusCertificate/{certificateId}/{newCertificateStatus}")
	@ResponseBody
	public SetStatusCertificateResponse statusCertificate(@PathVariable int certificateId, 
			@PathVariable boolean newCertificateStatus) {
		
		return messageService.setStatusCertificateResponse(certificateId, newCertificateStatus);
	}
	
	@RequestMapping("/getCertificate/{certificateId}")
	public void getCertificate(@PathVariable int certificateId, HttpServletResponse response) throws Exception {
		
		response.setHeader("Content-Disposition", "attachment; filename=certificate_" +  certificateId + ".cer");
		response.setHeader("Pragma", "no-cache");
	    response.setHeader("Cache-Control", "no-cache");
		
	    Certificate certificate = certificateService.getCertificate(certificateId);
	    if (certificate != null) {
	    	response.getOutputStream().write(certificateService.getCertificate(certificateId).getCertificate());
	    } else {
	    	response.getOutputStream().write(null);
	    }
	}
	
	
	@RequestMapping("/getFullCertificate/{certificateId}")
	public void getFullCertificate(@PathVariable int certificateId, HttpServletResponse response) throws Exception {
		
		response.setHeader("Content-Disposition", "attachment; filename=certificate_" +  certificateId + ".p12");
		response.setHeader("Pragma", "no-cache");
	    response.setHeader("Cache-Control", "no-cache");
		
	    Certificate certificate = certificateService.getCertificate(certificateId);
	    if (certificate != null) {
	    	response.getOutputStream().write(certificateService.getCertificate(certificateId).getPkcs12());
	    } else {
	    	response.getOutputStream().write(null);
	    }
	}
	
	@RequestMapping("/getCertificates")
	@ResponseBody
	public GetCertificatesResponse getCertificates(@RequestParam(required=false,name="start") Integer pageStart,
			@RequestParam(required=false) Integer count,
			@RequestParam(required=false) Boolean continueParam) {
		
		GetCertificatesResponse getCertificatesResponse = new GetCertificatesResponse();
		List<SaveOrUpdateCertificateResponse> list = new ArrayList<SaveOrUpdateCertificateResponse>();
		
		if (pageStart == null) {
			
			List<Certificate> certificateList = certificateService.getCertificates(PageRequest.of(0, PAGE_SIZE));

			for(Certificate c : certificateList) {
				list.add(new SaveOrUpdateCertificateResponse(c));
			}
			
			getCertificatesResponse.setPos(0);
			getCertificatesResponse.setTotal_count(certificateService.getCertificateCount());
		}
		else {
			
			List<Certificate> certificateList = certificateService.getCertificates(PageRequest.of(pageStart / PAGE_SIZE, PAGE_SIZE));

			for(Certificate c : certificateList) {
				list.add(new SaveOrUpdateCertificateResponse(c));
			}
			
			getCertificatesResponse.setPos(pageStart);
			getCertificatesResponse.setTotal_count(null);
		}
		
		
		getCertificatesResponse.setData(list);
		
		return getCertificatesResponse;
	}
	
}
