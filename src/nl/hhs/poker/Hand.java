package nl.hhs.poker;

import java.util.ArrayList;

/**
 * A private Hand of 2 cards, seen only by the player that holds them.
 * @author jeroen
 */
public final class Hand {

    private final static int MAX_CARD = 2;
    private final Deck deck;
    private final Card cards[];

    protected Hand(Deck deck) {
        this.deck = deck;
        cards = new Card[MAX_CARD];
        for (int i = 0; i < MAX_CARD; i++) {
            cards[i] = deck.dealCard();
        }
    }

    /**
     * @return a copy of the cards, to avoid tempering with cards
     */
    public Card[] getCards() {
        Card clone[] = new Card[cards.length];
        System.arraycopy(cards, 0, clone, 0, MAX_CARD);
        return clone;
    }

    private Deck getDeck() {
        return deck;
    }
    
    /**
     * @param hand
     * @return true if both hands were dealt from the same Deck, used to validate
     * if no one cheated by using a different Deck.
     */
    public boolean sameDeck(Hand hand) {
        return deck == hand.getDeck();
    }
}
