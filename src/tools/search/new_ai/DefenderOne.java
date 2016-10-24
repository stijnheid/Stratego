package tools.search.new_ai;

import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tools.search.ai.GameNode;
import tools.search.ai.SearchResult;
import tools.search.ai.TreeSearch;
import tools.search.ai.TreeSearch.MySearchResult;
import tools.search.ai.WeightedHeuristicTerm;

/**
 *
 */
public class DefenderOne extends AbstractWeightedPlayer {

    private int range;
    
    public DefenderOne(Team team) {
        super(team);
        this.range = 8;
        
        if(team != Team.BLUE) {
            throw new IllegalArgumentException("Assumes to be team BLUE due to heuristic function.");
        }
        
        // Set the heuristic.
        // Add the heuristic terms here to the list.
        List<WeightedHeuristicTerm> terms = new ArrayList<>();
        terms.add(new MaterialCount());
        this.evaluation = new TermedHeuristic(terms);
        this.search.setHeuristic(this.evaluation);
    }
    
    @Override
    public MoveAction nextMove(GameState state) {
        this.active = true;     
        
        // Create the game node.
        GameNode node = new GameNode(state);
        int initialDepth = 1;
        boolean moveOrdering = false;
        boolean isMaxPlayer = false; // BLUE player.
        SearchResult result = this.search.search(node, 
                initialDepth, 
                this.range, isMaxPlayer, moveOrdering);
        
        if(this.search instanceof TreeSearch) {
            MySearchResult report = (MySearchResult) result;
            System.out.println("Principal Variation Path:");
            List<MoveAction> pvPath = report.getPrincipalVariationPaths().pop();
            for(MoveAction move : pvPath) {
                System.out.println(move.toString());
            }
        }
        
        // Best move.
        MoveAction move = result.getBestMove();
        
        this.active = false;
        return move;
    }
    
    @Override
    public void setRange(int range) {
        this.range = range;
    }
    
    private class MaterialCount extends WeightedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            // Material count.
            GameBoard board = state.getGameBoard();
            List<GamePiece> red = board.getTeam(Team.RED);
            List<GamePiece> blue = board.getTeam(Team.BLUE);

            // Compute the score of team red.
            int redScore = 0;
            for(GamePiece piece : red) {
                redScore+= DefenderOne.attackerValues.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= DefenderOne.defendersValues.get(piece.getRank());
            }
            // Return score from the perspective of the defensive player
            // the BLUE player.
            //return (blueScore - redScore);
            // Reversing terms does not work!
            //return -1 * (redScore - blueScore);
            //return -1 * (blueScore - redScore);
            // Why does this not work?
            return (redScore - blueScore);
            //return (blueScore - redScore);
            // Does also not work, BLUE player must play as the minimizer.
            //return (blueScore - redScore);
            // returen rescore - blueScore should work for minimizing BLUE?
        }
    }
    
    private final static HashMap<Pieces, Integer> attackerValues;
    private final static HashMap<Pieces, Integer> defendersValues;
    
    static {
        attackerValues = new HashMap<>();
        attackerValues.put(Pieces.FLAG, 0);
        attackerValues.put(Pieces.BOMB, 0);
        attackerValues.put(Pieces.SCOUT, 20);
        attackerValues.put(Pieces.SPY, 350);
        attackerValues.put(Pieces.MINER, 300);
        attackerValues.put(Pieces.SERGEANT, 55);
        attackerValues.put(Pieces.LIEUTENANT, 100);
        attackerValues.put(Pieces.CAPTAIN, 220);
        attackerValues.put(Pieces.MAJOR, 325);
        attackerValues.put(Pieces.COLONEL, 400);
        attackerValues.put(Pieces.GENERAL, 500);
        attackerValues.put(Pieces.MARSHALL, 600);
        
        defendersValues = new HashMap<>();
        defendersValues.put(Pieces.FLAG, 1000);
        defendersValues.put(Pieces.BOMB, 400);
        defendersValues.put(Pieces.SCOUT, 50);
        defendersValues.put(Pieces.SPY, 350);
        defendersValues.put(Pieces.MINER, 300);
        defendersValues.put(Pieces.SERGEANT, 55);
        defendersValues.put(Pieces.LIEUTENANT, 100);
        defendersValues.put(Pieces.CAPTAIN, 220);
        defendersValues.put(Pieces.MAJOR, 325);
        defendersValues.put(Pieces.COLONEL, 400);
        defendersValues.put(Pieces.GENERAL, 500);
        defendersValues.put(Pieces.MARSHALL, 600);
    }    
}
