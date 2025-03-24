package edu.brandeis.cosi103a.groupe.Cards;

import java.util.ArrayList;
import java.util.List;

import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.GameEngine;

public class ActionCard {
    private final Supply supply;
    private final GameEngine gameEngine;

    public ActionCard(Supply supply, GameEngine gameEngine) {
        this.supply = supply;
        this.gameEngine = gameEngine;
    }

    // Play an action card
    public void playActionCard(Card card, ourPlayer player) {
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
    private void handleBacklog(ourPlayer player) {
        player.incrementActions(1);
        int discarded = player.discardAnyNumberOfCards();
        player.draw(discarded);

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

    private void handleHack(ourPlayer player) {
        player.incrementMoney(2);
        for (ourPlayer opponent : getOpponents(player)) {
            opponent.discardDownTo(3);
        }

    }

    private void handleMonitoring(ourPlayer player) {
        player.draw(2);
        //reaction phase.
    }

    private void handleTech_Debt(ourPlayer player) {
        player.draw(1);
        player.incrementActions(1);
        player.incrementMoney(1);
        int emptyPiles = supply.getEmptyPileCount();
        for (int i = 0; i < emptyPiles; i++) {
            player.discardDownTo(player.getHandSize()-1);;
        }

    }

    private void handleRefactor(ourPlayer player) {
        Card trashedCard = player.trashCardFromHand();
        if (trashedCard != null) {
            int costLimit = trashedCard.getCost() + 2;
            Card gainedCard = player.gainCardUpToCost(costLimit);
            player.gainCard(gainedCard);
        }

    }

    private void handleParallelization(ourPlayer player) {
        Card actionCard = player.chooseActionCardToPlay();
        if (actionCard != null) {
            player.playCard(actionCard);
            player.playCard(actionCard);
        }

    }

    private void handleCode_Review(ourPlayer player) {
        player.draw(1);
        player.incrementActions(2);

    }

    private void handleEvergreen_Test(ourPlayer player) {
        player.draw(2);
        for (ourPlayer opponent : getOpponents(player)) {
            opponent.gainCard(new Card(Card.Type.BUG, 0));
        }

    }
    private List<ourPlayer> getOpponents(ourPlayer player) {
        return gameEngine.getOpponents(player);
    }
}
