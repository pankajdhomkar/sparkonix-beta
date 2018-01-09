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
@Table(name = "machine_documents")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.MachineDocument.findAll", query = "SELECT md FROM MachineDocument md"),
	@NamedQuery(name = "com.sparkonix.entity.MachineDocument.findByManufacturer", query = "SELECT md FROM MachineDocument md WHERE md.manufacturer_id = :MANUFACTURERID")})


public class MachineDocument implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8994872172816270881L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "manufacturer_id", nullable = false)
	private long manufacturer_id;
	
	@Column(name = "model_number")
	private String model_number;
	
	@Column(name = "document_path")
	private String document_path;
	
	@Column(name = "description")
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getManufacturer_id() {
		return manufacturer_id;
	}

	public void setManufacturer_id(long manufacturer_id) {
		this.manufacturer_id = manufacturer_id;
	}

	public String getModel_number() {
		return model_number;
	}

	public void setModel_number(String model_number) {
		this.model_number = model_number;
	}

	public String getDocument_path() {
		return document_path;
	}

	public void setDocument_path(String document_path) {
		this.document_path = document_path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
