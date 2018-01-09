package com.sparkonix.resources;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ComplaintDTO;
import com.sparkonix.entity.dto.ComplaintHistoryDTO;
import com.sparkonix.utils.FcmMessage;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendFcmPushNotification;
import com.sparkonix.utils.SendMail;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author Pankaj Dhomkar Single issue will updated by this resource method
 */
@Path("/complaintdetail")
@Produces(MediaType.APPLICATION_JSON)
public class ComplaintDetailResource {
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

    private final Logger log = Logger.getLogger(ComplaintDetailResource.class.getName());

    public ComplaintDetailResource(IssueNumberDetailDAO issueNumberDetailDAO, PhoneOperatorDAO phoneOperatorDAO,
	    MachineDAO machineDAO, ComplaintDetailDAO complaintDetailDAO, ManufacturerDAO manufacturerDAO,
	    ResellerDAO resellerDAO, CustomerDAO customerDAO, IssueTechnicianDAO issueTechnicianDAO, UserDAO userDAO,
	    CompanyLocationDAO companyLocationDAO) {
	this.issueNumberDetailDAO = issueNumberDetailDAO;
	this.phoneOperatorDAO = phoneOperatorDAO;
	this.machineDAO = machineDAO;
	this.complaintDetailDAO = complaintDetailDAO;
	this.manufacturerDAO = manufacturerDAO;
	this.resellerDAO = resellerDAO;
	this.customerDAO = customerDAO;
	this.issueTechnicianDAO = issueTechnicianDAO;
	this.userDAO = userDAO;
	this.companyLocationDAO = companyLocationDAO;
    }

    @GET
    @UnitOfWork
    @Path("/{issuenumberId}")
    public Response getIssueById(@Auth User authUser, @PathParam("issuenumberId") long issuenumberId) {
	log.info(" In getIssueById");
	try {
	    System.out.println("Json of issue dao------------------"
		    + JsonUtils.getJson(issueNumberDetailDAO.getById(issuenumberId)));
	    ComplaintHistoryDTO complaintFull = new ComplaintHistoryDTO();
	    IssueNumberDetail issueDetails = issueNumberDetailDAO.getById(issuenumberId);
	    complaintFull.setIssueNumberDetail(issueDetails);
	    List<ComplaintDetail> objectComplaintList = complaintDetailDAO
		    .getAllComplaintDetailByIssueId(issuenumberId);
	    complaintFull.setComplaintDetail(objectComplaintList);
	    complaintFull.setCustomer(customerDAO.getById(issueDetails.getCustomer_id()));
	    complaintFull.setMachine(machineDAO.getById(issueDetails.getMachine_id()));
	    complaintFull.setUser(authUser);
	    return Response.status(Status.OK).entity(complaintFull).build();
	} catch (Exception e) {
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Issue")).build();
	}
    }

