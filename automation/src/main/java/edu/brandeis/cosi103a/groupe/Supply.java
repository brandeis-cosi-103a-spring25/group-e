package edu.brandeis.cosi103a.groupe;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brandeis.cosi.atg.api.GameDeck;


public class Supply {
    private Map<String, Integer> cardQuantities;
    
    /*
     * Constructor for the supply
     */
    public Supply() {
        cardQuantities = new HashMap<>();
        cardQuantities.put("Method", 14);
        cardQuantities.put("Module", 8);
        cardQuantities.put("Framework", 8);
        cardQuantities.put("Bitcoin", 60);
        cardQuantities.put("Ethereum", 40);
        cardQuantities.put("Dogecoin", 30);
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
    public boolean takeCard(String cardName) {
        int quantity = cardQuantities.getOrDefault(cardName, 0);
        
        if (quantity > 0) {
            cardQuantities.put(cardName, quantity - 1);
            return true;
        }
        return false;
    }

    public void printSupply() {
        System.out.println("Supply: ");
        for (String cardName : cardQuantities.keySet()) {
            System.out.println(cardName + ": " + cardQuantities.get(cardName));
        }
    }

    public GameDeck getGameDeck() {
        return ImmutableMap.copyOf(this, cardQuantities);
    }
}
