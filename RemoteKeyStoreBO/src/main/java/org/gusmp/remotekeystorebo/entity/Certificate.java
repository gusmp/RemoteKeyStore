package org.gusmp.remotekeystorebo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CERTIFICATE")
@Getter
@Setter
public class Certificate {
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;
	
	@Column(length=300)
	private String subject;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireDate;
	
	@Column(length=50)
	private String pin;
	
	@Column(length=300)
	private String alias;
	
	@Lob
	private byte[] pkcs12;
	
	@Lob
	private byte[] certificate;
	
	private Boolean enabled;
	
	@Column(length=300)
	private String issuer;
	
	@Column(length=300)
	private String serialNumber;
	
	@OneToMany(mappedBy="certificate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	List<Log> logList = new ArrayList<Log>();

}
