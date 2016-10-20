package tools.search.new_ai;

import Game.Team;
import tools.search.Player;

/**
 *
 */
public class HumanPlayer implements Player {

    private final Team team;

    public HumanPlayer(Team team) {
        this.team = team;
    }
    
    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }
    
}
