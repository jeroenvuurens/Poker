package nl.hhs.poker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * All players are dealt 2 cards they hold in hand and how to noone. For a true
 * game of Texas Holdem, the first round of bidding is with no 'community cards'
 * (i.e. cards in the middle open on table that can be used by all players to
 * create the best possible 5 card hand). Then 3 community cards are dealt (the
 * Turn), followed by another round of bidding, then one more community card is
 * dealt (the Flop), followed by another round of bidding, and then a final of 5
 * community cards is dealt (the River), followed by a last round of bidding. If
 * the last raiser was not called, the last raiser wins, otherwise the players
 * that compete for the prize money show there cards and the best 5 card
 * combination that any player can make using the 2 cards they hold with the
 * five cards on the table wins. In rare situations a tie gives multiple winners
 * who split the prize money.
 * <p/>
 *
 */
public final class Game {

    private static final int COMMUNITY_CARDS = 5;
    private final Deck deck;
    private TreeMap<ComparableHand, Player> showdown;
    private final ArrayList<Card> communitycards = new ArrayList();
    private final ArrayList<Player> playersLeftInGame;
    private final HashMap<Player, Event> lastEvent = new HashMap();
    private final ArrayList<Event> eventHistory = new ArrayList();
    private int blind;
    private Player bigblind;
    private Player smallblind;
    private boolean isshowdown = false;

    public Game(Collection<Player> players) {
        deck = new Deck();
        playersLeftInGame = new ArrayList(players);
        setBlinds();
        deal();
    }

    /**
     * Deal each player a Hand of cards
     */
    private void deal() {
        for (Player player : playersLeftInGame) {
            Hand hand = new Hand(deck);
            player.setHand(this, hand);
        }
    }

    /**
     * @return List of all players
     */
    public ArrayList<Player> getPlayers() {
        return new ArrayList(lastEvent.keySet());
    }

    /**
     * Add an event to history, and sets it as lastevent for the player. The
     * events contain information on bets made, money put in by binds, player
     * status (e.g. fold, all-in), and when the game is finished the winner(s).
     */
    private final void addEvent(Player player, EVENTTYPE type, int total) {
        Event event = new Event(player, type, total);
        eventHistory.add(event);
        lastEvent.put(player, event);
    }

    /**
     * Used by players to inform if they have to show their cards when
     * requested, this is only the case when the game is in ShowDown (e.g. there
     * is no winner and the bidding has stopped) and the player has not folded.
     *
     * @param player
     * @return
     */
    public final boolean isShowDown(Player player) {
        EVENTTYPE type = player.getPlayerLastAction();
        return isshowdown && type != null && type != EVENTTYPE.FOLD;
    }

    /**
     * @return true if a showDown was needed to find a winner. In that case
     * showDown() returned a list of players in the showdown, with the cards
     * they held
     */
    public final boolean isShowDown() {
        return isshowdown;
    }

    /**
     * Internally sets the blinds. The player left of the dealer is big blind,
     * the player left of the big blind is the small blind. The amount to be put
     * in by the small blinds is 100 for a game of 6 players, and doubles for
     * every player that leaves the game. The big blind puts in double the
     * amount of the small blind.
     */
    private final void setBlinds() {
        blind = 100 * (int) Math.pow(2, 6 - playersLeftInGame.size());

        // big blind is the last player to bet
        bigblind = playersLeftInGame.get(playersLeftInGame.size() - 1);
        // small blind is the prior to last player to bet
        smallblind = playersLeftInGame.get(playersLeftInGame.size() - 2);

        addEvent(smallblind, EVENTTYPE.SMALLBLIND, Math.min(smallblind.cashOwned(), blind));
        addEvent(bigblind, EVENTTYPE.BIGBLIND, Math.min(bigblind.cashOwned(), blind * 2));
    }

    /**
     * @return amount that was requested to put in by small blind, and twice
     * that by big blind
     */
    public int getBlindAmount() {
        return blind;
    }

    /**
     * @return player that is on the small blind position
     */
    public Player getSmallBlind() {
        return smallblind;
    }

    /**
     * @return player that is on the big blind position
     */
    public Player getBigBlind() {
        return bigblind;
    }

