package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import java.util.List;

public class DecisionRequest {
    private GameState state;
    private List<Decision> options;
    private Event reason;
    private String player_uuid;

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public List<Decision> getOptions() { return options; }
    public void setOptions(List<Decision> options) { this.options = options; }

    public Event getReason() { return reason; }
    public void setReason(Event reason) { this.reason = reason; }

    public String getPlayer_uuid() { return player_uuid; }
    public void setPlayer_uuid(String player_uuid) { this.player_uuid = player_uuid; }
}