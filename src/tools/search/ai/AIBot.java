package tools.search.ai;

import Game.GameState;
import actions.MoveAction;
import tools.search.Player;

/**
 * This interface must be implemented by AI players for the game.
 */
public interface AIBot extends Player {
    /**
     * 
     * @param state current game state.
     * @return the next move for this player.
     */
    public MoveAction nextMove(GameState state);
    
    /**
     * Invoke stop to terminate the search algorithm of the bot and let it
     * return a move immediately. This is useful if you want to limit the AI
     * bot to a specific amount of computation time.
     */
    public void stop();
}
