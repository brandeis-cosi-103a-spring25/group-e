package edu.brandeis.cosi103a.groupe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import edu.brandeis.cosi.atg.api.Hand;
import edu.brandeis.cosi.atg.api.cards.*;
/* 
 * This class creates a player in the game
 * 
 */
public class TempPlayer {
    private String name;
    private Deck deck;
    private List<Card> hand;
    //private Hand hand; 
    private List<Card> discardPile;
    private int money;

    /*
     * Constructor for the player
     * @param name The name of the player.
     */
    public TempPlayer(String name) {
        this.name = name;
        this.deck = new Deck();
        //this.hand = new Hand(ImmutableList.of(), ImmutableList.of()); //initialize empty hand
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
        //List<Card> playedCards = new ArrayList<>();
        //Collection<Card> unplayedCards = new ArrayList<Card>();
        hand.clear();
        for (int i = 0; i < handSize; i++) {
            if (deck.isEmpty()) {
                reshuffleDeck();
            }
            //unplayedCards.add(deck.drawCard());
            hand.add(deck.drawCard());
        }

    }
    
    /**
     * Plays the hand of cards, adding the money from each card to the player's total.
     */
    public void playHand() {
        money = 0;
        for (Card card : hand) {
            if (card instanceof CryptocurrencyCard) {
                money += card.getCost();
            }
        }
    }
    
    /**
     * Purchases a card from the hand, adding it to the discard pile.
     * @param card The card to purchase.
     * @param supply The supply to take the card from.
     */
    public void purchaseCard(Card card, Supply supply) {
        int totalMoneyInHand = getTotalMoneyInHand();
        if (totalMoneyInHand >= card.getCost() && supply.takeCard(card.getType())) {
            discardPile.add(card);
            money -= card.getCost();
        } 
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
                         .filter(card -> card instanceof AutomationCard)
                         .mapToInt(Card::getValue)
                         .sum();
        int discardPileAp = discardPile.stream()
                                       .filter(card -> card instanceof AutomationCard)
                                       .mapToInt(Card::getValue)
                                       .sum();
        return deckAp + handAp + discardPileAp;
    }
    
    /**
     * Gets the current amount of money the player has.
     * @return The current money.
     */
    public int getMoney() {
        return money;
    }

    /**
     * Gets the total money in the player's hand.
     * @return The total money in the hand.
     */
    public int getTotalMoneyInHand() {
        return hand.stream()
                   .filter(card -> card instanceof CryptocurrencyCard)
                   .mapToInt(Card::getId)
                   .sum();
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
    
    /**
     * Reshuffles the deck by adding the discard pile back to the deck and shuffling.
     */
    private void reshuffleDeck() {
        deck.addCards(discardPile);
        discardPile.clear();
        deck.shuffle();
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
        ImmutableCollection<Card> playedCards = ImmutableList.of();
        ImmutableCollection<Card> unplayedCards = ImmutableList.copyOf(hand);
        Hand thisHand = new Hand(playedCards, unplayedCards);
        return thisHand;
    }   
}
