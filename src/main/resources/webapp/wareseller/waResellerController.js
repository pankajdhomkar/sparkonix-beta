'use strict';

angular.module('sparkonixWebApp').controller('waResellerController',
		waResellerController);

function waResellerController($scope, $state, restAPIService, dialogs,
		$rootScope,$http) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.activeTabNumber = 1;
	$scope.manufacturers = [];
	$scope.resellers = [];
	$scope.parent = true;
	$scope.companyDetailData = [];
	$scope.totalCustomers = 0;
	$scope.totalMachines = 0;
	$scope.totalLocations = 0;
	$scope.totalOperators = 0;
	$scope.machines = [];
	$scope.complaints = []; // Added for store a complaints response in this array list 
	$scope.viewComplaint = {};
	$scope.filter = 0; // Whole data will be put into a excel sheet 
	
	getAllManRes();
	getComplaints();
	
	// ------------- PUBLIC FUNCTIONS ----------------
	
	// This for customers of particular reseller
	$scope.changedValue = function(item) {
		console.log("Clicked---");
		getCustomerDetails(item);
	}

	// this method when complaint click then pop up
	$scope.viewDetails = function(complaintObj) {
		$scope.viewComplaint = complaintObj;
		console.log("TEST--",$scope.viewComplaint);
		$('#viewIssueDetails').modal().show();
	}
	
	$scope.onSubmitSearchForm = function(){
		var count = angular.element("#customerD")[0].value;
		$scope.id = count;
		console.log("Cust id------",count);
		
		$scope.complaints.length = 0;
//		console.log("Length of complaint table-->",$scope.complaints);
		if($scope.id != 0){
			var promise2;
			var supportAssistance = 2; // Reseller Assistance 
//			here we send a manufacturer id and send a support assistance for machine
			promise2 = restAPIService.companyComplaintsByCustomerId(
					$scope.id, supportAssistance).query();
			console.log("Response of Complaints-->", promise2);
			promise2.$promise.then(function(response) {
				$scope.complaints = response;
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}
	
	$scope.onResetSearchForm = function(){
		$scope.complaints.length = 0;
		$scope.companyDetailData.length = 0;
		getComplaints();
		console.log("Reset is click and method call");
		getAllManRes();
	}
	
	// This method add new Reseller
	$scope.addNewRes = function() {
		$scope.parent = false;
		$scope.mode = "add";
		$scope.isRequired = true;
		$scope.role = "RESELLERADMIN";
		console.log("call from ---------r controller");
		$state.go('home.addreseller', {'isRequired': $scope.isRequired,
			'role': $scope.role,
			'parent': $scope.parent,
			'mode': $scope.mode,
			'manResId' : null,
			'companyType' : 4});
		console.log("call fjjjjcontroller");
	}

	// This method edit a existing reseller
	$scope.editRes = function(manResId, companyT) {
		$scope.mode = "edit";
		$scope.parent = false;
		$scope.isRequired = false;
		$scope.ngDisabled = false;
		$scope.companyType = companyT; //  here type is reseller = 4
		$scope.manResId = manResId;
		$state.go('home.addreseller', {'isRequired': $scope.isRequired,
			'role': $scope.role,
			'parent': $scope.parent,
			'mode': $scope.mode,
			'manResId' : $scope.manResId,
			'companyType' : $scope.companyType});
	}

	// This method will view the existing reseller
	$scope.viewRes = function(resellerId, roleType) { // here role type is reseller = 4
		$scope.parent = false;
		$scope.mode = "view";
		$scope.manResId = resellerId;
		$scope.companyType = roleType;
		console.log("call from manager controller");
		$state.go('home.viewreseller', { 'parent' : $scope.parent,
		'mode' : $scope.mode,
		'manResId' : $scope.manResId,
		'companyType' : $scope.companyType });
		console.log("Edit state after call");
	}
	
	// This method for get customer location from the db
	$scope.getLocation = function(customer){
		$scope.customer = customer;
		var promise;
		promise = restAPIService.companyLocationsByCustomerId(
				$scope.customer.id).query();
		
		promise.$promise.then(function(response) {
			$scope.factoryLocations = response;
			$('#viewLocation').modal().show();
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	
	// This method for get machine information from a customer id and its name
	$scope.getMachine = function(customer){
		$scope.customer = customer;
			var promise;
				promise = restAPIService.machinesByCustomerId($scope.customer.id).query();
				promise.$promise.then(
					function(response) {
						$scope.machines = response;
						console.log("Here---1--machine",response);
					}, function(error) {
						dialogs.error("Error", error.data.error, {
							'size' : 'sm'
						});
					});
	}
	
	// This for tab selected
	$scope.setTab = function(tabId) {
		$scope.activeTabNumber = tabId;
	};

	// This is set a new tab id when tab was clicked
	$scope.isSet = function(tabId) {
		return $scope.activeTabNumber === tabId;
	};

	if ($rootScope.tabValueManRes != undefined) {
		$scope.setTab($rootScope.tabValueManRes);
	}

	$scope.getIdofMan = function(tabId) {
		console.log(angular.element("#w")[0].value);
		/*
		 * var count = angular.element("#w")[0].value; $scope.id =
		 * $scope.resellers[count].id;
		 */
		// getListResellerofManufacturer($scope.id);
		// console.log("Id of resellers--".$scope.id);
	}
	
	// ------------- PRIVATE FUNCTIONS ----------------
	function getAllManRes() {
		// get resellers
		$scope.resellers = {};
		var promise2;
		// Change the url link Sparkonix v2
		promise2 = restAPIService.companyDetailsResllerByManufactrer(
				$rootScope.user.manufacturer_id).query();
		console.log("1-Response of reseller-->", promise2);
		promise2.$promise.then(function(response) {
			$scope.resellers = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	// this function return the complaints that are handle by resellers
	//Here only current complaint will be shown.
	function getComplaints(){
		$scope.complaints.length = 0;
		var supportAssistance = 2; // Supported by reseller Sparkonix v2
		var promise2;
		/*here we send a manufacturer id and send a support assistance for machine Sparkonix v2 */
		promise2 = restAPIService.companyComplaints(
				$rootScope.user.companyDetailsId, supportAssistance).query(); 
		console.log("Response of Complaints-->", promise2);
		promise2.$promise.then(function(response) {
			$scope.complaints = response;
			
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	
	// This for customer details helping methods
	function getCustomerDetails(item) {
		console.log("2 --->",item);
		var promise;
		if(item != 0){
			promise = restAPIService.companyDetailsCustomerByResllerId(item).query();
			promise.$promise.then(function(response) {
				$scope.companyDetailData = response;
				console.log("Response of method --->",$scope.companyDetailData);
				getTotalCounts();
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}
	
	// This method for getting a counts of mahcine and location of machine and operators of every customers
	function getTotalCounts() {
		$scope.totalCustomers = $scope.companyDetailData.length;
		for (var i=0; i<$scope.companyDetailData.length; i++){
			$scope.totalMachines = $scope.totalMachines + $scope.companyDetailData[i].machinesCount;
			$scope.totalLocations = $scope.totalLocations + $scope.companyDetailData[i].factoryLocationsCount;
			$scope.totalOperators = $scope.totalOperators + $scope.companyDetailData[i].operatorsCount;
		}
	}
	
	
	// download as excel using $http post
	$scope.onDownloadAsExcel = function() {
		angular.element(document.getElementById('btnDownloadAsExcel'))[0].disabled = true;
		
		console.log("Checking filter--",$scope.filter);
			$scope.complaintSearchFilter = {};
			$scope.complaintSearchFilter.customerId = Number($scope.companyDetailData.id);
			console.log("Customer-----",Number($scope.companyDetailData.id));
			$scope.complaintSearchFilter.startDate = $scope.companyDetailData.startDate;
			$scope.complaintSearchFilter.endDate = $scope.companyDetailData.endDate;
			$scope.complaintSearchFilter.manufacturerid = Number($rootScope.user.manufacturer_id);
			console.log("ManResId---",Number($rootScope.user.companyDetailsId));
			$scope.complaintSearchFilter.roleID = 2; // Role id is Reseller(2)
	
		if($scope.complaintSearchFilter != null){
			var fileName = "complaint_list_of_manufacturer_reseller.xlsx";
			//Sparkonix v2 it will call the complaint to download a excel sheet
			var url = $rootScope.apiUrl + "complaints/listbyfilter/excel2";
			$scope.Authorization = "Basic " + btoa($rootScope.token + "1:");
			console.log("Authoriation-->",$scope.Authorization);
			$http
					.post(url, $scope.complaintSearchFilter, {
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
		
	
}