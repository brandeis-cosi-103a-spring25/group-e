package edu.brandeis.cosi103a.groupe;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.event.Event;


public class GameObserver {
      public void notifyEvent(GameState state, Event event) {
        System.out.println("Event occurred: " + event + " at game state " + state + "\n");
    }
}
