package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brandeis.cosi.atg.api.GameDeck;
import edu.brandeis.cosi.atg.api.cards.Card;

//for_review

public class Supply {
    private Map<Card.Type, Integer> cardQuantities;
    private final Map<Card.Type, Integer> cardTypeIds;
    
    
    /*
     * Constructor for the supply
     */
    public Supply() {
        cardQuantities = new HashMap<>();
        cardTypeIds = new HashMap<>();
        cardQuantities.put(Card.Type.METHOD, 14);
        cardQuantities.put(Card.Type.MODULE, 8);
        cardQuantities.put(Card.Type.FRAMEWORK, 8);
        cardQuantities.put(Card.Type.BITCOIN, 60);
        cardQuantities.put(Card.Type.ETHEREUM, 40);
        cardQuantities.put(Card.Type.DOGECOIN, 30);
        cardQuantities.put(Card.Type.BACKLOG, 10);
        cardQuantities.put(Card.Type.CODE_REVIEW, 10);
        cardQuantities.put(Card.Type.DAILY_SCRUM, 10);
        cardQuantities.put(Card.Type.EVERGREEN_TEST, 10);
        cardQuantities.put(Card.Type.HACK, 10);
        cardQuantities.put(Card.Type.IPO, 10);
        cardQuantities.put(Card.Type.PARALLELIZATION, 10);
        cardQuantities.put(Card.Type.REFACTOR, 10);
        cardQuantities.put(Card.Type.TECH_DEBT, 10);
        cardQuantities.put(Card.Type.MONITORING, 10);

        cardTypeIds.put(Card.Type.MODULE, 1);
        cardTypeIds.put(Card.Type.METHOD, 2);
        cardTypeIds.put(Card.Type.FRAMEWORK, 3);
        cardTypeIds.put(Card.Type.BITCOIN, 4);
        cardTypeIds.put(Card.Type.ETHEREUM, 5);
        cardTypeIds.put(Card.Type.DOGECOIN, 6);
        cardTypeIds.put(Card.Type.BACKLOG, 7);
        cardTypeIds.put(Card.Type.CODE_REVIEW, 8);
        cardTypeIds.put(Card.Type.DAILY_SCRUM, 9);
        cardTypeIds.put(Card.Type.EVERGREEN_TEST, 10);
        cardTypeIds.put(Card.Type.HACK, 11);
        cardTypeIds.put(Card.Type.IPO, 12);
        cardTypeIds.put(Card.Type.PARALLELIZATION, 13);
        cardTypeIds.put(Card.Type.REFACTOR, 14);
        cardTypeIds.put(Card.Type.TECH_DEBT, 15);
        cardTypeIds.put(Card.Type.MONITORING, 16);
    }

    /**
     * Gets the quantity of a specific card in the supply.
     * @param cardName The name of the card.
     * @return The quantity of the card in the supply.
     */
    public int getCardQuantity(Card.Type cardName) {
        return cardQuantities.getOrDefault(cardName, 0);
    }


    public List<Card> getAvailableCardsInSupply() {
        List<Card> availableCards = new ArrayList<>();
        for (Card.Type type : cardQuantities.keySet()) {
            if (getCardQuantity(type) > 0) {
                availableCards.add(new Card(type, cardTypeIds.get(type)));
            }
        }
        return availableCards;
    }

    /**
     * Decreases the quantity of a specific card in the supply by one.
     * @param cardName The name of the card.
     * @return True if the card was successfully taken from the supply, false if the card is out of stock.
     */
    public boolean takeCard(Card.Type type) {
        GameDeck deck = getGameDeck();
        int available = deck.getNumAvailable(type);
        //int quantity = cardQuantities.getOrDefault(type, 0);
        
        if (available > 0) {
            cardQuantities.put(type, available - 1);
            return true;
        }
        return false;
    }

    public void printSupply() {
        System.out.println("Supply: ");
        for (Card.Type cardName : cardQuantities.keySet()) {
            System.out.println(cardName + ": " + cardQuantities.get(cardName));
        }
    }

    public GameDeck getGameDeck() {
        return new GameDeck(ImmutableMap.copyOf(cardQuantities));
    }
    
    public int getEmptyPileCount() {
        int emptyCount = 0;
        for (int quantity : cardQuantities.values()) {
            if (quantity == 0) {
                emptyCount++;
            }
        }
        return emptyCount;
    }
}
