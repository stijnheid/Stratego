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
public class NeuralTraining {
    
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
    
    String csvFilename = "src/csv/dataPoint.csv";
    FileWriter writer;
    
    public NeuralTraining() throws IOException {
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
    
    public static void main(String[] args) throws IOException {
        //new HeuristicTrainingTwo().train();
        new NeuralTraining().dataGenerator();
    }
    
    private void dataGenerator() throws IOException {
        initialize();
        System.out.println("Start Training.");
        this.attacker = new SparringAttacker(Team.RED);
        this.engine = new BattleEngine();
        
        List<BotScore> botScores = new ArrayList<>();
        BotScore first = new BotScore(new DefenderTwo(Team.BLUE));
        
        // Add bots to list.
        botScores.add(first);
        
        double bestWinRate = 0;
        int bestSetup = 0;
        
        first.bot.setWeights(new double[] { 0.9, 0.3 });
        
        List<GameBoard> boards = loadDefensiveSetups();
        long computationTime = 2000;
        int maxIterations = 50;
        
        //Number of attacker setups
        int rounds = 5;
        //Number of matches for each defensive setup 
        int turns = 3;
        for(int i=0; i<rounds; i++) {
            System.out.println("Round " + i);
            int setupID = 1;
            GameBoard offensiveSetup = loadOffensiveSetup(getArmyComposition());
            for(GameBoard defensiveBoard : boards) {
                // All bots should play with the same initial board.
                first.wins = 0;
                first.lost = 0;
                first.draws = 0;
                System.out.println("SetupID: " + setupID);
                for (int j=0; j<turns; j++) {
                    // Clone the board.
                    GameBoard battleBoard = ((GameBoard) offensiveSetup.clone());
                    // Merge boards.
                    battleBoard.mergeBoard(defensiveBoard);

                    System.out.println("Board:\n" + battleBoard.transcript());

                    AIBot bot = first.bot;
                    // For each setup.
                    BattleTranscript result = this.engine.battle(
                            battleBoard, this.attacker, bot, 
                            computationTime, maxIterations);

                    // Store result.
                    Team winner = result.getWinner();
                    if(winner != null) {
                        if(winner == bot.getTeam()) {
                            first.wins++;
                        } else {
                            first.lost++;
                        }
                    } else {
                        first.draws++;
                    }
                    
                    double winRate = ((first.wins + (first.draws / 2.0d)) / (double) (first.wins + first.lost + first.draws));
                    
                    if (winRate > bestWinRate) {
                        bestWinRate = winRate;
                        bestSetup = setupID;
                    }
                }
                
                System.out.println("DefenderTwo win: " + first.wins);
                System.out.println("DefenderTwo lost: " + first.lost);
                System.out.println("DefenderTwo draw: " + first.draws);
                setupID++;
            }
            System.out.println("Best Setup: " + bestSetup);
            writer.append(Integer.toString(bestSetup));
            writer.append("\n");
        }
        
        writer.flush();
        writer.close();
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
        
        StringBuilder builder = new StringBuilder();
        
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
                Logger.getLogger(NeuralTraining.class.getName()).log(Level.SEVERE, null, ex);
            }
            posIndex++;
        }
        
        for (int i = 0; i < setup.length; i++) {
            builder.append(setup[i]);
            builder.append(",");
        }
        writer.append(builder.toString());
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
