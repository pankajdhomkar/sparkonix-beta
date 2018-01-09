'use strict';

angular.module('sparkonixRestAPIs', []).factory('restAPIService',
	restAPIService, '$q');

function restAPIService($resource, $rootScope, $q) {
	$rootScope.apiUrl = "/api/";
	return {
		usersByRoleResource : usersByRoleResource,
		usersByRoleByCompanyResource : usersByRoleByCompanyResource,
		usersResource : usersResource,
		userResource : userResource,
		resetPasswordResource : resetPasswordResource,
		checkUserByUsernameAndPassword : checkUserByUsernameAndPassword,

		//-----------------New Added as Sparkonix V2----------------------------------
		companyManufacturerName : companyManufacturerName,
		customerDetailsResource : customerDetailsResource, //<--Added in Sparkonix v2 customer details save
		manufacturerDetailsList : manufacturerDetailsList, //<--- added Sparkonix v2
		resellerDetailsByManufactrer : resellerDetailsByManufactrer, //<--- added Sparkonix v2
		machineDocumentsByManufacturerResource : machineDocumentsByManufacturerResource,
		// To get the inforamtion of reseller as well as edit the reseller
		companyDetailResellerResource : companyDetailResellerResource,
		//It will add a reseller into a database 
		companyDetailsResellerResource : companyDetailsResellerResource,
		usersByRoleIDResource : usersByRoleIDResource,
		manufacturerDetailsByOnBoarded : manufacturerDetailsByOnBoarded,
		manufacturerDetailsByCompanyTypeResource : manufacturerDetailsByCompanyTypeResource,
		customerDeailsByUserIdRoleId : customerDeailsByUserIdRoleId,
		companyResellerName : companyResellerName, // To get reseller details
		getMachineQrCode : getMachineQrCode, //Getting a qrcode by qrcode id
//		complaintViewAllStatusByIssueNumberId : complaintViewAllStatusByIssueNumberId,
		//----------------------------------------------------------------------------
		// For details of company ------------------------------
		companyDetailsResource : companyDetailsResource,

		companyDetailsByCompanyTypeResource : companyDetailsByCompanyTypeResource,

		// Getting a Reseller of manufacturer's
		companyDetailsResllerByManufactrer : companyDetailsResllerByManufactrer, // <-----Added

		companyComplaints : companyComplaints, // <------ Added for getting a complaint supported by Reseller 
		companyDetailsByOnBoarded : companyDetailsByOnBoarded,
		companyDetailNameListByType : companyDetailNameListByType,
		companyDetailsManResResource : companyDetailsManResResource,
		companyDetailByCompanyTypeAndCompanyPan : companyDetailByCompanyTypeAndCompanyPan,
		companyDetailsCustomerByResllerId : companyDetailsCustomerByResllerId, // <-------Added
		// for
		// customer
		// list
		// from
		// the
		// machine
		// table
		// using
		// a
		// resellerId
		machinelistByCustomerId : machinelistByCustomerId, // <------- Added for machine list from the machine table using a resellerId
		qrcodesResource : qrcodesResource,
		checkMachineQrCode : checkMachineQrCode,
		downloadQrcodesBatch : downloadQrcodesBatch,
		generateqrcodesResource : generateqrcodesResource,
		companyLocationResource : companyLocationResource,
		companyLocationsResource : companyLocationsResource,
		companyLocationsForCompanyOnboardedBy : companyLocationsForCompanyOnboardedBy,
		companyLocationsByCustomerId : companyLocationsByCustomerId,
		machineResource : machineResource, // <------ This is for getting a machine details  using a machine id
		machinesResource : machinesResource,
		machinesByCustomerId : machinesByCustomerId,
		machinesByCustomerIdAndOnBoardedBy : machinesByCustomerIdAndOnBoardedBy,
		complaintsByCategoryResource : complaintsByCategoryResource,
		serviceHistoryByCategoryResource : serviceHistoryByCategoryResource,
		phoneOperatorResource : phoneOperatorResource,
//	not used	phoneDevicesResource : phoneDevicesResource,
//	not used	phoneDevicesByCustomerId : phoneDevicesByCustomerId,
//	not used	phoneDevicesByCustomerIdAndOnBoardedBy : phoneDevicesByCustomerIdAndOnBoardedBy,
		getAllMachinesForCompany : getAllMachinesForCompany,
		getAllMachineModelNumbers : getAllMachineModelNumbers,
		MachineAmcServiceHistoryResource : MachineAmcServiceHistoryResource,
		MachineAmcServiceHistoriesResource : MachineAmcServiceHistoriesResource,
		IssueResource : IssueResource,
		issuesResource : issuesResource,
		machineDocumentsResource : machineDocumentsResource,

		uploadBulkMachineList : uploadBulkMachineList,
		complaintsByManResRoleAndCompanyId : complaintsByManResRoleAndCompanyId,
		complaintsByTechnicianId : complaintsByTechnicianId,
		complaintByMachineId : complaintByMachineId, // Added To get the complaints by Machine ID
		companyComplaintsByCustomerId : companyComplaintsByCustomerId, // Added to get the complaints by customer id
		complaintListBySearchFilter : complaintListBySearchFilter,
		complaintListDownloadAsExcel : complaintListDownloadAsExcel,
		subscriptionReportResource : subscriptionReportResource,
		broadcastMessagesResource : broadcastMessagesResource,
		getMachineModelNumberListByManId : getMachineModelNumberListByManId,



		// ------------------------------------------------------------
		// Forgot password and reset password
		forgotPasswordLink : forgotPasswordLink, // send the reset password
		// link in email
		updateUserPassword : updateUserPassword, // update user password,
		// from forgot password
		// screen
		authenticateForgotPassLink : authenticateForgotPassLink
		// authenticate the user from token provided from resetLink Param

	}



	function usersByRoleResource(role) {
		var url = $rootScope.apiUrl + "users/" + role;
		return $resource(url);
	}
	// done changes in the method add a reseller id for technician
	function usersByRoleByCompanyResource(roleid, companyid, resellerId) {
		var url = $rootScope.apiUrl + "users/" + roleid + "/" + companyid + "/"
			+ resellerId;
		return $resource(url);
	}

	function usersResource() {
		var url = $rootScope.apiUrl + "users";
		return $resource(url);
	}

	function userResource(userid) {
		var url = $rootScope.apiUrl + "user/" + userid;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function resetPasswordResource() {
		var url = $rootScope.apiUrl + "user/resetpassword";
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	// It will check the username and password
	function checkUserByUsernameAndPassword() {
		var url = $rootScope.apiUrl + "login";
		console.log("HGEER"+url);
		return $resource(url);
	}

	//	// For manufacturer company details and reseller company details
	//	function companyDetailResource(compDetailId, companyType) {
	//		var url = $rootScope.apiUrl + "companydetail/" + compDetailId + "/"
	//			+ companyType;
	//		return $resource(url, null, {
	//			'update' : {
	//				method : 'PUT'
	//			}
	//		});
	//	}




	/*Sparkonix v2 customer information retrieve from this function */
	function customerDetailResource(customerDetailId) {
		var url = $rootScope.apiUrl + "customerdetail/" + customerDetailId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function companyDetailsResource() {
		var url = $rootScope.apiUrl + "companydetails";
		return $resource(url);
	}

	/* Sparkonix v2
	 * save the customers 
	 */
	function customerDetailsResource() {
		var url = $rootScope.apiUrl + "customerdetails";
		return $resource(url);
	}


	function companyDetailsByCompanyTypeResource(companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + companyType;
		return $resource(url);
	}

	/*Sparkonix v2 
	 * here we get the manufacturer list. 
	 */
	function manufacturerDetailsList() {
		var url = $rootScope.apiUrl + "manufacturerdetails/infomanufacturer/";
		return $resource(url);
	}



	//Sparkonix v2 get the all list of resellers by manufacturer id.
	function resellerDetailsByManufactrer(manufacturerId) {
		var url = $rootScope.apiUrl + "resellerdetails/info/" + manufacturerId;
		return $resource(url);
	}

	function companyDetailsByOnBoarded(onBoardedById, userRole, companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + onBoardedById + "/"
			+ userRole + "/" + companyType;
		return $resource(url);
	}

	function companyDetailNameListByType(companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + companyType;
		return $resource(url);
	}

	function qrcodesResource() {
		var url = $rootScope.apiUrl + "qrcodes";
		return $resource(url);
	}
	function downloadQrcodesBatch(batchName) {
		var url = $rootScope.apiUrl + "qrcodes/download/zip/" + batchName;
		return $resource(url);
	}

	function generateqrcodesResource(numCodes) {
		var url = $rootScope.apiUrl + "qrcodes/generate/" + numCodes;
		return $resource(url);
	}

	function companyLocationResource(locationId) {
		var url = $rootScope.apiUrl + "companylocation/" + locationId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function companyLocationsResource() {
		var url = $rootScope.apiUrl + "companylocations";
		return $resource(url);
	}

	function companyLocationsForCompanyOnboardedBy(companyId, onBoardedById) {
		var url = $rootScope.apiUrl + "companylocations/" + companyId + "/"
			+ onBoardedById;
		return $resource(url);
	}




	// For getting a machine list from machine table using a reseller_id
	function machinelistByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "machines/searchmachines/"
			+ customerId;
		return $resource(url);
	}

	function checkMachineQrCode(qrCode) {
		var url = $rootScope.apiUrl + "qrcode/check/" + qrCode;
		return $resource(url);
	}

	// This method for getting a machine info using mahcine id
	function machineResource(machineId) {
		var url = $rootScope.apiUrl + "machine/" + machineId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function machinesResource() {
		var url = $rootScope.apiUrl + "machines";
		return $resource(url);
	}

	function uploadBulkMachineList() {
		var url = $rootScope.apiUrl + "machines/bulkupload";
		return $resource(url);
	}

//	function getAllMachinesForCompany(companyId) {
//		var url = $rootScope.apiUrl + "machines/" + companyId;
//		return $resource(url);
//	}

	function getAllMachineModelNumbers() {
		var url = $rootScope.apiUrl + "machines/modelnumbers";
		return $resource(url);
	}

	function machinesByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "machines/all/" + customerId;
		return $resource(url);
	}

	function machinesByCustomerIdAndOnBoardedBy(customerId, onBoardedById) {
		var url = $rootScope.apiUrl + "machines/" + customerId + "/"
			+ onBoardedById;
		return $resource(url);
	}

	function complaintsByCategoryResource(category, parameter) {
		var url = $rootScope.apiUrl + "issues/" + category + "/" + parameter;
		return $resource(url);
	}

	/*function IssueResource(issueId) {
		var url = $rootScope.apiUrl + "issue/" + issueId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}*/

//	function issuesResource() {
//		var url = $rootScope.apiUrl + "issues";
//		return $resource(url);
//	}

// Get the machine service service history using a category and the id
	function serviceHistoryByCategoryResource(category, parameter) {
		var url = $rootScope.apiUrl + "machineamcservicehistories/" + category
			+ "/" + parameter;
		return $resource(url);
	}

	
//not used
	/*function phoneDevicesResource() {
		var url = $rootScope.apiUrl + "phonedevices";
		return $resource(url);
	}*/

	//not used
/*	function phoneDevicesByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "phonedevices/" + customerId;
		return $resource(url);
	}*/

	//not in used...
	/*function phoneDevicesByCustomerIdAndOnBoardedBy(customerId, onBoardedById) {
		var url = $rootScope.apiUrl + "phonedevices/" + customerId + "/"
			+ onBoardedById;
		return $resource(url);
	}*/
	function MachineAmcServiceHistoryResource(serviceId) {
		var url = $rootScope.apiUrl + "machineamcservicehistory/" + serviceId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	//Get the all machine service history
	function MachineAmcServiceHistoriesResource() {
		var url = $rootScope.apiUrl + "machineamcservicehistories";
		return $resource(url);
	}

	function machineDocumentsResource() {
		var url = $rootScope.apiUrl + "machinedocs";
		return $resource(url, null, {
			'save' : {
				method : 'POST',
				transformRequest : function(data) {
					if (data === undefined)
						return data;
					var fd = new FormData();
					angular.forEach(data, function(value, key) {
						if (value instanceof FileList) {
							if (value.length === 1) {
								fd.append(key, value[0]);
							} else {
								angular.forEach(value, function(file, index) {
									fd.append(key + '_' + index, file);
								});
							}
						} else {
							fd.append(key, value);
						}
					});
					return fd;
				},
				headers : {
					'Content-Type' : undefined,
					enctype : 'multipart/form-data'
				}
			}
		});
	}



	//	//This Method retrive a complaint using role id and company id
	//	function complaintsByManResRoleAndCompanyId(manResRole, manResId) {
	//		var url = $rootScope.apiUrl + "issues/" + manResRole + "/" + manResId;
	//		return $resource(url);
	//	}

	//	function complaintsByTechnicianId(technicianId) {
	//		var url = $rootScope.apiUrl + "issues/assignedto/" + technicianId;
	//		return $resource(url);
	//	}

	// post method will return list, so need to use isArray
	function complaintListBySearchFilter() {
		var url = $rootScope.apiUrl + "issues/listbyfilter";
		return $resource(url, null, {
			save : {
				method : 'POST',
				isArray : true
			}
		});
	}

	// Complaint retrieve through a machine id so we only see particular machine complaints
	function complaintByMachineId(machineId) {
		var url = $rootScope.apiUrl + "issues/machine/" + machineId;
		return $resource(url);
	}


	function complaintListDownloadAsExcel() {
		var url = $rootScope.apiUrl + "issues/listbyfilter/excel";
		return $resource(url, null, {
			save : {
				method : 'POST'
			},
			headers : {
				accept : 'application/vnd.ms-excel'
			},
			responseType : 'arraybuffer'
		});
	}

	function subscriptionReportResource(attendmeSubEndMonth,
		attendmeSubEndYear, warrantyExpEndMonth, warrantyExpEndYear,
		amcSubEndMonth, amcSubEndYear, onBoardedBy) {
		var url = $rootScope.apiUrl + "subscriptionreport/q?"
			+ "attendmeSubEndMonth=" + attendmeSubEndMonth
			+ "&attendmeSubEndYear=" + attendmeSubEndYear
			+ "&warrantyExpEndMonth=" + warrantyExpEndMonth
			+ "&warrantyExpEndYear=" + warrantyExpEndYear
			+ "&amcSubEndMonth=" + amcSubEndMonth + "&amcSubEndYear="
			+ amcSubEndYear + "&onBoardedBy=" + onBoardedBy;
		return $resource(url);
	}

	function broadcastMessagesResource() {
		var url = $rootScope.apiUrl + "broadcastmessages";
		return $resource(url);
	}

	function getMachineModelNumberListByManId(manufacturerId) {
		var url = $rootScope.apiUrl + "machines/modelnumbers/" + manufacturerId;
		return $resource(url);
	}

	/** Send reset password link, called from forgot password page* */
	function forgotPasswordLink() {
		var url = $rootScope.apiUrl + "user/forgotpassword";
		return $resource(url, null, {
			save : {
				method : 'POST'
			}
		});
	}

	/** update password via forgot password.* */
	function updateUserPassword() {
		var url = $rootScope.apiUrl + "user/resetpasswordsubmit";
		return $resource(url, null, {
			'update' : {
				method : 'POST'
			}
		});
	}
	function authenticateForgotPassLink(token) {
		var url = $rootScope.apiUrl + "user/resetpasswordcheck/" + token;
		return $resource(url, null, {
			save : {
				method : 'POST'
			}
		});

	}

	/*--------------SPARKONIX V2 NEW ADDED METHODS ------------------*/
	/*Sparkonix v2 manufacturer information get from the database*/
	function companyManufacturerName(manId) {
		var url = $rootScope.apiUrl + "manufacturerdetail/info/" + manId;
		return $resource(url, null, {
			'query' : {
				method : 'GET'
			}
		});
	}

	/*Sparkonix v2 manufacturer information update from this function */
	function companyManufacturerName(manId) {
		var url = $rootScope.apiUrl + "manufacturerdetail/info/" + manId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	/*  Sparkonix v2 change the url here  */
	// To get the reseller whole details
	function companyResellerName(resellerId) {
		var url = $rootScope.apiUrl + "resellerdetail/info/" + resellerId;
		return $resource(url, null, {
			'query' : {
				method : 'GET'
			}
		});
	}

	/*Sparkonix v2 reseller information update from this function */
	function companyResellerName(resellerId) {
		var url = $rootScope.apiUrl + "resellerdetail/info/" + resellerId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	//Sparkonix v2 change as manu id
	function machineDocumentsByManufacturerResource(manufacturerID) {
		var url = $rootScope.apiUrl + "machinedocs/manufacturer/"
			+ manufacturerID;
		return $resource(url);
	}

	//Sparkonix v2
	// For getting all complaints of Manufacturer who's support assistance is RESELLER
	function companyComplaints(manufacturerId, support) {
		var url = $rootScope.apiUrl + "complaints/" + manufacturerId + "/" + support;
		return $resource(url);
	}

	/*Sparkonix v2 
	 * Change is done with url 
	 * here we get the reseller info from reseller resource
	 */
	// For getting a reseller of manufacturer's
	function companyDetailsResllerByManufactrer(manufacturerId) {
		var url = $rootScope.apiUrl + "resellerdetails/info/{manufacturerId}"
			+ manufacturerId;
		return $resource(url);
	}
	//Sparkonix v2
	// For getting a customer id and its details from machine table using a
	// reseller_id
	function companyDetailsCustomerByResllerId(resellerId) {
		var url = $rootScope.apiUrl + "customerdetails/searchCustomer/"
			+ resellerId;
		return $resource(url);
	}

	//Sparkonix v2
	// For getting all complaints of Manufacturer who's support assistance is RESELLER by Customer id
	function companyComplaintsByCustomerId(customerId, support) {
		var url = $rootScope.apiUrl + "complaints/complaintby/" + customerId + "/" + support;
		return $resource(url);
	}

	/* Sparkonix v2 change the name of function and it will return company locations by customer id*/
	function companyLocationsByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "companylocations/" + customerId;
		return $resource(url);
	}


	/*Sparkonix v2 it is return the information of reseller and also edit*/
	function companyDetailResellerResource(resellerDetailId, companyType) {
		var url = $rootScope.apiUrl + "resellerdetail/editreseller/"
			+ resellerDetailId + "/" + companyType;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	/*Sparkonix v2 it is add the information of reseller*/
	function companyDetailsResellerResource() {
		var url = $rootScope.apiUrl + "resellerdetails/addreseller";
		return $resource(url);
	/*
	 * return $resource(url, null, { 'update' : { method : 'PUT' } });
	 */
	}

	/*Sparkonix v2 here it will check a pan number 
	 * if company Type is 3 means manufacturer 
	 *  company type is 4 means reseller
	 *  It is check pan number to avoid a duplication of company 
	 * 
	 */
	function companyDetailByCompanyTypeAndCompanyPan(companyType, companyPan) {
		var url = $rootScope.apiUrl + "manufacturerdetail/check/" + companyType
			+ "/" + companyPan;
		return $resource(url);
	}

	/* Sparkonix v2 
	 * We check by the role by it id
	 * Role id we compare with int value not by string
	 * 
	 */
	function usersByRoleIDResource(roleId) {
		var url = $rootScope.apiUrl + "users/" + roleID;
		return $resource(url);
	}

	/* Sparkonix v2 here it will get the all the manufacturer as well reseller
	 *	based on company type is manufacturer and Role id and onBoarded by Id
	 * 
	 */
	function manufacturerDetailsByOnBoarded(onBoardedById, userRoleId, companyType) {
		var url = $rootScope.apiUrl + "manufacturerdetails/" + onBoardedById + "/"
			+ userRoleId + "/" + companyType;
		return $resource(url);
	}

	/* Sparkonix v2 
	 * get the on comany type it will get the information of manufacturer and reseller 
	 * details
	 */
	function manufacturerDetailsByCompanyTypeResource(companyType) {
		var url = $rootScope.apiUrl + "manufacturerdetails/" + companyType;
		console.log("Root scope ----->",url);
		return $resource(url);
	}

	/* Sparkonix v2
	 * It will store the manufacturer as well as a reseller
	 */
	function companyDetailsManResResource() {
		var url = $rootScope.apiUrl + "manufacturerdetails/addmanres";
		console.log(url);
		return $resource(url);
	/*
	 * return $resource(url, null, { 'update' : { method : 'PUT' } });
	 */
	}

	/*Sparkonix v2 
	 * Get and Edit manufacturer by the its id
	 */
	function companyDetailManufacturerResource(manufacturerDetailId, companyType) {
		var url = $rootScope.apiUrl + "manufacturerdetail/editmanufacturer/"
			+ manufacturerDetailId + "/" + companyType;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	/*Sparkonix v2 
	 * Get the customer details through a user id and user role and reseller id
	 */
	function customerDeailsByUserIdRoleId(userId, userRoleId) {
		var url = $rootScope.apiUrl + "customerdetails/" + userId + "/"
			+ userRoleId;
		return $resource(url);
	}

	/* Sparkonix v2
	 *  Get the Qr-code by the qr code id
	 */
	function getMachineQrCode(qrCodeId) {
		var url = $rootScope.apiUrl + "qrcode/getbyid/" + qrCodeId;
		return $resource(url);
	}

	/* Sparkonix v2 check the complaint using role id and company id for manufacturer
	 * 
	 */
	//This Method retrive a complaint using role id and company id
	function complaintsByManResRoleAndCompanyId(roleId, companyId) {
		var url = $rootScope.apiUrl + "complaintdetails/" + roleId + "/" + companyId;
		return $resource(url);
	}

	/*
	 * Sparkonix v2 get the complaints for technician logged in
	 */
	function complaintsByTechnicianId(technicianId) {
		var url = $rootScope.apiUrl + "complaintdetails/assignedto/" + technicianId;
		return $resource(url);
	}
	
	/*
	 * Sparkonix v2 get the complaints for Super admin logged in
	 */
	function issuesResource() {
		var url = $rootScope.apiUrl + "complaintdetails";
		return $resource(url);
	}
	
	/*
	 * Sparkonix v2 get the machine list for service request machine
	 */
	function getAllMachinesForCompany(companyId) {
		var url = $rootScope.apiUrl + "machines/" + companyId;
		return $resource(url);
	}
	
	/*
	 * Sparkonix v2 update the record of complaint and issue number here technician assigned 
	 */
	function IssueResource(issueId) {
		var url = $rootScope.apiUrl + "complaintdetail/" + issueId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	/*
	 * Sparkonix v2
	 */
	function phoneOperatorResource(phoneOperatorId) {
		var url = $rootScope.apiUrl + "phoneoperator/" + phoneOperatorId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}


/*---------------------------------------------------------------*/
}