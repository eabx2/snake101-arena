app.controller('RoomlistViewController', ['$scope','$rootScope','$http','bridge','$timeout', function($scope,$rootScope,$http,bridge,$timeout) {

    $scope.roomInfoList;

    $scope.selectedRoomInfoIndex = null;

    $scope.selectRoomInfo = function (index) {
        $scope.selectedRoomInfoIndex = index;
    };

    $scope.onClickJoinRoomButton = function (roomInfo) {

        if(roomInfo.isPassword){
            bridge.addData("roomId",roomInfo.id);
            $rootScope.viewState = "join-room-password-view";
        }
        else {
            // send join request to the server
            $rootScope.socket.joinRoom(roomInfo.id,"",$rootScope.joinRoomCallback);
        }
    };

    $scope.onClickCreateButton = function () {
        $rootScope.viewState = "create-room-view";
    };

    $scope.onClickRefreshButton = function () {

        $http.get("/roomlist").success(function (data) {
            $scope.roomInfoList = data;
        });

    };

    // initializing

    // load rooms at the beginning
    $scope.onClickRefreshButton();

}]);