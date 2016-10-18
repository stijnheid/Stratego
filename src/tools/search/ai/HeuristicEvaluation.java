package tools.search.ai;

import Game.GameState;

/**
 *
 */
public interface HeuristicEvaluation {
    /**
     * 
     * @param state current game state at this node of the search tree.
     * @return appraised value for the current game state.
     */
    public double score(GameState state);
}
