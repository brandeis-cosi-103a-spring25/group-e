package edu.brandeis.cosi103a.groupe.Players;
// package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.cards.*;
import edu.brandeis.cosi103a.groupe.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupe.Deck;
import edu.brandeis.cosi103a.groupe.Supply;
import edu.brandeis.cosi103a.groupe.Cards.AutomationCard;
import edu.brandeis.cosi103a.groupe.Cards.CryptocurrencyCard;
/* 
 * This class creates a player in the game
 * 
 */
public abstract class ourPlayer implements Player{
    private String name, phase;
    private Deck deck;
    private List<Card> hand;
    private GameObserver observer;
    private List<Card> discardPile;
    private ImmutableCollection<Card> playedCards;
    private ImmutableCollection<Card> unplayedCards;
    private int money;
    public int actions = 1; // Tracks the number of actions the player has
    public int buys = 1; // Tracks the number of buys the player has

    /*
     * Constructor for the player
     * @param name The name of the player.
     */
    public ourPlayer(String name) {
        this.name = name;
        this.deck = new Deck();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.money = 0;
    }

    /**
     * Gets the name of the player.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Draws a hand of cards from the deck.
     * @param handSize The number of cards to draw.
     */
    public void drawHand(int handSize) {
        hand.clear();
        for (int i = 0; i < handSize; i++) {
            if (deck.isEmpty()) {
                reshuffleDeck();
            }
            hand.add(deck.drawCard());
        }

    }
    
    /**
     * Plays the hand of cards, adding the money from each card to the player's total.
     */
    public int playHand() {
       int playMoney = 0;
        for (Card card : hand) {
            if (card.getType() == Card.Type.BITCOIN) {
                playMoney += 1;
            }
            else if (card.getType() == Card.Type.ETHEREUM) {
                playMoney +=3;
            }
            else if (card.getType() == Card.Type.DOGECOIN) {
                playMoney += 5;
        }
    }
    return playMoney;
}

public void setHand(List<Card> hand) {
    this.hand = hand;
}
    
    /**
     * Purchases a card from the hand, adding it to the discard pile.
     * @param card The card to purchase.
     * @param supply The supply to take the card from.
     */
    public void purchaseCard(Card card, Supply supply) {
        int totalMoneyInHand = getMoney();
        if (totalMoneyInHand >= card.getCost() && supply.takeCard(card.getType())) {
            discardPile.add(card);
            money -= card.getCost();
        } 
    }


    public void setMoney(int val) {
        this.money = val;
    }
    /**
     * Cleans up the player's hand and discard pile.
     */
    public void cleanup() {
        discardPile.addAll(hand);
        hand.clear();
    }
    
    /**
     * Gets the total AP of the player's deck and discard pile.
     * @return The total AP.
     */
    public int getTotalAp() {
        int deckAp = deck.getTotalAp();
        int handAp = hand.stream()
                         .filter(card -> card.getType() == Card.Type.MODULE || card.getType() == Card.Type.METHOD || card.getType() == Card.Type.FRAMEWORK)   
                         .mapToInt(Card::getValue)
                         .sum();
        int discardPileAp = discardPile.stream()
        .filter(card -> card.getType() == Card.Type.MODULE || card.getType() == Card.Type.METHOD || card.getType() == Card.Type.FRAMEWORK)   
        .mapToInt(Card::getValue)
                                       .sum();
        return deckAp + handAp + discardPileAp;
    }
    
    /**
     * Gets the current amount of money the player has.
     * @return The current money.
     */
    public int getMoney() {
        // playHand();
        return money;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    /**
     * Adds a card to the deck.
     * @param card The card to add.
     */
    public void addCardToDeck(Card card) {
        deck.addCard(card);
    }
    
    /**
     * Shuffles the deck.
     */
    public void shuffleDeck() {
        deck.shuffle();
    }

    public void setPlayedCards(ImmutableList<Card> newUsedCards) {
        playedCards = newUsedCards;
    }

    public ImmutableCollection<Card> getPlayedCards() {
        return this.playedCards;
    }
    
    /**
     * Reshuffles the deck by adding the discard pile back to the deck and shuffling.
     */
    private void reshuffleDeck() {
        deck.addCards(discardPile);
        discardPile.clear();
        deck.shuffle();
    }

    public void setObserver(GameObserver observer) {
       this.observer = observer;
    }

    public Optional<GameObserver> getObserver() {
        return Optional.ofNullable(observer);
    }

     /**
     * Gets the current hand of cards.
     * @return The current hand of cards.
     */
    public Hand getHand() {
        return makeHand();
    }

    public List<Card> getCards() {
        return this.hand;
    }



    public Hand makeHand() {
       playedCards = ImmutableList.of();
       unplayedCards = ImmutableList.copyOf(hand);
       Hand thisHand = new Hand(playedCards, unplayedCards);
       return thisHand;
    }
    
    //not sure
    public void playCard(Card card) {
        // Implement the logic to play a card
        // For example, remove the card from hand and add it to the played cards
        hand.remove(card);
        discardPile.add(card);
    } 
    public void incrementActions(int amount) {
        actions += amount;
    }

    public void incrementBuys(int amount) {
        buys += amount;
    }

    public void incrementMoney(int amount) {
        money += amount;
    }

    public int discardAnyNumberOfCards() {
        int discarded = hand.size();
        discardPile.addAll(hand);
        hand.clear();
        return discarded;
    }

    public void draw(int count) {
        // Simulated draw logic
    }

    public void discardDownTo(int count) {
        while (hand.size() > count) {
            discardPile.add(hand.remove(0));
        }
    }

    public Card chooseActionCardToPlay() { 
        for (Card card : hand) {
            if (card.getCategory() == Card.Type.Category.ACTION) {
                return card;
            }
        }
        return null;
    }

    public Card trashCardFromHand() { //need to fix
        if (!hand.isEmpty()) {
            return hand.remove(0);
        }
        return null;
    }

    public Card gainCardUpToCost(int cost) { //need to fix
        return new Card(Card.Type.BITCOIN, cost);
    }

    public void gainCard(Card card) { //need to fix?
        discardPile.add(card);
    }

    public void discardCard() {
        if (!hand.isEmpty()) {
            discardPile.add(hand.remove(0));
        }
    }
    public int getHandSize() {
        return hand.size();
    }

    public int getActions() {
        return actions;
    }

    public int getBuys() {
        return buys;
    }
   
}
