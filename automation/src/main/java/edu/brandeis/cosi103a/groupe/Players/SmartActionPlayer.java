package edu.brandeis.cosi103a.groupe.Players;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.*;
import edu.brandeis.cosi.atg.api.event.Event;
import java.util.Optional;
 
public class SmartActionPlayer implements Player {
    private final String name;
    private Optional<GameObserver> observer = Optional.empty();
    private int turnNumber = 0;
    
    /*
     * Constructs an EngineBuilderPlayer with the given name.
     */
    public SmartActionPlayer(String name) {
        this.name = name;
    }
    
    
    /** 
     * @return String
     */
    /*
     * Returns the name of the player.
     */
    @Override
    public String getName() {
        return name;
    }
    
    
    /** 
     * @param state
     * @param options
     * @param reason
     * @return Decision
     */
    /*
     * This method takes the current game state and list of available options,
     * and returns the player's decision based on the game phase.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        // Increment turn number at the start of the ACTION phase
        if (state.getTurnPhase() == GameState.TurnPhase.ACTION) {
            turnNumber++;
            System.out.println("Turn " + turnNumber + " begins for " + name);
        }

        System.out.println("\n----- " + state.getTurnPhase() + " Phase: Engine Builder Turn: " + name + " -----");
        System.out.println("Available options:");
        options.forEach(option -> System.out.println("- " + option.getDescription()));

        if (options.size() == 1) {
            System.out.println("Only one option available. Choosing: " + options.get(0).getDescription());
            return options.get(0);
        }

        switch (state.getTurnPhase()) {
            case ACTION:
                return chooseActionCard(options);
            case BUY:
                return chooseBuyCard(state, options);
            case DISCARD:
                return chooseDiscardCard(options);
            case MONEY:
                return chooseMoneyCard(options);
            case REACTION:
                return chooseReactionCard(options);
            case GAIN:
                return chooseGainCard(state, options);
            default:
                return options.stream()
                        .filter(opt -> opt instanceof EndPhaseDecision)
                        .findFirst()
                        .orElse(null);
        }
    }
    
    /*
     * Chooses the best action card to play based on the available options.
     */
    private Decision chooseActionCard(ImmutableList<Decision> options) {
        System.out.println("Choosing an action card...");
        
        // First, try to play a prioritized action card
        Optional<Decision> prioritizedAction = options.stream()
                .filter(decision -> decision instanceof PlayCardDecision)
                .filter(decision -> {
                    Card.Type type = ((PlayCardDecision) decision).getCard().getType();
                    return type == Card.Type.CODE_REVIEW || type == Card.Type.REFACTOR || type == Card.Type.DAILY_SCRUM
                            || type == Card.Type.PARALLELIZATION || type == Card.Type.HACK || type == Card.Type.EVERGREEN_TEST;
                })
                .sorted((d1, d2) -> {
                    Card.Type type1 = ((PlayCardDecision) d1).getCard().getType();
                    Card.Type type2 = ((PlayCardDecision) d2).getCard().getType();
                    return Integer.compare(getActionPriority(type2), getActionPriority(type1)); // Higher priority first
                })
                .findFirst();
    
        // If no prioritized action card is found, play any available action card
        if (prioritizedAction.isPresent()) {
            return prioritizedAction.get();
        }
    
        System.out.println("No prioritized action card found. Playing any available action card...");
        return options.stream()
                .filter(decision -> decision instanceof PlayCardDecision)
                .findFirst()
                .orElse(null); // If no action cards are available, return null
    }
    
    /*
     * This method assigns a priority to each action card type.
     */
    private int getActionPriority(Card.Type type) {
        switch (type) {
            case CODE_REVIEW: return 100;
            case REFACTOR: return 90;
            case DAILY_SCRUM: return 80;
            case PARALLELIZATION: return 70;
            case HACK: return 60;
            case EVERGREEN_TEST: return 50;
            default: return 10;
        }
    }
    
    /*
     * Chooses the best card to buy based on the available options.
     */
    private Decision chooseBuyCard(GameState state, ImmutableList<Decision> options) {
        System.out.println("Choosing the best card to buy...");
        int money = state.getSpendableMoney();
    
        // Filter, sort, and select the best card
        return options.stream()
                .filter(decision -> decision instanceof BuyDecision) // Only consider BuyDecision options
                .map(decision -> (BuyDecision) decision)
                .filter(buy -> buy.getCardType().getCost() <= money) // Only consider cards the player can afford
                .sorted((b1, b2) -> {
                    int cost1 = b1.getCardType().getCost();
                    int cost2 = b2.getCardType().getCost();
                    return Integer.compare(cost2, cost1); // Sort by cost (most expensive first)
                })
                .findFirst() // Select the most expensive card
                .orElse(null);
    }

    /*
     * Chooses least valuble card to discard based on the available options.
     */
    private Decision chooseDiscardCard(ImmutableList<Decision> options) {
        System.out.println("Choosing a card to discard...");
        return options.stream()
                .filter(decision -> decision instanceof DiscardCardDecision)
                .min((d1, d2) -> {
                    int value1 = ((DiscardCardDecision) d1).getCard().getValue();
                    int value2 = ((DiscardCardDecision) d2).getCard().getValue();
                    return Integer.compare(value1, value2); // Discard least valuable card first
                })
                .orElse(null);
    }
    
    /*
     * Chooses the best money card to play based on the available options.
     */
    private Decision chooseMoneyCard(ImmutableList<Decision> options) {
        System.out.println("Choosing a money card to play...");
        return options.stream()
                .filter(decision -> decision instanceof PlayCardDecision)
                .sorted((d1, d2) -> {
                    int value1 = ((PlayCardDecision) d1).getCard().getValue();
                    int value2 = ((PlayCardDecision) d2).getCard().getValue();
                    return Integer.compare(value2, value1); // Play most valuable card first
                })
                .findFirst()
                .orElse(null);
    }
    
    /*
     * Plays monitoring cards if available.
     */
    private Decision chooseReactionCard(ImmutableList<Decision> options) {
        System.out.println("Choosing a reaction card...");
        return options.stream()
                .filter(decision -> decision instanceof PlayCardDecision)
                .filter(decision -> ((PlayCardDecision) decision).getCard().getType() == Card.Type.MONITORING)
                .findFirst()
                .orElse(null);
    }
     
    /*
     * Chooses the best card to gain based on the available options.
     */
    private Decision chooseGainCard(GameState state, ImmutableList<Decision> options) {
        System.out.println("Choosing a card to gain...");
        int costLimit = state.getSpendableMoney();

        return options.stream()
                .filter(decision -> decision instanceof GainCardDecision)
                .filter(decision -> ((GainCardDecision) decision).getCardType().getCost() <= costLimit)
                .sorted((d1, d2) -> {
                    int cost1 = ((GainCardDecision) d1).getCardType().getCost();
                    int cost2 = ((GainCardDecision) d2).getCardType().getCost();
                    return Integer.compare(cost2, cost1); // Gain most expensive card first
                })
                .findFirst()
                .orElse(null);
    }
    
    /*
     * Returns the observer for the player.
     */
    @Override
    public Optional<GameObserver> getObserver() {
        return observer;
    }
}
