"use strict";
angular.module('sprintGraphApp').controller('PlushCtrl', [ 'PlushService', 'MenuService', 'rx', '$localStorage', function(plushService,  menuService, rx, $localStorage) {
	var vm = this;
	this.plushs = plushService.plushs;
	this.memberName = $localStorage.memberName;
	this.memberId = "";
	this.onMemberNameChange = function(){
		if(vm.memberName == null  || vm.memberName == ""){
			menuService.setError("Your name is empty");
		}else{
			$localStorage.memberName = vm.memberName;
			vm.memberId = vm.memberName.trim().toLowerCase();
		}
	}
	this.onMemberNameChange();
	this.take = function(plush){
		if(vm.memberName == null || vm.memberName == ""){
			menuService.setError("Your name is empty");
		}else{
			plushService.take(plush,vm.memberName,vm.memberId);
		}
	
	}
	
	this.release = function(plush){
		if(vm.memberName == null  || vm.memberName == ""){
			menuService.setError("Your name is empty" );
		}else{
			plushService.release(plush,vm.memberName, vm.memberId);
		}
	}
} ]);
