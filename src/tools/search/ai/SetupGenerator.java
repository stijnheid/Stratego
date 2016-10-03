package tools.search.ai;

import Game.GameBoard;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.deeplearning.BattleEngine;

/**
 *
 */
public class SetupGenerator {
    
    public GameBoard generateSetup() {
        GameBoard board = new GameBoard(4, 6, Team.RED, Team.BLUE);        
        
        // Fixed Setup.
        try {
            // Attacker
            board.setupPiece(0, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(0, 1, Pieces.BOMB, Team.RED);
            board.setupPiece(1, 0, Pieces.BOMB, Team.RED);
            board.setupPiece(2, 0, Pieces.MINER, Team.RED);
            board.setupPiece(3, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(2, 1, Pieces.SERGEANT, Team.RED);
            board.setupPiece(3, 1, Pieces.LIEUTENANT, Team.RED);
            
            // Defender
            board.setupPiece(0, 5, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 4, Pieces.BOMB, Team.BLUE);
            board.setupPiece(1, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(2, 5, Pieces.MINER, Team.BLUE);
            board.setupPiece(3, 5, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 4, Pieces.CAPTAIN, Team.BLUE);
            board.setupPiece(2, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(3, 4, Pieces.LIEUTENANT, Team.BLUE);            
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return board;
    }
    
    public GameBoard smallSetup() {
        GameBoard board = new GameBoard(2, 5, Team.RED, Team.BLUE);
        try {
            // Setup the teams.
            board.setupPiece(0, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(0, 1, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(1, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            
            board.setupPiece(1, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 3, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(0, 4, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 3, Pieces.CAPTAIN, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return board;
    }
    
    public GameBoard mirroredSetup() {
        GameBoard board = new GameBoard(2, 5, Team.RED, Team.BLUE);
        try {
            // Setup the teams.
            board.setupPiece(1, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(1, 1, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(0, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(0, 1, Pieces.CAPTAIN, Team.RED);
            
            board.setupPiece(1, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 3, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(0, 4, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 3, Pieces.CAPTAIN, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return board;
    }    
    
    public GameBoard generateEqualSetup() {
        return null;
    }
}
