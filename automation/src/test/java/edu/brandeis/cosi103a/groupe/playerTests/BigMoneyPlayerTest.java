package edu.brandeis.cosi103a.groupe.playerTests;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.*;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi103a.groupe.Players.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

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
         ourPlayer ourPlayer1 = new ourPlayer(player.getName());
        ourPlayer1.setPlayer(player);

        // Create BuyDecision instances using proper parameters
        moneyDecision = new BuyDecision(Card.Type.BITCOIN);      // Adjusted to match the expected constructor
        frameworkDecision = new BuyDecision(Card.Type.FRAMEWORK); // Adjusted to match the expected constructor

        // Mock GameState with enough money for testing
        gameState = new GameState(
            "TestPlayer",
            null,  // No hand needed
            GameState.TurnPhase.BUY,
            1,  // Available actions
            0, // Plenty of money
            1,  // Available buys
            null // Deck not needed
        );
    }

    @Test
    public void testMakeDecision_BuysFrameworkWhenAffordable() {
        gameState = new GameState(
            "TestPlayer",
            null,
            GameState.TurnPhase.BUY,
            2, 
            10, // Enough money for Framework
            2, 
            null
        );
        ImmutableList<Decision> options = ImmutableList.of(moneyDecision, frameworkDecision);
        Decision decision = player.makeDecision(gameState, options, Optional.empty());

        assertEquals(frameworkDecision, decision); // Framework is more expensive, so should be chosen
    }

    @Test
    public void testMakeDecision_BuysMoneyWhenFrameworkTooExpensive() {
        gameState = new GameState(
            "TestPlayer",
            null,
            GameState.TurnPhase.BUY,
            2, 
            4, // Not enough for Framework
            2, 
            null
        );

        ImmutableList<Decision> options = ImmutableList.of(moneyDecision, frameworkDecision);
        Decision decision = player.makeDecision(gameState, options, Optional.empty());

        assertEquals(moneyDecision, decision); // Chooses money when Framework is too expensive
    }
}
