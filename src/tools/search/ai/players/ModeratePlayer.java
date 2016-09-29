/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.search.ai.players;

import Game.GameState;
import Game.Team;
import actions.MoveAction;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;

/**
 *
 * @author s122041
 */
public class ModeratePlayer extends AbstractPlayer {
    
    private AlphaBetaSearch searchEngine;

    public ModeratePlayer(Team team) {
        super.team = team;
        this.searchEngine = new AlphaBetaSearch(null);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        
        // Set the heuristic.
        this.searchEngine.setHeuristic(new MyHeuristic());
        
        GameNode node = new GameNode(state);
        this.searchEngine.iterativeDeepeningMinimax(node, 1, 2, false);
    }

    @Override
    public void stop() {
        super.active = false;
        // Interrupt algorithm.
        this.searchEngine.timeout();
    }
    
    private class MyHeuristic implements HeuristicEvaluation {

        @Override
        public double score(GameState state) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
}
