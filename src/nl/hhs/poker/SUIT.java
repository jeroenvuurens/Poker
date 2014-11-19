package nl.hhs.poker;

/**
 *
 * @author jeroen
 */
public enum SUIT {
       Hearts("♥"),
       Diamonds("♦"),
       Clubs("♣"),
       Spades("♠");
       
       String label;

       @Override
       public String toString() {
           return label;
       }
       
       private SUIT(String label) {
           this.label = label;
       }
}
