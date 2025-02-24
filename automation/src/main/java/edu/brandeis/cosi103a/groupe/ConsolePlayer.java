package edu.brandeis.cosi103a.groupe;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.event.Event;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based player that interacts with the user via input.
 */
public class ConsolePlayer extends Player {
    private final Scanner scanner;
    private final GameObserver observer; // Store observer locally

    public ConsolePlayer(String name, GameObserver observer) {
        super(name);
        this.scanner = new Scanner(System.in);
        this.observer = observer;
    }

    public Decision makeDecision(GameState state, ImmutableList<Decision> options) {
        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            return null;
        }

        System.out.println(getName() + ", choose a decision:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ": " + options.get(i).getDescription());
        }

        int choice = getValidChoice(options.size());
        Decision chosenDecision = options.get(choice - 1);

        // Notify observer using the inner class DecisionEvent
        getObserver().ifPresent(obs -> obs.notifyEvent(state, (Event) new DecisionEvent(chosenDecision)));

        return chosenDecision;
    }

    /**
     * Implements getObserver() without modifying Player.
     * @return Optional containing the observer if available.
     */
    public Optional<GameObserver> getObserver() {
        return Optional.ofNullable(observer);
    }

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

    public void closeScanner() {
        scanner.close();
    }

    /**
     * Inner class to wrap a Decision as an Event.
     */
    private class DecisionEvent extends Event {
        private final Decision decision;

        public DecisionEvent(Decision decision) {
            super();
            this.decision = decision;
        }

        @Override
        public String toString() {
            return "DecisionEvent: " + decision.getDescription();
        }
    }
}
