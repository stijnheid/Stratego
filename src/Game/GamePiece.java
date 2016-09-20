package Game;

import static Game.Pieces.*;

/**
 *
 *
 */
public abstract class GamePiece {
    private final Pieces rank;
    private final Team team;
    private boolean isHighlighted;
    private BoardPosition position;
    //private int x;
    //private int y;
    //private int rank.
    
    public GamePiece(Pieces rank, Team team) {
        this.rank = rank;
        this.isHighlighted = false;
        this.team = team;
    }
    
    public GamePiece(Pieces rank, Team team, BoardPosition position) {
        this.rank = rank;
        this.isHighlighted = false;
        this.team = team;
        this.position = position;
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

    public void setPosition(BoardPosition position) {
        this.position = position;
    }    
    
    /**
     * This function returns:
     *  1 if this piece wins;
     *  0 if the attack results in a draw, both pieces should die;
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
     * @return
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
                } // else continue into the default state.
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
}
