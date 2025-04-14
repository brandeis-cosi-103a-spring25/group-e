package edu.brandeis.cosi103a.groupe.cardTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Other.Deck;

public class deckTest {

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
