'use strict';

angular.module('sparkonixWebApp').controller(
		'manageMachineDocumentsController', manageMachineDocumentsController);

function manageMachineDocumentsController($scope, $rootScope, restAPIService,
		dialogs, $state) {
	// ------------- PUBLIC VARIABLES ----------------
	$scope.documents = [];
	$scope.newDocument = {};
	$scope.manufacturers = [];
	$scope.fileList = [];
	$scope.machineModelNumbers = [];
	$scope.downloadFilePath = "/api/machinedoc/fetch";

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ----------------
	getAllDocuments();
	getAllMachineModelNumbers();

//	changed here a if case that is on the basis of id 3 = manufacturer admin
	if ($rootScope.user.user_role_id != 3) //"MANUFACTURERADMIN"
		getAllManufacturers();

	// ------------- PRIVATE FUNCTIONS ----------------
	function getAllDocuments() {
		var promise1;
		if ($rootScope.user.user_role_id == 3) { // "MANUFACTURERADMIN"manufacturer can
			// see only his
			// documents
			$scope.colOrder = 0; // set column to be in ascending order
			promise1 = restAPIService.machineDocumentsByManufacturerResource(
					$rootScope.user.manufacturer_id).query();
		} else {
			// superadmin and reseller can see all documents
			$scope.colOrder = 1;
			promise1 = restAPIService.machineDocumentsResource().query();
		}
		promise1.$promise.then(function(response) {
			$scope.documents = response;

			$scope.machineDocByManufacturer = groupBy($scope.documents,
					'manufacturerId');

			$scope.manufacturerNames = Object
					.keys($scope.machineDocByManufacturer);

			$scope.machineDocByManufacturer = groupBy($scope.documents,
					'manufacturerId');

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// get list of all machine model numbers changes done V2
	function getAllMachineModelNumbers() {
		var promise;
		//Sparkonix v2
		if ($rootScope.user.user_role_id  == 3) { //"MANUFACTURERADMIN"
			var manufacturerId = Number($scope.user.companyDetailsId);
			promise = restAPIService.getMachineModelNumberListByManId(
					manufacturerId).query();
		} else {
			promise = restAPIService.getAllMachineModelNumbers().query();
		}

		promise.$promise.then(function(response) {
			$scope.machineModelNumbers = response;

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	function groupBy(xs, key) {
		return xs.reduce(function(rv, x) {
			(rv[x[key]] = rv[x[key]] || []).push(x);
			return rv;
		}, {});
	}
	;

//	Function change a its REST API service method this method request for a list of manufacturer
	function getAllManufacturers() {
		var promise1;
//		promise1 = restAPIService.companyDetailsByCompanyTypeResource(
//				"MANUFACTURER").query();
		promise1 = restAPIService.manufacturerDetailsList().query();
		promise1.$promise.then(function(response) {
			$scope.manufacturers = response;
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

	// ------------- PUBLIC FUNCTIONS ----------------
	$scope.onDownloadDocument = function(document) {

	}

	$scope.uploadDocument = function() {
		$('#uploadDoc').modal().show();
		$scope.newDocument = '';

	}

	$scope.onFileSelect = function() {

		if ('files' in fileLoader) {
			if (fileLoader.files.length > 0) {
				for (var i = 0; i < fileLoader.files.length; i++) {
					var file = fileLoader.files[i];
					if ('name' in file) {
						var fileToAdd = {
							fileName : file.name,
							fileContent : file,
						}
						$scope.fileList.push(fileToAdd);
						$scope.$apply();
					}
				}
			}
		}
	}
	
	$scope.startUpload = function() {

		if (document.getElementById("fileLoader").value != "") {

			angular.element(document.getElementById('btnUploadDoc'))[0].disabled = true;
			$('.modal-backdrop').remove();
			$('body').removeClass('modal-open');

			if ($rootScope.user.user_role_id  == 3 ) {// Sparkonix v2 "MANUFACTURERADMIN"
				$scope.newDocument.manufacturerId = $rootScope.user.manufacturer_id;
			}

			var fd = new FormData();

			for (var i = 0; i < ($scope.fileList).length; i++) {
				var content = $scope.fileList[i].fileContent;
				if (content !== null && content !== undefined && content !== '') {
					fd.append('machinedocs', content);
				}
			}

			fd.append('manufacturerid', $scope.newDocument.manufacturerId);
			fd.append('modelnum', $scope.newDocument.modelNumber);
			fd.append('description', $scope.newDocument.description);

			var promise1 = restAPIService.machineDocumentsResource().save(fd);
			promise1.$promise
					.then(
							function(response) {
								angular.element(document
										.getElementById('btnUploadDoc'))[0].disabled = false;
								dialogs.notify("Success", response.success, {
									'size' : 'sm'
								});
								getAllDocuments();
								$state.reload();
							},
							function(error) {
								angular.element(document
										.getElementById('btnUploadDoc'))[0].disabled = false;
								dialogs.error("Error", error.data.error, {
									'size' : 'sm'
								});
							});
		} else {
			dialogs.error("Error", "Please choose files to upload.", {
				'size' : 'sm'
			});
		}

	}

	$scope.getMachineModelNumberList = function() {

		var manufacturerId = Number($scope.newDocument.manufacturerId);
		var promise = restAPIService.getMachineModelNumberListByManId(
				manufacturerId).query();
		promise.$promise.then(function(response) {
			$scope.machineModelNumbers = response;

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}

}
