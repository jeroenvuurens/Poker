//class represents java application
package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import nl.hhs.poker.Card;
import nl.hhs.poker.Card;

public class Match {
    // An ArrayDeque is used to easily rotate the button (Player to bet first) between games
    private ArrayList<Player> playersLeftInGame;
    
    public Match(ArrayList<Player> players) throws ClassNotFoundException {
        playersLeftInGame = new ArrayList(players);
    }
    
    public void playGame() throws ClassNotFoundException {
        Game game = new Game(playersLeftInGame);

        // first round of bidding without community cards
        game.bidUntilNoRaise();
        int lastEventPrinted = printGame(game, 0);

        // the Turn: 3 community cards are shown
        game.dealCommunityCards(3);
        printCommunityCards(game.getCommunityCards());
        game.bidUntilNoRaise();
        lastEventPrinted = printGame(game, lastEventPrinted);

        // the Flop: a fourth community card is shown
        game.dealCommunityCards(4);
        printCommunityCards(game.getCommunityCards());
        game.bidUntilNoRaise();
        lastEventPrinted = printGame(game, lastEventPrinted);

        // the River: a fifth community card is shown
        game.dealCommunityCards(5);
        printCommunityCards(game.getCommunityCards());
        game.bidUntilNoRaise();
        
        TreeMap<ComparableHand, Player> showdown = game.getShowDown();
        if (game.isShowDown()) {
            for (Map.Entry<ComparableHand, Player> entry : showdown.entrySet()) {
                printComparableHand(entry.getValue(), entry.getKey());
            }
        }
        lastEventPrinted = printGame(game, lastEventPrinted);
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
    
    private void printComparableHand(Player player, ComparableHand hand) {
        System.out.println("Player " + player.toString() + " had " + hand.toString());
    }
    
    private int printGame(Game game, int lastposition) {
        ArrayList<Event> eventHistory = game.getEventHistory();
        for (int i = lastposition; i < eventHistory.size(); i++) {
            Event event = eventHistory.get(i);
            System.out.println(event);
        }
        return eventHistory.size();
    }
    
    private void printCommunityCards(ArrayList<Card> communityCards) {
        for (Card card : communityCards) {
            System.out.println("on table " + card);
        }
    }   
}