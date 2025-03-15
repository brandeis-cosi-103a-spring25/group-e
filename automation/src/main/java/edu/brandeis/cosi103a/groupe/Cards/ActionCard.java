package edu.brandeis.cosi103a.groupe.Cards;

import java.util.ArrayList;
import java.util.List;

import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.Card;

public class ActionCard {
    
     // Play an action card
    public void playActionCard(Card card, Player player) {
        switch (card.getType()) {
            case BACKLOG:
                handleBacklog(player);
                break;
            case DAILY_SCRUM:
                handleDailyScrum(player);
                break;
            case IPO:
                handleIpo(player);
                break;
            case HACK:
                handleHack(player);
                break;
            case MONITORING:
                handleMonitoring(player);
                break;
            case TECH_DEBT:
                handleTech_Debt(player);
                break;
            case REFACTOR:
                handleRefactor(player);
                break;
            case PARALLELIZATION:
                handleParallelization(player);
                break;
            case CODE_REVIEW:
                handleCode_Review(player);
                break;
            case EVERGREEN_TEST:
                handleEvergreen_Test(player);
                break;
            default:
                throw new UnsupportedOperationException("Action card not supported.");
        }
    }
    
    // Effect: Player discards any number of cards, then draws the same amount
    private void handleBacklog(Player player) {
        
    }

    private void handleDailyScrum(Player player){

    }

    private void handleIpo(Player player){

    }

    private void handleHack(Player player){

    }

    private void handleMonitoring(Player player){

    }

    private void handleTech_Debt(Player player){

    }
    
    private void handleRefactor(Player player){
        
    }

    private void handleParallelization(Player player){
        
    }

    private void handleCode_Review(Player player){
        
    }

    private void handleEvergreen_Test(Player player){
        
    }
}
