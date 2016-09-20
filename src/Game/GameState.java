package Game;

/**
 * Keeps track of the Gameboard and other state information like timers and
 * running animation information objects.
 */
public class GameState {
    private GameBoard board;
    private boolean isRunning;
    
    public GameState() {
        this.board = null;
        this.isRunning = false;
    }
    
    public GameBoard getGameBoard() {
        return this.board;
    }
    
    public void setGameBoard(GameBoard board) {
        this.board = board;
    }
    
    public boolean isRunnning() {
        return this.isRunning;
    }
    
    public void setRunning(boolean running) {
        this.isRunning = true;
    }
}
