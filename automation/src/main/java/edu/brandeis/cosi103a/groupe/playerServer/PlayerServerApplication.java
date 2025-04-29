package edu.brandeis.cosi103a.groupe.playerServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.context.annotation.Bean;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This is the main application class for the Player Server.
 * It initializes the Spring Boot application and scans for components in the specified package.
 * The Player Server handles player requests for decisions and event logging.
 */
@SpringBootApplication
public class PlayerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayerServerApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        return mapper;
    }
}
