package com.sparkonix.entity.dto;

import java.io.Serializable;

import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;

public class ManufacturerResellerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7062394873435231567L;
	
	Manufacturer manufacturer;
	User webAdminUser;
	Reseller reseller;
	String companyType;
	
	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
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
