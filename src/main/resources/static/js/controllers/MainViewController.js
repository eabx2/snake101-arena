app.controller('MainViewController', ['$scope','$rootScope','socket','bridge','$timeout', function($scope,$rootScope,socket,bridge,$timeout) {

    // init state
    $rootScope.viewState = "nickname-view";

    // this func is used by TWO controllers - RoomlistViewController, JoinRoomPasswordViewController -
    $rootScope.joinRoomCallback = function (body) {

        if(body.response == "accepted"){
            // .. go to room view
            // somehow to update the screen timeout func is necessary
            // change viewState inside
            $timeout( function(){
                bridge.addData("roomInfo",body.roomInfo);
                bridge.addData("playersRoomInfo",body.playersRoomInfo);
                bridge.addData("meInRoomId",body.meInRoomId);
                $rootScope.viewState = "room-view";
            }, 0 );
        }

        else if(body.response == "rejected"){
            alert("Wrong Password");
        }

    };

    // establish the connection between client and server
    $rootScope.socket = socket;
    $rootScope.socket.connect();

}]);