package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.TechManufacturer;

import io.dropwizard.hibernate.AbstractDAO;

public class TechManufacturerDAO extends AbstractDAO<TechManufacturer>{

	public TechManufacturerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public TechManufacturer save(TechManufacturer techManufacturer) throws Exception{
		return persist(techManufacturer);
	}
	
	public TechManufacturer getById(long techManufacturerId) throws Exception{
		return get(techManufacturerId);
	}
	
	public List<TechManufacturer> findAll(){
		return list(namedQuery("com.sparkonix.entity.TechManufacturer.findAll"));
	}

}
