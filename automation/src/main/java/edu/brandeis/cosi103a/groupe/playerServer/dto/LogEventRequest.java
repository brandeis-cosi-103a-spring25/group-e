package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.event.Event;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class represents a request to log an event for a player.
 */
public class LogEventRequest {
    private Event decision;
    private String player_uuid;

    public Event getDecision() { return decision; }
    public void setDecision(Event decision) { this.decision = decision; }

    public String getPlayer_uuid() { return player_uuid; }
    public void setPlayer_uuid(String player_uuid) { this.player_uuid = player_uuid; }
}