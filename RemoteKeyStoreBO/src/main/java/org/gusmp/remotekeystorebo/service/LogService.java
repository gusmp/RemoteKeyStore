package org.gusmp.remotekeystorebo.service;

import java.util.List;

import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.entity.Log.SOURCE;
import org.gusmp.remotekeystorebo.repository.CertificateRepository;
import org.gusmp.remotekeystorebo.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {
	
	@Autowired
	private CertificateRepository certificateRepository;
	
	@Autowired
	private LogRepository logRepository;
	
	@Transactional(readOnly=false)
	public Log save(Log log) {
		return logRepository.save(log);
	}

	@Transactional(readOnly=true)
	public List<Log> getLogs(SOURCE source, Pageable page) {
		return logRepository.findBySourceOrderByTimeStampActionDesc(source, page);
	}
	
	@Transactional(readOnly=true)
	public List<Log> getLogs(SOURCE source, int certificateId, Pageable page) {
		
		Certificate c = certificateRepository.findById(certificateId);
		return logRepository.findBySourceAndCertificateOrderByTimeStampActionDesc(source, 
				c, 
				page);
	}
	
	@Transactional(readOnly=true)
	public Integer getLogCount(SOURCE source) {
		return logRepository.getLogCount(source);
	}
	
	@Transactional(readOnly=true)
	public Integer getLogCount(SOURCE source, int certificateId) {
		return logRepository.getLogCount(source,
				certificateRepository.findById(certificateId));
	}

}
