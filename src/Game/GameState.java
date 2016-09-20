/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

/** Class that maintains the data of the current state of the game.
 *  
 * @author Maurits Ambags (0771400)
 */
public class GameState {
    
    public static int EMPTY = 0;
    public static int FLAG = 1;
    public static int BOMB = 2;
    //etc..
    
    //array of integers representing the contents of the board.
    private int[] cells;
    
    //number representing the number of moves left until the game ends.
    //inv: movesLeft >= 0
    private int movesLeft;
    
    //boolean representing whether an animation is currently playing.
    //user and AI input should be blocked whenever this boolean is true.
    private boolean animState;
    
    /**
     * Function allowing other parts of the engine to request the current GameState.
     * @return the current GameState.
     */
    public GameState getGameState(){
        return this;
    }
    
    /**
     * Function allowing other parts of the engine to update the current GameState.
     * Should only be called by the Simulator to communicate moves being done.
     */
    public void setGameState(){
        
    }
}
