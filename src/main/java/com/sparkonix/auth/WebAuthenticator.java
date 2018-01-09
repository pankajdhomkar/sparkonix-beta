package com.sparkonix.auth;

import java.util.Optional;
import org.hibernate.SessionFactory;

import com.sparkonix.entity.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class WebAuthenticator implements Authenticator<BasicCredentials, User> {
	private SessionFactory factory = null;
	
	public WebAuthenticator(SessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
		try {
			String token = credentials.getUsername();
			User userObj = JwtToken.decryptPayload(token);
			System.out.println("User Object 1 --"+ token);
			System.out.println("User Object --"+userObj.getUser_role_id()+"--"+userObj.getToken());
			Optional<User> okk = Optional.of(userObj); 
			System.out.println("OPTIONAL-->"+okk);
			return okk;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
}
