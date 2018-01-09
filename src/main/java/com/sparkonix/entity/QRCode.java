package com.sparkonix.entity;

import java.io.Serializable;

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
@Table(name = "qr_codes")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.QRCode.findAll", query = "SELECT qrc FROM QRCode qrc") })

public class QRCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5716529679263246804L;
	
	public static enum QRCODE_STATUS {
		ASSIGNED, UNASSIGNED
	};
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "qr_code")
	private String qr_code;
	
	@Column(name = "batch_name")
	private String batch_name;
	
	@Column(name = "status")
	private String status;
	
	/*
	 * @Column(name = "created_by", nullable = false) private long createdBy;
	 */

	// @JsonManagedReference("createdByRef")
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}

	public String getBatch_name() {
		return batch_name;
	}

	public void setBatch_name(String batch_name) {
		this.batch_name = batch_name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}