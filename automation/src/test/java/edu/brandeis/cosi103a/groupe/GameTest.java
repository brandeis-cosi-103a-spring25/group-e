package edu.brandeis.cosi103a.groupe;

import java.util.HashMap;
import java.util.Map;
import edu.brandeis.cosi.atg.api.cards.Card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */

public class GameTest 
{
    // /**
    //  * Tests that automation frameworks are create with the correct ap 
    //  */
    // @Test
    // public void testAutomationCard()
    // {
    //      // Create an AutomationCard with a specific AP value
    //      AutomationCard card = new AutomationCard("Framework", 8, 6);

    //      // Verify that the getAp method returns the correct AP value
    //      assertEquals(6,card.getAp());
    // }
    
    // /*
    //  * Tests that the supply is created with the correct quantities of cards
    //  */
    // @Test
    // public void testGetCardQuantity() {
    //     // Create a new Supply object
    //     Supply supply = new Supply();

    //     // Verify the initial quantities of the cards
    //     assertEquals(14, supply.getCardQuantity("Method"));
    //     assertEquals(8, supply.getCardQuantity("Module"));
    //     assertEquals(8, supply.getCardQuantity("Framework"));
    //     assertEquals(60, supply.getCardQuantity("Bitcoin"));
    //     assertEquals(40, supply.getCardQuantity("Ethereum"));
    //     assertEquals(30, supply.getCardQuantity("Dogecoin"));

    //     // Take some cards from the supply
    //     supply.takeCard("Method");
    //     supply.takeCard("Bitcoin");

    //     // Verify the updated quantities of the cards
    //     assertEquals(13, supply.getCardQuantity("Method"));
    //     assertEquals(59, supply.getCardQuantity("Bitcoin"));
    // }
}
