package nl.hhs.poker;

/**
 *
 * @author jeroen
 */
public final class Event {
    public final Player player;
    public final EVENTTYPE type;
    public final int bidLevel; // total amount put in by player (so not just the raised amount)

    public Event(Player player, EVENTTYPE type, int total) {
        this.player = player;
        this.type = type;
        this.bidLevel = total;
    }
    
    @Override
    public String toString() {
        return player.toString() + " " + type.toString() + " " + bidLevel; 
    }
}
