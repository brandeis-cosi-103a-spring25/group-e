package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import edu.brandeis.cosi.atg.api.*;
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
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

/*
 * This class simulates simlpified game play for the Automation card game.
 *
 */
public class GameEngine implements Engine {
    private final GameObserver observer;
    private final ourPlayer player1, player2;
    private final Supply supply;

    public GameEngine(ourPlayer player1, ourPlayer player2, GameObserver observer) {
        this.player1 = player1;
        this.player2 = player2;
        this.supply = new Supply();
        this.observer = observer;
    }

    @EngineCreator
    public static Engine makeEngine(ourPlayer player1, ourPlayer player2, GameObserver observer) {
        return new GameEngine(player1, player2, observer);
    }

    @Override
    public ImmutableList<ScorePair> play() throws PlayerViolationException {
        distributeCards(player1, player2, supply);
        player1.shuffleDeck();
        player2.shuffleDeck();
        player1.drawHand(5);
        player2.drawHand(5);

        Random random = new Random();
        boolean player1Starts = random.nextBoolean();

        while (supply.getCardQuantity(Card.Type.FRAMEWORK) > 0) {

            if (player1Starts) {
                playFullTurn(player1);
                playFullTurn(player2);
            } else {
                playFullTurn(player2);
                playFullTurn(player1);
            }
        }

        return determineWinner();
    }

    public void distributeCards(ourPlayer player1, ourPlayer player2, Supply supply) {
        for (int i = 0; i < 7; i++) {
            player1.addCardToDeck(new Card(Card.Type.BITCOIN, 31));
            supply.takeCard(Card.Type.BITCOIN);
            player2.addCardToDeck(new Card(Card.Type.BITCOIN, 31));
            supply.takeCard(Card.Type.BITCOIN);
        }
        for (int i = 0; i < 3; i++) {
            player1.addCardToDeck(new Card(Card.Type.METHOD, 1));
            supply.takeCard(Card.Type.METHOD);
            player2.addCardToDeck(new Card(Card.Type.METHOD, 1));
            supply.takeCard(Card.Type.METHOD);
        }
    }

    private void playFullTurn(ourPlayer player) throws PlayerViolationException {

        moneyPhase(player);
        buyPhase(player);
        cleanupPhase(player);
    }

