package com.me.snake101arena.config;

import com.me.snake101arena.game.Engine;
import com.me.snake101arena.game.Game;
import com.me.snake101arena.model.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by ersinn on 7.08.2020.
 */

@Configuration
public class ServiceConfig {

    @Bean
    @Scope(value = "prototype")
    public Room room(String name, String password, int playerCapacity, boolean isPrivate) {
        return new Room(name,password,playerCapacity,isPrivate);
    }

    @Bean
    @Scope(value = "prototype")
    public Game game(int numberOfPlayers) {
        return new Game(numberOfPlayers);
    }

    @Bean
    @Scope(value = "prototype")
    public Engine engine(Game game, String roomId) {
        return new Engine(game,roomId);
    }

}
