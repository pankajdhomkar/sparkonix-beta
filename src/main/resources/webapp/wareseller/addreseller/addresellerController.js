'use strict';
angular.module('sparkonixWebApp').controller('addResellerController',
		addResellerController);

function addResellerController($scope, $state, $rootScope, restAPIService,
		dialogs) {
	console.log("Reseller controller1");
	// ------------- PUBLIC VARIABLES ----------------
	$scope.companyTypes = [ "MANUFACTURER", "RESELLER"];
	$scope.curSubscriptionTypes = [ "BASIC", "PREMIUM" ];
	$scope.curSubscriptionStatusData = [ "ACTIVE", "PAYMENT_DUE", "INACTIVE",
			"EXPIRED" ];
	$scope.ManResDTO = {}
	$scope.opened = {};
	$scope.manufacturersList = {};
	$scope.mlist = {};
	$scope.manufacturerId=0;
	// $scope.isCompanyPanExist = false;
	$scope.panValidationMsg = "";
	$scope.checkingTemp = {};
	$scope.myvar=1;

	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ------------------
	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.opened = {};
		$scope.opened[$event.target.id] = true;
	};
	$scope.format = 'dd-MM-yyyy'
		console.log("2");
	if ($scope.mode == "add") {
//		if($scope.role != "MANUFACTURERADMIN"){
			$scope.headerTitleText = "Add New Reseller";
			console.log("2");
				// fetch manufacturer dropdown list
				getCompanyDetailListByType("manufacturer");
//		}

		$scope.editMode = false;
		$scope.disablePassword = true;
	} else if ($scope.mode == "edit") {
		$scope.headerTitleText = "Edit Reseller";
		$scope.editMode = true;
		$scope.disablePassword = false;

		var companyDetailId = Number($scope.manResId);
		
		var promise1;
		if($scope.companyType == "MANUFACTURER"){
			// get ManResDTO from db by manResId
			console.log("1Manufacturer");
			promise1 = restAPIService.companyDetailManResResource(
					$scope.manResId, "MANUFACTURER").get();
		}
				
		promise1.$promise.then(function(response) {
			// populate value of ManRes for edit form
			if(response.manResDetail == null){
				//It display when reseller edited option click
				console.log("1loggg-->REseller");
				$scope.ManResDTO = response;
				$scope.newManRes = response.reseller;
				console.log("id of Manufacturer---->"+$scope.newManRes.fld_manufid);
				getManuList();
			}else{
				console.log("2loggg-->Manufacturer");
				$scope.ManResDTO = response;
				$scope.newManRes = response.manResDetail;
				
			}
			
			if ($scope.newManRes.curSubscriptionStartDate != null) {
				$scope.newManRes.curSubscriptionStartDate = new Date(
						$scope.newManRes.curSubscriptionStartDate);
			}
			if ($scope.newManRes.curSubscriptionEndDate != null) {
				$scope.newManRes.curSubscriptionEndDate = new Date(
						$scope.newManRes.curSubscriptionEndDate);
			}

			if (response.webAdminUser != null) {
				$scope.newUser = response.webAdminUser;

				// $scope.newUser.mobile = Number($scope.newUser.mobile);
				// if user not updated his password then assign his old
				$scope.dbPassword = $scope.newUser.password;
				// do not display password in edit mode
				$scope.newUser.password = null;
			}
			// show/hide web admin details
			$scope.manageWebAdminDetails();

		}, function(error) {
			
			console.log(error);
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});

			$state.go('home.wareseller.addreseller');
			$state.reload();
		});

	}

	// ------------- PUBLIC FUNCTIONS -------------
	
	//----------------This function use to take id of manufacturers from the list
	$scope.getId = function(){
		$scope.array=[];
		$scope.array.push($scope.mlist);
		$scope.manufacturerId = $scope.array[0].id;
		console.log("Selected Manufacturer id -"+$scope.manufacturerId);
	}
	$scope.onSubmit = function() {
		if ($scope.mode == "add") {

			$scope.newManRes.onBoardedBy = $rootScope.user.id;

			if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {
				$scope.newManRes.curSubscriptionStartDate = "";
				$scope.newManRes.curSubscriptionEndDate = "";
			}

			$scope.ManResDTO.manResDetail = $scope.newManRes;
			if ($scope.hideFlag != "true") {
				// if web admin details not hidden
				var encryptedPassword = $scope.md5($scope.newUser.password);
				$scope.newUser.password = encryptedPassword;
				$scope.ManResDTO.webAdminUser = $scope.newUser;

			} else {
				$scope.ManResDTO.webAdminUser = null;
			}
			console.log("JSON------"+$scope.manufacturerId);
			console.log("Role------"+$scope.role);
			//Here number is send like 2 = reseller and 1 = manufacturer
			if($scope.role != "MANUFACTURERADMIN"){
				// fetch manufacturer dropdown list
				console.log("RESELLER");
				$scope.nres = {};
				$scope.nres.fld_manufid = $scope.manufacturerId;
				$scope.ManResDTO.reseller = $scope.nres; // Manufacturer id stored here
				$scope.ManResDTO.companyType = "RESELLER";
				$scope.newManRes.companyType = "RESELLER";// company details table beacuse it set as not null
			}else{
				$scope.newManRes.companyType = "MANUFACTURER";
				$scope.ManResDTO.companyType = "MANUFACTURER"
					
				$scope.nres = {};
				$scope.nres.fld_manufid = 0;
				$scope.ManResDTO.reseller =$scope.nres;
			}
			console.log("Manufacturer id ------"+$scope.ManResDTO.companyType);
			var promise1 = restAPIService.companyDetailsManResResource().save(
					$scope.ManResDTO);
			
			promise1.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});

				if ($scope.newManRes.companyType == "MANUFACTURER") {
					$rootScope.tabValueManRes = 1;
				} else if ($scope.newManRes.companyType == "RESELLER") {
					$rootScope.tabValueManRes = 2;
				}

				$state.go('home.wareseller');
				$state.reload();

			}, function(error) {
				$scope.newUser.password = $scope.password2;
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});
		} else if ($scope.mode == "edit") {
			console.log("Edit");
			$scope.newManRes.onBoardedBy = $rootScope.user.id;
			$scope.newManRes.companyType = "MANUFACTURER";
			$scope.ManResDTO.manResDetail = $scope.newManRes;
			

			if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {
				$scope.newManRes.curSubscriptionStartDate = "";
				$scope.newManRes.curSubscriptionEndDate = "";
			}
			if ($scope.newUser != null) {
				$scope.ManResDTO.webAdminUser = $scope.newUser;
			}
//			here $scope.manResId is company id and pass to resource so here i have to check which role is updating according to call service
			var promise2;
		
			if($scope.ManResDTO.manResDetail != null){
				console.log("I");
				$scope.ManResDTO.reseller = null;
				promise2 = restAPIService.companyDetailManResResource(
						$scope.manResId, "MANUFACTURER").update($scope.ManResDTO);
				console.log("Manufacturer response="+promise2);
			}else if($scope.ManResDTO.reseller != null){
				console.log("II");
				promise2 = restAPIService.companyDetailManResResource(
						$scope.manResId, "RESELLER").update($scope.ManResDTO);
				console.log("Reseller response="+promise2);
			}
			promise2.$promise.then(function(response) {
				dialogs.notify("Success", response.success, {
					'size' : 'sm'
				});

				if ($scope.newManRes.companyType == "MANUFACTURER") {
					$rootScope.tabValueManRes = 1;
				} else if ($scope.newManRes.companyType == "RESELLER") {
					$rootScope.tabValueManRes = 2;
				}
				$state.go('home.wareseller');
				$state.reload();

			}, function(error) {
				dialogs.error("Error", error.data.error, {
					'size' : 'sm'
				});
			});

		}
	}

	$scope.onCancel = function() {
		$state.go('home.wareseller');
		$state.reload();
	}

	$scope.matchPassword = function() {
		if ($scope.newUser.password != $scope.password2) {
			$scope.ngDisabled = true;
		} else if ($scope.newUser.password == $scope.password2) {
			$scope.ngDisabled = false;
		}
	}

	$scope.resetPasswordField = function() {
		if ($scope.disablePassword) {
			$scope.newUser.password = "";
			$scope.password2 = "";
		}
	}

	$scope.manageWebAdminDetails = function() {
		console.log("loggg-->function manage web admin details");
		if ($scope.newManRes.curSubscriptionStatus == "INACTIVE") {
			// alert('hide web admin details')
			console.log("loggg-->INACTIVE");
			$scope.hideFlag = "true";
		} else {
			// alert('show web admin details')
			console.log("loggg-->ACTIVE");
			$scope.hideFlag = "false";
		}
	}

	$scope.checkCompanyPan = function() {
		// alert($scope.newManRes.pan);
		if (($scope.newManRes.pan).length > 9) {
			$scope.isCompanyPanAvailable = false;
			if ($scope.newManRes.pan != undefined) {
				$scope.isCompanyPanFieldEmpty = false;
				var promise = restAPIService
						.companyDetailByCompanyTypeAndCompanyPan(
								$scope.newManRes.companyType,
								$scope.newManRes.pan).get();
				promise.$promise.then(function(response) {
					if (response.isCompanyPanExist == true) {
						// alert('not null');
						$scope.isCompanyPanExist = true;
						$scope.isCompanyPanAvailable = false;
					} else {
						// alert('null');
						$scope.isCompanyPanExist = false;
						$scope.isCompanyPanAvailable = true;
					}
				});
			} else if ($scope.newManRes.pan == undefined) {
				$scope.isCompanyPanFieldEmpty = true;

			}
		} else {
			$scope.isCompanyPanFieldEmpty = true;
			$scope.isCompanyPanExist = false;
			$scope.isCompanyPanAvailable = false;
		}
	}

	$scope.checkCompanyPanAvailability = function() {
		$scope.panValidationMsgColor = "red";

		if ($scope.newManRes.pan != undefined) {
			if (($scope.newManRes.pan).length == 10) {
				var promise = restAPIService
						.companyDetailByCompanyTypeAndCompanyPan(
								$scope.newManRes.companyType,
								$scope.newManRes.pan).get();
				promise.$promise
						.then(function(response) {
							if (response.isCompanyPanExist == true) {
								// pan already used by this company type
								$scope.panValidationMsg = "This company PAN is not available";
							} else {
								$scope.panValidationMsgColor = "green";
								// pan not being used by this company type
								$scope.panValidationMsg = "This company PAN is available";
							}
						});
			} else {
				$scope.panValidationMsg = "Enter 10 digit in company PAN ";

			}
		} else if ($scope.newManRes.pan == undefined) {
			$scope.panValidationMsg = "Enter company PAN";

		}

	}

	$scope.setEndDate = function() {

		var curSubStartDate = $scope.newManRes.curSubscriptionStartDate;
		var curSubEndDate = new Date();
		curSubEndDate.setYear(curSubStartDate.getFullYear() + 1);
		curSubEndDate.setMonth(curSubStartDate.getMonth());
		curSubEndDate.setDate(curSubStartDate.getDate());
		$scope.newManRes.curSubscriptionEndDate = "";

		// set end date
		$scope.newManRes.curSubscriptionEndDate = curSubEndDate;
	}
	
	
	function getCompanyDetailListByType(companyType) {
		var promise = restAPIService.companyDetailsByCompanyTypeResource(
				companyType).query();

		// dropdwon should display all Man/Res while adding new machine
		promise.$promise.then(function(response) {
				$scope.manufacturersList = response;
				console.log("List of manufacturer",response);
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
	
	function getManuList(){
		promise1 = restAPIService.companyDetailResource(
				$scope.newManRes.fld_manufid,"MANUFACTURER").get(); // Pass the manufacturer id 

		promise1.$promise.then(function(response) {
			console.log("Response print company name-->", response);
			$scope.companyDetails = response.manResDetail;					
			$scope.newManRes.companyType = response.manResDetail.companyName;

		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});
	}
}
