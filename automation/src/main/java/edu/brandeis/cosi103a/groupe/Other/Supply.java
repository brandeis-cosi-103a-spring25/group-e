package edu.brandeis.cosi103a.groupe.Other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brandeis.cosi.atg.api.GameDeck;
import edu.brandeis.cosi.atg.api.cards.Card;

public class Supply {
    private final Map<Card.Type, List<Card>> cardStacks;
    private int cardIdCounter;

    /**
     * Constructor for the supply
     */
    public Supply() {
        cardStacks = new EnumMap<>(Card.Type.class);
        cardIdCounter = 1;

        addCards(Card.Type.METHOD, 14);
        addCards(Card.Type.MODULE, 8);
        addCards(Card.Type.FRAMEWORK, 8);
        addCards(Card.Type.BITCOIN, 60);
        addCards(Card.Type.ETHEREUM, 40);
        addCards(Card.Type.DOGECOIN, 30);
        addCards(Card.Type.BACKLOG, 10);
        addCards(Card.Type.CODE_REVIEW, 10);
        addCards(Card.Type.DAILY_SCRUM, 10);
        addCards(Card.Type.EVERGREEN_TEST, 10);
        addCards(Card.Type.HACK, 10);
        addCards(Card.Type.IPO, 10);
        addCards(Card.Type.PARALLELIZATION, 10);
        addCards(Card.Type.REFACTOR, 10);
        addCards(Card.Type.TECH_DEBT, 10);
        addCards(Card.Type.MONITORING, 10);
    }

    
    /** 
     * @param type
     * @param quantity
     */
    private void addCards(Card.Type type, int quantity) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            cards.add(new Card(type, cardIdCounter++));
        }
        cardStacks.put(type, cards);
    }

    /**
     * Gets the quantity of a specific card type in the supply.
     * 
     * @param type The type of the card.
     * @return The quantity of the card type in the supply.
     */
    public int getCardQuantity(Card.Type type) {
        return cardStacks.getOrDefault(type, List.of()).size();
    }

    /**
     * Gets a list of all available cards remaining in the supply.
     * 
     * @return List of available cards.
     */
    public List<Card> getAvailableCardsInSupply() {
        List<Card> availableCards = new ArrayList<>();
        for (List<Card> stack : cardStacks.values()) {
            availableCards.addAll(stack);
        }
        return availableCards;
    }

    /**
     * Takes a card of the specified type from the supply.
     * 
     * @param type The type of card to take.
     * @return The card if available, otherwise null.
     */
    public boolean takeCard(Card.Type type) {
        List<Card> cards = cardStacks.getOrDefault(type, new ArrayList<>());
        if (!cards.isEmpty()) {
            cards.remove(0);
            return true;
        }
        return false;
    }

    /**
     * Prints the supply to the console.
     */
    public void printSupply() {
        System.out.println("Supply:");
        for (Card.Type type : cardStacks.keySet()) {
            System.out.println(type + ": " + getCardQuantity(type));
        }
    }

    /**
     * Gets a GameDeck representation of the current supply.
     * 
     * @return GameDeck based on the current supply.
     */
    public GameDeck getGameDeck() {
        Map<Card.Type, Integer> deckMap = new EnumMap<>(Card.Type.class);
        for (Card.Type type : cardStacks.keySet()) {
            deckMap.put(type, getCardQuantity(type));
        }
        return new GameDeck(ImmutableMap.copyOf(deckMap));
    }

    /**
     * Gets an unmodifiable view of the card stacks in the supply.
     * 
     * @return An unmodifiable map of card types to their corresponding stacks.
    */
    public Map<Card.Type, List<Card>> getCardStacks() {
       return Collections.unmodifiableMap(cardStacks);
    }

    /**
     * Counts how many card types are completely depleted.
     * 
     * @return The number of card types with no cards left.
     */
    public int getEmptyPileCount() {
        int emptyCount = 0;
        for (List<Card> stack : cardStacks.values()) {
            if (stack.isEmpty()) {
                emptyCount++;
            }
        }
        return emptyCount;
    }
}
