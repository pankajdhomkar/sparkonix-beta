package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.UnregisterOperator;

import io.dropwizard.hibernate.AbstractDAO;

public class UnregisterOperatorDAO extends AbstractDAO<UnregisterOperator> {

	public UnregisterOperatorDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
//	
	public UnregisterOperator save(UnregisterOperator unregisterOperator){
		return persist(unregisterOperator);
	}
	
	public void deleteByMobileNumber(UnregisterOperator unregisterOperator){
		currentSession().delete(unregisterOperator);
	}
	
	public List<UnregisterOperator> findAll() {
		return list(namedQuery("com.sparkonix.entity.UnregisterOperator.findAll"));
	}

	public UnregisterOperator getOperatorByPhoneNumber1(String phoneNumber) {
		
		return uniqueResult(namedQuery("com.sparkonix.entity.UnregisterOperator.getOperatorByPhoneNumber1")
				.setParameter("PHONE_NUMBER", phoneNumber));
	}


}
