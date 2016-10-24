package Game;

import Renderer.Animation;
import Renderer.CameraState;

/**
 * Keeps track of the Gameboard and other state information like timers and
 * running animation information objects.
 */
public class GameState {
    private GameBoard board;
    private boolean isRunning;
    private long startTime;
    //private Team currentTurn;
    // At most one animation can run at a time.
    // Simulation must not accept any action commands while an animation plays.
    private Animation animation;
    private CameraState rendererState;

    public GameState() {
        this.board = null;
        this.isRunning = false;
        this.animation = null;
    }
    
    public boolean isAnimationPlaying() {
        return (this.animation != null);
    }
    
    public Animation getAnimation() {
        return this.animation;
    }
    
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }
    
    public GameBoard getGameBoard() {
        return this.board;
    }
    
    public void setGameBoard(GameBoard board) {
        this.board = board;
    }
    
    public boolean isRunning() {
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
/**
    public Team getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Team currentTurn) {
        this.currentTurn = currentTurn;
    }
*/
    public CameraState getCameraState() {
        return rendererState;
    }

    public void setCameraState(CameraState cState) {
        this.rendererState = cState;
    }
    
    
    
    @Override
    public Object clone() {
        GameState clone = new GameState();
        clone.setGameBoard((GameBoard) board.clone());
        clone.setRunning(isRunning);
        // Copy the start time, be aware that this must be done after setRunning
        // since setRunning also modifies the start time.
        clone.setStartTime(getStartTime());
        clone.setCameraState(getCameraState());
        clone.setAnimation(getAnimation());
        return clone;
    }
}
