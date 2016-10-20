package tools.search.new_ai;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.ai.GameNode;
import tools.search.ai.SearchResult;
import tools.search.ai.WeightedHeuristicTerm;
import tools.search.ai.players.ModeratePlayer;

/**
 *
 */
public class DefenderTwo extends AbstractWeightedPlayer {

    private int range;
    
    public DefenderTwo(Team team) {
        super(team);
        this.range = -1;
        
        if(team != Team.BLUE) {
            throw new IllegalArgumentException("Assumes to be team BLUE due to heuristic function.");
        }
        
        // Set the heuristic.
        // Add the heuristic terms here to the list.
        List<WeightedHeuristicTerm> terms = new ArrayList<>();
        terms.add(new PieceValue());
        terms.add(new BoardValue());
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
        // Best move.
        MoveAction move = result.getBestMove();
        
        this.active = false;
        return move;
    }
    
    @Override
    public void setRange(int range) {
        this.range = range;
    }
    
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

    private class PieceValue extends WeightedHeuristicTerm {

        @Override
        public double computeScore(GameState state) {
            
            double score = 0;
            GameBoard board = state.getGameBoard();
            
            //if the spy of attacker is dead, the marshal of defender will become more vaulable
            List<GamePiece> attackerSpies = board.getPieces(DefenderTwo.super.team.opposite(), Pieces.SPY);
            if(attackerSpies.isEmpty()) {
                // Spy of attacker died.
                defender.put(Pieces.MARSHALL, 625);
            }
            
            //if the marshall of the defender is dead, the marshall and spy of attacker will become less vaulable
            List<GamePiece> defenderMarshalls = board.getPieces(DefenderTwo.super.team, Pieces.MARSHALL);
            if(defenderMarshalls.isEmpty()) {
                // Marshall of defender died.
                attacker.put(Pieces.MARSHALL, -480);
                attacker.put(Pieces.SPY, -260);
            }
            
            //If the flag is surrounded by the bombs, the bombs and the miner of attacker will become much more valueable
            if(FlagSurroundedByBomb(state)) {
                defender.put(Pieces.BOMB, 300 * getPiecesAmount(state, DefenderTwo.super.team.opposite(), Pieces.MINER));
            }
            else {
                defender.put(Pieces.FLAG, 2495);
                defender.put(Pieces.BOMB, 300 * (1 / (getPiecesAmount(state, DefenderTwo.super.team.opposite(), Pieces.MINER) + 1)));
            }
            
            //If all the bombs from defender are gone, the miner of attacker will become less valueable
            List<GamePiece> defenderBomb = board.getPieces(DefenderTwo.super.team, Pieces.BOMB);
            if(defenderBomb.isEmpty()) {
                attacker.put(Pieces.MINER, -100);
            }
            else {
                attacker.put(Pieces.MINER, -300 * (1 / getPiecesAmount(state, DefenderTwo.super.team, Pieces.BOMB)));
            }
            
            List<GamePiece> red = board.getTeam(Team.RED);
            List<GamePiece> blue = board.getTeam(Team.BLUE);

            // Compute the score of team red.
            int redScore = 0;
            for(GamePiece piece : red) {
                redScore+= DefenderTwo.attacker.get(piece.getRank());
            }

            int blueScore = 0;
            for(GamePiece piece : blue) {
                blueScore+= DefenderTwo.defender.get(piece.getRank());
            }
            score = blueScore + redScore;
            
            return score;
            //return -score * (blue.size()/red.size());
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
    
    private class BoardValue extends WeightedHeuristicTerm {
        
        //HashMap<BoardPosition, Integer> cellValue = new HashMap<>();

        @Override
        public double computeScore(GameState state) {
            double score = 0;
            GameBoard board = state.getGameBoard();            
            
            
            String setup =  "0|0|0|0\n" +
                            "50|50|50|50\n" +
                            "100|100|100|100\n" +
                            "150|150|150|150\n" +
                            "200|200|200|200\n" +
                            "250|250|250|250";
            
            /**
            String setup = "1000|1000|1000\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n" +
                            "0|0|0\n";
            */
            /**
            /*
            String setup = "25|25|25|25|25|25\n" +
                            "50|50|50|50|50|50\n" +
                            "100|100|100|100|100|100\n" +
                            "150|150|150|150|150|150\n" +
                            "200|200|200|200|200|200\n" +
                            "200|200|200|200|200|200\n";
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
            
            return score;
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
