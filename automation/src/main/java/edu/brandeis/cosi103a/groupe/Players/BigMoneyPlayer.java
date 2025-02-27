package edu.brandeis.cosi103a.groupe.Players;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.GameObserver;

import java.util.Optional;

public class BigMoneyPlayer implements Player {
    private final String name;

    public BigMoneyPlayer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<GameObserver> getObserver() {
        return Optional.empty();
    }

    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        if (options.isEmpty()) {
            throw new IllegalStateException("No available decisions!");
        }

        System.out.println("\n----- Big Money Player Turn: " + name + " -----");

        // Display reason if available (e.g., "You must choose a card to buy.")
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        Decision bestMoneyCard = null;
        Decision bestFramework = null;

        // Identify best available Framework and Money cards
        for (Decision decision : options) {
            if (isFramework(decision)) {
                if (bestFramework == null || getCost(decision) > getCost(bestFramework)) {
                    bestFramework = decision;
                }
            } else if (isMoneyCard(decision)) {
                if (bestMoneyCard == null || getCost(decision) > getCost(bestMoneyCard)) {
                    bestMoneyCard = decision;
                }
            }
        }

        // Always buy a Framework if affordable; otherwise, get the best Money card
        Decision chosenDecision = (bestFramework != null) ? bestFramework : bestMoneyCard;

        System.out.println("Big Money Player chose: " + (chosenDecision != null ? chosenDecision : "No valid decision"));
        return chosenDecision != null ? chosenDecision : options.get(0);
    }

    // Checks if the decision represents a Framework purchase
    private boolean isFramework(Decision decision) {
        return decision.toString().contains("Framework");
    }

    // Checks if the decision represents a Money card purchase
    private boolean isMoneyCard(Decision decision) {
        return decision.toString().contains("Money");
    }

    // Extracts cost from a decision name (assuming cost is in the name)
    private int getCost(Decision decision) {
        String decisionStr = decision.toString();
        return Integer.parseInt(decisionStr.replaceAll("[^0-9]", "")); // Extracts numeric cost
    }
}
