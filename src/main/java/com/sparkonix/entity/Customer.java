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
@Table(name = "customer")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Customer.findAll", query = "SELECT cust FROM Customer cust"),
	@NamedQuery(name = "com.sparkonix.entity.Customer.findCustomerByManufacturerID", query="SELECT cust FROM Customer cust INNER JOIN com.sparkonix.entity.CustomerManufacturerIndex cusmanin"
			+ " ON cust.id = cusmanin.cust_id"
			+ " AND cusmanin.manu_id = :MAN_ID"),
	@NamedQuery(name = "com.sparkonix.entity.Customer.findCustomerByResellerID", query="SELECT cust FROM Customer cust INNER JOIN com.sparkonix.entity.CustomerResellerIndex cusresin"
			+ " ON cust.id = cusresin.cust_id"
			+ " WHERE cusresin.rese_id = :MAN_ID")})


public class Customer implements Serializable{

	private static final long serialVersionUID = -5220738193999843211L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "company_type", nullable = false)
	private String company_type;

	@Column(name = "company_name", nullable = false)
	private String company_name;
	
	@Column(name = "pan", nullable = false)
	private String pan;
	
	@Column(name = "cust_support_name")
	private String cust_support_name;
	
	@Column(name = "cust_support_phone")
	private String cust_support_phone;
	
	@Column(name = "cust_support_email")
	private String cust_support_email;
	
	/*@Column(name = "cur_subscription_type")
	private String cur_subscription_type;
	
	@Column(name = "cur_subscription_startdate")
	private Date cur_subscription_startdate;
	
	@Column(name = "cur_subscription_enddate")
	private Date cur_subscription_enddate;
	
	@Column(name = "cur_subscription_status")
	private String cur_subscription_status;*/

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompany_type() {
		return company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCust_support_name() {
		return cust_support_name;
	}

	public void setCust_support_name(String cust_support_name) {
		this.cust_support_name = cust_support_name;
	}

	public String getCust_support_phone() {
		return cust_support_phone;
	}

	public void setCust_support_phone(String cust_support_phone) {
		this.cust_support_phone = cust_support_phone;
	}

	public String getCust_support_email() {
		return cust_support_email;
	}

	public void setCust_support_email(String cust_support_email) {
		this.cust_support_email = cust_support_email;
	}

}


