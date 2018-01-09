package com.sparkonix.entity.dto;

/**
 * @author Pankaj Dhomkar
 *
 */
public class UserRoleIndexDTO {
	private long user_id;
	private long user_role_id;
	
	
	public long getUser_id() {
		return user_id;
	}
	
	public void setUser_id(long userid) {
		this.user_id = userid;
	}
	
	public long getUser_role_id() {
		return user_role_id;
	}
	
	public void setUser_role_id(long user_role_id) {
		this.user_role_id = user_role_id;
	}
}
