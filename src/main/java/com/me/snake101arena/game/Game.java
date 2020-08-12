
package com.me.snake101arena.game;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ersinn on 9.08.2020.
 */
public class Game {

    private static final int gridSize = 15;

    // Settings
    public int mapWidth;
    public int mapHeight;
    public int numberOfPlayers;

    public HashMap<String,Snake> snakes;
    public LinkedList<Point> obstacles;
    public LinkedList<Point> foods;

    public double fps = 60.0;

    public Game(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
        snakes = new HashMap<>(numberOfPlayers);
        obstacles = new LinkedList<>();
        foods = new LinkedList<>();
    }

    public void moveDirectionSnake(String id, String moveDirection){

        switch (moveDirection){

            case "left":
                if(snakes.get(id).velX == 1 * gridSize) break;
                snakes.get(id).velX = -1 * gridSize;
                snakes.get(id).velY = 0;
                break;

            case "right":
                if(snakes.get(id).velX == -1 * gridSize) break;
                snakes.get(id).velX = gridSize;
                snakes.get(id).velY = 0;
                break;

            case "up":
                if(snakes.get(id).velY == 1 * gridSize) break;
                snakes.get(id).velX = 0;
                snakes.get(id).velY = -1 * gridSize;
                break;

            case "down":
                if(snakes.get(id).velY == -1 * gridSize) break;
                snakes.get(id).velX = 0;
                snakes.get(id).velY = gridSize;
                break;

        }

    }

    public Point createPoint(){

        int x = 0;
        int y = 0;
        boolean inside = true;

        while (inside){
            x = (int) Math.floor(Math.random() * mapWidth);
            x = (int) (Math.floor(x / gridSize) * gridSize);

            y = (int) Math.floor(Math.random() * mapWidth);
            y = (int) (Math.floor(y / gridSize) * gridSize);

            int finalX = x;
            int finalY = y;

            // if x and y overlap one the snakes then return false - that means try with different x and y
            inside = !snakes.values().stream().allMatch(snake -> snake.parts.stream().allMatch(point -> {
                if(point.x != finalX && point.y != finalY)
                    return true;
                else
                    return false;
            }));

            // if x and y overlap one the obstacle then return false - that means try with different x and y
            inside = !obstacles.stream().allMatch(obstacle -> {
                if(obstacle.x != finalX && obstacle.y != finalY)
                    return true;
                else
                    return false;
            });

            // if x and y overlap one the food then return false - that means try with different x and y
            inside = !foods.stream().allMatch(food -> {
                if(food.x != finalX && food.y != finalY)
                    return true;
                else
                    return false;
            });

        }

        return new Point(x,y);

    }

    public void init(List<String> playersInRoomId){
        mapWidth = 150 * numberOfPlayers;
        mapHeight = 600;

        // create snakes
        for (int i = 0; i < playersInRoomId.size(); i++) {
            Point point = createPoint();
            snakes.put(playersInRoomId.get(i), new Snake(point));
        }

        // create foods
        for (int i = 0; i < playersInRoomId.size()-1; i++) {
            Point point = createPoint();
            foods.add(point);
        }
    }

    public Map<String,Object> drawableInfo(){
        Map<String,Object> map = new HashMap<>();
        map.put("mapWidth",mapWidth);
        map.put("mapHeight",mapHeight);
        map.put("gridSize",gridSize);
        map.put("snakes",snakes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getSnakeInfo())));
        map.put("obstacles",obstacles);
        map.put("foods",foods);
        return map;
    }

}
