/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import Game.GameState;
import Game.Piece;

/** Class designated to drawing board and surrounding environment.
 *
 * @author Maurits Ambags (0771400)
 */
public class Terrain {
    
    /**
     * Function to draw the terrain including environment.
     */
    public void drawTerrain(){
        
    }
    
    /**
     * Function to draw the pieces on top of the already existing board.
     * @param s GameState that should be graphically represented on the screen.
     */
    public void drawBoard(GameState s){
        
    }
    
    /**
     * Method to draw a single piece at the specified position.
     * @param p the Piece object that should be drawn.
     * @param x the x coordinate of this piece (on the board).
     * @param y the y coordinate of this piece (on the board).
     */
    public void drawPiece(Piece p, int x, int y){
        
    }
    
    /**
     * Function that interrupts the game temporarily to showcase an animation.
     * Should only be called by the Simulator whenever it detects that
     * an animation should be played.
     * @param a the Animation that should be played.
     */
    public void playAnimation(Animation a){
        
    }
}
