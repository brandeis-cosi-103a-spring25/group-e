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

public class EngineHarness{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Automation: The Game!");

        // Select Player 1 Type
        System.out.print("Select Player 1 (1: Human, 2: AI BigMoney, 3: AI RandomBuy): ");
        Player player1 = createPlayer(scanner, "Player 1");
        ourPlayer ourPlayer1 = new ourPlayer(player1.getName());
           ourPlayer1.setPlayer(player1);

        // Select Player 2 Type
        System.out.print("Select Player 2 (1: Human, 2: AI BigMoney, 3: AI RandomBuy): ");
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
     * Helper method to create either a human or AI player.
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
                default:
                    System.out.print("Invalid choice. Enter 1 (Human), 2 (AI BigMoney), or 3 (AI RandomBuy): ");
            }
        }
    }
}

