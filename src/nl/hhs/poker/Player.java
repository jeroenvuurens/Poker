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
   private int cash = startAmount;
   
   protected Game game;
   private Hand hand; // private so no other players can see the players hand
    
   public Player() {
   }
   
   /**
    * Used by game to deal a hand.
    */
   public final void setHand(Game game, Hand hand) {
       this.game = game;
       this.hand = hand;
   }
   
   /**
    * only returns Hand when the game indicates it must be shown for showdown 
    */
   public final Hand giveHand() {
       if (game.isShowDown(this))
           return hand;
       return null;
   }
   
   public int getPlayerBidLevel() {
       Event lastEvent = game.getLastEvent(this);
       return lastEvent == null?0:lastEvent.bidLevel;
   }
   
   public EVENTTYPE getPlayerLastAction() {
       Event lastEvent = game.getLastEvent(this);
       return lastEvent == null?null:lastEvent.type;
   }
   
   public final int cashOwned() {
       return cash;
   }
   
   public final boolean isBankrupt() {
       return cash < 1;
   }
   
   public final void transferCash(CashTransfer voucher) {
       if (voucher.player == this)
           cash += voucher.amount;
   }
   
   /**
    * Used by game to request a player to bid. gameBidLevel indicates the maximum amount
    * put in by a player, to stay in the game, this must be met or go all in.
    */
   public final int raise(int gameBidLevel) {
       return raise(gameBidLevel, hand);
   }
   
   /**
    * Implemented by player to bid. gameBidLevel indicates the maximum amount
    * put in by a player, to stay in the game, this must be met or go all in.
    * The secret Hand is passed only for the player to see.
    */
   protected abstract int raise(int gameBidLevel, Hand hand);
   
   public abstract String name();
   
   @Override
   public String toString() {
       return name() + id;
   }
}
