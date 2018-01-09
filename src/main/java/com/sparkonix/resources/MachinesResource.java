package com.sparkonix.resources;

import java.io.IOException;
import java.util.ArrayList;
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

import com.google.zxing.WriterException;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.ComplaintDetailDAO;
import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineDetailsDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machines")
@Produces(MediaType.APPLICATION_JSON)
public class MachinesResource {
	private final MachineDAO machineDAO;
	private final QRCodeDAO qrCodeDAO;
	@SuppressWarnings("unused")
	private final ComplaintDetailDAO complaintDetailDAO;
	
	
	
	private final ManufacturerDAO manufacturerDAO;
	private final ResellerDAO resellerDAO;
	@SuppressWarnings("unused")
	private final CustomerDAO customerDAO;
	private final CompanyLocationDAO companyLocationDAO;
	
	private final Logger log = Logger.getLogger(MachinesResource.class.getName());

	public MachinesResource(MachineDAO machineDAO, QRCodeDAO qrCodeDAO, ComplaintDetailDAO complaintDetailDAO,
			ManufacturerDAO manufacturerDAO, ResellerDAO resellerDAO,
			CustomerDAO customerDAO, CompanyLocationDAO companyLocationDAO) {
		this.machineDAO = machineDAO;
		this.qrCodeDAO = qrCodeDAO;
		this.complaintDetailDAO = complaintDetailDAO;
		this.manufacturerDAO = manufacturerDAO;
		this.resellerDAO = resellerDAO;
		this.customerDAO = customerDAO;
		this.companyLocationDAO = companyLocationDAO;
	}
	
	@POST
	@UnitOfWork
	public Response createMachine(@Auth User authUser, Machine machine) {

		try {
			if (machine.getQr_code_id() != 0) {
				QRCode qrCode = qrCodeDAO.getById(machine.getQr_code_id());
				if (qrCode == null) {
					log.info("This is not valid AttendMe QR code");
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("This is not valid AttendMe QR code")).build();
				} else {
					log.info("This is valid AttendMe QR code");
					System.out.println("Status -------------->"+qrCode.getStatus());
					if (qrCode.getStatus().equals(QRCode.QRCODE_STATUS.ASSIGNED.toString())) {
						log.severe("This QR code is not available.");
						return Response.status(Status.BAD_REQUEST)
								.entity(JsonUtils.getErrorJson("This QR code is not available")).build();
					} else {
						System.out.println("Before machine save------"+machine.getId());
						
						Machine newMachine = machineDAO.save(machine);
						
						System.out.println("machine save------"+newMachine.getId());
						System.out.println("id-1 id -----"+newMachine.getId());
						System.out.println("id-2 customer id-----"+newMachine.getCustomer_id());
						System.out.println("id-3 manu id-----"+newMachine.getManufacturer_id());
						System.out.println("id-4 reseller id-----"+newMachine.getReseller_id());
						System.out.println("id-5 location id-----"+newMachine.getLocation_id());
						
						log.info("QR code has been assigned to machine");
						// update qr code as assigned
						qrCode.setStatus(QRCode.QRCODE_STATUS.ASSIGNED.toString());
						qrCodeDAO.save(qrCode);
						log.info("New machine successfully added.");
						return Response.status(Status.OK).entity(JsonUtils.getJson(newMachine)).build();
					}
				}
			} else {
				log.severe("QR code is null");
				// add machine without QR code
				
				
				Machine newMachine = machineDAO.save(machine);
				
				System.out.println("id-1 id -----"+machine.getId());
							
				
				if (newMachine != null) {
					System.out.println("2-----------------------");
					log.info("New machine successfully added.");
					return Response.status(Status.OK).entity(JsonUtils.getJson(newMachine)).build();
				} else {
					System.out.println("3-----------------------");
					log.info("New machine not added.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("New machine not added"))
							.build();
				}
			}
		} catch (Exception e) {
			log.severe("Unable to add machine. Error: " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to add machine")).build();
		}
	}
	
	/*
	 * It will return the machine list according to manufacturer id and reseller id 
	 */
	@GET
	@Path("/{companyId}")
	@UnitOfWork
	public Response getAllMachinesForCompany(@Auth User authUser, @PathParam("companyId") long companyId)
			throws IOException, WriterException {
		try {
			long companyType;
			log.info("In getAllMachinesForCompany");
			if(authUser.getUser_role_id() == 3){ // For manufacturer
				companyType = 3;
			}else{// For Reseller
				companyType = 4;
			}
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getMachinesListForCompany(companyId, companyType)))
					.build();
			
		} catch (Exception e) {
			log.severe("Unable to find Machines " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machines"))
					.build();
		}
	}
	
	
	//It will return all the machine.
	@GET
	@Path("/modelnumbers")
	@UnitOfWork
	public Response getAllMachineModelNumbers(@Auth User authUser) {
		try {
			log.info("In getAllMachineModelNumbers");		 
			
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getAllMachineModelNumbers())).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines model numbers " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines model numbers")).build();
		}
	}
	
	
	//It will return the list of machine that is on specific model numbers using manufactrer id
	@GET
	@Path("/modelnumbers/{manufacturer_id}")
	@UnitOfWork
	public Response getMachineModelNumberListByManId(@Auth User authUser, @PathParam("manufacturer_id") long manufacturerId) {
		try {
			log.info("In getMachineModelNumberListByManId");		 
			
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineDAO.getAllMachineModelNumbersByManufacturerId(manufacturerId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines model numbers " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines model numbers")).build();
		}
	}
	
	//function machinesByCustomerId(customerId)  calls this method in back end 
	@GET
	@UnitOfWork
	@Path("/all/{customerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listMachines(@Auth User authUser, @PathParam("customerId") long customerId) {
		/*
		 * try { log.info("In listMachines"); return
		 * Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.
		 * findAll())).build(); } catch (Exception e) { log.severe(
		 * "Unable to find Machines " + e); return
		 * Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(
		 * "Unable to find Machines")) .build(); }
		 */

		try {
			log.info(" In listMachines");
			List<Machine> machineList = machineDAO.findAllByCustomerId(customerId);

			List<MachineDetailsDTO> machineDTOList = new ArrayList<>();

			for (int i = 0; i < machineList.size(); i++) {
				MachineDetailsDTO machineDetailsDTO = new MachineDetailsDTO();

				machineDetailsDTO.setMachineId(machineList.get(i).getId());
				machineDetailsDTO.setSerialNumber(machineList.get(i).getSerial_number());
				machineDetailsDTO.setModelNumber(machineList.get(i).getModel_number());
				machineDetailsDTO.setQrCode_id(machineList.get(i).getQr_code_id());
				machineDetailsDTO.setName(machineList.get(i).getName());
				machineDetailsDTO.setInstallationDate(machineList.get(i).getInstallation_date());
				Manufacturer manufacturerObj = manufacturerDAO.getById(machineList.get(i).getManufacturer_id());
				machineDetailsDTO.setManufacturer(manufacturerObj);

				Reseller resellerObj = resellerDAO.getById(machineList.get(i).getReseller_id());
				machineDetailsDTO.setReseller(resellerObj);

				CompanyLocation locationObj = companyLocationDAO.getById(machineList.get(i).getLocation_id());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				machineDetailsDTO.setCompanyLocation(locationObj);

				machineDTOList.add(machineDetailsDTO);
			}
			return Response.status(Status.OK).entity(machineDTOList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}
}