    // MONEY PHASE
    public void moneyPhase(ourPlayer player) throws PlayerViolationException {
        boolean endPhaseSelected = false;
        int totalMoneyInHand = 0;
        player.playHand();
        while (!endPhaseSelected) {
         
            List<Decision> possibleDecisions = generatePossiblePlayDecisions(player, player.getHand());

            GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.MONEY, totalMoneyInHand, 0, 0, supply.getGameDeck());
            player.setPhase("Play");
            Decision decision = player.makeDecision(currentState, ImmutableList.copyOf(possibleDecisions), Optional.empty());

            if (decision == null) {
                endPhaseSelected = true;
            }
            if (decision instanceof PlayCardDecision) {
                PlayCardDecision playDecision = (PlayCardDecision) decision;
                Card playedCard = playDecision.getCard();

                if (playedCard.getType() == Card.Type.BITCOIN || playedCard.getType() == Card.Type.ETHEREUM || playedCard.getType() == Card.Type.DOGECOIN) {
              
                    totalMoneyInHand += playedCard.getValue();
                    player.playCard(playedCard);

                    PlayCardEvent playEvent = new PlayCardEvent(playedCard, player.getName());
                    GameState playState = new GameState(player.getName(), player.getHand(), 
                                                         GameState.TurnPhase.MONEY, totalMoneyInHand, 0, 0, 
                                                         supply.getGameDeck());
                    observer.notifyEvent(playState, playEvent);
                } 
            } else if (decision instanceof EndPhaseDecision) {
                // System.out.println(player.getName() + " ended the MONEY phase with " + totalMoneyInHand + " coins.");
                endPhaseSelected = true;
            }
        }
    }

    // BUY PHASE
    public void buyPhase(ourPlayer player) throws PlayerViolationException {
        boolean endPhaseSelected = false;
        int totalMoneyInHand = player.getMoney();

        while (!endPhaseSelected) {
            List<Decision> possibleDecisions = generatePossibleBuyDecisions(player, player.getHand());
            GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.BUY, totalMoneyInHand, 0, 0, supply.getGameDeck());
            player.setPhase("Buy");
            Decision decision = player.makeDecision(currentState, ImmutableList.copyOf(possibleDecisions), Optional.empty());

            if (decision == null) {
                endPhaseSelected = true;
            }
            if (decision instanceof BuyDecision) {
                BuyDecision buyDecision = (BuyDecision) decision;
                Card purchasedCard = new Card(buyDecision.getCardType(), 0);
                Card.Type cardType = purchasedCard.getType();
                supply.takeCard(cardType);
                
                if (supply.getCardQuantity(purchasedCard.getType()) > 0 && player.getMoney() >= purchasedCard.getCost()) {
                    player.purchaseCard(purchasedCard, supply);
                    GainCardEvent buyCardEvent = new GainCardEvent(purchasedCard.getType(), player.getName());
                    GameState buyCardState = new GameState(player.getName(), player.getHand(), 
                                                           GameState.TurnPhase.BUY, 
                                                           totalMoneyInHand, totalMoneyInHand, totalMoneyInHand, 
                                                           supply.getGameDeck());
                    observer.notifyEvent(buyCardState, buyCardEvent);

                  if (purchasedCard instanceof AutomationCard && purchasedCard.getDescription().equals("Framework")) {
                        System.out.println(player.getName() + " purchased a Framework card!");
                   }
                } else {
                    throw new PlayerViolationException("Invalid purchase attempt.");
                }
            } else if (decision instanceof EndPhaseDecision) {
                // System.out.println(player.getName() + " ended the BUY phase.");
                endPhaseSelected = true;
            }
        }
    }

    // CLEANUP PHASE
    public void cleanupPhase(ourPlayer player) {
        player.cleanup();
        observer.notifyEvent(new GameState(player.getName(), player.getHand(), GameState.TurnPhase.CLEANUP, 0, 0, 0, supply.getGameDeck()),
                             new EndTurnEvent());
        player.drawHand(5);
    }

    // Selects a random card from the available cards that are in the supply
    private static Card randomAvailableCard(Supply supply, int playerMoney, Card... cards) {
        List<Card> availableCards = new ArrayList<>();

        for (Card card : cards) {
            if (supply.getCardQuantity(card.getType()) > 0 && card.getCost() <= playerMoney) {
                availableCards.add(card);
            }
        }

        if (availableCards.isEmpty()) {
            return null;
        }

        return availableCards.get((int) (Math.random() * availableCards.size()));
    }

    public List<Card> availableCardsToBuy(ourPlayer player, List<Card> cards) {
        List<Card> availableToBuy = new ArrayList<>();

        for (Card card : cards) {
            if (supply.getCardQuantity(card.getType()) > 0 && card.getCost() <= player.getMoney()) {
                availableToBuy.add(card);
            }
        }

        return availableToBuy;
    }

    public List<Decision> generatePossibleBuyDecisions(ourPlayer player, Hand hand) {
        List<Decision> possibleBuyDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();

     

        // Add BuyDecisions if the player has enough crypto
        for (Card card : availableCardsToBuy(player, cards)) {
            if (player.getMoney() >= card.getCost()) {
                possibleBuyDecisions.add(new BuyDecision(card.getType()));
            }
        }

        // Always allow ending the phase
        possibleBuyDecisions.add(new EndPhaseDecision(GameState.TurnPhase.BUY));
        return possibleBuyDecisions;
    }
    public List<Decision> generatePossiblePlayDecisions(ourPlayer player, Hand hand) {
        List<Decision> possiblePlayDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();

        // Add PlayCardDecisions for each unplayed card
        for (Card card : cards) {
            if (card.getType() == Card.Type.BITCOIN) {
                possiblePlayDecisions.add(new PlayCardDecision(card));        
            }
            else if (card.getType() == Card.Type.ETHEREUM) {
                possiblePlayDecisions.add(new PlayCardDecision(card));        
            }
            else if (card.getType() == Card.Type.DOGECOIN) {
                possiblePlayDecisions.add(new PlayCardDecision(card));        
        }
        }


        // Always allow ending the phase
        possiblePlayDecisions.add(new EndPhaseDecision(GameState.TurnPhase.MONEY));
        return possiblePlayDecisions;
    }
  

    // WINNING LOGIC
    public ImmutableList<ScorePair> determineWinner() {
        int player1Ap = player1.getTotalAp();
        int player2Ap = player2.getTotalAp();

        List<ScorePair> scores = new ArrayList<>();
        scores.add(new ScorePair(player1, player1Ap));
        scores.add(new ScorePair(player2, player2Ap));

        return ImmutableList.copyOf(scores);
    }
}