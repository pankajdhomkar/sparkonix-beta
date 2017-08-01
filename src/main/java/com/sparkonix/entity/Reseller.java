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
		@NamedQuery(name = "com.sparkonix.entity.Reseller.findResellerPan", query = "SELECT re FROM Reseller re WHERE re.pan= :PAN"),
		@NamedQuery(name = "com.sparkonix.entity.Reseller.findAllByOnBoardedId", query = "SELECT re FROM Reseller re WHERE re.onBoardedBy= :ON_BOARDED_BY"),
		 // For getting a list of Resellers of manufacturers
		@NamedQuery(name = "com.sparkonix.entity.Reseller.findAllById", query = "SELECT re FROM Reseller re WHERE re.fld_manufid= :MANID"),
		@NamedQuery(name = "com.sparkonix.entity.Reseller.findResellerId", query = "SELECT re FROM Reseller re WHERE re.id = :RESELLERID")
})
public class Reseller implements Serializable {
	
	private static final long serialVersionUID = -259263174740554225L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "fld_manufid", nullable = false)
	private long fld_manufid;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "pan")
	private String pan;

	@Column(name = "cust_support_name")
	private String custSupportName;

	@Column(name = "cust_support_phone")
	private String custSupportPhone;

	@Column(name = "cust_support_email")
	private String custSupportEmail;

	@Column(name = "cur_subscription_type")
	private String curSubscriptionType;

	@Column(name = "cur_subscription_status")
	private String curSubscriptionStatus;

	@Column(name = "cur_subscription_startdate")
	private Date curSubscriptionStartDate;

	@Column(name = "cur_subscription_enddate")
	private Date curSubscriptionEndDate;

	@Column(name = "on_boarded_by")
	private long onBoardedBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFld_manufid() {
		return this.fld_manufid;
	}

	public void setFld_manufid(long fld_manufid) {
		this.fld_manufid = fld_manufid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCustSupportName() {
		return custSupportName;
	}

	public void setCustSupportName(String custSupportName) {
		this.custSupportName = custSupportName;
	}

	public String getCustSupportPhone() {
		return custSupportPhone;
	}

	public void setCustSupportPhone(String custSupportPhone) {
		this.custSupportPhone = custSupportPhone;
	}

	public String getCustSupportEmail() {
		return custSupportEmail;
	}

	public void setCustSupportEmail(String custSupportEmail) {
		this.custSupportEmail = custSupportEmail;
	}

	public String getCurSubscriptionType() {
		return curSubscriptionType;
	}

	public void setCurSubscriptionType(String curSubscriptionType) {
		this.curSubscriptionType = curSubscriptionType;
	}

	public String getCurSubscriptionStatus() {
		return curSubscriptionStatus;
	}

	public void setCurSubscriptionStatus(String curSubscriptionStatus) {
		this.curSubscriptionStatus = curSubscriptionStatus;
	}

	public Date getCurSubscriptionStartDate() {
		return curSubscriptionStartDate;
	}

	public void setCurSubscriptionStartDate(Date curSubscriptionStartDate) {
		this.curSubscriptionStartDate = curSubscriptionStartDate;
	}

	public Date getCurSubscriptionEndDate() {
		return curSubscriptionEndDate;
	}

	public void setCurSubscriptionEndDate(Date curSubscriptionEndDate) {
		this.curSubscriptionEndDate = curSubscriptionEndDate;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}
}

// @NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAll",
// query = "SELECT cd FROM CompanyDetail cd"),
//
// @NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAllByOnBoardedId",
// query = "SELECT cd FROM CompanyDetail cd "
// + "WHERE cd.onBoardedBy= :ON_BOARDED_BY AND cd.companyType= :COMPANY_TYPE"),
//
// @NamedQuery(name =
// "com.sparkonix.entity.CompanyDetail.findByLocationOnBoardedId",
// query = "SELECT DISTINCT cd FROM CompanyDetail cd, CompanyLocation loc WHERE
// "
// + "cd.onBoardedBy= :ON_BOARDED_BY AND " + "cd.companyType= :COMPANY_TYPE OR "
// + "(loc.onBoardedBy= :ON_BOARDED_BY AND cd.id = loc. companyDetailsId)"),
//
// @NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAllByCompanyType",
// query = "SELECT cd FROM CompanyDetail cd WHERE cd.companyType=
// :COMPANY_TYPE"),
//
// @NamedQuery(name =
// "com.sparkonix.entity.CompanyDetail.findCompanyDetailByPanAndCompanyType",
// query = "SELECT cd FROM CompanyDetail cd WHERE cd.pan= :PAN AND
// cd.companyType= :COMPANY_TYPE"),
//
// @NamedQuery(name =
// "com.sparkonix.entity.CompanyDetail.findCompanyDetailByPan",
// query = "SELECT cd FROM CompanyDetail cd WHERE cd.pan= :PAN")