package edu.brandeis.cosi103a.groupe.Cards;

import java.util.List;


import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;

import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
import edu.brandeis.cosi103a.groupe.Other.Supply;
/*
 * COSI 103a-Group E
 * April 28th, 2025
 * This class handles the logic for playing action cards in the game.
 * It includes methods for handling various action cards, including their effects and interactions with players.
 */
public class ActionCard {
    private final Supply supply;
    private final GameEngine gameEngine;
    public boolean parallelizationHandle = false;
    
    /*
     * This method constructs an ActionCard object with the given supply and game engine.
     */
    public ActionCard(Supply supply, GameEngine gameEngine) {
        this.supply = supply;
        this.gameEngine = gameEngine;
    }
    
    /** 
     * This method handles the logic for playing an action card.
     * It checks if the card is an attack card and allows opponents to react before executing the card's effect.
     * @param card
     * @param player
     * @throws PlayerViolationException
     */
    public void playActionCard(Card card, ourPlayer player) throws PlayerViolationException {
        boolean isAttackCard = isAttack(card);
        

        if (isAttackCard) {
            // 🔹 Before executing the attack, allow opponents to react
            for (ourPlayer opponent : gameEngine.getOpponents(player)) {
                if (opponent.getCards().stream()
                        .anyMatch(opponentCard -> opponentCard.getType() == Card.Type.MONITORING)) {
                    boolean reacted = gameEngine.reactionPhase(opponent, player, card);

                    if (reacted) {
                        System.out.println(
                                opponent.getName() + " negated " + player.getName() + "'s attack with Monitoring!");
                        return; // Attack is negated for this opponent
                    }
                }
            }
        }
        switch (card.getType()) {
            case BACKLOG:
                handleBacklog((ourPlayer) player);
                break;
            case DAILY_SCRUM:
                handleDailyScrum((ourPlayer) player);
                break;
            case IPO:
                handleIpo((ourPlayer) player);
                break;
            case HACK:
                handleHack((ourPlayer) player);
                break;
            case MONITORING:
                handleMonitoring(player);
                break;
            case TECH_DEBT:
                handleTech_Debt((ourPlayer) player);
                break;
            case REFACTOR:
                handleRefactor((ourPlayer) player);
                break;
            case PARALLELIZATION:
                handleParallelization((ourPlayer) player);
                break;
            case CODE_REVIEW:
                handleCode_Review((ourPlayer) player);
                break;
            case EVERGREEN_TEST:
                handleEvergreen_Test((ourPlayer) player);
                break;
            default:
                throw new UnsupportedOperationException("Action card not supported.");
        }
    }

    
    /**
     * This method handles the Backlog action card.
     * It allows the player to discard any number of cards and then draw the same amount. 
     * @param player
     * @throws PlayerViolationException
     */
    public void handleBacklog(ourPlayer player) throws PlayerViolationException {
        player.incrementActions(1);
        gameEngine.discardPhase(player, false, 0, 0);
    }
    
    /*
     * This mehtod handles the Daily Scrum action card.
     * It allows the player to draw 4 cards, increment their buys by 1,
     * and allows each opponent to draw 1 card.
     * @param player
     */
    private void handleDailyScrum(ourPlayer player) {
        player.draw(4);
        player.incrementBuys(1);
        for (ourPlayer opponent : getOpponents(player)) {
            opponent.draw(1);
        }

    }
    
    /*
     * This method handles the IPO action card.
     * It allows the player to draw 2 cards, increment their actions by 1,
     * and increment their money by 2.
     * @param player
     */
    private void handleIpo(ourPlayer player) {
        player.draw(2);
        player.incrementActions(1);
        player.incrementMoney(2);

    }
    
    /*
     * This method handles the Hack action card.
     * It allows the player to increment their money by 2,
     * and each opponent discards 3 cards.
     * @param player
     * @throws PlayerViolationException
     */
    public void handleHack(ourPlayer player) throws PlayerViolationException {
        player.incrementMoney(2);

        for (ourPlayer opponent : getOpponents(player)) {
            gameEngine.discardPhase(opponent, true, 3, 0);
        }
    }
    
    /*
     * This method handles the Monitoring action card.
     * It allows the player to draw 2 cards.
     * @param player
     */
    private void handleMonitoring(ourPlayer player) {
        player.draw(2);

    }
    
    /*
     * This method handles the Tech Debt action card.
     * It allows the player to draw 1 card, increment their actions by 1,
     * and increment their money by 1.
     * If there are empty supply piles, the player must discard cards equal to the number of empty piles.
     * @param player
     * @throws PlayerViolationException
     */
    public void handleTech_Debt(ourPlayer player) throws PlayerViolationException {
        player.draw(1);
        player.incrementActions(1);
        player.incrementMoney(1);

        int emptyPiles = supply.getEmptyPileCount();
        System.out.println(player.getName() + " must discard " + emptyPiles + " cards due to empty supply piles.");

        // If no discard is required, return early
        if (emptyPiles <= 0) {
            return;
        }

        gameEngine.discardPhase(player, true, 0, emptyPiles);
    }
    
    /*
     * This method handles the Refactor action card.
     * It allows the player to trash a card, and then gain a card up to the trashed card's cost +2.
     * @param player
     */
    private void handleRefactor(ourPlayer player) {
        try {
            gameEngine.trashCard(player);
            Card trashedCard = player.getLastTrashedCard();

            if (trashedCard != null) {
                int costLimit = trashedCard.getCost() + 2; // New card must be within this cost range

                System.out.println("You may gain a card up to cost " + costLimit);

                // Step 3: Gain a card up to the trashed card’s cost +2
                gameEngine.gainCard(player, null, costLimit);
            }
        } catch (PlayerViolationException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /*
     * This method handles the Parallelization action card.
     * It allows the player to increment their actions by 1.
     * @param player
     */
    private void handleParallelization(ourPlayer player) {
        player.incrementActions(1);
        parallelizationHandle = true;

    }
    
    /*
     * This method handles the Code Review action card.
     * It allows the player to draw 1 card and increment their actions by 2.
     * @param player
     */
    private void handleCode_Review(ourPlayer player) {
        player.draw(1);
        player.incrementActions(2);

    }
    
    /*
     * This method handles the Evergreen Test action card.
     * It allows the player to draw 2 cards and each opponent gains a Bug card.
     * @param player
     */
    private void handleEvergreen_Test(ourPlayer player) {
        player.draw(2);
        for (ourPlayer opponent : getOpponents(player)) {
            try {
                gameEngine.gainCard(opponent, new Card(Card.Type.BUG, 0), null);
            } catch (PlayerViolationException e) {
                System.err.println("Error gaining card for opponent: " + e.getMessage());
            }
        }

    }
    
    /*
     * This method retrieves the list of opponents for a given player.
     * @param player
     * @return List of opponents
     */
    private List<ourPlayer> getOpponents(ourPlayer player) {
        return gameEngine.getOpponents(player);
    }
    
    /*
     * This method checks if a card is an attack card.
     * It can be extended to include more attack cards as needed.
     * @param card
     * @return true if the card is an attack card, false otherwise
     */
    private boolean isAttack(Card card) {
        return card.getType() == Card.Type.HACK || card.getType() == Card.Type.EVERGREEN_TEST; // Add more attack cards
                                                                                               // as needed
    }
}
