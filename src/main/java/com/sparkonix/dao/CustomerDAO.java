package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.Customer;

import io.dropwizard.hibernate.AbstractDAO;

public class CustomerDAO extends AbstractDAO<Customer>{

	public CustomerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	/*
	 * Either save or update the given instance, depending upon resolution
	 * of the unsaved-value checks. This operation cascades to associated
	 * instances if the association is mapped with cascade="save-update".
	 */
	
	public Customer save(Customer customer) throws Exception{
		return persist(customer);
	}
	
	public Customer getById(long customerId) throws Exception {
		return get(customerId);
	}
	
	public List<Customer> findAll(){
		return list(namedQuery("com.sparkonix.entity.Customer.findAll"));
	}
	
	//It will return the manufacturers customers.
	public List<Customer> findCustomerByManufacturerID(long manId){
		return list(namedQuery("com.sparkonix.entity.Customer.findCustomerByManufacturerID").setParameter("MAN_ID", manId));
	}

	//It will return the resellers customers
	public List<Customer> findCustomerByResellerID(long resId){
		return list(namedQuery("com.sparkonix.entity.Customer.findCustomerByResellerID").setParameter("RES_ID", resId));
	}
	
	/*-------------Unique no isssue so it can change afterwards-------------------*/
	//It will return the customer information using a pan number
	public Customer findCustomerByPan(String pan) {
		return uniqueResult(namedQuery("com.sparkonix.entity.Customer.findCustomerByPan")
				.setParameter("PAN", pan));
	}

}
