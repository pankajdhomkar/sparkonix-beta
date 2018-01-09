package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author Pankaj Dhomkar
 * Here we will get the Machine information.
 */
@Path("/machine")
@Produces(MediaType.APPLICATION_JSON)
public class MachineResource {
	private final MachineDAO machineDAO;
	private final QRCodeDAO qrCodeDAO;
	private final ManufacturerDAO manufacturerDAO;
	private final ResellerDAO resellerDAO;
	private final CustomerDAO customerDAO;
	private final PhoneOperatorDAO phoneOperatorDAO;
	private final UserDAO userDAO;
	
	private final Logger log = Logger.getLogger(MachineResource.class.getName());

	public MachineResource(MachineDAO machineDAO, QRCodeDAO qrCodeDAO, ManufacturerDAO manufacturerDAO,
			ResellerDAO resellerDAO, CustomerDAO customerDAO, PhoneOperatorDAO phoneOperatorDAO, UserDAO userDAO) {
		this.machineDAO = machineDAO;
		this.qrCodeDAO = qrCodeDAO;
		this.manufacturerDAO = manufacturerDAO;
		this.resellerDAO = resellerDAO;
		this.customerDAO = customerDAO;
		this.phoneOperatorDAO = phoneOperatorDAO;
		this.userDAO = userDAO;
	}
	
	@GET
	@UnitOfWork
	@Path("/{machineId}")
	public Response getMachineById(@Auth User authUser, @PathParam("machineId") long machineId) {
		try {
			log.info(" In getMachineById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getById(machineId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machine")).build();
		}
	}
	
	@PUT
	@UnitOfWork
	@Path("/{machineId}")
	public Response updateMachine(@Auth User authUser, Machine machine, @PathParam("machineId") long machineId) {
		try {
			log.info(" In updateMachine");
			if (authUser == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
						.build();
			}

			QRCode qrCode = null;
			Machine existingMachine = machineDAO.getById(machineId);
			if (existingMachine != null) {

				if (machine.getQr_code_id() != 0 && machine.getQr_code_id() != existingMachine.getQr_code_id()) {
					qrCode = qrCodeDAO.getById(machine.getQr_code_id());
					if (qrCode == null) {
						log.info("This is not valid AttendMe QR code");
						return Response.status(Status.BAD_REQUEST)
								.entity(JsonUtils.getErrorJson("This is not valid AttendMe QR code")).build();
					} else {
						log.info("This is valid AttendMe QR code");
						if (qrCode.getStatus().equals(QRCode.QRCODE_STATUS.ASSIGNED.toString())) {
							log.severe("This QR code is not available.");
							return Response.status(Status.BAD_REQUEST)
									.entity(JsonUtils.getErrorJson("This QR code is not available")).build();
						} else {
							log.info("QR code has been assigned to machine");
							existingMachine.setQr_code_id(machine.getQr_code_id());
						}
					}

				} else {
					existingMachine.setQr_code_id(machine.getQr_code_id());
				}
				existingMachine.setName(machine.getName());
				existingMachine.setSerial_number(machine.getSerial_number());
				existingMachine.setModel_number(machine.getModel_number());
				existingMachine.setDescription(machine.getDescription());
				existingMachine.setMachine_year(machine.getMachine_year());
				existingMachine.setManufacturer_id(machine.getManufacturer_id());
				existingMachine.setReseller_id(machine.getReseller_id());
				existingMachine.setInstallation_date(machine.getInstallation_date());
				existingMachine.setWarranty_expiry_date(machine.getWarranty_expiry_date());
				existingMachine.setLocation_id(machine.getLocation_id());
				existingMachine.setSupport_assistance(machine.getSupport_assistance());
				existingMachine.setCur_amc_type(machine.getCur_amc_type());
//				existingMachine.setCur_amc_startdate(machine.getCur_subscription_startdate());
				existingMachine.setCur_amc_enddate(machine.getCur_amc_enddate());
				existingMachine.setCur_amc_status(machine.getCur_amc_status());
//				existingMachine.setCur_subscription_types(machine.getCur_subscription_types());
//				existingMachine.setCur_subscription_startdate(machine.getCur_subscription_startdate());
//				existingMachine.setCur_subscription_enddate(machine.getCur_amc_enddate());
//				existingMachine.setCur_subscription_status(machine.getCur_subscription_status());

				Machine newMachine = machineDAO.save(existingMachine);
				if (newMachine != null) {
					if (qrCode != null) {
						qrCode.setStatus(QRCode.QRCODE_STATUS.ASSIGNED.toString());
						qrCodeDAO.save(qrCode);
					}
					log.info("Machine successfully updated.");
					return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Machine updated successfully"))
							.build();

				} else {
					log.info("Machine not updated.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine not updated"))
							.build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine"))
						.build();
			}
		} catch (Exception e) {
			log.severe("Unable to update machine" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update machine"))
					.build();
		}
	}
	

}
