package edu.brandeis.cosi103a.groupe;

import static org.mockito.Mockito.*;

import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
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

    @Before
    public void setUp() {
        mockSupply = mock(Supply.class);
        mockGameEngine = mock(GameEngine.class);
        mockPlayer = mock(ourPlayer.class);
        mockOpponent = mock(ourPlayer.class);
        actionCard = new ActionCard(mockSupply, mockGameEngine);
    }

    @Test
    public void testPlayActionCard_Backlog() {
        Card backlogCard = new Card(Card.Type.BACKLOG, 0);
        when(mockPlayer.discardAnyNumberOfCards()).thenReturn(2);

        actionCard.playActionCard(backlogCard, mockPlayer);

        verify(mockPlayer).incrementActions(1);
        verify(mockPlayer).discardAnyNumberOfCards();
        verify(mockPlayer).draw(2);
    }

    @Test
    public void testPlayActionCard_DailyScrum() {
        Card dailyScrumCard = new Card(Card.Type.DAILY_SCRUM, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));

        actionCard.playActionCard(dailyScrumCard, mockPlayer);

        verify(mockPlayer).draw(4);
        verify(mockPlayer).incrementBuys(1);
        verify(mockOpponent).draw(1);
    }

    @Test
    public void testPlayActionCard_Ipo() {
        Card ipoCard = new Card(Card.Type.IPO, 0);

        actionCard.playActionCard(ipoCard, mockPlayer);

        verify(mockPlayer).draw(2);
        verify(mockPlayer).incrementActions(1);
        verify(mockPlayer).incrementMoney(2);
    }

    @Test
    public void testPlayActionCard_Hack() {
        Card hackCard = new Card(Card.Type.HACK, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));

        actionCard.playActionCard(hackCard, mockPlayer);

        verify(mockPlayer).incrementMoney(2);
        verify(mockOpponent).discardDownTo(3);
    }

    @Test
    public void testPlayActionCard_Monitoring() {
        Card monitoringCard = new Card(Card.Type.MONITORING, 0);

        actionCard.playActionCard(monitoringCard, mockPlayer);

        verify(mockPlayer).draw(2);
    }

    @Test
    public void testPlayActionCard_TechDebt() {
        Card techDebtCard = new Card(Card.Type.TECH_DEBT, 0);
        when(mockSupply.getEmptyPileCount()).thenReturn(2);
        when(mockPlayer.getHandSize()).thenReturn(5);

        actionCard.playActionCard(techDebtCard, mockPlayer);

        verify(mockPlayer).draw(1);
        verify(mockPlayer).incrementActions(1);
        verify(mockPlayer).incrementMoney(1);
        verify(mockPlayer, times(2)).discardDownTo(4);
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
    public void testPlayActionCard_Parallelization() {
        Card parallelizationCard = new Card(Card.Type.PARALLELIZATION, 0);

        actionCard.playActionCard(parallelizationCard, mockPlayer);

        verify(mockPlayer).incrementActions(1);
        assertTrue(actionCard.parallelizationHandle);
    }

    @Test
    public void testPlayActionCard_CodeReview() {
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
    public void testPlayActionCard_AttackNegatedByMonitoring() {
        Card hackCard = new Card(Card.Type.HACK, 0);
        when(mockGameEngine.getOpponents(mockPlayer)).thenReturn(Collections.singletonList(mockOpponent));
        when(mockOpponent.getCards()).thenReturn(Arrays.asList(new Card(Card.Type.MONITORING, 0)));
        when(mockGameEngine.reactionPhase(mockOpponent, mockPlayer, hackCard)).thenReturn(true);

        actionCard.playActionCard(hackCard, mockPlayer);

        verify(mockGameEngine).reactionPhase(mockOpponent, mockPlayer, hackCard);
        verify(mockPlayer, never()).incrementMoney(2);
    }
}