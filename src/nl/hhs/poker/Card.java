package nl.hhs.poker;

public final class Card {

    private FACE face;
    private SUIT suit;

    public Card(FACE face, SUIT suit) {
        this.face = face;
        this.suit = suit;
    }

    public FACE getFace() {
        return face;
    }

    public SUIT getSuit() {
        return suit;
    }
    
    @Override
    public String toString() {
        return suit.toString() + face.toString();
    }
}