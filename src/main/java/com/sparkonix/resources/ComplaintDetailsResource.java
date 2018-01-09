package com.sparkonix.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.ComplaintDetailDAO;
import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.IssueNumberDetailDAO;
import com.sparkonix.dao.IssueTechnicianDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.ComplaintDetail;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.IssueNumberDetail;
import com.sparkonix.entity.IssueTechnician;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ComplaintDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.FcmMessage;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendFcmPushNotification;
import com.sparkonix.utils.SendMail;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author Pankaj Dhomkar
 *
 */
@Path("/complaintdetails")
@Produces(MediaType.APPLICATION_JSON)
public class ComplaintDetailsResource {
    private final IssueNumberDetailDAO issueNumberDetailDAO;
    private final PhoneOperatorDAO phoneOperatorDAO;
    private final MachineDAO machineDAO;
    private final ComplaintDetailDAO complaintDetailDAO;
    private final ManufacturerDAO manufacturerDAO;
    private final ResellerDAO resellerDAO;
    private final CustomerDAO customerDAO;
    private final IssueTechnicianDAO issueTechnicianDAO;
    private final UserDAO userDAO;
    private final CompanyLocationDAO companyLocationDAO;

    private final Logger log = Logger.getLogger(ComplaintDetailsResource.class.getName());

    public ComplaintDetailsResource(IssueNumberDetailDAO issueNumberDetailDAO, PhoneOperatorDAO phoneOperatorDAO,
	    MachineDAO machineDAO, ComplaintDetailDAO complaintDetailDAO, ManufacturerDAO manufacturerDAO,
	    ResellerDAO resellerDAO, CustomerDAO customerDAO, UserDAO userDAO, IssueTechnicianDAO issueTechnicianDAO,
	    CompanyLocationDAO companyLocationDAO) {
	this.issueNumberDetailDAO = issueNumberDetailDAO;
	this.phoneOperatorDAO = phoneOperatorDAO;
	this.machineDAO = machineDAO;
	this.complaintDetailDAO = complaintDetailDAO;
	this.manufacturerDAO = manufacturerDAO;
	this.resellerDAO = resellerDAO;
	this.customerDAO = customerDAO;
	this.userDAO = userDAO;
	this.issueTechnicianDAO = issueTechnicianDAO;
	this.companyLocationDAO = companyLocationDAO;
    }

    /**
     * used to add new issue/complaint. also it send email for web admin & fcm
     * notification to operator
     * 
     */
    @POST
    @UnitOfWork
    public Response createIssue(@Auth User authUser, IssueNumberDetail issue) throws Exception {

	if (authUser == null) {
	    log.severe("User authorization failed");
	    return Response.status(Status.UNAUTHORIZED).build();
	}
	if (issue.getMachine_id() == 0) {
	    log.severe("Machine ID is not valid");
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine ID is not valid"))
		    .build();
	}

	System.out.println(authUser.getEmail());
	PhoneOperator phoneDevice = phoneOperatorDAO.getOperatorByPhoneNumber(authUser.getEmail());
	if (phoneDevice == null) {
	    log.severe("This is not authorized mobile number.");
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("This is not authorized mobile number")).build();
	}

	Machine machineObj = machineDAO.getById(issue.getMachine_id());
	// get location details by location_id in machine

	CompanyLocation machineLocation = companyLocationDAO.getById(machineObj.getLocation_id());
	// convert string to json object
	String address = machineLocation.getAddress();
	machineLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
	// machineLocationNameAddress
//	String machineLocationNameAddress = machineLocation.getCompanyLocationAddress().getLocationName() + ", "
//		+ machineLocation.getCompanyLocationAddress().getLocationAddress();

	// get customerCompanyName
	// CompanyDetail customerCompanyDetail =
	// companyDetailDAO.getById(machineObj.getCustomerId());
//	Customer customerCompanyDetail = customerDAO.getById(machineObj.getCustomer_id());

	ComplaintDetail complaintDetail = new ComplaintDetail(); // This table
								 // values
								 // always
								 // create
								 // new entry
	IssueNumberDetail newIssue = new IssueNumberDetail();

	newIssue.setIssue_number("IN-" + System.currentTimeMillis() / 1000L);
	newIssue.setReporting_device(phoneDevice.getId());
	complaintDetail.setStatus(ComplaintDetail.COMPLAINT_STATUS.OPEN.toString());
	complaintDetail.setDate_reported(new Date());
	newIssue.setMachine_id(issue.getMachine_id());
	newIssue.setDetails(issue.getDetails());

