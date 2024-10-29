package com.ex.chatsave;


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
public class ChatSaveApplication {

    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.load();

        ApplicationContextInitializer<ConfigurableApplicationContext> initializer = applicationContext -> {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> envVars = new HashMap<>();
            dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envVars));
        };

        SpringApplication app = new SpringApplication(ChatSaveApplication.class);
        app.addInitializers(initializer);
        app.run(args);
    }

}
