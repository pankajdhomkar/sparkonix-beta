package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.MachineDocument;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineDocumentDAO extends AbstractDAO<MachineDocument>{

	public MachineDocumentDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public MachineDocument save(MachineDocument machineDocument) throws Exception {
		return persist(machineDocument);
	}
	
	public MachineDocument getById(long machineDocumentId) throws Exception{
		return get(machineDocumentId);
	}

	public List<MachineDocument> findAll(){
		return list(namedQuery("com.sparkonix.entity.MachineDocument.findAll"));
	}
	
	//Find by manufacturer id
	public Object findByManufacturer(long manufacturerId) {
		return list(namedQuery("com.sparkonix.entity.MachineDocument.findByManufacturer").setParameter("MANUFACTURERID",
				manufacturerId));
	}
}
