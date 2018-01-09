package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;


import com.sparkonix.entity.IssueTechnician;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * @author Pankaj Dhomkar
 *
 */
public class IssueTechnicianDAO extends AbstractDAO<IssueTechnician>{

	/**
	 * @param sessionFactory
	 */
	public IssueTechnicianDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * Either save or update the given instance, depending upon resolution of
	 * the unsaved-value checks. This operation cascades to associated instances
	 * if the association is mapped with cascade="save-update".
	 */
	public IssueTechnician save(IssueTechnician issueTechnician) {
		return persist(issueTechnician);
	}

	public IssueTechnician getById(long issueNo) {
		return get(issueNo);
	}

	public List<IssueTechnician> findAll() {
		return list(namedQuery("com.sparkonix.entity.IssueTechnician.findAll"));
	}
	
	public List<IssueTechnician> getIssueID(long technicianId) {
		return list(namedQuery("com.sparkonix.entity.IssueTechnician.findByAssingedTechId").setParameter("TECHNICIANID", technicianId));
	}

}
