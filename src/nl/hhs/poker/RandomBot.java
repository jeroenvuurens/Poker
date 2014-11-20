package nl.hhs.poker;

import java.util.Random;

/**
 * Computer Player that plays randomly
 * @author jeroen
 */
public class RandomBot extends Player {

    private static final Random randNum = new Random();

    @Override
    public int raise(int gameBidLevel, Hand hand) {
        // example how to retrieve this players last bid
        // gameBidLevel - playerBidLevel = what it costs to stay in the game
        // you should return the total amount bet though
        int random = randNum.nextInt(100);
        if (random > 95) // go all in
        {
            return Integer.MAX_VALUE;
        }
        if (random > 80) // raise 100
        {
            return gameBidLevel - getPlayerBidLevel() + 100;
        }
        if (random > 40) // call, minimum - lastbid = what it costs to stay in the game
        {
            return gameBidLevel - getPlayerBidLevel();
        }
        // fold
        return 0;
    }

    @Override
    public String name() {
        return "RandomBot";
    }
}
