package com.me.snake101arena;

import com.me.snake101arena.model.Playerlist;
import com.me.snake101arena.model.Room;
import com.me.snake101arena.model.Roomlist;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Snake101ArenaApplication {

    public static ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) {
        applicationContext = SpringApplication.run(Snake101ArenaApplication.class, args);
	}

}
