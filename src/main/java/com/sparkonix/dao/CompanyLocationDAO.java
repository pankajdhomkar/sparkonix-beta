package com.sparkonix.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;

import io.dropwizard.hibernate.AbstractDAO;

/*
 * CompanyLocation DAO data abstract object class to use communicate with database tables
 * This is getting information of machine location of customers
 */
public class CompanyLocationDAO extends AbstractDAO<CompanyLocation> {

	// Parameterized Constructor
	public CompanyLocationDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	// This method store all data into a database table of company Location
	public CompanyLocation save(CompanyLocation companyLocation) throws Exception {
		/*
		 * Either save or update the given instance, depending upon resolution
		 * of the unsaved-value checks. This operation cascades to associated
		 * instances if the association is mapped with cascade="save-update".
		 */
		return persist(companyLocation);
	}

	public CompanyLocation getById(long companyLocationId) throws Exception {
		return get(companyLocationId);
	}

	public List<CompanyLocation> findAll() {
		return list(namedQuery("com.sparkonix.entity.CompanyLocation.findAll"));
	}

	//It will return a count of location where machine is placed using customer id. 
	public long getCountLocationByCustomerId(long customerId){
		if(customerId != 0){
			Query query;
			query = currentSession().createQuery(
					"select count(*) from CompanyLocation cl where cl.companyDetailsId= :COMPANY_DETAIL_ID");
			query.setParameter("COMPANY_DETAIL_ID", customerId);
			return (Long) query.uniqueResult();
		}else{
			return 0;
		}
	}
	
	public List<CompanyLocation> findAllByCompanyId(long customerId) throws Exception {
		@SuppressWarnings("unchecked")
		List<CompanyLocation> list = namedQuery("com.sparkonix.entity.CompanyLocation.findAllByCompanyId")
				.setParameter("CUSTOMER_ID", customerId).list();

		List<CompanyLocation> listCompanyLocations = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			CompanyLocation companyLocation = new CompanyLocation();
			String address = list.get(i).getAddress();
			companyLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
			companyLocation.setId(list.get(i).getId());
			companyLocation.setContactPerson(list.get(i).getContactPerson());
			companyLocation.setContactMobile(list.get(i).getContactMobile());

			listCompanyLocations.add(companyLocation);
		}
		return listCompanyLocations;
	}
	
}
