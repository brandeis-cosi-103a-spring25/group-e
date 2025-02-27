package edu.brandeis.cosi103a.groupe;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
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
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        int availableMoney = state.getSpendableMoney();
        int availableBuys = state.getAvailableBuys();
        if (availableBuys <= 0) {
            System.out.println("No available buys left.");
            return options.get(0); // Default fallback decision
        }

        BuyDecision bestMoneyCard = null;
        BuyDecision bestFramework = null;

        // Identify the best Framework and Money cards within budget
        for (Decision decision : options) {
            if (decision instanceof BuyDecision buyDecision) {
                int cost = buyDecision.getCardType().getCost();
                if (cost <= availableMoney) { // Ensure affordability
                    if (isFramework(buyDecision) && (bestFramework == null || cost > bestFramework.getCardType().getCost())) {
                        bestFramework = buyDecision;
                    } else if (isMoneyCard(buyDecision) && (bestMoneyCard == null || cost > bestMoneyCard.getCardType().getCost())) {
                        bestMoneyCard = buyDecision;
                    }
                }
            }
        }

        // Prefer buying a Framework if possible; otherwise, buy the best Money card
        BuyDecision chosenDecision = (bestFramework != null) ? bestFramework : bestMoneyCard;

        System.out.println("Big Money Player chose: " + (chosenDecision != null ? chosenDecision.getCardType().name() : "No valid decision"));
        return chosenDecision != null ? chosenDecision : options.get(0); // Fallback if no valid purchase
    }

    // Determines if a decision is for a Framework purchase
    private boolean isFramework(BuyDecision decision) {
        return decision.getCardType().name().toLowerCase().contains("framework");
    }

    // Determines if a decision is for a Money card purchase
    private boolean isMoneyCard(BuyDecision decision) {
        return decision.getCardType().name().toLowerCase().contains("money");
    }
}
