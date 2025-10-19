package com.alexia;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * Clase principal de la aplicación Alexia.
 * Punto de entrada para Spring Boot.
 * 
 * @author Alexia Team
 * @version 1.0
 * @since 2025-10-14
 */
@SpringBootApplication
public class AlexiaApplication {

    public static void main(String[] args) {
        // Cargar variables de entorno ANTES de iniciar Spring
        loadEnvironmentVariables();
        
        SpringApplication.run(AlexiaApplication.class, args);
        System.out.println("✓ Alexia Application Started Successfully!");
        System.out.println("✓ Dashboard available at: http://localhost:8080");
    }
    
    /**
     * Loads environment variables based on the active profile.
     * - Production (Render): Uses environment variables from Render dashboard
     * - Development: Loads from .env file in project root
     */
    private static void loadEnvironmentVariables() {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        
        // In production, use Render's environment variables directly
        if ("prod".equals(profile)) {
            System.out.println("✓ Using environment variables from Render dashboard (production)");
            return;
        }
        
        // In development, load from .env file
        try {
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
            
            dotenv.entries().forEach(entry -> {
                setEnvironmentVariable(entry.getKey(), entry.getValue());
            });
            
            System.out.println("✓ Loaded environment variables from .env file (development)");
        } catch (Exception e) {
            System.err.println("⚠ Could not load .env file: " + e.getMessage());
        }
    }
    
    /**
     * Sets an environment variable using reflection.
     * This is a workaround since Java doesn't allow direct modification of environment variables.
     */
    @SuppressWarnings("unchecked")
    private static void setEnvironmentVariable(String key, String value) {
        try {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);
        } catch (Exception e) {
            // Fallback to system property if reflection fails
            System.setProperty(key, value);
        }
    }
}
