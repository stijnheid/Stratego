package tools.deeplearning;

import Game.GameBoard;
import Game.Team;

/**
 *
 */
public interface DeepLearner {
    /**
     * The generator that generates random data sets and executes the battle
     * will supply the end result to this method. The input consits of the
     * initial board setup of both players and the winner of the match.
     * 
     * @param initialSetup initial GameBoard setup.
     * @param winner the winner of the match with this initial setup between
     * two AI bots.
     * @param isAttacker indicates if the winner was the attacking player or
     * the defending player.
     */
    public void learn(GameBoard initialSetup, Team winner, boolean isAttacker);    
}
