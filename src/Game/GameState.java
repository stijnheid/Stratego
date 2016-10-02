package Game;
import Renderer.Vector;

/**
 * Keeps track of the Gameboard and other state information like timers and
 * running animation information objects.
 */
public class GameState {
    private GameBoard board;
    private boolean isRunning;
    
    public float tAnim;         // Time since start of animation in seconds.
    
    public int w;               // Width of window in pixels.
    public int h;               // Height of window in pixels.
    
    public Vector cnt;          // Center point.
    public float vDist;         // Distance eye point to center point.
    public float vWidth;        // Width of scene to be shown.
    public float theta;         // Azimuth angle in radians.
    public float phi;           // Inclination angle in radians.
    
    public GameState() {
        this.board = null;
        this.isRunning = false;
        vDist = 10f;
        vWidth = 10f;
        cnt = new Vector(0,0,0);
        theta = 0f;
        phi = 0f;
        tAnim = -1;
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
        this.isRunning = true;
    }
}
