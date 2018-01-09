package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.CustomerManufacturerIndex;

import io.dropwizard.hibernate.AbstractDAO;

public class CustomerManufacturerIndexDAO extends AbstractDAO<CustomerManufacturerIndex>{
	
	public CustomerManufacturerIndexDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	public CustomerManufacturerIndex save(CustomerManufacturerIndex customerManufacturerIndex) throws Exception{
		return persist(customerManufacturerIndex);
	}
	
	public CustomerManufacturerIndex getById(long custManId) throws Exception{
		return get(custManId);
	}
	
	public List<CustomerManufacturerIndex> findAll(){
		return list(namedQuery("com.sparkonix.entity.CustomerManufacturerIndex.findAll"));
	}
	
	
}
