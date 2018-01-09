package com.sparkonix.auth;

import com.sparkonix.entity.User;

import io.dropwizard.auth.Authorizer;

/**
 * @author Pankaj Dhomkar
 *
 */
public class WebAuthorizer implements Authorizer<User> {

    /* (non-Javadoc)
     * @see io.dropwizard.auth.Authorizer#authorize(java.security.Principal, java.lang.String)
     */
    @Override
    public boolean authorize(User principal, String role) {
	// TODO Auto-generated method stub
	if (principal.isUserInRole(role))
		return true;
	
	return false;
    }

}
