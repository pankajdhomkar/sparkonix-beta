package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sparkonix.entity.PhoneOperator;


import io.dropwizard.hibernate.AbstractDAO;

public class PhoneOperatorDAO extends AbstractDAO<PhoneOperator>{

	public PhoneOperatorDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	/*
	 * Either save or update the given instance, depending upon resolution
	 * of the unsaved-value checks. This operation cascades to associated
	 * instances if the association is mapped with cascade="save-update".
	 */
	public PhoneOperator save(PhoneOperator phoneOperator) throws Exception {
		return persist(phoneOperator);
	}
	
	public PhoneOperator getById(long phoneOperatorId) throws Exception{
		return get(phoneOperatorId);
	}
	
	public List<PhoneOperator> findAll(){
		return list(namedQuery("com.sparkonix.entity.ComplaintOperator.findAll"));
	}
	
	//This will return unique information of Mobile operator which we pass the phone number.
	public PhoneOperator getOperatorByPhoneNumber(String phoneNumber){
		return uniqueResult(namedQuery("com.sparkonix.entity.PhoneOperator.getOperatorByPhoneNumber")
				.setParameter("PHONE_NUMBER", phoneNumber));
	}
	
	public List<String> getAllFcmTokens() {
		Criteria criteria = currentSession().createCriteria(PhoneOperator.class, "PhoneOperator");
		criteria.add(Restrictions.isNotNull("fld_fcm_token"));
		criteria.setProjection(Projections.projectionList().add(Projections.property("fld_fcm_token"), "fld_fcm_token"));
		//criteria.setResultTransformer(Transformers.aliasToBean(PhoneDevice.class));
		return criteria.list();
	}

}
