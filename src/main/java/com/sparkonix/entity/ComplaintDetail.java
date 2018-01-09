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
 * Database Table of Complaint detail
 */

@Entity
@Table(name = "complaint_detail")
@NamedQueries({
    @NamedQuery(name = "com.sparkonix.entity.ComplaintDetail.findAll", query = "SELECT cd FROM ComplaintDetail cd"),
    @NamedQuery(name = "com.sparkonix.entity.ComplaintDetail.findComplaintByCurrentStatus", 
    query = "SELECT cd FROM ComplaintDetail cd WHERE cd.id "
	    + "IN("
	    + "SELECT MAX(cdd.id) FROM ComplaintDetail cdd "
	    + "WHERE cdd.issueDetail_id =:ISSUEID)"),
    @NamedQuery(name = "com.sparkonix.entity.ComplaintDetail.findClosedDateByIssueNumIdTechId", query = "SELECT cd FROM ComplaintDetail cd"
    	+ " WHERE cd.id =:ISSUEID AND cd.assigned_to =:TECHID AND cd.status = :STATUS")
})
public class ComplaintDetail implements Serializable {

	private static final long serialVersionUID = 871956646450325454L;

	public static enum COMPLAINT_STATUS {
		OPEN, ASSIGNED, CLOSED, INPROGRESS
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "issueDetail_id", nullable = false)
	private long issueDetail_id;

/*	@Column(name = "details")
	private String details;*/

	@Column(name = "status")
	private String status;

	@Column(name = "date_reported")
	private Date date_reported;

	@Column(name = "assigned_to")
	private long assigned_to;

	@Column(name = "failure_reason")
	private String failure_reason;

	@Column(name = "action_taken")
	private String action_taken;

	@Column(name = "techinician_id")
	private long techinician_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIssueDetail_id() {
		return issueDetail_id;
	}

	public void setIssueDetail_id(long issueDetail_id) {
		this.issueDetail_id = issueDetail_id;
	}

/*	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}*/

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate_reported() {
		return date_reported;
	}

	public void setDate_reported(Date date_reported) {
		this.date_reported = date_reported;
	}

	public long getAssigned_to() {
		return assigned_to;
	}

	public void setAssigned_to(long assigned_to) {
		this.assigned_to = assigned_to;
	}

	public String getFailure_reason() {
		return failure_reason;
	}

	public void setFailure_reason(String failure_reason) {
		this.failure_reason = failure_reason;
	}

	public String getAction_taken() {
		return action_taken;
	}

	public void setAction_taken(String action_taken) {
		this.action_taken = action_taken;
	}

	public long getTechinician_id() {
		return techinician_id;
	}

	public void setTechinician_id(long techinician_id) {
		this.techinician_id = techinician_id;
	}
}
