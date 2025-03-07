package edu.brandeis.cosi103a.groupe.Cards;

import edu.brandeis.cosi.atg.api.cards.Card;

/*
 * This class creates a card that is a cryptocurrency
 * 
 */
public class CryptocurrencyCard extends Card{
    private int ID;
    public Type type;
    
    /**
     * Constructor for the CryptocurrencyCard class.
     * @param cost The cost of the card in cryptocoins.
     * @param money The value of the card in cryptocoins when played.
     */
    public CryptocurrencyCard(Card.Type type, int id) {
        super(type, id);
        this.ID = id;
        this.type = type;
    }
    
    /**
     * Gets the Automation Points (AP) of the card.
     * @return The AP of the card, which is always 0 for cryptocurrency cards.
     */
    public int getAp() {
        return 0;
    }
    
    /**
     * Gets the ID of the card.
     * @return The ID of the card.
     */
  
    public int getID() {
        return this.ID;
    }

    /*
     * Gets the cost of the card.
     * @return The cost of the card.
     */
    public int getCost() {
        return this.type.getCost();
    }
    
    /*
     * Gets the value of the card.
     * @return The value of the card.
     */
    public int getValue() {
        return this.type.getValue();
    }
}