    /**
     * The players are asked to bid in turn. If a player returns a bid equal to
     * the cash he has left, the player is all-in. Else, if a player return a
     * bid lower than the current maximum, the player folds. Else, if no raise
     * was made by pervious players or this player, the player passes. If the
     * player equals the maximum bid he calls, if a player over bids the current
     * maximum he raises and all other players in play get at least one more
     * bid.
     */
    public void bidUntilNoRaise() {
        // the bidLevel is the maximum amount that has been bet by any player, which
        // is what players have to meet to stay in the game, or go all in.
        int gameBidLevel = getMaxBidLevel();
        Player playerThatLastRaised = null;
        if (moreBidsRequired()) {
            while (playersLeftInGame.size() > 1) {
                // remove player from list
                Player player = playersLeftInGame.remove(0);
                if (playerThatLastRaised == player) {
                    // if this player is the last player that raised, this bidding
                    // round is over
                    playersLeftInGame.add(player);
                    break;
                }
                Event lastPlayerEvent = lastEvent.get(player);
                int playerCashAmount = player.cashOwned();
                int playerLastBidLevel = player.getPlayerBidLevel();
                if (playerCashAmount <= playerLastBidLevel) {
                    playersLeftInGame.add(player);
                    // player is already all in
                } else {
                    int raise = player.raise(gameBidLevel);
                    // a player cannot bet more than they have
                    int newPlayerBidLevel = (int) Math.min((long) playerLastBidLevel + raise, playerCashAmount);
                    if (newPlayerBidLevel == playerCashAmount) {
                        //  is all in
                        playersLeftInGame.add(player);
                        addEvent(player, EVENTTYPE.ALLIN, newPlayerBidLevel);
                    } else if (newPlayerBidLevel < gameBidLevel) {
                        addEvent(player, EVENTTYPE.FOLD, playerLastBidLevel);
                        // player folds
                    } else if (newPlayerBidLevel == gameBidLevel) {
                        EVENTTYPE b = (raise == 0) ? EVENTTYPE.PASS : EVENTTYPE.CALL;
                        addEvent(player, b, newPlayerBidLevel);
                        playersLeftInGame.add(player);
                        // pass or call
                    } else {
                        addEvent(player, EVENTTYPE.RAISE, newPlayerBidLevel);
                        playersLeftInGame.add(player);
                        // player raises
                    }
                    if (playerThatLastRaised == null && newPlayerBidLevel >= gameBidLevel) {
                        playerThatLastRaised = player;
                    }
                    if (newPlayerBidLevel > gameBidLevel) {
                        playerThatLastRaised = player;
                        gameBidLevel = newPlayerBidLevel;
                    }
                }
            }
        }
    }

    /**
     * @return the maximum amount of money put in by any player, which is the
     * amount that other players have to meet to stay in the game, or go all in
     * if they have less money.
     */
    private int getMaxBidLevel() {
        int maxBidLevel = 0;
        for (Event event : lastEvent.values()) {
            maxBidLevel = Math.max(maxBidLevel, event.bidLevel);
        }
        return maxBidLevel;
    }

    /**
     * @param bidlevel
     * @return true, if there is more than one player that has not folded and
     * did not go all in. Only then a new round of bids is required.
     */
    public boolean moreBidsRequired() {
        int count = 0;
        int maxBidLevel = getMaxBidLevel();
        for (Player player : playersLeftInGame) {
            Event event = lastEvent.get(player);
            if (event == null) {
                return true;
            }
            if (event.type != EVENTTYPE.ALLIN && maxBidLevel < player.cashOwned()) {
                count++;
            }
        }
        return count > 1;
    }

    /**
     * @return the total amount put in by all players
     */
    public int sumput() {
        int sumput = 0;
        for (Event value : lastEvent.values()) {
            sumput += value.bidLevel;
        }
        return sumput;
    }

    /**
     * Community Cards are cards in the middle of the table, that can be used by
     * all players to create the best possible hand of 5 cards when combined
     * with the two cards they hold. This method deals more community cards
     * until the total number of community cards is the given parameter.
     */
    public void dealCommunityCards(int cards) {
        for (int i = communitycards.size(); i < cards; i++) {
            communitycards.add(deck.dealCard());
        }
    }

    public ArrayList<Event> getEventHistory() {
        return (ArrayList) eventHistory.clone();
    }

    /**
     * @param player
     * @return last event for a player, with the total amount the player is in
     * for.
     */
    public Event getLastEvent(Player player) {
        return lastEvent.get(player);
    }

    /**
     * @return A list of cards in the middle of the table that can be use by any
     * player to make the best possible 5 card combination with the cards in
     * hand. This best combination is used to determine the winner.
     */
    public ArrayList<Card> getCommunityCards() {
        return (ArrayList) communitycards.clone();
    }

