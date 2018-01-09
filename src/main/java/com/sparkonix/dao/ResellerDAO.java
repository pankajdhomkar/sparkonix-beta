package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.Reseller;

import io.dropwizard.hibernate.AbstractDAO;

public class ResellerDAO extends AbstractDAO<Reseller> {

	public ResellerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Reseller save(Reseller reseller) throws Exception {
		return persist(reseller);
	}

	public Reseller getById(long resellerId) throws Exception {
		return get(resellerId);
	}

	public List<Reseller> findAll() {
		return list(namedQuery("com.sparkonix.entity.Reseller.findAll"));
	}

	public List<Reseller> findAllResellerByManufacturerId(long manufacturerId) {
		return list(namedQuery("com.sparkonix.entity.Reseller.findAllResellerByManufacturerId")
				.setParameter("MANUFACTURERID", manufacturerId));
	}

	// To get a Reseller Detail by Reseller id
	public Reseller findResellerDetailById(long resellerId) {
		return uniqueResult(
				namedQuery("com.sparkonix.entity.Reseller.findResellerId").setParameter("RESELLERID", resellerId));
	}
	
	public Reseller findResellerDetailByPan(String pan) {
		return uniqueResult(namedQuery("com.sparkonix.entity.Reseller.findResellerPan").setParameter("PAN", pan));
	}
	
	//To get all reseller list using who creates a reseller
		public List<Reseller> findAllByOnBoarded(long onBoardedById) {
			return list(namedQuery("com.sparkonix.entity.Reseller.findAllByOnBoarded")
					.setParameter("ON_BOARDED_BY", onBoardedById));

		}
}
