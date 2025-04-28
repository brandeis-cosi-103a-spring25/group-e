package edu.brandeis.cosi103a.groupe.playerServer.controller.service;

import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionRequest;
import edu.brandeis.cosi103a.groupe.playerServer.dto.LogEventRequest;
import org.springframework.stereotype.Service;

import java.util.List;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class provides services for making decisions and logging events.
 */
@Service
public class DecisionService {

    
    /** 
     * Makes a decision based on the provided request.
     * @param request the decision request containing options
     * @return Decision the first available decision from the options
     */
    public Decision makeDecision(DecisionRequest request) {
        List<Decision> options = request.getOptions();
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("No available decisions provided.");
        }
        return options.get(0);
    }

    
    /** 
     * Logs an event for a player based on the provided request.
     * This method simulates logging by printing to the console.
     * @param request the log event request containing player UUID and decision
     */
    public void logEvent(LogEventRequest request) {
        System.out.println("Logging event for player [" + request.getPlayer_uuid() + "]: " + request.getDecision());
    }
}