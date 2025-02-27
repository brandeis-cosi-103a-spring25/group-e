package edu.brandeis.cosi103a.groupe;

import edu.brandeis.cosi.atg.api.Engine;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi103a.groupe.Players.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupe.Players.ConsolePlayer;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

import com.google.common.collect.ImmutableList;

import java.util.Scanner;

public class EngineHarness{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Automation: The Game!");

        // Select Player 1 Type
        System.out.print("Select Player 1 (1: Human, 2: AI BigMoney): ");
        ourPlayer player1 = createPlayer(scanner, "Player 1");

        // Select Player 2 Type
        System.out.print("Select Player 2 (1: Human, 2: AI BigMoney): ");
        ourPlayer player2 = createPlayer(scanner, "Player 2");

        // Initialize Observer to print game events
        GameObserver observer = new ConsoleGameObserver();

        // Create and run the game engine
        Engine engine = GameEngine.makeEngine(player1, player2, observer);
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
    private static ourPlayer createPlayer(Scanner scanner, String playerName) {
        while (true) {
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return new ConsolePlayer(playerName);
                case "2":
                    return new BigMoneyPlayer(playerName);
                default:
                    System.out.print("Invalid choice. Enter 1 (Human) or 2 (AI BigMoney): ");
            }
        }
    }
}

