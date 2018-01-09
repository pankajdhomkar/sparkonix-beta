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
@Table(name = "tech_manufacturer")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.TechManufacturer.findAll", query = "SELECT tm FROM TechManufacturer tm") })

public class TechManufacturer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5526432266721150872L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "userid", nullable = false)
	private long userid;
	
	@Column(name = "manufacturer_id", nullable = false)
	private long manufacturer_id;

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

	public long getManufacturer_id() {
		return manufacturer_id;
	}

	public void setManufacturer_id(long manufacturer_id) {
		this.manufacturer_id = manufacturer_id;
	}
	
}