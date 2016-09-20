package actions;

import Game.Team;

/**
 *
 */
public class SelectAction extends Action {
    
    private int x;
    private int y;
    
    public SelectAction(Team team, int x, int y) {
        super(team);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }    
}
