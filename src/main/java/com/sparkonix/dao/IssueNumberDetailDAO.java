package com.sparkonix.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.IssueNumberDetail;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * @author Pankaj Dhomkar
 *
 */
public class IssueNumberDetailDAO extends AbstractDAO<IssueNumberDetail> {

	/**
	 * @param sessionFactory
	 */
	public IssueNumberDetailDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Either save or update the given instance, depending upon resolution of
	 * the unsaved-value checks. This operation cascades to associated instances
	 * if the association is mapped with cascade="save-update".
	 */
	public IssueNumberDetail save(IssueNumberDetail issueNumberDetail) {
		return persist(issueNumberDetail);
	}

	public IssueNumberDetail getById(long issueNo) {
		return get(issueNo);
	}

	public List<IssueNumberDetail> findAll() {
		return list(namedQuery("com.sparkonix.entity.IssueNumberDetail.findAll"));
	}

	public List<IssueNumberDetail> findAllBySupportAssitanaceAndCompanyId(long supportAssistance,
			long manResCompanyId) {
		if (supportAssistance == 1) {// Supportassistance by manufacturer
			return list(namedQuery("com.sparkonix.entity.IssueNumberDetail.findAllBySupportAssitanaceAndManId")
					.setParameter("SUPPORT_ASSISTANCE", supportAssistance)
					.setParameter("MANUFACTURER_ID", manResCompanyId));
		} else if (supportAssistance == 2) { // Supportassistance by reseller
			return list(namedQuery("com.sparkonix.entity.IssueNumberDetail.findAllBySupportAssitanaceAndResId")
					.setParameter("SUPPORT_ASSISTANCE", supportAssistance).setParameter("RESELLR_ID", manResCompanyId));
		} else {
			return Collections.emptyList();
		}

	}

}
