package com.me.snake101arena.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ersinn on 27.07.2020.
 */

@Component
@Scope(value = "prototype")
public class Player {

    public String id;

    public String nickname;

    private String roomId;
    public String inRoomId;
    public boolean isReady;

    public Player(String id, String nickname){
        this.id = id;
        this.nickname = nickname;
        isReady = false;
    }

    public Map<String,Object> getPlayerRoomInfo(){
        Map<String,Object> map = new HashMap<>();
        map.put("nickname",nickname);
        map.put("roomId",roomId);
        map.put("inRoomId",inRoomId);
        map.put("isReady",isReady);
        return map;
    }

    public boolean isInRoom(){
        return roomId != null;
    }

    public void joinRoom(String roomId,String inRoomId){
        this.roomId = roomId;
        this.inRoomId = inRoomId;
    }

    public void leaveRoom(){
        roomId = null;
        inRoomId = null;
        isReady = false;
    }

    public String getInRoomId(){
        return inRoomId;
    }

    public String getRoomId(){
        return roomId;
    }

}