	// set redundant field for faster lookup
	// newIssue.setMachineModelNumber(machineObj.getMac());
	// newIssue.setMachineSerialNumber(machineObj.getSerialNumber());
	// newIssue.setMachineInstallationDate(machineObj.getInstallationDate());
	//
	newIssue.setPhonedevice_operator_name(phoneDevice.getFld_name());
	// newIssue.setMachineLocationId(machineObj.getLocationId());
	// newIssue.setMachineLocationNameAddress(machineLocationNameAddress);
	newIssue.setCustomer_id(machineObj.getCustomer_id());
	// newIssue.setCustomerCompanyName(customerCompanyDetail.getCompanyName());
	newIssue.setManufacturer_id(machineObj.getManufacturer_id());
	newIssue.setReseller_id(machineObj.getReseller_id());
	newIssue.setMachine_support_assistance(machineObj.getSupport_assistance());

	ComplaintDetail dbComplaintDetail = complaintDetailDAO.save(complaintDetail);
	IssueNumberDetail dbIssue = issueNumberDetailDAO.save(newIssue);

	if (dbIssue != null && dbComplaintDetail != null) {

	    /*
	     * DTO for the complaint all information to use send a mail and
	     * further use
	     */
	    ComplaintDTO complaintFullInfo = new ComplaintDTO();
	    // Set the complaint details which is changes when status changed
	    // (multiple entry)
	    complaintFullInfo.setComplaintDetail(complaintDetail);
	    // Set the Issue number which is unique entry for all status of
	    // respective issue number
	    complaintFullInfo.setIssueNumberDetail(newIssue);

	    // send push notification to operator
	    FcmMessage fcmMessage = new FcmMessage();
	    String details = "Complaint no.: " + dbIssue.getIssue_number() + "\n" + "Details: " + dbIssue.getDetails()
		    + "\n" + "Status: " + dbComplaintDetail.getStatus() + "\n" + "Reported By: "
		    + newIssue.getPhonedevice_operator_name() + "\n" + "Reported Date: "
		    + dbComplaintDetail.getDate_reported();

	    JsonObject data = new JsonObject();
	    data.addProperty("title", "Complaint registered");
	    data.addProperty("message", "Your complaint has been registered with AttendMe");
	    data.addProperty("description", details);

	    fcmMessage.setData(data);
	    fcmMessage.setTo(phoneDevice.getFld_fcm_token());

	    Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
	    t1.start();

	    // send email to web admin if subscribed
	    Machine machine = machineDAO.getById(dbIssue.getMachine_id());
	    if (machine != null) {
		// Set the DTO with a machine information
		complaintFullInfo.setMachine(machine);
		long supportAssistance = machine.getSupport_assistance();

		// send sms to customer for new complaint
		Customer customer = customerDAO.getById(machine.getCustomer_id());

		if (customer != null) {
		    complaintFullInfo.setCustomer(customer);
		    complaintFullInfo.setUser(authUser);
		    String customerMainPerson = customer.getCust_support_phone();
		    JsonObject jsonObjSms = new JsonObject();
		    jsonObjSms.addProperty("toMobileNumber", customerMainPerson.replaceAll("\\+", ""));
		    jsonObjSms.addProperty("smsMessage",
			    "New Complaint has been raised by " + newIssue.getPhonedevice_operator_name() + " "
				    + newIssue.getReporting_device() + " Complaint No: " + newIssue.getIssue_number());

		    Thread custSms = new Thread(new SendSMS(jsonObjSms));
		    custSms.start();
		    log.info("Complaint raised sms sent to customer.");
		}
		/*
		 * Here it will check the support assistance according to that
		 * work flow. if Support assistance for manufacturer is 1 and
		 * for reseller is 2
		 */
		if (supportAssistance == 1) { // MANUFACTURER
		    Manufacturer manufacturer = manufacturerDAO.getById(machine.getManufacturer_id());
		    if (manufacturer != null) {

			// send email/sms to manufacturer support contact if
			// incident raised

			// EMAIL to support person mail
			String manContactEmail = manufacturer.getCust_support_email();
			if (manContactEmail != null) {
			    JsonObject jsonObj = MailUtils.getComplaintReportedMail(manContactEmail, complaintFullInfo,
				    phoneDevice);
			    Thread supportMail = new Thread(new SendMail(jsonObj));
			    supportMail.start();
			    log.info("Complaint registration email sent to support contact details.");
			}
			// SMS for manufacturer
			String manContactPhone = manufacturer.getCust_support_phone();
			if (manContactPhone != null) {
			    JsonObject jsonObjSms = new JsonObject();
			    jsonObjSms.addProperty("toMobileNumber", manContactPhone.replaceAll("\\+", ""));
			    jsonObjSms.addProperty("smsMessage",
				    "A new complaint has been registered with AttendMe. " + "Complaint No: "
					    + dbIssue.getIssue_number() + ", " + "Customer Name: "
					    + complaintFullInfo.getCustomer().getCompany_name() + ", "
					    + "Operator Mobile: " + phoneDevice.getFld_mobile_number());

			    Thread t6 = new Thread(new SendSMS(jsonObjSms));
			    t6.start();
			    log.info("Complaint registration sms sent to support contact details.");
			}

			// not subscribed
			String subscriptionStatus = manufacturer.getCur_subscription_status();
			if (subscriptionStatus.equals("INACTIVE")) {
			    // manufacturer not onboard
			    log.severe("A manufacturer has not subscribed to AttendMe.");
			} else {
			    // send email/sms to web admin
			    /*
			     * User manufacturerWebAdmin =
			     * userDAO.getgetByCompanyDetailsIdAndRole(
			     * manufacturer.getId(),
			     * User.ROLE_TYPE.MANUFACTURERADMIN.toString());
			     */
			    User manufacturerWebAdmin = userDAO.findByManufacturerIdRoleID(manufacturer.getId(), 3);
			    // EMAIL
			    String manufacturerWebAdminEmail = manufacturerWebAdmin.getEmail();
			    if (manufacturerWebAdminEmail != null) {
				JsonObject jsonObj = MailUtils.getComplaintReportedMail(manufacturerWebAdminEmail,
					complaintFullInfo, phoneDevice);

				Thread manuWebEmail = new Thread(new SendMail(jsonObj));
				manuWebEmail.start();

				log.info("Email sent to manufacturer web admin");
			    } else {
				log.info("There is no manufacturer web admin email");

			    }
			    // SMS
			    String manufacturerWebAdminMobile = manufacturerWebAdmin.getMobile();
			    if (manufacturerWebAdminMobile != null) {
				JsonObject jsonObjSms = new JsonObject();
				jsonObjSms.addProperty("toMobileNumber",
					manufacturerWebAdminMobile.replaceAll("\\+", ""));
				jsonObjSms.addProperty("smsMessage",
					"A new complaint has been registered with AttendMe. " + "Complaint No: "
						+ dbIssue.getIssue_number() + ", " + "Customer Name: "
						+ complaintFullInfo.getCustomer().getCompany_name() + ", "
						+ "Operator Mobile: " + phoneDevice.getFld_mobile_number());

				Thread manufWebAdminMobile = new Thread(new SendSMS(jsonObjSms));
				manufWebAdminMobile.start();
				log.info("Complaint registration sms sent to wen admin mobile.");
			    }

			}

		    } else {
			log.severe("A manufacturer does not exist.");
		    }
		    // Support Assistance is reseller = 2
		    // supportAssistance.equalsIgnoreCase("RESELLER")
		} else if (supportAssistance == 2) {

		    Reseller reseller = resellerDAO.getById(machine.getReseller_id());
		    if (reseller != null) {

			// send email/sms to reseller support contact if
			// incident raised
			// EMAIL
			String manContactEmail = reseller.getCust_support_email();
			if (manContactEmail != null) {
			    JsonObject jsonObj = MailUtils.getComplaintReportedMail(manContactEmail, complaintFullInfo,
				    phoneDevice);
			    Thread manContEmail = new Thread(new SendMail(jsonObj));
			    manContEmail.start();
			    log.info("Complaint registration email sent to support contact details.");
			}
			// SMS
			String resellerContactPhone = reseller.getCust_support_phone();
			if (resellerContactPhone != null) {
			    JsonObject jsonObjSms = new JsonObject();
			    jsonObjSms.addProperty("toMobileNumber", resellerContactPhone.replaceAll("\\+", ""));
			    jsonObjSms.addProperty("smsMessage",
				    "A new complaint has been registered with AttendMe. " + "Complaint No: "
					    + dbIssue.getIssue_number() + ", " + "Customer Name: "
					    + complaintFullInfo.getCustomer().getCompany_name() + ", "
					    + "Operator Mobile: " + phoneDevice.getFld_mobile_number());

			    Thread manContactPhoneSMS = new Thread(new SendSMS(jsonObjSms));
			    manContactPhoneSMS.start();
			    log.info("Complaint registration sms sent to support contact details.");
			}
			String subscriptionStatus = reseller.getCur_subscription_status();

			// not subscribed
			if ("INACTIVE".equals(subscriptionStatus)) {
			    log.severe("A reseller has not subscribed to AttendMe.");
			} else {
			    // send email to web admin of reseller
			    /*
			     * User resellerWebAdmin =
			     * userDAO.getByCompanyDetailsIdAndRole(reseller.
			     * getId(),
			     * User.ROLE_TYPE.RESELLERADMIN.toString());
			     */
			    User resellerWebAdmin = userDAO.findByResellerIdRoleID(reseller.getId(), 4);
			    String resellerWebAdminEmail = resellerWebAdmin.getEmail();
			    // EMAIL
			    if (resellerWebAdminEmail != null) {
				JsonObject jsonObj = MailUtils.getComplaintReportedMail(resellerWebAdminEmail, complaintFullInfo,
					phoneDevice);
				// new SendMail(jsonObj).run();
				Thread resellWebAdmin = new Thread(new SendMail(jsonObj));
				resellWebAdmin.start();
				log.info("Email sent to reseller web admin");
			    } else {
				log.info("There is no reseller web admin email");

			    }
			    // SMS
			    String resellerWebAdminMobile = resellerWebAdmin.getMobile();
			    if (resellerWebAdminMobile != null) {
				JsonObject jsonObjSms = new JsonObject();
				jsonObjSms.addProperty("toMobileNumber", resellerWebAdminMobile.replaceAll("\\+", ""));
				jsonObjSms.addProperty("smsMessage",
					"A new complaint has been registered with AttendMe. " + "Complaint No: "
						+ dbIssue.getIssue_number() + ", " + "Customer Name: "
						+ complaintFullInfo.getCustomer().getCompany_name() + ", " + "Operator Mobile: "
						+ phoneDevice.getFld_mobile_number());
				// send SMS
				Thread resellWebAdminMobile = new Thread(new SendSMS(jsonObjSms));
				resellWebAdminMobile.start();
				log.info("Complaint registration sms sent to web admin mobile.");
			    }

			}
		    } else {
			log.severe("A reseller does not exist.");
		    }
		}

	    } else {
		log.severe("A machine does not exist.");
	    }

	    return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("A complaint created successfully"))
		    .build();
	} else {
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("A complaint not created"))
		    .build();
	}

    }

    /*
     * Sparkonix v2 get the all complaints for the super admin
     */
    @GET
    @UnitOfWork
    public Response listIssues(@Auth User authUser) {
	try {
	    log.info("In Complaint Details for Super Admin");
	    List<ComplaintDTO> complaintDTOs = new ArrayList<>();
	    List<IssueNumberDetail> issueNumberDetailList = issueNumberDetailDAO.findAll();
	    for (int i = 0; i < issueNumberDetailList.size(); i++) {
		ComplaintDTO complaintDTO = new ComplaintDTO();

		complaintDTO.setIssueNumberDetail(issueNumberDetailList.get(i));

		ComplaintDetail complaintDetailObj = complaintDetailDAO
			.findComplaintByCurrentStatus(issueNumberDetailList.get(i).getId());
		complaintDTO.setComplaintDetail(complaintDetailObj);

		long machineId = issueNumberDetailList.get(i).getMachine_id();
		complaintDTO.setMachine(machineDAO.getById(machineId));

		long custID = issueNumberDetailList.get(i).getCustomer_id();
		complaintDTO.setCustomer(customerDAO.getById(custID));

		complaintDTO.setUser(userDAO.getById(complaintDetailObj.getTechinician_id()));

		complaintDTOs.add(complaintDTO);
	    }
	    return Response.status(Status.OK).entity(complaintDTOs).build();
	} catch (Exception e) {
	    log.severe("Unable to find Issues " + e);
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Complaints"))
		    .build();
	}
    }

    /*
     * It will return the complaints according to its support id.
     */
    @GET
    @Path("/{roleId}/{companyId}")
    @UnitOfWork
    public Response listIssuesByRoleAndCompanyId(@Auth User authUser, @PathParam("roleId") long roleId,
	    @PathParam("companyId") long manResCompanyId) {

	log.info(" In listIssuesByRoleAndCompanyId");
	long supportAssistance = 0;
	List<ComplaintDTO> complaintDTOs = new ArrayList<>();
	try {
	    if (roleId == 3) {// manufacturer
		supportAssistance = 1;
		List<IssueNumberDetail> issueNumberDetailList = issueNumberDetailDAO
			.findAllBySupportAssitanaceAndCompanyId(supportAssistance, manResCompanyId);
		for (int i = 0; i < issueNumberDetailList.size(); i++) {
		    ComplaintDTO complaintDTO = new ComplaintDTO();

		    complaintDTO.setIssueNumberDetail(issueNumberDetailList.get(i));

		    ComplaintDetail complaintDetailObj = complaintDetailDAO
			    .findComplaintByCurrentStatus(issueNumberDetailList.get(i).getId());
		    complaintDTO.setComplaintDetail(complaintDetailObj);

		    long machineId = issueNumberDetailList.get(i).getMachine_id();
		    complaintDTO.setMachine(machineDAO.getById(machineId));

		    long custID = issueNumberDetailList.get(i).getCustomer_id();
		    complaintDTO.setCustomer(customerDAO.getById(custID));

		    complaintDTO.setUser(userDAO.getById(complaintDetailObj.getTechinician_id()));

		    complaintDTOs.add(complaintDTO);
		}

	    } else if (roleId == 4) { // reseller
		supportAssistance = 2;

		List<IssueNumberDetail> issueNumberDetailList = issueNumberDetailDAO
			.findAllBySupportAssitanaceAndCompanyId(supportAssistance, manResCompanyId);
		for (int i = 0; i < issueNumberDetailList.size(); i++) {
		    ComplaintDTO complaintDTO = new ComplaintDTO();

		    complaintDTO.setIssueNumberDetail(issueNumberDetailList.get(i));

		    ComplaintDetail complaintDetailObj = complaintDetailDAO
			    .findComplaintByCurrentStatus(issueNumberDetailList.get(i).getId());
		    complaintDTO.setComplaintDetail(complaintDetailObj);

		    long machineId = issueNumberDetailList.get(i).getMachine_id();
		    complaintDTO.setMachine(machineDAO.getById(machineId));

		    long custID = issueNumberDetailList.get(i).getCustomer_id();
		    complaintDTO.setCustomer(customerDAO.getById(custID));

		    complaintDTO.setUser(userDAO.getById(complaintDetailObj.getTechinician_id()));

		    complaintDTOs.add(complaintDTO);
		}
	    }
	    return Response.status(Status.OK).entity(complaintDTOs).build();
	} catch (Exception e) {
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Complaints list"))
		    .build();
	}

    }

    /*
     * This will return the list of complaint that has been assigned to
     * technician
     * 
     */
    @GET
    @Path("/assignedto/{assignedToId}")
    @UnitOfWork
    public Response listIssuesByAssignedToId(@Auth User authUser, @PathParam("assignedToId") long assignedToId) {
	try {
	    log.info(" In listIssuesByAssignedToId");
	    // complaintDetailDAO.getComplaintofTechnician(assignedToId);
	    List<ComplaintDTO> complaintDTOs = new ArrayList<>();
	    // Assing to means technician id who get the complaint request
	    List<IssueTechnician> issuesNumList = issueTechnicianDAO.getIssueID(assignedToId);

	    for (int i = 0; i < issuesNumList.size(); i++) {
		ComplaintDTO complaintDTO = new ComplaintDTO();

		IssueNumberDetail issueNumberDetail = issueNumberDetailDAO.getById(issuesNumList.get(i).getIssue_id());
		complaintDTO.setIssueNumberDetail(issueNumberDetail);

		ComplaintDetail complaintDetailObj = complaintDetailDAO
			.findComplaintByCurrentStatus(issueNumberDetail.getId());
		complaintDTO.setComplaintDetail(complaintDetailObj);

		long machineId = issueNumberDetail.getMachine_id();
		complaintDTO.setMachine(machineDAO.getById(machineId));

		long custID = issueNumberDetail.getCustomer_id();
		complaintDTO.setCustomer(customerDAO.getById(custID));

		complaintDTO.setUser(userDAO.getById(complaintDetailObj.getTechinician_id()));

		complaintDTOs.add(complaintDTO);
	    }
	    return Response.status(Status.OK).entity(complaintDTOs).build();

	} catch (Exception e) {
	    log.severe("Unable to find Issues " + e);
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find issues")).build();
	}
    }

}
