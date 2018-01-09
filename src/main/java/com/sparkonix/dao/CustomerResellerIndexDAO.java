package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.CustomerResellerIndex;

import io.dropwizard.hibernate.AbstractDAO;

public class CustomerResellerIndexDAO extends AbstractDAO<CustomerResellerIndex>{

	public CustomerResellerIndexDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	public CustomerResellerIndex save(CustomerResellerIndex customerResellerIndex) throws Exception{
		return persist(customerResellerIndex);
	}
	
	public CustomerResellerIndex getById(long customerResellerId) throws Exception{
		return get(customerResellerId);
	}
	
	public List<CustomerResellerIndex> findAll(){
		return list(namedQuery("com.sparkonix.entity.CustomerResellerIndex.findAll"));
	}
	//It will return the list of customer id.
	public List<CustomerResellerIndex> findAllCustomerIdByResellerId(long resellerId){
		return list(namedQuery("com.sparkonix.entity.CustomerResellerIndex.findAllCustomerIdByResellerId")
				.setParameter("RESELLER_ID", resellerId));
	}

}
