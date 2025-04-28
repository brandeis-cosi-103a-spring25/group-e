package edu.brandeis.cosi103a.groupe.playerTests;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.decisions.DiscardCardDecision;
import edu.brandeis.cosi.atg.api.decisions.GainCardDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi103a.groupe.Players.SmartActionPlayer;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class tests the SmartActionPlayer's decision-making logic
 * during various phases of the game.
 */
public class SmartActionPlayerTest {
    
    //Tests player buys the most expensive card
    @Test
    public void testChooseBuyCard_MostExpensive() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new BuyDecision(Card.Type.FRAMEWORK), 
                new BuyDecision(Card.Type.MODULE),    
                new BuyDecision(Card.Type.METHOD)     
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.BUY, 0, 8, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof BuyDecision);
        assertEquals(Card.Type.FRAMEWORK, ((BuyDecision) decision).getCardType());
    }
    
    //Tests player discards the least valuable card
    @Test
    public void testChooseDiscardCard_LeastValuable() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new DiscardCardDecision(new Card(Card.Type.FRAMEWORK, 0)),
                new DiscardCardDecision(new Card(Card.Type.MODULE, 3)),
                new DiscardCardDecision(new Card(Card.Type.METHOD, 1))
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.DISCARD, 0, 0, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof DiscardCardDecision);
        assertEquals(Card.Type.METHOD, ((DiscardCardDecision) decision).getCard().getType());
    }
    
    //Tests player chooses the most valuable money card
    @Test
    public void testChooseMoneyCard_MostValuable() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new PlayCardDecision(new Card(Card.Type.BITCOIN, 1)),
                new PlayCardDecision(new Card(Card.Type.ETHEREUM, 3)),
                new PlayCardDecision(new Card(Card.Type.DOGECOIN, 2))
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.MONEY, 0, 0, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof PlayCardDecision);
        assertEquals(Card.Type.DOGECOIN, ((PlayCardDecision) decision).getCard().getType());
    }
    
    //Tests player chooses monitoring card during reaction phase
    @Test
    public void testChooseReactionCard_Monitoring() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new PlayCardDecision(new Card(Card.Type.MONITORING, 0)),
                new PlayCardDecision(new Card(Card.Type.HACK, 1))
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.REACTION, 0, 0, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof PlayCardDecision);
        assertEquals(Card.Type.MONITORING, ((PlayCardDecision) decision).getCard().getType());
    }
   
    //Tests player chooses the most expensive gain card
    @Test
    public void testChooseGainCard_MostExpensive() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new GainCardDecision(Card.Type.FRAMEWORK), 
                new GainCardDecision(Card.Type.MODULE),    
                new GainCardDecision(Card.Type.METHOD)     
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.GAIN, 0, 8, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof GainCardDecision);
        assertEquals(Card.Type.FRAMEWORK, ((GainCardDecision) decision).getCardType());
    }
    
    //Tests player ends phase when only one option is available
    @Test
    public void testMakeDecision_SingleOption() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new EndPhaseDecision(GameState.TurnPhase.ACTION)
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.ACTION, 0, 0, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof EndPhaseDecision);
    }
    
    //Tests player selects the highest priority action card
    @Test
    public void testMakeDecision_ActionPhase() {
        SmartActionPlayer player = new SmartActionPlayer("TestPlayer");
        ImmutableList<Decision> options = ImmutableList.of(
                new PlayCardDecision(new Card(Card.Type.CODE_REVIEW, 0)),
                new PlayCardDecision(new Card(Card.Type.REFACTOR, 1))
        );

        GameState state = new GameState("Player 1", null, GameState.TurnPhase.ACTION, 0, 0, 0, null);
        Decision decision = player.makeDecision(state, options, Optional.empty());

        assertTrue(decision instanceof PlayCardDecision);
        assertEquals(Card.Type.CODE_REVIEW, ((PlayCardDecision) decision).getCard().getType());
    }
}
