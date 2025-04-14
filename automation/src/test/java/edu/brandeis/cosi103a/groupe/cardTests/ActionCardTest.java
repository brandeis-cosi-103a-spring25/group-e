package edu.brandeis.cosi103a.groupe.cardTests;

import static org.mockito.Mockito.*;

import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
import edu.brandeis.cosi103a.groupe.Other.Supply;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

import java.util.Arrays;
import java.util.Collections;

import edu.brandeis.cosi103a.groupe.Cards.ActionCard;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ActionCardTest {

    private Supply mockSupply;
    private GameEngine mockGameEngine;
    private ourPlayer mockPlayer;
    private ourPlayer mockOpponent;
    private ActionCard actionCard;
    private Card hackCard;

    @Before
    public void setUp() {
        mockSupply = mock(Supply.class);
        mockGameEngine = mock(GameEngine.class);
        mockPlayer = mock(ourPlayer.class);
        mockOpponent = mock(ourPlayer.class);
        actionCard = new ActionCard(mockSupply, mockGameEngine);

        // Create a Hack card mock
        hackCard = mock(Card.class);
        when(hackCard.getType()).thenReturn(Card.Type.HACK);
    }

    @Test
    public void testPlayActionCard_Backlog() throws PlayerViolationException {
        // Arrange: Setting up the mocked behaviors
        when(mockPlayer.getName()).thenReturn("Player1");
        doNothing().when(mockPlayer).incrementActions(1);

        // Act: Call the method that triggers the effect of Backlog
        actionCard.handleBacklog(mockPlayer);

        // Assert: Verify interactions
        verify(mockPlayer, times(1)).incrementActions(1);
        verify(mockGameEngine, times(1)).discardPhase(mockPlayer, false, 0, 0);
    }

    @Test
    public void testPlayActionCard_DailyScrum() throws PlayerViolationException {
        Card dailyScrumCard = new Card(Card.Type.DAILY_SCRUM, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));

        actionCard.playActionCard(dailyScrumCard, mockPlayer);

        verify(mockPlayer).draw(4);
        verify(mockPlayer).incrementBuys(1);
        verify(mockOpponent).draw(1);
    }

    @Test
    public void testPlayActionCard_Ipo() throws PlayerViolationException {
        Card ipoCard = new Card(Card.Type.IPO, 0);

        actionCard.playActionCard(ipoCard, mockPlayer);

        verify(mockPlayer).draw(2);
        verify(mockPlayer).incrementActions(1);
        verify(mockPlayer).incrementMoney(2);
    }

    @Test
    public void testPlayActionCard_Hack() throws PlayerViolationException {
        // Arrange: Mock player and opponent interactions
        when(mockPlayer.getName()).thenReturn("Player1");
        when(mockOpponent.getName()).thenReturn("Player2");
 
        // Mock getOpponents() to return the opponent
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Arrays.asList(mockOpponent));
 
        // Mock opponent's card types, assuming the opponent does not have a Monitoring card
        when(mockOpponent.getCards()).thenReturn(Arrays.asList(mock(Card.class))); // No Monitoring card here
 
        // Act: Play the Hack card
        actionCard.playActionCard(hackCard, mockPlayer);
 
        // Assert: Verify the opponent's discard phase was triggered
        verify(mockGameEngine, times(1)).discardPhase(mockOpponent, true, 3, 0);
 
        // Verify player money increment
        verify(mockPlayer, times(1)).incrementMoney(2);
    }

    @Test
    public void testPlayActionCard_Monitoring() throws PlayerViolationException {
        Card monitoringCard = new Card(Card.Type.MONITORING, 0);

        actionCard.playActionCard(monitoringCard, mockPlayer);

        verify(mockPlayer).draw(2);
    }

    @Test
    public void testPlayActionCard_TechDebt() throws PlayerViolationException {
        // Arrange: Setting up the mocked behaviors
        when(mockSupply.getEmptyPileCount()).thenReturn(2);
        when(mockPlayer.getName()).thenReturn("Player1");
        doNothing().when(mockPlayer).draw(1);
        doNothing().when(mockPlayer).incrementActions(1);
        doNothing().when(mockPlayer).incrementMoney(1);

        // Act: Call the method that triggers the effect of Tech Debt
        actionCard.handleTech_Debt(mockPlayer);

        // Assert: Verify interactions
        verify(mockPlayer, times(1)).draw(1);
        verify(mockPlayer, times(1)).incrementActions(1);
        verify(mockPlayer, times(1)).incrementMoney(1);
        verify(mockGameEngine, times(1)).discardPhase(mockPlayer, true, 0, 2);
    }

    @Test
    public void testPlayActionCard_Refactor() throws PlayerViolationException {

        Card refactorCard = new Card(Card.Type.REFACTOR, 0);
        Card trashedCard = new Card(Card.Type.BACKLOG, 3);
        assertEquals(2, trashedCard.getCost());

        when(mockPlayer.getLastTrashedCard()).thenReturn(trashedCard);

        actionCard.playActionCard(refactorCard, mockPlayer);

        verify(mockGameEngine).trashCard(mockPlayer);
        verify(mockGameEngine).gainCard(mockPlayer, null, 4);

    }

    @Test
    public void testPlayActionCard_Parallelization() throws PlayerViolationException {
        Card parallelizationCard = new Card(Card.Type.PARALLELIZATION, 0);

        actionCard.playActionCard(parallelizationCard, mockPlayer);

        verify(mockPlayer).incrementActions(1);
        assertTrue(actionCard.parallelizationHandle);
    }

    @Test
    public void testPlayActionCard_CodeReview() throws PlayerViolationException {
        Card codeReviewCard = new Card(Card.Type.CODE_REVIEW, 0);

        actionCard.playActionCard(codeReviewCard, mockPlayer);

        verify(mockPlayer).draw(1);
        verify(mockPlayer).incrementActions(2);
    }

    @Test
    public void testPlayActionCard_EvergreenTest() throws PlayerViolationException {
        Card evergreenTestCard = new Card(Card.Type.EVERGREEN_TEST, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));

        actionCard.playActionCard(evergreenTestCard, mockPlayer);

        verify(mockPlayer).draw(2);
        verify(mockGameEngine).gainCard(mockOpponent, new Card(Card.Type.BUG, 0), null);
    }

    @Test
    public void testPlayActionCard_AttackNegatedByMonitoring() throws PlayerViolationException {
        Card hackCard = new Card(Card.Type.HACK, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));
        when(mockOpponent.getCards()).thenReturn(Arrays.asList(new Card(Card.Type.MONITORING, 0)));
        when(mockGameEngine.reactionPhase(mockOpponent, mockPlayer, hackCard)).thenReturn(true);

        actionCard.playActionCard(hackCard, mockPlayer);

        verify(mockGameEngine).reactionPhase(mockOpponent, mockPlayer, hackCard);
        verify(mockPlayer, never()).incrementMoney(2);
    }
}