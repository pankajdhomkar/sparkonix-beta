package com.sparkonix.entity.dto;

import java.util.Date;

/**
 * @author Pankaj Dhomkar
 *
 */
public class ComplaintSearchFilterPayloadDTO {
	private long customerId;
	private Date startDate;
	private Date endDate;
	private long manufacturerID;
	private long roleID;
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public long getManufacturerID() {
		return manufacturerID;
	}
	public void setManufacturerID(long manufacturerID) {
		this.manufacturerID = manufacturerID;
	}
	public long getRoleID() {
		return roleID;
	}
	public void setRoleID(long roleID) {
		this.roleID = roleID;
	}
	
	
}
