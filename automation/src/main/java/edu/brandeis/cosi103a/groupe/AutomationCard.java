package edu.brandeis.cosi103a.groupe;

import edu.brandeis.cosi.atg.api.cards.Card;

/*
 * This class creates a card that is an automation
 * 
 */
class AutomationCard extends Card{
    public Type type;
    private int ID;
    
    /**
     * Constructor for the AutomationCard class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     * @param ap The Automation Points (AP) the card provides.
     */
    public AutomationCard(Card.Type type, int id) {
        super(type, id);
        this.type = type;
        this.ID = id;
    }
    
    // /**
    //  * Gets the Automation Points (AP) of the card.
    //  * @return The AP of the card.
    //  */
    // @Override
    // public int getAp() {
    //     return ap;
    // }
    
    /**
     * Gets the money value of the card.
     * @return The money value of the card, which is always 0 for automation cards.
     */

    public int getID() {
        return this.ID;
    }

}