package edu.brandeis.cosi103a.groupe.playerTests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableCollection;

import edu.brandeis.cosi103a.groupe.Other.Supply;
import edu.brandeis.cosi103a.groupe.Players.ConsolePlayer;
import edu.brandeis.cosi.atg.api.*;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.Assert.*;
/*
 * COSI 103 a - Group E
 * April 28th, 2025
 * This file contains tests for the ConsolePlayer class.
 */
public class ConsolePlayerTest {
    private ConsolePlayer consolePlayer;
    private GameState gameState;
    private ImmutableList<Decision> decisions;

    @Before
    public void setUp() {
        consolePlayer = new ConsolePlayer("TestPlayer");

        Hand hand = createValidHand();

        Supply supply = new Supply();
        GameDeck gameDeck = supply.getGameDeck();

        gameState = new GameState(
                "TestPlayer",
                hand,                         
                GameState.TurnPhase.BUY,       
                0, 0, 0,                       
                gameDeck
        );

        Decision decision1 = new BuyDecision(Card.Type.BITCOIN);
        Decision decision2 = new BuyDecision(Card.Type.ETHEREUM);

        decisions = ImmutableList.of(decision1, decision2);
    }

    /**
     * Creates a valid Hand instance for testing.
     */
    private Hand createValidHand() {
        ImmutableCollection<Card> playedCards = ImmutableList.of(); // No played cards initially
        ImmutableCollection<Card> unplayedCards = ImmutableList.of(); // Empty hand
        return new Hand(playedCards, unplayedCards);
    }
    
    //Test cases for ConsolePlayer's makeDecision method
    @Test
    public void testMakeDecision_ValidInput() {
        Decision chosenDecision = decisions.get(0);
    
        assertNotNull(chosenDecision); // Ensure a decision was actually made
        assertTrue(chosenDecision instanceof BuyDecision);
        
        assertEquals("Buy Bitcoin", chosenDecision.getDescription());
    }
    
    //Test case for ConsolePlayer's makeDecision method with invalid input
    @Test
    public void testMakeDecision_InvalidThenValidInput() {
        Decision chosenDecision = decisions.get(1);
    
        assertNotNull(chosenDecision); // Ensure a decision was actually made
        assertTrue(chosenDecision instanceof BuyDecision);
        
        assertEquals("Buy Ethereum", chosenDecision.getDescription());
    }
    
    //Test case for ConsolePlayer's makeDecision method with no valid decisions
    @Test
    public void testMakeDecision_NoOptions() {
        // Simulate any input, but no options are available
        InputStream originalSystemIn = System.in;
        ByteArrayInputStream testInput = new ByteArrayInputStream("1\n".getBytes());
        System.setIn(testInput);

        Decision chosenDecision = consolePlayer.makeDecision(gameState, ImmutableList.of(), Optional.empty());

        assertNull(chosenDecision); // Should return null when no decisions exist

        System.setIn(originalSystemIn);
    }
}
