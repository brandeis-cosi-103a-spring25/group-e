package edu.brandeis.cosi103a.groupe.Cards;

import edu.brandeis.cosi.atg.api.cards.Card;

/*
 * This class creates a card that is an automation
 * 
 */
public class AutomationCard extends Card{
    public Type type;
    private int ID;
    
    /**
     * Constructor for the AutomationCard class.
     * @param cost The cost of the card in cryptocoins.
     * @param ap The Automation Points (AP) the card provides.
     */
    public AutomationCard(Card.Type type, int id) {
        super(type, id);
        this.ID = id;
        this.type = type;
    }
    
    /**
    * Gets the Automation Points (AP) of the card.
    * @return The AP of the card.
    */
    public int getAp() {
      return this.type.getValue();
    }
    
    /**
     * Gets the money value of the card.
     * @return The money value of the card, which is always 0 for automation cards.
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
}