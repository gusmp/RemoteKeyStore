package org.gusmp.remotekeystorebo.service;

import java.util.Map;

import org.gusmp.remotekeystorebo.bean.GetKeyStoreRequest;
import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.entity.Log.SOURCE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
	
	@Value( "#{${appUsersMap}}" ) 
	private Map<String,String> appUserDetailsMap;
	
	@Autowired
	private LogService logService;
	
	public void validateApplication(GetKeyStoreRequest getKeyStoreRequest) throws SecurityException {
		
		
		String passwordMap = appUserDetailsMap.get(getKeyStoreRequest.getUser());
		if (passwordMap == null) { 
			
			Log log = new Log(getKeyStoreRequest);
			log.setSource(SOURCE.UNKNOWN);
			log.setMessage("User " + getKeyStoreRequest.getUser() + " was not found");
			logService.save(log);
			
			throw new SecurityException("User " + getKeyStoreRequest.getUser() + " was not found");
		}
		
		if (getKeyStoreRequest.getPassword().equals(passwordMap) == false) {
			
			Log log = new Log(getKeyStoreRequest);
			log.setSource(SOURCE.UNKNOWN);
			log.setMessage("User " + getKeyStoreRequest.getUser() + " was provided with an wrong password." +
					"Request: " + getKeyStoreRequest.getPassword() + " " +
					"Real: " + passwordMap);
			logService.save(log);
			
			throw new SecurityException("User " + getKeyStoreRequest.getUser() + " was provided with an wrong password");
		}
	}

}
