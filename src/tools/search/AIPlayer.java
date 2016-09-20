package tools.search;

import Game.BoardPosition;
import Game.GameState;
import Game.Team;
import actions.MoveAction;


/**
 *
 */
public class AIPlayer implements AIBot, Runnable {

    private final Team team;
    private boolean active;
    
    public AIPlayer(Team team) {
        this.team = team;
        this.active = false;
        initialize();
    }
    
    // Initialize threading?
    private void initialize() {
        
    }
    
    @Override
    public MoveAction nextMove(GameState state, long computationTime) {
        this.active = true;
        while(this.active) {
            
        }
        BoardPosition start = new BoardPosition(5, 5);
        BoardPosition end = new BoardPosition(5, 6);
        MoveAction action = new MoveAction(this.team, start, end);
        return action;
    }

    @Override
    public void run() {
        
    }

    @Override
    public void stop() {
        this.active = false;
    }
}
