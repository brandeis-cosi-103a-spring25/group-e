package edu.brandeis.cosi103a.groupe;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Decision;
import edu.brandeis.cosi.atg.api.GameObserver;
import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based player implementation that interacts with the user through input.
 * Extends the existing Player class and implements required methods.
 */
public class ConsolePlayer extends Player {
    private final Scanner scanner;

    /**
     * Constructor for ConsolePlayer.
     * @param name The name of the player.
     */
    public ConsolePlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Gets the name of the player.
     * @return The name of the player.
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Makes a decision during the game.
     * Prompts the user to choose from the available decisions.
     * @param state The current game state.
     * @param options The available decisions to choose from.
     * @return The chosen decision.
     */
    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options) {
        if (options.isEmpty()) {
            System.out.println(getName() + ": No decisions available.");
            return null;
        }

        System.out.println(getName() + ", choose a decision:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ": " + options.get(i).toString());
        }

        int choice = getValidChoice(options.size());
        return options.get(choice - 1);
    }

    /**
     * Gets the observer for this player.
     * @return An empty Optional since this player does not require event observation.
     */
    @Override
    public Optional<GameObserver> getObserver() {
        return Optional.empty(); // No observer needed for console-based players
    }

    /**
     * Ensures valid user input within a given range.
     * @param max The maximum valid input.
     * @return A valid choice within the given range.
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
     * Cleans up the scanner resource when the game ends.
     */
    public void closeScanner() {
        scanner.close();
    }
}
