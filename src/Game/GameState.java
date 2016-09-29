package Game;

/**
 * Keeps track of the Gameboard and other state information like timers and
 * running animation information objects.
 */
public class GameState {
    private GameBoard board;
    private boolean isRunning;
    private long startTime;

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
        this.isRunning = running;
        if(running) {
            this.startTime = System.currentTimeMillis();
        }
    }
    
    public long getGameDuration() {
        long currentTime = System.currentTimeMillis();
        if(this.startTime == 0) {
            return 0;
        }
        return (currentTime - this.startTime);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }    
    
    @Override
    public Object clone() {
        GameState clone = new GameState();
        clone.setGameBoard((GameBoard) board.clone());
        clone.setRunning(isRunning);
        // Copy the start time, be aware that this must be done after setRunning
        // since setRunning also modifies the start time.
        clone.setStartTime(getStartTime());
        return clone;
    }
}
