/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

/**
 *
 * @author s146928
 */
public class GamePiece {
    
    private String name;
    private int rank;
    private int number;
    
    GamePiece(String piece_name, int piece_rank, int number_of_pieces){
        name = piece_name;
        rank = piece_rank;
        number = number_of_pieces;            
    }
    
    //Method for returning the name of the piece
    public String getName(){
        return name;
    }
    
    //Method for returning the rank of the piece
    public int getRank(){
        return rank;
    }
    
    //Method for returning the amount of pieces left
    public int getNumber(){
        return number;
    }
    
}
