'use strict';

angular.module('sparkonixWebApp')
  .directive('warSidebarDirective', warSidebarDirective);

function warSidebarDirective() {
	
	var sidebar = {};
	
	sidebar.templateUrl = 'webadminreseller/war_sidebar/war_sidebar.html';
	sidebar.restrict = 'E';
	sidebar.controller = warSidebarController;
	
	return sidebar;
}