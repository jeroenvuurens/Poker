package nl.hhs.poker;

/**
 *
 * @author jeroen
 */
public class CallBot extends Player {

    @Override
    public int raise(int gameBidLevel, Hand hand) {
        return gameBidLevel - getPlayerBidLevel();
    }

    @Override
    public String name() {
        return "CallBot";
    }
}
