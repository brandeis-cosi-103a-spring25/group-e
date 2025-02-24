package edu.brandeis.cosi103a.groupe;


import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based player that interacts with the user via input.
 * Implements the necessary decision-making process while notifying the observer.
 */
public class ConsolePlayer implements Player {
    private final Scanner scanner;

    /**
     * Constructor for ConsolePlayer.
     * @param name The player's name.
     * @param observer The game observer to track player decisions.
     */
    public ConsolePlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts the player to make a decision from the given options.
     * Notifies the game observer after a decision is made.
     * 
     * @param state The current game state.
     * @param options The list of available decisions.
     * @return The chosen decision.
     */
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

        // Notify observer using GameEvent
        getObserver().ifPresent(obs -> obs.notifyEvent(state, new GameEvent(getName() + " chose: " + chosenDecision.getDescription())));

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
}
