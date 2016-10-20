package tools.search.ai.players;

import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.util.List;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.TreeSearch;
import tools.search.ai.TreeSearch.MySearchResult;

/**
 *
 */
public class AttackerTwo extends AbstractPlayer {
    // Use a new search engine.
    private TreeSearch search;
    
    public AttackerTwo(Team team) {
        super(team);
        
        if(team != Team.RED) {
            throw new IllegalArgumentException("Assumes to be team RED due to heuristic function.");
        }
        
        this.search = new TreeSearch(null);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        // Activate
        this.active = true;
        
        HeuristicEvaluation evaluation = new Attacker.AttackerHeuristic();
        this.search.setHeuristic(evaluation);
        
        GameNode node = new GameNode(state);
        MoveAction move;
        int range = -1;
        // Conduct search.
        MySearchResult result = this.search.IDAlphaBeta(node, 1, range, true, true);
        move = result.getBestMove();
        
        // Print Principal Variation Path.
        List<MoveAction> path = result.getPrincipalVariationPaths().pop();
        int i = 0;
        for(MoveAction m : path) {
            System.out.println(i + ": " + m);
            i++;
        }
        
        // Deactive
        this.active = false;
        
        return move;
    }

    @Override
    public void stop() {
        this.active = false;
        this.search.timeout();
    }
}
