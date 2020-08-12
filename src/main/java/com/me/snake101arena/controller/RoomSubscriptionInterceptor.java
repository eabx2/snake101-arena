package com.me.snake101arena.controller;

import com.me.snake101arena.model.Playerlist;
import com.me.snake101arena.model.Roomlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * Created by ersinn on 3.08.2020.
 */

@Component
public class RoomSubscriptionInterceptor implements ChannelInterceptor {

    @Autowired
    private Roomlist roomlist;

    @Autowired
    private Playerlist playerlist;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {

            String simpDestination = message.getHeaders().get("simpDestination").toString();
            String playerId = message.getHeaders().get("simpSessionId").toString();

            // subscribe room
            if(simpDestination.split("/")[1].equals("room")){
                String roomId = simpDestination.split("/")[3];

                // if room does not have the player then reject subscription
                if(!roomlist.getRoom(roomId).containsPlayer(playerlist.getPlayer(playerId)))
                    throw new MessagingException("No permission for this room");
            }

        }
        return message;
    }

}
