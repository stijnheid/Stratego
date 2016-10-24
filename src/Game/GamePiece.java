package Game;

import static Game.Pieces.*;
import java.util.Objects;
import Renderer.Skeleton;

/**
 * This object represents a piece on the game board. A piece has a rank and team.
 * This object maintains state data, like the current position of the piece on
 * the board and if its position should be highlighted as well as if the piece
 * is still alive.
 *
 */
public class GamePiece {
    private final Pieces rank;
    private final Team team;
    private boolean isHighlighted;
    private BoardPosition position;
    private boolean isAlive;
    private Skeleton skeleton;
    
    public GamePiece(Pieces rank, Team team) {
        this.rank = rank;
        this.isHighlighted = false;
        this.team = team;
        this.isAlive = true;
    }
    
    public GamePiece(Pieces rank, Team team, BoardPosition position) {
        this.rank = rank;
        this.isHighlighted = false;
        this.team = team;
        this.isAlive = true;
        this.position = position;
        this.skeleton = new Skeleton(position, team, rank);
    }
    
    public boolean isStatic() {
        return (this.rank == BOMB || this.rank == FLAG);
    }
    
    public boolean isEnemy(GamePiece other) {
        return (this.team != other.getTeam());
    }
    
    public boolean isHightlighted() {
        return this.isHighlighted;
    }
    
    public void toggleHighlight() {
        this.isHighlighted = !this.isHighlighted;
    }
    
    public Pieces getRank() {
        return this.rank;
    }
    
    public Team getTeam() {
        return this.team;
    }

    public BoardPosition getPosition() {
        return this.position;
    }
    
    public Skeleton getSkeleton(){
        return this.skeleton;
    }

    public void setPosition(BoardPosition position) {
        this.position = position;
    }
    
    public void die() {
        this.isAlive = false;
    }
    
    public void revive() {
        this.isAlive = true;
    }
    
    public boolean isAlive() {
        return this.isAlive;
    }
    
    /**
     * This function returns:
     *  1 if this piece wins;
     *  0 if the attack results in a draw, both pieces die;
     * -1 if the opponent wins.
     * Stratego contains a few special pieces: the Scout, the Spy, the Bomb and
     * the Miner.
     * The Spy can only beat the Marshall if it initiates the attack, if the 
     * Marshall attacks the Spy the Spy dies.
     * The Bomb beats every piece except the Miner.
     * The Scout can only beat the Spy and is allowed to move multiple tiles
     * in any straight direction.
     * 
     * @param opponent the enemy piece.
     * @return the result of the attack.
     */
    public int attack(GamePiece opponent) {
        if(this.rank == opponent.getRank()) {
            return 0;
        }
        
        switch(opponent.getRank()) {
            case BOMB:
                if(this.rank == MINER) {
                    return 1;
                } else {
                    return -1;
                }
            case MARSHALL:
                if(this.rank == SPY) {
                    return 1;
                } // else continue into the default case.
            default:
                if(this.rank.ordinal() > opponent.getRank().ordinal()) {
                    return 1;
                } else if(this.rank.ordinal() < opponent.getRank().ordinal()) {
                    return -1;
                } else { // Pieces are equal and both die.
                    return 0;
                }
        }
    }

    @Override
    public Object clone() {
        GamePiece clone = new GamePiece(rank, team, (BoardPosition) position.clone());
        // Freshly created GamePiece is always alive, check if it is killed.
        // isAlive in constructor is initialized to true.
        if(!isAlive) {
            clone.die();
        }
        // Freshly created GamePiece is not highlighted. Check if the newly
        // created GamePiece should be highlighted.
        // isHighlighted in constructor is initialized to false.
        if(isHighlighted) {
            clone.toggleHighlight();
        }
        return clone;
    }

    @Override
    public String toString() {
        return "GamePiece{" + "rank=" + rank + ", team=" + team 
                + ", isHighlighted=" + isHighlighted + ", position=" 
                + position.toString() + ", isAlive=" + isAlive + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final GamePiece other = (GamePiece) obj;
        if (this.rank != other.rank) {
            return false;
        }
        if (this.team != other.team) {
            return false;
        }
        if (this.isHighlighted != other.isHighlighted) {
            return false;
        }
        if (!Objects.equals(this.position, other.position)) {
            return false;
        }
        if (this.isAlive != other.isAlive) {
            return false;
        }
        return true;
    }    
}
