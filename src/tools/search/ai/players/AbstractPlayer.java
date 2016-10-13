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
import tools.search.ai.AlphaBetaSearch;

/**
 *
 * @author s122041
 */
public abstract class AbstractPlayer implements AIBot{

    protected boolean active;
    protected Team team;
    
    protected AlphaBetaSearch searchEngine;
    
    public AbstractPlayer(Team team) {
        this.team = team;
        this.searchEngine = new AlphaBetaSearch(null);
    }
    
    @Override
    public abstract MoveAction nextMove(GameState state);
    
    @Override
    public void stop() {
        this.active = false;
        searchEngine.timeout();
    };

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }
}
