package com.ex.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories
public class BackendApplication {

    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.load();

        // Create an ApplicationContextInitializer to modify the environment before the context is refreshed
        ApplicationContextInitializer<ConfigurableApplicationContext> initializer = applicationContext -> {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> envVars = new HashMap<>();
            dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envVars));
        };

        // Run the application with the initializer
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.addInitializers(initializer);
        app.run(args);
    }
}
