package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.*;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi103a.groupe.Players.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class RandomPlayerTest {

    @Test
    public void testConstructor() {
        RandomBuyPlayer player = new RandomBuyPlayer("TestPlayer");
        assertEquals("TestPlayer", player.getName());
    }

    @Test
    public void testMakeBuyDecision() {
        RandomBuyPlayer player = new RandomBuyPlayer("TestPlayer");
        ourPlayer ourPlayer1 = new ourPlayer(player.getName());
        ourPlayer1.setPlayer(player);
        ourPlayer1.setPhase("Buy");

        // Mock GameState
        GameState state = new GameState(
                "TestPlayer",
                null,
                GameState.TurnPhase.BUY,
                1, // Available actions
                10, // Enough money
                1, // Available buys
                null // Deck not needed
        );

        // Create mock decisions
        BuyDecision decision1 = new BuyDecision(Card.Type.BITCOIN);
        BuyDecision decision2 = new BuyDecision(Card.Type.FRAMEWORK);
        ImmutableList<Decision> options = ImmutableList.of(decision1, decision2);

        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(options.contains(decision)); // Decision should be one of the available options
    }

    @Test
    public void testPlayDecision() {
        RandomBuyPlayer player = new RandomBuyPlayer("TestPlayer");
        ourPlayer ourPlayer1 = new ourPlayer(player.getName());
        ourPlayer1.setPlayer(player);

        Card bitcoinCard = new Card(Card.Type.BITCOIN, 1);
        Card frameworkCard = new Card(Card.Type.FRAMEWORK, 2);

        ImmutableList<Card> handCards = ImmutableList.of(bitcoinCard, frameworkCard);

        Hand hand = new Hand(null, handCards);

        ourPlayer1.setPhase("Play");

        // Mock GameState
        GameState state = new GameState(
                "TestPlayer",
                hand,
                GameState.TurnPhase.MONEY,
                1, // Available actions
                10, // Enough money
                1, // Available buys
                null // Deck not needed
        );

        // Create mock decisions
        PlayCardDecision decision1 = new PlayCardDecision(bitcoinCard);
        PlayCardDecision decision2 = new PlayCardDecision(frameworkCard);

        ImmutableList<Decision> options = ImmutableList.of(decision1, decision2);

        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision != null); 
    }

    @Test
    public void testMoney() {
        RandomBuyPlayer player = new RandomBuyPlayer("TestPlayer");
        ourPlayer ourPlayer1 = new ourPlayer(player.getName());
        ourPlayer1.setPlayer(player);
        ourPlayer1.setMoney(5);
        assertEquals(5, ourPlayer1.getMoney());

        Card bitcoinCard = new Card(Card.Type.BITCOIN, 1);
        Card eth = new Card(Card.Type.ETHEREUM, 5);

        ImmutableList<Card> handCards = ImmutableList.of(bitcoinCard, eth);

        ourPlayer1.setHand(handCards);
        System.out.println(ourPlayer1.getHand().toString());
        int val = ourPlayer1.playHand();
        assertEquals(9, ourPlayer1.getMoney() + val);

    }
}
