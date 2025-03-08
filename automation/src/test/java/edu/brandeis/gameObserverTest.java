package edu.brandeis;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.ConsoleGameObserver;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi.atg.api.event.GainCardEvent;

public class gameObserverTest {
    private ConsoleGameObserver observer;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        observer = new ConsoleGameObserver(); // Console-only observer
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream)); // Capture console output
    }

    /*
     * Test to verify that the notifyEvent method correctly handles a basic event.
     */
    @Test
    public void testNotifyEvent_BasicOutput() {
        GameState mockState = new GameState("Player1", null, GameState.TurnPhase.BUY, 10, 10, 10, null);
        Event mockEvent = new GainCardEvent(Card.Type.BITCOIN, "Player1");

        observer.notifyEvent(mockState, mockEvent);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Event occurred:"));
        assertTrue(output.contains("Bitcoin"));
        assertTrue(output.contains("Player1"));
    }
    
    /*
     * Test to verify that the notifyEvent method correctly handles an EndTurnEvent.
     */
    @Test
    public void testNotifyEvent_EndTurnEvent() {
        GameState mockState = new GameState("Player2", null, GameState.TurnPhase.CLEANUP, 5, 5, 5, null);
        Event mockEvent = new EndTurnEvent();

        observer.notifyEvent(mockState, mockEvent);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Event occurred:"));
        assertTrue(output.contains("End turn"));
    }
    
    /*
     * Test to verify that the notifyEvent method correctly handles null inputs.
     */
    @Test
    public void testNotifyEvent_NullInputs() {
        observer.notifyEvent(null, null);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Event occurred: null at game state null"));
    }
    
    /*
     * Test to verify that the notifyEvent method correctly handles multiple events.
     */
     @Test
    public void testNotifyEvent_MultipleEvents() {
        GameState mockState = new GameState("Player3", null, GameState.TurnPhase.BUY, 15, 15, 15, null);

        observer.notifyEvent(mockState, new GainCardEvent(Card.Type.ETHEREUM, "Player3"));
        observer.notifyEvent(mockState, new EndTurnEvent());

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Ethereum"));
        assertTrue(output.contains("End turn"));
    }
}
