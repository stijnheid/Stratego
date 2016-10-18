package tools.search.ai;

import Game.GameState;
import Game.Team;
import actions.MoveAction;

/**
 * Complementary class to the AlphaBetaSearch class that is used to store the
 * current game state and best move at a particular tree node.
 */
public class GameNode {
    private final GameState state;
    private MoveAction bestMove;
    private double value; // Heuristic value of the best move in context of the
    // current board state.
    
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
        //if(move != null && move.getTeam() != Team.RED) {
        //    throw new RuntimeException();
        //}
        
        this.bestMove = move;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
