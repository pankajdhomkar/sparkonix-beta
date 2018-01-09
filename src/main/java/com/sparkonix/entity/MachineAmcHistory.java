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
@Table(name = "machine_amc_history")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.MachineAmcHistory.findAll", query = "SELECT mah FROM MachineAmcHistory mah") })

public class MachineAmcHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3838902989510326220L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "machine_id", nullable = false)
	private long machine_id;

	@Column(name = "amc_type")
	private String amc_type;
	
	@Column(name = "amc_startdate")
	private Date amc_startdate;
	
	@Column(name = "amc_enddate")
	private Date amc_enddate;
	
	@Column(name = "details")
	private String details;
	
	@Column(name = "amc_status")
	private String amc_status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMachine_id() {
		return machine_id;
	}

	public void setMachine_id(long machine_id) {
		this.machine_id = machine_id;
	}

	public String getAmc_type() {
		return amc_type;
	}

	public void setAmc_type(String amc_type) {
		this.amc_type = amc_type;
	}

	public Date getAmc_startdate() {
		return amc_startdate;
	}

	public void setAmc_startdate(Date amc_startdate) {
		this.amc_startdate = amc_startdate;
	}

	public Date getAmc_enddate() {
		return amc_enddate;
	}

	public void setAmc_enddate(Date amc_enddate) {
		this.amc_enddate = amc_enddate;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getAmc_status() {
		return amc_status;
	}

	public void setAmc_status(String amc_status) {
		this.amc_status = amc_status;
	}
	
	
}
