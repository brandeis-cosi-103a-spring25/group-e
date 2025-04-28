package edu.brandeis.cosi103a.groupe.Players;
import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.DiscardCardDecision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.GainCardDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.decisions.TrashCardDecision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import java.util.Optional;
import java.util.Random;
/**
 * COSI 103a - Group E
 * April 28th, 2025
 * This class implements a "Big Money" AI player strategy for the game.
 * The player focuses on acquiring high-value money cards and playing them efficiently.
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

    
    /** 
     * Implements `getName()` for the Player interface.
     * @return String representing the player's name.
     */
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
        } else if ("Money".equalsIgnoreCase(phase)) {
            for (Card card : state.getCurrentPlayerHand().getUnplayedCards()) {
                if (card.getCategory() == Card.Type.Category.MONEY) {
                    System.out.println(getName() + " plays Money card: " + card.getType().name());
                    return new PlayCardDecision(card);
                }
            }
            return options.get(0);
        }
            else { // Assumed Action phase
                return makeActionDecision(state, options);
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
                return options.get(0);
            }
            
            System.out.println("Valid buy phase, checking best card...");
    
            BuyDecision[] bestPurchase = {null};
            for (Decision decision : options) {
                if (decision instanceof BuyDecision buyDecision) {
                    int cost = buyDecision.getCardType().getCost();
                    System.out.println("Checking card: " + buyDecision.getCardType().name() + " Cost: " + cost);
                    
                    if (cost <= availableMoney) {
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
        return options.get(0); 
    }
    
    
    /** 
     * This method implements the AI's decision-making for the Action phase.
     * @param state The current game state.
     * @param options The available decisions for the player.
     * @return Decision The chosen action decision, or an EndPhaseDecision if no actions are available.
     */
    private Decision makeActionDecision(GameState state, ImmutableList<Decision> options) {
        Decision actionChoice = null;

        Random randVal = new Random();
        if (options.isEmpty()) {
            System.out.println(getName() + ": No actions to play, ending phase.");
            EndPhaseDecision end = new EndPhaseDecision(state.getTurnPhase());
            return end;
        }
        int decisionChoice = randVal.nextInt(options.size());
        actionChoice = options.get(decisionChoice);
        if (actionChoice != null) {
            if (actionChoice instanceof PlayCardDecision) {

                System.out.println(
                        getName() + " chose to play: " + ((PlayCardDecision) actionChoice).getCard().getDescription());
                final PlayCardDecision finalActionChoice = (PlayCardDecision) actionChoice;
                observer.ifPresent(obs -> obs.notifyEvent(state,
                        new GameEvent(getName() + " played " + finalActionChoice.getCard().getDescription())));
            } else if (actionChoice instanceof DiscardCardDecision) {
                System.out.println(getName() + " chose to discard: "
                        + ((DiscardCardDecision) actionChoice).getCard().getDescription());
                final DiscardCardDecision finalActionChoice = (DiscardCardDecision) actionChoice;
                observer.ifPresent(obs -> obs.notifyEvent(state,
                        new GameEvent(getName() + " played " + finalActionChoice.getCard().getDescription())));

            } else if (actionChoice instanceof TrashCardDecision) {
                System.out.println(getName() + " chose to trash: "
                        + ((TrashCardDecision) actionChoice).getCard().getDescription());
                final TrashCardDecision finalActionChoice = (TrashCardDecision) actionChoice;
                observer.ifPresent(obs -> obs.notifyEvent(state,
                        new GameEvent(getName() + " played " + finalActionChoice.getCard().getDescription())));

            } else if (actionChoice instanceof GainCardDecision) {
                System.out.println(getName() + " chose to gain: " + ((GainCardDecision) actionChoice).getCardType());
                final GainCardDecision finalActionChoice = (GainCardDecision) actionChoice;
                observer.ifPresent(obs -> obs.notifyEvent(state,
                        new GameEvent(getName() + " played " + finalActionChoice.getCardType())));

            }

            return actionChoice;
        }
        return null;
    }
    
    /*
     * This method sets the observer for the player.
     * @param observer The GameObserver to set.
     */
    public void setObserver(GameObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }

}
