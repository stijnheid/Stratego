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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.WeighedEvaluation;
import tools.search.ai.WeighedHeuristicTerm;

/**
 *
 * @author s122041
 */
public class ModeratePlayerTwo extends AbstractPlayer{
    
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
    
    MarshallValue marshallValue = new MarshallValue();
    SpyValue spyValue = new SpyValue();
    MinerValue minerValue = new MinerValue();
    BombValue bombValue = new BombValue();
    OtherValue otherValue = new OtherValue();
    BoardValue boardValue = new BoardValue();
    
    List<WeighedHeuristicTerm> Term = Arrays.asList(marshallValue, spyValue, minerValue, bombValue, otherValue, boardValue);
    
    public ModeratePlayerTwo(Team team) {
        super(team);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        // Set the heuristic.
        this.searchEngine.setHeuristic(new ModeratePlayerTwo.MyHeuristic());
        
        GameNode node = new GameNode(state);
        //MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, -1, false);
        MoveAction move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 1, -1, false);
        
        return move;
    }
    
    private class MyHeuristic implements WeighedEvaluation {

        @Override
        public double score(GameState state) {
            double score = 0;
            for (WeighedHeuristicTerm term: Term) {
                score+= term.computeScore(state);
            }
            
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
    
    private class MarshallValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            //if the spy of attacker is still alive, the marshal of defender will become less vaulable
            List<GamePiece> attackerSpies = board.getPieces(ModeratePlayerTwo.super.team.opposite(), Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                score = defender.get(Pieces.MARSHALL) * 0.8;
            }
            else {
                score = defender.get(Pieces.MARSHALL);
            }
           
            return score;
        }        
    }
    
    private class SpyValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            List<GamePiece> defenderMarshall = board.getPieces(ModeratePlayerTwo.super.team, Pieces.MARSHALL);
            if(defenderMarshall.isEmpty()) {
                // Spy of attacker died.
                score = attacker.get(Pieces.SPY) * 0.5;
            }
            else {
                score = attacker.get(Pieces.SPY);
            }
            
            return score;
        }
        
    }
    
    private class MinerValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            List<GamePiece> attackerMiner = board.getPieces(ModeratePlayerTwo.super.team.opposite(), Pieces.MINER);
            if (attackerMiner.size() < 3) {
                score = attacker.get(Pieces.MINER) * (4 - (getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.MINER)));
            }
            else {
                score = defender.get(Pieces.MINER);
            }
            
            return score * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.MINER);
        }
        
    }
    
    private class BombValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            score = (-HighestValue(state, ModeratePlayerTwo.super.team.opposite(), attacker)/2);
            return score * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.BOMB);
        }
        
    }
    
    private class OtherValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double redScore = 0;
            double blueScore = 0;
            double score = 0;
            GameBoard board = state.getGameBoard();
            List<GamePiece> attackerArmy = board.getTeam(ModeratePlayerTwo.super.team.opposite());
            List<GamePiece> defenderArmy = board.getTeam(ModeratePlayerTwo.super.team);
            
            redScore = attacker.get(Pieces.MARSHALL) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.MARSHALL)
                       + attacker.get(Pieces.GENERAL) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.GENERAL) 
                       + attacker.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.COLONEL)
                       + attacker.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.CAPTAIN)
                       + attacker.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.MAJOR)
                       + attacker.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayerTwo.super.team.opposite(), Pieces.LIEUTENANT);
            
            blueScore = defender.get(Pieces.FLAG) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.FLAG)
                       + defender.get(Pieces.COLONEL) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.COLONEL)
                       + defender.get(Pieces.CAPTAIN) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.CAPTAIN)
                       + defender.get(Pieces.MAJOR) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.MAJOR)
                       + defender.get(Pieces.LIEUTENANT) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.LIEUTENANT);
            
            score = redScore * (attackerArmy.size()/defenderArmy.size())+ blueScore * (attackerArmy.size()/defenderArmy.size());
            return score;
        }
        
    }
    
    private class BoardValue extends WeighedHeuristicTerm {

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
    
    //get the amount of pieces of a specific unit
    public int getPiecesAmount(GameState state, Team team, Pieces unit) {
        GameBoard board = state.getGameBoard();
        List<GamePiece> pieces = board.getPieces(team, unit);
        return pieces.size();
    }
    
    //Find the highest value among all the pieces from a team
    public int HighestValue(GameState state, Team team, HashMap<Pieces, Integer> value) {
        GameBoard board = state.getGameBoard();
        List<GamePiece> army = board.getTeam(team);
        
        int maxValue = Integer.MIN_VALUE;
        for (GamePiece piece : army) {
            if (maxValue <= value.get(piece.getRank())) {
                maxValue = value.get(piece.getRank());
            }
        }
        
        return maxValue;
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
