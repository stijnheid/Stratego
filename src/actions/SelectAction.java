package actions;

import Game.BoardPosition;
import Game.Team;

/**
 * Action that represents the selection of a board cell.
 */
public class SelectAction extends Action {
    
    private final int x;
    private final int y;
    private final BoardPosition target;
    
    public SelectAction(Team team, BoardPosition target) {
        super(team);
        this.x = target.getX();
        this.y = target.getY();
        this.target = target;
    }
    
    public SelectAction(Team team, int x, int y) {
        super(team);
        this.x = x;
        this.y = y;
        this.target = new BoardPosition(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public BoardPosition getTarget() {
        return target;
    }
}
