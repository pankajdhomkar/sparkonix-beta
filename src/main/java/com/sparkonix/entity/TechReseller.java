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

@Entity
@Table(name = "tech_reseller")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.TechReseller.findAll", query = "SELECT tr FROM TechReseller tr") })

public class TechReseller implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 300580695410736210L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "userid", nullable = false)
	private long userid;
	
	@Column(name = "reseller_id", nullable = false)
	private long reseller_id;
	
	

	public long getId() {
	    return id;
	}

	public void setId(long id) {
	    this.id = id;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public long getReseller_id() {
		return reseller_id;
	}

	public void setReseller_id(long reseller_id) {
		this.reseller_id = reseller_id;
	}
	
}
