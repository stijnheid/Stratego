 package tools.search.ai.players;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.WeighedHeuristicTerm;

/**
 *
 */
public class ModerateAttacker extends AbstractPlayer {

    private final static HashMap<Pieces, Integer> attackerValues;
    private final static HashMap<Pieces, Integer> defendersValues;
    
    static {
        attackerValues = new HashMap<>();
        //attackerValues.put(Pieces.FLAG, 0);
        //attackerValues.put(Pieces.BOMB, 0);
        //attackerValues.put(Pieces.SCOUT, 20);
        attackerValues.put(Pieces.SPY, 325);
        attackerValues.put(Pieces.MINER, 300);
        //attackerValues.put(Pieces.SERGEANT, 55);
        attackerValues.put(Pieces.LIEUTENANT, 100);
        attackerValues.put(Pieces.CAPTAIN, 200);
        attackerValues.put(Pieces.MAJOR, 350);
        attackerValues.put(Pieces.COLONEL, 500);
        attackerValues.put(Pieces.GENERAL, 800);
        attackerValues.put(Pieces.MARSHALL, 1000);
        
        defendersValues = new HashMap<>();
        defendersValues.put(Pieces.FLAG, 1225);
        defendersValues.put(Pieces.BOMB, 300);
        //defendersValues.put(Pieces.SCOUT, 50);
        //defendersValues.put(Pieces.SPY, 325);
        defendersValues.put(Pieces.MINER, 300);
        //defendersValues.put(Pieces.SERGEANT, 55);
        defendersValues.put(Pieces.LIEUTENANT, 100);
        defendersValues.put(Pieces.CAPTAIN, 200);
        defendersValues.put(Pieces.MAJOR, 350);
        defendersValues.put(Pieces.COLONEL, 500);
        //defendersValues.put(Pieces.GENERAL, 500);
        defendersValues.put(Pieces.MARSHALL, 800);
        
        
    }
    
    public ModerateAttacker(Team team) {
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
            move = this.searchEngine.iterativeDeepeningAlphaBeta(node, 1, range, false);
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
            
            ModerateAttacker.PieceValue pieceValue = new ModerateAttacker.PieceValue();
            ModerateAttacker.BoardValue boardValue = new ModerateAttacker.BoardValue();
            double score = 0;
            pieceValue.setWeight(0.7);
            boardValue.setWeight(0.3);
            score = pieceValue.computeScore(state) + boardValue.computeScore(state);
            return score;
        }
    }
    
    private class PieceValue extends WeighedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            //if the spy of attacker is still alive, the marshal of defender will become less vaulable
            List<GamePiece> attackerSpies = board.getPieces(ModerateAttacker.super.team, Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                defendersValues.put(Pieces.MARSHALL, 640);
            }
            
            //if the marshall of the defender is still alive, the marshall and spy of attacker will become less vaulable
            List<GamePiece> defenderMarshalls = board.getPieces(ModerateAttacker.super.team.opposite(), Pieces.MARSHALL);
            if(defenderMarshalls.isEmpty()) {
                // Marshall of defender died.
                attackerValues.put(Pieces.MARSHALL, 800);
                attackerValues.put(Pieces.SPY, 260);
            }
            
            //If the flag is surrounded by the bombs, the bombs and the miner of attacker will become much more valueable
            if(!FlagSurroundedByBomb(state)) {
                attackerValues.put(Pieces.MINER, 100);
                defendersValues.put(Pieces.BOMB, 100);
            }
            
            List<GamePiece> red = board.getTeam(Team.RED);
            List<GamePiece> blue = board.getTeam(Team.BLUE);

            // Compute the score of team red.
            int redScore = 0;
            for(GamePiece piece : red) {
                redScore+= ModerateAttacker.attackerValues.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= ModerateAttacker.defendersValues.get(piece.getRank());
            }
            score =  (redScore - blueScore);
            
            return score;
        }
        
        //check if the flag are surrounded by bombs
        public boolean FlagSurroundedByBomb(GameState state) {
            GameBoard board = state.getGameBoard();
            return board.isFlagUnreachable();
        }
        
    }
    
    private class BoardValue extends WeighedHeuristicTerm {
        
        //HashMap<BoardPosition, Integer> cellValue = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            
            String setup = "0|0|0\n" +
                            "50|50|50\n" +
                            "100|100|100\n" +
                            "200|200|200\n" +
                            "200|200|200\n" +
                            "250|250|250\n";
            
            /**
            String setup = "1000|1000|1000\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n";
            */
            
            /*
            String setup = "0|0|0|0|0|0\n" +
                           "50|50|50|50|50|50\n" +
                           "100|100|100|100|100|100\n" +
                           "200|200|200|200|200|200\n" +
                           "250|250|250|250|250|250\n" +
                           "250|250|250|250|250|250\n";
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
            
            return -score;
        } 
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

