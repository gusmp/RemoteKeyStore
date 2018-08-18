package org.gusmp.remotekeystorebo.bean.response;

import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveOrUpdateCertificateResponse extends BaseResponse {
	
	
	private String alias;
	private int certificateId;
	private Boolean enabled;
	private String expiredate;
	private String issuer;
	private String pin;
	private String subject;
	
	private Boolean error;
	private String msg = "";
	
	public SaveOrUpdateCertificateResponse() {}
	
	public SaveOrUpdateCertificateResponse(Certificate certificate) {
		
		this.alias = certificate.getAlias();
		this.certificateId = certificate.getId();
		this.enabled = certificate.getEnabled();
		this.expiredate = DateUtils.parseDate(certificate.getExpireDate());
		this.issuer = certificate.getIssuer();
		this.pin = certificate.getPin();
		this.subject = certificate.getSubject();
	}

}
