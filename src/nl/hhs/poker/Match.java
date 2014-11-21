//class represents java application
package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Iterator;

public class Match {
    // An ArrayDeque is used to easily rotate the button (Player to bet first) between games
    private ArrayList<Player> playersLeftInGame;
    
    public Match(ArrayList<Player> players) {
        playersLeftInGame = new ArrayList(players);
    }
    
    public ArrayList<Player> getPlayersLeftInMatch() {
        return (ArrayList)playersLeftInGame.clone();
    }
    
    public Game playGame() {
        Game game = new Game(playersLeftInGame);

        // first round of bidding without community cards
        game.bidUntilNoRaise();

        // the Turn: 3 community cards are shown
        game.dealCommunityCards(3);
        game.bidUntilNoRaise();

        // the Flop: a fourth community card is shown
        game.dealCommunityCards(4);
        game.bidUntilNoRaise();

        // the River: a fifth community card is shown
        game.dealCommunityCards(5);
        game.bidUntilNoRaise();
        
        game.getShowDown();
        return game;
    }
    
    /**
     * Remove players that have no money left, and move the dealer to the next
     * player in line.
     */
    public void moveDealer() {
        Player first = playersLeftInGame.get(0);
        Iterator<Player> iterator = playersLeftInGame.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.isBankrupt())
                iterator.remove();
        }
        // if the dealer (first player) is still in the game, remove this player
        // and put him back as last player, the second player is now the new dealer.
        if (playersLeftInGame.get(0) == first) {
            playersLeftInGame.remove(0);
            playersLeftInGame.add(first);
        }
    }
}