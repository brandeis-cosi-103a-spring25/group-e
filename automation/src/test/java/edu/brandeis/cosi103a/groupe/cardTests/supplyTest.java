package edu.brandeis.cosi103a.groupe.cardTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Other.Supply;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class contains tests for the Supply class
 */
public class supplyTest {
    
    //Test to ensure that the Supply class can be instantiated
    @Test
    public void testGetQuantities() {
        // Create a new supply
        Supply supply = new Supply();

        assertEquals(true, supply.takeCard(Card.Type.METHOD));
    }
    
    //Test to ensure that the Supply class creates unique card IDs
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
