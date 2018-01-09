package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Pankaj Dhomkar
 *
 */
@Entity
@Table(name = "issue_technician")

@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.IssueTechnician.findAll", query = "SELECT it FROM IssueTechnician it"),
		@NamedQuery(name = "com.sparkonix.entity.IssueTechnician.findByAssingedTechId", query = "SELECT it FROM IssueTechnician it WHERE it.technician_id =:TECHNICIANID") })
public class IssueTechnician implements Serializable {

	private static final long serialVersionUID = 5998630289304503656L;

	@Column(name = "issue_id", nullable = false)
	private long issue_id;

	@Column(name = "technician_id", nullable = false)
	private long technician_id;

	public long getIssue_id() {
		return issue_id;
	}

	public void setIssue_id(long issue_id) {
		this.issue_id = issue_id;
	}

	public long getTechnician_id() {
		return technician_id;
	}

	public void setTechnician_id(long technician_id) {
		this.technician_id = technician_id;
	}
}
