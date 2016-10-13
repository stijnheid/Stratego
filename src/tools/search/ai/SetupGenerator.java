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
        GameBoard board = new GameBoard(6, 6, Team.RED, Team.BLUE);        
        
        // Fixed Setup.
        try {
            // Attacker
            board.setupPiece(0, 0, Pieces.MARSHALL, Team.RED);
            board.setupPiece(0, 1, Pieces.GENERAL, Team.RED);
            board.setupPiece(1, 0, Pieces.COLONEL, Team.RED);
            board.setupPiece(2, 0, Pieces.MINER, Team.RED);
            board.setupPiece(3, 0, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(4, 0, Pieces.MINER, Team.RED);
            board.setupPiece(5, 0, Pieces.SPY, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(2, 1, Pieces.SERGEANT, Team.RED);
            board.setupPiece(3, 1, Pieces.MAJOR, Team.RED);
            board.setupPiece(4, 1, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(5, 1, Pieces.MINER, Team.RED);
            
            // Defender
            board.setupPiece(0, 5, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 4, Pieces.BOMB, Team.BLUE);
            board.setupPiece(4, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(1, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(2, 5, Pieces.MINER, Team.BLUE);
            board.setupPiece(3, 5, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(1, 4, Pieces.CAPTAIN, Team.BLUE);
            board.setupPiece(2, 4, Pieces.MAJOR, Team.BLUE);
            board.setupPiece(3, 4, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(4, 4, Pieces.CAPTAIN, Team.RED);
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

    public GameBoard generateModeratePlayerSetup() {
        GameBoard board = new GameBoard(6, 6, Team.RED, Team.BLUE);
        try {
            // Attacker
            board.setupPiece(0, 0, Pieces.MARSHALL, Team.RED);
            board.setupPiece(0, 1, Pieces.GENERAL, Team.RED);
            board.setupPiece(1, 0, Pieces.COLONEL, Team.RED);
            board.setupPiece(2, 0, Pieces.MINER, Team.RED);
            board.setupPiece(3, 0, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(4, 0, Pieces.MINER, Team.RED);
            board.setupPiece(5, 0, Pieces.SPY, Team.RED);
            board.setupPiece(1, 1, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(2, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(3, 1, Pieces.MAJOR, Team.RED);
            board.setupPiece(4, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(5, 1, Pieces.MINER, Team.RED);
            
            // Defender
            board.setupPiece(0, 5, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 4, Pieces.BOMB, Team.BLUE);
            board.setupPiece(4, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(1, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(2, 5, Pieces.LIEUTENANT,Team.BLUE);
            board.setupPiece(3, 5, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(1, 4, Pieces.CAPTAIN, Team.BLUE);
            board.setupPiece(2, 4, Pieces.MAJOR, Team.BLUE);
            board.setupPiece(3, 4, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(4, 4, Pieces.CAPTAIN, Team.BLUE);
            board.setupPiece(5, 5, Pieces.COLONEL, Team.BLUE);
            board.setupPiece(5, 4, Pieces.MARSHALL, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return board;
    }

    public GameBoard generateSmallBoard() {
        String setup = "r:4|r:5\n" +
                       "--- ---\n" +
                       "   |   \n" + 
                       "--- ---\n" + 
                       "b:6|b:5\n";
        
        GameBoard board = GameBoard.loadBoard(setup, 2, 3);
        return board;
    }
    
    public GameBoard generateShowcase() {
        String setup = "r:4|r:4|r:4\n" +
                        "--- --- ---\n" + 
                        "r:4|r:8|r:S\n" + 
                        "--- --- ---\n" + 
                        "   |   |   \n" +
                        "--- --- ---\n" +
                        "   |   |   \n" +
                        "--- --- ---\n" +
                        "b:9|b:4|b:4\n" +
                        "--- --- ---\n" +
                        "b:4|b:4|b:4\n";
        return GameBoard.loadBoard(setup, 3, 6);
    }
}
