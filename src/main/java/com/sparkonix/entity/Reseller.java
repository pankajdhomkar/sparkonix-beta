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
@Table(name = "reseller")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Reseller.findAll", query = "SELECT re FROM Reseller re"),
	// For getting a list of Resellers of manufacturers
			
    @NamedQuery(name = "com.sparkonix.entity.Reseller.findAllResellerByManufacturerId", query = "SELECT re FROM Reseller re WHERE re.fld_manuid= :MANUFACTURERID"),
			
    //This query find a reseller detail by its id
    @NamedQuery(name = "com.sparkonix.entity.Reseller.findResellerId", query = "SELECT re FROM Reseller re WHERE re.id= :RESELLERID"),
			
    @NamedQuery(name = "com.sparkonix.entity.Reseller.findResellerPan", query = "SELECT re FROM Reseller re WHERE re.pan= :PAN"),
			
    @NamedQuery(name = "com.sparkonix.entity.Reseller.findAllByOnBoarded", query = "SELECT re FROM Reseller re WHERE re.onboardedby= :ON_BOARDED_BY")})

public class Reseller implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4425403190567554106L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "fld_manuid", nullable = false)
	private long fld_manuid;
	
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
	
	@Column(name = "cur_subscription_status")
	private String cur_subscription_status;
	
	@Column(name = "cur_subscription_startdate")
	private Date cur_subscription_startdate;
	
	@Column(name = "cur_subscription_enddate")
	private Date cur_subscription_enddate;
	
	@Column(name = "onboardedby")
	private long onboardedby;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFld_manuid() {
		return fld_manuid;
	}

	public void setFld_manuid(long fld_manuid) {
		this.fld_manuid = fld_manuid;
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

	public String getCur_subscription_status() {
		return cur_subscription_status;
	}

	public void setCur_subscription_status(String cur_subscription_status) {
		this.cur_subscription_status = cur_subscription_status;
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

	public long getOnboardedby() {
		return onboardedby;
	}

	public void setOnboardedby(long onboardedby) {
		this.onboardedby = onboardedby;
	}

}











