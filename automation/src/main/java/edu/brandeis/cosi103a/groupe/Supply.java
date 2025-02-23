package edu.brandeis.cosi103a.groupe;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brandeis.cosi.atg.api.GameDeck;
import edu.brandeis.cosi.atg.api.cards.Card;


public class Supply {
    private Map<Card.Type, Integer> cardQuantities;
    
    /*
     * Constructor for the supply
     */
    public Supply() {
        cardQuantities = new HashMap<>();
        cardQuantities.put(Card.Type.METHOD, 14);
        cardQuantities.put(Card.Type.MODULE, 8);
        cardQuantities.put(Card.Type.FRAMEWORK, 8);
        cardQuantities.put(Card.Type.BITCOIN, 60);
        cardQuantities.put(Card.Type.ETHEREUM, 40);
        cardQuantities.put(Card.Type.DOGECOIN, 30);
    }

    /**
     * Gets the quantity of a specific card in the supply.
     * @param cardName The name of the card.
     * @return The quantity of the card in the supply.
     */
    public int getCardQuantity(String cardName) {
        return cardQuantities.getOrDefault(cardName, 0);
    }

    /**
     * Decreases the quantity of a specific card in the supply by one.
     * @param cardName The name of the card.
     * @return True if the card was successfully taken from the supply, false if the card is out of stock.
     */
    public boolean takeCard(Card.Type type) {
        int quantity = cardQuantities.getOrDefault(type, 0);
        
        if (quantity > 0) {
            cardQuantities.put(type, quantity - 1);
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
}
