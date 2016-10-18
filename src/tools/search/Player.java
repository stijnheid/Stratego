package tools.search;

import Game.Team;

/**
 * Interface to be implemented by players.
 */
public interface Player {
    /**
     * A player can be a human player or an AI player.
     * 
     * @return true if the player is a human player.
     */
    public boolean isHuman();
    
    /**
     * 
     * @return returns the team of the player.
     */ 
    public Team getTeam();
}
