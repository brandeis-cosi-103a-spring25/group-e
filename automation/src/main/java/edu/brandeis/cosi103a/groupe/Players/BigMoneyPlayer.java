package edu.brandeis.cosi103a.groupe.Players;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import java.util.Optional;

/**
 * AI player that follows the "Big Money" strategy.
 */
public class BigMoneyPlayer extends ourPlayer {
    private final String name;
    private Optional<GameObserver> observer = Optional.empty();
    private String phase;

    /**
     * Constructor for BigMoneyPlayer.
     * @param name The player's name.
     */
    public BigMoneyPlayer(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Implements `getObserver()`, required by the Player interface.
     * @return Optional containing the observer if available.
     */
    @Override
    public Optional<GameObserver> getObserver() {
        return observer;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * Implements `makeDecision()` for the AI player.
     * @param state The current game state.
     * @param options The available decision options.
     * @param reason The reason for this decision prompt.
     * @return The selected decision.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            return null;
        }

        System.out.println("\n----- " + phase + " Phase: Big Money Player Turn: " + name + " -----");
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        if ("Buy".equalsIgnoreCase(phase)) {
            return makeBuyDecision(state, options);
        } else {
            playHand();
            return null;
        }
    }

    /**
     * Implements an AI-driven Buy decision for the "Big Money" strategy.
     * @param state The current game state.
     * @param options The list of available decisions.
     * @return The chosen Buy decision, or null if no valid purchase is possible.
     */
    private Decision makeBuyDecision(GameState state, ImmutableList<Decision> options) {
        int availableMoney = getMoney();
        int availableBuys = state.getAvailableBuys();

        if (availableBuys <= 0 || availableMoney <= 0) {
            System.out.println(name + ": No available buys left.");
            return options.get(0); // Safe fallback
        }

        final BuyDecision[] bestPurchase = {null};
        for (Decision decision : options) {
            if (decision instanceof BuyDecision buyDecision) {
                int cost = buyDecision.getCardType().getCost();
                if (cost <= availableMoney) { // Only consider affordable purchases
                    if (bestPurchase[0] == null || cost > bestPurchase[0].getCardType().getCost()) {
                        bestPurchase[0] = buyDecision;
                    }
                }
            }
        }

        if (bestPurchase[0] != null) {
            System.out.println(name + " chose to buy: " + bestPurchase[0].getCardType().name());
            observer.ifPresent(obs -> obs.notifyEvent(state, new GameEvent(name + " bought " + bestPurchase[0].getCardType().name())));
            return bestPurchase[0];
        }

        System.out.println(name + ": No valid buy available.");
        return options.get(0); // Safe fallback to prevent errors
    }

    public void setObserver(GameObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }

    // Utility methods to classify cards
    private boolean isFramework(BuyDecision decision) {
        return decision.getCardType() == Card.Type.FRAMEWORK;
    }

    private boolean isMoneyCard(BuyDecision decision) {
        return decision.getCardType().getCategory() == Card.Type.Category.MONEY;
    }
}
