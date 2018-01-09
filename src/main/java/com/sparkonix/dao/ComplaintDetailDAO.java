package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.sparkonix.entity.ComplaintDetail;

import io.dropwizard.hibernate.AbstractDAO;

public class ComplaintDetailDAO extends AbstractDAO<ComplaintDetail>{

    public ComplaintDetailDAO(SessionFactory sessionFactory) {
	super(sessionFactory);
    }

    /*
     * Either save or update the given instance, depending upon resolution
     * of the unsaved-value checks. This operation cascades to associated
     * instances if the association is mapped with cascade="save-update".
     */
    public ComplaintDetail save(ComplaintDetail complaintDetail) throws Exception{
	return persist(complaintDetail);
    }

    public ComplaintDetail getById(long complaintDetailId) throws Exception{
	return get(complaintDetailId);
    }

    public List<ComplaintDetail> findAll(){
	return list(namedQuery("com.sparkonix.entity.ComplaintDetail.findAll"));
    }

    //	Get the complaint details by issuenumber id and get the current status
    public ComplaintDetail findComplaintByCurrentStatus(long issueNumberId){
	return uniqueResult(namedQuery("com.sparkonix.entity.ComplaintDetail.findComplaintByCurrentStatus").setParameter("ISSUEID", issueNumberId));
    }

    public ComplaintDetail getComplaintofTechnician(long technicianId){
	return null;
    }

    //	Get the complaint by its issue number id 
    @SuppressWarnings("unchecked")
    public List<ComplaintDetail> getAllComplaintDetailByIssueId(long issueNumberID){
	Criteria criteria = currentSession().createCriteria(ComplaintDetail.class, "complaintDetail");
	criteria.add(Restrictions.eq("issueDetail_id", issueNumberID));
	return criteria.list();
    }

    public ComplaintDetail getDateClosedByIssueId(long issueNumberId, long technicianId, String status){
	return uniqueResult(namedQuery("com.sparkonix.entity.ComplaintDetail.findClosedDateByIssueNumIdTechId").setParameter("ISSUEID", issueNumberId).setParameter("TECHID", technicianId).setParameter("STATUS", status));
    }
    //	//It will get the complaint by manufacturer and by support assistance by reseller.
    //	//Here it will show the latest state of complaint if the history want to see then click on 
    //	//issue no then pop up dialog show the history of complaint
    //	public List<ComplaintDetail> findComplaintID(long manufacturerId, long support) {
    //		return list(namedQuery("com.sparkonix.entity.ComplaintDetail.findAllBySupportAssitanaceAndManId").setParameter("SUPPORT_ASSISTANCE", support).setParameter("MANUFACTURER_ID", manufacturerId));
    //	}
    //	
    //	public List<ComplaintDetail> findComplaintByCustomerID(long customerId, long support){
    //		return list(namedQuery("com.sparkonix.entity.ComplaintDetail.findComplaintByCustomerID").setParameter("CUSTOMER_ID", customerId).setParameter("SUPPORT_ASSISTANCE", support));
    //	}
    //	
    //	/*  This public method for the get the complaint through a database using a criteria
    //	 * 
    //	 */
    //	public List<ComplaintDetail> findAllBySearch(long supportAssistance,
    //			ComplaintSearchFilterPayloadDTO  complaintSearchFilterPayloadDTO) {
    //
    //		Criteria criteria = currentSession().createCriteria(ComplaintDetail.class, "complaintDetail");
    //		System.out.println("(*)000000000");
    //		System.out.println("ID MAN"+complaintSearchFilterPayloadDTO.getManufacturerID());
    //		System.out.println("ASSISTANCE--"+supportAssistance);
    //		System.out.println("(*)000000000");
    //		//Using a Manufacturer Id
    //		if (complaintSearchFilterPayloadDTO.getManufacturerID() != 0) {
    //			criteria.add(Restrictions.eq("manufacturer_id", complaintSearchFilterPayloadDTO.getManufacturerID()));
    //		}
    //		
    //	
    //		//for superadmin
    //		if (supportAssistance != 0) {
    //			criteria.add(Restrictions.eq("machineSupportAssistance", supportAssistance));
    //		}
    //
    //		return criteria.list();
    //
    //	}
}
