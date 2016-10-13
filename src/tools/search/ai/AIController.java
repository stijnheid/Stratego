package tools.search.ai;

import Game.GameState;
import Logic.Simulation;
import actions.MoveAction;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An AI bot controller that queries the bot for a new move in a background
 * thread and then submits the result via an action object to the simulation
 * component.
 */
public class AIController implements Runnable {
    
    private final GameState state;
    private final Simulation simulation;
    private final AIBot bot;
    private long thinkTime;

    public AIController(GameState state, Simulation simulation, AIBot bot) {
        this.state = state;
        this.simulation = simulation;
        this.bot = bot;
    }
    
    /**
     * This method creates a background thread that queries the AI bot for a
     * new move.
     * 
     * @param state the current game state.
     * @param thinkTime the amount of milliseconds the AI bot is allowed
     * to conduct search.
     */
    public void nextMove(GameState state, long thinkTime) {
        this.thinkTime = thinkTime;
        Thread thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        // Setup timeout task.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Timeout the AI bot.
                AIController.this.bot.stop();
            }
        }, this.thinkTime);
        
        // Execute AI algorithm.
        // Should clone the game state before supplying it.
        MoveAction action = this.bot.nextMove(this.state);
        // Cancel the scheduled AI task, necessary if nextMove returns early.
        timer.cancel();
        // Submit the next action to the simulation component.
        this.simulation.processAction(action); // TODO This runs not on the UI thread, any problem?
    }
}
