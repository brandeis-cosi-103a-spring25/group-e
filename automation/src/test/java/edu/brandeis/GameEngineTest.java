package edu.brandeis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.brandeis.cosi.atg.api.GameDeck;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.cards.Card;
import edu.brandeis.cosi103a.groupe.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.GameEngine;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
import edu.brandeis.cosi103a.groupe.Cards.CryptocurrencyCard;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

public class GameEngineTest {
    @Test
    public void testDistributeCards() {
        Supply mockSupply = mock(Supply.class);
        ourPlayer mockPlayer1 = mock(ourPlayer.class);
        ourPlayer mockPlayer2 = mock(ourPlayer.class);
        AutomationCard methodCard = new AutomationCard(Card.Type.METHOD, 1);
        CryptocurrencyCard bitcoinCard = new CryptocurrencyCard(Card.Type.BITCOIN, 31);
        
        GameEngine engine = new GameEngine(mockPlayer1, mockPlayer2, new ConsoleGameObserver());
    
       // Test card distribution logic
        engine.distributeCards(mockPlayer1, mockPlayer2, mockSupply, methodCard, bitcoinCard);

        verify(mockPlayer1, times(7)).addCardToDeck(any(CryptocurrencyCard.class));
        verify(mockPlayer2, times(7)).addCardToDeck(any(CryptocurrencyCard.class));
        verify(mockSupply, times(14)).takeCard(Card.Type.BITCOIN); // 7 cards for each player

        verify(mockPlayer1, times(3)).addCardToDeck(any(AutomationCard.class));
        verify(mockPlayer2, times(3)).addCardToDeck(any(AutomationCard.class));
        verify(mockSupply, times(6)).takeCard(Card.Type.METHOD); // 3 cards for each player
    }
}
