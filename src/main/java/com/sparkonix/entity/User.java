package com.sparkonix.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sparkonix.entity.dialect.StringJsonUserType;

@Entity
@Table(name = "user", schema = "public")
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "com.sparkonix.entity.User.findUserByUsernameAndPassword", query = "SELECT u FROM User u WHERE u.email = :USERNAME and u.password = :PASSWORD"),
		@NamedQuery(name = "com.sparkonix.entity.User.checkSuperAdminByUsername", query = "SELECT u FROM User u WHERE u.email = :USERNAME and u.user_role_id= :ROLEID"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByManufacturerIdRoleID", query = "SELECT u FROM User u WHERE u.manufacturer_id = :MANUFACTURER_ID and u.user_role_id= :ROLEID"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByResellerIdRoleID", query = "SELECT u FROM User u WHERE u.reseller_id = :RESELLER_ID and u.user_role_id= :ROLEID"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :EMAIL"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByRoleID", query = "SELECT u FROM User u WHERE u.user_role_id = :USERROLEID")
})

public class User implements Serializable, Principal {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "alt_email")
	private String alt_email;

	@Column(name = "password")
	private String password;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "user_role_id")
	private int user_role_id;
	/*
	 * 1 = super admin, 2 = sales team, 3 = manufacturer, 4 = reseller, 5 = customer,
	 * 6 = technician
	 */

	@Column(name = "notification_type")
	private String notification_type;

	@Column(name = "manufacturer_id")
	private long manufacturer_id;

	@Column(name = "reseller_id")
	private long reseller_id;
	
	private transient String token; // transient variable not serialized

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assignedTo")
	private List<MachineAmcServiceHistory> machineAmcServiceHistories;

	public static enum NOTIFICATION_TYPE {
		EMAIL, SMS, BOTH
	}

	public User() {
		//
	}

	public User(String username, String password, int role) {
		this.email = username;
		this.password = password;
		this.user_role_id = role;
	}

	public boolean isUserInRole(String role) {
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlt_email() {
		return alt_email;
	}

	public void setAlt_email(String alt_email) {
		this.alt_email = alt_email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getUser_role_id() {
		return user_role_id;
	}

	public void setUser_role_id(int user_role_id) {
		this.user_role_id = user_role_id;
	}

	public String getNotification_type() {
		return notification_type;
	}

	public void setNotification_type(String notification_type) {
		this.notification_type = notification_type;
	}

	public long getManufacturer_id() {
		return manufacturer_id;
	}

	public void setManufacturer_id(long manufacturer_id) {
		this.manufacturer_id = manufacturer_id;
	}

	public long getReseller_id() {
		return reseller_id;
	}

	public void setReseller_id(long reseller_id) {
		this.reseller_id = reseller_id;
	}

	public List<MachineAmcServiceHistory> getMachineAmcServiceHistories() {
		return machineAmcServiceHistories;
	}

	public void setMachineAmcServiceHistories(List<MachineAmcServiceHistory> machineAmcServiceHistories) {
		this.machineAmcServiceHistories = machineAmcServiceHistories;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
