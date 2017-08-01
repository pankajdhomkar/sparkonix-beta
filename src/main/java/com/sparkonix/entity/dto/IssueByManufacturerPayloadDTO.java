package com.sparkonix.entity.dto;

import java.util.Date;

public class IssueByManufacturerPayloadDTO {
	private long manufacturerId;
	private long resellerId;
	private long customerId;
	private long machineId;
	private String supportAssistance;
	
	public long getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	public long getResellerId() {
		return resellerId;
	}
	public void setResellerId(long resellerId) {
		this.resellerId = resellerId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public long getMachineId() {
		return machineId;
	}
	public void setMachineId(long machineId) {
		this.machineId = machineId;
	}
	public String getSupportAssistance() {
		return supportAssistance;
	}
	public void setSupportAssistance(String supportAssistance) {
		this.supportAssistance = supportAssistance;
	}
	
	
	
}
