package edu.brandeis.cosi103a.groupe;

import com.google.common.collect.ImmutableList;
import com.plotsquared.core.player.ConsolePlayer;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ConsolePlayerTest {
    private ConsolePlayer consolePlayer;
    private GameState dummyGameState;
    private ImmutableList<Decision> decisions;

    // Simple Decision implementation for testing
    private static final class TestDecision implements Decision {
        private final String name;

        public TestDecision(String name) {
            this.name = name;
        }

        @Override

        public String getDescription() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Before
    public void setUp() {
        consolePlayer = new ConsolePlayer("TestPlayer");
        dummyGameState = new GameState(/* provide necessary parameters here */); // Provide suitable constructor
                                                                                 // parameters
        // Example: dummyGameState = new GameState(param1, param2, ...);

        // Create test decisions
        Decision decision1 = new TestDecision("Decision1");
        Decision decision2 = new TestDecision("Decision2");

        decisions = ImmutableList.of(decision1, decision2);
    }

    @Test
    public void testMakeDecision_ValidInput() {
        // Simulate user input "1\n" (choosing the second decision)
        InputStream originalSystemIn = System.in;
        ByteArrayInputStream testInput = new ByteArrayInputStream("1\n".getBytes());
        System.setIn(testInput);

        Decision chosenDecision = consolePlayer.makeDecision(dummyGameState, decisions, Optional.empty());

        assertEquals("Decision2", chosenDecision.toString());

        // Restore original System.in
        System.setIn(originalSystemIn);
    }
}
