package nl.hhs.poker;

/**
 *
 * @author jeroen
 */
public enum FACE {
      C2("2"),
      C3("3"),
      C4("4"),
      C5("5"),
      C6("6"),
      C7("7"),
      C8("8"),
      C9("9"),
      C10("10"),
      Jack("J"),
      Queen("Q"),
      King("K"),
      Ace("A");
      
      String label;
      
      @Override
      public String toString() {
          return label;
      }
      
      private FACE(String label) {
          this.label = label;
      }
}
