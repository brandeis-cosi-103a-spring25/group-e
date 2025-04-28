package edu.brandeis.cosi103a.groupe.playerServer.controller;

import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionRequest;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionResponse;
import edu.brandeis.cosi103a.groupe.playerServer.dto.LogEventRequest;
import edu.brandeis.cosi103a.groupe.playerServer.controller.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    @Autowired
    private DecisionService decisionService;

    
    /** 
     * @param request
     * @return ResponseEntity<DecisionResponse>
     */
    @PostMapping("/decide")
    public ResponseEntity<DecisionResponse> handleDecision(@RequestBody DecisionRequest request) {
        DecisionResponse response = new DecisionResponse();
        response.setDecision(decisionService.makeDecision(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
    /** 
     * @param request
     * @return ResponseEntity<Void>
     */
    @PostMapping("/log-event")
    public ResponseEntity<Void> logEvent(@RequestBody LogEventRequest request) {
        decisionService.logEvent(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}