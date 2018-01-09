package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "user_role_index")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.UserRoleIndex.findAll", query = "SELECT uri FROM UserRoleIndex uri")
})

public class UserRoleIndex implements Serializable{
    
    	private static final long serialVersionUID = 3517251687176885152L;
    	
    	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
    	
	@Column(name = "user_id", nullable = false)
	private long user_id;
	
	@Column(name = "user_role_id")
	private long user_role_id;
	
	

	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getUser_role_id() {
		return user_role_id;
	}

	public void setUser_role_id(long user_role_id) {
		this.user_role_id = user_role_id;
	}
}
