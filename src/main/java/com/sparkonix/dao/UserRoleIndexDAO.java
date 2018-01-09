package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.UserRoleIndex;

import io.dropwizard.hibernate.AbstractDAO;

public class UserRoleIndexDAO extends AbstractDAO<UserRoleIndex>{

	public UserRoleIndexDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public UserRoleIndex save(UserRoleIndex userRoleIndex) throws Exception{
		return persist(userRoleIndex);
	}
	
	public UserRoleIndex getById(long userRoleIndexId) throws Exception{
		return get(userRoleIndexId);
	}
	
	public List<UserRoleIndex> findAll(){
		return list(namedQuery("com.sparkonix.entity.UserRoleIndex.findAll"));
	}

}
