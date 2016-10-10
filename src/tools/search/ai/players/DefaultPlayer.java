package tools.search.ai.players;

import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.util.List;
import tools.search.ai.AIBot;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;

/**
 * AI Player that counts pieces.
 */
public class DefaultPlayer implements AIBot {
    
    private final Team team;
    private boolean active;
    private final AlphaBetaSearch searchEngine;
    
    public DefaultPlayer(Team team) {
        this.team = team;
        this.active = false;
        this.searchEngine = new AlphaBetaSearch(null);
    }

    @Override
    public MoveAction nextMove(GameState state) {
        // Activate
        this.active = true;
        
        System.out.println("DefaultPlayer.nextMove() (" + this.team + ")");
        HeuristicEvaluation evaluation = new HeuristicEvaluation() {

            @Override
            public double score(GameState state) {
                GameBoard board = state.getGameBoard();
                List<GamePiece> pieces = board.getTeam(team);
                List<GamePiece> opponentPieces = board.getTeam(team.opposite());
                int score = 0;
                // How should we assign the score to the pieces?
                for(GamePiece piece : pieces) {
                    score+= (piece.getRank().ordinal() + 1);
                }
                
                int opponentScore = 0;
                for(GamePiece enemy : opponentPieces) {
                    opponentScore+= (enemy.getRank().ordinal() + 1);
                }
                // The order of this heuristic depends if the initial call
                // is for the maximizing player or the minimizing player.
                // The minimizingScore wants a lowest possible score, that is
                //
                if(getTeam() == Team.RED) { // Maximizing player.
                    return (score - opponentScore);
                } else { // Minimizing player.
                    return -1 * (score - opponentScore);
                }
                
                //return -1 * (score - opponentScore);
            }
        };
        this.searchEngine.setHeuristic(evaluation);
        // Run the search.
        GameNode node = new GameNode(state);
        
        boolean isAttacker = false;
        if(this.team == state.getGameBoard().getAttacker()) {
            isAttacker = true;
        }
        System.out.println("IsAttacker (Max Player): " + isAttacker);
        
        //System.out.println("Max: " + Double.POSITIVE_INFINITY);
        //System.out.println("Min: " + Double.NEGATIVE_INFINITY);
        
        System.out.println("BoardState before Deepening:");
        String shallowCopyBefore = state.getGameBoard().transcript();
        System.out.println(shallowCopyBefore);
        System.out.println();
        
        /**
         * System.out.println("Iterative Deepening AlphaBeta");
        MoveAction move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 
                1, -1, isAttacker);
        */
        System.out.println("Minimax Searching");
        // Why does it lose at 1 if both players have the same AI?
        int range = -1; //10; //4; //3; //4; //1; //1; //3; //10; //1; //10;
        MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, range, isAttacker);
        System.out.println("Reached Depth: " + this.searchEngine.getDeepestDepth());
        System.out.println("ExploredNodesCount: " + this.searchEngine.getExploredCount());
        if(move == null) {
            throw new RuntimeException("Move == null (" + team + ")");
        }
        
        //System.out.println("Move: " + move.getPiece());
        //System.out.println("Move: " + move.getOrigin().toString() + " -> " 
        //        + move.getDestination());
        System.out.println("DefaultPlayer.nextMove() -> " + move.toString());
        
        System.out.println("BoardState after Deepening:");
        String shallowCopyAfter = state.getGameBoard().transcript();
        System.out.println(shallowCopyAfter);
        System.out.println();
        
        // Should compare both boardstates!
        // Should only happen if the algorithm is timed out otherwise NEVER!
        // If this.active is true, then the Player was not timed out. This
        // means that the algorithm had enough time to complete and in that case
        // the state before and after should be identical!
        if(!shallowCopyBefore.equals(shallowCopyAfter) && this.active) {
            System.err.println("BoardState Before unequals After! Active: " + this.active);
            System.err.println("Before:");
            System.err.println(shallowCopyBefore);
            System.err.println("After:");
            System.err.println(shallowCopyAfter);
        }
        
        // Deactivate.
        this.active = false;        
        
        return move;
    }

    @Override
    public void stop() {
        if(this.active) {
            System.err.println("TIMEOUT DEFAULTPLAYER (" + this.team + ")");
        } else {
            System.err.println("Invoked Stop while not active.");
        }
        
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
}
