package edu.brandeis.cosi103a.groupe.Cards;

import edu.brandeis.cosi.atg.api.cards.Card;  // Import API Card
import edu.brandeis.cosi.atg.api.cards.Card.Type;  
/*
 * This class creates a card
 * 
 */
abstract class GameCard extends Card{ 
    private String name;
    private int cost;
    private Type type;
    
    /**
     * Constructor for the Card class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     */
    public GameCard(String name, int cost, Card.Type type, int id) {
        super(type, id);
        this.name = name;
        this.cost = cost;
        this.type = type;
    }
    
    /**
     * Gets the name of the card. 
     * @return The name of the card.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the cost of the card.
     * @return The cost of the card in cryptocoins.
     */
    public int getCost() {
        return cost;
    }
    
    /**
     * Gets the Automation Points (AP) of the card.
     * @return The AP of the card.
     */
    public abstract int getAp();

    /**
     * Gets the money value of the card.
     * @return The money value of the card.
     */
    public abstract int getMoney();

    public Card getCard() {
        return this;
    }
        
    /*public enum Type {
            MODULE,
            FRAMEWORK,
            BITCOIN,
            ETHEREUM,
            DOGECOIN, // Standardizing capitalization
            METHOD;
    }*/
        
    public Type getType() {
        return this.type; 
    }

}