    /*
     * It will update the technician to complaint and status changed
     */
    @PUT
    @UnitOfWork
    @Path("/{issueId}")
    public Response updateIssueById(@Auth User authUser, @PathParam("issueId") long issueId, ComplaintDTO issue) {
	log.info("1.Update Status");
	try {
	    IssueNumberDetail issueNumberDb = issueNumberDetailDAO.getById(issueId);
	    ComplaintDetail complaintDetailDB = new ComplaintDetail();
	    if (issueNumberDb == null) {
		log.severe("2.(Update Status) Unable to update complaint");
		return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update complaint"))
			.build();
	    }

	    /*
	     * -----------------------------------------------------------------
	     * Only for the Manufacturer and reseller who assign a complaint to
	     * technician
	     * -----------------------------------------------------------------
	     */
	    if (authUser.getUser_role_id() == 3 // .equalsIgnoreCase(User.ROLE_TYPE.MANUFACTURERADMIN.toString()
		    || authUser.getUser_role_id() == 4) {
		log.info("1.In Manufacturer/Reseller Update Status");
		// assignedToTechnician its a technician information taken from
		// data base
		User assignedToTechnician = userDAO.getById(issue.getComplaintDetail().getAssigned_to());
		// get operator(who has reported issue) details
		PhoneOperator reportedBy = phoneOperatorDAO.getById(issue.getIssueNumberDetail().getReporting_device());
		
		if (assignedToTechnician != null && reportedBy != null) {
		    if (issue.getComplaintDetail().getStatus()
			    .equalsIgnoreCase(ComplaintDetail.COMPLAINT_STATUS.ASSIGNED.toString())) {
			complaintDetailDB.setDate_reported(new Date());
			complaintDetailDB.setAssigned_to(issue.getComplaintDetail().getAssigned_to());// technician
			complaintDetailDB.setTechinician_id(issue.getComplaintDetail().getAssigned_to());
			complaintDetailDB.setStatus(ComplaintDetail.COMPLAINT_STATUS.ASSIGNED.toString());
		    }

		    /*-----------------------------------------
		     * Bellow all code that send SMS to all respected person
		     * send sms to technician if web admin assigned issue to him
		     * -------------------------------------------
		     */
		    JsonObject jsonObjSms = new JsonObject();
		    jsonObjSms.addProperty("toMobileNumber", assignedToTechnician.getMobile().replaceAll("\\+", ""));
		    jsonObjSms.addProperty("smsMessage",
			    "A machine complaint has been assigned to you. " + "Customer Name: "
				    + issue.getCustomer().getCompany_name() + ", " + "Operator Mobile: "
				    + reportedBy.getFld_mobile_number());
		    new SendSMS(jsonObjSms).run(); // send SMS
		    log.info("Complaint assigned sms sent to technician.");
		    /*
		     * send push notification to operator(who has reported) if
		     * web admin assigned technician to complaint
		     */

		    FcmMessage fcmMessage = new FcmMessage();

		    String details = "Complaint no.: " + issueNumberDb.getIssue_number() + "\n" + "Details: "
			    + issueNumberDb.getDetails() + "\n" + "Status: " + complaintDetailDB.getStatus() + "\n"
			    + "Reported By: " + reportedBy.getFld_name() + "\n" + "Reported Date: "
			    + issue.getComplaintDetail().getDate_reported() + "\n" + "Technician Name: "
			    + assignedToTechnician.getName() + "\n" + "Technician Mobile: "
			    + assignedToTechnician.getMobile() + "\n" + "Assigned Date: "
			    + issue.getComplaintDetail().getDate_reported();

		    JsonObject data = new JsonObject();
		    data.addProperty("title", "Technician assigned");
		    data.addProperty("message", "A technician has been assigned to your complaint");
		    data.addProperty("description", details);

		    fcmMessage.setData(data);
		    fcmMessage.setTo(reportedBy.getFld_fcm_token());

		    Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
		    t1.start();
		} else {
		    log.warning("Complaint not assigned to technician.");
		}
	    }

	    /*
	     * ----------------------------------------------- 
	     * Checks Technician
	     * logged in or not Only for technician he will change the status.
	     * Technician id is 5
	     * -----------------------------------------------
	     */
	    if (authUser.getUser_role_id() == 5) {
		log.info("1.Technician can change a status");
		// if status closed by technician then update the data
		// send notification to respected authorities
		if (issue.getComplaintDetail().getStatus()
			.equalsIgnoreCase(ComplaintDetail.COMPLAINT_STATUS.CLOSED.toString())) {
		    complaintDetailDB.setDate_reported(new Date());
		    complaintDetailDB.setStatus(ComplaintDetail.COMPLAINT_STATUS.CLOSED.toString());
		    complaintDetailDB.setFailure_reason(issue.getComplaintDetail().getFailure_reason());
		    complaintDetailDB.setAction_taken(issue.getComplaintDetail().getAction_taken());

		    User assignedTo2 = userDAO.getById(issue.getComplaintDetail().getAssigned_to());
		    PhoneOperator reportedBy2 = phoneOperatorDAO
			    .getById(issue.getIssueNumberDetail().getReporting_device());
		    /*
		     * ---------------------------------------------------------
		     * send push notification to operator(who has reported this
		     * issue) if technician changed status
		     * ---------------------------------------------------------
		     */
		    if (reportedBy2 != null) {
			FcmMessage fcmMessage = new FcmMessage();
			ComplaintDetail dateClosed = complaintDetailDAO.getDateClosedByIssueId(
				issue.getIssueNumberDetail().getId(), assignedTo2.getId(),
				issue.getComplaintDetail().getStatus());
			String details2 = "Complaint no.: " + issueNumberDb.getIssue_number() + "\n" + "Details: "
				+ issueNumberDb.getDetails() + "\n" + "Status: " + complaintDetailDB.getStatus() + "\n"
				+ "Reported By: " + reportedBy2.getFld_name() + "\n" + "Reported Date: "
				+ issue.getComplaintDetail().getDate_reported() + "\n" + "Technician Assigned Name: "
				+ assignedTo2.getName() + "\n" + "Technician Assigned Mobile: "
				+ assignedTo2.getMobile() + "\n" + "Technician Assigned Date: "
				+ complaintDetailDB.getDate_reported() + "\n" + "Failure Reason: "
				+ complaintDetailDB.getFailure_reason() + "\n" + "Action Taken: "
				+ complaintDetailDB.getAction_taken() + "\n" + "Closed Date: "
				+ dateClosed.getDate_reported();

			JsonObject data = new JsonObject();
			data.addProperty("title", "Complaint status changed");
			data.addProperty("message", "Status of your complaint has been changed by the technician");
			data.addProperty("description", details2);
			fcmMessage.setData(data);
			fcmMessage.setTo(reportedBy2.getFld_fcm_token());
			Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
			t1.start();

		    } else {
			log.severe("Failed to send push notifaication. Error: A PhoneDevice does not exist.");
		    }

		    /*
		     * ---------------------------------------- Send a SMS/Email
		     * to customer/Manufacturer/Reseller to acknowledge the
		     * technician changed the status
		     * ----------------------------------------
		     */
		    Machine machine = machineDAO.getById(issue.getIssueNumberDetail().getMachine_id());
		    if (machine != null) {
			long supportAssistance = machine.getSupport_assistance();

			/*
			 * ---------------------------------------- send SMS to
			 * customer for status is closed
			 * ----------------------------------------
			 */
			Customer customer = customerDAO.getById(machine.getCustomer_id());
			String customerMainPerson = customer.getCust_support_phone();
			if (customerMainPerson != null) {
			    JsonObject jsonObjSms = new JsonObject();
			    jsonObjSms.addProperty("toMobileNumber", customerMainPerson.replaceAll("\\+", ""));
			    jsonObjSms.addProperty("smsMessage",
				    "Complaint no " + issue.getIssueNumberDetail().getIssue_number()
					    + " has been closed. Kindly verify the same.");
			    Thread customerMainPersonSMS = new Thread(new SendSMS(jsonObjSms));
			    customerMainPersonSMS.start();
			    log.info("Complaint status changed sms sent to customer.");
			}
			/*
			 * -------------------------------------------- send
			 * Email to customer for status is closed
			 * --------------------------------------------
			 */
			String customerMailId = customer.getCust_support_email();
			User technicianName = userDAO.getById(issue.getComplaintDetail().getAssigned_to());
			if (customerMailId != null) {
			    JsonObject jsonObj = MailUtils.getComplaintStatusReportedMail(customerMailId, issue,
				    technicianName.getName());
			    new SendMail(jsonObj).run();
			    Thread customerWebAdmnEmail = new Thread(new SendMail(jsonObj));
			    customerWebAdmnEmail.start();
			    log.info("Email sent to customer web admin");
			} else {
			    log.info("There is no customer web admin email");
			}
			/*
			 * -------------------------------------------- send
			 * Email to Manufacturer if he is giving a support after
			 * sale of machine. Machine support assistance id
			 * manufacturer = 1 reseller = 2
			 * --------------------------------------------
			 */
			if (supportAssistance == 1) { // manufacturer
			    Manufacturer manufacturer = manufacturerDAO.getById(machine.getManufacturer_id());
			    if (manufacturer != null) {
				String subscriptionStatus = manufacturer.getCur_subscription_status();
				// If the current subscription is not active
				if (subscriptionStatus.equals("INACTIVE")) {
				    log.severe("A manufacturer has not subscribed to AttendMe.");
				} else {
				    /*
				     * ------------------------------------ send
				     * email to manufacturer web admin
				     * ---------------------------------------
				     */
				    User manufacturerWebAdmin = userDAO.findByManufacturerIdRoleID(3,
					    manufacturer.getId());
				    String manufacturerWebAdminEmail = manufacturerWebAdmin.getEmail();
				    String supportEmail = manufacturer.getCust_support_email();
				    ComplaintDetail dateReported = complaintDetailDAO.getDateClosedByIssueId(issueId,
					    issue.getComplaintDetail().getAssigned_to(), "OPEN");
				    Date dateReportedComplaint = dateReported.getDate_reported();
				    CompanyLocation machineLocation = companyLocationDAO
					    .getById(issue.getMachine().getLocation_id());
				    String locationMachine = machineLocation.getAddress();
				    // This mail goes to manufacturer admin
				    if (manufacturerWebAdminEmail != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(manufacturerWebAdminEmail,
						issue, authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread manufWebAdmnEmail = new Thread(new SendMail(jsonObj));
					manufWebAdmnEmail.start();
					log.info("Email sent to manufacturer web admin");
				    } else {
					log.info("There is no manufaturer web admin email");
				    }
				    /*
				     * ------------------------------------ Send
				     * mail to manufacturer support person
				     * ---------------------------------------
				     */
				    if (supportEmail != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(supportEmail, issue,
						authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread supportPersonEmail = new Thread(new SendMail(jsonObj));
					supportPersonEmail.start();
					log.info("Email sent to manufacturer support person");
				    } else {
					log.info("There is no manufaturer support person email");
				    }
				}
			    } else {
				log.severe("A manufacturer does not exist.");
			    }
			} else if (supportAssistance == 2) { // Reseller

			    Reseller reseller = resellerDAO.getById(machine.getReseller_id());
			    if (reseller != null) {
				String subscriptionStatus = reseller.getCur_subscription_status();
				// not subscribed
				if (subscriptionStatus.equals("INACTIVE")) {
				    log.severe("A reseller has not subscribed to AttendMe.");
				} else {
				    /*------------------
				     * Send mail to reseller admin
				     * --------------------
				     */
				    User resellerWebAdmin = userDAO.findByResellerIdRoleID(4, reseller.getId());
				    String resellerWebAdminEmail = resellerWebAdmin.getEmail();
				    ComplaintDetail dateReported = complaintDetailDAO.getDateClosedByIssueId(issueId,
					    issue.getComplaintDetail().getAssigned_to(), "OPEN");
				    Date dateReportedComplaint = dateReported.getDate_reported();
				    CompanyLocation machineLocation = companyLocationDAO
					    .getById(issue.getMachine().getLocation_id());
				    String locationMachine = machineLocation.getAddress();

				    if (resellerWebAdminEmail != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerWebAdminEmail,
						issue, authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread resellWebAdminEmail = new Thread(new SendMail(jsonObj));
					resellWebAdminEmail.start();
					log.info("Email sent to reseller web admin");
				    } else {
					log.info("There is no reseller web admin email");
				    }
				    /*
				     * -----------------------------------------
				     * This mail goes to a reseller customer
				     * support person
				     * -----------------------------------------
				     */
				    String resellerSupportPerson = reseller.getCust_support_email();
				    if (resellerSupportPerson != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerSupportPerson,
						issue, authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread resellSupportPerson = new Thread(new SendMail(jsonObj));
					resellSupportPerson.start();
					log.info("Email sent to reseller web admin");
				    }
				}
			    } else {
				log.severe("A reseller does not exist.");
			    }
			}
		    } else {
			log.severe("A machine does not exist.");
		    }
		    // ---------end
		    // --CLOSED---------------------------------------------
		} else if (issue.getComplaintDetail().getStatus()
			.equalsIgnoreCase(ComplaintDetail.COMPLAINT_STATUS.INPROGRESS.toString())) {
		    // ------------------------------INPROGRESS------------------------------------
		    complaintDetailDB.setDate_reported(new Date());
		    complaintDetailDB.setStatus(ComplaintDetail.COMPLAINT_STATUS.INPROGRESS.toString());
		    complaintDetailDB.setFailure_reason(issue.getComplaintDetail().getFailure_reason());
		    complaintDetailDB.setAction_taken(issue.getComplaintDetail().getAction_taken());

		    User assignedTo2 = userDAO.getById(issue.getComplaintDetail().getAssigned_to());
		    PhoneOperator reportedBy2 = phoneOperatorDAO
			    .getById(issue.getIssueNumberDetail().getReporting_device());
		    /*
		     * ---------------------------------------------------------
		     * -- send push notification to operator(who has reported
		     * this issue) if technician changed status
		     * ---------------------------------------------------------
		     * --
		     */
		    if (reportedBy2 != null) {
			FcmMessage fcmMessage = new FcmMessage();
			ComplaintDetail dateClosed = complaintDetailDAO.getDateClosedByIssueId(
				issue.getIssueNumberDetail().getId(), assignedTo2.getId(),
				issue.getComplaintDetail().getStatus());
			String details2 = "Complaint no.: " + issueNumberDb.getIssue_number() + "\n" + "Details: "
				+ issueNumberDb.getDetails() + "\n" + "Status: " + complaintDetailDB.getStatus() + "\n"
				+ "Reported By: " + reportedBy2.getFld_name() + "\n" + "Reported Date: "
				+ issue.getComplaintDetail().getDate_reported() + "\n" + "Technician Assigned Name: "
				+ assignedTo2.getName() + "\n" + "Technician Assigned Mobile: "
				+ assignedTo2.getMobile() + "\n" + "Technician Assigned Date: "
				+ complaintDetailDB.getDate_reported() + "\n" + "Failure Reason: "
				+ complaintDetailDB.getFailure_reason() + "\n" + "Action Taken: "
				+ complaintDetailDB.getAction_taken() + "\n" + "Closed Date: "
				+ dateClosed.getDate_reported();

			JsonObject data = new JsonObject();
			data.addProperty("title", "Complaint status changed");
			data.addProperty("message", "Status of your complaint has been changed by the technician");
			data.addProperty("description", details2);
			fcmMessage.setData(data);
			fcmMessage.setTo(reportedBy2.getFld_fcm_token());
			Thread pushOperator = new Thread(new SendFcmPushNotification(fcmMessage));
			pushOperator.start();
		    } else {
			log.severe("Failed to send push notifaication. Error: A PhoneDevice does not exist.");
		    }
		    /*
		     * ---------------------------------------------------------
		     * ---- send email to web admin if technician updated record
		     * & if web admin has subscribed
		     * ---------------------------------------------------------
		     * ----
		     */
		    Machine machine = machineDAO.getById(issue.getIssueNumberDetail().getMachine_id());
		    if (machine != null) {
			long supportAssistance = machine.getSupport_assistance();
			/*
			 * ----------------------------------------- send SMS to
			 * customer for status change
			 * -----------------------------------------
			 */
			Customer customer = customerDAO.getById(machine.getCustomer_id());
			String customerMainPerson = customer.getCust_support_phone();
			if (customerMainPerson != null) {
			    JsonObject jsonObjSms = new JsonObject();
			    jsonObjSms.addProperty("toMobileNumber", customerMainPerson.replaceAll("\\+", ""));
			    jsonObjSms.addProperty("smsMessage",
				    "Complaint status has been changed to " + issue.getComplaintDetail().getStatus()
					    + " " + "Complaint No: " + issue.getIssueNumberDetail().getIssue_number());
			    Thread customerMainPersonSMS = new Thread(new SendSMS(jsonObjSms));
			    customerMainPersonSMS.start();
			    log.info("Complaint status changed sms sent to customer.");
			}
			/*-------------------------------------------------
			 * Send mail to Support assistance to Manufacturer 
			 * Manufacturer = 1 and Reseller = 2
			 * -----------------------------------------------
			 */
			if (supportAssistance == 1) {
			    Manufacturer manufacturer = manufacturerDAO.getById(machine.getManufacturer_id());
			    if (manufacturer != null) {
				String subscriptionStatus = manufacturer.getCur_subscription_status();
				if (subscriptionStatus.equals("INACTIVE")) {
				    log.severe("A manufacturer has not subscribed to AttendMe.");
				} else {
				    /*
				     * -----------------------------------------
				     * - send email to manufacturer web admin
				     * (It takes a email from user table login
				     * email id)
				     * -----------------------------------------
				     * -
				     */
				    User manufacturerWebAdmin = userDAO.findByManufacturerIdRoleID(3,
					    manufacturer.getId());
				    String manufacturerWebAdminEmail1 = manufacturerWebAdmin.getEmail();
				    ComplaintDetail dateReported = complaintDetailDAO.getDateClosedByIssueId(issueId,
					    issue.getComplaintDetail().getAssigned_to(), "INPROGRESS");
				    Date dateReportedComplaint = dateReported.getDate_reported();
				    CompanyLocation machineLocation = companyLocationDAO
					    .getById(issue.getMachine().getLocation_id());
				    String locationMachine = machineLocation.getAddress();
				    if (manufacturerWebAdminEmail1 != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(
						manufacturerWebAdminEmail1, issue, authUser, dateReportedComplaint,
						locationMachine);
					new SendMail(jsonObj).run();
					Thread manufWebAdminEmail1 = new Thread(new SendMail(jsonObj));
					manufWebAdminEmail1.start();
					log.info("Email sent to manufacturer web admin");
				    } else {
					log.info("There is no manufaturer web admin email");
				    }
				    /*
				     * -----------------------------------------
				     * - Mail goes to manufacturer support
				     * person
				     * -----------------------------------------
				     * -
				     */
				    String supportEmail1 = manufacturer.getCust_support_email();

				    if (supportEmail1 != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(supportEmail1, issue,
						authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread supptEmail1 = new Thread(new SendMail(jsonObj));
					supptEmail1.start();
					log.info("Email sent to manufacturer support person");
				    } else {
					log.info("There is no manufaturer support person email");
				    }
				}
			    } else {
				log.severe("A manufacturer does not exist.");
			    }
			    /*
			     * ---------------------------------------------
			     * Send mail to Support assistance to Reseller
			     * reseller = 2
			     * ---------------------------------------------
			     */
			} else if (supportAssistance == 2) {
			    Reseller reseller = resellerDAO.getById(machine.getReseller_id());
			    if (reseller != null) {
				String subscriptionStatus = reseller.getCur_subscription_status();
				// not subscribed
				if (subscriptionStatus.equals("INACTIVE")) {
				    log.severe("A reseller has not subscribed to AttendMe.");
				} else {
				    /*
				     * -----------------------------------------
				     * --- send email to reseller web admin
				     * -----------------------------------------
				     * ---
				     */
				    User resellerWebAdmin = userDAO.findByResellerIdRoleID(4, reseller.getId());
				    String resellerWebAdminEmail = resellerWebAdmin.getEmail();
				    ComplaintDetail dateReported = complaintDetailDAO.getDateClosedByIssueId(issueId,
					    issue.getComplaintDetail().getAssigned_to(), "INPROGRESS");
				    Date dateReportedComplaint = dateReported.getDate_reported();
				    CompanyLocation machineLocation = companyLocationDAO
					    .getById(issue.getMachine().getLocation_id());
				    String locationMachine = machineLocation.getAddress();
				    if (resellerWebAdminEmail != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerWebAdminEmail,
						issue, authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread resellerWebAdminEmail1 = new Thread(new SendMail(jsonObj));
					resellerWebAdminEmail1.start();
					log.info("Email sent to reseller web admin");
				    } else {
					log.info("There is no reseller web admin email");
				    }
				    /*
				     * -----------------------------------------
				     * This mail goes to reseller support person
				     * -----------------------------------------
				     */
				    String resellerSupportPerson = reseller.getCust_support_email();
				    if (resellerSupportPerson != null) {
					JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerSupportPerson,
						issue, authUser, dateReportedComplaint, locationMachine);
					new SendMail(jsonObj).run();
					Thread resellerSupportPerson1 = new Thread(new SendMail(jsonObj));
					resellerSupportPerson1.start();
					log.info("Email sent to reseller web admin");
				    }
				}
			    } else {
				log.severe("A reseller does not exist.");
			    }
			}
		    } else {
			log.severe("A machine does not exist.");
		    }
		} else {
		    log.severe("Complaint status should be closed.");
		    return Response.status(Status.BAD_REQUEST)
			    .entity(JsonUtils.getErrorJson("Complaint status should be closed")).build();
		}

	    }
	    /*
	     * ---------------------------------------------------------------
	     * end of technician check
	     */

	    /*
	     * -----------------------------------------------------------------
	     * Update information into database Complaint detail.
	     * -----------------------------------------------------------------
	     */
	    complaintDetailDAO.save(complaintDetailDB);

	    log.info("Complaint details updated.");
	    return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Complaint details updated successfully"))
		    .build();

	} catch (Exception e) {
	    log.severe("Unable to update complaint details. Error: " + e);
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("Unable to update complaint details")).build();
	}

    }
}
