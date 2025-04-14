package edu.brandeis.cosi103a.groupe.Cards;

import java.util.List;


import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;

import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
import edu.brandeis.cosi103a.groupe.Other.Supply;

public class ActionCard {
    private final Supply supply;
    private final GameEngine gameEngine;
    public boolean parallelizationHandle = false;

    public ActionCard(Supply supply, GameEngine gameEngine) {
        this.supply = supply;
        this.gameEngine = gameEngine;
    }

    // Play an action card
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

    // Effect: Player discards any number of cards, then draws the same amount
    public void handleBacklog(ourPlayer player) throws PlayerViolationException {
        player.incrementActions(1);
        gameEngine.discardPhase(player, false, 0, 0);
    }

    private void handleDailyScrum(ourPlayer player) {
        player.draw(4);
        player.incrementBuys(1);
        for (ourPlayer opponent : getOpponents(player)) {
            opponent.draw(1);
        }

    }

    private void handleIpo(ourPlayer player) {
        player.draw(2);
        player.incrementActions(1);
        player.incrementMoney(2);

    }

    public void handleHack(ourPlayer player) throws PlayerViolationException {
        player.incrementMoney(2);

        for (ourPlayer opponent : getOpponents(player)) {
            gameEngine.discardPhase(opponent, true, 3, 0);
        }
    }

    private void handleMonitoring(ourPlayer player) {
        player.draw(2);

    }

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

    private void handleParallelization(ourPlayer player) {
        player.incrementActions(1);
        parallelizationHandle = true;

    }

    private void handleCode_Review(ourPlayer player) {
        player.draw(1);
        player.incrementActions(2);

    }

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

    private List<ourPlayer> getOpponents(ourPlayer player) {
        return gameEngine.getOpponents(player);
    }

    private boolean isAttack(Card card) {
        return card.getType() == Card.Type.HACK || card.getType() == Card.Type.EVERGREEN_TEST; // Add more attack cards
                                                                                               // as needed
    }
}
