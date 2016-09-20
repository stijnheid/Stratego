package actions;

import Game.Team;

/**
 *
 */
public abstract class Action {
    private Team from;
    
    public Action(Team team) {
        this.from = team;
    }
}
