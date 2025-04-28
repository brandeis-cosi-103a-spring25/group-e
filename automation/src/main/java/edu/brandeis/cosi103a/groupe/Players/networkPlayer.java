package edu.brandeis.cosi103a.groupe.Players;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionRequest;
import edu.brandeis.cosi103a.groupe.playerServer.dto.DecisionResponse;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;

public class networkPlayer {
    private RestTemplate restTemplate;
    private String serverUrl;
    private boolean active;
    private BigMoneyPlayer backupPlayer;

    public networkPlayer(String serverUrl, GameEngine gameEngine) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
        this.active = true;
        this.backupPlayer = new BigMoneyPlayer("ex-NetworkPlayer");
    }

    public DecisionResponse getDecision(DecisionRequest request) {
        if (!active) {
            return fallbackDecision(request);
       }
      
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<DecisionRequest> entity = new HttpEntity<>(request, headers);
          
            ResponseEntity<DecisionResponse> response = restTemplate.postForEntity(
                serverUrl + "/decision", entity, DecisionResponse.class);

            return response.getBody();
        } catch (ResourceAccessException e) {
            System.err.println("Server is unreachable or timed out. Switching to BigMoneyPlayer: " + e.getMessage());
            switchToBackupPlayer();
            return fallbackDecision(request);
        } catch (Exception e) {
            System.err.println("An error occurred. Switching to BigMoneyPlayer: " + e.getMessage());
            switchToBackupPlayer();
            return fallbackDecision(request);
        }
    }


    private DecisionResponse fallbackDecision(DecisionRequest request) {
        Decision decision = backupPlayer.makeDecision(
            request.getState(),
            ImmutableList.copyOf(request.getOptions()),
            Optional.ofNullable(request.getReason())
        );
        return new DecisionResponse(decision);
    }

    private void switchToBackupPlayer() {
        this.active = false;
        System.out.println("Network player is now a BigMoneyPlayer.");
    }

    public boolean isActive() {
        return active;
    }
}
