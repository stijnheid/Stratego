/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.search.ai.players;

import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.HashMap;
import java.util.List;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
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
    
    private class MyHeuristic implements HeuristicEvaluation {

        @Override
        public double score(GameState state) {
            double score = 0;
            return score;
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
            double score;
            return score = (HighestValue(state, ModeratePlayerTwo.super.team)/2) * getPiecesAmount(state, ModeratePlayerTwo.super.team, Pieces.BOMB);
        }
        
    }
    
    //get the amount of pieces of a specific unit
    public int getPiecesAmount(GameState state, Team team, Pieces unit) {
        GameBoard board = state.getGameBoard();
        List<GamePiece> pieces = board.getPieces(team, unit);
        return pieces.size();
    }
    
    public int HighestValue(GameState state, Team team) {
        GameBoard board = state.getGameBoard();
        List<GamePiece> army = board.getTeam(ModeratePlayerTwo.super.team);
        
        int maxValue = Integer.MIN_VALUE;
        for (GamePiece pieces : army) {
            if (attacker.get(army) > maxValue) {
                maxValue = attacker.get(army);
            }
        }
        
        return maxValue;
    }
}
