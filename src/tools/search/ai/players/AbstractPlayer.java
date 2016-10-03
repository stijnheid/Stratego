/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.search.ai.players;

import Game.GamePiece;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import tools.search.ai.AIBot;

/**
 *
 * @author s122041
 */
public abstract class AbstractPlayer implements AIBot{

    protected boolean active;
    protected Team team;
    
    @Override
    public abstract MoveAction nextMove(GameState state);
    
    @Override
    public abstract void stop();

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }
}
