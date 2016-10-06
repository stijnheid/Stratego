/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.search.ai.players;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.WeighedHeuristicTerm;

/**
 *
 * @author s122041
 */
public class ModeratePlayer extends AbstractPlayer {
    

    public ModeratePlayer(Team team) {
        super.team = team;
        this.searchEngine = new AlphaBetaSearch(null);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        
        // Set the heuristic.
        this.searchEngine.setHeuristic(new MyHeuristic());
        
        GameNode node = new GameNode(state);
        MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, -1, false);
        
        return move;
    }
    
    private class MyHeuristic implements HeuristicEvaluation {

        @Override
        public double score(GameState state) {
            PieceValue pieceValue = new PieceValue();
            BoardValue boardValue = new BoardValue();
            double score = 0;
            score = pieceValue.computeScore(state) + boardValue.computeScore(state);
            return score;
        }
        
    }
    
    private class PieceValue extends WeighedHeuristicTerm {
        HashMap<Pieces, Integer> defender = new HashMap<>();
        HashMap<Pieces, Integer> attacker          = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            defender.put(Pieces.BOMB, 300);
            defender.put(Pieces.MARSHALL, 500);
            defender.put(Pieces.COLONEL, 400);
            defender.put(Pieces.MAJOR, 350);
            defender.put(Pieces.CAPTAIN, 200);
            defender.put(Pieces.LIEUTENANT, 100);
            
            attacker.put(Pieces.MARSHALL, -600);
            attacker.put(Pieces.GENERAL, -500);
            attacker.put(Pieces.COLONEL, -400);
            attacker.put(Pieces.MAJOR, -350);
            attacker.put(Pieces.CAPTAIN, -200);
            attacker.put(Pieces.LIEUTENANT, -100);
            attacker.put(Pieces.MINER, -300);
            attacker.put(Pieces.SPY, -300);
            
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            //if the spy of attacker is still alive, the marshal of defender will become less vaulable
            List<GamePiece> attackerSpies = board.getPieces(ModeratePlayer.super.team.opposite(), Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                defender.put(Pieces.MARSHALL, 400);
            }
            
            //if the marshall of the defender is still alive, the marshall of attacker will become less vaulable
            List<GamePiece> defenderMarshalls = board.getPieces(ModeratePlayer.super.team.opposite(), Pieces.MARSHALL);
            if(defenderMarshalls.isEmpty()) {
                // Marshall of defender died.
                attacker.put(Pieces.MARSHALL, -480);
            }
            
            //adding all the values for the game state
            //If the flag is surrounded by the bombs, the bombs will become much more valueable
            if(FlagSurroundedByBomb(state)) {
                score = 1080 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defender.get(Pieces.BOMB) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                        + defender.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defender.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defender.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defender.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defender.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR)
                        + attacker.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attacker.get(Pieces.GENERAL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attacker.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attacker.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attacker.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attacker.get(Pieces.MINER) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER) 
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB))
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attacker.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
            }
            else {
               score =  2520 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defender.get(Pieces.BOMB) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER))
                        + defender.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defender.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defender.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defender.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defender.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR)
                        + attacker.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attacker.get(Pieces.GENERAL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attacker.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attacker.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attacker.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB))
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attacker.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
            }
            
            return -score;
        }
        
        //check if the flag are surrounded by bombs
        public boolean FlagSurroundedByBomb(GameState state) {
            GameBoard board = state.getGameBoard();
            return board.isFlagUnreachable();
        }
        
        //get the amount of pieces of a specific unit
        public int getPiecesAmount(GameState state, Team team, Pieces unit) {
            GameBoard board = state.getGameBoard();
            List<GamePiece> pieces = board.getPieces(team, unit);
            return pieces.size();
        }
        
        public void getHighestValue(GameState state, Team team) {
            GameBoard board = state.getGameBoard();
            List<GamePiece> army = board.getTeam(team);
        }
        
    }
    
    private class BoardValue extends WeighedHeuristicTerm {
        
        HashMap<BoardPosition, Integer> cellValue = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            cellValue.put(new BoardPosition(0, 0), 0);
            cellValue.put(new BoardPosition(0, 1), 0);
            cellValue.put(new BoardPosition(0, 2), 0);
            cellValue.put(new BoardPosition(0, 3), 0);
            cellValue.put(new BoardPosition(0, 4), 0);
            cellValue.put(new BoardPosition(0, 5), 0);
            cellValue.put(new BoardPosition(1, 0), 0);
            cellValue.put(new BoardPosition(1, 1), 0);
            cellValue.put(new BoardPosition(1, 2), 0);
            cellValue.put(new BoardPosition(1, 3), 0);
            cellValue.put(new BoardPosition(1, 4), 0);
            cellValue.put(new BoardPosition(1, 5), 0);
            cellValue.put(new BoardPosition(2, 0), 100);
            cellValue.put(new BoardPosition(2, 1), 100);
            cellValue.put(new BoardPosition(2, 2), 100);
            cellValue.put(new BoardPosition(2, 3), 100);
            cellValue.put(new BoardPosition(2, 4), 100);
            cellValue.put(new BoardPosition(2, 5), 100);
            cellValue.put(new BoardPosition(3, 0), 150);
            cellValue.put(new BoardPosition(3, 1), 150);
            cellValue.put(new BoardPosition(3, 2), 150);
            cellValue.put(new BoardPosition(3, 3), 150);
            cellValue.put(new BoardPosition(3, 4), 150);
            cellValue.put(new BoardPosition(3, 5), 150);
            cellValue.put(new BoardPosition(4, 0), 200);
            cellValue.put(new BoardPosition(4, 1), 200);
            cellValue.put(new BoardPosition(4, 2), 200);
            cellValue.put(new BoardPosition(4, 3), 200);
            cellValue.put(new BoardPosition(4, 4), 200);
            cellValue.put(new BoardPosition(4, 5), 200);
            cellValue.put(new BoardPosition(5, 0), 200);
            cellValue.put(new BoardPosition(5, 1), 200);
            cellValue.put(new BoardPosition(5, 2), 200);
            cellValue.put(new BoardPosition(5, 3), 200);
            cellValue.put(new BoardPosition(5, 4), 200);
            cellValue.put(new BoardPosition(5, 5), 200);
            
            for (Map.Entry<BoardPosition, Integer> entrys: cellValue.entrySet()) {
                BoardPosition position = entrys.getKey();
                Integer value = entrys.getValue();
                
                try {
                    if (board.isEmpty(position)) {
                        score = score + 0;
                    }
                    else {
                        if (board.getPiece(position).getTeam() == Team.RED) {
                            score = score - value;
                        }
                        else {
                            score = score + value;
                        }
                    }
                } catch (InvalidPositionException ex) {
                    Logger.getLogger(ModeratePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return -score;
        }
        
    }
    
}
