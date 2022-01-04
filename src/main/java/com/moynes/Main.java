package com.moynes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(value = "com.moynes")
@Slf4j
public class Main {


    public static void main(String[] args) {
        //SpringApplication.run(Main.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);
        int width = 800;
        int height = 600;
        GameState gameState = new GameState(width, height);
        KeyState keyState = new KeyState();
        Listener listener = new Listener(keyState);
        Game game = new Game(Game.frame(width, height),
                new GameCanvas(width, height, gameState, listener),
                gameState,
                keyState);
        game.run();
    }
}
