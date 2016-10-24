package actions;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.Team;
import java.util.Objects;

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
    private boolean isAttack;
    // Will only be defined if this move results in an attack.
    private GamePiece enemy;

    public MoveAction(Team team, GamePiece piece, 
            BoardPosition origin, 
            BoardPosition destination) {
        super(team);
        
        if(piece == null) {
            throw new NullPointerException("piece == null");
        }
        
        if(origin == null) {
            throw new NullPointerException("origin == null");
        }
        
        if(destination == null) {
            throw new NullPointerException("destination == null");
        }
        
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

    public GamePiece getEnemy() {
        return enemy;
    }

    public void setEnemy(GamePiece enemy) {
        this.enemy = enemy;
    }
    
    public boolean isOkay() {
        if(!this.isApplied && !this.piece.getPosition().equals(this.origin)) {
            return false;
        }
        if(this.isApplied && !this.piece.getPosition().equals(this.destination)) {
            return false;
        }
        // Check if the origin and destination are neighbours.
        //int xDiff = Math.abs(this.origin.getX() - this.destination.getX());
        //int yDiff = Math.abs(this.origin.getY() - this.destination.getY());
        // Does not check if the move was diagonal.
        
        //if(xDiff > 1 || yDiff > 1) {
        //    return false;
        //}
        int distance = GameBoard.distance(this.origin, this.destination);
        if(distance > 1) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "MoveAction{" + "piece=" + piece.toString()
                + ", origin=" + origin.toString() 
                + ", destination=" + destination.toString() + '}';
    }
    
    @Override
    public Object clone() {
        if(this.isApplied) {
            throw new RuntimeException("Not allowed to clone an applied move. Can be unsafe.");
        }
        
        GamePiece clonedPiece = (GamePiece) piece.clone();
        MoveAction clone = new MoveAction(super.team, clonedPiece, 
                (BoardPosition) origin.clone(), 
                (BoardPosition) destination.clone());
        
        clone.isAttack = this.isAttack;
        clone.isApplied = this.isApplied;
        // TODO also copy the deadOpponent and deadAttacker, but we can assume
        // that the clone function is not used on already applied moves.
        return clone;
    }

    public boolean isIsAttack() {
        return isAttack;
    }
    
    public void setAttack() {
        this.isAttack = true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MoveAction other = (MoveAction) obj;

        if (!Objects.equals(this.piece, other.piece)) {
            return false;
        }
        /*
        if (!Objects.equals(this.deadAttacker, other.deadAttacker)) {
            return false;
        }
        if (!Objects.equals(this.deadOpponent, other.deadOpponent)) {
            return false;
        }
        if (this.piece.getRank() != other.piece.getRank() &&
                piece.getTeam() != other.piece.getTeam()) {
            return false;
        }*/
        if (!Objects.equals(this.origin, other.origin)) {
            return false;
        }
        if (!Objects.equals(this.destination, other.destination)) {
            return false;
        }
        if (this.isApplied != other.isApplied) {
            return false;
        }
        if (this.isAttack != other.isAttack) {
            return false;
        }
        return true;
    }
    
    
}
