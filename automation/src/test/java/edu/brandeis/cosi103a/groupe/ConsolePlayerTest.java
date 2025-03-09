package edu.brandeis.cosi103a.groupe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableCollection;
import edu.brandeis.cosi103a.groupe.Players.ConsolePlayer;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
import edu.brandeis.cosi103a.groupe.Deck;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi.atg.api.*;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import edu.brandeis.cosi.atg.api.event.Event;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.Assert.*;

public class ConsolePlayerTest {
    private ConsolePlayer consolePlayer;
    private ourPlayer player;
    private GameState gameState;
    private ImmutableList<Decision> decisions;
    private Deck deck; // Real deck instance

    @Before
    public void setUp() {
        // Create a real Deck with some test cards
        deck = new Deck();

        // Create a subclass of ourPlayer that implements makeDecision()
        player = new ourPlayer("TestPlayer") {
            @Override
            public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
                return options.isEmpty() ? null : options.get(0); // Default decision logic
            }

            // Expose deck through a getter
            public Deck getDeck() {
                return deck;
            }
        };

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
    
    @Test
    public void testMakeDecision_ValidInput() {
        Decision chosenDecision = decisions.get(0);
    
        assertNotNull(chosenDecision); // Ensure a decision was actually made
        assertTrue(chosenDecision instanceof BuyDecision);
        
        assertEquals("Buy Bitcoin", chosenDecision.getDescription());
    }
    
    @Test
    public void testMakeDecision_InvalidThenValidInput() {
        Decision chosenDecision = decisions.get(1);
    
        assertNotNull(chosenDecision); // Ensure a decision was actually made
        assertTrue(chosenDecision instanceof BuyDecision);
        
        assertEquals("Buy Ethereum", chosenDecision.getDescription());
    }
    
    

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
