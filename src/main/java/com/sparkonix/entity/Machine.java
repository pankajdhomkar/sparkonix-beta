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

/*
 * Database Table of Machine here declare a table and getter and setter method.
 * Define query so that call by DAO 
 */

@Entity
@Table(name = "machine")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Machine.findAll", query = "SELECT mach FROM Machine mach"),
		@NamedQuery(name = "com.sparkonix.entity.Machine.findAllByCustomerId", query = "SELECT mach FROM Machine mach "
				+ "WHERE mach.customer_id = :CUSTOMER_ID") })

public class Machine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2358798360762574178L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "qr_code_id")
	private long qr_code_id;

	@Column(name = "serial_number")
	private String serial_number;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "model_number")
	private String model_number;

	@Column(name = "machine_year")
	private String machine_year;

	@Column(name = "customer_id", nullable = false)
	private long customer_id;

	@Column(name = "manufacturer_id", nullable = false)
	private long manufacturer_id;

	@Column(name = "reseller_id")
	private long reseller_id;

	@Column(name = "installation_date")
	private Date installation_date;

	@Column(name = "warranty_expiry_date")
	private Date warranty_expiry_date;

	@Column(name = "location_id", nullable = false)
	private long location_id;

	@Column(name = "cur_amc_type")
	private String cur_amc_type;

	@Column(name = "cur_amc_startdate")
	private Date cur_amc_startdate;

	@Column(name = "cur_amc_enddate")
	private Date cur_amc_enddate;

	@Column(name = "cur_amc_status")
	private String cur_amc_status;

	/*@Column(name = "cur_subscription_type")
	private String cur_subscription_types;

	@Column(name = "cur_subscription_startdate")
	private Date cur_subscription_startdate;

	@Column(name = "cur_subscription_enddate")
	private Date cur_subscription_enddate;

	@Column(name = "cur_subscription_status")
	private String cur_subscription_status;*/

	@Column(name = "support_assistance")
	private long support_assistance;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getQr_code_id() {
		return qr_code_id;
	}

	public void setQr_code_id(long qr_code_id) {
		this.qr_code_id = qr_code_id;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModel_number() {
		return model_number;
	}

	public void setModel_number(String model_number) {
		this.model_number = model_number;
	}

	public String getMachine_year() {
		return machine_year;
	}

	public void setMachine_year(String machine_year) {
		this.machine_year = machine_year;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
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

	public Date getInstallation_date() {
		return installation_date;
	}

	public void setInstallation_date(Date installation_date) {
		this.installation_date = installation_date;
	}

	public Date getWarranty_expiry_date() {
		return warranty_expiry_date;
	}

	public void setWarranty_expiry_date(Date warranty_expiry_date) {
		this.warranty_expiry_date = warranty_expiry_date;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public String getCur_amc_type() {
		return cur_amc_type;
	}

	public void setCur_amc_type(String cur_amc_type) {
		this.cur_amc_type = cur_amc_type;
	}

	public Date getCur_amc_startdate() {
		return cur_amc_startdate;
	}

	public void setCur_amc_startdate(Date cur_amc_startdate) {
		this.cur_amc_startdate = cur_amc_startdate;
	}

	public Date getCur_amc_enddate() {
		return cur_amc_enddate;
	}

	public void setCur_amc_enddate(Date cur_amc_enddate) {
		this.cur_amc_enddate = cur_amc_enddate;
	}

	public String getCur_amc_status() {
		return cur_amc_status;
	}

	public void setCur_amc_status(String cur_amc_status) {
		this.cur_amc_status = cur_amc_status;
	}

	public long getSupport_assistance() {
		return support_assistance;
	}

	public void setSupport_assistance(long support_assistance) {
		this.support_assistance = support_assistance;
	}

}
