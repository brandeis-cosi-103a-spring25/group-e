package edu.brandeis.cosi103a.groupe.Players;

import java.util.Optional;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GameEvent;

public class RandomBuyPlayer implements Player{
    private final String name;
    private Optional<GameObserver> observer = Optional.empty();
    private String phase;

    /**
     * Constructor for RandomBuyPlayer.
     * @param name The player's name.
     */
    public RandomBuyPlayer(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
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

        System.out.println("\n----- " + phase + " Phase: Random Buy Player Turn: " + name + " -----");
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
     * Implements an AI-driven Buy decision for the "Random Buy" strategy.
     * @param state The current game state.
     * @param options The list of available decisions.
     * @return The chosen Buy decision, or null if no valid purchase is possible.
     */
    private Decision makeBuyDecision(GameState state, ImmutableList<Decision> options) {
        int boughtCard = 0;
        Decision buyChoice = null;

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
    
            Random randVal = new Random();
            if (options.size()-1 == 0) {
               return null;
            }
            int decisionChoice =  randVal.nextInt(options.size()-1);
            buyChoice =  options.get(decisionChoice);
           
            
    
            if (buyChoice != null) {
                System.out.println(getName() + " chose to buy: " + ((BuyDecision) buyChoice).getCardType().name());
                final BuyDecision finalBuyChoice = (BuyDecision) buyChoice;
                observer.ifPresent(obs -> obs.notifyEvent(state, new GameEvent(getName() + " bought " + finalBuyChoice.getCardType().name())));
                boughtCard++;
                return buyChoice;
            }
        }
        
        System.out.println(getName() + ": No valid buy available. Returning options.get(0)");
        return options.get(0); // Safe fallback
    }
    


    public void setObserver(GameObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }
}
