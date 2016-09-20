package tools.search;

import Game.GameState;
import actions.MoveAction;

/**
 *
 */
public interface AIBot {
    public MoveAction nextMove(GameState state, long computationTime);
    public void stop();
}
