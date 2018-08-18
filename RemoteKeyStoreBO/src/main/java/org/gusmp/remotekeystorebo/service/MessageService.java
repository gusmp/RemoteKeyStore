package org.gusmp.remotekeystorebo.service;


import java.util.Base64;

import org.gusmp.remotekeystorebo.bean.GetKeyStoreRequest;
import org.gusmp.remotekeystorebo.bean.request.SaveOrUpdateCertificateRequest;
import org.gusmp.remotekeystorebo.bean.response.DeleteCertificateResponse;
import org.gusmp.remotekeystorebo.bean.response.SaveOrUpdateCertificateResponse;
import org.gusmp.remotekeystorebo.bean.response.SetStatusCertificateResponse;
import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.entity.Log.SOURCE;
import org.gusmp.remotekeystorebo.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

@Service
public class MessageService {
	
	@Autowired
	private CipherService cipherService;
	
	@Autowired
	private CertificateService certificateService;
	
	@Autowired
	private ValidationService validationService;
	
	@Autowired
	private LogService logService;
	
	private Gson gson = new Gson();
	
	@Transactional(readOnly=false)
	public String processGetKeyStoreRequest(String request) throws Exception {
		
		String requestDecrypted = cipherService.decrypt(request);

		GetKeyStoreRequest getKeyStoreRequest = gson.fromJson(requestDecrypted, GetKeyStoreRequest.class);
		
		validationService.validateApplication(getKeyStoreRequest);
		
		Certificate certificate = certificateService.getCertificateByPin(getKeyStoreRequest.getLabel());

		Log log = new Log(getKeyStoreRequest);
		
		if (certificate != null) {
			log.setCertificate(certificate);
			log.setSource(SOURCE.APPLICATION);
			log.setMessage("OK");
			certificate.getLogList().add(log);
			certificateService.save(certificate);
		} else {
			log.setSource(SOURCE.UNKNOWN);
			log.setMessage("No certificate was found with label: " + getKeyStoreRequest.getLabel());
			logService.save(log);
		}
		
		return new String(Base64.getEncoder().encode(certificate.getPkcs12()));
	}
	
	public SaveOrUpdateCertificateResponse saveOrUpdateCertificate(SaveOrUpdateCertificateRequest addCertificateInfo) {
		
		SaveOrUpdateCertificateResponse saveOrUpdateCertificateResponse = new SaveOrUpdateCertificateResponse();
		saveOrUpdateCertificateResponse.setStatus("server");
		
		try {
			Certificate certificateEntity = certificateService.save(addCertificateInfo);
			
			saveOrUpdateCertificateResponse.setError(false);
			saveOrUpdateCertificateResponse.setAlias(certificateEntity.getAlias());
			saveOrUpdateCertificateResponse.setCertificateId(certificateEntity.getId());
			saveOrUpdateCertificateResponse.setEnabled(certificateEntity.getEnabled());
			saveOrUpdateCertificateResponse.setExpiredate(DateUtils.parseDate(certificateEntity.getExpireDate()));
			saveOrUpdateCertificateResponse.setIssuer(certificateEntity.getIssuer());
			saveOrUpdateCertificateResponse.setMsg("");
			saveOrUpdateCertificateResponse.setPin(certificateEntity.getPin());
			saveOrUpdateCertificateResponse.setSubject(certificateEntity.getSubject());
			
		} catch(Exception exc) {
			
			saveOrUpdateCertificateResponse.setError(true);
			saveOrUpdateCertificateResponse.setMsg(exc.toString());
		}
		
		return saveOrUpdateCertificateResponse; 
	}
	
	public DeleteCertificateResponse deleteCertificate(int certificateId) {
		
		certificateService.delete(certificateId);
		
		DeleteCertificateResponse deleteCertificateResponse = new DeleteCertificateResponse();
		deleteCertificateResponse.setStatus("server");
		return deleteCertificateResponse;
		
	}
	
	public SetStatusCertificateResponse setStatusCertificateResponse(int certificateId, boolean enabled) {
		
		Certificate certificate = certificateService.getCertificate(certificateId);
		if (certificate != null) {
			certificate.setEnabled(enabled);
			certificateService.save(certificate);
		}
		
		SetStatusCertificateResponse setStatusCertificateResponse = new SetStatusCertificateResponse();
		setStatusCertificateResponse.setStatus("server");
		return setStatusCertificateResponse;
	}

}
