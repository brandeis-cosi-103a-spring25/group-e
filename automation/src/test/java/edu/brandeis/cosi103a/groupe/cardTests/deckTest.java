package edu.brandeis.cosi103a.groupe.cardTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Other.Deck;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class tests the functionality of Deck in the game.
 */
public class deckTest {
    
    //Tests that the deck functionality works correctly.
    @Test
    public void testDeck() {
        // Create a new deck
        Deck deck = new Deck();

        // Add cards to the deck
        for (int i = 0; i < 10; i++) {
            deck.addCard(new Card(Card.Type.METHOD, i));
        }

        // Shuffle the deck
        deck.shuffle();

        // Draw a card from the deck
        Card drawnCard = deck.drawCard();
        assertEquals(drawnCard.getType(), Card.Type.METHOD);

        // Check if the deck is empty
        boolean isEmpty = deck.isEmpty();
        assertEquals(false, isEmpty);
    }

    //Tests that adding a card to the deck works correctly.
    @Test
    public void testAddCard() {
        // Create a new deck
        Deck deck = new Deck();

        // Add a card to the deck
        Card card = new Card(Card.Type.METHOD, 1);
        deck.addCard(card);

        // Check if the card was added
        Card drawnCard = deck.drawCard();
        assertEquals(card, drawnCard);
    }

}
