'use strict';

angular.module('sparkonixWebApp').controller('viewResellerController',
		viewResellerController);

function viewResellerController($scope, $state, restAPIService, dialogs,
		$rootScope,$http,$stateParams) {
	$scope.ManResDTO = {}
	$scope.number ={};
	// ------------- PRIVATE VARIABLES ----------------

	// ------------- CONTROLLER CODE ------------------
	$scope.mode = $stateParams.mode;
	$scope.manResId = $stateParams.manResId;
	$scope.companyType = $stateParams.companyType;
	$scope.parent = $stateParams.parent;
	if ($scope.mode == "view") {

		$scope.headerTitleText = "View Details";
		$scope.editMode = true;
		$scope.disablePassword = false;
		
		var companyDetailId = Number($scope.manResId);
		// get ManResDTO from db by manResId
		console.log("Herer in view company type is ",$scope.companyType);
		console.log("data from api-->");
		var promise1 = restAPIService.companyDetailManResResource(
				$scope.manResId, $scope.companyType).get();
		promise1.$promise.then(function(response) {
			// populate value of ManRes for edit form
			
			if(response.manResDetail != null){
				$scope.newManRes = response.manResDetail;		
			}else{
				$scope.newManRes = response.reseller;
				$scope.number = response.reseller.fld_manufid;
				getManuList();
				console.log("manufacturer id-->",$scope.number);
			}
				
			if ($scope.newManRes.curSubscriptionStartDate != null) {
				$scope.newManRes.curSubscriptionStartDate = new Date(
						$scope.newManRes.curSubscriptionStartDate);
			}
			if ($scope.newManRes.curSubscriptionEndDate != null) {
				$scope.newManRes.curSubscriptionEndDate = new Date(
						$scope.newManRes.curSubscriptionEndDate);
			}
			$scope.newUser = response.webAdminUser;	
			
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});

		});

	}

	// ------------- PUBLIC FUNCTIONS -------------

	$scope.onCancel = function() {
		$scope.activeTabNumber = 1;
		$state.go('home.wareseller');
	}
	
	function getManuList(){
		promise1 = restAPIService.companyDetailResource(
				$scope.number,"MANUFACTURER").get();

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
