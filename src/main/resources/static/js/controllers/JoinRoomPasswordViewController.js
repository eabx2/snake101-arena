app.controller('JoinRoomPasswordViewController', ['$scope','$rootScope','bridge','$timeout', function($scope,$rootScope,bridge,$timeout) {

    $scope.onClickCancelButton = function () {
        // go back to roomlist
        $rootScope.viewState = "roomlist-view";
    };

    $scope.onClickOkButton = function (password) {

        // send password to the server
        var roomId = bridge.getData("roomId");

        $rootScope.socket.joinRoom(roomId,password,$rootScope.joinRoomCallback );
    }

}]);