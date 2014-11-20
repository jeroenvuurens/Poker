package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jeroen
 */
public class testMatch {

    public static void main(String[] args) {

        ArrayList<Player> players = new ArrayList();
        players.add(new CallBot());
        players.add(new RandomBot());
        players.add(new RandomBot());
        players.add(new RandomBot());
        players.add(new RandomBot());

        Match match = new Match(players);
        while (match.getPlayersLeftInMatch().size() > 1) {
            System.out.println("New Game " + match.getPlayersLeftInMatch());

            Game game = match.playGame();
            printGameEvents(game);
            printGame(game);
            match.moveDealer();
        }

    }

    private static void printGame(Game game) {
        if (game.isShowDown()) {
            TreeMap<ComparableHand, Player> showDown = game.getShowDown();
            for (Map.Entry<ComparableHand, Player> entry : showDown.entrySet()) {
                Player player = entry.getValue();
                ComparableHand hand = entry.getKey();
                System.out.println("Player " + player.toString() + " had " + hand.toString());
            }
        }
    }
    
    private static void printGameEvents(Game game) {
        for (Event event : game.getEventHistory()) {
            System.out.println(event);
        }
    }
}
