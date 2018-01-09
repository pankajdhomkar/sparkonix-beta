'use strict';

angular.module('sparkonixWebApp').controller('waProfileController',
		waProfileController);

function waProfileController($scope, $rootScope, restAPIService, dialogs,
		$state) {
	$scope.companyDetails = {};
	$scope.manufacturerName = "TEST";
	$scope.resetPasswordDTO = {};
	$scope.userRole = "";
	getCompanyDetails();
	$scope.ManufacturerResellerDTO = {};

	//It will return the details of manufacturer or reseller
	function getCompanyDetails() {
		var promise1;
		$scope.userRoleId = $rootScope.user.user_role_id;
		console.log("Role-"+$scope.userRoleId);
		
		if ($scope.userRoleId == 3) { //MANUFACTURERADMIN = 3
			// use .get() to fetch single record
			// use .query() to fetch multiple record
			//Here we call the manufacturer resource(table) to get the manufacturer info			 
			promise1 = restAPIService.companyManufacturerName(
					$rootScope.user.manufacturer_id).get();

			promise1.$promise.then(function(response) {
				console.log("Response print company name-->", response);
				$scope.companyDetails = response.manufacturer;				
				
				$scope.ManufacturerResellerDTO = response;
				
//				$scope.ManufacturerResellerDTO.companyType = "MANUFACTURER";

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.userRoleId == 4) { // RESELLERADMIN = 4
			
			console.log("type====" + $rootScope.user.role);
			promise1 = restAPIService.companyResellerName(
					$rootScope.user.reseller_id).get();
			promise1.$promise.then(function(response) {
				$scope.companyDetails = response.reseller;
				console.log("Response print company name-->", $scope.companyDetails);
				$scope.ManufacturerResellerDTO = response;
				
//				$scope.ManufacturerResellerDTO.companyType = "RESELLER";
				console.log("ManDTO-->",$scope.ManufacturerResellerDTO);
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	$scope.onSaveCustomerSupportDetails = function() {

		if ($scope.userRoleId == 3) { //MANUFACTURERADMIN = 3
			var manufacturerId = Number($rootScope.user.manufacturer_id);
			console.log("Manu variable ----------->"+$scope.ManufacturerResellerDTO);
						
			var promise = restAPIService.companyManufacturerName(
					manufacturerId).update(
							$scope.ManufacturerResellerDTO);
			console.log("After manufacturer -------------------");
			promise.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getCompanyDetails();
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.userRoleId == 4) { // RESELLERADMIN = 4
			$scope.manufacturerName = "TEST 3";
			var resellerId = Number($rootScope.user.reseller_id);
			console.log("Reseller resources-->>>", $scope.ManufacturerResellerDTO);
		
			var promise1 = restAPIService.companyDetailResource(resellerId).update($scope.ManufacturerResellerDTO);
			promise1.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
				getCompanyDetails();
			}, function(error) {
				console.log("error",error);
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
		$state.go("home.waprofile");
	}

	$scope.onUpdateWebAdminPassword = function() {

		var encryptedOldPassword = $scope.md5($scope.webAdmin.oldPassword);
		$scope.webAdmin.oldPassword = encryptedOldPassword;

		var encryptedNewPassword = $scope.md5($scope.webAdmin.newPassword);
		$scope.webAdmin.newPassword = encryptedNewPassword;

		$scope.resetPasswordDTO.userId = Number($rootScope.user.id);
		$scope.resetPasswordDTO.oldPassword = $scope.webAdmin.oldPassword;
		$scope.resetPasswordDTO.newPassword = $scope.webAdmin.newPassword;

		var promise = restAPIService.resetPasswordResource().update(
				$scope.resetPasswordDTO);

		promise.$promise.then(function(response) {
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');

			dialogs.notify("Success", response.success, {
				'size' : 'sm'
			});
			getCompanyDetails();
			$state.go("home.waprofile");
			$state.reload();

		}, function(error) {
			$scope.webAdmin = '';
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	}
	
	$scope.cancelResetPassword = function(){
		console.log("1");
		$("#editWebAdminPassword").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
			$scope.resetPasswordDTO = {};
	    });	
		console.log("3");
	}
	
	$scope.cancelEditCustomer = function(){
		console.log("1");
		$("#editCustSupport").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
	    });	
		console.log("3");
	}

	$scope.onClose = function() {
		$state.go('home.wadashboard');
		// $state.reload();
	};

}
