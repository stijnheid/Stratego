package tools.search.new_ai;

import Game.GameState;
import Game.Team;
import actions.MoveAction;

/**
 *
 */
public class DefenderOne extends AbstractWeightedPlayer {

    public DefenderOne(Team team) {
        super(team);
    }
    
    @Override
    public MoveAction nextMove(GameState state) {
        return null;
    }    
}
