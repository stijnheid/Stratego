package tools.search.ai.players;

import Game.GameBoard;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import tools.search.ai.AIBot;

/**
 * AI Player that returns a random move.
 */
public class RandomPlayer implements AIBot {
    
    private final Team team;
    
    public RandomPlayer(Team team) {
        this.team = team;
    }

    @Override
    public MoveAction nextMove(GameState state) {
        System.out.println("RandomPlayer.nextMove()");
        
        GameBoard board = state.getGameBoard();
        List<MoveAction> moves = board.getMoves(this.team);
        System.out.println("RandomPlayer # of Moves: " + moves.size());
        Collections.shuffle(moves, new SecureRandom());
        return moves.get(0);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }
}
