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

@Entity
@Table(name = "unregister_operator")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.UnregisterOperator.findAll", 
		query = "SELECT uo FROM UnregisterOperator uo"),
		@NamedQuery(name = "com.sparkonix.entity.UnregisterOperator.getOperatorByPhoneNumber1", 
		query = "SELECT uo FROM UnregisterOperator uo WHERE uo.fld_mobile_number= :PHONE_NUMBER")})
public class UnregisterOperator implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3571532620501933931L;
	//Id automatically increase
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	//For mobile no
	@Column(name = "fld_mobile_number")
	private String fld_mobile_number;
	//For name of operator
	@Column(name = "fld_name")
	private String fldOperatorName_unregister;
	//For OTP
	@Column(name = "fld_otp")
	private String fldOtp_unregister;
	
	@Column(name = "fld_fcm_token")
	private String fldFcmToken_unregister;

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

	public String getFldOperatorName_unregister() {
		return fldOperatorName_unregister;
	}

	public void setFldOperatorName_unregister(String fldOperatorName_unregister) {
		this.fldOperatorName_unregister = fldOperatorName_unregister;
	}

	public String getFldOtp_unregister() {
		return fldOtp_unregister;
	}

	public void setFldOtp_unregister(String fldOtp_unregister) {
		this.fldOtp_unregister = fldOtp_unregister;
	}

	public String getFldFcmToken_unregister() {
		return fldFcmToken_unregister;
	}

	public void setFldFcmToken_unregister(String fldFcmToken_unregister) {
		this.fldFcmToken_unregister = fldFcmToken_unregister;
	}
}
