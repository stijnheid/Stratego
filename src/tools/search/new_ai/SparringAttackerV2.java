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
import tools.search.ai.WeightedEvaluation;
import tools.search.ai.WeightedHeuristicTerm;

/**
 *
 */
public class SparringAttackerV2 extends AbstractWeightedPlayer {

    public SparringAttackerV2(Team team) {
        super(team);
        this.range = 8;
        
        if(team != Team.RED) {
            throw new IllegalArgumentException("Assumes to be team RED due to heuristic function.");
        }
        
        // Set the heuristic.
        this.evaluation = new SparringAttackerHeuristic();
        this.search.setHeuristic(this.evaluation);         
    }
    
    @Override
    public MoveAction nextMove(GameState state) {
        this.active = true;       
        
        // Create the game node.
        GameNode node = new GameNode(state);
        int initialDepth = 1;
        boolean moveOrdering = false;
        SearchResult result = this.search.search(node, initialDepth, 
                this.range, true, moveOrdering);
        
        this.searchResult = result;
        // Best move.
        MoveAction move = result.getBestMove();
        
        this.active = false;
        return move;
    }    

    // Put all Heuristic stuff after here.
    
    public class SparringAttackerHeuristic implements WeightedEvaluation {

        List<WeightedHeuristicTerm> terms;

        public SparringAttackerHeuristic() {
            // Add the heuristic terms here to the list.
            this.terms = new ArrayList<>();
            this.terms.add(new MaterialCount());
            this.terms.add(new CellValues());
        }
        
        @Override
        public int featureCount() {
            return terms.size();
        }

        @Override
        public void setWeights(double[] weights) {
            if(terms.size() != weights.length) {
                throw new IllegalArgumentException(
                        "length of weights array does not match #features: " + 
                                weights.length + " != " + featureCount());
            }
            
            // Assign weights to heuristic terms.
            int index = 0;
            for(WeightedHeuristicTerm term : terms) {
                term.setWeight(weights[index++]);
            }
        }

        @Override
        public double score(GameState state) {
            double score = 0;
            for(WeightedHeuristicTerm term : terms) {
                score+= term.score(state);
            }
            return score;
        }
        
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
                redScore+= SparringAttackerV2.attackerValues.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= SparringAttackerV2.defendersValues.get(piece.getRank());
            }
            return (redScore - blueScore);
        }
    }
    
    private class CellValues extends WeightedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            return 0;
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
