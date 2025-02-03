package edu.brandeis.cosi103a.groupe;

/*
 * This class creates a card
 * 
 * Emily Szabo
 * emilyszabo@brandeis.edu
 * Jan. 27th, 2025
 * COSI 103A ip2
 */
abstract class Card {
    private String name;
    private int cost;
    
    /**
     * Constructor for the Card class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     */
    public Card(String name, int cost) {
        this.name = name;
        this.cost = cost;
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
}
