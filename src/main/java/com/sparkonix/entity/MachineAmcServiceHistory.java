package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "machine_amc_service_history")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findAll", query = "SELECT m FROM MachineAmcServiceHistory m "),
    @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByManufacturerID", query = "SELECT m FROM MachineAmcServiceHistory m "
	    + "WHERE m.manufacturer_id = :MANID"),
    @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByResellerID", query = "SELECT m FROM MachineAmcServiceHistory m "
	    + "WHERE m.reseller_id = :RESELLERID"),
    @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician", query = "SELECT m FROM MachineAmcServiceHistory m "
	    + "WHERE m.assignedTo = :TECHID"),
    @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByMachineID", query = "SELECT m FROM MachineAmcServiceHistory m "
	    + "WHERE m.machine = :MACHINEID"), 
    @NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findAllByMachineId", query = "SELECT m FROM MachineAmcServiceHistory m "
	    + "WHERE m.machine.id = :MACHINE_ID")})

public class MachineAmcServiceHistory implements Serializable{
    
	private static final long serialVersionUID = 8310907395443757240L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "amc_id")
	private long amc_id;
	
	@Column(name = "details")
	private String details;
	
	@Column(name = "servicing_assigned_date")
	private Date servicing_assigned_date;
	
	@Column(name = "servicing_done_date")
	private Date servicing_done_date;
	
	@Column(name = "action_taken")
	private String action_taken;
	
	@Column(name = "status")
	private String status;
	
	//@JsonManagedReference("machineIdRef")

	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_id", nullable = false)
	private Machine machine;
		
	//@JsonManagedReference("assignedToRef")
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_to", nullable = false)
	private  User assignedTo;

	
	@Column(name = "manufacturer_id")
	private long manufacturer_id;
	
	@Column(name = "reseller_id")
	private long reseller_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAmc_id() {
		return amc_id;
	}

	public void setAmc_id(long amc_id) {
		this.amc_id = amc_id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getServicing_assigned_date() {
		return servicing_assigned_date;
	}

	public void setServicing_assigned_date(Date servicing_assigned_date) {
		this.servicing_assigned_date = servicing_assigned_date;
	}

	public Date getServicing_done_date() {
		return servicing_done_date;
	}

	public void setServicing_done_date(Date servicing_done_date) {
		this.servicing_done_date = servicing_done_date;
	}

	public String getAction_taken() {
		return action_taken;
	}

	public void setAction_taken(String action_taken) {
		this.action_taken = action_taken;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Machine getMachine() {
	    return machine;
	}

	public void setMachine(Machine machine) {
	    this.machine = machine;
	}

	public User getAssignedTo() {
	    return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
	    this.assignedTo = assignedTo;
	}
}
