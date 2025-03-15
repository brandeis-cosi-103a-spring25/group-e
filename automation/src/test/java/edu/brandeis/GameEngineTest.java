package edu.brandeis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi.atg.api.decisions.BuyDecision;
import edu.brandeis.cosi.atg.api.decisions.EndPhaseDecision;
import edu.brandeis.cosi.atg.api.decisions.PlayCardDecision;
import edu.brandeis.cosi.atg.api.event.EndTurnEvent;
import edu.brandeis.cosi.atg.api.event.GainCardEvent;
import edu.brandeis.cosi.atg.api.event.GameEvent;
import edu.brandeis.cosi.atg.api.event.PlayCardEvent;
import edu.brandeis.cosi103a.groupe.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.GameEngine;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
import edu.brandeis.cosi103a.groupe.Cards.CryptocurrencyCard;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

public class GameEngineTest {
    private ourPlayer player1;
    private ourPlayer player2;
    private GameObserver observer;
    private GameEngine engine;
    private Supply supply;

    @Before
    public void setUp() {
        player1 = mock(ourPlayer.class);
        player2 = mock(ourPlayer.class);
        observer = mock(GameObserver.class);
        supply = mock(Supply.class);
        engine = new GameEngine(player1, player2, observer);
    }

    @Test
    public void testInitialization() {
        assertNotNull(engine);
        assertNotNull(player1);
        assertNotNull(player2);
        assertNotNull(observer);
    }

    @Test
    public void testMoneyPhase() throws PlayerViolationException {
       ImmutableList<Card> playedCards = ImmutableList.of();  // or some other list of played cards
       ImmutableList<Card> unplayedCards = ImmutableList.of(new CryptocurrencyCard(Card.Type.BITCOIN, 1));
       Hand mockHand = new Hand(playedCards, unplayedCards);
        

        when(player1.getHand()).thenReturn(mockHand);
        when(player1.makeDecision(any(), any(), any())).thenReturn(new PlayCardDecision(new CryptocurrencyCard(Card.Type.BITCOIN, 1)))
                                                      .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY));

        engine.moneyPhase(player1);

        verify(player1, times(1)).playCard(any());
        verify(observer, times(1)).notifyEvent(any(), any(PlayCardEvent.class));
    }

    @Test
    public void testCleanupPhase() {
        when(player1.getHand()).thenReturn(new Hand(ImmutableList.of(), ImmutableList.of()));

        engine.cleanupPhase(player1);

        verify(player1, times(1)).cleanup();
        verify(player1, times(1)).drawHand(5);
        verify(observer, times(1)).notifyEvent(any(), any(EndTurnEvent.class));
    }

    @Test
    public void testPlay() throws PlayerViolationException {
        ImmutableList<Card> playedCards = ImmutableList.of();  // or some other list of played cards
        ImmutableList<Card> unplayedCards = ImmutableList.of(new CryptocurrencyCard(Card.Type.BITCOIN, 1));
        Hand mockHand = new Hand(playedCards, unplayedCards);

        when(player1.getHand()).thenReturn(mockHand);
        when(player2.getHand()).thenReturn(mockHand);
        when(player1.makeDecision(any(), any(), any())).thenReturn(new PlayCardDecision(new CryptocurrencyCard(Card.Type.BITCOIN, 1)))
                                                      .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY))
                                                      .thenReturn(new EndPhaseDecision(GameState.TurnPhase.BUY));
        when(player2.makeDecision(any(), any(), any())).thenReturn(new PlayCardDecision(new CryptocurrencyCard(Card.Type.BITCOIN, 1)))
                                                      .thenReturn(new EndPhaseDecision(GameState.TurnPhase.MONEY))
                                                      .thenReturn(new EndPhaseDecision(GameState.TurnPhase.BUY));

        ImmutableList<ScorePair> scores = engine.play();

        assertNotNull(scores);
        assertEquals(2, scores.size());
    }

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
