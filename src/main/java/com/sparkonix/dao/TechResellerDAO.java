package com.sparkonix.dao;



import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.TechReseller;

import io.dropwizard.hibernate.AbstractDAO;

public class TechResellerDAO extends AbstractDAO<TechReseller> {

	public TechResellerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public TechReseller save(TechReseller techReseller) throws Exception{
		return persist(techReseller);
	}
	
	public TechReseller getById(long techResellerId) throws Exception{
		return get(techResellerId);
	}
	
	public List<TechReseller> findAll(){
		return list(namedQuery("com.sparkonix.entity.TechReseller.findAll"));
	}

}
