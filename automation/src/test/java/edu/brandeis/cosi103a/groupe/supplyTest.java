package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.cards.Card;

public class supplyTest {
    
    @Test
    public void testGetQuantities() {
        // Create a new supply
        Supply supply = new Supply();
       
        assertEquals(true, supply.takeCard(Card.Type.METHOD));
        
    }
}
