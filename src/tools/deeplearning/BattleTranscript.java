package tools.deeplearning;

import Game.GameBoard;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.List;

/**
 * A battle can be described as an initial setup and a list of sequential
 * moves.
 */
public class BattleTranscript {
    
    private final GameBoard initialSetup;
    private final List<MoveAction> moves;
    
    public BattleTranscript(GameBoard board) {
        this.initialSetup = (GameBoard) board.clone();
        this.moves = new ArrayList<>();
    }
    
    public void addMove(MoveAction move) {
        moves.add((MoveAction) move.clone());
    }
    
    public List<MoveAction> getMoves() {
        return this.moves;
    }
    
    public void print() {
        System.out.println("Initial Setup\n");
        System.out.println(initialSetup.transcript());
        System.out.println("\n");
        
        // Should actually clone the board again.
        
        for(MoveAction move : moves) {
            initialSetup.applyMove(move);
            System.out.println(move.toString());
            System.out.println(initialSetup.transcript());
            System.out.println("\n");
        }
    }
}
