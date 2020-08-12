package com.me.snake101arena.game;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by ersinn on 9.08.2020.
 */
public class Snake {

    private boolean isAlive;

    public List<Point> parts;

    public int velX;
    public int velY;

    public double ticksPerSecond = 2;
    public double innerCount = 0;

    Snake(Point initPoint){
        parts = new LinkedList<>();
        parts.add(initPoint);
        isAlive = true;
    }

    public Map<String,Object> getSnakeInfo(){
        Map<String,Object> map = new HashMap<>();
        map.put("parts",parts);
        map.put("isAlive",isAlive);
        return map;
    }


    public void die(){
        isAlive = false;
    }

    public boolean isAlive(){
        return isAlive;
    }

}
