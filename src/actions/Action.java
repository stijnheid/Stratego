package actions;

import Game.Team;

/**
 * Abstract class that represents actions that can be made by players in the
 * game.
 */
public abstract class Action {
    // The team that initiates this action.
    protected Team team;
    
    public Action(Team team) {
        this.team = team;
    }
    
    public Team getTeam() {
        return this.team;
    }
}
