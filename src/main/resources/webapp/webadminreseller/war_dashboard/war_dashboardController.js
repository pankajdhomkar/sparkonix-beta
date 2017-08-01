'use strict';

angular.module('sparkonixWebApp').controller('warDashboardController',
		warDashboardController);

function warDashboardController($scope, $rootScope) {
	
	$scope.loggedUser = $rootScope.user.name;
	console.log("----",$scope.loggedUser);
}
