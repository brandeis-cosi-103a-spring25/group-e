package edu.brandeis.cosi103a.groupe.Players;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableCollection;
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

public class RandomBuyPlayer implements Player {
    private final String name;
    private Optional<GameObserver> observer = Optional.empty();
    private String phase;

    /**
     * Constructor for RandomBuyPlayer.
     * 
     * @param name The player's name.
     */
    public RandomBuyPlayer(String name) {
        super();
        this.name = name;
    }

    
    /** 
     * @return String
     */
    @Override
    public String getName() {
        return name;
    }

    
    /** 
     * @return Optional<GameObserver>
     */
    @Override
    public Optional<GameObserver> getObserver() {
        return observer;
    }

    /**
     * Implements `makeDecision()` for the AI player.
     * 
     * @param state   The current game state.
     * @param options The available decision options.
     * @param reason  The reason for this decision prompt.
     * @return The selected decision.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {

        this.phase = state.getTurnPhase().toString(); // Get the current phase of the game

        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            EndPhaseDecision end = new EndPhaseDecision(state.getTurnPhase());
            return end;        
        }

        System.out.println("\n----- " + phase + " Phase: Random Buy Player Turn: " + name + " -----");
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        if ("Buy".equalsIgnoreCase(phase)) {
            return makeBuyDecision(state, options);
        } else if ("Money".equalsIgnoreCase(phase)) {
            ImmutableCollection<Card> playableHand = (state.getCurrentPlayerHand().getUnplayedCards());
            for (Card card : playableHand) {
                if (card.getCategory() == Card.Type.Category.MONEY) {
                    observer.ifPresent(obs -> obs.notifyEvent(state,
                            new GameEvent(getName() + " bought " + card.getDescription())));
                    return new PlayCardDecision(card);
                }
            }
            return null;
        } else { // Assumed Action phase
            return makeActionDecision(state, options);
        }
    }

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

    /**
     * Implements an AI-driven Buy decision for the "Random Buy" strategy.
     * 
     * @param state   The current game state.
     * @param options The list of available decisions.
     * @return The chosen Buy decision, or null if no valid purchase is possible.
     */
    private Decision makeBuyDecision(GameState state, ImmutableList<Decision> options) {
        Random randVal = new Random();

        int availableMoney = state.getSpendableMoney();
        int availableBuys = state.getAvailableBuys();

        System.out.println("Checking buy decision for " + getName());
        System.out.println("Available money: " + availableMoney);
        System.out.println("Available buys: " + availableBuys);

        if (availableBuys <= 0 || availableMoney <= 0) {
            System.out.println(getName() + ": No available buys or money. Ending phase.");
            return new EndPhaseDecision(state.getTurnPhase());
        }

        // Filter valid BuyDecision options
        List<BuyDecision> buyOptions = options.stream()
                .filter(d -> d instanceof BuyDecision)
                .map(d -> (BuyDecision) d)
            
                .collect(Collectors.toList());

        if (buyOptions.isEmpty()) {
            System.out.println(getName() + ": No valid buy decisions. Ending phase.");
            return new EndPhaseDecision(state.getTurnPhase());
        }

        int decisionChoice = randVal.nextInt(buyOptions.size());
        BuyDecision buyChoice = buyOptions.get(decisionChoice);

        System.out.println(getName() + " chose to buy: " + buyChoice.getCardType().name());
        observer.ifPresent(obs -> obs.notifyEvent(state,
                new GameEvent(getName() + " bought " + buyChoice.getCardType().name())));

        return buyChoice;
    }

    public void setObserver(GameObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }
}
