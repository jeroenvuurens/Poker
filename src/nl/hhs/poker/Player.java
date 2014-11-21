package nl.hhs.poker;

import nl.hhs.poker.Game.CashTransfer;

/**
 * A Player participates in a Poker Game, it receives a Hand of two Cards in a Game
 * and is requested to {@link #bid(int, org.game.cards.impl.Hand)} in that Game.
 * The Player can query the Game object to learn about the other players, Event
 * history (who folded, raised, etc.), all players last bet, etc.
 * @author jeroen
 */
public abstract class Player {
   // unique id is used to distinguish between multiple players of the same class
   private static final int startAmount = 10000;
   protected static int playerid = 0; 
   private final int id = playerid++;
   private int bidround;
   private int cash = startAmount;
   
   protected Game game; // WARNING, may set to private, use getGame() instead
   private Hand hand; // private so no other players can see the players hand
    
   public Player() {
   }
   
   /**
    * Used by game to deal a hand.
    */
   public final void setHand(Game game, Hand hand) {
       this.game = game;
       this.hand = hand;
       this.bidround = 0;
   }
   
   /**
    * @return only returns Hand when the game indicates it must be shown for showdown 
    */
   public final Hand giveHand() {
       if (game.isShowDown(this))
           return hand;
       return null;
   }
   
   /**
    * @return current Game object
    */
   public final Game getGame() {
       return game;
   }
   
   /**
    * @return total amount this player has bet in the current game 
    */
   public int getPlayerBidLevel() {
       Event lastEvent = game.getLastEvent(this);
       return lastEvent == null?0:lastEvent.bidLevel;
   }
   
   /**
    * @return last EVENTTYPE of this player in the current game or null
    * if the player has no Event in the current game yet.
    */
   public EVENTTYPE getPlayerLastAction() {
       Event lastEvent = game.getLastEvent(this);
       return lastEvent == null?null:lastEvent.type;
   }
   
   /**
    * @return cash owned by player, the amount that the player has bet in the
    * current game has not been subtracted yet (money is payed when game is finished)
    */
   public final int chipsOwned() {
       return cash;
   }
   
   /**
    * @return true if the player has no chips left 
    */
   public final boolean isBankrupt() {
       return cash < 1;
   }
   
   /**
    * Used internally to let losing players pay bet chips to winning players. Should
    * never be used by others.
    */
   public final void transferCash(CashTransfer voucher) {
       if (voucher.player == this)
           cash += voucher.amount;
   }
   
   /**
    * Used by game to request a player to bid. gameBidLevel indicates the maximum amount
    * put in by a player, to stay in the game, this must be met or go all in.
    */
   public final int raise(int gameBidLevel) {
       int raise = raise(gameBidLevel, hand);
       bidround++;
       return raise;
   }
   
   /**
    * @return the number of times the player has made a bid in this game
    */
   protected int getRound() {
       return bidround;
   }
   
   /**
    * Implemented by player to bid. gameBidLevel indicates the maximum amount
    * put in by a player, to stay in the game, this must be met or go all in.
    * The secret Hand is passed only for the player to see.
    */
   protected abstract int raise(int gameBidLevel, Hand hand);
   
   /**
    * @return name of the player 
    */
   public abstract String name();
   
   @Override
   public String toString() {
       return name() + id;
   }
}
