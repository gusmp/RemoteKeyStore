package org.gusmp.remotekeystorebo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gusmp.remotekeystorebo.bean.request.GetKeyStoreRequest;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "LOG")
@Getter
@Setter
public class Log {
	
	public enum SOURCE {BO, APPLICATION, UNKNOWN }
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;

	@Column(length=50)
	private String userName;

	@Column(length=50)
	private String password;

	@Column(length=50)
	private String label;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStampAction;

	@Column(length=50)
	private String host;

	@Column(length=50)
	private String ip;
	
	@Enumerated(EnumType.STRING)
	private SOURCE source;
	
	@Column(length=500)
	private String message;
	
	@ManyToOne(optional=true)
	@JoinColumn(name = "certificate_id")
	private Certificate certificate;
	
	public Log() {}
	
	public Log(GetKeyStoreRequest getKeyStoreRequest) {
		
		this.setHost(getKeyStoreRequest.getHost());
		this.setIp(getKeyStoreRequest.getIp());
		this.setLabel(getKeyStoreRequest.getLabel());
		this.setPassword(getKeyStoreRequest.getPassword());
		this.setTimeStampAction(new Date());
		this.setUserName(getKeyStoreRequest.getUser());
	}
	
}
