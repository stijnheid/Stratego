package actions;

import Game.BoardPosition;
import Game.GamePiece;
import Game.Team;

/**
 * Action that represents piece movement, if the move is applied and resulted in
 * an attack, the result of this attack will be stored inside this moveaction
 * such that this move can be undone.
 */
public class MoveAction extends Action {
    
    private final GamePiece piece;
    private GamePiece deadAttacker;
    private GamePiece deadOpponent;
    private final BoardPosition origin;
    private final BoardPosition destination;
    private boolean isApplied;

    public MoveAction(Team team, GamePiece piece, 
            BoardPosition origin, 
            BoardPosition destination) {
        super(team);
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
        this.deadAttacker = null;
        this.deadOpponent = null;
        this.isApplied = false;
    }

    public GamePiece getPiece() {
        return piece;
    }
    
    public BoardPosition getOrigin() {
        return origin;
    }

    public BoardPosition getDestination() {
        return destination;
    }

    public GamePiece getDeadAttacker() {
        return deadAttacker;
    }

    public void setDeadAttacker(GamePiece deadAttacker) {
        this.deadAttacker = deadAttacker;
    }

    public GamePiece getDeadOpponent() {
        return deadOpponent;
    }

    public void setDeadOpponent(GamePiece deadOpponent) {
        this.deadOpponent = deadOpponent;
    }
    
    public boolean isApplied() {
        return this.isApplied;
    }
    
    public void setApplied(boolean applied) {
        this.isApplied = applied;
    }

    @Override
    public String toString() {
        return "MoveAction{" + "piece=" + piece.getRank() 
                + ", team=" + piece.getTeam() 
                + ", origin=" + origin.toString() 
                + ", destination=" + destination.toString() + '}';
    }
    
    @Override
    public Object clone() {
        GamePiece clonedPiece = (GamePiece) piece.clone();
        MoveAction clone = new MoveAction(super.team, clonedPiece, 
                (BoardPosition) origin.clone(), 
                (BoardPosition) destination.clone());
        return clone;
    }
}
