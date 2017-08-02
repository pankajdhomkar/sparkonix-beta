'use strict';

angular.module('sparkonixWebApp').controller('manageTechniciansController',
		manageTechniciansController);

function manageTechniciansController($scope, $rootScope, $state,
		restAPIService, dialogs) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.technicians = [];
	$scope.newTechnician = {};
	$scope.editTechnician = {};
	$scope.newTechnician = {
		name : "",
		email : "",
		altEmail : "",
		mobile : "",
		password : "",
		role : "",
		reseller_id : ""
	};

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getTechnicians();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getTechnicians() {
		var promise;

		if ($rootScope.user.role == "SUPERADMIN") {
			promise = restAPIService.usersByRoleResource(
					"TECHNICIAN").query();
		} else if($rootScope.user.role == "MANUFACTURERADMIN"){
			console.log("Manufacturer Techincian--");
			// for Manufacturer
			promise = restAPIService.usersByRoleByCompanyResource(
					$rootScope.user.role, $rootScope.user.companyDetailsId, 0).query();
		} else if($rootScope.user.role == "RESELLERADMIN"){
			console.log("Call For Reseller Technician",$rootScope.user.companyDetailsId);
			promise = restAPIService.usersByRoleByCompanyResource(
					$rootScope.user.role, 0, $rootScope.user.companyDetailsId).query();
		}

		// var promise = restAPIService.usersByRoleResource("TECHNICIAN").query();

		promise.$promise.then(function(response) {
			$scope.technicians = response;
			console.log("TEc-",$scope.technicians);
		/*	if($scope.technicians.altEmail == null){
				$scope.technicians.alt_Email = " ";
			}*/
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.addNewTechnician = function() {

		$scope.newTechnician.password = CryptoJS.MD5(
				$scope.newTechnician.password).toString();
		$scope.newTechnician.role = "TECHNICIAN";
		
		if($rootScope.user.role == "MANUFACTURERADMIN"){
			console.log("Here1--",$rootScope.user.role);
			$scope.newTechnician.companyDetailsId = Number($rootScope.user.companyDetailsId);
			$scope.newTechnician.reseller_id = 0;
		}else if($rootScope.user.role == "RESELLERADMIN"){
			console.log("Here2--",$rootScope.user.role);
			$scope.newTechnician.companyDetailsId = 0;
			$scope.newTechnician.reseller_id = $rootScope.user.companyDetailsId;
		}
		console.log("Here3--",$scope.newTechnician);
		var promise = restAPIService.usersResource().save($scope.newTechnician);
		promise.$promise.then(function(response) {
			console.log("REsponse",response);
			$scope.technicians.push(response);
			$scope.newTechnician = {};
			$('#addTechnician').hide();
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');
			dialogs.notify("Success", "Added new technician successfully", {
				'size' : 'sm'
			});
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	$scope.updateTechnician = function() {
		console.log("1");
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to update this technician?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			console.log("2--->",$scope.editTechnician);
			
			var promise = restAPIService.userResource($scope.editTechnician.id).update($scope.editTechnician);
			console.log("33--->");
			promise.$promise.then(function(response) {
				var index = $scope.technicians.indexOf($scope.editTechnician);
				$scope.technicians.splice(index, 1);
				$scope.technicians.splice(index, 0, response);
				dialogs.notify("Success", "Details updated successfully", {
					'size' : 'sm'
				});
			}, function(error) {
				console.log("in error boz"+error);
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() { // this function for click on NO in the 2nd modal
			console.log("3");
			$state.reload();
		});
	}
	
	$scope.cancelUpdateTechnician = function(){
		console.log("1");
		$("#editTechnician").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
	    });	
		console.log("3");
	}
	
	$scope.cancelAddTechnician = function(){
		console.log("1");
		$("#addTechnician").on('hidden.bs.modal', function () {	
			console.log("2");
			$state.reload();
			$scope.newTechnician={};
	    });	
		console.log("3");
	}
	
	$scope.onEdit = function(technician) {
		
		$scope.editTechnician = technician;
		console.log("on Edit function ---",technician);
		console.log("scope of edit technician--  ",$scope.editTechnician)
		/*if(technician.altEmail != "" || technician.altEmail != undefined){
			$scope.editTechnician.altEmail= technician.altEmail;
		}else{
			$scope.editTechnician.altEmail="";
		}
		
		$scope.editTechnician.companyDetailsId =technician.companyDetailsId;
		$scope.editTechnician.email = technician.email;
		$scope.editTechnician.id =technician.id;
		$scope.editTechnician.mobile =technician.mobile;
		$scope.editTechnician.name =technician.name;
		$scope.editTechnician.password =technician.password;
		$scope.editTechnician.reseller_id =technician.reseller_id;
		$scope.editTechnician.role =technician.role;
		*/
		$scope.editTechnician.mobile = technician.mobile;
//		$scope.editTechnician.mobile = Number($scope.editTechnician.mobile);
		$('#editTechnician').modal().show();
	}

	$scope.onDelete = function(technician) {
		var dlg = dialogs.confirm("Are you sure?",
				"Are you sure you want to delete this technician?", {
					'size' : 'sm'
				});
		dlg.result.then(function() {
			var promise = restAPIService.userResource(technician.id).remove();
			promise.$promise.then(function(response) {
				var index = $scope.technicians.indexOf(technician);
				$scope.technicians.splice(index, 1);
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});
			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		}, function() {
			$state.reload();
		});
	}
}
