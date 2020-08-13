var gameDrawableInfo;
var canvas;

var sketch = function (s) {

    s.setup = function () {
        var c = s.createCanvas(gameDrawableInfo.mapWidth, gameDrawableInfo.mapHeight);
        c.parent("room-game-state-view");
    };

    s.draw = function () {

        // clear background
        s.background(220);

        // draw snakes

        var snakes = Object.values(gameDrawableInfo.snakes);
        for(var i=0;i<snakes.length;i++){
            if(Object.keys(gameDrawableInfo.snakes).indexOf(s.$scope.meInRoomId ) == i)
                s.fill(255,255,0);
            else
                s.fill(0);
            var snake = snakes[i];
            for(var j=0;j<snake.parts.length;j++){
                var part = snake.parts[j];
                s.square(part.x,part.y,gameDrawableInfo.gridSize);
            }
            s.fill(255);
            s.text(s.$scope.playersRoomInfo[Object.keys(gameDrawableInfo.snakes)[i]].nickname,snake.parts[0].x,snake.parts[0].y);
        }

        // draw foods
        s.fill(0,255,0);
        var foods = gameDrawableInfo.foods;
        for(var i=0;i<foods.length;i++){
            var food = foods[i];
            s.square(food.x,food.y,gameDrawableInfo.gridSize);
        }

        // draw obstacles
        s.fill(255,0,0);
        var obstacles = gameDrawableInfo.obstacles;
        for(var i=0;i<obstacles.length;i++){
            var obstacle = obstacles[i];
            s.square(obstacle.x,obstacle.y,gameDrawableInfo.gridSize);
        }

    };

    s.keyPressed = function(){

        var message = {
            'messageType' : 'moveDirectionSnake',
            'value' : ""
        };

        if(s.keyCode == s.LEFT_ARROW){
            message.value = "left";
            s.$rootScope.socket.sendToRoom(s.$scope.roomInfo.id,message);
        }
        else if(s.keyCode === s.RIGHT_ARROW){
            message.value = "right";
            s.$rootScope.socket.sendToRoom(s.$scope.roomInfo.id,message);
        }
        else if(s.keyCode == s.UP_ARROW){
            message.value = "up";
            s.$rootScope.socket.sendToRoom(s.$scope.roomInfo.id,message);
        }
        else if(s.keyCode == s.DOWN_ARROW){
            message.value = "down";
            s.$rootScope.socket.sendToRoom(s.$scope.roomInfo.id,message);
        }

    };

};