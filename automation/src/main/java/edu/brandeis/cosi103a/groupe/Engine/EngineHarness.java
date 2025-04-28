package edu.brandeis.cosi103a.groupe.Engine;

import edu.brandeis.cosi.atg.api.Engine;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi103a.groupe.Other.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.Players.*;
import edu.brandeis.cosi.atg.api.PlayerViolationException;

import com.google.common.collect.ImmutableList;

import java.util.Scanner;
/*
 * COSI 103a-Group E
 * April 28th, 2025
 * This class serves as the main entry point for the Automation game.
 * It initializes the game, allows players to select their types, and starts the game engine.
 * It also handles the game observer to print game events and final scores.
 */
public class EngineHarness{
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Automation: The Game!");

        // Select Player 1 Type
        System.out.print("Select Player 1 (1: Human, 2: AI BigMoney, 3: AI RandomBuy, 4: AI SmartAction, 5: Network Player): ");
        Player player1 = createPlayer(scanner, "Player 1");
        ourPlayer ourPlayer1 = new ourPlayer(player1.getName());
           ourPlayer1.setPlayer(player1);

        // Select Player 2 Type
        System.out.print("Select Player 2 (1: Human, 2: AI BigMoney, 3: AI RandomBuy, 4: AI SmartAction, 5: Network Player): ");
        Player player2 = createPlayer(scanner, "Player 2");
        ourPlayer ourPlayer2 = new ourPlayer(player2.getName());
           ourPlayer2.setPlayer(player2);


        // Initialize Observer to print game events
        GameObserver observer = new ConsoleGameObserver();

        // Create and run the game engine
        Engine engine = GameEngine.makeEngine(ourPlayer1, ourPlayer2, observer);
        try {
            ImmutableList<ScorePair> results = engine.play();

            // Print Final Scores
            System.out.println("Game Over! Final Scores:");
            for (ScorePair result : results) {
                System.out.println(result.player.getName() + ": " + result.getScore() + " points");
            }
        } catch (PlayerViolationException e) {
            System.err.println("A player violated the rules: " + e.getMessage());
        }
    }

    /**
     * This method prompts the user to select a player type and returns the corresponding Player object.
     * @param scanner The Scanner object to read user input.
     * @param playerName The name of the player to be created.
     * @return A Player object based on the user's choice.
     *         1 for Human, 2 for AI BigMoney, 3 for AI RandomBuy, 4 for AI SmartAction.
     */
    private static Player createPlayer(Scanner scanner, String playerName) {
        while (true) {
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return (Player) new ConsolePlayer(playerName);
                case "2":
                    return (Player) new BigMoneyPlayer(playerName);
                case "3": 
                    return (Player) new RandomBuyPlayer(playerName);
                case "4":
                    return (Player) new SmartActionPlayer(playerName);
                case "5":
                    return (Player) new networkPlayer("https://localhost:8080", playerName);
                default:
                    System.out.print("Invalid choice. Enter 1 (Human), 2 (AI BigMoney), 3 (AI RandomBuy) or 4 (AI SmartAction), 5 (Network Player): ");
            }
        }
    }
}

