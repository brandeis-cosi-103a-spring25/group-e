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
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.decisions.Decision;

public class networkPlayer implements Player {
    private RestTemplate restTemplate;
    private String serverUrl;
    private boolean active;
    private BigMoneyPlayer backupPlayer;
    private Optional<GameObserver> observer = Optional.empty();
    private String playerName;

    public networkPlayer(String serverUrl, String playerName) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
        this.playerName = playerName;
        this.active = true;
        this.backupPlayer = new BigMoneyPlayer("ex-NetworkPlayer");
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public Optional<GameObserver> getObserver() {
        return observer;
    }

    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        if (!active) {
            return backupPlayer.makeDecision(state, options, reason);
        }

        try {
            DecisionRequest request = new DecisionRequest();
            request.setState(state);
            request.setOptions(options);
            request.setReason(reason.orElse(null));
            request.setPlayer_uuid(playerName); // Assuming name is unique ID, adjust if needed

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<DecisionRequest> entity = new HttpEntity<>(request, headers);
          
            ResponseEntity<DecisionResponse> response = restTemplate.postForEntity(
                serverUrl + "/decision", entity, DecisionResponse.class);

            if (response.getBody() != null) {
                return response.getBody().getDecision();
            } else {
                System.err.println("Empty server response. Switching to BigMoneyPlayer.");
                switchToBackupPlayer();
                return backupPlayer.makeDecision(state, options, reason);
            }
        } catch (ResourceAccessException e) {
            System.err.println("Server unreachable. Switching to BigMoneyPlayer: " + e.getMessage());
            switchToBackupPlayer();
            return backupPlayer.makeDecision(state, options, reason);
        } catch (Exception e) {
            System.err.println("Error during server request. Switching to BigMoneyPlayer: " + e.getMessage());
            switchToBackupPlayer();
            return backupPlayer.makeDecision(state, options, reason);
        }
    }

    private void switchToBackupPlayer() {
        this.active = false;
        System.out.println("Network player is now a BigMoneyPlayer.");
    }

    public boolean isActive() {
        return active;
    }
}
