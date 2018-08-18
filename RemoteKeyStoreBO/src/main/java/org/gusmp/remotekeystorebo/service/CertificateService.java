package org.gusmp.remotekeystorebo.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.gusmp.remotekeystorebo.bean.request.SaveOrUpdateCertificateRequest;
import org.gusmp.remotekeystorebo.entity.Certificate;
import org.gusmp.remotekeystorebo.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateService {
	
	@Autowired
	private CertificateRepository certificateRepository;
	
	private Certificate getCertificate(String pin, String alias, InputStream binaryCertificate) throws Exception {
		
		
		byte certificteByte[] = IOUtils.toByteArray(binaryCertificate);
		
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(new ByteArrayInputStream(certificteByte), pin.toCharArray());
		
		X509Certificate x509certificate = (X509Certificate) store.getCertificate(alias);
		
		Certificate certificateEntity = new Certificate();
		
		certificateEntity.setAlias(alias);
		certificateEntity.setCertificate(x509certificate.getEncoded());
		certificateEntity.setExpireDate(x509certificate.getNotAfter());
		certificateEntity.setIssuer(x509certificate.getIssuerDN().getName());
		certificateEntity.setPin(pin);
		certificateEntity.setPkcs12(certificteByte);
		certificateEntity.setSerialNumber(x509certificate.getSerialNumber().toString());
		certificateEntity.setSubject(x509certificate.getSubjectDN().getName());
		
		return certificateEntity;
	}
	

	@Transactional(readOnly=false)
	public Certificate save(SaveOrUpdateCertificateRequest addCertificateInfo) throws Exception {
		
		Certificate certificateEntity = getCertificate(addCertificateInfo.getPin(), 
				addCertificateInfo.getAlias(), 
				addCertificateInfo.getUpload().getInputStream());
		
		
		Certificate certificateInDataBase = certificateRepository.findByIssuerAndSerialNumber(certificateEntity.getIssuer(),
				certificateEntity.getSerialNumber());
		
		if (certificateInDataBase == null) { 
			certificateEntity.setEnabled(addCertificateInfo.getEnabled());
			return certificateRepository.save(certificateEntity);
		} else {
			throw new Exception("The certificat is already present");
		}
	}

	
	@Transactional(readOnly=false)
	public Certificate save(Certificate certificate) {
		
		return certificateRepository.save(certificate);
	}
	
	@Transactional(readOnly=true)
	public Certificate getCertificate(Integer certificateId) {
		
		Certificate certificate = certificateRepository.findById(certificateId);
		if (certificate != null) {
			return certificate;
		}
		else {
			return null;
		}
	}
	
	@Transactional(readOnly=true)
	public void delete(Integer certificateId) {
		
		Certificate certificate = certificateRepository.findById(certificateId);
		if (certificate != null) {
			certificateRepository.delete(certificate);
		}
	}
	
	@Transactional(readOnly=true)
	public List<Certificate> getCertificates(Pageable page) {
		return certificateRepository.findAllByOrderByExpireDateDesc(page);
	}
	
	@Transactional(readOnly=true)
	public Integer getCertificateCount() {
		return certificateRepository.getCertificateCount();
	}
	
	@Transactional(readOnly=true)
	public Certificate getCertificateByPin(String pin) {
		
		List<Certificate> certificateList = certificateRepository.findByPinAndEnabledOrderByExpireDateDesc(pin,true);
		if (certificateList.size() > 0) {
			// returns the most updated
			return certificateList.get(0);
		}
		else {
			return null;
		}
	}	

}
