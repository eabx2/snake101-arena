package com.me.snake101arena.controller;

import com.me.snake101arena.model.Room;
import com.me.snake101arena.model.Roomlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ersinn on 27.07.2020.
 */

@RestController
public class RoomlistRestController {

    @Autowired
    private Roomlist roomlist;

    @GetMapping("/roomlist")
    public List<Map<String, Object>> getRoomlist(){
        return roomlist.getRoomlist().stream()
                 .map(Room::getRoomInfo)
                 .collect(Collectors.toList());
    }

}
