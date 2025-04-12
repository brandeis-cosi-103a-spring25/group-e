package edu.brandeis.cosi103a.groupe.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Engine;
import edu.brandeis.cosi.atg.api.EngineCreator;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.DiscardCardDecision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.GainCardDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.decisions.TrashCardDecision;
import edu.brandeis.cosi.atg.api.event.DiscardCardEvent;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.GainCardEvent;
import edu.brandeis.cosi.atg.api.event.PlayCardEvent;
import edu.brandeis.cosi.atg.api.event.TrashCardEvent;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.Cards.ActionCard;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

/*
 * This class simulates simlpified game play for the Automation card game.
 *
 */
public class GameEngine implements Engine {
    private final GameObserver observer;
    private final ourPlayer player1, player2;
    private Supply supply = new Supply();
    private final ActionCard actionCardHandler = new ActionCard(supply, this);

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

        while (true) {
            if (player1Starts) {
                playFullTurn(player1);
                if (supply.getCardQuantity(Card.Type.FRAMEWORK) <= 0) {
                    break; // Exit the loop if all FRAMEWORK cards have been bought
                }
                playFullTurn(player2);
                if (supply.getCardQuantity(Card.Type.FRAMEWORK) <= 0) {
                    break; // Exit the loop if all FRAMEWORK cards have been bought
                }
            } else {
                playFullTurn(player2);
                if (supply.getCardQuantity(Card.Type.FRAMEWORK) <= 0) {
                    break; // Exit the loop if all FRAMEWORK cards have been bought
                }
                playFullTurn(player1);
                if (supply.getCardQuantity(Card.Type.FRAMEWORK) <= 0) {
                    break; // Exit the loop if all FRAMEWORK cards have been bought
                }
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

    public void playFullTurn(ourPlayer player) throws PlayerViolationException {
        actionPhase(player);
        moneyPhase(player);
        buyPhase(player);
        cleanupPhase(player);

        System.out.println("points for " + player.getName() + ": " + player.getTotalAp());

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
                System.out.println("Recieved decision: " + decision);
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
                    if (actionCardHandler.parallelizationHandle) {

                        actionCardHandler.parallelizationHandle = false;
                        actionCardHandler.playActionCard(playedCard, player);
                        actionCardHandler.playActionCard(playedCard, player);

                    } else {
                        actionCardHandler.playActionCard(playedCard, player);
                    }

                    player.incrementActions(-1);

                }
            } else if (decision instanceof EndPhaseDecision) {
                endPhaseSelected = true;
            }
        }

    }

    public void discardPhase(ourPlayer player, boolean forceDiscard, int targetCardCount, int targetDiscardCount)
            throws PlayerViolationException {
        System.out.println(player.getName() + " is in the Discard Phase.");
        // Set the turn phase to Discard
        player.setPhase("Discard");

        boolean endPhaseSelected = false;
        int numDiscarded = 0;

        List<Decision> possibleDecisions = generatePossibleDiscardDecisions(player, forceDiscard);

        // Create the GameState for the Discard Phase
        GameState currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.DISCARD,
                possibleDecisions.size(), player.getMoney(), 0, supply.getGameDeck());

        while (!endPhaseSelected && !possibleDecisions.isEmpty()
                && (player.getHandSize() > targetCardCount || numDiscarded < targetDiscardCount)) {
            // Pass the possible discard decisions to the player
            Decision decision = player.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                    Optional.empty());

            // Process the discard decision
            if (decision instanceof DiscardCardDecision) {
                DiscardCardDecision discardDecision = (DiscardCardDecision) decision;
                Card discardedCard = discardDecision.getCard();
                numDiscarded++;

                // Discard the card
                player.discardCard(discardedCard);
                System.out.println(player.getName() + " discards: " + discardedCard.getType());

                // Notify the observer of the discard event
                DiscardCardEvent discardEvent = new DiscardCardEvent(discardedCard.getType(), player.getName());
                observer.notifyEvent(currentState, discardEvent);

                // Update the possible decisions list based on the remaining cards
                possibleDecisions = generatePossibleDiscardDecisions(player, forceDiscard);

            } else if (decision instanceof EndPhaseDecision && !forceDiscard) {
                endPhaseSelected = true; // Player decides to stop discarding and end the phase
            }
            currentState = new GameState(player.getName(), player.getHand(), GameState.TurnPhase.DISCARD,
                    possibleDecisions.size(), player.getMoney(), 0, supply.getGameDeck());
        }

        // End the Discard Phase
        if (!forceDiscard) {
            endDiscardPhase(player, numDiscarded);
        }
    }

    public boolean reactionPhase(ourPlayer opponent, ourPlayer attacker, Card attackCard)
            throws PlayerViolationException {
        List<Decision> possibleDecisions = new ArrayList<>();

        possibleDecisions.add(new PlayCardDecision(new Card(Card.Type.MONITORING, 0)));
        possibleDecisions.add(new EndPhaseDecision(GameState.TurnPhase.REACTION));

        System.out.println(opponent.getName() + " has Monitoring! Reaction Phase begins.");
        GameState currentState = new GameState(opponent.getName(), opponent.getHand(), GameState.TurnPhase.REACTION,
                possibleDecisions.size(), opponent.getMoney(), 0, supply.getGameDeck());
        Decision decision = opponent.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                Optional.empty());
        opponent.setHasReactedToAttack(false);
        // If opponent decides to play a reaction, apply it
        if (decision instanceof PlayCardDecision) {
            PlayCardDecision playDecision = (PlayCardDecision) decision;
            Card playedCard = playDecision.getCard();
            if (playedCard.getType() == Card.Type.MONITORING) {
                opponent.playCard(playedCard);
                opponent.setHasReactedToAttack(true);
                PlayCardEvent playEvent = new PlayCardEvent(playedCard, opponent.getName());
                GameState playState = new GameState(opponent.getName(), opponent.getHand(),
                        GameState.TurnPhase.REACTION, possibleDecisions.size(), opponent.getMoney(), 0,
                        supply.getGameDeck());
                observer.notifyEvent(playState, playEvent);
                actionCardHandler.playActionCard(playedCard, opponent);

                return true;

            } else if (decision instanceof EndPhaseDecision) {
                return false;
            }
        }

        // Default return value if no decision is made
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
            System.out.println(possibleDecisions);
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

                if (supply.getCardQuantity(purchasedCard.getType()) >= 0
                        && player.getMoney() >= purchasedCard.getCost()) {
                    player.purchaseCard(purchasedCard, supply);
                    GainCardEvent buyCardEvent = new GainCardEvent(purchasedCard.getType(), player.getName());
                    GameState buyCardState = new GameState(player.getName(), player.getHand(),
                            GameState.TurnPhase.BUY,
                            0, player.getMoney(), possibleDecisions.size() - 1,
                            supply.getGameDeck());
                    observer.notifyEvent(buyCardState, buyCardEvent);

                    if (purchasedCard.getCategory().equals(Card.Type.Category.VICTORY)
                            && purchasedCard.getDescription().equals("Framework")) {
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

    public void trashCard(ourPlayer player) throws PlayerViolationException {
        System.out.println("🔹 Trash Card Phase for " + player.getName());

        // Generate available trash decisions
        List<Decision> possibleDecisions = new ArrayList<>();

        // Add a TrashCardDecision for each card in hand
        for (Card card : player.getCards()) {
            possibleDecisions.add(new TrashCardDecision(card));
        }

        GameState currentState = new GameState(
                player.getName(), player.getHand(), GameState.TurnPhase.DISCARD, // DISCARD phase used for trashing
                possibleDecisions.size() - 1, player.getMoney(), 0, supply.getGameDeck());

        // Let the player make a decision
        Decision decision = player.getPlayer().makeDecision(currentState, ImmutableList.copyOf(possibleDecisions),
                Optional.empty());

        if (decision instanceof TrashCardDecision) {
            TrashCardDecision trashDecision = (TrashCardDecision) decision;
            Card trashedCard = trashDecision.getCard();

            if (trashedCard != null) {
                // Remove the card from the player's hand (not sending it to discard)
                player.trashCard(trashedCard);

                // Trigger TrashCardEvent
                TrashCardEvent trashEvent = new TrashCardEvent(trashedCard.getType(), player.getName());
                observer.notifyEvent(currentState, trashEvent);

            }
        }
    }

    public void gainCard(ourPlayer player, Card card, Integer costLimit) throws PlayerViolationException {
        if (card != null) {
            // Directly give a specific card (not from supply)
            player.gainCard(card.getType(), null);
            System.out.println(player.getName() + " gained " + card);
            return;
        }

        if (costLimit == null) {
            System.out.println("⚠️ Error: Either card or costLimit must be provided.");
            return;
        }

        // Normal gain process (pick from supply)
        System.out.println(player.getName() + " is gaining a card up to cost " + costLimit);

        List<Decision> possibleDecisions = new ArrayList<>();

        // Iterate through all available cards in the supply
        for (Card supplyCard : supply.getAvailableCardsInSupply()) {
            if (supplyCard.getCost() <= costLimit) {
                possibleDecisions.add(new GainCardDecision(supplyCard.getType()));
            }
        }
        GameState gainState = new GameState(
                player.getName(), player.getHand(), GameState.TurnPhase.GAIN,
                possibleDecisions.size() - 1, player.getMoney(), 0, supply.getGameDeck());

        Decision decision = player.getPlayer().makeDecision(gainState, ImmutableList.copyOf(possibleDecisions),
                Optional.empty());

        if (decision instanceof GainCardDecision) {
            GainCardDecision gainDecision = (GainCardDecision) decision;
            Card.Type gainedCard = gainDecision.getCardType();

            if (gainedCard != null && gainedCard.getCost() <= costLimit) {
                player.gainCard(gainedCard, supply);
                System.out.println(player.getName() + " gained " + gainedCard.getDescription() + " from supply.");
            } else {
                System.out.println("⚠️ Invalid choice! Card cost exceeds " + costLimit);
            }
        }
    }

    public List<Decision> generatePossibleBuyDecisions(ourPlayer player, Supply supply) {
        List<Decision> possibleBuyDecisions = new ArrayList<>();
        Map<Card.Type, List<Card>> cardStacks = supply.getCardStacks();

        // Iterate over all card types in the supply
        for (Card.Type cardType : cardStacks.keySet()) {
            int cardCost = cardType.getCost();
            int cardQuantity = supply.getCardQuantity(cardType);

            // Add a BuyDecision if the player can afford the card and it is available
            if (player.getMoney() >= cardCost && cardQuantity > 0) {
                possibleBuyDecisions.add(new BuyDecision(cardType));
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
    public List<ourPlayer> getOpponents(ourPlayer player) {
        List<ourPlayer> opponents = new ArrayList<>();
        if (!player.equals(player1)) {
            opponents.add(player1);
        }
        if (!player.equals(player2)) {
            opponents.add(player2);
        }
        return opponents;
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

    private List<Decision> generatePossibleDiscardDecisions(ourPlayer player, boolean isHackAttack) {
        List<Decision> discardDecisions = new ArrayList<>();
        List<Card> cards = player.getCards();

        // Generate discard decisions based on the player's hand
        for (Card card : cards) {
            discardDecisions.add(new DiscardCardDecision(card));
        }

        if (!isHackAttack) {
            discardDecisions.add(new EndPhaseDecision(GameState.TurnPhase.DISCARD));
        }

        return discardDecisions;
    }

    /*
     * This method ends the discard phase for a player, refilling their hand if
     * necessary.
     */
    private void endDiscardPhase(ourPlayer player, int numCardstoDraw) throws PlayerViolationException {
        System.out.println(player.getName() + "'s discard phase ends.");

        int discardedCount = numCardstoDraw;
        if (discardedCount > 0) {
            // Draw cards to refill the hand
            player.draw(numCardstoDraw);
        }
    }
}
