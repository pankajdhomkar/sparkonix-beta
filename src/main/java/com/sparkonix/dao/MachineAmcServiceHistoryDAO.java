package com.sparkonix.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.User;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineAmcServiceHistoryDAO extends AbstractDAO<MachineAmcServiceHistory>{

	public MachineAmcServiceHistoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public MachineAmcServiceHistory save(MachineAmcServiceHistory machineAmcServiceHistory) throws Exception{
		return persist(machineAmcServiceHistory);
	}
	
	public MachineAmcServiceHistory getById(long machineAmcServiceHistoryId) throws Exception{
		return get(machineAmcServiceHistoryId);
	}
	
	public List<MachineAmcServiceHistory> findAll(){
		return list(namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findAll"));
	}
	
	public List<MachineAmcServiceHistory> getServicesForTechnician(User userObj){
	    List<MachineAmcServiceHistory> machineAmcServiceHistories = list(
			namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician").setParameter("TECHID",
					userObj));
	    for (MachineAmcServiceHistory machineAmcServiceHistory : machineAmcServiceHistories) {
		initialize(machineAmcServiceHistory.getMachine());
		initialize(machineAmcServiceHistory.getAssignedTo());
		}
	    return machineAmcServiceHistories;
	}
	
	public List<MachineAmcServiceHistory> getServicesByCategory(String category, long parameter) {
		List<MachineAmcServiceHistory> machineAmcServiceHistories;
		if ("manufacturer".equalsIgnoreCase(category)) {
		    machineAmcServiceHistories = list(
			    namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByManufacturerID")
			    .setParameter("MANID", parameter));
		} else if("reseller".equalsIgnoreCase(category)){
		    machineAmcServiceHistories = list(
			    namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByResellerID")
			    .setParameter("RESELLERID", parameter));
		} else if ("technician".equalsIgnoreCase(category)) {
		    machineAmcServiceHistories = list(
			    namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician").setParameter("TECHID",
				    parameter));
		} else if ("machine".equalsIgnoreCase(category)) {
		    /*
		     * machineAmcServiceHistories = list( namedQuery(
		     * "com.sparkonix.entity.MachineAmcServiceHistory.findByMachineID")
		     * .setParameter("MACHINEID", parameter));
		     */
		    machineAmcServiceHistories = list(
			    namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findAllByMachineId")
			    .setParameter("MACHINE_ID", parameter));
		} else {
		    return Collections.emptyList();
		}

		for (MachineAmcServiceHistory machineAmcServiceHistory : machineAmcServiceHistories) {
			initialize(machineAmcServiceHistory.getMachine());
			initialize(machineAmcServiceHistory.getAssignedTo());
		}

		return machineAmcServiceHistories;

	}
	

}
