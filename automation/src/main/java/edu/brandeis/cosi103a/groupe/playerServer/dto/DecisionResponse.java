package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.decisions.Decision;

public class DecisionResponse {
    private Decision decision;

    public DecisionResponse() {
    }

    public DecisionResponse(Decision decision) {
        this.decision = decision;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
