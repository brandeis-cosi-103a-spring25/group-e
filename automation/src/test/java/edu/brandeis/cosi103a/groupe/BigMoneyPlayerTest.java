package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.*;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class BigMoneyPlayerTest {
    private BigMoneyPlayer player;
    private GameState gameState;
    private BuyDecision moneyDecision;
    private BuyDecision frameworkDecision;

    @Before
    public void setUp() {
        player = new BigMoneyPlayer("TestPlayer");

        // Create real BuyDecision instances using card types
        moneyDecision = new BuyDecision(Card.Type.BITCOIN);      // Assume MONEY is a valid card type
        frameworkDecision = new BuyDecision(Card.Type.FRAMEWORK); // Assume FRAMEWORK is a valid card type

        // Create a real GameState (assuming buy phase)
        gameState = new GameState(
            "TestPlayer",
            null,  // Hand is not needed for this test
            GameState.TurnPhase.BUY, // Assume it's the buy phase
            1, // Available actions (not used in this test but required)
            0, // Spendable money
            1, // Available buys
            null // Deck is not needed for this test
        );
    }

    @Test
    public void testMakeDecision_BuysFrameworkWhenAffordable() {
        gameState = new GameState(
            "TestPlayer",
            null,  // Hand is not needed for this test
            GameState.TurnPhase.BUY, // Assume it's the buy phase
            1, // Available actions (not used in this test but required)
            8, // Spendable money
            1, // Available buys
            null // Deck is not needed for this test
        );
        ImmutableList<Decision> options = ImmutableList.of(moneyDecision, frameworkDecision);
        Decision decision = player.makeDecision(gameState, options, Optional.empty());

        assertEquals(frameworkDecision, decision);
    }

    @Test
    public void testMakeDecision_BuysMoneyWhenFrameworkTooExpensive() {
        // Modify gameState to have only 4 money (can't afford Framework)
        gameState = new GameState(
            "TestPlayer",
            null,
            GameState.TurnPhase.BUY,
            1, // Available actions
            4, // Only 4 money available
            1, // Available buys
            null
        );

        ImmutableList<Decision> options = ImmutableList.of(moneyDecision, frameworkDecision);
        Decision decision = player.makeDecision(gameState, options, Optional.empty());

        assertEquals(moneyDecision, decision);
    }
}