    /**
     * When the bidding is over, getWinner() resolves if there is a winner, and
     * arranges the cash transfer. If a player won by a raise to which all other
     * players, no cards are shown, otherwise, the players need to show their
     * cards and the computer evaluates which is the winner.
     *
     * @return if there was no showdown, a single value with no Hand and the
     * Player that won, otherwise a list of all players that have shown their
     * cards during a showdown. The hands are sorted by value, i.e. the winner
     * is listed first. Whether there is a winner or tie can also be queried by
     * the lastEvent(Player) which contains WINNER or TIE as the winners last
     * events.
     */
    public TreeMap<ComparableHand, Player> getShowDown() {
        if (showdown == null) {
            showdown = new TreeMap();
            if (playersLeftInGame.size() == 1) {
                cashToWinner(playersLeftInGame.get(0));
            } else {
                isshowdown = true; // so players show their cards
                for (Player p : playersLeftInGame) {
                    ComparableHand comparableHand = new ComparableHand(this, p.giveHand());
                    showdown.put(comparableHand, p);
                }
                TreeMap<ComparableHand, Player> winners = new TreeMap(showdown);
                ComparableHand firsthand = winners.firstKey();
                while (winners.lastEntry().getKey().compareTo(firsthand) > 0) {
                    winners.remove(winners.lastKey());
                }
                if (winners.size() == 1) {
                    cashToWinner(winners.firstEntry().getValue());
                } else {
                    splitPot(winners);
                }
            }
        }
        return showdown;
    }

    public Player getWinner() {
        for (Event event : lastEvent.values()) {
            if (event.type == EVENTTYPE.WIN) {
                return event.player;
            }
        }
        return null;
    }

    /**
     * When there is a single winner, he/she takes the pot, taking into
     * consideration that winners can never earn more money than what they
     * gambled (for all-in).
     *
     * @param winner
     */
    private void cashToWinner(Player winner) {
        int maxbet = lastEvent.get(winner).bidLevel;
        int prizemoney = 0;
        for (Event event : lastEvent.values()) {
            if (event.player != winner) {
                Player loser = event.player;
                int amount = Math.min(event.bidLevel, maxbet);
                loser.transferCash(new CashTransfer(loser, -amount));
                prizemoney += amount;
            }
        }
        winner.transferCash(new CashTransfer(winner, prizemoney));
        addEvent(winner, EVENTTYPE.WIN, maxbet);
    }

    /**
     * When multiple winners tie, the winnings are split by the joint winners. A
     * slight complication is that when players went all-in with an amount that
     * is less that what a losing player put in, the winner cannot receive more
     * than the amount he went all-in with.
     *
     * @param winners
     */
    private void splitPot(TreeMap<ComparableHand, Player> winners) {
        TreeSet<Integer> maxwinamount = new TreeSet();
        for (Player player : winners.values()) {
            maxwinamount.add(lastEvent.get(player).bidLevel);
            addEvent(player, EVENTTYPE.TIE, lastEvent.get(player).bidLevel);
        }
        HashMap<Player, Integer> owing = new HashMap();
        for (Event event : lastEvent.values()) {
            if (!winners.containsKey(event.player)) {
                Player loser = event.player;
                owing.put(loser, event.bidLevel);
            }
        }
        int amountAlreadyTaken = 0;
        int split = maxwinamount.size();
        for (int winamount : maxwinamount) {
            if (winamount > amountAlreadyTaken) {
                split = 0;
                for (Player player : winners.values()) {
                    int playerBet = lastEvent.get(player).bidLevel;
                    if (playerBet >= winamount) {
                        split++;
                    }
                }
                for (Map.Entry<Player, Integer> owe : owing.entrySet()) {
                    int amount = Math.min(owe.getValue(), winamount - amountAlreadyTaken);
                    int splitamount = amount / split;
                    for (Player player : winners.values()) {
                        int playerBet = lastEvent.get(player).bidLevel;
                        if (playerBet >= winamount) {
                            player.transferCash(new CashTransfer(player, splitamount));
                        }
                    }
                    int remainder = amount - split * splitamount;
                    if (remainder > 0) {
                        Player player = winners.firstEntry().getValue();
                        player.transferCash(new CashTransfer(player, remainder));
                    }
                    owe.setValue(owe.getValue() - amount);
                }
                amountAlreadyTaken = winamount;
            }
        }
        // losers must pay up, either what they have betted in total,
        // or less if the winner(s) went all-in with less money
        for (Map.Entry<Player, Integer> owe : owing.entrySet()) {
            Player loser = owe.getKey();
            int playerLastBet = lastEvent.get(loser).bidLevel;
            int moneyNotGoneToWinners = owe.getValue();
            int amountToPay = playerLastBet - moneyNotGoneToWinners;
            loser.transferCash(new CashTransfer(loser, amountToPay));
        }
    }

    protected final class CashTransfer {

        final int amount;
        final Player player;

        private CashTransfer(Player player, int amount) {
            this.player = player;
            this.amount = amount;
        }
    }
}
