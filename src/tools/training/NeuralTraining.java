package tools.training;

import Game.BoardPosition;
import Game.GameBoard;
import Game.InvalidPositionException;
import Game.Pieces;
import static Game.Pieces.*;
import Game.Team;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.deeplearning.BattleEngine;
import tools.deeplearning.BattleTranscript;
import tools.search.ai.AIBot;
import tools.search.new_ai.DefenderOne;
import tools.search.new_ai.DefenderThree;
import tools.search.new_ai.DefenderTwo;
import tools.search.new_ai.SparringAttacker;
import tools.search.new_ai.WeightedAIBot;
import static tools.training.DataPointGenerator.addInt;

/**
 *
 */
public class HeuristicTrainingTwo implements WeightSetListener {
    
    private WeightedAIBot attacker;
    private WeightedAIBot defender;
    //private GameBoard board;
    private BattleEngine engine;
    //private List<GameBoard> defensiveSetups;
    private List<BoardPosition> offensiveSide;
    private WeightSetListener listener;
    
    private Team attackingTeam = Team.RED;
    private Team defendingTeam = Team.BLUE;
    
    private final int boardWidth = 6;
    private final int boardHeight = 6;
    private final int matchesPerWeightAssigment = 10;
    private WeightedAIBot subject; // The subject that is being trained.
    private SimulatedAnnealing algorithm;
    
    private int[] setup;
    
    String csvFilename = "src/csv/setup.csv";
    FileWriter writer;
    StringBuilder builder = new StringBuilder();
    
    public HeuristicTrainingTwo() throws IOException {
        this.writer = new FileWriter(csvFilename, true);
        
    }
    
    private class BotScore {
        private final WeightedAIBot bot;
        private int wins;
        private int lost;
        private int draws;
        
        public BotScore (WeightedAIBot bot) {
            this.bot = bot;
            this.wins = 0;
            this.lost = 0;
            this.draws = 0;
        }
    }
    
    private void initialize() {
        // Load defensive setups.
        //this.defensiveSetups = loadDefensiveSetups();
        this.offensiveSide = fillPositions(6, 2);
    }
    
    public void setListener(WeightSetListener listener) {
        this.listener = listener;
    }
    
