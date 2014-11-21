//class to represent a player
package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class ComparableHand implements Comparable<ComparableHand> {

    private final static int MAX_CARD = 7;
    private final Hand hand;
    private RankHand rankhand;
    private final Card cards[];

    protected ComparableHand(Game game, Hand hand) {
        this.hand = hand;
        ArrayList<Card> cards = new ArrayList(game.getCommunityCards());
        for (Card card : hand.getCards()) {
            cards.add(card);
        }
        Collections.sort(cards, comparator);
        this.cards = cards.toArray(new Card[0]);
    }

    public Card[] getCards() {
        Card clone[] = new Card[cards.length];
        System.arraycopy(cards, 0, clone, 0, MAX_CARD);
        return clone;
    }

    private Hand getHand() {
       return hand;
    }
        
    @Override
    public int compareTo(ComparableHand o) {
        if (!(o instanceof ComparableHand)) {
            throw new RuntimeException("Cannot compare illegal hands");
        }
        ComparableHand h = (ComparableHand) o;
        if (!hand.sameDeck(h.getHand())) {
            throw new RuntimeException("Cannot compare hands from different decks");
        }
        int i = h.getRankHand().rank().ordinal() - getRankHand().rank().ordinal();
        if (i == 0) {
            i = getRankHand().compareTo(h.getRankHand());
        }
        return i;
    }

    private RankHand getRankHand() {
        if (rankhand == null) {
            rankhand = getRank();
        }
        return rankhand;
    }

    private static enum RANK {

        HIGHCARD("High Card"),
        ONEPAIR("One Pair"),
        TWOPAIR("Two Pair"),
        THREEOFAKIND("Three of a Kind"),
        STRAIGHT("Straight"),
        FLUSH("Flush"),
        FULLHOUSE("Full House"),
        FOUROFAKIND("Four of a Kind"),
        STRAIGHTFLUSH("Straight Flush");

        String label;

        public String toString() {
            return label;
        }

        RANK(String label) {
            this.label = label;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append(" ");
        }
        return sb.append(getRankHand().toString()).toString();
    }

    private static CardComparator comparator = new CardComparator();

    private static class CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card o1, Card o2) {
            int c = o2.getFace().compareTo(o1.getFace());
            if (c == 0) {
                c = o2.getSuit().compareTo(o1.getSuit());
            }
            return c;
        }
    }

    interface RankHand extends Comparable<RankHand> {

        public RANK rank();
    }

    class RankCard1 implements RankHand {

        RANK rank;
        FACE face;

        public RankCard1(RANK rank, FACE face) {
            this.rank = rank;
            this.face = face;
        }

        @Override
        public RANK rank() {
            return rank;
        }

        @Override
        public int compareTo(RankHand o) {
            RankCard1 otherHand = (RankCard1) o;
            return otherHand.face.compareTo(face);
        }

        public String toString() {
            return rank + " from " + face;
        }
    }

    class FullHouse implements RankHand {

        FACE face;
        FACE face2;

        public FullHouse(FACE face, FACE face2) {
            this.face = face;
            this.face2 = face2;
        }

        @Override
        public RANK rank() {
            return RANK.FULLHOUSE;
        }

        @Override
        public int compareTo(RankHand o) {
            FullHouse otherHand = (FullHouse) o;
            int i = otherHand.face.compareTo(face);
            if (i == 0) {
                i = otherHand.face2.compareTo(face2);
            }
            return i;
        }

        @Override
        public String toString() {
            return RANK.FULLHOUSE + " " + face + " " + face2;
        }
    }

    class RankCard3 implements RankHand {

        RANK rank;
        FACE face;
        FACE face2;
        FACE face3;

        public RankCard3(RANK rank, FACE face, FACE face2, FACE face3) {
            this.rank = rank;
            this.face = face;
            this.face2 = face2;
            this.face3 = face3;
        }

        @Override
        public RANK rank() {
            return rank;
        }

        @Override
        public int compareTo(RankHand o) {
            RankCard3 otherHand = (RankCard3) o;

            int i = otherHand.face.compareTo(face);
            if (i == 0) {
                i = otherHand.face2.compareTo(face2);
                if (i == 0) {
                    i = otherHand.face3.compareTo(face3);
                }
            }
            return i;
        }

        @Override
        public String toString() {
            return rank + " " + face + " " + face2 + " " + face3;
        }
    }

    class OnePair implements RankHand {

        FACE face;
        FACE face2;
        FACE face3;
        FACE face4;

        public OnePair(FACE face, FACE face2, FACE face3, FACE face4) {
            this.face = face;
            this.face2 = face2;
            this.face3 = face3;
            this.face4 = face4;
        }

        @Override
        public RANK rank() {
            return RANK.ONEPAIR;
        }

        @Override
        public int compareTo(RankHand o) {
            OnePair otherHand = (OnePair) o;
            int i = otherHand.face.compareTo(face);
            if (i == 0) {
                i = otherHand.face2.compareTo(face2);
                if (i == 0) {
                    i = otherHand.face3.compareTo(face3);
                    if (i == 0) {
                        i = otherHand.face4.compareTo(face4);
                    }
                }
            }
            return i;
        }

        @Override
        public String toString() {
            return RANK.ONEPAIR + " " + face + " " + face2 + " " + face3 + " " + face4;

        }
    }

    class HighCard implements RankHand {

        Card rankedcards[];

        public HighCard(Card rankedcards[]) {
            this.rankedcards = rankedcards;
        }

        @Override
        public RANK rank() {
            return RANK.HIGHCARD;
        }

        @Override
        public int compareTo(RankHand o) {
            HighCard otherHand = (HighCard) o;
            for (int i = 0; i < 5; i++) {
                int difference = otherHand.rankedcards[i].getFace().compareTo(cards[i].getFace());
                if (difference != 0) {
                    return difference;
                }
            }
            return 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append(RANK.HIGHCARD);
            for (Card c : rankedcards) {
                sb.append(" ").append(c.getFace());
            }
            return sb.toString();
        }
    }

    private RankHand getRank() {
        ArrayList<RankHand> matches = new ArrayList();
        Arrays.sort(cards, comparator);

        TreeMap<FACE, ArrayList<Card>> groupByFace = new TreeMap();
        Card current = cards[0];
        for (Card card : cards) {
            FACE faceValue = card.getFace();
            ArrayList<Card> listForFace = groupByFace.get(faceValue);
            if (listForFace == null) {
                listForFace = new ArrayList();
                groupByFace.put(faceValue, listForFace);
            }
            listForFace.add(card);
        }

        Card flush = flush(cards);
        Card straight = (groupByFace.size() >= 5) ? straight(cards) : null;
        if (straight != null) {
            if (flush != null) {
                Card straightflush = straightflush(cards, flush.getSuit());
                if (straightflush != null) {
                    return new RankCard1(RANK.STRAIGHTFLUSH, straightflush.getFace());
                }
            }
        }
        if (groupByFace.size() == 7) {
            if (flush != null) {
                return new RankCard1(RANK.FLUSH, flush.getFace());
            }
            if (straight != null) {
                return new RankCard1(RANK.STRAIGHT, straight.getFace());
            }
            return new HighCard(cards);
        }

        ArrayList<FACE> highcards = new ArrayList();
        ArrayList<FACE> pairs = new ArrayList();
        ArrayList<FACE> threeofakind = new ArrayList();
        for (Map.Entry<FACE, ArrayList<Card>> entry : groupByFace.descendingMap().entrySet()) {
            FACE faceValue = entry.getKey();
            ArrayList<Card> listForFace = entry.getValue();
            int count = listForFace.size();
            switch (count) {
                case 1:
                    highcards.add(faceValue);
                    break;
                case 2:
                    pairs.add(faceValue);
                    break;
                case 3:
                    threeofakind.add(faceValue);
                    break;
                case 4:
                    return new RankCard1(RANK.FOUROFAKIND, faceValue);
            }
        }
        RankHand fullhouse = fullHouse(threeofakind, pairs);
        if (fullhouse != null) {
            return fullhouse;
        }
        if (flush != null) {
            return new RankCard1(RANK.FLUSH, flush.getFace());
        }
        if (straight != null) {
            return new RankCard1(RANK.STRAIGHT, straight.getFace());
        }
        if (threeofakind.size() > 0) {
            return new RankCard3(RANK.THREEOFAKIND, threeofakind.get(0), highcards.get(0), highcards.get(1));
        }
        if (pairs.size() > 1) {
            return new RankCard3(RANK.TWOPAIR, pairs.get(0), pairs.get(1), highcards.get(0));
        }
        return new OnePair(pairs.get(0), highcards.get(0), highcards.get(1), highcards.get(2));
    }

    protected RankHand fullHouse(ArrayList<FACE> threeofakind, ArrayList<FACE> pair) {
        if (threeofakind.size() > 0 && (pair.size() > 0 || threeofakind.size() > 1)) {
            FACE three = threeofakind.get(0);
            FACE two = threeofakind.size() > 1 ? threeofakind.get(1) : pair.get(0);
            return new FullHouse(three, two);
        }
        return null;
    }

    private Card flush(Card[] cards) {
        HashMap<SUIT, Integer> persuit = new HashMap();
        for (int i = 0; i < cards.length; i++) {
            SUIT suit = cards[i].getSuit();
            Integer count = persuit.get(suit);
            if (count == null) {
                count = 0;
            }
            if (count == 4) {
                for (Card card : cards)
                    if (card.getSuit() == suit)
                        return card;
            }
            persuit.put(suit, count + 1);
        }
        return null;
    }

    private Card straightflush(Card sortedcards[], SUIT suit) {
        int i = cards.length - 1;
        for (; i >= 0 && cards[i].getSuit() != suit; i--);
        FACE previousface = cards[i].getFace();
        Card high = cards[i];
        for (i--; i >= 0; i--) {
            if (cards[i].getSuit() == suit) {
                FACE currentface = cards[i].getFace();
                if (currentface.ordinal() == previousface.ordinal() - 1) {
                    if (currentface.ordinal() == high.getFace().ordinal() - 4) {
                        return high;
                    }
                    previousface = currentface;
                } else if (currentface != previousface) {
                    if (i > 3 || (currentface == FACE.C5 && i > 2)) {
                        previousface = currentface;
                        high = cards[i];
                    } else {
                        return null;
                    }
                }
            }
        }
        if (previousface == FACE.C2
                && cards[cards.length - 1].getFace() == FACE.Ace
                && cards[cards.length - 1].getSuit() == suit
                && high.getFace() == FACE.C5) {
            return high;
        }
        return null;
    }

    private Card straight(Card rankedCards[]) {
        FACE previousface = rankedCards[rankedCards.length - 1].getFace();
        Card high = rankedCards[rankedCards.length - 1];
        for (int i = rankedCards.length - 2; i >= 0; i--) {
            FACE currentface = rankedCards[i].getFace();
            if (currentface.ordinal() == previousface.ordinal() - 1) {
                if (currentface.ordinal() == high.getFace().ordinal() - 4) {
                    return high;
                }
                previousface = currentface;
            } else if (currentface != previousface) {
                if (i > 3 || (currentface == FACE.C5 && i > 2)) {
                    previousface = currentface;
                    high = rankedCards[i];
                } else {
                    return null;
                }
            }
        }
        if (previousface == FACE.C2
                && rankedCards[rankedCards.length - 1].getFace() == FACE.Ace
                && high.getFace() == FACE.C5) {
            return high;
        }
        return null;
    }

}
