package org.gusmp.remotekeystorebo.repository;

import java.util.List;

import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.entity.Log.SOURCE;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends Repository<Log, Integer> {
	
	public Log save(Log log);
	public List<Log> findBySourceOrderByTimeStampActionDesc(SOURCE source, Pageable page);
	
	@Query("SELECT COUNT(l.id) FROM Log l WHERE l.source=:source")
	public Integer getLogCount(@Param(value="source") SOURCE source);
	

	public List<Log> findBySourceAndCertificateOrderByTimeStampActionDesc(SOURCE source, Certificate certificate, Pageable page);
	
	@Query("SELECT COUNT(l.id) FROM Log l WHERE l.source=:source AND l.certificate=:certificate")
	public Integer getLogCount(@Param(value="source") SOURCE source, @Param(value="certificate") Certificate certificate); 

	
}
