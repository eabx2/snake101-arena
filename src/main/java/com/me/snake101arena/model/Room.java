package com.me.snake101arena.model;

import com.me.snake101arena.constant.RoomState;
import com.me.snake101arena.game.Engine;
import com.me.snake101arena.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.me.snake101arena.Snake101ArenaApplication.applicationContext;

/**
 * Created by ersinn on 27.07.2020.
 */

public class Room {

    @Autowired
    public SimpMessagingTemplate messagingTemplate;

    @Autowired
    public Roomlist roomlist;

    public RoomState state = RoomState.inLobby;

    private final String id;
    private final String name;
    public final String password;
    public final int playerCapacity;
    public String adminInRoomId;
    public HashMap<String, Player> players;

    private Thread gameThread;
    private Game game;

    public Room(String name,String password, int playerCapacity, boolean isPrivate){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.password = password != null ? password : "";
        this.playerCapacity = playerCapacity;
        this.players = new HashMap<>(playerCapacity);
    }

    public Map<String,Object> getRoomInfo(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("name",name);
        map.put("isPassword",password.length() != 0);
        map.put("playerCapacity",playerCapacity);
        map.put("adminInRoomId",adminInRoomId);
        map.put("playersNumber",players.size());
        map.put("state",state);
        return map;
    }

    public Map<String, Object> getPlayersInfo() {
        return players.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getPlayerRoomInfo()));
    }

    public void initGame(){
        game = applicationContext.getBean(Game.class,players.size());
        game.init(players.keySet().stream().collect(Collectors.toList()));
    }

    public void startGame(){
        state = RoomState.inGame;
        // start engine
        gameThread = new Thread(applicationContext.getBean(Engine.class,game,id));
        gameThread.start();
    }

    public Game getGame(){
        return game;
    }

    public boolean isAllReady(){
        return  players.values().stream().allMatch(new Predicate<Player>() {
            @Override
            public boolean test(Player player) {
                return player.isReady;
            }
        });
    }

    public void makeAdmin(Player player){
        adminInRoomId = player.getInRoomId();
    }

    public void joinPlayer(Player player) {
        player.joinRoom(id,UUID.randomUUID().toString());
        players.put(player.getInRoomId(),player);

        // broadcast
        HashMap<String,Object> response = new HashMap<>();
        response.put("messageType","joinPlayer");
        response.put("playerRoomInfo",player.getPlayerRoomInfo());
        messagingTemplate.convertAndSend("/room/response/" + id,response);
    }

    public void leavePlayer(Player player){

        HashMap<String,Object> response = new HashMap<>();

        // if the last player left the room then delete it
        if(players.size() == 1){
            deleteRoom();
            player.leaveRoom();
            return;
        }

        // if admin is out then delete the room
        if(player.inRoomId.equals(adminInRoomId)){
            response.put("messageType","deleteRoom");
            messagingTemplate.convertAndSend("/room/response/" + id,response);
            deleteRoom();
            player.leaveRoom();
            return;
        }

        // broadcast
        response.put("messageType","leavePlayer");
        response.put("playerRoomInfo",player.getPlayerRoomInfo());
        messagingTemplate.convertAndSend("/room/response/" + id,response);

        players.remove(player.getInRoomId());
        player.leaveRoom();
    }

    public boolean containsPlayer(Player player){
        return players.values().contains(player);
    }

    public void deleteRoom(){
        System.out.println(roomlist);
        roomlist.removeRoom(this);
    }

    public static Room createRoom(String name,String password, int playerCapacity, boolean isPrivate){
        return applicationContext.getBean(Room.class,name,password,playerCapacity,isPrivate);
    }

}
