package edu.brandeis.cosi103a.groupe.playerServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
}
