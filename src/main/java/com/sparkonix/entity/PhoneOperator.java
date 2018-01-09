package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/*
 * Database Table of complaint operator it will add a operator. 
 */

@Entity
@Table(name = "phone_operator")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.PhoneOperator.findAll", query = "SELECT pho FROM PhoneOperator pho"),
		@NamedQuery(name = "com.sparkonix.entity.PhoneOperator.getOperatorByPhoneNumber", query = "SELECT pho FROM PhoneOperator pho WHERE pho.fld_mobile_number= :PHONE_NUMBER") })

public class PhoneOperator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1944389808000749615L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "fld_mobile_number", nullable = false)
	private String fld_mobile_number;

	@Column(name = "fld_name", nullable = false)
	private String fld_name;

	@Column(name = "fld_otp")
	private String fld_otp;

	@Column(name = "fld_fcm_token", nullable = false)
	private String fld_fcm_token;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFld_mobile_number() {
		return fld_mobile_number;
	}

	public void setFld_mobile_number(String fld_mobile_number) {
		this.fld_mobile_number = fld_mobile_number;
	}

	public String getFld_name() {
		return fld_name;
	}

	public void setFld_name(String fld_name) {
		this.fld_name = fld_name;
	}

	public String getFld_otp() {
		return fld_otp;
	}

	public void setFld_otp(String fld_otp) {
		this.fld_otp = fld_otp;
	}

	public String getFld_fcm_token() {
		return fld_fcm_token;
	}

	public void setFld_fcm_token(String fld_fcm_token) {
		this.fld_fcm_token = fld_fcm_token;
	}

}
