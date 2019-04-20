package org.gusmp.remotekeystorebo.bean.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GetKeyStoreRequest {
	
	private String user;
	private String password;
	private String label;
	private String host;
	private String ip;

}
