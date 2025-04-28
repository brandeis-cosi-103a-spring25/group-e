package edu.brandeis.cosi103a.groupe.engineTests;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.PlayCardEvent;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
import edu.brandeis.cosi103a.groupe.Other.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.Other.Supply;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;
/*
 * COSI 103a - Group E
 * April 28th, 2025
 * This class contains tests for the GameEngine class
 */
public class GameEngineTest {
    private ourPlayer player1;
    private ourPlayer player2;
    private GameObserver observer;
    private GameEngine engine;
    private Player mockInnerPlayer1;
    private Player mockInnerPlayer2;

    @Before
    public void setUp() {
        player1 = mock(ourPlayer.class);
        player2 = mock(ourPlayer.class);
        observer = mock(GameObserver.class);
        engine = new GameEngine(player1, player2, observer);
        
        // Mock the inner Player objects
        mockInnerPlayer1 = mock(Player.class);
        mockInnerPlayer2 = mock(Player.class);

        player1.setPlayer(mockInnerPlayer1); // Set the inner player for player1
        player2.setPlayer(mockInnerPlayer2); // Set the inner player for player2

        // When getPlayer() is called on player1 or player2, return the inner mocks
        when(player1.getPlayer()).thenReturn(mockInnerPlayer1);
        when(player2.getPlayer()).thenReturn(mockInnerPlayer2);
    }
    
    //Test to ensure that the GameEngine is initialized correctly
    @Test
    public void testInitialization() {
        assertNotNull(engine);
        assertNotNull(player1);
        assertNotNull(player2);
        assertNotNull(observer);
    }

    
    /** 
     * Tests the action phase of the game engine.
     * @throws PlayerViolationException
     */
    @Test
    public void testActionPhase() throws PlayerViolationException {
       // Create the real ourPlayer object
       ourPlayer realPlayer = new ourPlayer("Test Player");

       // Spy on it so we can verify method calls but still run real logic
       ourPlayer playerSpy = spy(realPlayer);

       // Set a mock Player to control decisions
       Player mockInnerPlayer = mock(Player.class);
       playerSpy.setPlayer(mockInnerPlayer);

       // Create a valid action card
       Card actionCard = new Card(Card.Type.DAILY_SCRUM, 1); // make sure DAILY_SCRUM is Category.ACTION

       // Simulate hand with the action card
       Hand mockHand = new Hand(ImmutableList.of(), ImmutableList.of(actionCard));
       when(playerSpy.getHand()).thenReturn(mockHand);
       when(playerSpy.getActions()).thenReturn(1); // needed to enter loop

       // Mock decision-making
       when(mockInnerPlayer.makeDecision(any(), any(), any()))
         .thenReturn(new PlayCardDecision(actionCard))
         .thenReturn(new EndPhaseDecision(GameState.TurnPhase.ACTION));

       // Run action phase
       engine.actionPhase(playerSpy);

       // Verify playCard was actually called
       verify(playerSpy, times(1)).playCard(actionCard); 
    }
    

    
    /** 
     * Tests the money phase of the game engine.
     * @throws PlayerViolationException
     */
    @Test
    public void testMoneyPhase() throws PlayerViolationException {
        ImmutableList<Card> playedCards = ImmutableList.of(); // or some other list of played cards
        ImmutableList<Card> unplayedCards = ImmutableList.of(new Card(Card.Type.BITCOIN, 1));
        Hand mockHand = new Hand(playedCards, unplayedCards);

        when(player1.getHand()).thenReturn(mockHand);
        when(player1.getPlayer().makeDecision(any(), any(), any()))
                .thenReturn(new PlayCardDecision(new Card(Card.Type.BITCOIN, 1)))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY));

        engine.moneyPhase(player1);

        verify(player1, times(1)).playCard(any());
        verify(observer, times(1)).notifyEvent(any(), any(PlayCardEvent.class));
    }
    
    //Test to ensure that the cleanup phase of the game engine works correctly
    @Test
    public void testCleanupPhase() {
        when(player1.getHand()).thenReturn(new Hand(ImmutableList.of(), ImmutableList.of()));

        engine.cleanupPhase(player1);

        verify(player1, times(1)).cleanup();
        verify(player1, times(1)).drawHand(5);
        verify(observer, times(1)).notifyEvent(any(), any(EndTurnEvent.class));
    }
    
    //Tests playFullTurn method of the GameEngine class
    @Test
    public void testPlayFullTurn() throws PlayerViolationException {
        ImmutableList<Card> playedCards = ImmutableList.of(); // or some other list of played cards
        ImmutableList<Card> unplayedCards = ImmutableList.of(new Card(Card.Type.BITCOIN, 1));
        Hand mockHand = new Hand(playedCards, unplayedCards);


        when(player1.getHand()).thenReturn(mockHand);
        when(player2.getHand()).thenReturn(mockHand);
        when(player1.getPlayer().makeDecision(any(), any(), any()))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.ACTION)) // end ACTION phase
                .thenReturn(new PlayCardDecision(new Card(Card.Type.BITCOIN, 1)))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.BUY));
        when(player2.getPlayer().makeDecision(any(), any(), any()))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.ACTION)) // end ACTION phase
                .thenReturn(new PlayCardDecision(new Card(Card.Type.BITCOIN, 1)))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY))
                .thenReturn(new EndPhaseDecision(GameState.TurnPhase.BUY));

        engine.playFullTurn(player1);
        engine.playFullTurn(player2);

        verify(player1.getPlayer(), times(1)).makeDecision(any(), any(), any());
        verify(player2.getPlayer(), times(1)).makeDecision(any(), any(), any());
    }
    
    //Test to ensure that the distributeCards method works correctly
    @Test
    public void testDistributeCards() {
        Supply mockSupply = mock(Supply.class);
        ourPlayer mockPlayer1 = mock(ourPlayer.class);
        ourPlayer mockPlayer2 = mock(ourPlayer.class);

        GameEngine engine = new GameEngine(mockPlayer1, mockPlayer2, new ConsoleGameObserver());

        // Test card distribution logic
        engine.distributeCards(mockPlayer1, mockPlayer2, mockSupply);

        // Verify 7 Bitcoin cards were added to each player's deck
        verify(mockPlayer1, times(7)).addCardToDeck(argThat(card -> card.getType() == Card.Type.BITCOIN));
        verify(mockPlayer2, times(7)).addCardToDeck(argThat(card -> card.getType() == Card.Type.BITCOIN));

        // Verify 14 Bitcoin cards were taken from the supply
        verify(mockSupply, times(14)).takeCard(Card.Type.BITCOIN);

        // Verify 3 Method cards were added to each player's deck
        verify(mockPlayer1, times(3)).addCardToDeck(argThat(card -> card.getType() == Card.Type.METHOD));
        verify(mockPlayer2, times(3)).addCardToDeck(argThat(card -> card.getType() == Card.Type.METHOD));

        // Verify 6 Method cards were taken from the supply
        verify(mockSupply, times(6)).takeCard(Card.Type.METHOD);
    }
}
