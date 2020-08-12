app.factory('socket', [function() {

    var stompClient = null;
    var baseAddress = window.location.href;

    var mainResponseCallback = function (message) {

        var body = JSON.parse(message.body);

        switch (body.messageType){
            case "nicknameResponse":
                mySocket.nicknameCallback(body);
                break;
            case "joinRoomResponse":
                mySocket.joinRoomCallback(body);
                break;
            case "createRoomResponse":
                mySocket.createRoomCallback(body);
                break;
        }

    };

    var mySocket = {

        connect: function () {
            var socket = new SockJS(baseAddress + '/main');
            stompClient = Stomp.over(socket);
            stompClient.debug = null; // close logging
            var connectCallback = function (frame) {
                stompClient.subscribe('/user/main/response', mainResponseCallback);
            };
            var errorCallback = function (error) {
                if(error.headers.message == "No permission for this room")
                    console.log("join room denied");
                    mySocket.connect(); // reconnect
            };
            stompClient.connect({},connectCallback,errorCallback);
            this.stompClient = stompClient;
        },

        joinRoom: function (roomId,password,callback) {
            this.joinRoomCallback = callback;
            this.stompClient.send("/main", {},
                JSON.stringify({'messageType': 'joinRoom', 'roomId': roomId, 'password': password}));
        },

        subscribeRoom: function (roomId,callback) {
            this.stompClient.subscribe('/room/response/' + roomId, callback);
        },

        unsubscribeRoom: function (roomId) {
            this.stompClient.unsubscribe('/room/response/' + roomId);
        },
        
        createRoom: function (room,callback) {
            this.createRoomCallback = callback;
            this.stompClient.send("/main", {},
                JSON.stringify({'messageType' : "createRoom", "room" : JSON.stringify(room)}));
        },

        sendToRoom: function (roomId,message) {
            this.stompClient.send("/room/" + roomId, {}, JSON.stringify(message));
        },

        sendNickname: function (nickname,callback) {
            this.nicknameCallback = callback;
            this.stompClient.send("/main", {},
                JSON.stringify({'messageType' : "nickname", "nickname": nickname}));
        }

    };

    return mySocket;

}]);