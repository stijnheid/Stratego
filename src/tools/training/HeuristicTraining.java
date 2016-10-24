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
import tools.search.new_ai.DefenderOne;
import tools.search.new_ai.DefenderThree;
import tools.search.new_ai.DefenderTwo;
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
    private WeightSetListener listener;
    
    private Team attackingTeam = Team.RED;
    private Team defendingTeam = Team.BLUE;
    private final int boardWidth = 4;
    private final int boardHeight = 6;
    private final int matchesPerWeightAssigment = 10;
    private WeightedAIBot subject; // The subject that is being trained.
    private SimulatedAnnealing algorithm;
    
    public HeuristicTraining() {
        
    }
    
    private void initialize() {
        // Load defensive setups.
        //this.defensiveSetups = loadDefensiveSetups();
        this.offensiveSide = fillPositions(4, 2);
    }
    
    public void setListener(WeightSetListener listener) {
        this.listener = listener;
    }
    
    public static void main(String[] args) {
        new HeuristicTraining().train();
    }
    
    private void train() {
        initialize();
        
        // Setup the bots to be used.
        this.attacker = new SparringAttacker(Team.RED);
        //this.defender = new ModeratePlayer(Team.BLUE);        
        this.defender = new DefenderThree(Team.BLUE);
        
        
        int numberOfFeatures = this.defender.featureCount();
        int rounds = 1; //50; // # of weight assignments that will be used.
        //SimulatedAnnealing generator = new SimulatedAnnealing(
        //        numberOfFeatures, rounds);
        long start = System.currentTimeMillis();
        this.algorithm = new SimulatedAnnealing(numberOfFeatures, rounds);
        long end = System.currentTimeMillis();
        SimulatedAnnealing generator = this.algorithm;

        // Create battle engine.
        this.engine = new BattleEngine();
        
        // Attach this class as single listener.
        generator.setListener(this);
        
        // Set the training subject.
        this.subject = this.defender;
        
        try {
            // Run the generator, this is a blocking method not a Thread.
            System.out.println("Start training...");
            generator.start();
            
            // Get the resulting weights.
            double[] weights = generator.getWeights();
            //System.out.println("Final Weights: " + Arrays.toString(weights));
            
            // Save the plot.
            String filename = "";
            generator.savePlot(true, filename);
            System.out.println("TRAINING ENDED");
            System.out.println("Weight Assignment Iteration Training lasted " + (end - start) + " ms.");
            System.out.println(Arrays.toString(this.algorithm.getWeights()));
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
        int maxIterations = 50; //2; //50;
        
        // Run a single match with the given weight assignment for each
        // defensive board setup.
        
        // Get a fresh list of defensive setups.
        List<GameBoard> defensiveSetups = loadDefensiveSetups();
        
        double winRate = 0; // # of won finished matches.
        double redWin = 0;// # of matches with team red winning
        double incomplete = 0; // # of unfinished matches.
        int matches = 0; // # of total matches.
        
        // Assign the new weights to the defensive heuristic.
        this.subject.setWeights(weights);
        
        int rounds = 3;
        // The attacker plays against each setup #rounds times, but each time
        // with a different setup.
        for(int i=0; i<rounds; i++) {
            for(GameBoard defensiveSetup : defensiveSetups) {
                /*
                if(matches >= loadDefensiveSetups().size()) {
                    System.out.println("EARLY TERMINATION");
                    this.algorithm.stop();
                    break;
                }
                */
                
                // Generate a random attacker setup.
                GameBoard attackerSetup = loadOffensiveSetup(getArmyComposition());
                // Merge the board setups.
                attackerSetup.mergeBoard(defensiveSetup);
                // Check if the setup is valid.
                if(!attackerSetup.isSetupValid()) {
                    throw new RuntimeException("Setup is not valid.");
                    //System.out.println("Setup is not valid.");
                    //this.algorithm.stop();
                    //return 0.0;
                }
                
                // Simulate the battle.
                System.out.println("Start Battle #" + matches);
                BattleTranscript result = this.engine.battle(
                        attackerSetup, this.attacker, this.defender, 
                        computationTime, maxIterations);
                
                // Save results to a file. Put onto a queue and save in batches.

                // How to affect the win rate if the game does not end?
                Team winner = result.getWinner();
                if(winner != null) { // There is an actual winner.
                    if(winner == this.subject.getTeam()) {
                        winRate++;
                    }
                    if (winner == this.subject.getTeam().opposite()) {
                        redWin++;
                    }
                } else {
                    // Game did not end.
                    incomplete++;
                }
                
                System.out.println("match#" + matches + ": winner=" + winner);
                // Print match.
                result.print();
                
                matches++;
            }
            System.out.println("NEXT ROUND");
        }
        System.out.println("Blue won: " + winRate);
        System.out.println("Red won: " + redWin);
        System.out.println("draw: " + incomplete);
        System.out.println("WinRate " + (winRate + (incomplete / 2.0d)) / (double) matches);
        // Can store the transcript here with the given weight set and
        // the WeightedAIBot algorithms used.
        
        // Return the win rate, is the fraction of the winRate summed with
        // half of the incomplete matches divided by the total # of matches.
        return (winRate + (incomplete / 2.0d)) / (double) matches;
    }
    
    private List<Pieces> getArmyComposition() {
        Pieces[] army = new Pieces[] { 
            MARSHALL, SPY, MINER, GENERAL, LIEUTENANT, COLONEL, MAJOR, CAPTAIN };
        List<Pieces> list = new ArrayList<>();
        for(Pieces type : army) {
            list.add(type);
        }
        
        return list;
        //return Arrays.asList(army); // Return a List that does not support remove()
    }
    
    private GameBoard loadOffensiveSetup(List<Pieces> army) {
        if(this.offensiveSide == null) {
            this.offensiveSide = fillPositions(this.boardWidth, this.boardHeight);
        }
        
        List<BoardPosition> positions = this.offensiveSide;
        if(army.size() != positions.size()) {
            throw new IllegalArgumentException(
                    "#pieces != #setup positions -> " + army.size() +
                            " != " + positions.size());
        }
        
        //System.out.println("List Type: " + army.getClass());
        SecureRandom random = new SecureRandom();
        
        GameBoard board = new GameBoard(this.boardWidth, this.boardHeight, 
                this.attackingTeam, this.defendingTeam);
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
        // Initialize this class.
        initialize();
        
        // Print the defensive setups.
        List<GameBoard> boards = loadDefensiveSetups();
        for(GameBoard board : boards) {
            System.out.println(board.transcript());
        }
        
        // Print the offensive setup.
        GameBoard offense = loadOffensiveSetup(getArmyComposition());
        System.out.println("Offense:\n" + offense.transcript());
        
        // Test the merging of boards.
        GameBoard defense = boards.get(0);
        offense.mergeBoard(defense);
        System.out.println("Merged board:\n" + offense.transcript());
    }
}
