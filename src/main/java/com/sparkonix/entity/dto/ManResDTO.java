package com.sparkonix.entity.dto;

import java.io.Serializable;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;

public class ManResDTO implements Serializable {

	private static final long serialVersionUID = 3090830713597004836L;

	CompanyDetail manResDetail;
	User webAdminUser;
	Reseller reseller;
	String companyType;
	
	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public CompanyDetail getManResDetail() {
		return manResDetail;
	}

	public void setManResDetail(CompanyDetail manResDetail) {
		this.manResDetail = manResDetail;
	}

	public User getWebAdminUser() {
		return webAdminUser;
	}

	public void setWebAdminUser(User webAdminUser) {
		this.webAdminUser = webAdminUser;
	}

	public Reseller getReseller() {
		return reseller;
	}

	public void setReseller(Reseller reseller) {
		this.reseller = reseller;
	}
}
