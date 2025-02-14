package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/*
 * This class creates a deck of cards
 * 
 */
public class Deck {
    private LinkedList<GameCard> cards;
    
    /*
     * Constructor for the deck
     */
    public Deck() {
        cards = new LinkedList<>();
    }
    
    /*
     * This method adds a card to the deck
     * @param card The card to add to the deck
     */
    public void addCard(GameCard card) {
        cards.add(card);
    }
    
    /*
     * This method adds a list of cards to the deck
     * @param cards The list of cards to add to the deck
     */
    public void addCards(List<GameCard> cards) {
        this.cards.addAll(cards);
    }
    
    /*
     * This method draws a card from the deck
     * @return The card drawn from the deck
     */
    public GameCard drawCard() {
        return cards.poll();
    }
    
    /*
     * This method checks if the deck is empty
     * @return True if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /*
     * This method shuffles the deck
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a hand of cards from the deck.
     * @param handSize The number of cards to draw.
     * @return The list of drawn cards.
     */
    public List<GameCard> drawHand(int handSize) {
        List<GameCard> hand = new ArrayList<>();
        for (int i = 0; i < handSize && i < cards.size(); i++) {
            hand.add(cards.get(i));
        }
        return hand;
    }

    /**
     * Gets the total Automation Points (AP) in the deck.
     * @return The total AP.
     */
    public int getTotalAp() {
        return cards.stream()
                    .filter(card -> card instanceof AutomationCard)
                    .mapToInt(GameCard::getAp)
                    .sum();
    }

    /**
     * Prints the contents of the deck.
     */
    public void printDeck() {
        cards.forEach(card -> System.out.println("- " + card.getName() + " (AP: " + card.getAp() + ")"));
    }
}
