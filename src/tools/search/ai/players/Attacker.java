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

/**
 *
 */
public class Attacker extends AbstractPlayer {

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
    
    public Attacker(Team team) {
        super(team);
    }
    
    @Override
    public MoveAction nextMove(GameState state) {
        this.active = true;
        
        this.searchEngine.setHeuristic(new AttackerHeuristic());
        
        //GameNode node = new GameNode(state);
        //this.searchEngine.iterativeDeepeningNegamax(node);
        GameNode node = new GameNode(state);
        
        // Assumes to be the max player with team RED assigned to it.
        //MoveAction move = this.searchEngine.iterativeDeepeningMinimax(node, 1, -1, true);
        MoveAction move;
        int range = -1; //5; //-1;
        if(this.team == Team.RED) {
            move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 1, range, true);
        } else {
            move = this.searchEngine.iterativeDeepeningMinimax(node, 1, range, false);
        }
        // Should never happen.
        if(move == null) {
            throw new RuntimeException("Attacker: move == null (" + team + ")");
        }
        
        System.out.println("Attacker.nextMove() -> " + move.toString());
        System.out.println("Explored Nodes: " + this.searchEngine.getExploredCount());
        System.out.println("Deepest Depth: " + this.searchEngine.getDeepestDepth());
        System.out.println("Cutoffs: " + this.searchEngine.getCutoffsCount());
        
        this.active = false;
        
        return move;
    }
    
    /**
     * Assumes to be the maximizing player with team RED assigned to it.
     */ 
    private class AttackerHeuristic implements HeuristicEvaluation {

        @Override
        public double score(GameState state) {
            GameBoard board = state.getGameBoard();
            List<GamePiece> red = board.getTeam(Team.RED);
            List<GamePiece> blue = board.getTeam(Team.BLUE);

            // Compute the score of team red.
            int redScore = 0;
            for(GamePiece piece : red) {
                redScore+= Attacker.attackerValues.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= Attacker.defendersValues.get(piece.getRank());
            }
            return (redScore - blueScore);
        }
    }
    
}
