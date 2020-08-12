app.controller('CreateRoomViewController', ['$scope','$rootScope','$timeout','bridge', function($scope,$rootScope,$timeout,bridge) {

    var callback = function (body) {

        if(body.response == "accepted"){
            // .. go to room view
            // somehow to update the screen timeout func is necessary - inside callback functions -
            // change viewState inside
            $timeout( function(){
                bridge.addData("roomInfo",body.roomInfo);
                bridge.addData("playersRoomInfo",body.playersRoomInfo);
                bridge.addData("meInRoomId",body.meInRoomId);
                $rootScope.viewState = "room-view";
            }, 0 );
        }

        else if(body.response == "rejected"){
            alert("Something gone wrong");
        }

    };
    
    $scope.onClickOkButton = function (roomName,password,maxPlayers,isPrivate) {

        var room = {
            roomName: roomName,
            password: password,
            maxPlayers: maxPlayers == undefined ? "2" : "" + maxPlayers,
            isPrivate: "" + isPrivate
        };

        $rootScope.socket.createRoom(room,callback);

        // .. go to room view

    };
    
    $scope.onClickCancelButton = function () {
        // go back to roomlist
        $rootScope.viewState = "roomlist-view";
    };

}]);