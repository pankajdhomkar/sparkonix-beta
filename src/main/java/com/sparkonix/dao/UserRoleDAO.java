package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.UserRole;

import io.dropwizard.hibernate.AbstractDAO;

public class UserRoleDAO extends AbstractDAO<UserRole>{

	public UserRoleDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public UserRole save(UserRole userRole) throws Exception{
		return persist(userRole);
	}
	
	public UserRole getById(long userRoleId) throws Exception{
		return get(userRoleId);
	}
	
	public List<UserRole> findAll(){
		return list(namedQuery("com.sparkonix.entity.UserRole.findAll"));
	}

}
