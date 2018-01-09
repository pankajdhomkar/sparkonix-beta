package com.sparkonix.utils;

import java.util.Date;

import com.google.gson.JsonObject;
import com.sparkonix.ApplicationContext;
import com.sparkonix.entity.ComplaintDetail;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.IssueNumberDetail;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ComplaintDTO;

public class MailUtils {
    private static final String SEND_TO = "sendTo";
    private static final String SUBJECT = "subject";

    MailUtils() {

    }
    /*send email to super admin that customer has been added*/
    public static JsonObject getAddCompanyMail(Customer customer, User user, String email) {

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] New customer added ");

	String emailBody = "Hey, <br><br>" + "A new customer has been added by "
		+ user.getName().toLowerCase()+"." 
		+ "<br>" 
		+ "<p><b>Company Details: </b></p>"
		+ "Comapny Name: "+customer.getCompany_name()+"<br>"
		+ "Comapny PAN: "+customer.getPan()+"<br>";

	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }

    // send email to super admin manufacturer created
    public static JsonObject getAddCompanyMailManufacturer(Manufacturer manufacturer, User onBoardedBy) {
	String email = ApplicationContext.getInstance().getConfig().getSuperadminEmail();

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] New manufacturer added ");

	String emailBody = "Hey, <br><br>" + "A new manufacturer has been added by " //+ onBoardedBy.getRole().toLowerCase()+"." 
		+ "<br>" 
		+ "<p><b>Company Details: </b></p>"
		//				+ "Comapny Name: "+reseller.getCompanyName()+"<br>"
		+ "Comapny PAN: "+manufacturer.getPan()+"<br>"
		+ "Boarded By: "+onBoardedBy.getName()+"( "+onBoardedBy.getEmail()+" )<br><br>";


	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }

    // send email to super admin reseller created
    public static JsonObject getAddCompanyMailReseller(Reseller reseller, User onBoardedBy) {
	String email = ApplicationContext.getInstance().getConfig().getSuperadminEmail();

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] New reseller added ");

	String emailBody = "Hey, <br><br>" + "A new reseller has been added by " //+ onBoardedBy.getRole().toLowerCase()+"." 
		+ "<br>" 
		+ "<p><b>Company Details: </b></p>"
		//				+ "Comapny Name: "+reseller.getCompanyName()+"<br>"
		+ "Comapny PAN: "+reseller.getPan()+"<br>"
		+ "Boarded By: "+onBoardedBy.getName()+"( "+onBoardedBy.getEmail()+" )<br><br>";


	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }

    //send email to manufacturer/reseller if new issue reported
    public static JsonObject getComplaintReportedMailToManRes(String email, ComplaintDetail complaintDetail, IssueNumberDetail issueNum, PhoneOperator operator) { 

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint registered");

	String emailBody = "Hey, <br><br>" 
		+ "A new complaint has been registered with AttendMe. "
		+ "<br>" 
		+ "<p><b>Complaint Details: </b></p>"
		+ "Complaint No.: "+issueNum.getIssue_number()+"<br>"
		//				+ "Machine Model: "+complaintDetail.getMachine_model_number()+"<br>"
		//				+ "Machine Serial: "+complaintDetail.getMachine_serial_number()+"<br>"
		//				+ "Machine Installation Date: "+complaintDetail.getMahicne_installation_date()+"<br>"
		//				+ "Customer Company Name: "+complaintDetail.getCustomer_company_name()+"<br>"
		//				+ "Machine Location: "+complaintDetail.getMachine_location_name_address()+"<br>"						
		+ "Date Reported: "+complaintDetail.getDate_reported()+"<br>"
		+ "Details: "+issueNum.getDetails()+"<br>"				
		+ "ReportedBy Name: "+operator.getFld_fcm_token()+"<br>"
		+ "ReportedBy Contact: "+operator.getFld_mobile_number()+"<br>"
		+ "Status: "+complaintDetail.getStatus()+"<br><br>";				 

	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }

    /*---------------------------------------------------------
     * Send a email to customer if new complaint raised. 
	 -------------------------------------------------------- */
    public static JsonObject getComplaintReportedMail(String email, ComplaintDTO complaintDetail , PhoneOperator operator) { 

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint registered");

	String emailBody = "Hey, <br><br>" 
		+ "A new complaint has been registered with AttendMe. "
		+ "<br>" 
		+ "<p><b>Complaint Details: </b></p>"
		+ "Complaint No.: "+complaintDetail.getIssueNumberDetail().getIssue_number()+"<br>"
		+ "Machine Model: "+complaintDetail.getMachine().getModel_number()+"<br>"
		+ "Machine Serial: "+complaintDetail.getMachine().getSerial_number()+"<br>"						
		+ "Date Reported: "+complaintDetail.getComplaintDetail().getDate_reported()+"<br>"
		+ "Details: "+complaintDetail.getIssueNumberDetail().getDetails()+"<br>"				
		+ "ReportedBy Name: "+operator.getFld_name()+"<br>"
		+ "ReportedBy Contact: "+operator.getFld_mobile_number()+"<br>"
		+ "Status: "+complaintDetail.getComplaintDetail().getStatus()+"<br><br>";				 

	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }

    /*---------------------------------------------------------
     * Send a email to customer that status has been changed. 
	 -------------------------------------------------------- */
    public static JsonObject getComplaintStatusReportedMail(String email, ComplaintDTO complaintDetail , String technicianName) { 

	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint registered");

	String emailBody = "Hey, <br><br>" 
		+ "A complaint status has been changed to "+"Status: "+complaintDetail.getComplaintDetail().getStatus()+"  with AttendMe. "
		+ "<br>" 
		+ "<p><b>Complaint Details: </b></p>"
		+ "Complaint No. : "+ complaintDetail.getIssueNumberDetail().getIssue_number() +"<br>"
		+ "Machine Model : "+ complaintDetail.getMachine().getModel_number() +"<br>"
		+ "Machine Serial : "+ complaintDetail.getMachine().getSerial_number() +"<br>"
		+ "Technician name : "+ technicianName +"<br>"
		+ "Date Reported : "+complaintDetail.getComplaintDetail().getDate_reported() +"<br>"
		+ "Details: "+complaintDetail.getIssueNumberDetail().getDetails() +"<br>";
	//add technician id if the complaint has been reassigned. 

	jsonObject.addProperty("body", emailBody);

	return jsonObject;
    }
    
    public static JsonObject getComplaintStatusMail(String email, ComplaintDTO complaint, User updatedBy, Date reporetedDate, String machineLocation) {
	
	JsonObject jsonObject = new JsonObject();
	jsonObject.addProperty(SEND_TO, email);
	jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint status changed");

	String emailBody = "Hey, <br><br>" 
			+ "A status of complaint has been changed by the technician. "
			+ "<br>" 
			+ "<p><b>Complaint Details: </b></p>"
			+ "Complaint No.: "+complaint.getIssueNumberDetail().getIssue_number()+"<br>"
			+ "Machine Model: "+complaint.getMachine().getModel_number()+"<br>"
			+ "Machine Serial: "+complaint.getMachine().getSerial_number()+"<br>"
			+ "Machine Installation Date: "+complaint.getMachine().getInstallation_date()+"<br>"
			+ "Customer Company Name: "+complaint.getCustomer().getCompany_name()+"<br>"
			+ "Machine Location: "+ machineLocation +"<br>"
			+ "Date Reported: "+ reporetedDate+"<br>"
			+ "Details: "+complaint.getIssueNumberDetail().getDetails()+"<br>"
			+ "Failure Reason: "+complaint.getComplaintDetail().getFailure_reason()+"<br>"
			+ "Action Taken: "+complaint.getComplaintDetail().getAction_taken()+"<br>"				 
			+ "Technician Name: "+updatedBy.getName()+"<br>"
			+ "Technician Contact: "+updatedBy.getMobile()+"<br>"
			+ "Technician Email: "+updatedBy.getEmail()+"<br>"
			+ "Status: "+complaint.getComplaintDetail().getStatus()+"<br><br>";	

	jsonObject.addProperty("body", emailBody);

	return jsonObject;
}
}
