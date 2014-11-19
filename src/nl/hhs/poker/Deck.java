package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Random;

public final class Deck {    
    private final Card cards[];
    private static final Random randNum = new Random();
    private int nextCard = 0;

    public Deck() {
        ArrayList<Card> newdeck = new ArrayList();
        for (SUIT suit : SUIT.values()) {
            for (FACE face : FACE.values())
               newdeck.add(new Card(face, suit));
        }
        cards = newdeck.toArray(new Card[0]);
        shuffle();
    }

    private void shuffle() {
        for (int i = 0; i < cards.length; i++) {
            int j = randNum.nextInt(cards.length);
            Card c = cards[i];
            cards[i] = cards[j];
            cards[j] = c;
        }
    }
    
    public Card dealCard() {
        return cards[nextCard++];
    }
}
