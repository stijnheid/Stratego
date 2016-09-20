/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import Game.GameState;

/**
 * Class which executes Actions performed by a controller, after which it
 * updates the GameState according to the game logic, and potentially requests
 * Animations to be played.
 * @author Maurits Ambags (0771400)
 */
public class Simulator {
    
    /* The current GameState. May be modified by an Action but should be
       communicated to the main GameState at the end of that Ply.
    */
    private GameState currentState;
    
    /* The currently selected cell (null if none selected).*/
    private int currentlySelected;
    
    /**
     * Method to execute a valid Ply.
     * This method should update the currentState and potentially call an 
     * Animation to be played. Finally, it should call setGameState() to update
     * the new GameState.
     * @param p 
     */
    private void makePly(Ply p){
        
    }
    
    /**
     * Method to process an Action performed by a controller.
     * If this action is of the form Ply, this method should call makeMove to
     * execute this Ply.
     * A Select Action may trigger a Ply to be made if there already is a cell
     * selected and the new Select is a valid move for the piece on the selected
     * cell. If this is not the case, only update the currentlySelected cell.
     * An UnSelect Action should only set the currentlySelected variable to null.
     * @param a the Action that should be processed.
     */
    private void processAction(Action a){
        
    }
}
