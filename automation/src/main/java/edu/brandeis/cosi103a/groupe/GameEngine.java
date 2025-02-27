package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import edu.brandeis.cosi.atg.api.Player;
import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Engine;
import edu.brandeis.cosi.atg.api.EngineCreator;
import edu.brandeis.cosi.atg.api.GameDeck;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GainCardEvent;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import edu.brandeis.cosi.atg.api.event.PlayCardEvent;
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
import edu.brandeis.cosi103a.groupe.Cards.CryptocurrencyCard;

/*
 * This class simulates simlpified game play for the Automation card game.
 * 
 *
 */
public class GameEngine implements Engine {
    private final GameObserver observer;
    private final Player player1, player2;
    private final Supply supply;
    
    public GameEngine(Player player1, Player player2, GameObserver observer) {
        this.player1 = player1;
        this.player2 = player2;
        this.supply = new Supply();
        this.observer = observer;
    }
    
    @EngineCreator
    public static Engine makeEngine(Player player1, Player player2, GameObserver observer){
        Engine thisEngine = new GameEngine(player1, player2, observer);
        return thisEngine;

    }

    @Override
    public ImmutableList<ScorePair> play() throws PlayerViolationException {
        
        // ASSUME 10 OF EACH CARD TYPE- CHANGE IF NEEDED
        // for (int i =1; i <= 10; i ++) {
            AutomationCard methodCard = new AutomationCard(Card.Type.METHOD, 1);
            AutomationCard moduleCard = new AutomationCard(Card.Type.MODULE, 11);
            AutomationCard frameworkCard = new AutomationCard(Card.Type.FRAMEWORK, 21);
            CryptocurrencyCard bitcoinCard = new CryptocurrencyCard(Card.Type.BITCOIN, 31);
            bitcoinCard.setStuff(1, "Bitcoin");
            CryptocurrencyCard ethereumCard = new CryptocurrencyCard(Card.Type.ETHEREUM, 41);
            ethereumCard.setStuff(2, "Ethereum");
            CryptocurrencyCard dogecoinCard = new CryptocurrencyCard(Card.Type.DOGECOIN, 51);
            dogecoinCard.setStuff(3, "DogeCoin");
        // }
        

        // Cryptocurrency cards
       

        // Distribute starter decks to both players
        // for (int i = 100; i < 107; i++) {
            // CryptocurrencyCard bitcoinCard = new CryptocurrencyCard(Card.Type.BITCOIN, i);
            bitcoinCard.setStuff(1, "Bitcoin");
            supply.takeCard(Card.Type.BITCOIN);
            player2.addCardToDeck(bitcoinCard);
            supply.takeCard(Card.Type.BITCOIN);
        // }
        // for (int i = 200; i < 203; i++) {
            //  AutomationCard methodCard = new AutomationCard(Card.Type.METHOD, i);
            player1.addCardToDeck(methodCard);
            supply.takeCard(Card.Type.METHOD);
            player2.addCardToDeck(methodCard);
            supply.takeCard(Card.Type.METHOD);
        // }

        // Shuffle the decks
        //GameEvent GameEvent = new GameEvent("Deck Shuffled.");
        //this.observer.notifyEvent(game, GameEvent);
        player1.shuffleDeck();
        player2.shuffleDeck();

        // Deal initial hands
        player1.drawHand(5);
        player2.drawHand(5);

        // Randomly choose the starting player
        Random random = new Random();
        boolean player1Starts = random.nextBoolean();

        // Track the number of turns
        
        while (supply.getCardQuantity("Framework") > 0) {
            
            if (player1Starts) {
                playerTurn(player1, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
                playerTurn(player2, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
            } else {
                playerTurn(player2, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
                playerTurn(player1, supply, frameworkCard, bitcoinCard, methodCard, moduleCard, ethereumCard, dogecoinCard);
            }
        
        }

        // Determine the winner
        int player1Ap = player1.getTotalAp();
        int player2Ap = player2.getTotalAp();

        String desc = "Final Scores: \nPlayer 1 - Total AP: " + player1Ap + "\nPlayer 2 - Total AP: " + player2Ap;
        Player winner = null;

        if (player1Ap > player2Ap) {
           desc += "\nPlayer 1 wins!";
           winner = player1;
        } else if (player2Ap > player1Ap) {
            desc += "\nPlayer 2 wins!";
           winner = player2;
        } else {
           desc += "\nIt's a tie!";
        }
        GameEvent endGame = new GameEvent(desc); 
        GameState endGameState = new GameState(winner.getName(), winner.getHand(), GameState.TurnPhase.CLEANUP, 0, 0, 0, supply.getGameDeck() );
        this.observer.notifyEvent(endGameState, endGame);
                return null; //Should be player and score pairs
    }

    /**
     * Simulates a player's turn, including the buy phase and cleanup phase.
     * @param player The player taking the turn.
     * @param supply The supply of cards.
     * @param availableCards The available cards for purchase.
     */
    private void playerTurn(Player player, Supply supply, Card... availableCards) {
        // Buy phase
        
        player.playHand();
        int totalMoneyInHand = player.getTotalMoneyInHand();
        
        for (Card card : player.getCards()) {
            System.out.println("- " + card.getDescription()+ ", (Value: " + card.getCost() + ")");
        }

        Card purchasedCard = randomAvailableCard(supply, totalMoneyInHand, availableCards);
        if (purchasedCard != null) {
            player.purchaseCard(purchasedCard, supply);
            GainCardEvent buyCard = new GainCardEvent(purchasedCard.getType(), player.getName());
            GameState buyCardState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.BUY, totalMoneyInHand, totalMoneyInHand, totalMoneyInHand, supply.getGameDeck() );
            this.observer.notifyEvent(buyCardState, buyCard);

            // Check if a Framework card was purchased
            if (purchasedCard instanceof AutomationCard && purchasedCard.getDescription().equals("Framework")) {
                System.out.println(player.getName() + " purchased a Framework card!");
            }
        }
        // Cleanup phase
        player.cleanup();
        EndTurnEvent event = new EndTurnEvent();
        GameState endTurn = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.CLEANUP, totalMoneyInHand, totalMoneyInHand, totalMoneyInHand, supply.getGameDeck() ); //Didn't know what to do here
        this.observer.notifyEvent(endTurn, event);
        player.drawHand(5);

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
            if (supply.getCardQuantity(card.getDescription()) > 0 && card.getCost() <= playerMoney) {
                availableCards.add(card);
            }
        }

        if (availableCards.isEmpty()) {
            return null;
        }

        
        return availableCards.get((int) (Math.random() * availableCards.size()));
    }
    
    public List<Card> availableCardsToBuy(Player player, List<Card> cards){
        List<Card> availabletoBuy = new ArrayList<>();


        for (Card card : cards) {
            if (supply.getCardQuantity(card.getDescription()) > 0 && card.getCost() <= player.getTotalMoneyInHand()) {
                availabletoBuy.add(card);
            }
        }

        return availabletoBuy;
    }


    public List<Decision> generatePossibleDecisions(Player player, Hand hand){
        List<Decision> possibleDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();
       
        // Add PlayCardDecisions for each unplayed card
        for(Card card: cards){
           possibleDecisions.add(new PlayCardDecision(card));
        }

        // Add BuyDecisions if the player has enough crypto
        for (Card card : availableCardsToBuy(player, cards)) { //Not sure what this method is
            if (player.getTotalMoneyInHand() >= card.getCost()) {
                possibleDecisions.add(new BuyDecision(card.getType()));
            }
        }
        
        // Always allow ending the phase
        possibleDecisions.add(new EndPhaseDecision(GameState.TurnPhase.BUY));

        return possibleDecisions;
    }

}
