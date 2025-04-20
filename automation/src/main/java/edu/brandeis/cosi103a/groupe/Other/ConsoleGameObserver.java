package edu.brandeis.cosi103a.groupe.Other;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.event.Event;


public class ConsoleGameObserver implements GameObserver {

      /*
       * This method is called whenever an event occurs in the game and is 
       * responsible for printing the event details to the console.
       * @param state The current game state.
       * @param event The event that occurred.
       */
      public void notifyEvent(GameState state, Event event) {
        if (state == null || event == null) {
            System.out.println("Event occurred: null at game state null");
            return;
        }
        // Get event details using getDescription() from Event interface
        String eventDescription = event.getDescription();

        // Get the current player's name from the GameState
        String currentPlayerName = state.getCurrentPlayerName();

        // Get the available actions, buys, and spendable money from the GameState
        int availableActions = state.getAvailableActions();
        int availableBuys = state.getAvailableBuys();
        int spendableMoney = state.getSpendableMoney();

        // Get the turn phase from GameState
        GameState.TurnPhase turnPhase = state.getTurnPhase();

         // Format the output to be more descriptive and user-friendly
        System.out.println("Event occurred: " + eventDescription + 
            " at game state (Player: " + currentPlayerName + 
            ", Phase: " + turnPhase + 
            ", Actions: " + availableActions + 
            ", Buys: " + availableBuys + 
            ", Spendable Money: " + spendableMoney + ")");
        //System.out.println("Event occurred: " + event + " at game state " + state + "\n");
    }
}
