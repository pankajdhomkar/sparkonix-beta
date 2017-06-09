package com.sparkonix.dao;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.PasswordResetToken;

import io.dropwizard.hibernate.AbstractDAO;

public class PasswordResetTokenDAO extends AbstractDAO<PasswordResetToken>{

	public PasswordResetTokenDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	public PasswordResetToken save(PasswordResetToken token) throws Exception {
		return persist(token);
	}

	public PasswordResetToken findToken(String token) {
		return uniqueResult(namedQuery("com.sparkonix.entity.PasswordResetToken.findToken").setParameter("TOKEN", token));
	}
	
	public PasswordResetToken findByTokens(String token, String secondToken) {
		return uniqueResult(namedQuery("com.sparkonix.entity.PasswordResetToken.findByTokens").setParameter("TOKEN", token).setParameter("SECOND_TOKEN", secondToken));
	}

}
