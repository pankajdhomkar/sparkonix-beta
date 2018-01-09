'use strict';

angular.module('sparkonixWebApp').controller('saDashboardController',
		saDashboardController);

function saDashboardController($scope, $rootScope) {

	$scope.loggedUser = $rootScope.user.name;
	console.log("saDashboardController----->>>>",$rootScope.user);
}
