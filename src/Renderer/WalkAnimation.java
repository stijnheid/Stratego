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
    
    /*Direction of movement.*/
    private Vector direction;
    
    public WalkAnimation(Terrain terrain, GamePiece subject, int AnimType, BoardPosition target){
        super(terrain, subject, AnimType, target);
    }
    
    @Override
    public void execute(){
        faceTarget();
        direction = new Vector(1,0,0);
        direction.rotate(skel.getRotation());
        direction.scale(1/(float)duration);
        Thread walk = new Thread(() -> {
            for(int i=0; i < duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        refresh(i);
                    }
                }   catch (Exception e){}
            }
        });
        walk.start();
    }
    
    /**
     * Method to update the rotation and translation variables for the next frame.
     * Called right after a CameraState.refresh signal.
     * @param frame which frame of the animation is to be displayed next.
     */
    private void refresh(int frame){
        skel.move(direction);
        skel.hipLRotX = (frame*20) % 360;
        skel.hipRRotX = (frame*-20) % 360;
    }
    
}
