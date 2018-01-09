package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.User;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * @author Pankaj Dhomkar
 *
 */
public class UserDAO extends AbstractDAO<User> {

    /**
     * @param sessionFactory
     */
    public UserDAO(SessionFactory sessionFactory) {
	super(sessionFactory);
	// TODO Auto-generated constructor stub
    }

    public User save(User user) throws Exception {
	return persist(user);
    }

    public User getById(long userId) throws Exception {
	return get(userId);
    }

    public List<User> findAll() {
	return list(namedQuery("com.sparkonix.entity.User.findAll"));
    }

    @SuppressWarnings("unchecked")
    public User findUserByUsernameAndPassword(String username, String password) {
	return uniqueResult(namedQuery("com.sparkonix.entity.User.findUserByUsernameAndPassword")
		.setParameter("USERNAME", username.toLowerCase()).setParameter("PASSWORD", password));
    }

    @SuppressWarnings("unchecked")
    public User checkSuperAdminByUsername(String superadminUsername) {
	return uniqueResult(namedQuery("com.sparkonix.entity.User.checkSuperAdminByUsername")
		.setParameter("USERNAME", superadminUsername).setParameter("ROLEID", 1));
    }

    public User findByEmail(String email) {
	return uniqueResult(namedQuery("com.sparkonix.entity.User.findByEmail").setParameter("EMAIL", email));
    }

    /*
     * public String userRole(int userRoleId) {
     * 
     * @SuppressWarnings("deprecation") Criteria criteria =
     * currentSession().createCriteria(UserRole.class, "UserRole");
     * criteria.add(Restrictions.eq("id", userRoleId)); String role =
     * criteria.getAlias(); return role; }
     */

    public Object findByRoleID(long roleID) {
	return list(namedQuery("com.sparkonix.entity.User.findByRoleID").setParameter("USERROLEID", roleID));
    }

    // This method will return the list of users according to its a role id's
    // and manufacturers id's
    public List<User> findAllByRoleManufacturerId(long userRoleId, long manufacturerId) {
	return list(namedQuery("com.sparkonix.entity.User.findByManufacturerIdRoleID")
		.setParameter("MANUFACTURER_ID", manufacturerId).setParameter("ROLEID", userRoleId));
    }

    // This method will return the list of users according to its a role id's
    // and reseller id's
    public List<User> findAllByRoleResellerId(long userRoleId, long resellerId) {
	return list(namedQuery("com.sparkonix.entity.User.findByResellerIdRoleID")
		.setParameter("RESELLER_ID", resellerId).setParameter("ROLEID", userRoleId));
    }

    // Method will return the user info by its a Manufacturer id and its user
    // role id
    public User findByManufacturerIdRoleID(long userRoleId, long manufacturerId) {
	return uniqueResult(namedQuery("com.sparkonix.entity.User.findByManufacturerIdRoleID")
		.setParameter("MANUFACTURER_ID", manufacturerId).setParameter("ROLEID", userRoleId));
    }

    // Method will return the user info by its a Manufacturer id and its user
    // role id
    public User findByResellerIdRoleID(long userRoleId, long resellerId) {
	return uniqueResult(namedQuery("com.sparkonix.entity.User.findByResellerIdRoleID")
		.setParameter("RESELLER_ID", resellerId).setParameter("ROLEID", userRoleId));
    }

}
