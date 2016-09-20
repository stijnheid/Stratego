/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

/**
 *  Class representing a Piece object on the board.
 * @author Maurits Ambags (0771400)
 */
public abstract class Piece {
    
    public static int ATT = 0;
    public static int DEF = 1;
    
    public int team;
    
    /**
     * Constructor for a Piece object.
     * @param rank rank of this piece.
     * inv:: 0 < rank <= number of pieces.
     * @param team the team on which this piece is playing.
     * inv:: team \in [0,1].
     */
    public Piece (int rank, int team){
        
    }
    
    /**
     * Method to determine whether this piece defeats a given opponent.
     * @param p the opponent that is engaged by this piece.
     * @return true iff this piece defeats its opponent in 
     * an engagement where this piece attacks.
     */
    abstract public boolean defeats(Piece p);
}
