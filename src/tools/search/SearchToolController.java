package tools.search;

import Game.GameState;
import Game.Team;
import java.util.Timer;
import java.util.TimerTask;
import actions.MoveAction;

/**
 *
 */
public class SearchToolController {
    
    private final Timer timer = new Timer();
    private AIPlayer bot;
    private GameState state;
    
    public static void main(String[] args) {
        new SearchToolController().initialize();
    }
    
    private void initialize() {
        this.bot = new AIPlayer(Team.BLUE);
        
        startGame();
    }
    
    private void startGame() {
        System.out.println("Invoke Get MOVE");
        MoveAction move = getMoveAIPlayer();
        System.out.println("Got Move");
        
    }
    
    public MoveAction getMoveAIPlayer() {
        System.out.println("Get MOVE");
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                SearchToolController.this.bot.stop();
            }
        };
        
        long delay = 1000;
        long start = System.currentTimeMillis();
        timer.schedule(timeTask, delay);
        long end = System.currentTimeMillis();
        System.out.println("Scheduling: " + (end - start) + " ms.");
        
        // Ask the player to calculate a move.
        
        long startOne = System.currentTimeMillis();
        MoveAction move = this.bot.nextMove(state, delay);
        long endOne = System.currentTimeMillis();
        System.out.println("Move Computation Time: " + (endOne - startOne) + " ms.");
        return move;
    }
}
