package com.me.snake101arena.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ersinn on 27.07.2020.
 */

@Component
public class Roomlist {

    private HashMap<String,Room> roomlist;

    public Roomlist(){
        roomlist = new HashMap<>();
    }

    public List<Room> getRoomlist(){
        return roomlist.values().stream().collect(Collectors.toList());
    }

    public Room getRoom(String id){
        return roomlist.get(id);
    }

    public void addRoom(Room room){
        roomlist.put(room.getRoomInfo().get("id").toString(),room);
    }

    public void removeRoom(Room room){
        roomlist.remove(room.getRoomInfo().get("id").toString());
    }

}
