'use strict';

angular.module('sparkonixWebApp').controller('headernotificationController',
		headernotificationController);

headernotificationController.$inject = [ '$scope', '$http', '$state',
		'$rootScope', 'restAPIService', '$cookies' ];

function headernotificationController($scope, $http, $state, $rootScope,
		restAPIService, $cookies) {

	$scope.loggedUserRoleId = $rootScope.user.user_role_id;

	$scope.logout = function() {
		$cookies.user = "";
		$state.go('login');
	}
}
