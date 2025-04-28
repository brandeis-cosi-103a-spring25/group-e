package edu.brandeis.cosi103a.groupe.Engine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi103a.groupe.Other.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.Players.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupe.Players.RandomBuyPlayer;
import edu.brandeis.cosi103a.groupe.Players.SmartActionPlayer;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
/*
 * COSI 103A-Group E
 * April 28th, 2025
 * This class simulates a series of games between players to evaluate their performance.
 * It allows users to select players, specify the number of games per matchup, and displays the results.
 */
public class SimulationHarness {
    private int numGames;
    private List<ourPlayer> players;
    private Map<ourPlayer, Integer> totalWins; 
    private Map<ourPlayer, Integer> totalLosses; 
    private Map<ourPlayer, Integer> totalAp;  
    private List<String> matchupResults; // Store matchup results

    /**
     * Constructor for SimulationHarness.
     * @param numGames The number of games to simulate per matchup.
     */
    public SimulationHarness(int numGames) {
        this.numGames = numGames;
        this.players = new ArrayList<>();
        this.totalWins = new HashMap<>();
        this.totalLosses = new HashMap<>();
        this.totalAp = new HashMap<>();
        this.matchupResults = new ArrayList<>(); 
    }
    
    /**
     * Adds a player to the simulation.
     * @param player The player to add.
     */
    public void addPlayer(ourPlayer player) {
        players.add(player);
        totalWins.put(player, 0);
        totalLosses.put(player, 0);
        totalAp.put(player, 0);
    }
    
    /**
    * Runs the simulation for all distinct pairs of players.
    * @throws PlayerViolationException 
    */
    public void run() throws PlayerViolationException {
        if (players.size() < 2) {
            System.out.println("At least two players are required to run the simulation.");
            return;
        }

        System.out.println("Running simulation with " + numGames + " games per matchup...");
        // Iterate through all distinct pairs of players
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                simulateMatchup(players.get(i), players.get(j));
            }
        }
        
        // Print all matchup results
        System.out.println("\nAll Matchup Results:");
        for (String result : matchupResults) {
          System.out.println(result);
        }
        printOverallResults();
    }
   
    /**
     * Simulates a matchup between two players for the specified number of games.
     * @param player1 The first player.
     * @param player2 The second player.
     * @throws PlayerViolationException 
     */
    public void simulateMatchup(ourPlayer player1, ourPlayer player2) throws PlayerViolationException {
        int player1Wins = 0;
        int player2Wins = 0;
        int ties = 0; // Track the number of ties
        int player1TotalAp = 0;
        int player2TotalAp = 0;

        for (int i = 0; i < numGames; i++) {
            GameEngine engine = createGameEngine(player1, player2);
            engine.play();
            
            ImmutableList<ScorePair> scores = engine.determineWinner();
            int player1Score = scores.get(0).getScore();
            int player2Score = scores.get(1).getScore();

            player1TotalAp += player1Score;
            player2TotalAp += player2Score;

            if (player1Score > player2Score) {
                player1Wins++;
            } else if (player2Score > player1Score) {
                player2Wins++;
            } else {
                ties++; // Increment ties if scores are equal
            }
            player1.clear();
            player2.clear();
        }
        
        // Update global stats
        totalWins.put(player1, totalWins.get(player1) + player1Wins);
        totalWins.put(player2, totalWins.get(player2) + player2Wins);
        totalLosses.put(player1, totalLosses.get(player1) + player2Wins);
        totalLosses.put(player2, totalLosses.get(player2) + player1Wins);
        totalAp.put(player1, totalAp.get(player1) + player1TotalAp);
        totalAp.put(player2, totalAp.get(player2) + player2TotalAp);

        // Log matchup results
       String result = String.format(
          "\nMatchup Results: %s vs %s\n%s - Wins: %d, Avg AP: %.2f\n%s - Wins: %d, Avg AP: %.2f\nTies: %d",
          player1.getName(), player2.getName(),
          player1.getName(), player1Wins, (double) player1TotalAp / numGames,
          player2.getName(), player2Wins, (double) player2TotalAp / numGames,
          ties
      );
      matchupResults.add(result);
    }
    
    /**
     * Prints the overall results for all players after all matchups are completed.
     */
    public void printOverallResults() {
        System.out.println("\nOverall Results After All Matchups:");
        for (ourPlayer player : players) {
            int wins = totalWins.get(player);
            int losses = totalLosses.get(player);
            int totalGames = wins + losses;
            double winRate = totalGames == 0 ? 0.0 : (double) wins / totalGames * 100;
            double avgAp = totalGames == 0 ? 0.0 : (double) totalAp.get(player) / totalGames;

            System.out.printf("%s - Wins: %d, Losses: %d, Win Rate: %.2f%%, Avg AP: %.2f%n", 
                              player.getPlayer().getName(), wins, losses, winRate, avgAp);
        }
    }
    
    
   
    /**
     * Gets the list of players in the simulation.
     * @return The list of players.
     */
    public List<ourPlayer> getPlayers() {
        return players;
    }

    /**
     * Gets the total number of wins for each player.
     * @return The map of players to total wins.
     */
    public Map<ourPlayer, Integer> getTotalAp() {
        return totalAp;
    }
    
    /**
     * Gets the total number of wins for each player.
     * @return The map of players to total wins.
     */
    public Map<ourPlayer, Integer> getTotalWins() {
        return totalWins;
    }
    
    /**
     * Gets the total number of losses for each player.
     * @return The map of players to total losses.
     */
    public Map<ourPlayer, Integer> getTotalLosses() {
        return totalLosses;
    }

    
    /** 
     * Creates a GameEngine instance for the specified players.
     * @param player1
     * @param player2
     * @return GameEngine
     */
    public GameEngine createGameEngine(ourPlayer player1, ourPlayer player2) {
        return new GameEngine(player1, player2, new ConsoleGameObserver());
    }

    /**
     * Main method to run the simulation harness.
     * Allows the user to select players and set the number of games per matchup.
     * @throws PlayerViolationException 
     */
    public static void main(String[] args) throws PlayerViolationException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Player Rating Harness!");

        System.out.print("Enter the number of games per matchup: ");
        int numGames = scanner.nextInt();

        SimulationHarness harness = new SimulationHarness(numGames);

        // Hardcoded list of available players
        List<Player> availablePlayers = List.of(
            new BigMoneyPlayer("Big Money 1"),
            new BigMoneyPlayer("Big Money 2"),
            new RandomBuyPlayer("Random Buy 1"),
            new RandomBuyPlayer("Random Buy 2"),
            new SmartActionPlayer("Smart Action 1"),
            new SmartActionPlayer("Smart Action 2")
        );

        System.out.println("Available players:");
        for (int i = 0; i < availablePlayers.size(); i++) {
            System.out.printf("%d: %s%n", i + 1, availablePlayers.get(i).getName());
        }



        System.out.println("Select players to include in the simulation (enter numbers separated by spaces):");
        scanner.nextLine();

        String[] selectedIndices = scanner.nextLine().split(" ");
        for (String index : selectedIndices) {
            try{
              int playerIndex = Integer.parseInt(index) - 1;
              if (playerIndex >= 0 && playerIndex < availablePlayers.size()) {
                ourPlayer newPlayer = new ourPlayer(availablePlayers.get(playerIndex).getName());
                newPlayer.setPlayer(availablePlayers.get(playerIndex));
                harness.addPlayer(newPlayer);
              } else {
                System.out.println("Invalid player index: " + index);
              }
            } catch (NumberFormatException e) {
              System.out.println("Invalid input: " + index);
            }
        }

        harness.run();
        scanner.close();
    }
}