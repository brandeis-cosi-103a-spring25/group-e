package edu.brandeis.cosi103a.groupe;

/*
 * This class creates a card that is an automation
 * 
 */
class AutomationCard extends Card{
    private int ap;
    
    /**
     * Constructor for the AutomationCard class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     * @param ap The Automation Points (AP) the card provides.
     */
    public AutomationCard(String name, int cost, int ap) {
        super(name, cost);
        this.ap = ap;
    }
    
    /**
     * Gets the Automation Points (AP) of the card.
     * @return The AP of the card.
     */
    @Override
    public int getAp() {
        return ap;
    }
    
    /**
     * Gets the money value of the card.
     * @return The money value of the card, which is always 0 for automation cards.
     */
    @Override
    public int getMoney() {
        return 0;
    }
}