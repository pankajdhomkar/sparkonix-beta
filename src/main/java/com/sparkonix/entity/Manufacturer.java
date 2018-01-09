package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "manufacturer", schema = "public")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Manufacturer.findAll", query = "SELECT man FROM Manufacturer man"),
	@NamedQuery(name = "com.sparkonix.entity.Manufacturer.findmanufacturerDetailByPan", 
	query = "SELECT man FROM Manufacturer man WHERE man.pan= :PAN"),
	@NamedQuery(name = "com.sparkonix.entity.Manufacturer.findAllByOnBoardedId", query = "SELECT man FROM Manufacturer man WHERE man.onBoardedBy = :ON_BOARDED_BY AND man.company_type = :COMPANY_TYPE")})

public class Manufacturer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1402591810426814306L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "company_type")
	private String company_type;
	
	@Column(name = "company_name")
	private String company_name;
	
	@Column(name = "pan")
	private String pan;
	
	@Column(name = "cust_support_name")
	private String cust_support_name;
	
	@Column(name = "cust_support_phone")
	private String cust_support_phone;
	
	@Column(name = "cust_support_email")
	private String cust_support_email;
	
	@Column(name = "cur_subscription_type")
	private String cur_subscription_type;
	
	@Column(name = "cur_subscription_startdate")
	private Date cur_subscription_startdate;
	
	@Column(name = "cur_subscription_enddate")
	private Date cur_subscription_enddate;
	
	@Column(name = "cur_subscription_status")
	private String cur_subscription_status;
	
	@Column(name = "onboardedby")
	private long onBoardedBy;

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

	public String getCur_subscription_type() {
		return cur_subscription_type;
	}

	public void setCur_subscription_type(String cur_subscription_type) {
		this.cur_subscription_type = cur_subscription_type;
	}

	public Date getCur_subscription_startdate() {
		return cur_subscription_startdate;
	}

	public void setCur_subscription_startdate(Date cur_subscription_startdate) {
		this.cur_subscription_startdate = cur_subscription_startdate;
	}

	public Date getCur_subscription_enddate() {
		return cur_subscription_enddate;
	}

	public void setCur_subscription_enddate(Date cur_subscription_enddate) {
		this.cur_subscription_enddate = cur_subscription_enddate;
	}

	public String getCur_subscription_status() {
		return cur_subscription_status;
	}

	public void setCur_subscription_status(String cur_subscription_status) {
		this.cur_subscription_status = cur_subscription_status;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}
	
	
	

}