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

/**
 * @author Pankaj Dhomkar It will store the issue number phone device id and
 *         name machine id
 * 
 */
@Entity
@Table(name = "issuenumber_detail")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.IssueNumberDetail.findAll", query = "SELECT ind FROM ComplaintDetail ind"),
		@NamedQuery(name = "com.sparkonix.entity.IssueNumberDetail.findAllBySupportAssitanaceAndManId", query = "SELECT ind FROM IssueNumberDetail ind WHERE ind.machine_support_assistance = :SUPPORT_ASSISTANCE AND ind.manufacturer_id = :MANUFACTURER_ID"),
		@NamedQuery(name = "com.sparkonix.entity.IssueNumberDetail.findAllBySupportAssitanaceAndResId", query = "SELECT ind FROM IssueNumberDetail ind WHERE ind.machine_support_assistance = :SUPPORT_ASSISTANCE AND ind.reseller_id = :RESELLR_ID") })
public class IssueNumberDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4831367979835661221L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "issue_number", nullable = false)
	private String issue_number;

	@Column(name = "machine_id", nullable = false)
	private long machine_id;

	@Column(name = "reporting_device")
	private long reporting_device;

	@Column(name = "phonedevice_operator_name")
	private String phonedevice_operator_name;

	@Column(name = "manufacturer_id")
	private long manufacturer_id;

	@Column(name = "reseller_id")
	private long reseller_id;

	@Column(name = "customer_id")
	private long customer_id;

	@Column(name = "machine_support_assistance")
	private long machine_support_assistance;

	@Column(name = "details")
	private String details;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIssue_number() {
		return issue_number;
	}

	public void setIssue_number(String string) {
		this.issue_number = string;
	}

	public long getMachine_id() {
		return machine_id;
	}

	public void setMachine_id(long machine_id) {
		this.machine_id = machine_id;
	}

	public long getReporting_device() {
		return reporting_device;
	}

	public void setReporting_device(long reporting_device) {
		this.reporting_device = reporting_device;
	}

	public String getPhonedevice_operator_name() {
		return phonedevice_operator_name;
	}

	public void setPhonedevice_operator_name(String phonedevice_operator_name) {
		this.phonedevice_operator_name = phonedevice_operator_name;
	}

	public long getManufacturer_id() {
		return manufacturer_id;
	}

	public void setManufacturer_id(long manufacturer_id) {
		this.manufacturer_id = manufacturer_id;
	}

	public long getReseller_id() {
		return reseller_id;
	}

	public void setReseller_id(long reseller_id) {
		this.reseller_id = reseller_id;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public long getMachine_support_assistance() {
		return machine_support_assistance;
	}

	public void setMachine_support_assistance(long supportAssistance) {
		this.machine_support_assistance = supportAssistance;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
