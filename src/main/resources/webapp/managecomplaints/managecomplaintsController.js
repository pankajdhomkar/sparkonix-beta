'use strict';

angular.module('sparkonixWebApp').controller('manageComplaintsController',
		manageComplaintsController);

function manageComplaintsController($scope, $rootScope, restAPIService,
		dialogs, $state, $http) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.activeTabNumber = 1;
	$scope.complaints = []; // Here Complaints get the response of complete DTO of complaint
	$scope.services = [];
	$scope.machinesList = [];
	$scope.viewComplaint = {};
	$scope.technicianList = {};
	$scope.newServiceRequest = {};
	$scope.searchForm = {};
	$scope.displayFlag = true;
	// date-picker format
	$scope.format = 'dd-MM-yyyy';
	$scope.showtext = 'Assign';

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getComplaintsData(); // get the all complaints from the database
	getServiceHistoryList(); // this will get the servicing history from database

	// ------------- PRIVATE FUNCTIONS ----------------
	//-------------------Complaint---------------------
	function getComplaintsData() {
		var promise1;
		/* if manufacturer- 3 or reseller - 4  web admin logged in
		 * get complaint by man/res id & support_assistance
		 * 
		 */
		if ($rootScope.user.users_role_id == 3) { // For Manufacturer
			// companyDetailsId of logged in web admin
			console.log("LOGIN---->",$rootScope.user.users_role_id);
			var manResId = Number($rootScope.user.manufacturer_id);

			promise1 = restAPIService.complaintsByManResRoleAndCompanyId(
					$rootScope.user.users_role_id, manResId).query();
		}
		if($rootScope.user.role == 4){ // Reseller
			// companyDetailsId of logged in web admin
			console.log("LOGIN---->",$rootScope.user.users_role_id);
			var manResId = Number($rootScope.user.reseller_id);

			promise1 = restAPIService.complaintsByManResRoleAndCompanyId(
					$rootScope.user.users_role_id, manResId).query();
		}
		// Check if user is technician
		if ($rootScope.user.users_role_id == 6) { 
			// if technician logged in
			var technicianId = Number($rootScope.user.id);
			promise1 = restAPIService.complaintsByTechnicianId(technicianId)
			.query();
		}
		//Check if user is superadmin
		if ($rootScope.user.users_role_id == 1) {
			// if superadmin logged in
			promise1 = restAPIService.issuesResource().query();
		}

		promise1.$promise.then(function(response) {
			$scope.complaints = response;
			console.log("Complaint object --",$scope.complaints);
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	//--------------------------------------------------------
	/*
	 * Sparkonix v2 getMachine list only see a his own machine who is logged in
	 * 
	 */
	function getMachinesList() {
		var promise2;
		var manResId;
		if($rootScope.user.users_role_id == 3){ // For Manufacturers machines
			manResId = Number($rootScope.user.manufacturer_id);
		}else if($rootScope.user.users_role_id == 4){ // For resellers machines
			manResId = Number($rootScope.user.resellerId);
		}
		promise2 = restAPIService.getAllMachinesForCompany(manResId).query();
		promise2.$promise.then(function(response) {
			$scope.machineList = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getTechniciansByCompanyId() {
		// for Man/Res
		var promise;
		if($rootScope.user.users_role_id == 3){ //"MANUFACTURERADMIN"
			console.log("Manufacturer Techincian--");
			// for Manufacturer
			promise = restAPIService.usersByRoleByCompanyResource(
					// User Role id, Manufacturer Id, Reseller Id
					$rootScope.user.users_role_id, $rootScope.user.manufacturer_id, 0).query();
		} else{
			console.log("Call For Reseller Technician",$rootScope.user.companyDetailsId);
			// User Role id, Manufacturer Id, Reseller Id
			promise = restAPIService.usersByRoleByCompanyResource(
					$rootScope.user.users_role_id, 0, $rootScope.user.resellerId).query();
		}

		/*var promise1 = restAPIService.usersByRoleByCompanyResource(
				"TECHNICIAN", $rootScope.user.companyDetailsId).query();*/
		promise.$promise.then(function(response) {
			$scope.technicianList = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	/*
	 * For viewing a complaint all by issue number 
	 * 
	 */
	$scope.viewDetails = function(issueNumberId) {
		var promise;
		promise = restAPIService.complaintViewAllStatusByIssueNumberId(issueNumberId).query();
		
		$scope.viewComplaint = complaintObj;

		$('#viewIssueDetails').modal().show();
	}

	$scope.setTab = function(tabId) {
		$scope.activeTabNumber = tabId;
	};

	$scope.isSet = function(tabId) {
		return $scope.activeTabNumber === tabId;
	};

	$scope.onEditComplaint = function(complaintObj) {
		$scope.updateIssueRecord = complaintObj;
		if ($rootScope.user.users_role_id == 5) { //"TECHNICIAN"
			$('#actionTechnician').modal().show();
		} else {
			// load technnician dropdown
			getTechniciansByCompanyId();
			$('#actionWebAdmin').modal().show();

		}
	}

	$scope.textSetter = function(){
		if ($rootScope.user.users_role_id == 5) { // "TECHNICIAN"
			console.log("------TECHNICIAN--------");
			if ($scope.complaint.status == "INPROGRESS") {
				console.log("1-->", $scope.complaint.status);
				$scope.showtext = "INPROGRESS";
			} else if ($scope.complaint.status == "CLOSED") {
				console.log("2-->", $scope.complaint.status);
				$scope.showtext = "CLOSED";
			} else {
				console.log("3-->", $scope.complaint.status);
				$scope.showtext = "Change Status";
			}
		}else{
			console.log("------ADMIN--------");
			if($scope.complaint.status == "ASSIGNED"){
				console.log("1-->",$scope.complaint.status);
				$scope.showtext = "Re-Assign";
			}else{
				console.log("2-->",$scope.complaint.status);
				$scope.showtext = "Assign";
			}
		}
	}

	/*Assign a complaint to technician by Manufacturer/Reseller admin*/
	$scope.assignComplaintToTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
			'size' : 'sm'
		});
		dlg.result
		.then(
				function() {
					$scope.updateIssueRecord.assignedTo = Number($scope.updateIssueRecord.assignedTo);
					$scope.updateIssueRecord.status = "ASSIGNED";
//					here it will update the record that technician is alloted.
					var promise = restAPIService.IssueResource(
							$scope.updateIssueRecord.id).update(
									$scope.updateIssueRecord);
					promise.$promise.then(function(response) {
						getComplaintsData();
						dialogs.notify("Success", response.success, {
							'size' : 'sm'
						});
					}, function(error) {
						dialogs.error("Error", error.data.error, {
							'size' : 'sm'
						});
					});
				}, function() {
					console.log("3");
					$state.reload();
				});
	}
	/*Update the status by technician*/
	$scope.updateComplaintByTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
			'size' : 'sm'
		});
		dlg.result.then(function() {
			var promise = restAPIService.IssueResource(
					$scope.updateIssueRecord.id).update(
							$scope.updateIssueRecord);
			promise.$promise.then(function(response) {
				getComplaintsData();
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
			console.log("3");
			$state.reload();
			$scope.updateIssueRecord = {};
		});
	}
//------------------------------Complaint section end-------------
	// -------------------------Service -------------------------
	$scope.newServiceReq = function() {
		$scope.newServiceRequest = '';
		// load machine drop-down
		getMachinesList();
		// load technician drop-down
		getTechniciansByCompanyId();
		$('#addServiceRequest').modal().show();
	}

	$scope.onSaveServiceRequest = function() {
		$scope.newServiceRequest.companyId = Number($rootScope.user.companyDetailsId);
		var promise = restAPIService.MachineAmcServiceHistoriesResource().save(
				$scope.newServiceRequest);
		promise.$promise.then(function(response) {
			$scope.newTechnician = {};
			// $scope.services.push(response);
			getServiceHistoryList();

			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');
			dialogs.notify("Success", "New servie request added successfully",
					{
				'size' : 'sm'
					});
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function getServiceHistoryList() {
		var category; // here category use as role.
		var parameter;
		var promise2;

		if ($rootScope.user.users_role_id == 3){ //"MANUFACTURERADMIN"
			category = "manufacturer";
			parameter = $rootScope.user.manufacturer_id;
		}
		
		if($rootScope.user.users_role_id == 4) { //"RESELLERADMIN"
			category = "reseller";
			parameter = $rootScope.user.resellerId;
		}
		
		if ($rootScope.user.users_role_id == 5) { //"TECHNICIAN"
			category = "technician";
			parameter = $rootScope.user.id;
		}

		if ($rootScope.user.users_role_id == 1) { //"SUPERADMIN"
			promise2 = restAPIService.MachineAmcServiceHistoriesResource()
			.query();
		} else {
			promise2 = restAPIService.serviceHistoryByCategoryResource(
					category, parameter).query();
		}

		promise2.$promise.then(function(response) {
			$scope.services = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	$scope.onEditServiceRequest = function(service) {
		$scope.updatedRecord = service;
		if ($rootScope.user.role == "TECHNICIAN") {
			$('#editServiceRequestByTechnician').modal().show();
		} else if ($rootScope.user.role == "MANUFACTURERADMIN"
			|| $rootScope.user.role == "RESELLERADMIN") {
			// load machine drop-down
			getMachinesList();
			// load technician drop-down
			getTechniciansByCompanyId();

			$('#editServiceRequest').modal().show();
		}

	}

	$scope.assignServiceRequestToTechnician = function() {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this record?", {
			'size' : 'sm'
		});
		dlg.result.then(function() {
			var promise = restAPIService.MachineAmcServiceHistoryResource(
					$scope.updatedRecord.id).update($scope.updatedRecord);
			promise.$promise.then(function(response) {
				$scope.setTab(2);
				getServiceHistoryList();
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
			console.log("3");
			$state.reload();
		});
	}

	$scope.onCancelRegularService = function() {
		$scope.setTab(2);
		$state.reload();
		getServiceHistoryList();
	};

	//cancel Technician change modal
	$scope.cancelComplaintByTechnician = function() {
		console.log("1");
		$("#actionTechnician").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
			$scope.updateIssueRecord = {};
		});	
		console.log("3");
	}

	// admin to technician cancel.
	$scope.cancelActionWebAdmin = function() {
		console.log("1");
		$("#actionWebAdmin").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
			$scope.updateIssueRecord = {};
		});	
		console.log("3");
	}


	$scope.onResetSearchForm = function() {
		$scope.searchForm = '';
		$state.reload();
		$state.reload();
	};
	$scope.onSubmitSearchForm = function() {

		var promise1;
		if ($rootScope.user.users_role_id == 4 // "MANUFACTURERADMIN"
			|| $rootScope.user.role == "RESELLERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResId = Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;

			promise1 = restAPIService.complaintListBySearchFilter().save(
					$scope.complaintSearchFilter);
		}
		if ($rootScope.user.role == "SUPERADMIN") {
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
			promise1 = restAPIService.complaintListBySearchFilter().save(
					$scope.complaintSearchFilter);
		}

		promise1.$promise.then(function(response) {
			$scope.complaints = response;
			console.log("Complaint Resonse-->",$scope.complaints);
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	};

	// download as excel using $http post
	$scope.onDownloadAsExcel = function() {
		angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = true;

		if ($rootScope.user.role == "MANUFACTURERADMIN"
			|| $rootScope.user.role == "RESELLERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			$scope.complaintSearchFilter.manResId = Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
		}
		if ($rootScope.user.role == "SUPERADMIN") {
			// companyDetailsId of logged in web admin
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.searchForm.customer);
			$scope.complaintSearchFilter.startDate = $scope.searchForm.startDate;
			$scope.complaintSearchFilter.endDate = $scope.searchForm.endDate;
			// $scope.complaintSearchFilter.manResId =
			// Number($rootScope.user.companyDetailsId);
			$scope.complaintSearchFilter.manResRole = $rootScope.user.role;
		}

		var fileName = "complaint_list.xlsx";
		var url = $rootScope.apiUrl + "issues/listbyfilter/excel";
		$scope.Authorization = "Basic " + btoa($rootScope.token + "1:");

		$http
		.post(url, $scope.complaintSearchFilter,{ 
			responseType : 'arraybuffer',
			'Authorization' : $scope.Authorization
		})
		.success(
				function(data) {
					angular.element(document
							.getElementById('btnDownloadAsExcel'))[0].disabled = false;
					var file = new Blob([ data ], {
						type : 'application/octet-stream'
					});
					saveAs(file, fileName);
				})
				.error(
						function(data, status) {
							angular.element(document
									.getElementById('btnDownloadAsExcel'))[0].disabled = false;
							dialogs.error("Error", error.data.error, {
								'size' : 'sm'
							});
						});
	};
}
