package com.me.snake101arena.game;

import com.me.snake101arena.constant.RoomState;
import com.me.snake101arena.model.Roomlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by ersinn on 9.08.2020.
 */
public class Engine implements Runnable{

    @Autowired
    public SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Roomlist roomlist;

    public boolean run = true;

    Game game;
    String roomId;

    public Engine(Game game, String roomId){
        this.game = game;
        this.roomId = roomId;
    }

    public void engine(){

        // game finish condition
        int aliveSnakeCounter = 0;
        List<Snake> tempSnakes = game.snakes.values().stream().collect(Collectors.toList());
        for (int i = 0; i < game.snakes.values().size(); i++){
            if (tempSnakes.get(i).isAlive()) aliveSnakeCounter++;
        }

        // if only one snake is alive game is finished
        if(aliveSnakeCounter == 1){
            run = false; // break the game loop
            HashMap<String,Object> response = new HashMap<>();
            response.put("messageType","gameFinished");
            messagingTemplate.convertAndSend("/room/response/" + roomId,response);
            return;
        }

        // iterate snakes
        game.snakes.forEach((snakeId, snake) -> {

            // if it is not alive do not render it
            if(!snake.isAlive()) return;

            // check if snake is in tick or wait
            if(snake.innerCount < 1.0){
                snake.innerCount = snake.innerCount + (snake.ticksPerSecond / game.fps);
                return;
            }

            // tick
            snake.innerCount = 0;

            int headNextX = (game.mapWidth + snake.parts.get(0).x + snake.velX) % game.mapWidth;
            int headNextY = (game.mapHeight + snake.parts.get(0).y + snake.velY) % game.mapHeight;

            // cross with a food
            for(int i = 0; i < game.foods.size(); i++){

                if(headNextX == game.foods.get(i).x && headNextY == game.foods.get(i).y){
                    snake.parts.add(0,game.foods.get(i)); // add food into snake
                    snake.ticksPerSecond = snake.ticksPerSecond != 20.0 ? snake.ticksPerSecond + 1 : 20.0; // increase speed of the snake

                    game.foods.remove(i); // remove food
                    game.foods.add(game.createPoint()); // create new food

                    game.obstacles.add(game.createPoint()); // create new obstacle

                    // broadcast snake
                    HashMap<String,Object> response = new HashMap<>();
                    response.put("messageType","snakeParts");
                    response.put("snakeId",snakeId);
                    response.put("parts",snake.parts);
                    messagingTemplate.convertAndSend("/room/response/" + roomId,response);

                    // broadcast foods
                    response = new HashMap<>();
                    response.put("messageType","foods");
                    response.put("foods",game.foods);
                    messagingTemplate.convertAndSend("/room/response/" + roomId,response);

                    // broadcast obstacles
                    response = new HashMap<>();
                    response.put("messageType","obstacles");
                    response.put("obstacles",game.obstacles);
                    messagingTemplate.convertAndSend("/room/response/" + roomId,response);

                    return;
                }

            }

            // cross with an obstacle
            for(int i = 0; i < game.obstacles.size(); i++){

                if(headNextX == game.obstacles.get(i).x && headNextY == game.obstacles.get(i).y){
                    snake.die(); // kill the snake
                    HashMap<String,Object> response = new HashMap<>();
                    response.put("messageType","snakeDie");
                    response.put("snakeId",snakeId);
                    messagingTemplate.convertAndSend("/room/response/" + roomId,response);
                    return;
                }

            }

            // head-body conflict and cross with other snakes
            List<Snake> snakes = game.snakes.values().stream().collect(Collectors.toList());
            for(int i = 0; i < snakes.size(); i++){

                // if snake is one size let it move freely without hit itself
                if(snake.parts.size() == 1 && snakes.get(i).equals(snake)) continue;

                for(int j = 0; j < snakes.get(i).parts.size(); j++){

                    if(headNextX == snakes.get(i).parts.get(j).x && headNextY == snakes.get(i).parts.get(j).y){
                        snake.die(); // kill the snake
                        HashMap<String,Object> response = new HashMap<>();
                        response.put("messageType","snakeDie");
                        response.put("snakeId",snakeId);
                        messagingTemplate.convertAndSend("/room/response/" + roomId,response);
                        return;
                    }

                }
            }


            // MOViNG

            // save head last position
            int tempX1 = snake.parts.get(0).x;
            int tempY1 = snake.parts.get(0).y;
            int tempX2;
            int tempY2;

            // move head
            snake.parts.get(0).x = (game.mapWidth + snake.parts.get(0).x + snake.velX) % game.mapWidth;
            snake.parts.get(0).y = (game.mapHeight + snake.parts.get(0).y + snake.velY) % game.mapHeight;

            for (int i = 1; i < snake.parts.size(); i++){

                // save the last position
                tempX2 = snake.parts.get(i).x;
                tempY2 = snake.parts.get(i).y;

                // make it move
                snake.parts.get(i).x = tempX1;
                snake.parts.get(i).y = tempY1;

                // switch
                tempX1 = tempX2;
                tempY1 = tempY2;

            }

            HashMap<String,Object> response = new HashMap<>();
            response.put("messageType","snakeParts");
            response.put("snakeId",snakeId);
            response.put("parts",snake.parts);
            messagingTemplate.convertAndSend("/room/response/" + roomId,response);
        });

    }

    @Override
    public void run() {

        // game loop
        double ticksPerSecond = 1000000000 / game.fps;
        long lastTime = System.nanoTime();
        double delta = 0;
        while(run){

            long now = System.nanoTime();
            delta += (now - lastTime) / ticksPerSecond;
            lastTime = now;
            while(delta >= 1){
                engine(); // tick
                delta--;
            }

        }

        // go back to lobby
        roomlist.getRoom(roomId).state = RoomState.inLobby;

    }

}
