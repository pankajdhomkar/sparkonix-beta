package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Reseller;
import io.dropwizard.hibernate.AbstractDAO;

public class ResellerDAO extends AbstractDAO<Reseller> {

	public ResellerDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	public Reseller save(Reseller reseller) throws Exception{
		return persist(reseller);
	}
	
	public Reseller getById(long resellerId) throws Exception{
		return get(resellerId);
	}
	
	public List<Reseller> findAll(){
		return list(namedQuery("com.sparkonix.entity.Reseller.findAll"));
	}
	
	/*
	 * public CompanyDetail findCompanyDetailByPan(String pan) { return
	 * uniqueResult(
	 * namedQuery("com.sparkonix.entity.CompanyDetail.findCompanyDetailByPan").
	 * setParameter("PAN", pan)); }
	 */

	public Reseller findResellerDetailByPan(String pan) {
		return uniqueResult(namedQuery("com.sparkonix.entity.Reseller.findResellerPan").setParameter("PAN", pan));
	}
	
	//To get a Reseller Detail by Reseller id
	public Reseller findResellerDetailById(long resellerId){
		return uniqueResult(namedQuery("com.sparkonix.entity.Reseller.findResellerId").setParameter("RESELLERID", resellerId));
	}

	//To get all reseller list using who creates a reseller
	public List<Reseller> findAllByOnBoarded(long onBoardedById) {
		return list(namedQuery("com.sparkonix.entity.Reseller.findAllByOnBoardedId")
				.setParameter("ON_BOARDED_BY", onBoardedById));

	}
	
	public List<Reseller> findAllById(long id){
		return list(namedQuery("com.sparkonix.entity.Reseller.findAllById")
				.setParameter("MANID", id));

	}

}
