'use strict';

angular.module('sparkonixWebApp').controller('manageManResController',
		manageManResController);

function manageManResController($scope, $state, restAPIService, dialogs,
		$rootScope) {
	
	// ------------- PUBLIC VARIABLES ----------------
	$scope.activeTabNumber = 1;
	$scope.manufacturers = [];
	$scope.resellers = [];
	$scope.parent = true;

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getAllManRes();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getAllManRes() {
		var promise1;
		if ($rootScope.user.role == "SALESTEAM"
				|| $rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			promise1 = restAPIService.companyDetailsByOnBoarded(
					$rootScope.user.id, $rootScope.user.role, "MANUFACTURER")
					.query();

		} else if ($rootScope.user.role == "SUPERADMIN") {
			promise1 = restAPIService.companyDetailsByCompanyTypeResource(
					"MANUFACTURER").query();
		}
		promise1.$promise.then(function(response) {
			$scope.manufacturers = response;
			console.log("1 Manufacturer if ---->", $scope.manufacturers);
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
		// get resellers
		var promise2;
		console.log("ROLE--",$rootScope.user.role);
		if ($rootScope.user.role == "SALESTEAM"
				|| $rootScope.user.role == "MANUFACTURERADMIN"
				|| $rootScope.user.role == "RESELLERADMIN") {
			console.log("HERE-------------");
			promise2 = restAPIService.companyDetailsByOnBoarded(
					$rootScope.user.id, $rootScope.user.role, "RESELLER")
					.query();
			console.log("Response of reseller-->",promise2);

		} else if ($rootScope.user.role == "SUPERADMIN") {
			promise2 = restAPIService.companyDetailsByCompanyTypeResource(
					"RESELLER").query();
		}
		promise2.$promise.then(function(response) {
			$scope.resellers = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.addNewManRes = function(role) {
		$scope.parent = false;
		$scope.mode = "add";
		$scope.isRequired = true;
		if(role == 1){
			$scope.role = "MANUFACTURERADMIN";
		}else{
			$scope.role = "RESELLERADMIN";
		}
		$state.go('home.managemanres.addmanres');
	}

	//This for edit
	$scope.editManRes = function(manResId, companyT) {
		$scope.parent = false;
		$scope.mode = "edit";
		$scope.isRequired = false;
		$scope.ngDisabled = false;
		$scope.companyType = companyT;
		$scope.manResId = manResId;
		$state.go('home.managemanres.addmanres');
	}

	$scope.deleteManufacturer = function(manResId){
		
	}
	
	$scope.viewManRes = function(manResId,companyT) {
		$scope.parent = false;
		$scope.mode = "view";
		$scope.manResId = manResId;
		$scope.companyType = companyT;
		console.log("call from manager controller");
		$state.go('home.managemanres.viewmanres');	
	}
	
	$scope.setTab = function(tabId) {
		$scope.activeTabNumber = tabId;
	};

	$scope.isSet = function(tabId) {
		return $scope.activeTabNumber === tabId;
	};

	if ($rootScope.tabValueManRes != undefined) {
		$scope.setTab($rootScope.tabValueManRes);
	}

	
}
