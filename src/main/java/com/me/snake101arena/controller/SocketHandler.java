package com.me.snake101arena.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.snake101arena.constant.RoomState;
import com.me.snake101arena.model.Player;
import com.me.snake101arena.model.Playerlist;
import com.me.snake101arena.model.Room;
import com.me.snake101arena.model.Roomlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashMap;

/**
 * Created by ersinn on 29.07.2020.
 */

@Controller
@CrossOrigin
public class SocketHandler {

    @Autowired
    public SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Roomlist roomlist;

    @Autowired
    private Playerlist playerlist;

    @MessageMapping("/main")
    @SendToUser("/main/response")
    public HashMap<String,Object> handleGame(Message message, @Payload HashMap<String,String> payload) throws JsonProcessingException {

        String playerId = message.getHeaders().get("simpSessionId").toString();

        HashMap<String,Object> response = new HashMap<>();

        switch (payload.get("messageType")){

            case "nickname":

                String nickname = payload.get("nickname");

                System.out.println("New Player: " + playerId);

                // register player
                playerlist.addPlayer(new Player(playerId,nickname));

                response.put("messageType","nicknameResponse");
                response.put("response","accepted");

                break;

            case "joinRoom":

                if(roomlist.getRoom(payload.get("roomId")).password.equals(payload.get("password"))
                        && roomlist.getRoom(payload.get("roomId")).state.equals(RoomState.inLobby)
                        && roomlist.getRoom(payload.get("roomId")).players.size() < roomlist.getRoom(payload.get("roomId")).playerCapacity) {

                    // join player
                    roomlist.getRoom(payload.get("roomId")).joinPlayer(playerlist.getPlayer(playerId));

                    response.put("messageType","joinRoomResponse");
                    response.put("response","accepted");
                    response.put("roomInfo",roomlist.getRoom(payload.get("roomId")).getRoomInfo());
                    response.put("playersRoomInfo",roomlist.getRoom(payload.get("roomId")).getPlayersInfo());
                    response.put("meInRoomId",playerlist.getPlayer(playerId).getInRoomId());
                }

                else {
                    response.put("messageType","joinRoomResponse");
                    response.put("response","rejected");
                }

                break;

            case "createRoom":

                ObjectMapper mapper = new ObjectMapper();
                HashMap<String,String> temp = mapper.readValue(payload.get("room"),HashMap.class);

                Room newRoom = Room.createRoom(temp.get("roomName"),temp.get("password"),Integer.valueOf(temp.get("maxPlayers")),Boolean.valueOf(temp.get("isPrivate")));
                roomlist.addRoom(newRoom);

                // join player
                newRoom.joinPlayer(playerlist.getPlayer(playerId));

                // promote admin
                newRoom.makeAdmin(playerlist.getPlayer(playerId));

                response.put("messageType","createRoomResponse");
                response.put("response","accepted");
                response.put("roomInfo",newRoom.getRoomInfo());
                response.put("playersRoomInfo",newRoom.getPlayersInfo());
                response.put("meInRoomId",playerlist.getPlayer(playerId).getInRoomId());

                break;
        }

        return response;

    }

    @MessageMapping("/room/{roomId}")
    public void handleRoom(@DestinationVariable String roomId, Message message, @Payload HashMap<String,String> payload){

        String playerId = message.getHeaders().get("simpSessionId").toString();

        HashMap<String,Object> response = new HashMap<>();

        switch (payload.get("messageType")){
            case "readyPlayer":
                playerlist.getPlayer(playerId).isReady = Boolean.valueOf(payload.get("value"));
                response.put("messageType","readyPlayer");
                response.put("playerInRoomId",playerlist.getPlayer(playerId).getInRoomId());
                response.put("value",Boolean.valueOf(payload.get("value")));
                break;
            case "startGame":
                // only admin can start the game
                if(!roomlist.getRoom(roomId).adminInRoomId.equals(playerlist.getPlayer(playerId).getInRoomId())) return;
                if(roomlist.getRoom(roomId).players.size() == 1) return;
                if(!roomlist.getRoom(roomId).isAllReady()) return;
                roomlist.getRoom(roomId).initGame(); // init the game
                response.put("messageType","startGame");
                response.put("gameDrawableInfo",roomlist.getRoom(roomId).getGame().drawableInfo());
                messagingTemplate.convertAndSend("/room/response/" + roomId,response); // send the game drawable
                roomlist.getRoom(roomId).startGame(); // start the game
                return;

                /////////////////////////////////

            case "moveDirectionSnake":
                roomlist.getRoom(roomId).getGame().moveDirectionSnake(playerlist.getPlayer(playerId).getInRoomId(),payload.get("value"));
                return;

        }

        messagingTemplate.convertAndSend("/room/response/" + roomId,response);

        //payload.forEach((s, s2) -> System.out.println("s - " + s2));
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {

        Player player = playerlist.getPlayer(event.getSessionId());

        if(player == null) return;

        // leave room
        if(player.isInRoom())
            roomlist.getRoom(player.getRoomId()).leavePlayer(player);

        // remove player
        playerlist.removePlayer(event.getSessionId());

        System.out.println("Player disconnected: " + event.getSessionId());
    }

    @EventListener
    public void onUnsubscribeEvent(SessionUnsubscribeEvent sessionUnsubscribeEvent) {
        Player player = playerlist.getPlayer(sessionUnsubscribeEvent.getMessage().getHeaders().get("simpSessionId").toString());

        // if the room does not exist
        if(roomlist.getRoom(player.getRoomId()) == null)
            player.leaveRoom();
        else
            roomlist.getRoom(player.getRoomId()).leavePlayer(player);
    }
}
