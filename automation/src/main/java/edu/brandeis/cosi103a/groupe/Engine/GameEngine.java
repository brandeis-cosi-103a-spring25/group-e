package edu.brandeis.cosi103a.groupe.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Engine;
import edu.brandeis.cosi.atg.api.EngineCreator;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.GainCardEvent;
import edu.brandeis.cosi.atg.api.event.PlayCardEvent;
<<<<<<< HEAD:automation/src/main/java/edu/brandeis/cosi103a/groupe/GameEngine.java
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
=======
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.Cards.ActionCard;
>>>>>>> 063fe197fdc6b2202acc3d21a733e50925ed456a:automation/src/main/java/edu/brandeis/cosi103a/groupe/Engine/GameEngine.java
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
    public static Engine makeEngine(ourPlayer player12, ourPlayer player22, GameObserver observer) {
        return new GameEngine(player12, player22, observer);
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
        // supply.getCardQuantity(Card.Type.FRAMEWORK) > 0
        int i = 2;
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
        actionPhase(player);
        moneyPhase(player);
        buyPhase(player);
        cleanupPhase(player);
<<<<<<< HEAD:automation/src/main/java/edu/brandeis/cosi103a/groupe/GameEngine.java
        System.out.println("points for " + player.getName() + ": " + player.getTotalAp());
        //actionPhase(player);
=======
        
>>>>>>> 063fe197fdc6b2202acc3d21a733e50925ed456a:automation/src/main/java/edu/brandeis/cosi103a/groupe/Engine/GameEngine.java
    }

    public void actionPhase(ourPlayer player) throws PlayerViolationException {
        
        boolean endPhaseSelected = false;
        System.out.println("Action phase for " + player.getName());
        while (!endPhaseSelected && player.getActions() > 0) {
            player.setPhase("Action");
            List<Decision> possibleDecisions = generatePossibleActionDecisions(player, player.getHand());
            GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.ACTION,
                    possibleDecisions.size() - 1, player.getMoney(), 0, supply.getGameDeck());
            Decision decision = player.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                    Optional.empty());

            if (decision instanceof PlayCardDecision) {
                PlayCardDecision playDecision = (PlayCardDecision) decision;
                Card playedCard = playDecision.getCard();
                if (playedCard.getCategory() == Card.Type.Category.ACTION
                        && playedCard.getType() != Card.Type.MONITORING) {

                    player.playCard(playedCard);
                    PlayCardEvent playEvent = new PlayCardEvent(playedCard, player.getName());
                    GameState playState = new GameState(player.getName(), player.getHand(),
                            GameState.TurnPhase.ACTION, possibleDecisions.size() - 1, player.getMoney(), 0,
                            supply.getGameDeck());
                    observer.notifyEvent(playState, playEvent);
<<<<<<< HEAD:automation/src/main/java/edu/brandeis/cosi103a/groupe/GameEngine.java

                    switch (playedCard.getType()) {
                        case BACKLOG:
                            player.incrementActions(1);
                            int discarded = player.discardAnyNumberOfCards();
                            player.draw(discarded);
                            break;
                        case CODE_REVIEW:
                            player.draw(1);
                            player.incrementActions(2);
                            break;
                        case DAILY_SCRUM:
                            player.drawHand(4);
                            player.incrementBuys(1);
                            for (ourPlayer opponent : getOpponents(player)) {
                                opponent.drawHand(1);
                            }
                            break;
                        case EVERGREEN_TEST:
                            player.drawHand(2);
                            for (ourPlayer opponent : getOpponents(player)) {
                                opponent.gainCard(new Card(Card.Type.BUG, 0));
                            }
                            break;
                        case HACK:
                            player.incrementMoney(2);
                            for (ourPlayer opponent : getOpponents(player)) {
                                opponent.discardDownTo(3);
                            }
                            break;
                        case IPO:
                            player.drawHand(2);
                            player.incrementActions(1);
                            player.incrementMoney(2);
                            break;
                        case PARALLELIZATION:
                            Card actionCard = player.chooseActionCardToPlay();
                            if (actionCard != null) {
                                player.playCard(actionCard);
                                player.playCard(actionCard);
                            }
                            break;
                        case REFACTOR:
                            Card trashedCard = player.trashCardFromHand();
                            if (trashedCard != null) {
                                int costLimit = trashedCard.getCost() + 2;
                                Card gainedCard = player.gainCardUpToCost(costLimit);
                                player.gainCard(gainedCard);
                            }
                            break;
                        case TECH_DEBT:
                            player.draw(1);
                            player.incrementActions(1);
                            player.incrementMoney(1);
                            int emptyPiles = supply.getEmptyPileCount();
                            for (int i = 0; i < emptyPiles; i++) {
                                player.discardCard();
                            }
                            break;
                        default:
                            break;
                    }
=======
                    actionCardHandler.playActionCard(playedCard, player);
                    player.incrementActions(-1);

>>>>>>> 063fe197fdc6b2202acc3d21a733e50925ed456a:automation/src/main/java/edu/brandeis/cosi103a/groupe/Engine/GameEngine.java
                }
            } else if (decision instanceof EndPhaseDecision) {
                endPhaseSelected = true;
            }
        }

    }

    public boolean reactionPhase(ourPlayer opponent, ourPlayer attacker, Card attackCard) {
        boolean endPhaseSelected = false;
        System.out.println(opponent.getName() + " has Monitoring! Reaction Phase begins.");

        boolean reacted = generateReactionDecision(opponent, new Card(Card.Type.MONITORING, 0));
    
        if (reacted) {
            System.out.println(opponent.getName() + " played Monitoring and negated " + attacker.getName() + "'s attack!");
            return true; // Attack is negated for this opponent
        }
    
        return false;
       
    }

    // MONEY PHASE
    public void moneyPhase(ourPlayer player) throws PlayerViolationException {

        boolean endPhaseSelected = false;
        int totalMoneyInHand = 0;
        // player.playHand();
        System.out.println("Current money: " + player.getMoney());
        while (!endPhaseSelected) {

            List<Decision> possibleDecisions = generatePossiblePlayDecisions(player, player.getHand());

            GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.MONEY,
                    possibleDecisions.size() - 1, totalMoneyInHand, 0, supply.getGameDeck());
            player.setPhase("Play");
            Decision decision = player.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                    Optional.empty());

            if (decision == null) {
                endPhaseSelected = true;
            }
            if (decision instanceof PlayCardDecision) {
                PlayCardDecision playDecision = (PlayCardDecision) decision;
                Card playedCard = playDecision.getCard();

                if (playedCard.getCategory().equals(Card.Type.Category.MONEY)) {
                    totalMoneyInHand += playedCard.getValue();
                    player.playCard(playedCard);
                    player.setMoney(totalMoneyInHand);

                    PlayCardEvent playEvent = new PlayCardEvent(playedCard, player.getName());
                    GameState playState = new GameState(player.getName(), player.getHand(),
                            GameState.TurnPhase.MONEY, possibleDecisions.size() - 1, player.getMoney(), 0,
                            supply.getGameDeck());
                    observer.notifyEvent(playState, playEvent);
                }
            } else if (decision instanceof EndPhaseDecision) {
                endPhaseSelected = true;
            }
        }
    }

    // BUY PHASE
    public void buyPhase(ourPlayer player) throws PlayerViolationException {
        System.out.println("current money: " + player.getMoney());

        boolean endPhaseSelected = false;

        while (!endPhaseSelected && player.getBuys() > 0) {
            List<Decision> possibleDecisions = generatePossibleBuyDecisions(player, supply);
            GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.BUY, 0,
                    player.getMoney(), possibleDecisions.size() - 1, supply.getGameDeck());
            player.setPhase("Buy");
            Decision decision = player.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                    Optional.empty());

            if (decision == null) {
                endPhaseSelected = true;
            }
            if (decision instanceof BuyDecision) {
                BuyDecision buyDecision = (BuyDecision) decision;
                Card purchasedCard = new Card(buyDecision.getCardType(), 0);
                Card.Type cardType = purchasedCard.getType();
                supply.takeCard(cardType);

                if (supply.getCardQuantity(purchasedCard.getType()) > 0
                        && player.getMoney() >= purchasedCard.getCost()) {
                    player.purchaseCard(purchasedCard, supply);
                    GainCardEvent buyCardEvent = new GainCardEvent(purchasedCard.getType(), player.getName());
                    GameState buyCardState = new GameState(player.getName(), player.getHand(),
                            GameState.TurnPhase.BUY,
                            0, player.getMoney(), possibleDecisions.size() - 1,
                            supply.getGameDeck());
                    observer.notifyEvent(buyCardState, buyCardEvent);
              
                    if (purchasedCard.getCategory().equals(Card.Type.Category.VICTORY) && purchasedCard.getDescription().equals("Framework")) {
                        System.out.println(player.getName() + " purchased a Framework card!");
                    }
                    player.incrementBuys(-1);
                } else {
                    throw new PlayerViolationException("Invalid purchase attempt.");
                }
            } else if (decision instanceof EndPhaseDecision) {
                endPhaseSelected = true;
            }
        }
    }

    // CLEANUP PHASE
    public void cleanupPhase(ourPlayer player) {
        player.cleanup();
        observer.notifyEvent(
                new GameState(player.getName(), player.getHand(), GameState.TurnPhase.CLEANUP, 0, 0, 0,
                        supply.getGameDeck()),
                new EndTurnEvent());
        player.drawHand(5);
        player.actions = 1;
        player.buys = 1;
    }

    // Selects a random card from the available cards that are in the supply

    public List<Card> availableCardsToBuy(ourPlayer player, List<Card> cards) {
        List<Card> availableToBuy = new ArrayList<>();

        for (Card card : cards) {
            if (supply.getCardQuantity(card.getType()) > 0 && card.getCost() <= player.getMoney()) {
                availableToBuy.add(card);
            }
        }

        return availableToBuy;
    }

    public List<Decision> generatePossibleBuyDecisions(ourPlayer player, Supply supply) {
        List<Decision> possibleBuyDecisions = new ArrayList<>();
        List<Card> cards = supply.getAvailableCardsInSupply();

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

    public List<Decision> generatePossibleActionDecisions(ourPlayer player, Hand hand) {
        List<Decision> possibleActionDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();

        // Add ActionDecisions for each unplayed action card
        for (Card card : cards) {
            if (card.getCategory() == Card.Type.Category.ACTION) {
                possibleActionDecisions.add(new PlayCardDecision(card));
            }
        }

        // Always allow ending the phase
        possibleActionDecisions.add(new EndPhaseDecision(GameState.TurnPhase.ACTION));
        return possibleActionDecisions;
    }

    public List<Decision> generatePossiblePlayDecisions(ourPlayer player, Hand hand) {
        List<Decision> possiblePlayDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();

        // Add PlayCardDecisions for each unplayed card
        for (Card card : cards) {
            if (card.getCategory().equals(Card.Type.Category.MONEY)) {
                possiblePlayDecisions.add(new PlayCardDecision(card));
        }
    }

        // Always allow ending the phase
        possiblePlayDecisions.add(new EndPhaseDecision(GameState.TurnPhase.MONEY));
        return possiblePlayDecisions;
    }

    // Helper method to get opponents of a player
    private List<ourPlayer> getOpponents(ourPlayer player) {
        List<ourPlayer> opponents = new ArrayList<>();
        if (!player.equals(player1)) {
            opponents.add(player1);
        }
        if (!player.equals(player2)) {
            opponents.add(player2);
        }
        return opponents;
    }

    public boolean generateReactionDecision(ourPlayer player, Card reactionCard) {
        List<Decision> possibleReactDecisions = new ArrayList<>();
        
    
        possibleReactDecisions.add(new PlayCardDecision(reactionCard));
        possibleReactDecisions.add(new EndPhaseDecision(GameState.TurnPhase.REACTION));
    
        String decision = player.getPlayerInput(); // Simulating user input
    
        if (decision.equalsIgnoreCase("yes")) {
            player.playAction(reactionCard, this); // Play the reaction card
            return true; // Reaction was played
        }
    
        return false; // Player chose not to react
    }

    // WINNING LOGIC
    public ImmutableList<ScorePair> determineWinner() {
        int player1Ap = player1.getTotalAp();
        int player2Ap = player2.getTotalAp();

        List<ScorePair> scores = new ArrayList<>();
        scores.add(new ScorePair(player1.getPlayer(), player1Ap));
        scores.add(new ScorePair(player2.getPlayer(), player2Ap));

        return ImmutableList.copyOf(scores);
    }
}