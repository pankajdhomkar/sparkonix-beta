package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "customer_reseller_index")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.CustomerResellerIndex.findAll", query ="SELECT custresin FROM CustomerResellerIndex custresin"),
	@NamedQuery(name = "com.sparkonix.entity.CustomerResellerIndex.findAllCustomerIdByResellerId", query ="SELECT custresin FROM CustomerResellerIndex custresin WHERE custresin.rese_id = :RESELLER_ID")})

public class CustomerResellerIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9028981992998594717L;
	
	@Column(name = "rese_id", nullable = false)
	private int rese_id;
	
	@Column(name = "cust_id", nullable = false)
	private int cust_id;

	public int getRese_id() {
		return rese_id;
	}

	public void setRese_id(int rese_id) {
		this.rese_id = rese_id;
	}

	public int getCust_id() {
		return cust_id;
	}

	public void setCust_id(int cust_id) {
		this.cust_id = cust_id;
	}
	
}
