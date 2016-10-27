package Main;

import Game.GameBoard;
import Game.GameState;
import Game.Pieces;
import Game.Team;
import Logic.Simulation;
import Renderer.SetupGUI;
import Renderer.Terrain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import tools.search.Player;
import tools.search.ai.SetupGenerator;
import tools.search.new_ai.DefenderTwo;
import tools.search.new_ai.HumanPlayer;
import tools.search.new_ai.WeightedAIBot;

/**
 * Main class which handles creating all the objects and running the game
 *
 * @author s146928
 */
public class Main {

    private int[] attackersetup;
    private GameBoard board;

    public Main() {
        SetupGUI setupGUI = new SetupGUI(this); // creates a SetupGUI object, creating an attacker setup and storing this in the instance variable
        //The neural net requires the CORRECT attacker setup, so in between here there needs to be a callback to make sure we don't run this code before the user is
        // done creating a setup 
        /**
        NeuralNetwork neuralnetwork = new NeuralNetwork(this, attackersetup); // creates a NeuralNetwork object, creating a defensive setup and a complete GameBoard, and storing this in the instance variable
        GameState gamestate = new GameState();
        gamestate.setGameBoard(board);
        */
        
        
        //Simulation needs to be initiated here somewhere
        //Simulation simulation = new Simulation();
        //This also needs to happen sequentially, we first need all the crap above before we create a new Terrain
        //Creating a terrain object 
        //Terrain terrain = new Terrain(gamestate,board,simulation);
        // Here we have the GameBoard object, use this to create the game itself
        // Something needs to call the animations too!
    }

    public static void main(String[] args) {
        /**
        try {
            Main main = new Main();
        } catch (Exception e) {

        }
        */
        //new Main().initialize();
        //new Main().test();
        
        Main main = new Main();
    }
    
    private void test() {
        List<GameBoard> boards = loadDefensiveSetups();
        //boards.get(0);
        GameBoard board = boards.get(0);
        GameBoard initial = new GameBoard(6, 6, Team.RED, Team.BLUE);
        
        //System.out.println(boards.get(0).transcript());
        String[] setup = new String[] { "S", "1", "2", "3", "4", "5", "6", "7", "8", "9", "2", "F" };
        addAttackerSetup(initial, setup);
        
        initial.mergeBoard(board);
        System.out.println(initial.transcript());
        
    }
    
    public void initialize(String[] userSetup) {
        Player human = new HumanPlayer(Team.RED);
        WeightedAIBot bot = new DefenderTwo(Team.BLUE);
        // Assign best playing weights to AI bot.
        bot.setWeights(new double[] { 0.9, 0.3 });
        GameState state = new GameState();
        // Set game board.
        SetupGenerator generator = new SetupGenerator();
        this.board = new GameBoard(6, 6, Team.RED, Team.BLUE);
        
        // Request offensive setup from human player.
        System.out.println("SetupArray: " + Arrays.toString(userSetup));
        addAttackerSetup(board, userSetup);
        
        // Merge offensive setup with defensive setup.
        List<GameBoard> defensiveSetups = loadDefensiveSetups();
        // Pick a random defensive setup.
        Random random = new Random();
        int index = random.nextInt(defensiveSetups.size());
        GameBoard defenderSetup = defensiveSetups.get(index);
        board.mergeBoard(defenderSetup);
        
        System.out.println("Intial setup:\n" + board.transcript());
        
        state.setGameBoard(board);
        
        System.out.println("Build Terrain");
        Terrain terrain = new Terrain(state, state.getGameBoard());
        Simulation simulation = new Simulation(state, human, bot, terrain);
        // Attach the simulation to the terrain.
        terrain.setSimulation(simulation);
        
        // Start the Game UI.
        terrain.run();
        
        // Start the game.
        simulation.startGame();
    }

    public void setAttackerSetup(int[] setup) {
        this.attackersetup = setup;
    }

    public void setGameBoard(GameBoard board) {
        this.board = board;
    }
    
    public List<GameBoard> loadDefensiveSetups() {
        List<GameBoard> boards = new ArrayList<>();
        
        String setup;
        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:6|b:4|b:7|b:4|b:B\n" + 
                "--- --- --- --- --- ---\n" +
                "b:F|b:B|b:9|b:5|b:5|b:4";
        
        boards.add(GameBoard.loadBoard(setup, 6, 6));

        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:4|b:B|b:B|b:5|b:5|b:4\n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:F|b:4|b:9|b:7|b:6";
        
        boards.add(GameBoard.loadBoard(setup, 6, 6));

        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:6|b:9|b:7|b:B|b:4|b:4\n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:F|b:5|b:4|b:5|b:B";
        boards.add(GameBoard.loadBoard(setup, 6, 6));
        
        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:4|b:7|b:6|b:9|b:B|b:B\n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:F|b:5|b:5|b:4|b:4";
        
        boards.add(GameBoard.loadBoard(setup, 6, 6));

        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:4|b:B|b:5|b:6|b:4|b:B\n" + 
                "--- --- --- --- --- ---\n" +
                "b:5|b:4|b:9|b:7|b:B|b:F";
        
        boards.add(GameBoard.loadBoard(setup, 6, 6));

        setup = "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" +
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "   |   |   |   |   |   \n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:4|b:6|b:9|b:4|b:F\n" + 
                "--- --- --- --- --- ---\n" +
                "b:B|b:B|b:4|b:7|b:5|b:5";
        
        boards.add(GameBoard.loadBoard(setup, 6, 6));
        
        // Return list of boards.
        return boards;
    }
    
    private void addAttackerSetup(GameBoard board, String[] userSetup){
        for (int i=0; i < userSetup.length; i++){
            board.setupPiece(5 - (i % 6), 1 - (i / 6), Pieces.bySymbol(userSetup[i]), Team.RED);
        }
    }
}
