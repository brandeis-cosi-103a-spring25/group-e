package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;

public class CardTest {

    /*
     * Tests that automation cards are created correctly.
     */
    @Test
    public void testAutomationCard() {
        Card card = new Card(Card.Type.METHOD, 1);
        assertEquals(2, card.getCost());

        Card card2 = new Card(Card.Type.MODULE, 1);
        assertEquals(5, card2.getCost());

        Card card3 = new Card(Card.Type.FRAMEWORK, 1);
        assertEquals(8, card3.getCost());
    }

    @Test
    public void testCryptocurrencyCard() {
        Card card = new Card(Card.Type.BITCOIN, 31);
        assertEquals(1, card.getValue());
        assertEquals(1, card.getCost());

        Card card2 = new Card(Card.Type.ETHEREUM, 31);
        assertEquals(2, card2.getValue());
        assertEquals(3, card2.getCost());

        Card card3 = new Card(Card.Type.DOGECOIN, 31);
        assertEquals(3, card3.getValue());
        assertEquals(6, card3.getCost());
    }
}
