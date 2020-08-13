app.controller('RoomViewController', ['$scope','$rootScope','socket','bridge','$timeout', function($scope,$rootScope,socket,bridge,$timeout) {

    var roomResponseCallback = function (msg) {

        var body = JSON.parse(msg.body);

        switch (body.messageType){
            // somehow to update the screen timeout func is necessary - inside callback functions -
            case "joinPlayer":
                $timeout( function(){
                    $scope.playersRoomInfo[body.playerRoomInfo.inRoomId] = body.playerRoomInfo;
                }, 0 );
                break;

            case "leavePlayer":
                $timeout( function(){
                    delete $scope.playersRoomInfo[body.playerRoomInfo.inRoomId];
                }, 0 );
                break;

            case "readyPlayer":
                $timeout( function(){
                    $scope.playersRoomInfo[body.playerInRoomId].isReady = body.value;
                }, 0 );
                break;

            case "deleteRoom":
                if($scope.roomInfo.adminInRoomId == $scope.meInRoomId) return;
                $timeout( function(){
                    $scope.onClickLeaveButton();
                    alert("Room has been disbanded");
                }, 0 );
                break;

            case "startGame":
                $timeout( function(){
                    $scope.gameWindow = "room-game-state-view";
                    gameDrawableInfo = body.gameDrawableInfo;
                    if(canvas)
                        canvas.remove();
                    canvas = new p5(sketch);
                    // pass scope variables
                    canvas.$rootScope = $rootScope;
                    canvas.$scope = $scope;
                }, 0 );
                break;

            case "gameFinished":
                $timeout( function(){
                    var winnerId;
                    var snakes = Object.values(gameDrawableInfo.snakes);
                    for(var i=0;i<snakes.length;i++)
                        if(snakes[i].isAlive) winnerId = Object.keys(gameDrawableInfo.snakes)[i];
                    $scope.messages.push({text : $scope.playersRoomInfo[winnerId].nickname + " won - SCORE " + gameDrawableInfo.snakes[winnerId].parts.length})
                    canvas.remove();
                    $scope.gameWindow = "room-lobby-view";
                    $scope.onClickReadyButton();
                }, 0 );
                break;

            ///////////////////////////////////////////////////////////////////
                // in game messages

            case "snakeParts":
                $timeout( function(){
                    gameDrawableInfo.snakes[body.snakeId].parts = body.parts;
                }, 0 );
                break;

            case "snakeDie":
                $timeout( function(){
                    gameDrawableInfo.snakes[body.snakeId].isAlive = false;
                    $scope.messages.push({text : $scope.playersRoomInfo[body.snakeId].nickname + " died - SCORE " + gameDrawableInfo.snakes[body.snakeId].parts.length})
                }, 0 );
                break;

            case "foods":
                $timeout( function(){
                    gameDrawableInfo.foods = body.foods;
                }, 0 );
                break;

            case "obstacles":
                $timeout( function(){
                    gameDrawableInfo.obstacles = body.obstacles;
                }, 0 );
                break;

        }

    };

    // get data
    $scope.roomInfo = bridge.getData("roomInfo");
    $scope.playersRoomInfo = bridge.getData("playersRoomInfo");
    $scope.meInRoomId = bridge.getData("meInRoomId");

    // subscribe room
    $rootScope.socket.subscribeRoom($scope.roomInfo.id,roomResponseCallback);

    // init state
    $scope.gameWindow = "room-lobby-view";

    // chat
    $scope.messages = [];

    $scope.onClickReadyButton = function () {

        var message = {
            'messageType' : 'readyPlayer',
            'value' :  !$scope.playersRoomInfo[$scope.meInRoomId].isReady
        };

        $rootScope.socket.sendToRoom($scope.roomInfo.id,message);

    };

    $scope.onClickLeaveButton = function () {

        $rootScope.socket.unsubscribeRoom($scope.roomInfo.id);

        if(canvas)
            canvas.remove();

        // get back to roomlist-view
        $rootScope.viewState = "roomlist-view";

    };

    $scope.onClickStartButton = function () {

        var message = {
            'messageType' : 'startGame'
        };

        $rootScope.socket.sendToRoom($scope.roomInfo.id,message);

    };


}]);