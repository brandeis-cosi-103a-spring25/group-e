package edu.brandeis.cosi103a.groupe.playerServer.controller.service;

import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionRequest;
import edu.brandeis.cosi103a.groupe.playerServer.dto.LogEventRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionService {

    public Decision makeDecision(DecisionRequest request) {
        List<Decision> options = request.getOptions();
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("No available decisions provided.");
        }
        return options.get(0);
    }

    public void logEvent(LogEventRequest request) {
        System.out.println("Logging event for player [" + request.getPlayer_uuid() + "]: " + request.getDecision());
    }
}