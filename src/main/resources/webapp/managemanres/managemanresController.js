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
//	getAllManRes();
	getAllManufacturer();// Sparkonix v2
	getAllReseller();
	// ------------- PRIVATE FUNCTIONS ----------------
	/*Sparkonix v2
	 *  To find a manufacturer only by who is created
	 */
		function getAllManufacturer(){
		var promise1;
		console.log("1 )User Details---->",$rootScope.user);

		/*if ($rootScope.user.user_role_id == 2 // SALESTEAM 
				|| $rootScope.user.user_role_id == 3 //MANUFACTURERADMIN
				|| $rootScope.user.user_role_id == 4) {//RESELLERADMIN
			//here onboarded by id and role id and company type 
			promise1 = restAPIService.manufacturerDetailsByOnBoarded(
					$rootScope.user.id, $rootScope.user.user_role_id, "MANUFACTURER")
					.query();
		} else if ($rootScope.user.user_role_id == 1) {//SUPERADMIN
*/			console.log("In the if case of if-->");
			promise1 = restAPIService.manufacturerDetailsByCompanyTypeResource(
					"MANUFACTURER").query();
			console.log("In the else if promise 1-->",promise1);
		/*}*/
		promise1.$promise.then(function(response) {
			console.log("2In the if case of if-->",response);
			$scope.manufacturers = response;
		}, function(error) {
			console.log("errrrrrrr>",error);
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	
		/*Sparkonix v2
		 *  To find a manufacturer only by who is created
		 */
	function getAllReseller(){
		var promise2;
		console.log(" getAllReseller ROLE Id--",$rootScope.user.user_role_id);
		if ($rootScope.user.user_role_id == 2 // SALESTEAM 
				|| $rootScope.user.user_role_id == 3 //MANUFACTURERADMIN
				|| $rootScope.user.user_role_id == 4) {//RESELLERADMIN
			console.log("HERE-------------");
			promise2 = restAPIService.manufacturerDetailsByOnBoarded(
					$rootScope.user.id, $rootScope.user.id, "RESELLER")
					.query();
			console.log("Response of reseller-->",promise2);

		} else if ($rootScope.user.user_role_id == 1) {//SUPERADMIN
			promise2 = restAPIService.manufacturerDetailsByCompanyTypeResource(
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
	
	//old version method
	function getAllManRes() {
		var promise1;
		if ($rootScope.user.user_role_id == 2 // SALESTEAM 
				|| $rootScope.user.user_role_id == 3 //MANUFACTURERADMIN
				|| $rootScope.user.user_role_id == 4) {//RESELLERADMIN
			//here onboarded by id and role id and company type 
			promise1 = restAPIService.companyDetailsByOnBoarded(
					$rootScope.user.id, $rootScope.user.user_role_id, "MANUFACTURER")
					.query();
		} else if ($rootScope.user.role == "SUPERADMIN") {
			promise1 = restAPIService.companyDetailsByCompanyTypeResource(
					"MANUFACTURER").query();
		}
		promise1.$promise.then(function(response) {
			$scope.manufacturers = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
		// get resellers
		var promise2;
		console.log("ROLE--",$rootScope.user.role);
		if ($rootScope.user.user_role_id == 2 // SALESTEAM 
				|| $rootScope.user.user_role_id == 3 //MANUFACTURERADMIN
				|| $rootScope.user.user_role_id == 4) {//RESELLERADMIN
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
		if(role == 1){ // HERE ONE MENAS A MANUFACTURER ADMIN BEACUSE IT ONLY FOR DIFFERENTIATE.
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
