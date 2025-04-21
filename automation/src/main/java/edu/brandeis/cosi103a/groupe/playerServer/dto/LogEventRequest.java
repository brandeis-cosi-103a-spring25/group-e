package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.event.Event;

public class LogEventRequest {
    private Event decision;
    private String player_uuid;

    public Event getDecision() { return decision; }
    public void setDecision(Event decision) { this.decision = decision; }

    public String getPlayer_uuid() { return player_uuid; }
    public void setPlayer_uuid(String player_uuid) { this.player_uuid = player_uuid; }
}