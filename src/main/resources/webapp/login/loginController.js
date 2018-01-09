	'use strict';

angular.module('sparkonixWebApp')
		.controller('loginController', loginController);
loginController.$inject = [ '$http', '$state', 'restAPIService', '$scope',
		'$rootScope', 'dialogs', '$cookies'];

function loginController($http, $state, restAPIService, $scope, $rootScope,
		dialogs, $cookies) {
	$rootScope.apiUrl = "/api/";
//	$rootScope.user.role = "";
	$scope.onLogin = function() {

		var encryptedPassword = $scope.md5($scope.password);

		var userObj = {
				"email" : $scope.username,
				"password" : encryptedPassword
			};

		console.log("1-user object->",$scope.username,"  ",$scope.password);
		console.log("user object->",userObj);
		
		var authUserObj = restAPIService.checkUserByUsernameAndPassword().save(userObj);

		console.log("2-user auth->",authUserObj);
		
		authUserObj.$promise.then(function(response) {
			console.log("33-user auth->");
			$rootScope.user = response;
			
			console.log("3-user auth->",$rootScope.user);
			$rootScope.token = $rootScope.user.token;
			console.log("4-user auth->",$rootScope.user.token);
			$cookies.user = JSON.stringify($rootScope.user);
			$http.defaults.headers.common.Authorization = "Basic"
					+ btoa($rootScope.token + ":");
			
			console.log("Authorization string--->",$http.defaults.headers.common.Authorization);
			
			switch ($rootScope.user.user_role_id){
			case 1:
				//For Super admin
//				$rootScope.user.role = "SUPERADMIN";
				$state.go('home.sadashboard');
				break;
			case 2:
				//For Sales Team
//				$rootScope.user.role = "SALESTEAM";
				$state.go('home.stdashboard');
				break;
			case 3:
				//For Manufacturer
//				$rootScope.user.role = "MANUFACTURERADMIN";
				$state.go('home.wadashboard');
				break;
			case 4:
				//For Reseller
//				$rootScope.user.role = "RESELLERADMIN";
				$state.go('home.wardashboard');
				break;
			case 5:
				//For Technician
//				$rootScope.user.role = "TECHNICIAN";
				$state.go('home.tcdashboard');
				break;
			}
			/*if ($rootScope.user.role == "SUPERADMIN") {
				$state.go('home.sadashboard');
			}
			
			if ($rootScope.user.role == "SALESTEAM") {
				$state.go('home.stdashboard');
			}
			
			if ($rootScope.user.role == "MANUFACTURERADMIN") {
				$state.go('home.wadashboard');
			}
			
			if ($rootScope.user.role == "RESELLERADMIN") {	
				$state.go('home.wardashboard');
			}
			if ($rootScope.user.role == "TECHNICIAN") {
				$state.go('home.tcdashboard');
			}*/
		}, function(error) {
			console.log("Error Login js----",error);
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	}
}