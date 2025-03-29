package edu.brandeis.cosi103a.groupe.Players;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.GameState.TurnPhase;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import java.util.Optional;

/**
 * AI player that follows the "Big Money" strategy.
 */
public class BigMoneyPlayer implements Player {
    private final String name;
    private Optional<GameObserver> observer = Optional.empty();
    private String phase;

    /**
     * Constructor for BigMoneyPlayer.
     * @param name The player's name.
     */
    public BigMoneyPlayer(String name) {
        super();
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

   

    /**
     * Implements `makeDecision()` for the AI player.
     * @param state The current game state.
     * @param options The available decision options.
     * @param reason The reason for this decision prompt.
     * @return The selected decision.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
     
        this.phase = state.getTurnPhase().toString(); // Get the current phase of the game
        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            return null;
        }

        System.out.println("\n----- " + phase + " Phase: Big Money Player Turn: " + name + " -----");
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        if ("Buy".equalsIgnoreCase(phase)) {
            return makeBuyDecision(state, options);
        } 
        else{
            setMoney(playHand());
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
        int boughtCard = 0;
        int availableMoney = state.getSpendableMoney(); 
        //int availableMoney = getMoney();  // Make sure this is correct
    
        System.out.println("Checking buy decision for " + getName());
        System.out.println("Available money: " + availableMoney);
        System.out.println("Available buys: " + state.getAvailableBuys());
        
        if (boughtCard < 1) {
            int availableBuys = state.getAvailableBuys();
            if (availableBuys <= 0 || availableMoney <= 0) {
                System.out.println(getName() + ": No available buys left. Returning options.get(0)");
                return options.get(0); // Safe fallback
            }
            
            System.out.println("Valid buy phase, checking best card...");
    
            BuyDecision[] bestPurchase = {null};
            for (Decision decision : options) {
                if (decision instanceof BuyDecision buyDecision) {
                    int cost = buyDecision.getCardType().getCost();
                    System.out.println("Checking card: " + buyDecision.getCardType().name() + " Cost: " + cost);
                    
                    if (cost <= availableMoney) { // Only consider affordable purchases
                        if (bestPurchase[0] == null || cost > bestPurchase[0].getCardType().getCost()) {
                            System.out.println("Selecting: " + buyDecision.getCardType().name());
                            bestPurchase[0] = buyDecision;
                        }
                    }
                }
            }
    
            if (bestPurchase[0] != null) {
                System.out.println(getName() + " chose to buy: " + bestPurchase[0].getCardType().name());
                observer.ifPresent(obs -> obs.notifyEvent(state, new GameEvent(getName() + " bought " + bestPurchase[0].getCardType().name())));
                boughtCard++;
                return bestPurchase[0];
            }
        }
        
        System.out.println(getName() + ": No valid buy available. Returning options.get(0)");
        return options.get(0); // Safe fallback
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
