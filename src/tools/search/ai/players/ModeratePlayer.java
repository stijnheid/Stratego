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
import java.util.List;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;

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
        //Assign value to defender's pieces
        double defenderBomb = 300;
        double defenderMarshal = 500; 
        double defenderColonel = 400;
        double defenderMajor = 350;
        double defenderCaptain = 200;
        double defenderLieutenant = 100;
        //Assign value to attacker's pieces
        double attackerMarshal = -600;
        double attackerGeneral = -500;
        double attackerColonel = -400;
        double attackerMajor = -350;
        double attackerCaptain = -200;
        double attackerLieutenant = -100;
        double attackerMiner = -300;
        double attackerSpy = -300;

        @Override
        public double score(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();        
            
            //if the spy of attacker is still alive, the marshal of defender will become less vaulable
            List<GamePiece> attackerSpies = board.getPieces(ModeratePlayer.super.team.opposite(), Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                defenderMarshal = defenderMarshal * 0.8;
            }
            
            //if the marshall of the defender is still alive, the marshall of attacker will become less vaulable
            List<GamePiece> defenderMarshalls = board.getPieces(ModeratePlayer.super.team.opposite(), Pieces.MARSHALL);
            if(defenderMarshalls.isEmpty()) {
                // Marshall of defender died.
                attackerMarshal = attackerMarshal * 0.8;
            }
            
            //adding all the values for the game state
            //If the flag is surrounded by the bombs, the bombs will become much more valueable
            if(FlagSurroundedByBomb(state)) {
                score = 1080 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defenderBomb * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                        + defenderMarshal * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defenderCaptain * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defenderLieutenant * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defenderColonel * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defenderMajor * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR)
                        + attackerMarshal * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attackerGeneral * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attackerColonel * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attackerMajor * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attackerCaptain * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attackerMiner * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER) 
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB))
                        + attackerSpy * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attackerLieutenant * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
            }
            else {
               score =  2520 * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.FLAG) 
                        + defenderBomb * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB) 
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER))
                        + defenderMarshal * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MARSHALL)
                        + defenderCaptain * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.CAPTAIN)
                        + defenderLieutenant * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.LIEUTENANT)
                        + defenderColonel * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.COLONEL) 
                        + defenderMajor * getPiecesAmount(state, ModeratePlayer.super.team, Pieces.MAJOR)
                        + attackerMarshal * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MARSHALL) 
                        + attackerGeneral * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.GENERAL)
                        + attackerColonel * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.COLONEL)
                        + attackerMajor * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MAJOR)
                        + attackerCaptain * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.CAPTAIN)
                        + attackerMiner * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.MINER)
                          * (1 / getPiecesAmount(state, ModeratePlayer.super.team, Pieces.BOMB))
                        + attackerSpy * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.SPY)
                        + attackerLieutenant * getPiecesAmount(state, ModeratePlayer.super.team.opposite(), Pieces.LIEUTENANT);
            }
            
            return score;
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
    
}
