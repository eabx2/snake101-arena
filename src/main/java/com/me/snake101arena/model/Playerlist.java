package com.me.snake101arena.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by ersinn on 2.08.2020.
 */

@Component
public class Playerlist {

    public Playerlist(){
        playerlist = new HashMap<>();
    }

    public void addPlayer(Player player){
        playerlist.put(player.id,player);
    }

    public void removePlayer(String id){
        playerlist.remove(id);

        // ...
    }

    public Player getPlayer(String id){
        return playerlist.get(id);
    }

    private HashMap<String,Player> playerlist;

}
