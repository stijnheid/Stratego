/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;
import Logic.Action;
import Game.GameState;

/**
 *
 * @author Maurits Ambags (0771400)
 */
public class SearchController extends Controller {
    
    /**
     * Method that does the game search tree search using an alphabeta algorithm.
     * @return 
     */
    public Action alphabeta(){
        
        return null;
    }
    
    /**
     * Method that returns a numerical value indicating the quality of the given
     * GameState. Used to evaluate leaf nodes in alphabeta search tree.
     * @param s the GameState to be evaluated.
     * @return an integer representing the value of this GameState.
     */
    public int evaluate (GameState s){
        
        return 42;
    }
}
