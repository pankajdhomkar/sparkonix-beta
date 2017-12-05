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
	$scope.ManResDTO = {};

	function getCompanyDetails() {
		var promise1;
		$scope.userRole = $rootScope.user.role;
		console.log("Role-"+$scope.userRole);
		if ($scope.userRole == "MANUFACTURERADMIN") {
			// use .get() to fetch single record
			// use .query() to fetch multiple record
			console.log("type===" + $rootScope.user.role);
			 
			promise1 = restAPIService.companyDetailResource(
					$rootScope.user.companyDetailsId,"MANUFACTURER").get();

			promise1.$promise.then(function(response) {
				console.log("Response print company name-->", response);
				$scope.companyDetails = response.manResDetail;				
				
				$scope.ManResDTO = response;
				
				$scope.ManResDTO.companyType = "MANUFACTURER";

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.userRole == "RESELLERADMIN") {
			
			console.log("type====" + $rootScope.user.role);
			promise1 = restAPIService.companyDetailResource(
					$rootScope.user.reseller_id, "RESELLER").get();
			promise1.$promise.then(function(response) {
				$scope.companyDetails = response.reseller;
				console.log("12Response print company name-->", $scope.companyDetails);
				$scope.ManResDTO = response;
				
				$scope.ManResDTO.companyType = "RESELLER";
				console.log("ManDTO-->",$scope.ManResDTO);
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}
	}

	$scope.onSaveCustomerSupportDetails = function() {

		var companyDetailsId = Number($rootScope.user.companyDetailsId);

		if ($rootScope.user.role == "MANUFACTURERADMIN") {
			console.log("MANUFACTURERADMIN -------------------"+$scope.ManResDTO.companyName);
			console.log("Manu variable ----------->"+$scope.ManResDTO);
			
			
			var promise = restAPIService.companyDetailResource(
					companyDetailsId, "MANUFACTURER").update(
							$scope.ManResDTO);
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
		} else {
			$scope.manufacturerName = "TEST 3";
			console.log("Reseller -------------------"+$scope.ManResDTO.companyName);
			console.log("Reseller resources-->>>", $scope.ManResDTO);
		
			var promise1 = restAPIService.companyDetailResource(companyDetailsId,"RESELLER").update($scope.ManResDTO);
				
				
				/*restAPIService.companyDetailResource(companyDetailsId, "RESELLER").update(
					$scope.ManResDTO);*/
				
				
			console.log("After Reseller -------------------");
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
