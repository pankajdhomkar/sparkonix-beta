package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.sparkonix.entity.Machine;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineDAO extends AbstractDAO<Machine> {

	public MachineDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Machine save(Machine machine) throws Exception {
		return persist(machine);
	}

	public Machine getById(long machineId) throws Exception {
		return get(machineId);
	}

	public List<Machine> findAll() {
		return list(namedQuery("com.sparkonix.entity.Machine.findAll"));
	}

	public long getMachineCountByCustomerId(long customerId) {
		Query query;
		if (customerId != 0) {
			query = currentSession().createQuery("select count(*) from Machine m where m.customerId= :CUSTOMER_ID");
			query.setParameter("CUSTOMER_ID", customerId);
			return (Long) query.uniqueResult();
		} else {
			return 0;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Machine> getMachinesListForCompany(long companyId, long companyType){
		Criteria criteria = currentSession().createCriteria(Machine.class);
		if(companyType == 3){
			criteria.add(Restrictions.eq("manufacturer_id", companyType));
		}else{
			criteria.add(Restrictions.eq("reseller_id", companyType));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));
		return criteria.list();
	}

	// List of mahine model using a manufacturer id
	@SuppressWarnings("unchecked")
	public List<String> getAllMachineModelNumbersByManufacturerId(long manufacturerId) {

		Criteria criteria = currentSession().createCriteria(Machine.class);
		criteria.add(Restrictions.eq("manufacturerId", manufacturerId));
		criteria.setProjection(Projections
				.distinct(Projections.projectionList().add(Projections.property("modelNumber"), "modelNumber")));
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllMachineModelNumbers() {
		
		Criteria criteria = currentSession().createCriteria(Machine.class);		
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("modelNumber"), "modelNumber")));
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));
		
		return criteria.list();
	}
	
	public List<Machine> findAllByCustomerId(long customerId){
		return list(namedQuery("com.sparkonix.entity.Machine.findAllByCustomerId").setParameter("CUSTOMER_ID", customerId));
	}

}
