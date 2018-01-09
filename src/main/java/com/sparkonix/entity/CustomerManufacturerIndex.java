package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "customer_manufacturer_index")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.CustomerManufacturerIndex.findAll", query ="SELECT custmanin FROM CustomerManufacturerIndex custmanin")})

public class CustomerManufacturerIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1431272638613549889L;
	
	@Column(name = "manu_id", nullable = false)
	private int manu_id;
	
	@Column(name = "cust_id", nullable = false)
	private int cust_id;

	public int getManu_id() {
		return manu_id;
	}

	public void setManu_id(int manu_id) {
		this.manu_id = manu_id;
	}

	public int getCust_id() {
		return cust_id;
	}

	public void setCust_id(int cust_id) {
		this.cust_id = cust_id;
	}
	
}
