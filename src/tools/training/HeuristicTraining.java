package tools.training;

import Game.BoardPosition;
import Game.GameBoard;
import Game.InvalidPositionException;
import Game.Pieces;
import static Game.Pieces.*;
import Game.Team;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.deeplearning.BattleEngine;
import tools.deeplearning.BattleTranscript;
import tools.search.ai.players.ModeratePlayer;
import tools.search.new_ai.SparringAttacker;
import tools.search.new_ai.WeightedAIBot;

/**
 *
 */
public class HeuristicTraining implements WeightSetListener {
    
    private WeightedAIBot attacker;
    private WeightedAIBot defender;
    //private GameBoard board;
    private BattleEngine engine;
    //private List<GameBoard> defensiveSetups;
    private List<BoardPosition> offensiveSide;
    
    private Team attackingTeam = Team.RED;
    private Team defendingTeam = Team.BLUE;
    private final int boardWidth = 4;
    private final int boardHeight = 6;
    private final int matchesPerWeightAssigment = 10;
    
    public HeuristicTraining() {
        
    }
    
    private void initialize() {
        // Load defensive setups.
        //this.defensiveSetups = loadDefensiveSetups();
        this.offensiveSide = fillPositions(4, 2);
    }
    
    public static void main(String[] args) {
        new HeuristicTraining().train();
    }
    
