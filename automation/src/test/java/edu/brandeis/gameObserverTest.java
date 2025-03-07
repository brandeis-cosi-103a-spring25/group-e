package edu.brandeis;

import org.junit.Test;
import static org.mockito.Mockito.*;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi103a.groupe.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.Player;
import edu.brandeis.cosi.atg.api.event.Event;

import org.mockito.Mock;
import org.mockito.Mockito;

public class gameObserverTest {

    Mockito mockito = new Mockito();
    @Test
    public void testGameObserver() {
        // Create a new game observer
        ConsoleGameObserver observer = new ConsoleGameObserver();
        
        // Create a new player
        
        GameState gameState = mock(GameState.class);

        Event event =  mock(Event.class);
        
        // Notify the observer of a game event
        observer.notifyEvent(gameState, event);
        
        // Check if the player is in the observer
    
    }
}
