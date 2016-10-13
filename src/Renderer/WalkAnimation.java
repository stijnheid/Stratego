/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import Game.BoardPosition;
import Game.GamePiece;

/**
 *  Class to execute a walking animation.
 */
public class WalkAnimation extends Animation{
    
    public WalkAnimation(GamePiece subject, int AnimType, BoardPosition target, Terrain terrain){
        super(subject, AnimType, target, terrain);
    }
    
    @Override
    public void execute(){
        
    }
    
    private void startWalk(){
        
    }
    
    private void endWalk(){
        
    }
    
}
