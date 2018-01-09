'use strict';

angular.module('sparkonixWebApp').controller('manageCustomersController',
		manageCustomersController);

function manageCustomersController($scope, $state, restAPIService, $rootScope,
		dialogs) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.parent = true;
	$scope.companyDetailData = [];
	$scope.totalCustomers = 0;
	$scope.totalMachines = 0;
	$scope.totalLocations = 0;
	$scope.totalOperators = 0;

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getCustomerDetails();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getCustomerDetails() {
		var promise;
		console.log("Customer-->",$rootScope.user.id);
		console.log("resellerID -->",$rootScope.user.resellerId);
		console.log( "role--->",$rootScope.user.role);
//		promise = restAPIService.companyDetailsByOnBoarded($rootScope.user.id,
//				$rootScope.user.role, "CUSTOMER", $rootScope.user.resellerId).query();
		//Sparkonix 2V
		//here it will get all the customer information
		promise = restAPIService.customerDeailsByUserIdRoleId($rootScope.user.id, $rootScope.user.userid).query();
		promise.$promise.then(function(response) {
			$scope.companyDetailData = response;
			console.log("List-->",$scope.companyDetailData);
			getTotalCounts();
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	
	function getTotalCounts() {
		$scope.totalCustomers = $scope.companyDetailData.length;
		for (var i=0; i<$scope.companyDetailData.length; i++){
			$scope.totalMachines = $scope.totalMachines + $scope.companyDetailData[i].machinesCount;
			$scope.totalLocations = $scope.totalLocations + $scope.companyDetailData[i].factoryLocationsCount;
//			$scope.totalOperators = $scope.totalOperators + $scope.companyDetailData[i].operatorsCount;
		}
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.addNewCustomer = function() {
		$scope.parent = false;
		$scope.mode = "add";
		$state.go('home.managecustomers.addcustomer');
	}

	$scope.editCustomer = function(customer, setTabId) {
		$scope.parent = false;
		$scope.mode = "edit";
		$scope.customer = customer;
		$scope.setTabId = setTabId;
		$state.go("home.managecustomers.addcustomer");
	}
}
