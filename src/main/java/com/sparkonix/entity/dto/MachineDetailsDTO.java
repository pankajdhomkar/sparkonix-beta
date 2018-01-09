package com.sparkonix.entity.dto;

import java.util.Date;

import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.Reseller;

/**
 * @author Pankaj Dhomkar
 *
 */

public class MachineDetailsDTO {

	private long machineId;
	private String serialNumber;
	private String modelNumber;
	private Date installationDate;
	private Date lastServiceDate;
	private String curAmcType;
	private Date curAmcStartDate;
	private Date curAmcEndDate;
	private String curAmcStatus;
	private String machineDocuments;//
	private String adminName;
	private String adminEmail;
	private String adminContact;
	private long qrCode_id;
	private String name;
	
	private Manufacturer manufacturer;
	private Reseller reseller;
	private CompanyLocation companyLocation;
	public long getMachineId() {
		return machineId;
	}
	public void setMachineId(long machineId) {
		this.machineId = machineId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	public Date getInstallationDate() {
		return installationDate;
	}
	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}
	public Date getLastServiceDate() {
		return lastServiceDate;
	}
	public void setLastServiceDate(Date lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}
	public String getCurAmcType() {
		return curAmcType;
	}
	public void setCurAmcType(String curAmcType) {
		this.curAmcType = curAmcType;
	}
	public Date getCurAmcStartDate() {
		return curAmcStartDate;
	}
	public void setCurAmcStartDate(Date curAmcStartDate) {
		this.curAmcStartDate = curAmcStartDate;
	}
	public Date getCurAmcEndDate() {
		return curAmcEndDate;
	}
	public void setCurAmcEndDate(Date curAmcEndDate) {
		this.curAmcEndDate = curAmcEndDate;
	}
	public String getCurAmcStatus() {
		return curAmcStatus;
	}
	public void setCurAmcStatus(String curAmcStatus) {
		this.curAmcStatus = curAmcStatus;
	}
	public String getMachineDocuments() {
		return machineDocuments;
	}
	public void setMachineDocuments(String machineDocuments) {
		this.machineDocuments = machineDocuments;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAdminEmail() {
		return adminEmail;
	}
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	public String getAdminContact() {
		return adminContact;
	}
	public void setAdminContact(String adminContact) {
		this.adminContact = adminContact;
	}
	public long getQrCode_id() {
		return qrCode_id;
	}
	public void setQrCode_id(long qrCode_id) {
		this.qrCode_id = qrCode_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Reseller getReseller() {
		return reseller;
	}
	public void setReseller(Reseller reseller) {
		this.reseller = reseller;
	}
	public CompanyLocation getCompanyLocation() {
		return companyLocation;
	}
	public void setCompanyLocation(CompanyLocation companyLocation) {
		this.companyLocation = companyLocation;
	}
	
	

}
