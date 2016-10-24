package Main;

import Game.GameBoard;
import Game.GameState;
import Game.Team;
import Logic.Simulation;
import NeuralNetwork.NeuralNetwork;
import Renderer.SetupGUI;
import java.io.IOException;
import tools.search.Player;
import tools.search.ai.SetupGenerator;
import tools.search.new_ai.DefenderTwo;
import tools.search.new_ai.HumanPlayer;

/**
 * Main class which handles creating all the objects and running the game
 *
 * @author s146928
 */
public class Main {

    private int[] attackersetup;
    private GameBoard board;

    public Main() throws IOException {
        SetupGUI setupGUI = new SetupGUI(this); // creates a SetupGUI object, creating an attacker setup and storing this in the instance variable

        synchronized (this) {
            try {
                wait();
            } catch (Exception e) {

            }
        }

        //The neural net requires the CORRECT attacker setup, so in between here there needs to be a callback to make sure we don't run this code before the user is
        // done creating a setup 
        NeuralNetwork neuralnetwork = new NeuralNetwork(this, attackersetup); // creates a NeuralNetwork object, creating a defensive setup and a complete GameBoard, and storing this in the instance variable
        GameState gamestate = new GameState();
        gamestate.setGameBoard(board);

        //Simulation needs to be initiated here somewhere
        //Simulation simulation = new Simulation();
        //This also needs to happen sequentially, we first need all the crap above before we create a new Terrain
        //Creating a terrain object 
        //Terrain terrain = new Terrain(gamestate,board,simulation);
        // Here we have the GameBoard object, use this to create the game itself
        // Something needs to call the animations too!
    }

    public static void main(String[] args) {
        try {
            Main main = new Main();
        } catch (Exception e) {

        }

    }
    
    private void initialize() {
        Player human = new HumanPlayer(Team.RED);
        Player bot = new DefenderTwo(Team.BLUE);
        GameState state = new GameState();
        // Set game board.
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateWholeSetup();
        state.setGameBoard(board);
        
        Simulation simulation = new Simulation(state, human, bot);
        // Start the game.
        simulation.startGame();
    }

    public void setAttackerSetup(int[] setup) {
        this.attackersetup = setup;
    }

    public void setGameBoard(GameBoard board) {
        this.board = board;
    }
}
