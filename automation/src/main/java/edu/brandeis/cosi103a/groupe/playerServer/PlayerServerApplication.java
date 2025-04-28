package edu.brandeis.cosi103a.groupe.playerServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
public class PlayerServerApplication {

    
    /** 
     * @param args
     */   
    public static void main(String[] args) {
        SpringApplication.run(PlayerServerApplication.class, args);
    }
}