    public static void main(String[] args) throws IOException {
        //new HeuristicTrainingTwo().train();
        new HeuristicTrainingTwo().competition();
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
        //        numberOfFeatures, rounds)
        this.algorithm = new SimulatedAnnealing(numberOfFeatures, rounds);
        SimulatedAnnealing generator = this.algorithm;

        // Create battle engine.
        this.engine = new BattleEngine();
        
        // Attach this class as single listener.
        generator.setListener(this);
        
        // Set the training subject.
        this.subject = this.defender;
        
        try {
            // Run the generator, this is a blocking method not a Thread.
            long start = System.currentTimeMillis();
            System.out.println("Start training...");
            generator.start();
            
            // Get the resulting weights.
            double[] weights = generator.getWeights();
            //System.out.println("Final Weights: " + Arrays.toString(weights));
            
            // Save the plot.
            String filename = "";
            generator.savePlot(true, filename);
            System.out.println("TRAINING ENDED");
            long end = System.currentTimeMillis();
            System.out.println("Weight Assignment Iteration Training lasted " + (end - start) + " ms.");
            System.out.println(Arrays.toString(this.algorithm.getWeights()));
        } catch (IOException ex) {
            Logger.getLogger(HeuristicTrainingTwo.class.getName()).log(Level.SEVERE, null, ex);
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
        
        int rounds = 1;
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
                GameBoard attackerSetup;
                try {
                    attackerSetup = loadOffensiveSetup(getArmyComposition());
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
                } catch (IOException ex) {
                    Logger.getLogger(HeuristicTrainingTwo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
    
    private void competition() throws IOException {
        initialize();
        System.out.println("Start Competition.");
        this.attacker = new SparringAttacker(Team.RED);
        this.engine = new BattleEngine();
        
        List<BotScore> botScores = new ArrayList<>();
        BotScore first = new BotScore(new DefenderTwo(Team.BLUE));
        
        // Add bots to list.
        botScores.add(first);
        
        double bestWinRate = 0;
        
        first.bot.setWeights(new double[] { 0.9, 0.3 });
        
        List<GameBoard> boards = loadDefensiveSetups();
        long computationTime = 2000;
        int maxIterations = 50;
        
        int rounds = 7;
        for(int i=0; i<rounds; i++) {
            System.out.println("Round " + i);
            int setupID = 0;
            GameBoard offensiveSetup = loadOffensiveSetup(getArmyComposition());
            for(GameBoard defensiveBoard : boards) {
                // All bots should play with the same initial board.
                System.out.println("SetupID: " + setupID);
                
                for(BotScore bs : botScores) {
                    // Clone the board.
                    GameBoard battleBoard = ((GameBoard) offensiveSetup.clone());
                    // Merge boards.
                    battleBoard.mergeBoard(defensiveBoard);
                    
                    System.out.println("Board:\n" + battleBoard.transcript());
                    
                    AIBot bot = bs.bot;
                    // For each setup.
                    BattleTranscript result = this.engine.battle(
                            battleBoard, this.attacker, bot, 
                            computationTime, maxIterations);
                    
                    // Store result.
                    Team winner = result.getWinner();
                    if(winner != null) {
                        if(winner == bot.getTeam()) {
                            bs.wins++;
                        } else {
                            bs.lost++;
                        }
                    } else {
                        bs.draws++;
                    }
                }
                
                System.out.println("DefenderTwo win: " + first.wins);
                System.out.println("DefenderTwo lost: " + first.lost);
                System.out.println("DefenderTwo draw: " + first.draws);
                setupID++;
            }
        }
        
        double defenderOneWinRate;
        defenderOneWinRate = ((first.wins + (first.draws / 2.0d)) / (double) (first.wins + first.lost + first.draws));
        System.out.println("DefenderOne Win rate: " + defenderOneWinRate);
    }
    
    private List<Pieces> getArmyComposition() {
        Pieces[] army = new Pieces[] { 
            MARSHALL, SPY, MINER, GENERAL, LIEUTENANT, COLONEL, MAJOR, CAPTAIN, CAPTAIN, LIEUTENANT, MINER, MINER };
        List<Pieces> list = new ArrayList<>();
        for(Pieces type : army) {
            list.add(type);
        }
        
        return list;
        //return Arrays.asList(army); // Return a List that does not support remove()
    }
    
    private GameBoard loadOffensiveSetup(List<Pieces> army) throws IOException {     
        setup = new int[0];
        
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
            String symbol = type.getPieceSymbol();
            army.remove(index);
            
            setup = addInt(setup, Integer.parseInt(symbol));
            
            try {
                // Place piece on the board.
                board.setupPiece(positions.get(posIndex), type, this.attackingTeam);       
            } catch (InvalidPositionException ex) {
                Logger.getLogger(HeuristicTrainingTwo.class.getName()).log(Level.SEVERE, null, ex);
            }
            posIndex++;
        }
        
        for (int i = 0; i < setup.length; i++) {
            builder.append(setup[i]);
            builder.append(",");
        }
        writer.append(builder.toString());
        writer.flush();
        writer.close();
        setup = null;
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
        String s1 =    "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:6|b:4|b:7|b:4|b:B\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:F|b:B|b:9|b:5|b:5|b:4\n";
        GameBoard one = GameBoard.loadBoard(s1, this.boardWidth, this.boardHeight);
        boards.add(one);

        String s2 =    "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:4|b:B|b:B|b:5|b:5|b:4\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:F|b:4|b:9|b:7|b:6\n";
        GameBoard two = GameBoard.loadBoard(s2, this.boardWidth, this.boardHeight);
        boards.add(two);

        String s3 =    "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:6|b:9|b:7|b:B|b:4|b:4\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:F|b:5|b:4|b:5|b:B\n";
        GameBoard three = GameBoard.loadBoard(s3, this.boardWidth, this.boardHeight);
        boards.add(three);

        String s4 = "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:4|b:7|b:6|b:9|b:B|b:B\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:F|b:5|b:5|b:4|b:4\n";
        GameBoard four = GameBoard.loadBoard(s4, this.boardWidth, this.boardHeight);
        boards.add(four);

        String s5 = "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:4|b:B|b:5|b:6|b:4|b:B\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:5|b:4|b:9|b:7|b:B|b:F\n";
        GameBoard five = GameBoard.loadBoard(s5, this.boardWidth, this.boardHeight);
        boards.add(five);

        String s6 = "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" +
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "   |   |   |   |   |   \n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:4|b:6|b:9|b:4|b:F\n" + 
                       "--- --- --- --- --- ---\n" +
                       "b:B|b:B|b:4|b:7|b:5|b:5\n";
        GameBoard six = GameBoard.loadBoard(s6, this.boardWidth, this.boardHeight);
        boards.add(six);

        return boards;
    }
    
    private void test() throws IOException {
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
