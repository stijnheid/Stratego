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
        super(team);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        
        // Set the heuristic.
        this.searchEngine.setHeuristic(new MyHeuristic());
        
        GameNode node = new GameNode(state);
        MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, -1, false);
        //MoveAction move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 1, -1, false);
        
        return move;
    }
    
    private class MyHeuristic implements HeuristicEvaluation {

        @Override
        public double score(GameState state) {
            PieceValue pieceValue = new PieceValue();
            BoardValue boardValue = new BoardValue();
            double score = 0;
            pieceValue.setWeight(0.7);
            boardValue.setWeight(0.3);
            score = pieceValue.computeScore(state) + boardValue.computeScore(state);
            return score;
        }
        
    }
    
    private class PieceValue extends WeighedHeuristicTerm {
        HashMap<Pieces, Integer> defender = new HashMap<>();
        HashMap<Pieces, Integer> attacker = new HashMap<>();

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
            double attackerScore = 0;
            double defenderScore = 0;
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
                defenderScore = defender.get(Pieces.BOMB) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                        + defender.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defender.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defender.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defender.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defender.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR);
                attackerScore = attacker.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attacker.get(Pieces.GENERAL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attacker.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attacker.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attacker.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attacker.get(Pieces.MINER) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER) 
                          * (1 / (getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) + 1))
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attacker.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
                score = 1080 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defenderScore
                        + attackerScore;
            }
            else {
                defenderScore = defender.get(Pieces.BOMB) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * (1 / (getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER) + 1))
                        + defender.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defender.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defender.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defender.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defender.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR);
               attackerScore = attacker.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attacker.get(Pieces.GENERAL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attacker.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attacker.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attacker.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                          * (1 / (getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) + 1))
                        + attacker.get(Pieces.SPY) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attacker.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
               score =  2520 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defenderScore
                        + attackerScore;
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
        
        //HashMap<BoardPosition, Integer> cellValue = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            /**
            cellValue.put(new BoardPosition(0, 0), 0);
            cellValue.put(new BoardPosition(0, 1), 0);
            cellValue.put(new BoardPosition(0, 2), 100);
            cellValue.put(new BoardPosition(0, 3), 150);
            cellValue.put(new BoardPosition(0, 4), 200);
            cellValue.put(new BoardPosition(0, 5), 200);
            cellValue.put(new BoardPosition(1, 0), 0);
            cellValue.put(new BoardPosition(1, 1), 0);
            cellValue.put(new BoardPosition(1, 2), 100);
            cellValue.put(new BoardPosition(1, 3), 150);
            cellValue.put(new BoardPosition(1, 4), 200);
            cellValue.put(new BoardPosition(1, 5), 200);
            cellValue.put(new BoardPosition(2, 0), 0);
            cellValue.put(new BoardPosition(2, 1), 0);
            cellValue.put(new BoardPosition(2, 2), 100);
            cellValue.put(new BoardPosition(2, 3), 150);
            cellValue.put(new BoardPosition(2, 4), 200);
            cellValue.put(new BoardPosition(2, 5), 200);
            cellValue.put(new BoardPosition(3, 0), 0);
            cellValue.put(new BoardPosition(3, 1), 0);
            cellValue.put(new BoardPosition(3, 2), 100);
//            cellValue.put(new BoardPosition(3, 3), 150);
            cellValue.put(new BoardPosition(3, 4), 200);
            cellValue.put(new BoardPosition(3, 5), 200);
            cellValue.put(new BoardPosition(4, 0), 0);
            cellValue.put(new BoardPosition(4, 1), 0);
            cellValue.put(new BoardPosition(4, 2), 100);
            cellValue.put(new BoardPosition(4, 3), 150);
//            cellValue.put(new BoardPosition(4, 4), 200);
//            cellValue.put(new BoardPosition(4, 5), 200);
            cellValue.put(new BoardPosition(5, 0), 0);
            cellValue.put(new BoardPosition(5, 1), 0);
            cellValue.put(new BoardPosition(5, 2), 100);
            cellValue.put(new BoardPosition(5, 3), 150);
//            cellValue.put(new BoardPosition(5, 4), 200);
//            cellValue.put(new BoardPosition(5, 5), 200);
            */
            
            /**
            String setup = "0|0|0\n" +
                            "0|0|0\n" +
                            "100|100|100\n" +
                            "150|150|150\n" +
                            "200|200|200\n" +
                            "200|200|200";
            */
            String setup = "1000|1000|1000\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n";
            
            HashMap<BoardPosition, Integer> map = loadMap(setup);
            
            for (Map.Entry<BoardPosition, Integer> entrys: map.entrySet()) {
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
    
    public static void main(String[] args) {
        ModeratePlayer player = new ModeratePlayer(Team.BLUE);
        
        String setup = "0|0|0\n" +
                        "0|0|0\n" +
                        "100|100|100\n" +
                        "150|150|150\n" +
                        "200|200|200\n" +
                        "200|200|200";
        
        HashMap<BoardPosition, Integer> map = player.loadMap(setup);
        player.printMap(map, 3, 6);
    }
    
    private void printMap(HashMap<BoardPosition, Integer> map, int w, int h) {
        StringBuilder builder = new StringBuilder();
        for(int r=0; r<h; r++) {
            for(int c=0; c<w; c++) {
                BoardPosition pos = new BoardPosition(c, r);
                Integer value = map.get(pos);
                if(value != null) {
                    int val = (int) value;
                    builder.append(val);
                } else {
                    builder.append(" ");
                }
                
                if(c < (w - 1)) {
                    builder.append("|");
                }
            }
            
            builder.append("\n");
            
            if(r < (h - 1)) {
                for(int i=0; i<w; i++) {
                    builder.append("-");
                    if(i < w - 1) {
                        builder.append(" ");
                    }
                }
                builder.append("\n");
            }
        }
        
        System.out.println("CellMap:\n" + builder.toString());
    }
    
    private HashMap<BoardPosition, Integer> loadMap(String values) {
        HashMap<BoardPosition, Integer> map = new HashMap<>();
        
        String[] lines = values.split("\n");
        for(int row=0; row<lines.length; row++) {
            String line = lines[row];
            
            String[] vals = line.split("\\|");
            for(int column=0; column<vals.length; column++) {
                int value = Integer.parseInt(vals[column]);
                BoardPosition pos = new BoardPosition(column, row);
                map.put(pos, value);
            }
        }
        
        return map;
    }
}
