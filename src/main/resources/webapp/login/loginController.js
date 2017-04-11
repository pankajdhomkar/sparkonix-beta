'use strict';

angular.module('sparkonixWebApp')
		.controller('loginController', loginController);
loginController.$inject = [ '$http', '$state', 'restAPIService', '$scope',
		'$rootScope', 'dialogs', '$cookies' ];

function loginController($http, $state, restAPIService, $scope, $rootScope,
		dialogs, $cookies) {
	$rootScope.apiUrl = "/api/";

	$scope.onLogin = function() {

		var encryptedPassword = $scope.md5($scope.password);

		var userObj = {
			"email" : $scope.username,
			"password" : encryptedPassword
		};

		var authUserObj = restAPIService.checkUserByUsernameAndPassword().save(
				userObj);

		authUserObj.$promise.then(function(response) {
			$rootScope.user = response;
			$rootScope.token = $rootScope.user.token;
			$cookies.user = JSON.stringify($rootScope.user);
			$http.defaults.headers.common.Authorization = "Basic "
					+ btoa($rootScope.token + ":");
			if ($rootScope.user.role == "SALESTEAM") {
				$state.go('home.stdashboard');
			}
			if ($rootScope.user.role == "SUPERADMIN") {
				$state.go('home.sadashboard');
			}
			if ($rootScope.user.role == "MANUFACTURERADMIN") {
				$state.go('home.wadashboard');
			}
			if ($rootScope.user.role == "RESELLERADMIN") {
				$state.go('home.wadashboard');
			}
			if ($rootScope.user.role == "TECHNICIAN") {
				$state.go('home.tcdashboard');
			}
		}, function(error) {
			dialogs.error("Error", error.data.error, {
				'size' : 'sm'
			});
		});

	}

	/*
	 * $scope.md5 = function(value) { return CryptoJS.MD5(value).toString(); }
	 */

	$scope.forgotPassword = function() {
		dialogs.error("Error", "oops!", {
			'size' : 'sm'
		});
	}
}