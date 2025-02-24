package edu.brandeis.cosi103a.groupe;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.GameObserver;

import java.util.Optional;
import java.util.Scanner;

public class ConsolePlayer implements Player {
    private final String name;
    private final Scanner scanner;

    public ConsolePlayer(String name) {
        this.name = name;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<GameObserver> getObserver() {
        return Optional.empty();
    }

    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        if (options.isEmpty()) {
            throw new IllegalStateException("No available decisions!");
        }

        System.out.println("\n----- Your Turn: " + name + " -----");
        
        // Display the reason for the decision if available
        reason.ifPresent(event -> System.out.println("Reason: " + event.getDescription()));

        System.out.println("Available decisions:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println(i + ": " + options.get(i));
        }

        int choice = -1;
        while (true) {
            System.out.print("Enter your choice (0-" + (options.size() - 1) + "): ");
            String input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);
                if (choice >= 0 && choice < options.size()) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please select a number within the range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return options.get(choice);
    }
}
