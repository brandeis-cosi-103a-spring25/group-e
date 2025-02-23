package edu.brandeis.cosi103a.groupe;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Decision;
import edu.brandeis.cosi.atg.api.GameObserver;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based player that interacts with the user via input.
 */
public class ConsolePlayer extends Player {
    private final Scanner scanner;
    private final GameObserver observer; // Add observer field

    public ConsolePlayer(String name, GameObserver observer) {
        super(name);
        this.scanner = new Scanner(System.in);
        this.observer = observer; // Store observer locally
    }

    @Override
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

        // Notify observer
        getObserver().ifPresent(obs -> obs.notifyEvent(state, chosenDecision));

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
}
