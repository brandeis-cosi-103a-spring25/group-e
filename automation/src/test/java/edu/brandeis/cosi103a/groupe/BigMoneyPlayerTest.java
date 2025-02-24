package edu.brandeis.cosi103a.groupe;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class BigMoneyPlayerTest {
    private BigMoneyPlayer bigMoneyPlayer;
    private GameState dummyGameState;

    // Simple Decision class for testing
    private static final class TestDecision implements Decision {
        private final String name;
        private final int cost;

        public TestDecision(String name, int cost) {
            this.name = name;
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return name + " (Cost: " + cost + ")";
        }

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
        }
    }

    @Before
    public void setUp() {
        bigMoneyPlayer = new BigMoneyPlayer("BigMoneyBot");
        dummyGameState = new GameState(); // Assume default constructor for testing
    }

    @Test
    public void testMakeDecision_PrioritizesFramework() {
        // Given both a Framework and Money option, it should choose Framework
        Decision frameworkDecision = new TestDecision("Framework", 5);
        Decision moneyDecision = new TestDecision("Money", 3);

        ImmutableList<Decision> options = ImmutableList.of(frameworkDecision, moneyDecision);

        Decision chosenDecision = bigMoneyPlayer.makeDecision(dummyGameState, options, Optional.empty());

        assertEquals("Framework (Cost: 5)", chosenDecision.toString());
    }

    @Test
    public void testMakeDecision_ChoosesBestMoneyCardWhenNoFramework() {
        // When only Money cards are available, it should pick the highest-cost one
        Decision moneyDecision1 = new TestDecision("Money", 3);
        Decision moneyDecision2 = new TestDecision("Money", 6);

        ImmutableList<Decision> options = ImmutableList.of(moneyDecision1, moneyDecision2);

        Decision chosenDecision = bigMoneyPlayer.makeDecision(dummyGameState, options, Optional.empty());

        assertEquals("Money (Cost: 6)", chosenDecision.toString());
    }

    @Test
    public void testMakeDecision_WithReasonDisplayed() {
        // Ensure the reason (event) is correctly handled
        Event testEvent = new Event() {
            @Override
            public String toString() {
                return "You must buy a card";
            }
        };

        Decision frameworkDecision = new TestDecision("Framework", 5);
        Decision moneyDecision = new TestDecision("Money", 3);

        ImmutableList<Decision> options = ImmutableList.of(frameworkDecision, moneyDecision);

        Decision chosenDecision = bigMoneyPlayer.makeDecision(dummyGameState, options, Optional.of(testEvent));

        assertEquals("Framework (Cost: 5)", chosenDecision.toString());
    }
}
