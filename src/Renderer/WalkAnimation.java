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
    
    /*Start location of walk.*/
    private Vector startloc;
    
    public WalkAnimation(Terrain terrain, GamePiece subject, BoardPosition target){
        super(terrain, subject, target);
    }
    
    @Override
    public void execute(){
        startloc = skel.offset;
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread walk = new Thread(() -> {
            //Current camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
            //move Camera to location.
            moveCamera(new Vector(skel.offset.x+5, skel.offset.y, skel.offset.z+5)
                    ,new Vector(skel.offset.x, skel.offset.y, skel.offset.z+1));
            //execute animation (through refresh call).
            for(int i=1; i <= duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        refresh(i);
                    }
                }   catch (Exception e){}
            }
            //move Camera back to original location.
            moveCamera(eye, center);
        });
        walk.start();
    }
    
    /**
     * Method to update the rotation and translation variables for the next frame.
     * Called right after a CameraState.refresh signal.
     * @param frame which frame of the animation is to be displayed next.
     */
    private void refresh(int frame){
        skel.offset = startloc.sum(direction.scale(Math.sin((frame*Math.PI)/(2*duration))));
        double rot = 0.5*Math.toDegrees(Math.sin((frame*Math.PI)/duration));
        skel.hipLRotX = rot;
        skel.hipRRotX = -rot;
        skel.shoulderLRotX = rot;
        skel.shoulderRRotX = -rot;
    }
}
