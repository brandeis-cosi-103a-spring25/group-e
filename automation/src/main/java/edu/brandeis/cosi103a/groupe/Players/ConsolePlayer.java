package edu.brandeis.cosi103a.groupe.Players;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based player that interacts with the user via input.
 * Implements the necessary decision-making process.
 */
//extend ourplayer 
public class ConsolePlayer implements Player{
    private final Scanner scanner;
    private String phase, name;
    private Optional<GameObserver> observer; // Observer is optional

    /**
     * Constructor for ConsolePlayer.
     * @param name The player's name.
     */
    public ConsolePlayer(String name) {
        super();
         this.name = name;
        this.scanner = new Scanner(System.in);
        this.observer = Optional.empty(); // Default to no observer
    }

    /**
     * Constructor with Observer.
     * @param name The player's name.
     * @param observer The observer for game events.
     */
    /*public ConsolePlayer(String name, GameObserver observer) {
        this.name = name;
        this.scanner = new Scanner(System.in);
        this.observer = Optional.ofNullable(observer);
    }*/

    /**
     * Implements `getObserver()` required by the Player interface.
     * @return Optional containing the observer if available.
     */
    @Override
    public Optional<GameObserver> getObserver() {
        return observer;
    }

   

    /**
     * Implements `makeDecision()`, prompting the player to choose an action.
     * Notifies the observer (if present) after a decision is made.
     *
     * @param state  The current game state.
     * @param options The list of available decisions.
     * @param reason The reason for prompting this decision.
     * @return The chosen decision.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        this.phase = state.getTurnPhase().toString(); // Get the current phase of the game

        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            return null;
        }

        System.out.println("\n----- " + phase + " Phase: Console Player Turn: " + name + " -----");

        
        System.out.println(getName() + ", choose a decision:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ": " + options.get(i).getDescription());
        }
        int availableMoney = getMoney();
        int choice = getValidChoice(options.size());
        Decision chosenDecision = options.get(choice - 1);

        // Notify observer of the decision
                    if (chosenDecision instanceof BuyDecision buyDecision) {
                        int cost = buyDecision.getCardType().getCost();
                        availableMoney -= cost;
                    }


        observer.ifPresent(obs -> obs.notifyEvent(state, new GameEvent(getName() + " chose: " + chosenDecision.getDescription())));
        // ArrayList<Card> playedCards = new ArrayList<>();
        // List<Card> allCards = getCards();
        // playedCards.add(allCards.get(choice)); 
        
        // setPlayedCards(ImmutableList.copyOf(playedCards));
        return chosenDecision;
                
    }

    /**
     * Ensures the player provides a valid numeric input within a given range.
     *
     * @param max The maximum valid input.
     * @return A valid choice.
     */
    private int getValidChoice(int max) {
        int choice;
        while (true) {
            System.out.print("Enter choice (1-" + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid input. Please enter a number between 1 and " + max + ".");
        }
    }

    /**
     * Closes the scanner resource when the game ends.
     */
    public void closeScanner() {
        scanner.close();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
}
