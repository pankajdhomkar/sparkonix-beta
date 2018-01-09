package com.sparkonix.entity.dto;

import com.sparkonix.entity.ComplaintDetail;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.IssueNumberDetail;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.User;

/**
 * @author Pankaj Dhomkar
 * Complaint DTO will store the all info and send as response
 */
public class ComplaintDTO {
	private ComplaintDetail complaintDetail;
	private IssueNumberDetail issueNumberDetail;
	private Machine machine;
	private Customer customer;
	private User user;
	
	public ComplaintDetail getComplaintDetail() {
		return complaintDetail;
	}
	public void setComplaintDetail(ComplaintDetail complaintDetail) {
		this.complaintDetail = complaintDetail;
	}
	public IssueNumberDetail getIssueNumberDetail() {
		return issueNumberDetail;
	}
	public void setIssueNumberDetail(IssueNumberDetail issueNumberDetail) {
		this.issueNumberDetail = issueNumberDetail;
	}
	public Machine getMachine() {
		return machine;
	}
	public void setMachine(Machine machine) {
		this.machine = machine;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
