package edu.brandeis.cosi103a.groupe.Cards;

import edu.brandeis.cosi.atg.api.cards.Card;

/*
 * This class creates a card that is a cryptocurrency
 * 
 */
public class CryptocurrencyCard extends Card{
    private int ID, money;
    public Type type;
    private String name;
    
    /**
     * Constructor for the CryptocurrencyCard class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     * @param money The value of the card in cryptocoins when played.
     */
    public CryptocurrencyCard(Card.Type type, int id) {
        super(type, id);
        this.type = type;
        this.ID = id;
    }

    public void setStuff(int money, String name) {
        this.money = money;
        this.name = name;
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

    public String getName() {
        return this.name;
    }
    
    public int getMoney() {
        return this.money;
    }
}
