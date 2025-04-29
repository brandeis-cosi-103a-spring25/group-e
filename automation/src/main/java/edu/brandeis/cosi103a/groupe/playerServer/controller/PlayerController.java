package edu.brandeis.cosi103a.groupe.playerServer.controller;

import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionRequest;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionResponse;
import edu.brandeis.cosi103a.groupe.playerServer.dto.LogEventRequest;
import edu.brandeis.cosi103a.groupe.playerServer.controller.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class handles player requests for decisions and event logging.
 * It provides endpoints for making decisions and logging events.
 */
@RestController
public class PlayerController {

    @Autowired
    private DecisionService decisionService;

    @PostMapping(
        value = "/decide",
        consumes = {"application/json", "application/json;charset=UTF-8"},
        produces = "application/json"
    )
    public ResponseEntity<DecisionResponse> handleDecision(@RequestBody DecisionRequest request) {
        DecisionResponse response = new DecisionResponse();
        System.out.println("Received decision request: " + request.getPlayer_uuid());
        response.setDecision(decisionService.makeDecision(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/log-event")
    public ResponseEntity<Void> logEvent(@RequestBody LogEventRequest request) {
        decisionService.logEvent(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Player server is running!");
    }

}

