package org.gusmp.remotekeystorebo.bean.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveOrUpdateCertificateRequest {
	
	private String certificateId;
	private String alias;
	private String pin;
	private MultipartFile upload;
	private Boolean enabled;

}
