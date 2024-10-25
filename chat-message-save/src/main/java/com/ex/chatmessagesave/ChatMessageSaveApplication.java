package com.ex.chatmessagesave;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class ChatMessageSaveApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        ApplicationContextInitializer<ConfigurableApplicationContext> initializer = applicationContext -> {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> envVars = new HashMap<>();
            dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envVars));
        };

        SpringApplication app = new SpringApplication(ChatMessageSaveApplication.class);
        app.addInitializers(initializer);
        app.run(args);
    }

}
