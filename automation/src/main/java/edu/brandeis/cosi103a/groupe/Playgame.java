package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
 * This class simulates simlpified game play for the Automation card game.
 * 
 * Emily Szabo
 * emilyszabo@brandeis.edu
 * Jan. 27th, 2025
 * COSI 103A ip2
 */
public class Playgame {
    public static void main(String[] args) {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Supply supply = new Supply();
        
        // Automation cards
        Card methodCard = new AutomationCard("Method", 2, 1);
        Card moduleCard = new AutomationCard("Module", 5, 3);
        Card frameworkCard = new AutomationCard("Framework", 8, 6);

        // Cryptocurrency cards
        Card bitcoinCard = new CryptocurrencyCard("Bitcoin", 0, 1);
        Card ethereumCard = new CryptocurrencyCard("Ethereum", 3, 2);
        Card dogecoinCard = new CryptocurrencyCard("Dogecoin", 6, 3);

        // Distribute starter decks to both players
        for (int i = 0; i < 7; i++) {
            player1.addCardToDeck(bitcoinCard);
            supply.takeCard("Bitcoin");
            player2.addCardToDeck(bitcoinCard);
            supply.takeCard("Bitcoin");
        }
        for (int i = 0; i < 3; i++) {
            player1.addCardToDeck(methodCard);
            supply.takeCard("Method");
            player2.addCardToDeck(methodCard);
            supply.takeCard("Method");
        }

        // Shuffle the decks
        player1.shuffleDeck();
        player2.shuffleDeck();

        // Deal initial hands
        player1.drawHand(5);
        player2.drawHand(5);

        // Randomly choose the starting player
        Random random = new Random();
        boolean player1Starts = random.nextBoolean();

        // Track the number of turns
        int turnCounter = 1;
        
        while (supply.getCardQuantity("Framework") > 0) {
            System.out.println("Turn " + turnCounter);
            
            if (player1Starts) {
                playerTurn(player1, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
                playerTurn(player2, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
            } else {
                playerTurn(player2, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
                playerTurn(player1, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
            }
        
            turnCounter++;
            System.out.println();
        }

        // Determine the winner
        int player1Ap = player1.getTotalAp();
        int player2Ap = player2.getTotalAp();

        System.out.println("Final Scores:");
        System.out.println("Player 1 - Total AP: " + player1Ap);
        System.out.println("Player 2 - Total AP: " + player2Ap);

        if (player1Ap > player2Ap) {
            System.out.println("Player 1 wins!");
        } else if (player2Ap > player1Ap) {
            System.out.println("Player 2 wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }

    /**
     * Simulates a player's turn, including the buy phase and cleanup phase.
     * @param player The player taking the turn.
     * @param supply The supply of cards.
     * @param availableCards The available cards for purchase.
     */
    private static void playerTurn(Player player, Supply supply, Card... availableCards) {
        // Buy phase
        player.playHand();
        int totalMoneyInHand = player.getTotalMoneyInHand();
        System.out.println(player.getName() + " has " + totalMoneyInHand + " money in hand.");
        
        System.out.println(player.getName() + "'s hand:");
        for (Card card : player.getHand()) {
            System.out.println("- " + card.getName()+ ", (Value: " + card.getMoney() + ")");
        }

        Card purchasedCard = randomAvailableCard(supply, totalMoneyInHand, availableCards);
        if (purchasedCard != null) {
            player.purchaseCard(purchasedCard, supply);
            System.out.println(player.getName() + " buys: " + purchasedCard.getName() + " (Cost: " + purchasedCard.getCost() + ", AP: " + purchasedCard.getAp() + ")");
            System.out.println(player.getName() + " - Total AP: " + player.getTotalAp() + ", Money left: " + player.getMoney());

            // Check if a Framework card was purchased
            if (purchasedCard instanceof AutomationCard && purchasedCard.getName().equals("Framework")) {
                System.out.println(player.getName() + " purchased a Framework card!");
            }
        } else {
            System.out.println(player.getName() + " could not buy a card.");
        }
        // Cleanup phase
        player.cleanup();
        player.drawHand(5);
        System.out.println();
    }

    /**
     * Selects a random card from the available cards that are in the supply.
     * @param supply The supply of cards.
     * @param playerMoney The amount of money the player has in hand.
     * @param cards The available cards.
     * @return A randomly selected card that is available in the supply, or null if none are available.
     */
    private static Card randomAvailableCard(Supply supply, int playerMoney, Card... cards) {
        List<Card> availableCards = new ArrayList<>();

        for (Card card : cards) {
            if (supply.getCardQuantity(card.getName()) > 0 && card.getCost() <= playerMoney) {
                availableCards.add(card);
            }
        }

        if (availableCards.isEmpty()) {
            return null;
        }
        
        return availableCards.get((int) (Math.random() * availableCards.size()));
    } 
}
