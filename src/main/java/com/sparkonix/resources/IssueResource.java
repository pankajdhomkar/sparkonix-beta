package com.sparkonix.resources;

import java.util.Date;
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
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.IssueDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.IssueDetailDTO;
import com.sparkonix.utils.FcmMessage;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendFcmPushNotification;
import com.sparkonix.utils.SendMail;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/issue")
@Produces(MediaType.APPLICATION_JSON)
public class IssueResource {

	private final IssueDAO issueDAO;
	private final PhoneDeviceDAO phoneDeviceDAO;
	private final MachineDAO machineDAO;
	private final CompanyDetailDAO companyDetailDAO;
	private final UserDAO userDAO;
	private final ResellerDAO resellerDAO;

	private final Logger log = Logger.getLogger(IssueResource.class.getName());

	public IssueResource(IssueDAO issueDAO, PhoneDeviceDAO phoneDeviceDAO, MachineDAO machineDAO,
			CompanyDetailDAO companyDetailDAO, UserDAO userDAO, ResellerDAO resellerDAO) {
		this.issueDAO = issueDAO;
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.machineDAO = machineDAO;
		this.companyDetailDAO = companyDetailDAO;
		this.userDAO = userDAO;
		this.resellerDAO = resellerDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{issueId}")
	public Response getIssueById(@Auth User authUser, @PathParam("issueId") long issueId) {
		try {
			log.info(" In getIssueById");
			System.out.println("Json of issue dao------------------" + JsonUtils.getJson(issueDAO.getById(issueId)));
			return Response.status(Status.OK).entity(JsonUtils.getJson(issueDAO.getById(issueId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Issue " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Issue")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/getdetail/{issueId}")
	public Response getIssueDetailById(@Auth User authUser, @PathParam("issueId") long issueId) throws Exception {

		Issue issue = issueDAO.getById(issueId);
		if (issue != null) {
			try {
				Machine machine = machineDAO.getById(issue.getMachineId());
				CompanyDetail companyDetail = companyDetailDAO.getById(machine.getManufacturerId());
				PhoneDevice phoneDevice = phoneDeviceDAO.getById(issue.getReportingDevice());
				User user = userDAO.getById(issue.getAssignedTo());

				IssueDetailDTO issueDetailDTO = new IssueDetailDTO();

				issueDetailDTO.setIssueNumber(issue.getIssueNumber());
				issueDetailDTO.setComplaintDetail(issue.getDetails());
				issueDetailDTO.setLoggedDate(issue.getDateReported());

				if (machine != null) {
					issueDetailDTO.setMachineSerialNumber(machine.getSerialNumber());
				}

				if (companyDetail != null) {
					issueDetailDTO.setManufacturerName(companyDetail.getCompanyName());
				}

				if (phoneDevice != null) {
					issueDetailDTO.setOperatorName(phoneDevice.getOperatorName());
					issueDetailDTO.setOperatorContactNumber(phoneDevice.getPhoneNumber());
				}

				if (user != null) {
					issueDetailDTO.setAttendedBy(user.getName());
					issueDetailDTO.setAttendedByContactNumber(user.getMobile());
				}

				return Response.status(Status.OK).entity(issueDetailDTO).build();

			} catch (Exception e) {
				log.severe("Unable to find Issue " + e);
			}

		} else {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Issue")).build();
		}
		return null;
	}

	@PUT
	@UnitOfWork
	@Path("/{issueId}")
	public Response updateIssueById(@Auth User authUser, @PathParam("issueId") long issueId, Issue issue) {
		try {
			log.info(" In updateUserById");
			Issue dbIssue = issueDAO.getById(issueId);

			if (dbIssue == null) {
				log.severe("Unable to update complaint");
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update complaint"))
						.build();
			}

			if (authUser.getRole().equalsIgnoreCase(User.ROLE_TYPE.MANUFACTURERADMIN.toString())
					|| authUser.getRole().equalsIgnoreCase(User.ROLE_TYPE.RESELLERADMIN.toString())) {
				User assignedTo = userDAO.getById(issue.getAssignedTo());
				// get operator(who has reported issue) details
				PhoneDevice reportedBy = phoneDeviceDAO.getById(issue.getReportingDevice());

				if (assignedTo != null && reportedBy != null) {
					if (issue.getStatus().equalsIgnoreCase(Issue.ISSUE_STATUS.ASSIGNED.toString())) {
						dbIssue.setDateAssigned(new Date());
						dbIssue.setAssignedTo(issue.getAssignedTo());// technician
																		// id
						dbIssue.setTechnicianName(assignedTo.getName());// technician
																		// name
						dbIssue.setStatus(Issue.ISSUE_STATUS.ASSIGNED.toString());
					}

					/*
					 * Bellow all code that send sms to all respected person
					 * 
					 */
					// send sms to technician if web admin assigned issue to him
					JsonObject jsonObjSms = new JsonObject();
					jsonObjSms.addProperty("toMobileNumber", assignedTo.getMobile().replaceAll("\\+", ""));
					jsonObjSms.addProperty("smsMessage",
							"A machine complaint has been assigned to you. " + "Customer Name: "
									+ dbIssue.getCustomerCompanyName() + ", " + "Operator Mobile: "
									+ reportedBy.getPhoneNumber());

					// send SMS
					new SendSMS(jsonObjSms).run();
					log.info("Complaint assigned sms sent to technician.");
					// send push notification to operator(who has reported) if
					// web admin assigned technician to issue

					FcmMessage fcmMessage = new FcmMessage();

					String details = "Complaint no.: " + dbIssue.getIssueNumber() + "\n" + "Details: "
							+ dbIssue.getDetails() + "\n" + "Status: " + dbIssue.getStatus() + "\n" + "Reported By: "
							+ reportedBy.getOperatorName() + "\n" + "Reported Date: " + dbIssue.getDateReported() + "\n"
							+ "Technician Name: " + assignedTo.getName() + "\n" + "Technician Mobile: "
							+ assignedTo.getMobile() + "\n" + "Assigned Date: " + dbIssue.getDateAssigned();

					JsonObject data = new JsonObject();
					data.addProperty("title", "Technician assigned");
					data.addProperty("message", "A technician has been assigned to your complaint");
					data.addProperty("description", details);

					fcmMessage.setData(data);
					fcmMessage.setTo(reportedBy.getFcmToken());

					Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
					t1.start();
				} else {
					log.warning("Complaint not assigned to technician.");
				}
			}

			if (authUser.getRole().equalsIgnoreCase(User.ROLE_TYPE.TECHNICIAN.toString())) {

				// if status closed by technician then update the data and send
				// notification to respected authorities
				if (issue.getStatus().equalsIgnoreCase(Issue.ISSUE_STATUS.CLOSED.toString())) {
					System.out.println("---10---" + Issue.ISSUE_STATUS.CLOSED.toString());
					dbIssue.setDateClosed(new Date());
					dbIssue.setStatus(Issue.ISSUE_STATUS.CLOSED.toString());
					dbIssue.setFailureReason(issue.getFailureReason());
					dbIssue.setActionTaken(issue.getActionTaken());

					User assignedTo2 = userDAO.getById(issue.getAssignedTo());
					PhoneDevice reportedBy2 = phoneDeviceDAO.getById(issue.getReportingDevice());
					// send push notification to operator(who has reported this
					// issue) if technician changed status
					if (reportedBy2 != null) {
						FcmMessage fcmMessage = new FcmMessage();
						String details2 = "Complaint no.: " + dbIssue.getIssueNumber() + "\n" + "Details: "
								+ dbIssue.getDetails() + "\n" + "Status: " + dbIssue.getStatus() + "\n"
								+ "Reported By: " + reportedBy2.getOperatorName() + "\n" + "Reported Date: "
								+ dbIssue.getDateReported() + "\n" + "Technician Assigned Name: "
								+ assignedTo2.getName() + "\n" + "Technician Assigned Mobile: "
								+ assignedTo2.getMobile() + "\n" + "Technician Assigned Date: "
								+ dbIssue.getDateAssigned() + "\n" + "Failure Reason: " + dbIssue.getFailureReason()
								+ "\n" + "Action Taken: " + dbIssue.getActionTaken() + "\n" + "Closed Date: "
								+ dbIssue.getDateClosed();

						JsonObject data = new JsonObject();
						data.addProperty("title", "Complaint status changed");
						data.addProperty("message", "Status of your complaint has been changed by the technician");
						data.addProperty("description", details2);
						fcmMessage.setData(data);
						fcmMessage.setTo(reportedBy2.getFcmToken());
						Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
						t1.start();

					} else {
						log.severe("Failed to send push notifaication. Error: A PhoneDevice does not exist.");
					}

					// send email to web admin if technician updated record
					Machine machine = machineDAO.getById(dbIssue.getMachineId());
					if (machine != null) {
						String supportAssistance = machine.getSupportAssistance();

						// send sms to customer for status closed
						CompanyDetail customer = companyDetailDAO.getById(machine.getCustomerId());
						String customerMainPerson = customer.getCustSupportPhone();
						if (customerMainPerson != null) {
							JsonObject jsonObjSms = new JsonObject();
							jsonObjSms.addProperty("toMobileNumber", customerMainPerson.replaceAll("\\+", ""));
							jsonObjSms.addProperty("smsMessage",
									"Complaint no " + dbIssue.getIssueNumber() + " has been closed. Kindly verify the same.");
							Thread customerMainPersonSMS = new Thread(new SendSMS(jsonObjSms));
							customerMainPersonSMS.start();
							log.info("Complaint status changed sms sent to customer.");
						}
						if (supportAssistance.equalsIgnoreCase("MANUFACTURER")) {
							CompanyDetail manufacturer = companyDetailDAO.getById(machine.getManufacturerId());
							if (manufacturer != null) {
								String subscriptionStatus = manufacturer.getCurSubscriptionStatus();

								// not subscribed
								if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
									log.severe("A manufacturer has not subscribed to AttendMe.");
								} else {
									// send email to manufacturer web admin
									User manufacturerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(
											manufacturer.getId(), User.ROLE_TYPE.MANUFACTURERADMIN.toString());
									String manufacturerWebAdminEmail = manufacturerWebAdmin.getEmail();
									CompanyDetail supportWebAdminEmail = companyDetailDAO.getById(manufacturer.getId());
									String supportEmail = supportWebAdminEmail.getCustSupportEmail();
									// This mail goes to manufacturer admin
									if (manufacturerWebAdminEmail != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(manufacturerWebAdminEmail,
												dbIssue, authUser);
										new SendMail(jsonObj).run();
										Thread manufWebAdmnEmail = new Thread(new SendMail(jsonObj));
										manufWebAdmnEmail.start();
										log.info("Email sent to manufacturer web admin");
									} else {
										log.info("There is no manufaturer web admin email");
									}
									// This mail goes to manufacturer support
									// person
									if (supportEmail != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(supportEmail,
												dbIssue, authUser);
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
						} else if (supportAssistance.equalsIgnoreCase("RESELLER")) {

							Reseller reseller = resellerDAO.getById(machine.getResellerId());
							if (reseller != null) {
								String subscriptionStatus = reseller.getCurSubscriptionStatus();
								// not subscribed
								if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
									log.severe("A reseller has not subscribed to AttendMe.");
								} else {
									// this mail goes to reseller admin----
									User resellerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(reseller.getId(),
											User.ROLE_TYPE.RESELLERADMIN.toString());
									String resellerWebAdminEmail = resellerWebAdmin.getEmail();
									if (resellerWebAdminEmail != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerWebAdminEmail,
												dbIssue, authUser);
										new SendMail(jsonObj).run();
										Thread resellWebAdminEmail = new Thread(new SendMail(jsonObj));
										resellWebAdminEmail.start();
										log.info("Email sent to reseller web admin");
									} else {
										log.info("There is no reseller web admin email");
									}
									// This mail goes to a reseller customer
									// support person
									String resellerSupportPerson = reseller.getCustSupportEmail();
									if (resellerSupportPerson != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerSupportPerson,
												dbIssue, authUser);
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
					System.out.println("----Statuts---->" + issue.getStatus());
				} else if (issue.getStatus().equalsIgnoreCase(Issue.ISSUE_STATUS.INPROGRESS.toString())) {
					// ------------------------------INPROGRESS------------------------------------
					dbIssue.setDateClosed(new Date());
					dbIssue.setStatus(Issue.ISSUE_STATUS.INPROGRESS.toString());
					dbIssue.setFailureReason(issue.getFailureReason());
					dbIssue.setActionTaken(issue.getActionTaken());

					User assignedTo2 = userDAO.getById(issue.getAssignedTo());
					PhoneDevice reportedBy2 = phoneDeviceDAO.getById(issue.getReportingDevice());
					// send push notification to operator(who has reported this
					// issue) if technician changed status
					if (reportedBy2 != null) {
						FcmMessage fcmMessage = new FcmMessage();
						String details2 = "Complaint no.: " + dbIssue.getIssueNumber() + "\n" + "Details: "
								+ dbIssue.getDetails() + "\n" + "Status: " + dbIssue.getStatus() + "\n"
								+ "Reported By: " + reportedBy2.getOperatorName() + "\n" + "Reported Date: "
								+ dbIssue.getDateReported() + "\n" + "Technician Assigned Name: "
								+ assignedTo2.getName() + "\n" + "Technician Assigned Mobile: "
								+ assignedTo2.getMobile() + "\n" + "Technician Assigned Date: "
								+ dbIssue.getDateAssigned() + "\n" + "Failure Reason: " + dbIssue.getFailureReason()
								+ "\n" + "Action Taken: " + dbIssue.getActionTaken() + "\n" + "Inprogress Date: "
								+ dbIssue.getDateClosed();

						JsonObject data = new JsonObject();
						data.addProperty("title", "Complaint status changed");
						data.addProperty("message", "Status of your complaint has been changed by the technician");
						data.addProperty("description", details2);
						fcmMessage.setData(data);
						fcmMessage.setTo(reportedBy2.getFcmToken());
						Thread pushOperator = new Thread(new SendFcmPushNotification(fcmMessage));
						pushOperator.start();
					} else {
						log.severe("Failed to send push notifaication. Error: A PhoneDevice does not exist.");
					}

					// send email to web admin if technician updated record & if
					// web admin has subscribed
					Machine machine = machineDAO.getById(dbIssue.getMachineId());
					if (machine != null) {

						String supportAssistance = machine.getSupportAssistance();

						// send sms to customer for status change
						CompanyDetail customer = companyDetailDAO.getById(machine.getCustomerId());
						String customerMainPerson = customer.getCustSupportPhone();
						if (customerMainPerson != null) {
							JsonObject jsonObjSms = new JsonObject();
							jsonObjSms.addProperty("toMobileNumber", customerMainPerson.replaceAll("\\+", ""));
							jsonObjSms.addProperty("smsMessage",
									"Complaint status has been changed to " + dbIssue.getStatus() + " "
											+ "Complaint No: " + dbIssue.getIssueNumber());

							Thread customerMainPersonSMS = new Thread(new SendSMS(jsonObjSms));
							customerMainPersonSMS.start();
							log.info("Complaint status changed sms sent to customer.");
						}

						if (supportAssistance.equalsIgnoreCase("MANUFACTURER")) {
							CompanyDetail manufacturer = companyDetailDAO.getById(machine.getManufacturerId());
							if (manufacturer != null) {
								String subscriptionStatus = manufacturer.getCurSubscriptionStatus();
								if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
									log.severe("A manufacturer has not subscribed to AttendMe.");
								} else {
									// send email to manufacturer web admin
									User manufacturerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(
											manufacturer.getId(), User.ROLE_TYPE.MANUFACTURERADMIN.toString());
									String manufacturerWebAdminEmail1 = manufacturerWebAdmin.getEmail();
									if (manufacturerWebAdminEmail1 != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(manufacturerWebAdminEmail1,
												dbIssue, authUser);
										new SendMail(jsonObj).run();
										Thread manufWebAdminEmail1 = new Thread(new SendMail(jsonObj));
										manufWebAdminEmail1.start();
										log.info("Email sent to manufacturer web admin");
									} else {
										log.info("There is no manufaturer web admin email");
									}
									//This mail goes to manufacturer support person
									CompanyDetail supportWebAdminEmail = companyDetailDAO.getById(manufacturer.getId());
									String supportEmail1 = supportWebAdminEmail.getCustSupportEmail();
									
									if (supportEmail1 != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(supportEmail1,
												dbIssue, authUser);
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

						} else if (supportAssistance.equalsIgnoreCase("RESELLER")) {
							Reseller reseller = resellerDAO.getById(machine.getResellerId());
							if (reseller != null) {
								String subscriptionStatus = reseller.getCurSubscriptionStatus();
								// not subscribed
								if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
									log.severe("A reseller has not subscribed to AttendMe.");
								} else {
									// send email to reseller web admin
									User resellerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(reseller.getId(),
											User.ROLE_TYPE.RESELLERADMIN.toString());
									String resellerWebAdminEmail = resellerWebAdmin.getEmail();
									if (resellerWebAdminEmail != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerWebAdminEmail,
												dbIssue, authUser);
										new SendMail(jsonObj).run();
										Thread resellerWebAdminEmail1 = new Thread(new SendMail(jsonObj));
										resellerWebAdminEmail1.start();
										log.info("Email sent to reseller web admin");
									} else {
										log.info("There is no reseller web admin email");
									}
									// This mail goes to reseller support person....
									String resellerSupportPerson = reseller.getCustSupportEmail();
									if (resellerSupportPerson != null) {
										JsonObject jsonObj = MailUtils.getComplaintStatusMail(resellerSupportPerson,
												dbIssue, authUser);
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

			} // end of technician check

			// update issue in database
			issueDAO.save(dbIssue);
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
