package edu.brandeis.cosi103a.groupe.playerServer.dto;

import edu.brandeis.cosi.atg.api.decisions.Decision;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class represents a response containing a decision made by the player.
 */
public class DecisionResponse {
    private Decision decision;

    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }
}