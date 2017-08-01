'use strict';

angular.module('sparkonixWebApp')
    .directive('webadminresellerStats',function() {
    	return {
  		templateUrl:'webadminreseller/war_dashboard/stats/stats.html',
  		restrict:'E',
  		replace:true,
  		scope: {
        'model': '=',
        'comments': '@',
        'number': '@',
        'name': '@',
        'colour': '@',
        'details':'@',
        'type':'@',
        'goto':'@',
        'image':'@'
  		}
  		
  	}
  });
