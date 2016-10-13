package tools.search.ai.players;

import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.List;
import tools.search.Player;
import tools.search.ai.AIBot;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.WeighedHeuristicTerm;


/**
 * AI Player that counts material and returns a move based on that.
 */
public class AIPlayer implements AIBot, Player {

    private final Team team;
    private boolean active;
    private final AlphaBetaSearch searchEngine;
    
    public AIPlayer(Team team) {
        this.team = team;
        this.active = false;
        this.searchEngine = new AlphaBetaSearch(null);
    }
    
    @Override
    public MoveAction nextMove(GameState state) {
        this.active = true;
        
        // Setup.
        HeuristicEvaluation evaluation = new EvaluationFunction();
        this.searchEngine.setHeuristic(evaluation);
        GameNode node = new GameNode(state);
        
        // Run alpha beta search.
        //this.searchEngine.search(node, Integer.MAX_VALUE, Integer.MIN_VALUE);
        boolean isAttacker = false;
        if(this.team == state.getGameBoard().getAttacker()) {
            isAttacker = true;
        }
        MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, isAttacker);
        return node.getBestMove();
    }

    @Override
    public void stop() {
        this.active = false;
        this.searchEngine.timeout();
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }
    
    /**
     * Simple heuristic that counts material.
     */
    private class EvaluationFunction extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            // Count pieces naively.
            GameBoard board = state.getGameBoard();
            List<GamePiece> army = board.getTeam(AIPlayer.this.team);
            return army.size();
        }        
    }
    
    private class CompositeHeuristic extends WeighedHeuristicTerm {

        List<WeighedHeuristicTerm> terms = new ArrayList<>();
        
        @Override
        public double computeScore(GameState state) {
            double result = 0;
            for(WeighedHeuristicTerm term : terms) {
                result+= term.score(state);
            }
            return result;
        }
    }
}
