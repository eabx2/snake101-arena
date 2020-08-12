app.controller('NicknameViewController', ['$scope', '$rootScope','$timeout', function($scope,$rootScope,$timeout) {

    var nicknameCallback = function (body) {

        if(body.response == "accepted"){
            // .. go to roomlist view
            // somehow to update the screen timeout func is necessary
            $timeout( function(){
                $rootScope.viewState = "roomlist-view";
            }, 0 );
        }

        else if(body.response == "rejected"){
            alert("not suitable");
        }

    };

    $scope.onClickNicknameOkButton = function (nickname) {

        // send nickname to the server
        $rootScope.socket.sendNickname(nickname,nicknameCallback);

    };

}]);