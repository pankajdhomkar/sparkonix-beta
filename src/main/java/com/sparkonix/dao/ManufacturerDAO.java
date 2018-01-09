package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.Manufacturer;
import io.dropwizard.hibernate.AbstractDAO;

public class ManufacturerDAO extends AbstractDAO<Manufacturer> {

	public ManufacturerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Manufacturer save(Manufacturer manufacturer) throws Exception{
	    if(manufacturer != null){
		 System.out.println("Comes here -->"+manufacturer.toString());
	    }else{
		System.out.println("Comes here -->null save man");
	    }
	   
		return persist(manufacturer);		
	}
	
	public Manufacturer getById(long manufacturerId) throws Exception{
		return get(manufacturerId);
	}
	
	public List<Manufacturer> findAll(){
		return list(namedQuery("com.sparkonix.entity.Manufacturer.findAll"));
	}
	
	public List<Manufacturer> findAllByOnBoardedId(long onBoardedById, String companyType) {
		// for salesteam/manRes we need to get all the customers onboarded by
		// him AND
		// customers not onboarded by him but whose new factory locations were
		// onboarded by him
			return list(namedQuery("com.sparkonix.entity.CompanyDetail.findByLocationOnBoardedId")
					.setParameter("ON_BOARDED_BY", onBoardedById)
					.setParameter("COMPANY_TYPE", companyType.toUpperCase()));
	}

	
	public Manufacturer findmanufacturerDetailByPan(String pan) {
		return uniqueResult(namedQuery("com.sparkonix.entity.Manufacturer.findmanufacturerDetailByPan")
				.setParameter("PAN", pan));
	}
}
