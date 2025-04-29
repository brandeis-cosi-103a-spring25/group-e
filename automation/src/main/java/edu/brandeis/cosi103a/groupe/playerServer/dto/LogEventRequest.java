package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.decisions.Decision;

/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class represents a request to log an event for a player.
 */
public class LogEventRequest {
    private String player_uuid;
    private Decision decision;

    public String getPlayer_uuid() {
        return player_uuid;
    }

    public void setPlayer_uuid(String player_uuid) {
        this.player_uuid = player_uuid;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}