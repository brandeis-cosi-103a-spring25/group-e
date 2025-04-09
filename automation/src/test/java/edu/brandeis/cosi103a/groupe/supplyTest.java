package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.brandeis.cosi103a.groupe.Supply; // Import the Supply class

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;

public class supplyTest {

    @Test
    public void testGetQuantities() {
        // Create a new supply
        Supply supply = new Supply();

        assertEquals(true, supply.takeCard(Card.Type.METHOD));
    }

    @Test
    public void testUniqueCardIdsInSupply() {
        Supply supply = new Supply();
        List<Card> cards = supply.getAvailableCardsInSupply();

        Set<Integer> ids = new HashSet<>();

        for (Card card : cards) {
            boolean added = ids.add(card.getId());
            assertTrue("Duplicate card ID found: " + card.getId(), added);
        }

        // Sanity check
        assertEquals("Card count and unique ID count do not match", cards.size(), ids.size());
    }
}
