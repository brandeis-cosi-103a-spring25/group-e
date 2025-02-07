package edu.brandeis.cosi103a.groupe;

import java.util.List;
import java.util.*;

/**
 * ConsolePlayer class that extends Player and allows user input through the console.
 */
public class ConsolePlayer extends Player {
    private Scanner scanner;

    /**
     * Constructor for the ConsolePlayer.
     * @param name The name of the player.
     */
    public ConsolePlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Makes a decision by prompting the user through the console.
     * @return The chosen card to play or purchase.
     */
    public void playTurn() {
        System.out.println("It's your turn, " + getName() + "!");
        displayHand();
        
        System.out.println("1. Play Hand");
        System.out.println("2. Purchase Card");
        System.out.println("3. Shuffle Deck");
        System.out.println("Enter your choice: ");
        
        int choice = getUserInput(1, 3);
        
        switch (choice) {
            case 1:
                playHand();
                System.out.println("Played hand. Your total money: " + getMoney());
                break;
            case 2:
                purchaseCardFromHand();
                break;
            case 3:
                shuffleDeck();
                System.out.println("Deck shuffled.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Displays the player's current hand.
     */
    private void displayHand() {
        List<Card> hand = getHand();
        System.out.println("Your hand:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i).toString());
        }
    }

    /**
     * Allows the user to select a card from hand to purchase.
     */
    private void purchaseCardFromHand() {
        if (getHand().isEmpty()) {
            System.out.println("You have no cards to purchase.");
            return;
        }
        
        displayHand();
        System.out.println("Select a card to purchase (enter index): ");
        
        int choice = getUserInput(0, getHand().size() - 1);
        Card selectedCard = getHand().get(choice);
        
        System.out.println("Attempting to purchase " + selectedCard.getName());
        // Assuming a Supply object needs to be provided to purchase the card
        Supply supply = new Supply(); // This should be properly initialized
        purchaseCard(selectedCard, supply);
        
        System.out.println("Purchased: " + selectedCard.getName());
    }

    /**
     * Gets user input within a valid range.
     * @param min The minimum valid input.
     * @param max The maximum valid input.
     * @return The user's chosen input.
     */
    private int getUserInput(int min, int max) {
        int input = -1;
        while (input < min || input > max) {
            System.out.print("Enter a number between " + min + " and " + max + ": ");
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input < min || input > max) {
                    System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }
        return input;
    }
}
