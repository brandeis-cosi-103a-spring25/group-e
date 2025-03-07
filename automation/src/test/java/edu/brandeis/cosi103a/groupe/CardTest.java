package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
import edu.brandeis.cosi103a.groupe.Cards.CryptocurrencyCard;

public class CardTest {

    /*
     * Tests that automation cards are created correctly.
     */
    @Test
    public void testAutomationCard() {
        AutomationCard card = new AutomationCard(Card.Type.METHOD, 1);
        assertEquals(1, card.getAp());
        assertEquals(2, card.getCost());

        AutomationCard card2 = new AutomationCard(Card.Type.MODULE, 1);
        assertEquals(3, card2.getAp());
        assertEquals(5, card2.getCost());

        AutomationCard card3 = new AutomationCard(Card.Type.FRAMEWORK, 1);
        assertEquals(6, card3.getAp());
        assertEquals(8, card3.getCost());
    }

    @Test
    public void testCryptocurrencyCard() {
        CryptocurrencyCard card = new CryptocurrencyCard(Card.Type.BITCOIN, 31);
        assertEquals(1, card.getValue());
        assertEquals(1, card.getCost());

        CryptocurrencyCard card2 = new CryptocurrencyCard(Card.Type.ETHEREUM, 31);
        assertEquals(2, card2.getValue());
        assertEquals(3, card2.getCost());

        CryptocurrencyCard card3 = new CryptocurrencyCard(Card.Type.DOGECOIN, 31);
        assertEquals(3, card3.getValue());
        assertEquals(6, card3.getCost());
    }
}
