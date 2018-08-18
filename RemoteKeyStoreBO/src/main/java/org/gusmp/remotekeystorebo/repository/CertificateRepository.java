package org.gusmp.remotekeystorebo.repository;

import java.util.List;

import org.gusmp.remotekeystorebo.entity.Certificate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CertificateRepository extends Repository<Certificate, Integer> {
	
	public Certificate save(Certificate certificate);
	public Certificate findById(Integer certificateId);
	
	public void delete(Certificate certificate);
	public Certificate findByIssuerAndSerialNumber(String issuer, String serialNumber);
	public List<Certificate> findByPinAndEnabledOrderByExpireDateDesc(String pin,Boolean enabled);

	public List<Certificate> findAllByOrderByExpireDateDesc(Pageable page);
	
	@Query("SELECT COUNT(c.id) FROM Certificate c")
	public Integer getCertificateCount(); 
	
}
