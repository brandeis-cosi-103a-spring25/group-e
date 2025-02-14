package edu.brandeis.cosi103a.groupe;

/*
 * This class creates a card that is a cryptocurrency
 * 
 */
class CryptocurrencyCard extends Card{
    private int money;
    
    /**
     * Constructor for the CryptocurrencyCard class.
     * @param name The name of the card.
     * @param cost The cost of the card in cryptocoins.
     * @param money The value of the card in cryptocoins when played.
     */
    public CryptocurrencyCard(String name, int cost, int money, Type type) {
        super(name, cost, type);
        this.money = money;
    }
    
    /**
     * Gets the Automation Points (AP) of the card.
     * @return The AP of the card, which is always 0 for cryptocurrency cards.
     */
    @Override
    public int getAp() {
        return 0;
    }
    
    /**
     * Gets the money value of the card.
     * @return The money value of the card.
     */
    @Override
    public int getMoney() {
        return money;
    }
}