    private void train() {
        initialize();
        
        // Setup the bots to be used.
        this.attacker = new SparringAttacker(Team.RED);
        //this.defender = new ModeratePlayer(Team.BLUE);        
        this.defender = null;
        
        int numberOfFeatures = this.defender.featureCount();
        int rounds = 50; // # of weight assignments that will be used.
        SimulatedAnnealing generator = new SimulatedAnnealing(
                numberOfFeatures, rounds);

        // Create battle engine.
        this.engine = new BattleEngine();
        
        // Attach this class as single listener.
        generator.setListener(this);
        
        try {
            // Run the generator, this is a blocking method not a Thread.
            generator.start();
            
            // Get the resulting weights.
            double[] weights = generator.getWeights();
            System.out.println("Final Weights: " + Arrays.toString(weights));
            
            // Save the plot.
            String filename = "";
            generator.savePlot(false, filename);
            System.out.println("TRAINING ENDED");
        } catch (IOException ex) {
            Logger.getLogger(HeuristicTraining.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Callback.
     * Will be notified by the Genetic Annealing Algorithm once a set of weights
     * has been generated. This method blocks the execution of the Annealing
     * Algorithm. And returns the win rate with the given set of weights over
     * the matches against the defensive setups.
     * 
     * @param weights array of weights to be assigned to heuristic function terms.
     * @return win rate.
     */
    @Override
    public double generated(double[] weights) {
        // Receive a set of weights apply them to the heuristic.
        int computationTime = 2000;
        int maxIterations = 50;
        
        // Run a single match with the given weight assignment for each
        // defensive board setup.
        
        // Get a fresh list of defensive setups.
        List<GameBoard> defensiveSetups = loadDefensiveSetups();
        
        double winRate = 0; // # of won finished matches.
        double incomplete = 0; // # of unfinished matches.
        double matches = 0; // # of total matches.
        
        // Assign the new weights to the defensive heuristic.
        this.defender.setWeights(weights);
        
        int rounds = 3;
        for(int i=0; i<rounds; i++) {
            for(GameBoard defensiveSetup : defensiveSetups) {
                // Generate a random attacker setup.
                GameBoard attackerSetup = loadOffensiveSetup(getArmyComposition());
                // Merge the board setups.
                attackerSetup.mergeBoard(defensiveSetup);
                
                // Simulate the battle.
                BattleTranscript result = this.engine.battle(
                        attackerSetup, this.attacker, this.defender, 
                        computationTime, maxIterations);

                // How to affect the win rate if the game does not end?
                Team winner = result.getWinner();
                if(winner != null) { // There is an actual winner.
                    if(winner == this.attackingTeam) {
                        winRate++;
                    }
                } else {
                    // Game did not end.
                    incomplete++;
                }
                
                matches++;
            }
        }
        // Can store the transcript here with the given weight set and
        // the WeightedAIBot algorithms used.
        
        // Return the win rate, is the fraction of the winRate summed with
        // half of the incomplete matches divided by the total # of matches.
        return (winRate + (incomplete / 2.0d)) / matches;
    }
    
    private List<Pieces> getArmyComposition() {
        Pieces[] army = new Pieces[] { 
            MARSHALL, SPY, MINER, GENERAL, LIEUTENANT, COLONEL, MAJOR, CAPTAIN };
        return Arrays.asList(army);
    }
    
    private GameBoard loadOffensiveSetup(List<Pieces> army) {
        List<BoardPosition> positions = this.offensiveSide;
        if(army.size() != positions.size()) {
            throw new IllegalArgumentException(
                    "#pieces != #setup positions -> " + army.size() +
                            " != " + positions.size());
        }
        
        SecureRandom random = new SecureRandom();
        
        GameBoard board = new GameBoard(this.boardWidth, this.boardHeight, this.attackingTeam, this.defendingTeam);
        int posIndex = 0;
        while(!army.isEmpty()) {
            int index = random.nextInt(army.size());
            Pieces type = army.get(index);
            army.remove(index);
            
            try {
                // Place piece on the board.
                board.setupPiece(positions.get(posIndex), type, this.attackingTeam);
            } catch (InvalidPositionException ex) {
                Logger.getLogger(HeuristicTraining.class.getName()).log(Level.SEVERE, null, ex);
            }
            posIndex++;
        }
        return board;
    }
    
    private List<BoardPosition> fillPositions(int w, int rowDepth) {
        List<BoardPosition> positions = new ArrayList<>();
        for(int row=0; row<rowDepth; row++) {
            for(int column=0; column<w; column++) {
                positions.add(new BoardPosition(column, row));
            }
        }
        return positions;
    }
    
    private List<GameBoard> loadDefensiveSetups() {
        List<GameBoard> boards = new ArrayList<>();
        String s1 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:6|b:4|b:7\n" + 
                       "--- --- --- ---\n" +
                       "b:F|b:B|b:9|b:5\n";
        GameBoard one = GameBoard.loadBoard(s1, this.boardWidth, this.boardHeight);
        boards.add(one);

        String s2 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:4|b:B|b:B|b:5\n" + 
                       "--- --- --- ---\n" +
                       "b:9|b:6|b:F|b:7\n";
        GameBoard two = GameBoard.loadBoard(s2, this.boardWidth, this.boardHeight);
        boards.add(two);

        String s3 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:6|b:9|b:7|b:B\n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:F|b:5|b:4\n";
        GameBoard three = GameBoard.loadBoard(s3, this.boardWidth, this.boardHeight);
        boards.add(three);

        String s4 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:9|b:7|b:5\n" + 
                       "--- --- --- ---\n" +
                       "b:4|b:6|b:F|b:B\n";
        GameBoard four = GameBoard.loadBoard(s4, this.boardWidth, this.boardHeight);
        boards.add(four);

        String s5 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:5|b:6|b:B\n" + 
                       "--- --- --- ---\n" +
                       "b:4|b:9|b:7|b:F\n";
        GameBoard five = GameBoard.loadBoard(s5, this.boardWidth, this.boardHeight);
        boards.add(five);

        String s6 = "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" +
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "   |   |   |   \n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:6|b:9|b:F\n" + 
                       "--- --- --- ---\n" +
                       "b:B|b:4|b:7|b:5\n";
        GameBoard six = GameBoard.loadBoard(s6, this.boardWidth, this.boardHeight);
        boards.add(six);

        return boards;
    }
    
    private void test() {
        
    }
}
