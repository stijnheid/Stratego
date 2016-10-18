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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.WeighedEvaluation;
import tools.search.ai.WeighedHeuristicTerm;

/**
 *
 * @author s122041
 */
public class ModeratePlayer extends AbstractPlayer {
    
    private final static HashMap<Pieces, Integer> attacker;
    private final static HashMap<Pieces, Integer> defender;
    
    static {
        defender = new HashMap<>();
        defender.put(Pieces.BOMB, 300);
        defender.put(Pieces.MARSHALL, 500);
        defender.put(Pieces.COLONEL, 400);
        defender.put(Pieces.MAJOR, 350);
        defender.put(Pieces.CAPTAIN, 200);
        defender.put(Pieces.LIEUTENANT, 100);
        defender.put(Pieces.FLAG, 1055);
        
        attacker = new HashMap<>();            
        attacker.put(Pieces.MARSHALL, -600);
        attacker.put(Pieces.GENERAL, -500);
        attacker.put(Pieces.COLONEL, -400);
        attacker.put(Pieces.MAJOR, -350);
        attacker.put(Pieces.CAPTAIN, -200);
        attacker.put(Pieces.LIEUTENANT, -100);
        attacker.put(Pieces.MINER, -300);
        attacker.put(Pieces.SPY, -325);
    }
    
    PieceValue pieceValue = new PieceValue();
    BoardValue boardValue = new BoardValue();
        
    List<WeighedHeuristicTerm> Term = Arrays.asList(pieceValue, boardValue);

    public ModeratePlayer(Team team) {
        super(team);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        
        // Set the heuristic.
        this.searchEngine.setHeuristic(new MyHeuristic());
        
        GameNode node = new GameNode(state);
        //MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, -1, false);
        MoveAction move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 1, -1, false);
        
        return move;
    }
    
    private class MyHeuristic implements WeighedEvaluation {

        @Override
        public double score(GameState state) {
            double score = 0;
            pieceValue.setWeight(0.7);
            boardValue.setWeight(0.3);
            score = pieceValue.computeScore(state) + boardValue.computeScore(state);
            
            //return a minus value because it is a minimize player
            return -score;
        }

        @Override
        public void setWeights(double[] weights) {
            
            for (WeighedHeuristicTerm term: Term) {
                for (double weight: weights) {
                    term.setWeight(weight);
                }
            }
        }

        @Override
        public int featuresConnt() {
            return Term.size();
        }
    }
    
    private class PieceValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            //if the spy of attacker is dead, the marshal of defender will become more vaulable
            List<GamePiece> attackerSpies = board.getPieces(ModeratePlayer.super.team.opposite(), Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                defender.put(Pieces.MARSHALL, 625);
            }
            
            //if the marshall of the defender is dead, the marshall and spy of attacker will become less vaulable
            List<GamePiece> defenderMarshalls = board.getPieces(ModeratePlayer.super.team, Pieces.MARSHALL);
            if(defenderMarshalls.isEmpty()) {
                // Marshall of defender died.
                attacker.put(Pieces.MARSHALL, -480);
                attacker.put(Pieces.SPY, -260);
            }
            
            //If the flag is surrounded by the bombs, the bombs and the miner of attacker will become much more valueable
            if(FlagSurroundedByBomb(state)) {
                defender.put(Pieces.BOMB, 300 * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER));
            }
            else {
                defender.put(Pieces.FLAG, 2495);
                defender.put(Pieces.BOMB, 300 * (1 / (getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER) + 1)));
            }
            
            //If all the bombs from defender are gone, the miner of attacker will become less valueable
            List<GamePiece> defenderBomb = board.getPieces(ModeratePlayer.super.team, Pieces.BOMB);
            if(defenderBomb.isEmpty()) {
                attacker.put(Pieces.MINER, -100);
            }
            else {
                attacker.put(Pieces.MINER, -300 * (1 / getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB)));
            }
            
            List<GamePiece> red = board.getTeam(Team.RED);
            List<GamePiece> blue = board.getTeam(Team.BLUE);

            // Compute the score of team red.
            int redScore = 0;
            for(GamePiece piece : red) {
                redScore+= ModeratePlayer.attacker.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= ModeratePlayer.defender.get(piece.getRank());
            }
            score = blueScore + redScore;
            
            return score;
            //return -score * (blue.size()/red.size());
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
        
    }
    
    private class BoardValue extends WeighedHeuristicTerm {
        
        //HashMap<BoardPosition, Integer> cellValue = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();            
            
            
            String setup =  "0|0|0|0\n" +
                            "50|50|50|50\n" +
                            "100|100|100|100\n" +
                            "150|150|150|150\n" +
                            "200|200|200|200\n" +
                            "250|250|250|250";
            
            /**
            String setup = "1000|1000|1000\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n";
            */
            /**
            /*
            String setup = "25|25|25|25|25|25\n" +
                            "50|50|50|50|50|50\n" +
                            "100|100|100|100|100|100\n" +
                            "150|150|150|150|150|150\n" +
                            "200|200|200|200|200|200\n" +
                            "200|200|200|200|200|200\n";
            */
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
            
            return score;
        } 
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
