package actions;

import Game.BoardPosition;
import Game.Team;

/**
 *
 */
public class MoveAction extends Action {
    
    private final BoardPosition start;
    private final BoardPosition end;

    public MoveAction(Team team, BoardPosition start, BoardPosition end) {
        super(team);
        this.start = start;
        this.end = end;
    }

    public BoardPosition getStart() {
        return start;
    }

    public BoardPosition getEnd() {
        return end;
    }
}
