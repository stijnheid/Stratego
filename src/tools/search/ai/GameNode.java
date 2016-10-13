package tools.search.ai;

import Game.GameState;
import actions.MoveAction;

/**
 * Complementary class to the AlphaBetaSearch class that is used to store the
 * current game state and best move at a particular tree node.
 */
public class GameNode {
    private final GameState state;
    private MoveAction bestMove;
    
    public GameNode(GameState state) {
        this.state = state;
        this.bestMove = null;
    }
    
    public GameState getState() {
        return this.state;
    }
    
    public MoveAction getBestMove() {
        return this.bestMove;
    }
    
    public void setBestMove(MoveAction move) {
        this.bestMove = move;
    }
}
