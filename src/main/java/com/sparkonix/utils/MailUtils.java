package com.sparkonix.utils;

import com.google.gson.JsonObject;
import com.sparkonix.ApplicationContext;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Issue;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.User;

public class MailUtils {

	private static final String SEND_TO = "sendTo";
	private static final String SUBJECT = "subject";

	MailUtils() {

	}
	//send email to super amin
	public static JsonObject getAddCompanyMail(CompanyDetail companyDetail, User onBoardedBy) {
		String email = ApplicationContext.getInstance().getConfig().getSuperadminEmail();

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(SEND_TO, email);
		jsonObject.addProperty(SUBJECT, "[AttendMe] New " + companyDetail.getCompanyType().toLowerCase() + " added ");

		String emailBody = "Hey, <br><br>" + "A new " + companyDetail.getCompanyType().toLowerCase()
				+ " has been added by " + onBoardedBy.getRole().toLowerCase()+"." 
				+ "<br>" 
				+ "<p><b>Company Details: </b></p>"
				+ "Comapny Name: "+companyDetail.getCompanyName()+"<br>"
				+ "Comapny PAN: "+companyDetail.getPan()+"<br>"
				+ "Boarded By: "+onBoardedBy.getName()+"( "+onBoardedBy.getEmail()+" )<br><br>";
				 

		jsonObject.addProperty("body", emailBody);

		return jsonObject;
	}
	
	//send email to manufacturer/reseller if new issue reported
	public static JsonObject getComplaintReportedMail(String email, Issue complaint, PhoneDevice onBoardedBy) { 

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(SEND_TO, email);
		jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint registered");

		String emailBody = "Hey, <br><br>" 
				+ "A new complaint has been registered with AttendMe. "
				+ "<br>" 
				+ "<p><b>Complaint Details: </b></p>"
				+ "Complaint No.: "+complaint.getIssueNumber()+"<br>"
				+ "Machine Model: "+complaint.getMachineModelNumber()+"<br>"
				+ "Machine Serial: "+complaint.getMachineSerialNumber()+"<br>"
				+ "Machine Installation Date: "+complaint.getMachineInstallationDate()+"<br>"
				+ "Customer Company Name: "+complaint.getCustomerCompanyName()+"<br>"
				+ "Machine Location: "+complaint.getMachineLocationNameAddress()+"<br>"						
				+ "Date Reported: "+complaint.getDateReported()+"<br>"
				+ "Details: "+complaint.getDetails()+"<br>"				
				+ "ReportedBy Name: "+onBoardedBy.getOperatorName()+"<br>"
				+ "ReportedBy Contact: "+onBoardedBy.getPhoneNumber()+"<br>"
				+ "Status: "+complaint.getStatus()+"<br><br>";				 

		jsonObject.addProperty("body", emailBody);

		return jsonObject;
	}
	
	//send email to manufacturer/reseller
	public static JsonObject getComplaintStatusMail(String email, Issue complaint, User updatedBy) {
				
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(SEND_TO, email);
		jsonObject.addProperty(SUBJECT, "[AttendMe] Complaint status changed");

		String emailBody = "Hey, <br><br>" 
				+ "A status of complaint has been changed by the technician. "
				+ "<br>" 
				+ "<p><b>Complaint Details: </b></p>"
				+ "Complaint No.: "+complaint.getIssueNumber()+"<br>"
				+ "Machine Model: "+complaint.getMachineModelNumber()+"<br>"
				+ "Machine Serial: "+complaint.getMachineSerialNumber()+"<br>"
				+ "Machine Installation Date: "+complaint.getMachineInstallationDate()+"<br>"
				+ "Customer Company Name: "+complaint.getCustomerCompanyName()+"<br>"
				+ "Machine Location: "+complaint.getMachineLocationNameAddress()+"<br>"
				+ "Date Reported: "+complaint.getDateReported()+"<br>"
				+ "Details: "+complaint.getDetails()+"<br>"
				+ "Failure Reason: "+complaint.getFailureReason()+"<br>"
				+ "Action Taken: "+complaint.getActionTaken()+"<br>"				 
				+ "Technician Name: "+updatedBy.getName()+"<br>"
				+ "Technician Contact: "+updatedBy.getMobile()+"<br>"
				+ "Technician Email: "+updatedBy.getEmail()+"<br>"
				+ "Status: "+complaint.getStatus()+"<br><br>";	

		jsonObject.addProperty("body", emailBody);

		return jsonObject;
	}
}
