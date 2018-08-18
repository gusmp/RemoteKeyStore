package org.gusmp.remotekeystorebo.bean.response;

import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogResponse  {
	
	private String host;
	private String ip;
	private String userName;
	private String password;
	private String label;
	private String timestamp;
	private String message;
	
	public LogResponse(Log log) {
		
		this.host = log.getHost();
		this.ip = log.getIp();
		this.userName = log.getUserName();
		this.password = log.getPassword();
		this.label = log.getLabel();
		this.timestamp = DateUtils.parseDate(log.getTimeStampAction());
		this.message = log.getMessage();
	}

}
