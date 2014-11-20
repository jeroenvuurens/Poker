package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jeroen
 */
public class testGame {
   
    public static void main(String[] args) {
        
        ArrayList<Player> players = new ArrayList();
        players.add(new CallBot());
        players.add(new RandomBot());
        
        Game game = new Game(players);
        game.dealCommunityCards(5);
        game.bidUntilNoRaise();
        
        for (Player player : players) {
            EVENTTYPE event = player.getPlayerLastAction();
            int amount = player.getPlayerBidLevel();
            System.out.println("Player " + player + " " + event + " on amount " + amount);
        }
        
        TreeMap<ComparableHand, Player> showdown = game.getShowDown();
        for (Map.Entry<ComparableHand, Player> entry : showdown.entrySet()) {
            ComparableHand hand = entry.getKey();
            System.out.println(hand);
        }
        System.out.println("Winner " + game.getWinner());
        
    }

}
